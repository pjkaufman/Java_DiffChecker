package dbdiffchecker;

/**
 * Establishes a connection with a database and runs the necessary statements to
 * get schema information.
 * @author Peter Kaufman
 * @version 1-6-20
 * @since 5-24-19
 */
abstract public class DbConn {
  /**
   * This is the default constructor for this class, <b> Needed for
   * Serialization</b>.
   */
  public DbConn() {}

  /**
   * Returns the name of the database to connect to.
   * @author Peter Kaufman
   * @return The name of the database to connect to.
   */
  abstract public String getDatabaseName();

  /**
   * Makes a connection to the database using the necessary information.
   * @author Peter Kaufman
   * @throws DatabaseDifferenceCheckerException Error when connecting to the
   *         database.
   */
  abstract public void establishDatabaseConnection() throws DatabaseDifferenceCheckerException;

  /**
   * Closes the connection to the database.
   * @author Peter Kaufman
   * @throws DatabaseDifferenceCheckerException Error when closing the connection
   *         to the database.
   */
  abstract public void closeDatabaseConnection() throws DatabaseDifferenceCheckerException;

  /**
   * Takes a statement and attempts to run it.
   * @author Peter Kaufman
   * @param Statement The statement to run on the connected database.
   * @throws DatabaseDifferenceCheckerException An error when running the SQL
   *         statement.
   */
  abstract public void runStatement(String Statement) throws DatabaseDifferenceCheckerException;

  /**
   * Attempts to connect to the database and responds accordingly.
   * @author Peter Kaufman
   * @throws DatabaseDifferenceCheckerException Error attempting to connect to the
   *         database (it might be unavailable).
   */
  abstract protected void testConnection() throws DatabaseDifferenceCheckerException;
}
