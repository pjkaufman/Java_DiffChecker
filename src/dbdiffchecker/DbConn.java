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
 * Program Name: Database Difference Checker
 * CSCI Course: 325
 * Grade Received: Pass
 * @author Peter Kaufman
 * @version 2-16-19
 * @since 9-6-17
 */
public class DbConn {

  private String username = ""; 
  private String password = ""; 
  private String host = ""; 
  private String db = "";
  private String connString = ""; 
  private String port = ""; 
  private String type = ""; 
  private String firstStep = "";
  private int count = 0;
  private Connection con = null;
  private ArrayList<String> firstSteps = new ArrayList<>();

  /**
   * Initializes a DB_conn object by setting the instance variables and
   * testing the database connection to make sure that the database can be reached. 
   * @author Peter Kaufman
   * @param username The username of the MySQL account.
   * @param password The password of the MySQL account.
   * @param host The host of the MySQL account.
   * @param port The port MySQL is running on.
   * @param database The database in MySQL that the connection is to be established with.
   * @param type Is to either dev or live.
   * @throws SQLException The database could not be connected to using the provided information.
   */
  public DbConn(String username, String password, String host, String port, 
      String database, String type) throws SQLException {

    this.type = type;
    this.username = username;
    this.password = password;
    this.host = host;
    this.db = database;
    this.port = port;
    this.connString = "jdbc:mysql://" + this.host + ":" +  this.port + "/" 
        + this.db + "?autoReconnect=true&useSSL=false&maxReconnects=150";
    this.testConnection();
  }

  /**
   * Returns the name of the database to connect to.
   * @author Peter Kaufman
   * @return The name of the database to connect to.
   */
  public String getDatabaseName() {

    return this.db;
  }

  /**
   * Returns the first steps to be taken in order to run the SQL
   * statements. These SQL statements are used to drop Primary Keys and remove 
   * auto_increments on the database provided. <b>Note: this funntion will return 
   * an empty ArrayList if the function is called on the dev database.</b>
   * @author Peter Kaufman
   * @return The first steps to be taken in order to run the SQL statements
   */
  public ArrayList<String> getFirstSteps() {

    return this.firstSteps;
  }

  /**
   * Makes a connection with the database using the information from this object's constructor.
   * @author Juan Nadal
   * @see <a href="https://www.youtube.com/watch?v=e3gnhsGqNmI">https://www.youtube.com/watch?v=e3gnhsGqNmI</a>
   * @throws DatabaseDiffernceCheckerException Error when connecting to the database.
   */
  public void establishDatabaseConnection() throws DatabaseDiffernceCheckerException {
    try {
      this.con = DriverManager.getConnection(this.connString, this.username, this.password);
    } catch (SQLException e) {
      throw new DatabaseDiffernceCheckerException("There was an error connecting to the " 
          + this.db + " database.", e);
    }
  }

