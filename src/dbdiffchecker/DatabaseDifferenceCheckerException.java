package dbdiffchecker;

/**
 * Used to wrap exception that occur throughtout the program.
 * @author Peter Kaufman
 * @version 1-6-20
 * @since 7-29-18
 */
public class DatabaseDifferenceCheckerException extends Exception {
  // Instance variables
  private int errorCode;
  /**
   * Takes in a message and the cause of the exception and creates an exception.
   * @param message The error message to display to the user.
   * @param cause The original exception.
   * @param code The errorCode of the excpetion.
   */
  public DatabaseDifferenceCheckerException(String message, Exception cause, int code) {
    super(message, cause);
    this.errorCode = code;
  }

  @Override
  public String toString() {
    return super.toString().replace("dbdiffchecker.DatabaseDifferenceCheckerException:", errorCode + " -");
  }
}
