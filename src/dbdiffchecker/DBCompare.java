package dbdiffchecker;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
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
import javax.swing.text.JTextComponent;

import dbdiffchecker.DatabaseDiffernceCheckerException;
import dbdiffchecker.FileHandler;

/**
 * DBCompare is a JFrame that takes user input to make a comparison between two
 * databases or a take a database snapshot.
 * @author Peter Kaufman
 * @version 5-18-19
 * @since 5-14-19
 */
public abstract class DBCompare extends JFrameV2 {
  // Instance variables
  protected static final String[] titleOptions = { "Compare Two Databases", "Compare Database to Snapshot",
      "Take Database Snapshot" };
  protected int type = 0;
  protected String salt = "";
  protected String[] labelText;
  protected Database devDatabase;
  protected Database liveDatabase;
  protected DbConn devDatabaseConnection;
  protected DbConn liveDatabaseConnection;
  protected HashMap<String, String> updateTables = new HashMap<>();
  protected JTextField liveDatabaseName = new JTextField(10);
  protected JTextField devDatabaseName = new JTextField(10);
  protected JTextComponent[] devDatabaseInputs;
  protected JTextComponent[] livevDatabaseInputs;
  protected JLabel headT = new JLabel("Enter Database Information Below", JLabel.CENTER);
  private JButton databaseConnection1btn = new JButton("Compare");
  private JPanel header = new JPanel(new BorderLayout());
  private JPanel content;
  private JPanel footer = new JPanel(new BorderLayout());
  private JPanel footc = new JPanel();

  /**
   * Initializes a DBCompe object with a title and text for the its button.
   * @author Peter Kaufman
   * @param type The type of JFrame to create.
   */
  public DBCompare(int type) {
    this.type = type;
    clase = this.getClass().getName();
  }

   /**
   * Determines if the user has put in the appropriate information and either
   * takes a database snapshot or compares a two databases (one can be a
   * snapshot).
   * @author Peter Kaufman
   * @param evt The event button that occurs when databaseConnection1btn is
   * clicked.
   */
  protected abstract void databaseConnection1btnActionPerformed(ActionEvent evt);

  /**
   * Creates a DbConn object for the development database based on user input.
   * @return DbConn A connector to the development database.
   * @throws SQLException if there is an issue connecting to the development
   * database.
   */
  protected abstract DbConn createDevDatabaseConnection() throws SQLException;

  /**
   * Creates a DbConn object for the live database based on user input.
   * @return DbConn A connector to the live database.
   * @throws SQLException if there is an issue connecting to the live
   * database.
   */
  protected abstract DbConn createLiveDatabaseConnection() throws SQLException;

  /**
   * Whether or not the user has filled out all of the inputs that are needed to
   * run the program.
   * @author Peter Kaufman
   * @return Whether or not all fields have had something written to them.
   */
  protected boolean allFieldsFilledOut() {
    boolean allFilledOut = true;
    switch (type) {
    case 0:
      for (JTextComponent cpn: devDatabaseInputs) {
        allFilledOut = allFilledOut && !(new String(cpn.getText())).equals("");
      }
      for (JTextComponent cpn: livevDatabaseInputs) {
        allFilledOut = allFilledOut && !(new String(cpn.getText())).equals("");
      }
      break;
    case 1:
      for (JTextComponent cpn: livevDatabaseInputs) {
        allFilledOut = allFilledOut && !(new String(cpn.getText())).equals("");
      }
      break;
    case 2:
      for (JTextComponent cpn: devDatabaseInputs) {
        allFilledOut = allFilledOut && !(new String(cpn.getText())).equals("");
      }
      break;
    }

    return allFilledOut;
  }

