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
import javax.swing.BorderFactory;
import java.awt.Color;

/**
 * ErrorPopup is a JFrame that shows a message about an error that occurred.
 * Program Name: Database Difference Checker CSCI Course: 325 Grade Received:
 * Pass
 * @author Peter Kaufman
 * @version 3-17-19
 * @since 9-21-17
 */
public class ErrorPopup extends JFrameV2 {
  // Instance variables
  private JLabel errorLabel = new JLabel();
  private JLabel titleLabel = new JLabel();

  /**
   * Initializes a JFrame that displays an error to the user and logs the error.
   * @author Peter Kaufman
   * @param error Error message to display to the user and to log.
   */
  public ErrorPopup(DatabaseDiffernceCheckerException error) {

    String errorMessage = "";
    StringWriter sw = new StringWriter();
    error.printStackTrace(new PrintWriter(sw));
    String exceptionAsString = sw.toString();
    try {
      log(exceptionAsString);
    } catch (DatabaseDiffernceCheckerException err) {
      System.out.println("Could not log the error...");
    }
    this.error = false;
    errorMessage = error.getMessage().substring(error.getMessage().indexOf(":") + 2);
    initComponents(errorMessage.length());
    this.errorLabel.setText(errorMessage);
    this.setVisible(true);
  }

  /**
   * Sets up the GUI Layout, sets up all action events, and initializes instance
   * variables.
   * @author Peter Kaufman
   */
  private void initComponents(int sizeFactor) {
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
