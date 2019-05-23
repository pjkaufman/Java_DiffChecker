package dbdiffchecker;

/**
 * Used to wrap exception that occur throughtout the program.
 * @author Peter Kaufman
 * @version 5-23-19
 * @since 7-29-18
 */
public class DatabaseDiffernceCheckerException extends Exception {
  /**
   * Takes in a message and the cause of the exception and creates an exception.
   * @param message The error message to display to the user.
   * @param cause The original exception.
   */
  public DatabaseDiffernceCheckerException(String message, Exception cause) {
    super(message, cause);
  }
}