  /**
   * InitComonents sets up the GUI Layout, sets up all action events, and
   * initializes instance variables.
   * @author Peter Kaufman
   */
  protected void initComponents() {
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
      setMinimumSize(new Dimension(630, 200));
    }
    // add components to the appropriate ArrayList
    cpnbtn.add(databaseConnection1btn);
    cpnt.add(headT);
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
      @Override
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
    if (this.type != 0) {
      setSize(350, 100 + 35 * labelText.length);
    } else {
      setSize(630, 100 + 45 * labelText.length);
    }
    setVisible(true);
  }

  /**
   * Takes a devDatabase snapshot based on user input.
   * @author Peter Kaufman
   */
  protected void takeSnapshot() {

    prepProgressBar("Establishing Database Connection", true);
    SwingWorker<Boolean, String> swingW = new SwingWorker<Boolean, String>() {

      @Override
      protected Boolean doInBackground() throws Exception {
        try {
          publish("Establishing Database Connection");
          sw.start();
          devDatabaseConnection = createDevDatabaseConnection();
          publish("Gathering Database Information");
          devDatabase = new Database(devDatabaseConnection);
          publish("Writing to JSON File");
          FileHandler.serializeDatabase(devDatabase, salt);
          sw.stop();
          log("Took a DB Snapshot on " /* + sw.getDate() + " at " + sw.getHour() */ + " in "
              + sw.getElapsedTime().toMillis() / 1000.0 + "s with no errors.");
        } catch (SQLException e) {
          sw.stop();
          throw new DatabaseDiffernceCheckerException(
              "There was an error" + " with the dev database connection. Please try again.", e);
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
  protected void getSequelStatementsInBackground() {

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
  private void addComponents(Component[] components) {
    JLabel tempLabel = null;
    JPanel tempPanel = new JPanel(new GridLayout(labelText.length, 2));
    Component tempComponent = null;
    for (int i = 0; i < labelText.length; i++) {
      tempLabel = new JLabel(labelText[i]);
      tempComponent = components[i];
      cpnr.add(tempLabel);
      cpnr.add(tempComponent);
      tempPanel.add(new JPanel().add(tempLabel));
      tempPanel.add(new JPanel().add(tempComponent));
    }
    content.add(tempPanel);
  }

  /**
   * Gets two databases setup based on the type of the JFrame.
   * @author Peter Kaufman
   * @throws DatabaseDiffernceCheckerException if there was an error connnecting
   * to a database.
   */
  private void setupDatabases() throws DatabaseDiffernceCheckerException {
    sw.start();
    try {
      if (this.type == 0) {
        devDatabaseConnection = createDevDatabaseConnection();
        devDatabase = new Database(devDatabaseConnection);
      } else {
        devDatabase = FileHandler.deserailizDatabase(salt);
      }
      liveDatabaseConnection = createLiveDatabaseConnection();
      liveDatabase = new Database(liveDatabaseConnection);
    } catch (Exception cause) {
      DatabaseDiffernceCheckerException error;
      String errorMessage = "";
      if (cause instanceof DatabaseDiffernceCheckerException) {
        error = (DatabaseDiffernceCheckerException)cause;
      } else {
        if (cause instanceof SQLException) {
        errorMessage = "There was an error with the database connection. Please try again.";
        } else {
          errorMessage = "There was an error reading in the database snapshot. Please try again.";
        }
        error = new DatabaseDiffernceCheckerException(errorMessage, cause);
      }
      throw error;
    }
  }

  /**
   * Compares two databases and determines their differences and how to make them
   * the same.
   * @author Peter Kaufman
   * @throws DatabaseDiffernceCheckerException if there was an getting the database
   * or comparing the database info.
   */
  private void compareDatabases() throws DatabaseDiffernceCheckerException {
    sql.addAll(devDatabase.compareTables(liveDatabase.getTables()));
    updateTables.putAll(devDatabase.tablesDiffs(liveDatabase.getTables(), liveDatabase));
    sql.addAll(0, liveDatabase.getFirstSteps());
    sql.addAll(devDatabase.updateTables(liveDatabase.getTables(), updateTables));
    sql.addAll(devDatabase.getFirstSteps());
    sql.addAll(devDatabase.updateViews(liveDatabase.getViews()));
    sw.stop();
    log(salt + " DB Comparison Complete in " + sw.getElapsedTime().toMillis() / 1000.0 + "s with no errors.");
  }
}
