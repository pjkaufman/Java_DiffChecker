package dbdiffchecker;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.swing.JLabel;
import javax.swing.BorderFactory;
import java.awt.Color;

/**
 * A JFrame that displays an error that occurred.
 * @author Peter Kaufman
 * @version 6-15-19
 * @since 9-21-17
 */
public class ErrorPopup extends JFrameV2 {
  // Instance variables
  private JLabel errorLabel = new JLabel();
  private JLabel titleLabel = new JLabel();
  private int sizeFactor = 1;

  /**
   * Initializes a JFrame that displays an error to the user and logs the error.
   * @author Peter Kaufman
   * @param error The error message to display to the user and to log.
   */
  public ErrorPopup(DatabaseDifferenceCheckerException error) {
    String errorMessage = "";
    StringWriter sw = new StringWriter();
    error.printStackTrace(new PrintWriter(sw));
    String exceptionAsString = sw.toString();
    try {
      log(exceptionAsString);
    } catch (DatabaseDifferenceCheckerException err) {
      System.out.println("Could not log the error...");
    }
    this.error = false;
    errorMessage = error.getMessage().substring(error.getMessage().indexOf(":") + 2);
    sizeFactor = errorMessage.length();
    initComponents();
    this.errorLabel.setText(errorMessage);
    this.setVisible(true);
  }

  @Override
  protected void initComponents() {
    // set up JFrame properties
    setTitle("Error");
    setType(Window.Type.UTILITY);
    setMinimumSize(new Dimension(7 * sizeFactor, 100));
    setResizable(false);
    // set component properties
    titleLabel.setFont(new Font("Tahoma", 1, 18));
    titleLabel.setText("An Error Occured.");
    errorLabel.setFont(new Font("Tahoma", 0, 14));
    // add components
    getContentPane().setLayout(new BorderLayout());
    add(titleLabel, BorderLayout.NORTH);
    add(errorLabel, BorderLayout.CENTER);
    this.getRootPane().setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, Color.white));
    pack();
  }
}
