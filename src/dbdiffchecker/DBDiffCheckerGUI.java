package dbdiffchecker;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

/**
 * DBDiffCheckerGUI is a JFrame that takes user input to decide which JFrame
 * to open. 
 * @author Peter Kaufman
 * @version 2-16-19
 * @since 9-20-17
 */
public class DBDiffCheckerGUI extends JFrameV2 {
  // Instance variables
  private final String[] optionLabelText = { "1-Database compare using 2 database connections",
      "2-Database compare using 1 database connection", "3-Take database snapshot using 1" + " database connection",
      "4-Review the SQL statement(s) from the last run ", "5-Review the logs" };
  private final String[] databaseTypes = { "Select Database Type", "MySQL", "SQLite" };
  private JTextField input = new JTextField(1);
  private JButton continueBtn = new JButton("Continue");
  private JComboBox databaseType = new JComboBox(databaseTypes);
  private JLabel optionTitleLabel = new JLabel("Database Options", JLabel.CENTER);
  private JLabel promptLabel = new JLabel("Enter method to use:");
  private JPanel userOptions = new JPanel();
  private JPanel submitArea = new JPanel();

  /**
   * Initializes a JFrame which will be used by the user to navigate through the
   * application.
   * @author Peter Kaufman
   */
  public DBDiffCheckerGUI() {
    initComponents();
    error = false;
    clase = this.getClass().getName();
  }

  /**
   * InitComonents sets up the GUI Layout, sets up all action events, and
   * initializes instance variables.
   */
  private void initComponents() {
    cpnr.add(promptLabel);
    cpnr.add(input);
    cpnr.add(continueBtn);
    cpnt.add(optionTitleLabel);
    // set up JFrame properties
    setSize(330, 230);
    setMinimumSize(new Dimension(370, 200));
    this.setTitle("Database Difference Checker");
    // set component properties
    optionTitleLabel.setFont(new Font("Tahoma", 1, 11));
    // add listeners
    continueBtn.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent evt) {
        continueBtnMouseClicked(evt);
      }
    });
    // add components and add the labels to the apropriate ArrayList
    userOptions.setLayout(new GridLayout(optionLabelText.length + 1, 1));
    databaseType.setSize(new Dimension(10, 10));
    cpnr.add(databaseType);
    userOptions.add(databaseType);

    JLabel tempLabel = null;
    for (int i = 0; i < optionLabelText.length; i++) {
      tempLabel = new JLabel(optionLabelText[i], JLabel.CENTER);
      cpnr.add(tempLabel);
      userOptions.add(tempLabel);
    }
    submitArea.setLayout(new FlowLayout());
    submitArea.add(promptLabel);
    submitArea.add(input);
    submitArea.add(continueBtn);
    getContentPane().setLayout(new BorderLayout());
    this.add(optionTitleLabel, BorderLayout.NORTH);
    this.add(userOptions, BorderLayout.CENTER);
    this.add(submitArea, BorderLayout.SOUTH);
    pack();
  }

  /**
   * Determines which JFrame to open based on user input.
   * @author Peter Kaufman
   * @param evt The continue button click event object.
   */
  private void continueBtnMouseClicked(MouseEvent evt) {
    String databaseSelected = databaseTypes[databaseType.getSelectedIndex()];
    switch (input.getText().trim()) {
      case "1":
        chooseTwoDBCompare(databaseSelected);
        break;
      case "2":
        if (FileHandler.fileExists(FileHandler.databaseSnapshotFileName)) {
          chooseOneDBCompare(databaseSelected);
        } else {
          optionTitleLabel.setText("Please create a database snapshot first.");
        }
        break;
      case "3":
        chooseDBSnapshot(databaseSelected);
        break;
      case "4":
        if (FileHandler.fileExists(FileHandler.lastSequelStatementFileName)) {
          displayLog(FileHandler.lastSequelStatementFileName);
          this.close();
        } else {
          optionTitleLabel.setText("The DBC has not been run before.");
        }
        break;
      case "5":
        if (FileHandler.fileExists(FileHandler.logFileName)) {
          displayLog(FileHandler.logFileName);
          this.close();
        } else {
          optionTitleLabel.setText("The DBC has not been run before.");
        }
        break;
      default:
        optionTitleLabel.setText("Please enter a number 1 to " + optionLabelText.length + ".");
      }
  }

  /**
   * Opens a JFrame with log information depending on what file name is passed to
   * it.
   * @author Peter Kaufman
   * @param file The file to have its contents displayed.
   */
  private void displayLog(String file) {
    try {

      String title;
      Result rs = new Result(null);
      if (file.equals(FileHandler.logFileName)) {

        title = "The Run Log:";
      } else {

        title = "Last Set of SQL Statements Run:";
      }
      rs.results(FileHandler.readFrom(file), title);
      rs.setTitle(title.substring(0, title.length() - 1));
    } catch (IOException e) {
      try {
        log("There was an error recovering the last list of SQL statements.");
      } catch (DatabaseDiffernceCheckerException logError) {
        System.out.println("unable to log error... " + logError.getMessage());
      }
      error(new DatabaseDiffernceCheckerException("There was an error recovering the" + " last list of SQL statements.",
          e));
    }
  }

  private void chooseTwoDBCompare(String databaseSelected) {
    JFrameV2 compare2Databases;
    switch(databaseSelected) {
      case "SQLite":
        compare2Databases = new SQLiteCompare(0);
        compare2Databases.setSize(575, 325);
        compare2Databases.setVisible(true);
        this.close();
        break;
      case "MySQL":
        compare2Databases = new DBCompare(0);
        compare2Databases.setSize(575, 325);
        compare2Databases.setVisible(true);
        this.close();
        break;
      default:
        optionTitleLabel.setText("Please select a database type.");
    }
  }

  private void chooseOneDBCompare(String databaseSelected) {
    JFrameV2 compare1Database;
    switch(databaseSelected) {
      case "SQLite":
        compare1Database = new SQLiteCompare(1);
        compare1Database.setSize(350, 275);
        compare1Database.setVisible(true);
        this.close();
        break;
      case "MySQL":
        compare1Database = new DBCompare(1);
        compare1Database.setSize(350, 275);
        compare1Database.setVisible(true);
        this.close();
        break;
      default:
        optionTitleLabel.setText("Please select a database type.");
    }
  }

  private void chooseDBSnapshot(String databaseSelected) {
    JFrameV2 databaseSnapshot;
    switch(databaseSelected) {
      case "SQLite":
        databaseSnapshot = new SQLiteCompare(2);
        databaseSnapshot.setSize(350, 275);
        databaseSnapshot.setVisible(true);
        this.close();
        break;
      case "MySQL":
        databaseSnapshot = new DBCompare(2);
        databaseSnapshot.setSize(350, 275);
        databaseSnapshot.setVisible(true);
        this.close();
        break;
      default:
        optionTitleLabel.setText("Please select a database type.");
    }
  }

  /**
   * Sets up and prepares the GUI for the user and initializes the first JFrame
   * @author Peter Kaufman
   * @param args is not used.
   */
  public static void main(String[] args) {

    /* Create and display the form */
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        DBDiffCheckerGUI gui = new DBDiffCheckerGUI();
        gui.setVisible(true);
      }
    });
  }
}
