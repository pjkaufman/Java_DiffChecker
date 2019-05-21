package dbdiffchecker;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * MySQLConn establishes a connection with a MySQL database based on the password,
 * username, port, host, and database provided.
 * @author Peter Kaufman
 * @version 5-21-19
 * @since 9-6-17
 */
public class MySQLConn extends DbConn {

  private String username = ""; 
  private String password = ""; 
  private String host = ""; 
  private String port = ""; 

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
  public MySQLConn(String username, String password, String host, String port, 
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

  @Override
  public void establishDatabaseConnection() throws DatabaseDiffernceCheckerException {
    try {
      this.con = DriverManager.getConnection(this.connString, this.username, this.password);
    } catch (SQLException e) {
      throw new DatabaseDiffernceCheckerException("There was an error connecting to the " 
          + this.db + " database.", e);
    }
  }

  @Override
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

  @Override
  public HashMap<String, Table> getTableList() throws DatabaseDiffernceCheckerException {

    HashMap<String, Table> tablesList = new HashMap<>();
    String sql = "SHOW FULL TABLES IN `" + this.db + "` WHERE TABLE_TYPE LIKE 'BASE TABLE';";
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
        table = tables.getString("Tables_in_" + this.db);
        create = getTableCreateStatement(table);
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
            // find the auto-increment column and remove it
            int endColumn = create.indexOf("AUTO_INCREMENT");
            int startColumn = create.indexOf("\n");
            int temp = -1;
            while(startColumn != -1) {
              temp = create.indexOf("\n", startColumn + 1);
              if (temp < endColumn) {
                startColumn = temp;
              } else {
                this.firstStep += "\n MODIFY COLUMN " + create.substring(startColumn+1, endColumn).trim();
                count++;
                break;
              }
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
            if (count != 0) {
              this.firstStep += ",\n ";
            } else {
              this.firstStep += "\n ";
            }
            this.firstStep += "DROP PRIMARY KEY";
            count++;
          }
        }
        add = new MySQLTable(table, create);
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

  @Override
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

  @Override
  protected void testConnection() throws SQLException {

    this.con = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" 
          + this.db + "?autoReconnect=true&useSSL=false&maxReconnects=5", this.username, 
          this.password);
    this.con.close();
  }
}
