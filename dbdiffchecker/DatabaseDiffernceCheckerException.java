package dbdiffchecker;

/**
 * DatabaseDiffernceCheckerException used to wrap exception that occur throughtout the program.
 * Program Name: Database Difference Checker
 * CSCI Course: 325
 * Grade Received: Pass
 * @author Peter Kaufman
 * @version 2-16-19
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