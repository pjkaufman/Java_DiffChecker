package dbdiffchecker.sql;

import dbdiffchecker.DatabaseDifferenceCheckerException;
import dbdiffchecker.DbConn;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Establishes a connection with an SQL database and runs the necessary SQL
 * statements to get schema information.
 *
 * @author Peter Kaufman
 * @version 7-6-20
 * @since 5-24-19
 */
public abstract class SQLDbConn extends DbConn {
  protected String db;
  protected String connString;
  protected String type;
  protected String firstStep;
  protected int count = 0;
  protected Connection con = null;
  protected List<String> firstSteps = new ArrayList<>();

  @Override
  public String getDatabaseName() {
    return this.db;
  }

  /**
   * Returns the first steps to be taken in order to run the SQL statements. These
   * SQL statements are used to drop Primary Keys and remove auto_increments on
   * the database provided. <b>Note: this funntion will return an empty ArrayList
   * if the function is called on the development database.</b>
   *
   * @return The first steps to be taken in order to run the SQL statements.
   */
  public List<String> getFirstSteps() {
    return this.firstSteps;
  }

  @Override
  public abstract void establishDatabaseConnection() throws DatabaseDifferenceCheckerException;

  @Override
  public void closeDatabaseConnection() throws DatabaseDifferenceCheckerException {
    try {
      this.con.close();
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
   * @throws DatabaseDifferenceCheckerException Error when getting a table's
   *                                            create statement.
   */
  public abstract String getTableCreateStatement(String table) throws DatabaseDifferenceCheckerException;

  /**
   * Gets the tables, columns, and indices of the database.
   *
   * @return The list of tables in the database where the table name is the key.
   * @throws DatabaseDifferenceCheckerException Error when getting table data.
   */
  public abstract Map<String, Table> getTableList() throws DatabaseDifferenceCheckerException;

  /**
   * Gets a list of views of that exist in the database.
   *
   * @return All of the views in the database.
   * @throws DatabaseDifferenceCheckerException Error when getting view data.
   */
  public abstract List<View> getViews() throws DatabaseDifferenceCheckerException;

  @Override
  public void runStatement(String sqlStatement) throws DatabaseDifferenceCheckerException {
    try (Statement query = this.con.createStatement()) {
      query.executeUpdate(sqlStatement);
    } catch (SQLException e) {
      throw new DatabaseDifferenceCheckerException(
          String.format("There was an error running %s on the %s database.", sqlStatement, db), e, 1012);
    }
  }

  @Override
  protected abstract void testConnection() throws DatabaseDifferenceCheckerException;
}
