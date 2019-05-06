package dbdiffchecker;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.border.TitledBorder;
import dbdiffchecker.FileHandler;

/**
 * DBCompare is a JFrame that takes user input to make a comparison between 1
 * devDatabase and a devDatabase snapshot or to take a devDatabase snapshot.
 * Program Name: Database Difference Checker
 * CSCI Course: 325
 * Grade Received: Pass
 * @author Peter Kaufman
 * @version 2-16-19
 * @since 9-20-17
 */
public class SQLiteCompare extends JFrameV2 {
  // Instance variables
  private int type = 0;
  private final String[] titleOptions = { "Compare Two Databases", "Compare Database to Snapshot",
      "Take Database Snapshot" };
  private final String[] labelText = { "Enter SQLite Database Path:", "Enter SQLite Database:"};
  private Database devDatabase;
  private Database liveDatabase;
  private SQLiteConn devDatabaseConnection;
  private SQLiteConn liveDatabaseConnection;
  private HashMap<String, String> updateTables = new HashMap<>();
  private JLabel headT = new JLabel("Enter Database Information Below", JLabel.CENTER);
  private JButton databaseConnection1btn = new JButton("Compare");
  private JTextField liveDatabaseName = new JTextField(10);
  private JTextField devDatabaseName = new JTextField(10);
  private JTextField devPath = new JTextField(10);
  private JTextField livePath = new JTextField(10);
  private Component[] devDatabaseInputs = { devPath, devDatabaseName };
  private Component[] livevDatabaseInputs = { livePath, liveDatabaseName };
  private JPanel header = new JPanel(new BorderLayout());
  private JPanel content;
  private JPanel footer = new JPanel(new BorderLayout());
  private JPanel footc = new JPanel(new FlowLayout());

  /**
   * Initializes a DBCompare object with a title and text for the its button.
   * @author Peter Kaufman
   * @param type The type of JFrame to create.
   */
  public SQLiteCompare(int type) {
    this.type = type;
    initComponents();
    clase = this.getClass().getName();
  }

