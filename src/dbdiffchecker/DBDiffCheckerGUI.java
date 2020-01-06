package dbdiffchecker;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * A JFrame that takes user input to decide which JFrame to open.
 * @author Peter Kaufman
 * @version 10-27-19
 * @since 9-20-17
 */
public class DBDiffCheckerGUI extends JFrameV2 {
  // Instance variables
  private static int databaseTypeIndex = 0;
  private final String[] optionLabelText = { "1-Database compare using 2 database connections",
      "2-Database compare using 1 database connection", "3-Take database snapshot using 1" + " database connection",
      "4-Review the SQL statement(s) from the last run ", "5-Review the logs" };
  private final String[] databaseTypes = { "Select Database Type", "MySQL", "SQLite", "Couchbase", "MongoDB" };
  private DBCompare compareGUI;
  private JTextField input = new JTextField(1);
  private JButton continueBtn = new JButton("Continue");
  private JComboBox<String> databaseType = new JComboBox<>(databaseTypes);
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
   * Initializes the first JFrame.
   * @author Peter Kaufman
   * @param args Parameters from the user. <b>Note it is not used</b>
   */
  public static void main(String[] args) {
    /* Create and display the JFrame */
    EventQueue.invokeLater(new Runnable() {
      @Override
      public void run() {
        new DBDiffCheckerGUI();
      }
    });
  }

  @Override
  protected void initComponents() {
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
      @Override
      public void mouseClicked(MouseEvent evt) {
        continueBtnMouseClicked(evt);
      }
    });
    input.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void insertUpdate(DocumentEvent e) {
        Runnable format = new Runnable() {
          @Override
          public void run() {
            String size = "" + optionLabelText.length;
            String text = input.getText();
            String regex = "";
            if (size.length() > 1) {
              regex += "[0-9]{1," + (size.length() - 1) + "}";
            }
            regex += "[0-" + size.charAt(size.length() - 1) + "]{0,1}";
            if (!text.matches(regex)) {
              input.setText(text.substring(0, text.length() - 1));
            }
          }
        };
        SwingUtilities.invokeLater(format);
      }

      @Override
      public void removeUpdate(DocumentEvent e) {}

      @Override
      public void changedUpdate(DocumentEvent e) {}
    });
    // add components and add the labels to the apropriate ArrayList
    userOptions.setLayout(new GridLayout(optionLabelText.length + 1, 1));
    databaseType.setSize(new Dimension(10, 10));
    databaseType.setSelectedIndex(databaseTypeIndex);
    cpnr.add(databaseType);
    userOptions.add(databaseType);
    JLabel tempLabel = null;
    for (int i = 0; i < optionLabelText.length; i++) {
      tempLabel = new JLabel(optionLabelText[i], JLabel.CENTER);
      cpnr.add(tempLabel);
      userOptions.add(tempLabel);
    }
    submitArea.add(promptLabel);
    submitArea.add(input);
    submitArea.add(continueBtn);
    getContentPane().setLayout(new BorderLayout());
    this.add(optionTitleLabel, BorderLayout.NORTH);
    this.add(userOptions, BorderLayout.CENTER);
    this.add(submitArea, BorderLayout.SOUTH);
    pack();
    setVisible(true);
  }

  /**
   * Determines which JFrame to open based on user input.
   * @author Peter Kaufman
   * @param evt The continue button click event object.
   */
  private void continueBtnMouseClicked(MouseEvent evt) {
    databaseTypeIndex = databaseType.getSelectedIndex();
    String databaseSelected = databaseTypes[databaseTypeIndex];
    switch (input.getText().trim()) {
    case "1":
      chooseTwoDBCompare(databaseSelected);
      break;
    case "2":
      if (FileHandler.fileExists(databaseSelected + "_" + FileHandler.databaseSnapshotFileName)) {
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
   * Opens a JFrame with log information based on what file name is passed to it.
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
    } catch (DatabaseDifferenceCheckerException cause) {
      try {
        log("There was an error recovering the last list of statements.");
      } catch (DatabaseDifferenceCheckerException logError) {
        System.out.println("unable to log error... " + logError.getMessage());
      }
      error(cause);
    }
  }

  /**
   * Determines whether to run a two database comparisons and if so it determiens
   * which one to run.
   * @author Peter Kaufman
   * @param databaseSelected The user selected database implimentation.
   */
  private void chooseTwoDBCompare(String databaseSelected) {
    switch (databaseSelected) {
    case "SQLite":
      compareGUI = new SQLiteCompare(0);
      this.close();
      break;
    case "MySQL":
      compareGUI = new MySQLCompare(0);
      this.close();
      break;
    case "Couchbase":
      compareGUI = new CouchbaseCompare(0);
      this.close();
      break;
    case "MongoDB":
      compareGUI = new MongoDBCompare(0);
      this.close();
      break;
    default:
      optionTitleLabel.setText("Please select a database type.");
    }
  }

  /**
   * Determines whether to run a one database comparisons and if so it determiens
   * which one to run.
   * @author Peter Kaufman
   * @param databaseSelected The user selected database implimentation
   */
  private void chooseOneDBCompare(String databaseSelected) {
    switch (databaseSelected) {
    case "SQLite":
      compareGUI = new SQLiteCompare(1);
      this.close();
      break;
    case "MySQL":
      compareGUI = new MySQLCompare(1);
      this.close();
      break;
    case "Couchbase":
      compareGUI = new CouchbaseCompare(1);
      this.close();
      break;
    case "MongoDB":
      compareGUI = new MongoDBCompare(1);
      this.close();
      break;
    default:
      optionTitleLabel.setText("Please select a database type.");
    }
  }

  /**
   * Determines whether to run a database snapshot and if so it determiens which
   * one to run.
   * @author Peter Kaufman
   * @param databaseSelected The user selected database implimentation
   */
  private void chooseDBSnapshot(String databaseSelected) {
    switch (databaseSelected) {
    case "SQLite":
      compareGUI = new SQLiteCompare(2);
      this.close();
      break;
    case "MySQL":
      compareGUI = new MySQLCompare(2);
      this.close();
      break;
    case "Couchbase":
      compareGUI = new CouchbaseCompare(2);
      this.close();
      break;
    case "MongoDB":
      compareGUI = new MongoDBCompare(2);
      this.close();
      break;
    default:
      optionTitleLabel.setText("Please select a database type.");
    }
  }
}
