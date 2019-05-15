package dbdiffchecker;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * DbConn establishes a connection with a MySQL database based on the password,
 * username, port, host, and database provided.
 * @author Peter Kaufman
 * @version 2-16-19
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

  /**
   * Makes a connection with the database using the information from this object's constructor.
   * @author Juan Nadal
   * @see <a href="https://www.youtube.com/watch?v=e3gnhsGqNmI">https://www.youtube.com/watch?v=e3gnhsGqNmI</a>
   * @throws DatabaseDiffernceCheckerException Error when connecting to the database.
   */
  public void establishDatabaseConnection() throws DatabaseDiffernceCheckerException {
    try {
      this.con = DriverManager.getConnection(this.connString);
    } catch (SQLException e) {
      throw new DatabaseDiffernceCheckerException("There was an error connecting to the " 
          + this.db + " database.", e);
    }
  }

  /**
   * Determines if the connection to the db is correct or not.
   * @author Peter Kaufman
   * @throws SQLException An error occurred while attempting to connect to the database.
   */
  protected void testConnection() throws SQLException {

    this.con = DriverManager.getConnection(this.connString);
    this.con.close();
  }

  /**
   * Gets and returns the create statement of the specified table.
   * @author Peter Kaufman
   * @param table The name of the table for which the create statement should be retrieved.
   * @return Table's create statement or an empty string if an error occurred.
   * @throws DatabaseDiffernceCheckerException Error when getting a table's create statement.
   */
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

  /**
   * Gets and returns the create statement of the desired table.
   * @author Peter Kaufman
   * @param view The name of the view for which the create statement should be retrieved.
   * @return The view's create statement.
   * @throws DatabaseDiffernceCheckerException Error when getting a view's create statement.
   */
  public String getViewCreateStatement(String view) throws DatabaseDiffernceCheckerException {
    try {
      Statement query = this.con.createStatement();
      ResultSet set = query.executeQuery("SELECT `sql` FROM `sqlite_master` WHERE `type`= 'view' AND `name`= '" + view + "' -- create view");
      set.next(); // move to the first result

      return set.getString("sql");
    } catch (SQLException e) {
      throw new DatabaseDiffernceCheckerException("There was an error getting the "
          + view + " view's create statement.", e);
    }
  }

  /**
   * Gets the tables, columns, and indices of the database.
   * @author Peter Kaufman
   * @return The names of the tables and table data that exist in the database.
   * @throws DatabaseDiffernceCheckerException Error when getting a table data.
   */
  public HashMap<String, Table> getTableList() throws DatabaseDiffernceCheckerException {

    HashMap<String, Table> tablesList = new HashMap<>();
    String sql = "SELECT `name`, `sql` FROM `sqlite_master` WHERE `type`= 'table'";
    try {
      String table = "";
      String create = "";
      Table add = null;
      // set up and run the query to get the table names
      Statement query1 = this.con.createStatement(); 
      Statement query2 = this.con.createStatement();
      Statement query3 = this.con.createStatement();
      ResultSet tables = query1.executeQuery(sql);
      ResultSet columns;
      ResultSet indexes;
      // for each table in the database
      while (tables.next()) {
        // get the table name and its createStatement
        table = tables.getString("name");
        create = getTableCreateStatement(table);
        add = new SQLiteTable(table, create);
        // query for and get the columns for the table
        columns = query2.executeQuery("PRAGMA TABLE_INFO(`" + table + "`);");
        // for each column fill out the column information
        while (columns.next()) {
          
           fillOutColumns(add, columns);
        }
        // query and get index dat for the table
        indexes = query3.executeQuery("PRAGMA index_list('" + table + "');");
        createIndexes(add, indexes);
        tablesList.put(table, add);
      }

      return tablesList;
    } catch (SQLException e) {
      throw new DatabaseDiffernceCheckerException("There was an error getting the " 
          + this.db + " database's table, column, and index details.", e);
    }
  }

  /**
   * Gets a list of views of that exist in the database.
   * @author Peter Kaufman
   * @return All of the views in the database.
   * @throws DatabaseDiffernceCheckerException Error when getting a view's data.
   */
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

  /**
   * Creates a column, gets the column's info, and adds it to the provided Table object.
   * @author Peter Kaufman
   * @param table Where the new column will be added.
   * @param column Contains the data to make a column.
   * @throws SQLException An error occurred while accessing a column property.
   */
  protected void fillOutColumns(Table table, ResultSet column) throws SQLException {
     // get data from queried array
     String name = column.getString("name");
     String type = column.getString("type");
     boolean extra = column.getBoolean("pk"); 
     boolean notNull = column.getBoolean("notnull");
     String def = column.getString("dflt_value"); 
     String info = type;
     // if the type is a string of some sort then make the default a string by adding single quotes
     if (type.contains("char")) {
       def = "\'" + def + "\'";
     }
     // format the primary key value appropriately if it exists
     if (extra) {
       info += "\t PRIMARY KEY";
       if (notNull) { // setup for notNull definition
          info += "\n\t";
       }
     } 
     if (notNull) { 
        info += "\t NOT NULL";
        if (def != null) { // setup for default definition
          info += "\n\t";
        }
     }
     if (def != null ) {
      info += "\t DEFAULT(" + def + ")";
     }

     table.addColumn(new Column(name, info));
  }

  /**
   * Takes in a ResultSet and a table object and adds all indexes found in the 
   * ResultSet to the table object.
   * @author Peter Kaufman
   * @param table Where the new indexes will be added.
   * @param index Contains the data to make all indexes for a specific table.
   * @throws SQLException An error occurred while accessing an index property.
   */
  protected void createIndexes(Table table, ResultSet index) throws SQLException {
        // initalize variables for index data
        String name = "";
        String create = "";
        String type = "";
        String columns = "";
        boolean unique = false;
        Statement indexDataQuery;
        ResultSet indexData;
        // add each index to the table
        while (index.next()) {
          columns = "";
          // get index name, what created the index, and whether it is unique or not
          name = index.getString("name");
          type = index.getString("origin");
          unique = index.getBoolean("unique");

          // get the index info using the index name
          indexDataQuery = this.con.createStatement(); 
          indexData = indexDataQuery.executeQuery("PRAGMA INDEX_INFO('" + name + "');");
          // iterate over all columns attached to the index
          while (indexData.next()) {
            columns += "`" + indexData.getString("name") + "`,";
          }
          columns = columns.substring(0, columns.length() - 1);
          // get create statemet and add it to the table
          create = getCreateIndex(columns, name, type, table.getName(), unique);
          table.addIndex(new Index(name, create, columns));
        }
  }

  /**
   * Takes in three Strings and an integer which are used to determine the type of 
   * index and create the index's create statement.
   * @author Peter Kaufman
   * @param columns The columns that the index is on.
   * @param name The name of the index.
   * @param type The type of indexing used on the index.
   * @param table The table name to add the index to.
   * @param unique Whether or not the index is unique.
   */
  private String getCreateIndex(String columns, String name, String type, String table, boolean unique) {
    String create = "";
    // initialize the add index statement
    if (type.equals("pk")) {

      create = ""; // cannot be added to a table after its creation in SQLite
    } else if (type.equals("u") || unique) {

      create = "CREATE UNIQUE INDEX `" + name + "` ON `" + table + "` (" + columns + ")";
    } else {

      create = "CREATE INDEX `" + name + "` ON `" + table + "` (" + columns + ")";
    }
   
    return create;
  }
}