package dbdiffchecker.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import dbdiffchecker.DbConn;
import dbdiffchecker.DatabaseDiffernceCheckerException;

/**
 * Establishes a connection with an SQL database and runs the necessary SQL
 * statements to get schema information.
 * @author Peter Kaufman
 * @version 5-24-19
 * @since 5-24-19
 */
abstract public class SQLDbConn extends DbConn {
  protected String db = "";
  protected String connString = "";
  protected String type = "";
  protected String firstStep = "";
  protected int count = 0;
  protected Connection con = null;
  protected ArrayList<String> firstSteps = new ArrayList<>();

  @Override
  public String getDatabaseName() {
    return this.db;
  }

  /**
   * Returns the first steps to be taken in order to run the SQL statements. These
   * SQL statements are used to drop Primary Keys and remove auto_increments on
   * the database provided. <b>Note: this funntion will return an empty ArrayList
   * if the function is called on the development database.</b>
   * @author Peter Kaufman
   * @return The first steps to be taken in order to run the SQL statements.
   */
  public ArrayList<String> getFirstSteps() {
    return this.firstSteps;
  }

  /**
   * Makes a connection to the database using the necessary information.
   * @author Juan Nadal
   * @see <a href=
   *      "https://www.youtube.com/watch?v=e3gnhsGqNmI">https://www.youtube.scom/watch?v=e3gnhsGqNmI</a>
   * @throws DatabaseDiffernceCheckerException Error when connecting to the
   *         database.
   */
  abstract public void establishDatabaseConnection() throws DatabaseDiffernceCheckerException;

  /**
   * Closes the connection to the database.
   * @author Peter Kaufman
   * @throws DatabaseDiffernceCheckerException Error when closing the connection
   *         to the database.
   */
  public void closeDatabaseConnection() throws DatabaseDiffernceCheckerException {
    try {
      this.con.close();
    } catch (SQLException e) {
      throw new DatabaseDiffernceCheckerException("There was an error closing the " + this.db + " database.", e);
    }
  }

  /**
   * Gets and returns the create statement of the specified table.
   * @author Peter Kaufman
   * @param table The name of the table for which the create statement should be
   *        retrieved.
   * @return Table's create statement or an empty string if an error occurred.
   * @throws DatabaseDiffernceCheckerException Error when getting a table's create
   *         statement.
   */
  abstract public String getTableCreateStatement(String table) throws DatabaseDiffernceCheckerException;

  /**
   * Gets the tables, columns, and indices of the database.
   * @author Peter Kaufman
   * @return The list of tables in the database where the table name is the key.
   * @throws DatabaseDiffernceCheckerException Error when getting table data.
   */
  abstract public HashMap<String, Table> getTableList() throws DatabaseDiffernceCheckerException;

  /**
   * Gets a list of views of that exist in the database.
   * @author Peter Kaufman
   * @return All of the views in the database.
   * @throws DatabaseDiffernceCheckerException Error when getting view data.
   */
  abstract public ArrayList<View> getViews() throws DatabaseDiffernceCheckerException;

  /**
   * Takes an SQL statement and attempts to run it.
   * @author Peter Kaufman
   * @param sqlStatement An SQL statement.
   * @throws DatabaseDiffernceCheckerException An error when running the SQL
   *         statement.
   */
  public void runStatement(String sqlStatement) throws DatabaseDiffernceCheckerException {
    try {
      this.establishDatabaseConnection();
      Statement query = this.con.createStatement();
      query.executeUpdate(sqlStatement);
      this.closeDatabaseConnection();
    } catch (SQLException e) {
      throw new DatabaseDiffernceCheckerException(
          "There was an error running " + sqlStatement + " on the " + this.db + " database.", e);
    }
  }

  /**
   * Attempts to connect to the database and responds accordingly.
   * @author Peter Kaufman
   * @throws SQLExceptDatabaseDiffernceCheckerExceptionion Error connectecting to
   *         the database (it might be unavailable).
   */
  abstract protected void testConnection() throws DatabaseDiffernceCheckerException;
}
