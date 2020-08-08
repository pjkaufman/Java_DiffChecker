package test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.junit.Assert;
import org.junit.Test;

import dbdiffchecker.DatabaseDifferenceCheckerException;
import dbdiffchecker.sql.MySQLConn;
import dbdiffchecker.sql.MySQLTable;
import dbdiffchecker.sql.SQLDatabase;
import dbdiffchecker.sql.Table;
import dbdiffchecker.sql.View;

public class MySQLIntegrationTest {
  private Connection con;
  private Map<String, Table> devTableList = new HashMap<>();
  private Map<String, View> devViewList = new HashMap<>();
  private Map<String, Table> liveTableList = new HashMap<>();
  private Map<String, View> liveViewList = new HashMap<>();

  private List<String> readInFile(String fileName, boolean isLive) {
    List<String> statements = new ArrayList<>();
    try (Scanner fileInput = new Scanner(
        new File("src" + File.separator + "resources" + File.separator + "sql" + File.separator + fileName))) {
      String statement = "";
      String lineContents;
      String name;
      while (fileInput.hasNext()) {
        lineContents = fileInput.nextLine();
        if (!lineContents.startsWith("--") && !lineContents.trim().isEmpty()) {
          statement += lineContents.trim() + "\n  ";
          if (statement.contains(";")) {
            statement = statement.substring(0, statement.length() - 4);
            statements.add(statement);
            if (statement.contains("CREATE TABLE")) {
              name = statement.substring(28, statement.indexOf("`", 28));
              if (isLive) {
                liveTableList.put(name, new MySQLTable(name, statement.replace(" IF NOT EXISTS", "")));
              } else {
                devTableList.put(name, new MySQLTable(name, statement.replace(" IF NOT EXISTS", "")));
              }
            } else if (statement.contains("DEFINER VIEW")) {
              int startIndex = statement.indexOf("DEFINER VIEW `") + 14;
              name = statement.substring(startIndex, statement.indexOf("`", startIndex));
              if (isLive) {
                liveViewList.put(name, new View(name, statement.replace("  ", " ").trim()));
                liveTableList.remove(name);
              } else {
                devViewList.put(name, new View(name, statement.replace("  ", " ").trim()));
                devTableList.remove(name);
              }
            }
            statement = "";
          }
        }
      }
    } catch (Exception err) {
      System.out.println(err);
    }

    return statements;
  }

  private void connectToDB() {
    String connString = "jdbc:mysql://localhost:3308?&autoReconnect=true&useSSL=false&maxReconnects=5";
    try {
      con = DriverManager.getConnection(connString, "root", "");
    } catch (SQLException err) {
      System.out.println(err);
    }
  }

  private void closeDB() {
    try {
      con.close();
    } catch (SQLException err) {
      System.out.println(err);
    }
  }

  private SQLDatabase devDatabaseSetup() {
    List<String> createDevDatabaseStatements = readInFile("intTestDev.sql", false);
    connectToDB();
    for (String statement : createDevDatabaseStatements) {
      try {
        con.createStatement().execute(statement);
      } catch (Exception err) {
        Assert.fail(err.toString());
      }
    }

    closeDB();
    try {
      MySQLConn devConn = new MySQLConn("root", "", "localhost", "3308", "intTestDev", true);
      return new SQLDatabase(devConn, 0);
    } catch (DatabaseDifferenceCheckerException err) {
      Assert.fail(err.toString());
    }
    return null;
  }

  private SQLDatabase liveDatabaseSetup() {
    List<String> createLiveDatabaseStatements = readInFile("intTestLive.sql", true);
    connectToDB();

    for (String statement : createLiveDatabaseStatements) {
      try {
        con.createStatement().execute(statement);
      } catch (Exception err) {
        Assert.fail(err.toString());
      }
    }
    closeDB();
    try {
      MySQLConn liveConn = new MySQLConn("root", "", "localhost", "3308", "intTestLive", false);
      return new SQLDatabase(liveConn, 0);
    } catch (DatabaseDifferenceCheckerException err) {
      Assert.fail(err.toString());
    }

    return null;
  }

  @Test
  public void testDevTableInstantiation() {
    SQLDatabase devDb = devDatabaseSetup();
    String tableName;

    assertEquals("The amount of tables should be the same as those in the database", devTableList.size(),
        devDb.getTables().size());
    for (Map.Entry<String, Table> tableEntry : devTableList.entrySet()) {
      tableName = tableEntry.getKey();
      assertEquals("All tables in the database should be in the database table list", true,
          devDb.getTables().containsKey(tableName));
      assertEquals("All table info should be the same", true,
          tableEntry.getValue().generateStatements(devDb.getTables().get(tableName)).isEmpty());
    }
  }

  @Test
  public void testDevViewInstantiation() {
    SQLDatabase devDb = devDatabaseSetup();
    String viewName;

    assertEquals("The amount of views should be the same as those in the database", devViewList.size(),
        devDb.getViews().size());
    for (View view : devDb.getViews()) {
      viewName = view.getName();
      assertEquals("All views in the database should be in the database table list", true,
          devViewList.containsKey(viewName));
      assertEquals("All view info should be the same", devViewList.get(viewName).getCreateStatement(),
          view.getCreateStatement());
    }
  }

  @Test
  public void testLiveViewInstantiation() {
    SQLDatabase liveDb = liveDatabaseSetup();
    String viewName;

    assertEquals("The amount of views should be the same as those in the database", liveViewList.size(),
        liveDb.getViews().size());
    for (View view : liveDb.getViews()) {
      viewName = view.getName();
      assertEquals("All views in the database should be in the database table list", true,
          liveViewList.containsKey(viewName));
      assertEquals("All view info should be the same", liveViewList.get(viewName).getCreateStatement(),
          view.getCreateStatement());
    }
  }

  @Test
  public void testLiveTableInstantiation() {
    SQLDatabase liveDb = liveDatabaseSetup();
    String tableName;

    assertEquals("The amount of tables should be the same as those in the database", liveTableList.size(),
        liveDb.getTables().size());
    for (Map.Entry<String, Table> tableEntry : liveTableList.entrySet()) {
      tableName = tableEntry.getKey();
      assertEquals("All tables in the database should be in the database table list", true,
          liveDb.getTables().containsKey(tableName));
      assertEquals("All table info should be the same", true,
          tableEntry.getValue().generateStatements(liveDb.getTables().get(tableName)).isEmpty());
    }
  }

  // @Test
  // public void testCompleteComparison() {

  // }
}
