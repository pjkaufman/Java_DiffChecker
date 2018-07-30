package dbdiffchecker;

/**
 * Db_conn establishes a connection with a MySQL database based on the password,
 * username, port, host, and database provided.
 * @author Peter Kaufman
 * @version 7-29-18
 * @since 7-29-18
 */
public class DatabaseDiffernceCheckerException extends Exception {
  public DatabaseDiffernceCheckerException(String message) {
    super(message);
  }

  public DatabaseDiffernceCheckerException(String message, Exception cause) {
    super(message, cause);
  }
}