package dbdiffchecker.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dbdiffchecker.DatabaseDifferenceCheckerException;
import dbdiffchecker.DbConn;

/**
 * Establishes a connection with an SQL database and runs the necessary SQL
 * statements to get schema information.
 *
 * @author Peter Kaufman
 */
public abstract class SQLDbConn extends DbConn {
  protected String db;
  protected String connString;
  protected boolean isLive;
  protected Connection con;
  protected List<String> firstSteps = new ArrayList<>();

  @Override
  public String getDatabaseName() {
    return db;
  }

  /**
   * Returns the first steps to be taken in order to run the SQL statements. These
   * SQL statements are used to drop Primary Keys and remove auto_increments on
   * the database provided.
   *
   * @return The first steps to be taken in order to run the SQL statements.
   */
  public List<String> getFirstSteps() {
    return firstSteps;
  }

  @Override
  public abstract void establishDatabaseConnection() throws DatabaseDifferenceCheckerException;

  @Override
  public void closeDatabaseConnection() throws DatabaseDifferenceCheckerException {
    try {
      con.close();
    } catch (SQLException e) {
      throw new DatabaseDifferenceCheckerException(String.format("There was an error closing the %s database.", db), e,
          1011);
    }
  }

  /**
   * Gets and returns the create statement of the specified table.
   *
   * @param table The name of the table for which the create statement should be
   *              retrieved.
   * @return Table's create statement or an empty string if an error occurred.
   * @throws DatabaseDifferenceCheckerException Error getting a table's create
   *                                            statement.
   */
  public abstract String getTableCreateStatement(String table) throws DatabaseDifferenceCheckerException;

  /**
   * Gets the tables, columns, and indices of the database.
   *
   * @return The list of tables in the database where the table name is the key.
   * @throws DatabaseDifferenceCheckerException Error getting table data.
   */
  public abstract Map<String, Table> getTableList() throws DatabaseDifferenceCheckerException;

  /**
   * Gets a list of views of that exist in the database.
   *
   * @return All of the views in the database.
   * @throws DatabaseDifferenceCheckerException Error getting view data.
   */
  public abstract List<View> getViews() throws DatabaseDifferenceCheckerException;

  @Override
  public void runStatement(String sqlStatement) throws DatabaseDifferenceCheckerException {
    try (Statement query = con.createStatement()) {
      query.executeUpdate(sqlStatement);
    } catch (SQLException e) {
      throw new DatabaseDifferenceCheckerException(
          String.format("There was an error running %s on the %s database.", sqlStatement, db), e, 1012);
    }
  }

  @Override
  protected abstract void testConnection() throws DatabaseDifferenceCheckerException;
}
