package test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.junit.Assert;
import org.junit.Test;

import dbdiffchecker.DatabaseDifferenceCheckerException;
import dbdiffchecker.sql.Column;
import dbdiffchecker.sql.MySQLConn;
import dbdiffchecker.sql.MySQLTable;
import dbdiffchecker.sql.SQLDatabase;
import dbdiffchecker.sql.Table;
import dbdiffchecker.sql.View;

public class MySQLIntegrationTest {
  private static boolean devIsSetup = false;
  private static boolean liveIsSetup = false;
  private Connection con;
  private List<String> expectedStatements = new ArrayList<>(Arrays.asList("SET FOREIGN_KEY_CHECKS=0;",
  "ALTER TABLE `fkchanges` DROP FOREIGN KEY `fkchanges_ibfk_1`,\n  DROP FOREIGN KEY `fkchanges_ibfk_2`;",
  "ALTER TABLE `3` MODIFY COLUMN `idnew_table2` int(11) NOT NULL,\n  DROP PRIMARY KEY;",
      "ALTER TABLE `charsetcheck` DROP PRIMARY KEY;",
      "ALTER TABLE `compositemodify` MODIFY COLUMN `idnew_table2` int(11) NOT NULL,\n  DROP PRIMARY KEY;",
      "ALTER TABLE `compositeprimarymodification` DROP PRIMARY KEY;", "ALTER TABLE `d` DROP PRIMARY KEY;",
      "ALTER TABLE `fkchanges` DROP PRIMARY KEY;", "ALTER TABLE `l` DROP PRIMARY KEY;",
      "CREATE TABLE `new_table2` (\n  `idnew_table2` int(11) NOT NULL,\n  `new_table2col` varchar(45) DEFAULT NULL,\n"
          + "  `new_table2col1` varchar(45) DEFAULT NULL,\n  `new_table2col2` varchar(45) DEFAULT NULL,\n"
          + "  PRIMARY KEY (`idnew_table2`)\n) ENGINE=MyISAM DEFAULT CHARSET=latin1;",
      "CREATE TABLE `new_table` (\n  `idnew_table` int(11) NOT NULL,\n  `new_tablecol` varchar(45) DEFAULT NULL,\n"
          + "  `new_tablecol1` varchar(45) DEFAULT NULL,\n  PRIMARY KEY (`idnew_table`)\n"
          + ") ENGINE=MyISAM DEFAULT CHARSET=latin1;",
      "DROP TABLE `droppedgroceries`;", "DROP TABLE `planes`;",
      "ALTER TABLE `3` MODIFY COLUMN `idnew_table2` int(11) NOT NULL AUTO_INCREMENT,\n"
          + "  ADD PRIMARY KEY (`idnew_table2`),\n  AUTO_INCREMENT=1000;",
      "ALTER TABLE `fkchanges` DROP INDEX `id3`,\n  ADD INDEX `id2` (`id2`),\n  ADD INDEX `id4` (`id4`),\n  "
          + "ADD PRIMARY KEY (`id`),\n  DROP INDEX `id5`,\n  ADD INDEX `id5` (`id5`,`part2`),\n  "
          + "ADD CONSTRAINT `fkchanges_ibfk_1` FOREIGN KEY (`id2`) REFERENCES `afa` (`idnew_table2`),\n  "
          + "ADD CONSTRAINT `fkchanges_ibfk_3` FOREIGN KEY (`id5`, `part2`) REFERENCES `compositeprimarymodification` (`idnew_table2`, `part2`),\n  "
          + "ADD CONSTRAINT `fkchanges_ibfk_2` FOREIGN KEY (`id4`) REFERENCES `dfas` (`idnew_table2`);",
      "ALTER TABLE `d` DROP INDEX `drop_index`,\n  DROP COLUMN `dropme2`,\n"
          + "  ADD FULLTEXT INDEX `add_index` (`new_table2col2`);",
      "ALTER TABLE `af` ADD COLUMN `addme` int(24) NOT NULL AFTER `new_table2col2`,\n"
          + "  MODIFY COLUMN `new_table2col2` varchar(45) DEFAULT '' AFTER `new_table2col1`,\n  DROP COLUMN `dropme`;",
      "ALTER TABLE `charsetcheck` CHARACTER SET latin1,\n  ADD PRIMARY KEY (`id`);",
      "ALTER TABLE `compositemodify` ADD COLUMN `new_table2col10` varchar(45) DEFAULT NULL AFTER `new_table2col`,\n"
          + "  MODIFY COLUMN `new_table2col2` varchar(45) DEFAULT 'b' AFTER `new_table2col10`,\n  MODIFY COLUMN `idnew_table2`"
          + " int(11) NOT NULL AUTO_INCREMENT,\n  DROP COLUMN `new_table2col1`,\n  DROP INDEX `dfjsalkldskj`,\n"
          + "  ADD INDEX `dfjsalkldskj` (`new_table2col`,`new_table2col10`),\n  DROP INDEX `index3`,\n  "
          + "ADD UNIQUE INDEX `index3` (`new_table2col2`,`new_table2col10`),\n  ADD PRIMARY KEY (`idnew_table2`),\n  "
          + "DROP INDEX `index5`,\n  ADD FULLTEXT INDEX `index5` (`new_table2col`),\n  AUTO_INCREMENT=12;",
      "ALTER TABLE `compositeprimarymodification` ADD PRIMARY KEY (`idnew_table2`,`part2`);",
      "ALTER TABLE `l` ADD PRIMARY KEY (`id1`);",
      "ALTER TABLE `dfasdfsa` ADD PRIMARY KEY (`idnew_table2`);", "DROP VIEW `view1`;", "DROP VIEW `view2`;",
      "DROP VIEW `view3`;",
      "CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `view1`"
          + " AS select `3`.`idnew_table2` AS `idnew_table2`,`3`.`new_table2col` AS `new_table2col`,"
          + "`3`.`new_table2col1` AS `new_table2col1`,`3`.`new_table2col2` AS `new_table2col2`,"
          + "`3`.`3col` AS `3col`,`3`.`3col1` AS `3col1` from `3`;",
      "CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `view3`"
          + " AS select `afa`.`idnew_table2` AS `idnew_table2`,`afa`.`new_table2col` AS `new_table2col`,"
          + "`afa`.`new_table2col1` AS `new_table2col1`,`afa`.`new_table2col2` AS `new_table2col2` from `afa`;",
      "SET FOREIGN_KEY_CHECKS=1;"));
  private Map<String, Table> devTableList = new HashMap<>();
  private Map<String, View> devViewList = new HashMap<>();
  private Map<String, Table> liveTableList = new HashMap<>();
  private Map<String, View> liveViewList = new HashMap<>();

