package dbdiffchecker;

/**
 * Establishes a connection with a database and runs the necessary statements to
 * get schema information.
 *
 * @author Peter Kaufman
 */
public abstract class DbConn {
  /**
   * <b>Needed for Serialization</b>.
   */
  public DbConn() {
  }

  /**
   * Returns the name of the database to connect to.
   *
   * @return The name of the database to connect to.
   */
  public abstract String getDatabaseName();

  /**
   * Makes a connection to the database using the necessary information.
   *
   * @throws DatabaseDifferenceCheckerException Error connecting to the database.
   */
  public abstract void establishDatabaseConnection() throws DatabaseDifferenceCheckerException;

  /**
   * Closes the connection to the database.
   *
   * @throws DatabaseDifferenceCheckerException Error closing the connection to the database.
   */
  public abstract void closeDatabaseConnection() throws DatabaseDifferenceCheckerException;

  /**
   * Takes a statement and attempts to run it.
   *
   * @param statement The statement to run on the connected database.
   * @throws DatabaseDifferenceCheckerException An error running the statement.
   */
  public abstract void runStatement(String statement) throws DatabaseDifferenceCheckerException;

  /**
   * Attempts to connect to the database and responds accordingly.
   *
   * @throws DatabaseDifferenceCheckerException Error attempting to connect to the
   *                                            database (it might be
   *                                            unavailable).
   */
  protected abstract void testConnection() throws DatabaseDifferenceCheckerException;
}
