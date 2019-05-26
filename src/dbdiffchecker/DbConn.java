package dbdiffchecker;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Establishes a connection with a database and runs the necessary statements to
 * get schema information.
 * @author Peter Kaufman
 * @version 5-24-19
 * @since 5-24-19
 */
abstract public class DbConn {
  /**
   * Returns the name of the database to connect to.
   * @author Peter Kaufman
   * @return The name of the database to connect to.
   */
  abstract public String getDatabaseName();

  /**
   * Makes a connection to the database using the necessary information.
   * @author Peter Kaufman
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
  abstract public void closeDatabaseConnection() throws DatabaseDiffernceCheckerException;

  /**
   * Takes a statement and attempts to run it.
   * @author Peter Kaufman
   * @param Statement The statement to run on the connected database.
   * @throws DatabaseDiffernceCheckerException An error when running the SQL
   *         statement.
   */
  abstract public void runStatement(String Statement) throws DatabaseDiffernceCheckerException;

  /**
   * Attempts to connect to the database and responds accordingly.
   * @author Peter Kaufman
   * @throws DatabaseDiffernceCheckerException Error attempting to connect to the
   *         database (it might be unavailable).
   */
  abstract protected void testConnection() throws DatabaseDiffernceCheckerException;
}
