package dbdiffchecker.sql;

import dbdiffchecker.DatabaseDifferenceCheckerException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Establishes a connection with an SQL database based on the path to the
 * database and database name.
 * 
 * @author Peter Kaufman
 * @version 1-6-20
 * @since 5-5-19
 */
public class SQLiteConn extends SQLDbConn {
  // Instance variables
  private String path = "";

  /**
   * Sets the instance variables and tests the database connection to make sure
   * that the database can be reached.
   * 
   * @author Peter Kaufman
   * @param path     The path of the SQLite database.
   * @param database The SQLite database name that the connection is to be
   *                 established with.
   * @param type     Either 'dev' or 'live'.
   * @throws DatabaseDifferenceCheckerException Error connecting to the database.
   */
  public SQLiteConn(String path, String database, String type) throws DatabaseDifferenceCheckerException {
    this.type = type;
    this.db = database;
    this.path = path;
    this.connString = "jdbc:sqlite:" + this.path + this.db + ".db";
    this.testConnection();
  }

  @Override
  public void establishDatabaseConnection() throws DatabaseDifferenceCheckerException {
    try {
      this.con = DriverManager.getConnection(this.connString);
    } catch (SQLException e) {
      throw new DatabaseDifferenceCheckerException("There was an error connecting to the " + this.db + " database.", e,
          1020);
    }
  }

  @Override
  protected void testConnection() throws DatabaseDifferenceCheckerException {
    try {
      this.con = DriverManager.getConnection(this.connString);
      this.con.close();
    } catch (SQLException error) {
      throw new DatabaseDifferenceCheckerException(
          "There was an error with the connection to " + this.db + ". Please try again.", error, 1023);
    }
  }

  @Override
  public String getTableCreateStatement(String table) throws DatabaseDifferenceCheckerException {
    try {
      String create = "";
      Statement query = this.con.createStatement();
      ResultSet set = query.executeQuery(
          "SELECT `sql` FROM `sqlite_master` WHERE tbl_name='" + table + "' AND `sql` NOT NULL; -- create table;");
      // get all data needed to create the table
      while (set.next()) {
        create += set.getString("sql") + ";\n";
      }
      create = create.substring(0, create.length() - 2);
      return create;
    } catch (SQLException e) {
      throw new DatabaseDifferenceCheckerException(
          "There was an error getting the " + table + " table's create statement.", e, 1021);
    }
  }

  @Override
  public HashMap<String, Table> getTableList() throws DatabaseDifferenceCheckerException {
    HashMap<String, Table> tablesList = new HashMap<>();
    String sql = "SELECT `name`, `sql` FROM `sqlite_master` WHERE `type`= 'table' AND `name` NOT Like 'sqlite%'";
    try {
      String table = "";
      String create = "";
      Table add = null;
      // set up and run the query to get the table names
      Statement query1 = this.con.createStatement();
      ResultSet tables = query1.executeQuery(sql);
      // for each table in the database
      while (tables.next()) {
        // get the table name and its createStatement
        table = tables.getString("name");
        create = getTableCreateStatement(table);
        add = new SQLiteTable(table, create);
        tablesList.put(table, add);
      }
      return tablesList;
    } catch (SQLException e) {
      throw new DatabaseDifferenceCheckerException(
          "There was an error getting the " + this.db + " database's table, column, and index details.", e, 1024);
    }
  }

  @Override
  public ArrayList<View> getViews() throws DatabaseDifferenceCheckerException {
    ArrayList<View> views = new ArrayList<>();
    try {
      String sql = "SELECT `name`, `sql` FROM `sqlite_master` WHERE `type`= 'view';";
      Statement query = this.con.createStatement();
      ResultSet set = query.executeQuery(sql);
      while (set.next()) {
        views.add(new View(set.getString("name"), set.getString("sql")));
      }
      return views;
    } catch (SQLException e) {
      throw new DatabaseDifferenceCheckerException(
          "There was an error getting the " + this.db + " database's view details.", e, 1022);
    }
  }
}
