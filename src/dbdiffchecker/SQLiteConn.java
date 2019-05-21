package dbdiffchecker;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * SQLiteConn establishes a connection with an SQL database based on the path 
 * to the database and database name
 * @author Peter Kaufman
 * @version 5-16-19
 * @since 9-6-17
 */
public class SQLiteConn extends DbConn {

  private String path = ""; 

 /**
   * Initializes a DB_conn object by setting the instance variables and
   * testing the database connection to make sure that the database can be reached. 
   * @author Peter Kaufman
   * @param path The path of the SQLite database.
   * @param database The database in SQLite that the connection is to be established with.
   * @param type Is to either dev or live.
   * @throws SQLException The database could not be connected to using the provided information.
   */
  public SQLiteConn(String path, String database, String type) throws SQLException {

    this.type = type;
    this.db = database;
    this.path = path;
    this.connString = "jdbc:sqlite:" + this.path + this.db + ".db";
    this.testConnection();
  }

  @Override
  public void establishDatabaseConnection() throws DatabaseDiffernceCheckerException {
    try {
      this.con = DriverManager.getConnection(this.connString);
    } catch (SQLException e) {
      throw new DatabaseDiffernceCheckerException("There was an error connecting to the " 
          + this.db + " database.", e);
    }
  }

  @Override
  protected void testConnection() throws SQLException {

    this.con = DriverManager.getConnection(this.connString);
    this.con.close();
  }

  @Override
  public String getTableCreateStatement(String table) throws DatabaseDiffernceCheckerException {
    try {
      String create = "";
      Statement query = this.con.createStatement();
      ResultSet set = query.executeQuery("SELECT `sql` FROM `sqlite_master` WHERE tbl_name='" + table + "' AND `sql` NOT NULL; -- create table;");
      // get all data needed to create the table
      while (set.next()) {
        create += set.getString("sql") + ";\n";
      }
      create = create.substring(0, create.length() - 2);

      return create;
    } catch (SQLException e) {
      throw new DatabaseDiffernceCheckerException("There was an error getting the " 
          + table + " table's create statement.", e);
    }
  }

  @Override
  public HashMap<String, Table> getTableList() throws DatabaseDiffernceCheckerException {

    HashMap<String, Table> tablesList = new HashMap<>();
    String sql = "SELECT `name`, `sql` FROM `sqlite_master` WHERE `type`= 'table'";
    try {
      String table = "";
      String create = "";
      Table add = null;
      // set up and run the query to get the table names
      Statement query1 = this.con.createStatement(); 
      ResultSet tables = query1.executeQuery(sql);
      ResultSet columns;
      ResultSet indexes;
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
      throw new DatabaseDiffernceCheckerException("There was an error getting the " 
          + this.db + " database's table, column, and index details.", e);
    }
  }

  @Override
  public ArrayList<View> getViews() throws DatabaseDiffernceCheckerException {
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
      throw new DatabaseDiffernceCheckerException("There was an error getting the " + this.db 
          + " database's view details.", e);
    }
  }
}
