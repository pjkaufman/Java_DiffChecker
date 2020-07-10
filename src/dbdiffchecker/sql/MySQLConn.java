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
 * Establishes a connection with a MySQL database based on the password,
 * username, port, host, and database provided.
 *
 * @author Peter Kaufman
 * @version 7-9-20
 * @since 5-21-19
 */
public class MySQLConn extends SQLDbConn {
  private static final String CONN_STRING_FMT = "jdbc:mysql://%s:%s/%s?autoReconnect=true&useSSL=false&maxReconnects=%d";
  private String username;
  private String password;
  private String host;
  private String port;

  /**
   * Sets the instance variables and tests the database connection to make sure
   * that the database can be reached.
   *
   * @param username The username of the MySQL account.
   * @param password The password of the MySQL account.
   * @param host     The host of the MySQL account.
   * @param port     The port MySQL is running on.
   * @param database The database in MySQL that the connection is to be
   *                 established with.
   * @param isLive   Whether or not the database connection is to the live
   *                 database.
   * @throws DatabaseDifferenceCheckerException Error connecting to the database.
   */
  public MySQLConn(String username, String password, String host, String port, String database, boolean isLive)
      throws DatabaseDifferenceCheckerException {
    this.isLive = isLive;
    this.username = username;
    this.password = password;
    this.host = host;
    db = database;
    this.port = port;
    connString = String.format(CONN_STRING_FMT, host, port, db, 150);
    testConnection();
  }

  @Override
  public void establishDatabaseConnection() throws DatabaseDifferenceCheckerException {
    try {
      con = DriverManager.getConnection(connString, username, password);
    } catch (SQLException e) {
      throw new DatabaseDifferenceCheckerException("There was an error connecting to the " + db + " database.", e,
          1013);
    }
  }

  @Override
  public String getTableCreateStatement(String table) throws DatabaseDifferenceCheckerException {
    try (PreparedStatement query = con.prepareStatement("SHOW CREATE TABLE `?`;")) {
      query.setString(1, table);
      ResultSet set = runPreparedStatement(query);
      set.next(); // move to the first result
      return set.getString("Create Table");
    } catch (SQLException e) {
      throw new DatabaseDifferenceCheckerException(
          "There was an error getting the " + table + " table's create statement.", e, 1014);
    }
  }

  /**
   * Gets and returns the create statement of the desired table.
   *
   * @param view The name of the view for which the create statement should be
   *             retrieved.
   * @return The view's create statement.
   * @throws DatabaseDifferenceCheckerException Error when getting a view's create
   *                                            statement.
   */
  public String getViewCreateStatement(String view) throws DatabaseDifferenceCheckerException {
    try (PreparedStatement query = con.prepareStatement("SHOW CREATE VIEW `?`;")) {
      query.setString(1, view);
      ResultSet set = runPreparedStatement(query);
      set.next(); // move to the first result
      return set.getString("Create View");
    } catch (SQLException e) {
      throw new DatabaseDifferenceCheckerException(
          "There was an error getting the " + view + " view's create statement.", e, 1015);
    }
  }

  @Override
  public Map<String, Table> getTableList() throws DatabaseDifferenceCheckerException {
    Map<String, Table> tablesList = new HashMap<>();
    String sql = "SHOW FULL TABLES IN `?` WHERE TABLE_TYPE LIKE 'BASE TABLE';";
    try (PreparedStatement query = con.prepareStatement(sql)) {
      // set up and run the query to get the table names
      query.setString(1, db);
      ResultSet tables = query.executeQuery(sql);
      String table = "";
      String create = "";
      Table add = null;
      int count;
      // for each table in the database
      while (tables.next()) {
        // get the table name and its createStatement
        table = tables.getString("Tables_in_" + db);
        create = getTableCreateStatement(table);
        firstStep.append("ALTER TABLE `" + table + "`");
        count = 0;
        add = new MySQLTable(table, create);
        // if the database is the live database
        if (isLive) {
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
                firstStep.append("\n MODIFY COLUMN " + columnDetails);
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
            StringBuilder foreignKeyDrop = new StringBuilder("ALTER TABLE `" + table + "`");
            String name;
            String temp = create;
            do {
              start = 0;
              start = temp.indexOf("CONSTRAINT `", start) + 12;
              // get name
              name = temp.substring(start, temp.indexOf("`", start));
              if (!firstTime) {
                foreignKeyDrop.append("\n,");
              }
              foreignKeyDrop.append(" DROP FOREIGN KEY `" + name + "`");
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
              firstStep.append(",\n ");
            } else {
              firstStep.append("\n ");
            }
            firstStep.append("DROP PRIMARY KEY");
            // remove the PRIMARY KEY to make sure the appropriate SQL will be generated if
            // there is a difference
            add.getIndices().remove("PRIMARY");
            count++;
          }
        }
        if (count != 0) {
          firstSteps.add(firstStep + ";");
        }
        tablesList.put(table, add);
      }
      return tablesList;
    } catch (SQLException e) {
      throw new DatabaseDifferenceCheckerException(
          "There was an error getting the " + db + " database's table, column, and index details.", e, 1017);
    }
  }

  @Override
  public List<View> getViews() throws DatabaseDifferenceCheckerException {
    List<View> views = new ArrayList<>();
    String sql = "SHOW FULL TABLES IN `?` WHERE TABLE_TYPE LIKE 'VIEW';";
    try (PreparedStatement query = con.prepareStatement(sql)) {
      query.setString(1, db);
      ResultSet set = query.executeQuery(sql);
      while (set.next()) {
        views.add(new View(set.getString("Tables_in_" + db), getViewCreateStatement(set.getString("Tables_in_" + db))));
      }
      return views;
    } catch (SQLException e) {
      throw new DatabaseDifferenceCheckerException("There was an error getting the " + db + " database's view details.",
          e, 1018);
    }
  }

  @Override
  protected void testConnection() throws DatabaseDifferenceCheckerException {
    try (Connection testCon = DriverManager.getConnection(String.format(CONN_STRING_FMT, host, port, db, 5), username,
        password)) {
    } catch (SQLException error) {
      throw new DatabaseDifferenceCheckerException(
          "There was an error with the connection to " + db + ". Please try again.", error, 1016);
    }
  }
}
