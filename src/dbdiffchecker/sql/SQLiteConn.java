package dbdiffchecker.sql;

import dbdiffchecker.DatabaseDifferenceCheckerException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Establishes a connection with an SQL database based on the path to the
 * database and database name.
 *
 * @author Peter Kaufman
 * @version 7-9-20
 * @since 5-5-19
 */
public class SQLiteConn extends SQLDbConn {

  /**
   * Sets the instance variables and tests the database connection to make sure
   * that the database can be reached.
   *
   * @param path     The path of the SQLite database.
   * @param database The SQLite database name that the connection is to be
   *                 established with.
   * @param isLive   Whether or not the database conenction is to the live
   *                 database.
   * @throws DatabaseDifferenceCheckerException Error connecting to the database.
   */
  public SQLiteConn(String path, String database, boolean isLive) throws DatabaseDifferenceCheckerException {
    this.isLive = isLive;
    db = database;
    connString = "jdbc:sqlite:" + path + db + ".db";
    testConnection();
  }

  @Override
  public void establishDatabaseConnection() throws DatabaseDifferenceCheckerException {
    try {
      con = DriverManager.getConnection(connString);
    } catch (SQLException e) {
      throw new DatabaseDifferenceCheckerException(
          String.format("There was an error connecting to the %s database.", db), e, 1020);
    }
  }

  @Override
  protected void testConnection() throws DatabaseDifferenceCheckerException {
    try (Connection tempCon = DriverManager.getConnection(connString)) {
      // just tests that the connection can be established with the database
    } catch (SQLException error) {
      throw new DatabaseDifferenceCheckerException(
          String.format("There was an error with the connection to %s. Please try again.", db), error, 1023);
    }
  }

  @Override
  public String getTableCreateStatement(String table) throws DatabaseDifferenceCheckerException {
    String sql = "SELECT `sql` FROM `sqlite_master` WHERE tbl_name='?' AND `sql` NOT NULL;";
    try (PreparedStatement query = con.prepareStatement(sql)) {
      query.setString(1, table);
      StringBuilder create = new StringBuilder();
      ResultSet set = runPreparedStatement(query);
      // get all data needed to create the table
      while (set.next()) {
        create.append(set.getString("sql") + ";\n");
      }
      set.close();
      return create.toString().substring(0, create.length() - 2);
    } catch (SQLException e) {
      throw new DatabaseDifferenceCheckerException(
          String.format("There was an error getting the %s table's create statement.", table), e, 1021);
    }
  }

  @Override
  public Map<String, Table> getTableList() throws DatabaseDifferenceCheckerException {
    HashMap<String, Table> tablesList = new HashMap<>();
    String sql = "SELECT `name`, `sql` FROM `sqlite_master` WHERE `type`= 'table' AND `name` NOT Like 'sqlite%'";
    try (PreparedStatement query = con.prepareStatement(sql)) {
      String table = "";
      String create = "";
      Table add;
      ResultSet tables = runPreparedStatement(query);
      while (tables.next()) {
        table = tables.getString("name");
        create = getTableCreateStatement(table);
        add = new SQLiteTable(table, create);
        tablesList.put(table, add);
      }
      tables.close();
      return tablesList;
    } catch (SQLException e) {
      throw new DatabaseDifferenceCheckerException(
          String.format("There was an error getting the %s database's table, column, and index details.", db), e, 1024);
    }
  }

  @Override
  public List<View> getViews() throws DatabaseDifferenceCheckerException {
    List<View> views = new ArrayList<>();
    String sql = "SELECT `name`, `sql` FROM `sqlite_master` WHERE `type`= 'view';";
    try (PreparedStatement query = con.prepareStatement(sql)) {
      ResultSet set = runPreparedStatement(query);
      while (set.next()) {
        views.add(new View(set.getString("name"), set.getString("sql")));
      }
      return views;
    } catch (SQLException e) {
      throw new DatabaseDifferenceCheckerException(
          String.format("There was an error getting the %s database's view details.", db), e, 1022);
    }
  }
}
