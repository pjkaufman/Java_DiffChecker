package dbdiffchecker;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.SwingWorker;

/**
 * ErrorPopup is a JFrame that shows a message about an error that occurred.
 * @author Peter Kaufman
 * @version 8-6-18
 * @since 9-21-17
 */
public class ErrorPopup extends JFrameV2 {
  // Instance variables
  private JLabel errorLabel = new JLabel();
  private JLabel titleLabel = new JLabel();

  /**
   * Iinitializes s JFrame that displays an error to the user and logs the error.
   * @author Peter Kaufman
   * @param error Error message to display to the user and to log.
   */
  public ErrorPopup(DatabaseDiffernceCheckerException error) {

    StringWriter sw = new StringWriter();
    error.printStackTrace(new PrintWriter(sw));
    String exceptionAsString = sw.toString();
    try {
      log(exceptionAsString);
    } catch(DatabaseDiffernceCheckerException err) {
      System.out.println("Could not log the error...");
    } 
    error.printStackTrace();
    this.error = false;
    initComponents();
    this.errorLabel.setText(error.getMessage());
    this.setVisible(true);
  }

  /**
   * Ssets up the GUI Layout, sets up all action events, and initializes instance variables.
   * @author Peter Kaufman
   */
  private void initComponents() {
    // set up JFrame properties
    setTitle("Error");
    setType(Window.Type.POPUP);
    setMinimumSize(new Dimension(430, 100));
    setResizable(false);
    // set component properties
    titleLabel.setFont(new Font("Tahoma", 1, 18));
    titleLabel.setText("An Error Occured.");
    errorLabel.setFont(new Font("Tahoma", 0, 14));
    // add components
    getContentPane().setLayout(new BorderLayout());
    add(titleLabel, BorderLayout.NORTH);
    add(errorLabel, BorderLayout.CENTER);
    pack();
  }
}
