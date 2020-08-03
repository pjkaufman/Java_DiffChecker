package dbdiffchecker;

/**
 * Used to wrap exceptions that occur throughtout the program.
 *
 * @author Peter Kaufman
 */
public class DatabaseDifferenceCheckerException extends Exception {
  private static final long serialVersionUID = 1L;
  private final int errorCode;

  /**
   * Takes in a message and the cause of the exception and creates an exception.
   *
   * @param message The error message to display to the user.
   * @param cause   The original exception.
   * @param code    The error code of the excpetion.
   */
  public DatabaseDifferenceCheckerException(String message, Exception cause, int code) {
    super(message, cause);
    errorCode = code;
  }

  @Override
  public String toString() {
    String message = super.toString();
    return String.format("%d - %s", errorCode, message.substring(message.indexOf(":") + 2));
  }
}
