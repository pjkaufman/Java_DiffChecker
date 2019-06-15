package dbdiffchecker.sql;

import dbdiffchecker.DatabaseDifferenceCheckerException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Establishes a connection with a MySQL database based on the password,
 * username, port, host, and database provided.
 * @author Peter Kaufman
 * @version 6-15-19
 * @since 5-21-19
 */
public class MySQLConn extends SQLDbConn {
  // Instance variables
  private String username = "";
  private String password = "";
  private String host = "";
  private String port = "";

  /**
   * Sets the instance variables and tests the database connection to make sure
   * that the database can be reached.
   * @author Peter Kaufman
   * @param username The username of the MySQL account.
   * @param password The password of the MySQL account.
   * @param host The host of the MySQL account.
   * @param port The port MySQL is running on. <b>Note: the default is 3306 </b>
   * @param database The database in MySQL that the connection is to be
   *        established with.
   * @param type Either 'dev' or 'live'.
   * @throws DatabaseDifferenceCheckerException Error connecting to the database.
   */
  public MySQLConn(String username, String password, String host, String port, String database, String type)
      throws DatabaseDifferenceCheckerException {
    this.type = type;
    this.username = username;
    this.password = password;
    this.host = host;
    this.db = database;
    this.port = port;
    this.connString = "jdbc:mysql://" + this.host + ":" + this.port + "/" + this.db
        + "?autoReconnect=true&useSSL=false&maxReconnects=150";
    this.testConnection();
  }

  @Override
  public void establishDatabaseConnection() throws DatabaseDifferenceCheckerException {
    try {
      this.con = DriverManager.getConnection(this.connString, this.username, this.password);
    } catch (SQLException e) {
      throw new DatabaseDifferenceCheckerException("There was an error connecting to the " + this.db + " database.", e, 1013);
    }
  }

  @Override
  public String getTableCreateStatement(String table) throws DatabaseDifferenceCheckerException {
    try {
      Statement query = this.con.createStatement();
      ResultSet set = query.executeQuery("SHOW CREATE TABLE `" + table + "` -- create table;");
      set.next(); // move to the first result
      return set.getString("Create Table");
    } catch (SQLException e) {
      throw new DatabaseDifferenceCheckerException(
          "There was an error getting the " + table + " table's create statement.", e, 1014);
    }
  }

  /**
   * Gets and returns the create statement of the desired table.
   * @author Peter Kaufman
   * @param view The name of the view for which the create statement should be
   *        retrieved.
   * @return The view's create statement.
   * @throws DatabaseDifferenceCheckerException Error when getting a view's create
   *         statement.
   */
  public String getViewCreateStatement(String view) throws DatabaseDifferenceCheckerException {
    try {
      Statement query = this.con.createStatement();
      ResultSet set = query.executeQuery("SHOW CREATE VIEW `" + view + "` -- create view");
      set.next(); // move to the first result
      return set.getString("Create View");
    } catch (SQLException e) {
      throw new DatabaseDifferenceCheckerException(
          "There was an error getting the " + view + " view's create statement.", e, 1015);
    }
  }

  @Override
  public HashMap<String, Table> getTableList() throws DatabaseDifferenceCheckerException {
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
        add = new MySQLTable(table, create);
        // if the database is the live database
        if (this.type.equals("live")) {
          // remove auto_increment value statement
          if (create.contains("AUTO_INCREMENT")) {
            // find the auto-increment column and remove it
            int endColumn = create.indexOf("AUTO_INCREMENT");
            int startColumn = create.indexOf("\n");
            int temp = -1;
            while (startColumn != -1) {
              temp = create.indexOf("\n", startColumn + 1);
              if (temp < endColumn) {
                startColumn = temp;
              } else {
                String columnDetails = create.substring(startColumn + 1, endColumn).trim();
                int startColumnName = columnDetails.indexOf("`");
                String columnName = columnDetails.substring(startColumnName + 1,
                    columnDetails.indexOf("`", startColumnName + 1));
                this.firstStep += "\n MODIFY COLUMN " + columnDetails;
                // modify the column definition in order properly generate SQL if there is a
                // difference found
                add.getColumns().put(columnName, new Column(columnName, columnDetails));
                count++;
                break;
              }
            }
          }
          if (create.contains("FOREIGN KEY")) {
            // drop the all Foreign Keys before the Primary Keys are to be dropped
            int start;
            boolean firstTime = true;
            String foreignKeyDrop = "ALTER TABLE `" + table + "`";
            String name;
            String temp = create;
            do {
              start = 0;
              start = temp.indexOf("CONSTRAINT `", start) + 12;
              // get name
              name = temp.substring(start, temp.indexOf("`", start));
              if (!firstTime) {
                foreignKeyDrop += "\n,";
              }
              foreignKeyDrop += " DROP FOREIGN KEY `" + name + "`";
              add.getIndices().remove(name);
              firstTime = false;
              // update temp
              temp = temp.substring(temp.indexOf("FOREIGN KEY") + 11);
            } while (temp.contains("FOREIGN KEY"));
            // remove foreign key
            firstSteps.add(0, foreignKeyDrop + ";");
          }
          if (create.contains("PRIMARY KEY")) {
            if (count != 0) {
              this.firstStep += ",\n ";
            } else {
              this.firstStep += "\n ";
            }
            this.firstStep += "DROP PRIMARY KEY";
            // remove the PRIMARY KEY to make sure the appropriate SQL will be generated if
            // there is a difference
            add.getIndices().remove("PRIMARY");
            count++;
          }
        }
        if (this.count != 0) {
          firstSteps.add(firstStep + ";");
        }
        tablesList.put(table, add);
      }
      return tablesList;
    } catch (SQLException e) {
      throw new DatabaseDifferenceCheckerException(
          "There was an error getting the " + this.db + " database's table, column, and index details.", e, 1017);
    }
  }

  @Override
  public ArrayList<View> getViews() throws DatabaseDifferenceCheckerException {
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
      throw new DatabaseDifferenceCheckerException(
          "There was an error getting the " + this.db + " database's view details.", e, 1018);
    }
  }

  @Override
  protected void testConnection() throws DatabaseDifferenceCheckerException {
    try {
      this.con = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.db
          + "?autoReconnect=true&useSSL=false&maxReconnects=5", this.username, this.password);
      this.con.close();
    } catch (SQLException error) {
      throw new DatabaseDifferenceCheckerException(
          "There was an error with the connection to " + this.db + ". Please try again.", error, 1016);
    }
  }
}