  /**
   * Closes the connection with the database.
   * @author Peter Kaufman
   * @throws DatabaseDiffernceCheckerException Error when closing the connection to the database.
   */
  public void closeDatabaseConnection() throws DatabaseDiffernceCheckerException {
    try {
      this.con.close();
    } catch (SQLException e) {
      throw new DatabaseDiffernceCheckerException("There was an error closing the " 
          + this.db + " database.", e);
    }
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
      Statement query = this.con.createStatement();
      ResultSet set = query.executeQuery("SHOW CREATE TABLE `" + table + "` -- create table;");
      set.next(); // move to the first result

      return set.getString("Create Table");
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
      ResultSet set = query.executeQuery("SHOW CREATE VIEW `" + view + "` -- create view");
      set.next(); // move to the first result

      return set.getString("Create View");
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
    String sql = "SHOW FULL TABLES IN `" + this.db + "` WHERE TABLE_TYPE LIKE 'BASE TABLE';";
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
        table = tables.getString("Tables_in_" + this.db);
        create = getTableCreateStatement(table);
        System.out.println(create + " 178");
        this.firstStep = "ALTER TABLE `" + table + "`";
        this.count = 0;
        // if the database is the live database
        if (this.type.equals("live")) {
          // remove auto_increment value statement
          if (create.contains("AUTO_INCREMENT")) {
            if (create.contains("AUTO_INCREMENT=")) {

              create = create.substring(0, create.indexOf("AUTO_INCREMENT="))
                    + create.substring(create.indexOf("DEFAULT CHARSET"));
            }
            create = create.replace("AUTO_INCREMENT", ""); // remove auto-increment from column
          }
          if (create.contains("PRIMARY KEY")) {
            // determine how many columns are in the PRIMARY KEY and replace 
            // the PRIMARY KEY reference in the create statement
            String temp = create.substring(create.indexOf("PRIMARY KEY"));
            create = create.substring(0, create.indexOf("PRIMARY KEY"))
                  + create.substring(create.indexOf("PRIMARY KEY") +  temp.indexOf(")") + 2);

            // check to see if the PRIMARY KEY was the last table line inside the create statement
            if (!create.contains("KEY")) {
              create = create.substring(0, create.lastIndexOf(",")) + "\n"
                    + create.substring(create.lastIndexOf(",") + 2);
            }
          }
        }
        add = new Table(table, create);
        // query for and get the columns for the table
        columns = query2.executeQuery("SHOW COLUMNS FROM `" + table + "`");
        // for each column fill out the column information
        while (columns.next()) {
          
          fillOutColumns(add, columns);
        }
        // query and get index dat for the table
        indexes = query3.executeQuery("SHOW INDEXES FROM `" + table + "`");
        createIndexes(add, indexes);
        if (this.count != 0) {
          firstSteps.add(firstStep + ";");
        }
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
      String sql = "SHOW FULL TABLES IN `" + this.db + "` WHERE TABLE_TYPE LIKE 'VIEW';";
      Statement query = this.con.createStatement();
      ResultSet set = query.executeQuery(sql);
      while (set.next()) {
        views.add(new View(set.getString("Tables_in_" + this.db),
              getViewCreateStatement(set.getString("Tables_in_" + this.db))));
      }

      return views;
    } catch (SQLException e) {
      throw new DatabaseDiffernceCheckerException("There was an error getting the " + this.db 
          + " database's view details.", e);
    }
  }

  /**
   * Takes an SQL statement and attempts to run it.
   * @author Peter Kaufman
   * @param sqlStatement A SQL statement.
   * @throws DatabaseDiffernceCheckerException An error when running the SQL statement.
   */
  public void runSequelStatement(String sqlStatement) throws DatabaseDiffernceCheckerException {
    try {
      this.establishDatabaseConnection();
      Statement query = this.con.createStatement();
      query.executeUpdate(sqlStatement);
      this.closeDatabaseConnection();
    } catch (SQLException e) {
      throw new DatabaseDiffernceCheckerException("There was an error running " + sqlStatement 
          + " on the " + this.db + " database.", e);
    }
  }

  /**
   * Determines if the connection to the db is correct or not.
   * @author Peter Kaufman
   * @throws SQLException An error occurred while attempting to connect to the database.
   */
  private void testConnection() throws SQLException {

    this.con = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" 
          + this.db + "?autoReconnect=true&useSSL=false&maxReconnects=5", this.username, 
          this.password);
    this.con.close();
  }

  /**
   * Creates a column, gets the column's info, and adds it to the provided Table object.
   * @author Peter Kaufman
   * @param table Where the new column will be added.
   * @param column Contains the data to make a column.
   * @throws SQLException An error occurred while accessing a column property.
   */
  private void fillOutColumns(Table table, ResultSet column) throws SQLException {
    // get data from queried array
    String name = column.getString("Field");
    String type = column.getString("Type");
    String extra = column.getString("Extra"); 
    String def = column.getString("Default");
    String nullable = column.getString("Null"); 
    String info = type;
    // set up desired variables
    // if the type is a string of some sort then make the default a string by adding single quotes
    if (def == null) {
      def = "NULL";
    } else if (type.contains("char")) {
      def = "\'" + def + "\'";
    }
    // is the column nullable? if not, add the default value
    if (nullable.equals("NO")) {
      info += " NOT NULL";
    } else {
      info += " DEFAULT " + def;
    }
    // if the the database is not the live database then add the AUTO_INCREMENT if it exists
    // otherwise drop the AUTO_INCREMENT
    if (extra.equals("auto_increment")) {
      if (this.type.equals("live")) {
        if (this.count == 0) {

          this.firstStep += "\n MODIFY COLUMN `" + name + "`" + info;
          this.count++;
        } else {

          this.firstStep += ",\n MODIFY COLUMN `" + name + "`" + info;
          this.count++;
        }
      } else {
        info += " AUTO_INCREMENT";
      }
    }

    table.addColumn(new Column(name, info));
  }

  /**
   * Takes in a ResultSet and a table object and adds all indexes found in the 
   * ResultSet to the table object.
   * @author Peter Kaufman
   * @param table Where the new indexes will be added.
   * @param index Contains the data to make a indexes for a specific table.
   * @throws SQLException An error occurred while accessing an index property.
   */
  private void createIndexes(Table table, ResultSet index) throws SQLException {
    // set up a hashmap for fast index name checking
    HashMap<String, String> indices = new HashMap<>();
    // initalize variables for index data
    String name = "";
    String create = "";
    String type = "";
    String columns = "";
    int unique = 0;
    // add first index name to the list and collect index data then collect data
    if (index.next()) {
      // get index data
      name = index.getString("Key_name");
      unique = index.getInt("Non_unique");
      type = index.getString("Index_type");
      columns += "`" + index.getString("Column_name") + "`,";
      indices.put(name, name);
      // iterate over the ramaining indexes
      while (index.next()) {
        // if the index name is already in the hashmap, the
        // index data has not been completely collected yet
        if (indices.containsKey(index.getString("Key_name"))) {
          // the only data needed is the column because the other data is the same
          columns += "`" + index.getString("Column_name") + "`,";
        } else {
          // the last index found has all of its data collected,
          // so check to see if the index is a PRIMARY KEY and that the databse type is live
          if (name.equals("PRIMARY") && this.type.equals("live")) {
            if (this.count == 0) {

              this.firstStep += "\n DROP PRIMARY KEY";
              this.count++;
            } else {

              this.firstStep += ",\n DROP PRIMARY KEY";
              this.count++;
            }
          } else {
            // the index is to be added
            create = getCreateIndex(columns.substring(0, columns.length() - 1), 
                  name, type, unique);
            table.addIndex(new Index(name, create, columns.substring(0, columns.length() - 1)));
          }
          // collect the new index's data
          name = index.getString("Key_name");
          unique = index.getInt("Non_unique");
          type = index.getString("Index_type");
          columns = "`" + index.getString("Column_name") + "`,";
          // add index to the list of indexes found
          indices.put(name, name);
        }
      }
      // add the last index found if it is not a PRIMARY KEY on the live database
      if (name.equals("PRIMARY") && this.type.equals("live")) {
        if (this.count == 0) {

          this.firstStep += "\n DROP PRIMARY KEY";
          this.count++;
        } else {

          this.firstStep += ",\n DROP PRIMARY KEY";
          this.count++;
        }
      } else {
        // the index is to be added
        create = getCreateIndex(columns.substring(0, columns.length() - 1), name, type, unique);
        table.addIndex(new Index(name, create, columns.substring(0, columns.length() - 1)));
      }
    }
  }

  /**
   * Takes in three Strings and an integer which are used to determine the type of 
   * index and create the index's create statement.
   * @author Peter Kaufman
   * @param columns The columns that the index is on.
   * @param name The name of the index.
   * @param type The type of indexing used on the index.
   * @param unique Whether or not an index has unique values or not.
   */
  private String getCreateIndex(String columns, String name, String type, int unique) {

    String create = "";
    // initialize the add index statement
    if (name.equals("PRIMARY")) {

      create = " ADD PRIMARY KEY(" + columns + ")";
    } else if (unique == 0) {

      create = " ADD UNIQUE INDEX `" + name + "` (" + columns + ")";
    } else if (type.equals("FULLTEXT")) {

      create = " ADD FULLTEXT INDEX `" + name + "` (" + columns + ")";
    } else if (type == "SPATIAL") {

      create = " ADD SPATIAL INDEX `" + name + "` (" + columns + ")";
    } else {

      create = " ADD INDEX `" + name + "` (" + columns + ")";
    }

    return create;
  }
}
