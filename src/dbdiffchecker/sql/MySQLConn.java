package dbdiffchecker.sql;

import dbdiffchecker.DatabaseDifferenceCheckerException;
import com.mysql.cj.exceptions.CJException;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Establishes a connection with a MySQL database based on the password,
 * username, port, host, and database provided.
 *
 * @author Peter Kaufman
 */
public class MySQLConn extends SQLDbConn {
  private static final String CONN_STRING_FMT = "jdbc:mysql://%s:%s/%s?autoReconnect=true&useSSL=false&maxReconnects=%d";
  private String username;
  private String password;
  private String host;
  private String port;
  private StringBuilder firstStep;

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
      handleConnectionExceptions(e, 1013);
    }
  }

  @Override
  public String getTableCreateStatement(String table) throws DatabaseDifferenceCheckerException {
    try (Statement query = con.createStatement()) {
      ResultSet set = runQuery(query, "SHOW CREATE TABLE `" + table + "`;");
      set.next();
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
   * @throws DatabaseDifferenceCheckerException Error getting a view's create
   *                                            statement.
   */
  public String getViewCreateStatement(String view) throws DatabaseDifferenceCheckerException {
    try (Statement query = con.createStatement()) {
      ResultSet set = runQuery(query, "SHOW CREATE VIEW `" + view + "`;");
      set.next();
      return set.getString("Create View");
    } catch (SQLException e) {
      throw new DatabaseDifferenceCheckerException(
          "There was an error getting the " + view + " view's create statement.", e, 1015);
    }
  }

  @Override
  public Map<String, Table> getTableList() throws DatabaseDifferenceCheckerException {
    Map<String, Table> tablesList = new HashMap<>();
    String sql = "SHOW FULL TABLES IN `" + db + "` WHERE TABLE_TYPE LIKE 'BASE TABLE';";
    try (Statement query = con.createStatement()) {
      ResultSet tables = runQuery(query, sql);
      String tableName;
      String create;
      Table newTable;
      boolean hasFirstStep;
      while (tables.next()) {
        tableName = tables.getString("Tables_in_" + db);
        create = getTableCreateStatement(tableName);
        firstStep = new StringBuilder("ALTER TABLE `" + tableName + "`");
        hasFirstStep = false;
        newTable = new MySQLTable(tableName, create);
        if (isLive) {
          if (create.contains("AUTO_INCREMENT")) {
            removeAutoIncrement(create, newTable);
            hasFirstStep = true;
          }
          if (create.contains("FOREIGN KEY")) {
            dropAllForeignKeys(create, newTable);
          }
          if (create.contains("PRIMARY KEY")) {
            if (hasFirstStep) {
              firstStep.append(", \n");
            }
            firstStep.append(" DROP PRIMARY KEY");
            // remove the PRIMARY KEY to make sure the appropriate SQL will be generated if
            // there is a difference in schema structure for this table
            newTable.getIndices().remove("PRIMARY");
            hasFirstStep = true;
          }
        }
        if (hasFirstStep) {
          firstSteps.add(firstStep + ";");
        }
        tablesList.put(tableName, newTable);
      }
      return tablesList;
    } catch (SQLException e) {
      throw new DatabaseDifferenceCheckerException(
          "There was an error getting the " + db + " database's table, column, and index details.", e, 1017);
    }
  }

  /**
   * Removes the auto increment from a the table definition and add it to the
   * first step if the table is updated.
   *
   * @param createStatement The create statement to modify.
   * @param table           The table that the column with the auto increment
   *                        exists on.
   */
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
        firstStep.append("\n MODIFY COLUMN " + columnDetails); // modify the column definition in order properly
                                                               // generate SQL if there is a difference found
        table.getColumns().put(columnName, new Column(columnName, columnDetails));
        break;
      }
    }
  }

  /**
   * Ups the foreign key count on the table and makes sure the foreign keys will
   * be dropped if the table needs to be updated.
   *
   * @param createStatement
   * @param table
   */
  private void dropAllForeignKeys(String createStatement, Table table) {
    int start;
    boolean firstTime = true;
    StringBuilder foreignKeyDrop = new StringBuilder("ALTER TABLE `" + table.name + "`");
    String indexName;
    String toSearch = createStatement;
    do {
      start = toSearch.indexOf("CONSTRAINT `", 0) + 12;
      indexName = toSearch.substring(start, toSearch.indexOf("`", start));
      if (!firstTime) {
        foreignKeyDrop.append("\n,");
      }
      foreignKeyDrop.append(" DROP FOREIGN KEY `" + indexName + "`");
      table.getIndices().remove(indexName);
      firstTime = false;
      toSearch = toSearch.substring(toSearch.indexOf("FOREIGN KEY") + 11);
    } while (toSearch.contains("FOREIGN KEY"));
    firstSteps.add(0, foreignKeyDrop + ";");
  }

  @Override
  public List<View> getViews() throws DatabaseDifferenceCheckerException {
    List<View> views = new ArrayList<>();
    String sql = "SHOW FULL TABLES IN `" + db + "` WHERE TABLE_TYPE LIKE 'VIEW';";
    try (Statement query = con.createStatement()) {
      ResultSet set = runQuery(query, sql);
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
      // just tests that the connection can be established with the database
    } catch (SQLException error) {
      handleConnectionExceptions(error, 1016);
    }
  }

  private void handleConnectionExceptions(SQLException e, int code) throws DatabaseDifferenceCheckerException {
    Throwable cause = e.getCause();
    String errorMessage;
    if (cause instanceof CJException && cause.toString().contains("Access denied")) {
      errorMessage = "The username or passowrd provided is not correct for the provided host and port for " + db + ".";
    } else if (cause.getCause() instanceof UnknownHostException) {
      errorMessage = "Please make sure that the host is correct and valid for " + db + ".";
    } else {
      errorMessage = "There was an error connecting to the " + db + " database.";
    }
    throw new DatabaseDifferenceCheckerException(errorMessage, e, code);
  }
}