  /**
   * InitComonents sets up the GUI Layout, sets up all action events, and
   * initializes instance variables.
   * @author Peter Kaufman
   */
  private void initComponents() {
    // use parameters to set JFrame properties
    setTitle(titleOptions[this.type]);
    if (this.type == 2) {
      databaseConnection1btn.setText("Snapshot");
    }
    // create content pane
    if (this.type != 0) {
      content = new JPanel(new GridLayout(1, 2));
      setMinimumSize(new Dimension(120, 100));
    } else {
      content = new JPanel(new GridLayout(1, 2));
      headT.setText(headT.getText() + "(Dev, Live)");
      // add listeners
      addComponentListener(new ComponentListener() {
        public void componentResized(ComponentEvent e) {

          double width = e.getComponent().getWidth();
          Font title = new Font("Tahoma", Font.BOLD, 24);
          Font reg = new Font("Tahoma", Font.PLAIN, 11);
          Font button = new Font("Tahoma", Font.BOLD, 18);
          if (width >= 660) {
            title = new Font("Tahoma", Font.BOLD, (int) (width / 25));
            reg = new Font("Tahoma", Font.PLAIN, (int) (width / 56));
            button = new Font("Tahoma", Font.BOLD, (int) (width / 34));
          }

          for (Component cpn : cpnt) {
            cpn.setFont(title);
          }

          for (Component cpn : cpnr) {
            cpn.setFont(reg);
          }
          databaseConnection1btn.setFont(button);
          myFont = reg;
        }

        public void componentHidden(ComponentEvent e) {
        }

        public void componentShown(ComponentEvent e) {
        }

        public void componentMoved(ComponentEvent e) {
        }
      });
      setMinimumSize(new Dimension(630, 325));
    }
    // add components to the appropriate ArrayList
    cpnt.add(headT);
    cpnr.add(databaseConnection1btn);
    switch (type) {
    case 0:
      addComponents(devDatabaseInputs);
      addComponents(livevDatabaseInputs);
      break;
    case 1:
      addComponents(livevDatabaseInputs);
      break;
    case 2:
      addComponents(devDatabaseInputs);
      break;
    }
    // set component properties
    headT.setFont(new Font("Tahoma", 1, 14));
    databaseConnection1btn.setFont(new Font("Tahoma", 0, 18));
    // add listeners
    databaseConnection1btn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        databaseConnection1btnActionPerformed(evt);
      }
    });
    // add components
    getContentPane().setLayout(new BorderLayout());
    header.add(headT, BorderLayout.CENTER);
    footc.add(databaseConnection1btn);
    footer.add(footc, BorderLayout.CENTER);
    footer.add(pb, BorderLayout.SOUTH);
    add(header, BorderLayout.NORTH);
    add(content, BorderLayout.CENTER);
    add(footer, BorderLayout.SOUTH);
    pack();
  }

  /**
   * Determines if the user has put in the appropriate information and either
   * takes a database snapshot or compares a two databases (one can be a
   * snapshot).
   * @author Peter Kaufman
   * @param evt The event button that occurs when databaseConnection1btn is
   *            clicked.
   */
  private void databaseConnection1btnActionPerformed(ActionEvent evt) {
    if (allFieldsFilledOut()) {

      this.error = false;
      switch (type) {
      case 0:
      case 1:
        getSequelStatementsInBackground();
        break;
      case 2:
        takeSnapshot();
        break;
      }
    } else {

      headT.setText("Please do not leave any fields blank.");
    }
  }

  /**
   * Takes a devDatabase snapshot based on user input.
   * @author Peter Kaufman
   */
  private void takeSnapshot() {

    prepProgressBar("Establishing Database Connection", true);
    SwingWorker<Boolean, String> swingW = new SwingWorker<Boolean, String>() {

      @Override
      protected Boolean doInBackground() throws Exception {
        try {
          publish("Establishing Database Connection");
          sw.start();
          devDatabaseConnection = new SQLiteConn(new String(devPath.getText()), devDatabaseName.getText(), "dev");
          publish("Gathering Database Information");
          devDatabase = new Database(devDatabaseConnection);
          publish("Writing to JSON File");
          FileHandler.serializeDatabase(devDatabase);
          sw.stop();
          log("Took a DB Snapshot in " + sw.getElapsedTime().toMillis() / 1000.0 + "s with no errors.");
        } catch (SQLException e) {
          sw.stop();
          throw new DatabaseDiffernceCheckerException(
              "There was an error" + " with the devDatabase connection. Please try again.", e);
        }

        return true;
      }

      @Override
      protected void done() {
        try {
          get();
          endProgressBar("Database Snapshot Complete");
          error = true;
          close();
        } catch (Exception e) {
          endProgressBar("An Error Occurred");
          if (e instanceof DatabaseDiffernceCheckerException) {
            error((DatabaseDiffernceCheckerException) e);
          } else {
            error(new DatabaseDiffernceCheckerException(e.getMessage().substring(e.getMessage().indexOf(":") + 1), e));
          }
        }
      }

      @Override
      protected void process(List<String> chunks) {
        newBorder(chunks.get(chunks.size() - 1));
      }
    };

    swingW.execute();
  }

  /**
   * Compares two databases based on user input (one can be a snapshot).
   * @author Peter Kaufman
   */
  private void getSequelStatementsInBackground() {

    prepProgressBar("Establishing Database Connection(s) and Collecting Database Info", true);
    SwingWorker<Boolean, String> swingW = new SwingWorker<Boolean, String>() {

      @Override
      protected Boolean doInBackground() throws Exception {
        setupDatabases();
        publish("Comparing Databases");
        compareDatabases();
        return true;
      }

      @Override
      protected void done() {
        try {
          get();
          endProgressBar("Database Comparison Complete");
          displayResult(liveDatabaseConnection);
          close();
        } catch (Exception e) {
          sw.stop();
          endProgressBar("An Error Occurred");
          if (e instanceof DatabaseDiffernceCheckerException) {
            error((DatabaseDiffernceCheckerException) e);
          } else {
            error(new DatabaseDiffernceCheckerException(e.getMessage(), e));
          }
        }
      }

      @Override
      protected void process(List<String> chunks) {
        newBorder(chunks.get(chunks.size() - 1));
      }
    };

    swingW.execute();
  }

  /**
   * Takes in an array of components and adds them to a JFrame.
   * @author Peter Kaufman
   * @param components The array of components to add to the JFrame.
   */
  public void addComponents(Component[] components) {
    JLabel tempLabel = null;
    JPanel tempPanel = new JPanel(new GridLayout(labelText.length, 2));
    Component tempComponent = null;
    for (int i = 0; i < labelText.length; i++) {
      tempLabel = new JLabel(labelText[i]);
      tempComponent = components[i];
      cpnr.add(tempLabel);
      cpnr.add(tempComponent);
      tempPanel.add(new JPanel(new FlowLayout()).add(tempLabel));
      tempPanel.add(new JPanel(new FlowLayout()).add(tempComponent));
    }
    content.add(tempPanel);
  }

  /**
   * Gets two databases setup based on the type of the JFrame.
   * @author Peter Kaufman
   */
  private void setupDatabases() throws DatabaseDiffernceCheckerException {
    sw.start();
    try {
      if (this.type == 0) {
        devDatabaseConnection = new SQLiteConn(new String(devPath.getText()),
           devDatabaseName.getText(), "dev");
        devDatabase = new Database(devDatabaseConnection);
      } else {
        devDatabase = FileHandler.deserailizDatabase();
      }
      liveDatabaseConnection = new SQLiteConn(new String(livePath.getText()),
      liveDatabaseName.getText(), "live");
      liveDatabase = new Database(liveDatabaseConnection);
    } catch (Exception cause) {
      String errorMessage = "";
      if (cause instanceof DatabaseDiffernceCheckerException) {
        throw (DatabaseDiffernceCheckerException)cause;
      } else if(cause instanceof SQLException) {
        errorMessage = "There was an error with the database connection. Please try again.";
      } else {
        errorMessage = "There was an error reading in the database snapshot. Please try again.";
      }
      throw new DatabaseDiffernceCheckerException(errorMessage, cause);
    }
  }

  /**
   * Compares two databases and determines their differences and how to make them
   * the same.
   * @author Peter Kaufman
   */
  private void compareDatabases() throws DatabaseDiffernceCheckerException {
    sql.addAll(liveDatabase.getFirstSteps());
    sql.addAll(devDatabase.compareTables(liveDatabase.getTables()));
    updateTables.putAll(devDatabase.tablesDiffs(liveDatabase.getTables()));
    sql.addAll(devDatabase.updateTables(liveDatabase.getTables(), updateTables));
    sql.addAll(devDatabase.getFirstSteps());
    sql.addAll(devDatabase.updateViews(liveDatabase.getViews()));
    sw.stop();
    log("DB Comparison Complete on " /* + sw.getDate() + " at " + sw.getHour() */ + " in "
        + sw.getElapsedTime().toMillis() / 1000.0 + "s with no errors.");
  }

  /**
   * Whether or not the user has filled out all of the inputs that are needed to
   * run the program.
   * @author Peter Kaufman
   */
  private boolean allFieldsFilledOut() {
    boolean allFilledOut = false;
    switch (type) {
    case 0:
      allFilledOut = !(new String(devPath.getText()).equals("") || new String(livePath.getText()).equals("")
         || devDatabaseName.getText().equals("") || liveDatabaseName.getText().equals(""));
      break;
    case 1:
      allFilledOut = !(new String(livePath.getText()).equals("") || liveDatabaseName.getText().equals(""));
      break;
    case 2:
      allFilledOut = !(new String(devPath.getText()).equals("") || devDatabaseName.getText().equals(""));
      break;
    }

    return allFilledOut;
  }
}