  private List<String> readInFile(String fileName, boolean isLive) {
    List<String> statements = new ArrayList<>();
    try (Scanner fileInput = new Scanner(new File("src" +  File.separator + "resources"
        + File.separator + "sql" + File.separator + fileName))) {
      String statement = "";
      String lineContents;
      String name;
      Table newTable;
      View newView;
      while (fileInput.hasNext()) {
        lineContents = fileInput.nextLine();
        if (!lineContents.startsWith("--") && !lineContents.trim().isEmpty()) {
          if (lineContents.trim().startsWith(")")) {
            statement = statement.substring(0, statement.length() - 2);
          }
          statement += lineContents.trim() + "\n  ";
          if (statement.contains(";")) {
            statement = statement.substring(0, statement.length() - 4);
            statements.add(statement);
            if (statement.contains("CREATE TABLE")) {
              name = statement.substring(28, statement.indexOf("`", 28));
              newTable = new MySQLTable(name, statement.replace(" IF NOT EXISTS", ""));
              if (isLive) {
                if (statement.contains("PRIMARY KEY")) {
                  newTable.getIndices().remove("PRIMARY");
                }
                if (statement.contains("AUTO_INCREMENT")) {
                  removeAutoIncrement(statement, newTable);
                }
                if (statement.contains("FOREIGN KEY")) {
                  dropAllForeignKeys(statement, newTable);
                }
                liveTableList.put(name, newTable);
              } else {
                devTableList.put(name, newTable);
              }
            } else if (statement.contains("DEFINER VIEW")) {
              int startIndex = statement.indexOf("DEFINER VIEW `") + 14;
              name = statement.substring(startIndex, statement.indexOf("`", startIndex));
              newView = new View(name, statement.replace("  ", " ").trim());
              if (isLive) {
                liveViewList.put(name, newView);
                liveTableList.remove(name);
              } else {
                devViewList.put(name, newView);
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
    if (!devIsSetup) {
      connectToDB();
      try {
        con.createStatement().execute("SET FOREIGN_KEY_CHECKS=0;");
        for (String statement : createDevDatabaseStatements) {
          con.createStatement().execute(statement);
        }
        con.createStatement().execute("SET FOREIGN_KEY_CHECKS=1;");
      } catch (Exception err) {
        Assert.fail(err.toString());
      }

      closeDB();
      devIsSetup = true;
    }

    try {
      MySQLConn devConn = new MySQLConn("root", "", "localhost", "3308", "intTestDev", false);

      return new SQLDatabase(devConn, 0);
    } catch (DatabaseDifferenceCheckerException err) {
      Assert.fail(err.toString());
    }
    return new SQLDatabase();
  }

  private SQLDatabase liveDatabaseSetup() {
    List<String> createLiveDatabaseStatements = readInFile("intTestLive.sql", true);
    connectToDB();
    if (!liveIsSetup) {
      connectToDB();
      try {
        con.createStatement().execute("SET FOREIGN_KEY_CHECKS=0;");
        for (String statement : createLiveDatabaseStatements) {
          con.createStatement().execute(statement);
        }
        con.createStatement().execute("SET FOREIGN_KEY_CHECKS=1;");
      } catch (Exception err) {
        Assert.fail(err.toString());
      }

      closeDB();
      liveIsSetup = true;
    }

    try {
      MySQLConn liveConn = new MySQLConn("root", "", "localhost", "3308", "intTestLive", true);

      return new SQLDatabase(liveConn, 0);
    } catch (DatabaseDifferenceCheckerException err) {
      Assert.fail(err.toString());
    }

    return new SQLDatabase();
  }

  private void removeAutoIncrement(String createStatement, Table table) {
    int endColumn = createStatement.indexOf("AUTO_INCREMENT");
    int startColumn = createStatement.indexOf("\n");
    int currentPos = -1;
    while (startColumn != -1) {
      currentPos = createStatement.indexOf("\n", startColumn + 1);
      if (currentPos < endColumn) {
        startColumn = currentPos;
      } else {
        String columnDetails = createStatement.substring(startColumn + 1, endColumn).trim();
        int startColumnName = columnDetails.indexOf("`");
        String columnName = columnDetails.substring(startColumnName + 1,
            columnDetails.indexOf("`", startColumnName + 1));
        table.getColumns().put(columnName, new Column(columnName, columnDetails));
        break;
      }
    }
  }

  private void dropAllForeignKeys(String createStatement, Table table) {
    int start;
    boolean firstTime = true;
    StringBuilder foreignKeyDrop = new StringBuilder("ALTER TABLE `" + table.getName() + "`");
    String indexName;
    String toSearch = createStatement;
    do {
      start = toSearch.indexOf("CONSTRAINT `", 0) + 12;
      indexName = toSearch.substring(start, toSearch.indexOf("`", start));
      if (!firstTime) {
        foreignKeyDrop.append(",\n ");
      }
      foreignKeyDrop.append(" DROP FOREIGN KEY `" + indexName + "`");
      table.getIndices().remove(indexName);
      firstTime = false;
      toSearch = toSearch.substring(toSearch.indexOf("FOREIGN KEY") + 11);
    } while (toSearch.contains("FOREIGN KEY"));
  }

  // @Test
  // public void testDevTableInstantiation() {
  // SQLDatabase devDb = devDatabaseSetup();
  // String tableName;

  // assertEquals("The amount of tables should be the same as those in the
  // database", devTableList.size(),
  // devDb.getTables().size());
  // for (Map.Entry<String, Table> tableEntry : devTableList.entrySet()) {
  // tableName = tableEntry.getKey();
  // assertEquals("All tables in the database should be in the database table
  // list", true,
  // devDb.getTables().containsKey(tableName));
  // assertEquals("All table info should be the same", true,
  // tableEntry.getValue().generateStatements(devDb.getTables().get(tableName)).isEmpty());
  // }
  // }

  // @Test
  // public void testDevViewInstantiation() {
  // SQLDatabase devDb = devDatabaseSetup();
  // String viewName;

  // assertEquals("The amount of views should be the same as those in the
  // database", devViewList.size(),
  // devDb.getViews().size());
  // for (View view : devDb.getViews()) {
  // viewName = view.getName();
  // assertEquals("All views in the database should be in the database table
  // list", true,
  // devViewList.containsKey(viewName));
  // assertEquals("All view info should be the same",
  // devViewList.get(viewName).getCreateStatement(),
  // view.getCreateStatement());
  // }
  // }

  // @Test
  // public void testLiveViewInstantiation() {
  // SQLDatabase liveDb = liveDatabaseSetup();
  // String viewName;

  // assertEquals("The amount of views should be the same as those in the
  // database", liveViewList.size(),
  // liveDb.getViews().size());
  // for (View view : liveDb.getViews()) {
  // viewName = view.getName();
  // assertEquals("All views in the database should be in the database table
  // list", true,
  // liveViewList.containsKey(viewName));
  // assertEquals("All view info should be the same",
  // liveViewList.get(viewName).getCreateStatement(),
  // view.getCreateStatement());
  // }
  // }

  // @Test
  // public void testLiveTableInstantiation() {
  // SQLDatabase liveDb = liveDatabaseSetup();
  // String tableName;

  // assertEquals("The amount of tables should be the same as those in the
  // database", liveTableList.size(),
  // liveDb.getTables().size());
  // for (Map.Entry<String, Table> tableEntry : liveTableList.entrySet()) {
  // tableName = tableEntry.getKey();
  // assertEquals("All tables in the database should be in the database table
  // list", true,
  // liveDb.getTables().containsKey(tableName));
  // assertEquals("All table info should be the same", true,
  // tableEntry.getValue().generateStatements(liveDb.getTables().get(tableName)).isEmpty());
  // }
  // }

  @Test
  public void testCompleteComparisonRegular() {
    List<String> generatedStatments;
    SQLDatabase liveDb = liveDatabaseSetup();
    SQLDatabase devDb = devDatabaseSetup();

    generatedStatments = devDb.compare(liveDb);
    // System.out.println(generatedStatments);
    for (int i = 0; i < generatedStatments.size(); i++) {
      // System.out.printf("Expected: %s%nWas: %s%n", expectedStatements.get(i),
      // generatedStatments.get(i));
      assertEquals(expectedStatements.get(i), generatedStatments.get(i));
    }
    assertEquals("All of the expected statements should have been generated", expectedStatements, generatedStatments);
  }
}
