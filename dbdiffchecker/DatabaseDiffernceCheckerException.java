package dbdiffchecker;

/**
 * DatabaseDiffernceCheckerException used to wrap exception that occur throughtout the program.
 * @author Peter Kaufman
 * @version 8-6-18
 * @since 7-29-18
 */
public class DatabaseDiffernceCheckerException extends Exception {
  
  /**
   * Takes in a message and the cause of the exception and creates an exception.
   * @param message the error message to display to the user
   * @param cause the original exception
   */
  public DatabaseDiffernceCheckerException(String message, Exception cause) {
    super(message, cause);
  }
}