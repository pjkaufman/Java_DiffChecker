package dbdiffchecker;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.text.JTextComponent;
import dbdiffchecker.sql.SQLDatabase;

/**
 * A JFrame that takes user input to make a comparison between two databases or
 * take a database snapshot.
 * @author Peter Kaufman
 * @version 1-6-20
 * @since 5-11-19
 */
public abstract class DBCompare extends JFrameV2 {
  // Instance variables
  protected static final String[] titleOptions = { "Compare Two Databases", "Compare Database to Snapshot",
      "Take Database Snapshot" };
  protected int type = 0;
  protected int implimentation;
  protected String salt = "";
  protected String[] labelText;
  protected Database devDatabase;
  protected Database liveDatabase;
  protected DbConn devDatabaseConnection;
  protected DbConn liveDatabaseConnection;
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
   * Sets the type of the compare and sets the class name (for sizeing purposes).
   * @author Peter Kaufman
   * @param type The type of compare that is occuring.
   */
  public DBCompare(int type) {
    this.type = type;
    clase = this.getClass().getName();
  }

  /**
   * Determines if the user has put in the appropriate information and either
   * takes a database snapshot or compares two databases (one can be a snapshot).
   * @author Peter Kaufman
   * @param evt The event button that occurs when databaseConnection1btn is
   *        clicked.
   */
  protected abstract void databaseConnection1btnActionPerformed(ActionEvent evt);

  /**
   * Creates a database connection for the development database based on user
   * input.
   * @return A database connection for the development database.
   * @throws DatabaseDifferenceCheckerException Error connecting to the development
   *         database.
   */
  protected abstract DbConn createDevDatabaseConnection() throws DatabaseDifferenceCheckerException;

  /**
   * Creates a database connection for the live database based on user input.
   * @return A database connection for the live database.
   * @throws DatabaseDifferenceCheckerException Error connecting to the live
   *         database.
   */
  protected abstract DbConn createLiveDatabaseConnection() throws DatabaseDifferenceCheckerException;

  /**
   * Creates a database for the database connection provided.
   * @param databaseConn The database connection to use to make the database.
   * @return A database for the development database
   * @throws DatabaseDifferenceCheckerException Error getting data from the
   *         development database.
   */
  protected Database createDatabase(DbConn databaseConn) throws DatabaseDifferenceCheckerException {
    return new SQLDatabase(databaseConn, this.implimentation);
  }

  /**
   * Whether or not the user has filled out all of the inputs that are needed to
   * run the desired operation (compare or snapshot).
   * @author Peter Kaufman
   * @return Whether or not all fields have had something written to them.
   */
  protected boolean allFieldsFilledOut() {
    boolean allFilledOut = true;
    switch (type) {
    case 0:
      for (JTextComponent cpn : devDatabaseInputs) {
        allFilledOut = allFilledOut && !(new String(cpn.getText())).equals("");
      }
      for (JTextComponent cpn : livevDatabaseInputs) {
        allFilledOut = allFilledOut && !(new String(cpn.getText())).equals("");
      }
      break;
    case 1:
      for (JTextComponent cpn : livevDatabaseInputs) {
        allFilledOut = allFilledOut && !(new String(cpn.getText())).equals("");
      }
      break;
    case 2:
      for (JTextComponent cpn : devDatabaseInputs) {
        allFilledOut = allFilledOut && !(new String(cpn.getText())).equals("");
      }
      break;
    }
    return allFilledOut;
  }

  @Override
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
        publish("Establishing Database Connection");
        sw.start();
        devDatabaseConnection = createDevDatabaseConnection();
        publish("Gathering Database Information");
        devDatabase = createDatabase(devDatabaseConnection);
        publish("Writing to JSON File");
        FileHandler.serializeDatabase(devDatabase, salt);
        sw.stop();
        log("Took a DB Snapshot in " + sw.getElapsedTime().toMillis() / 1000.0 + "s with no errors.");
        return true;
      }

      @Override
      protected void done() {
        try {
          get();
          endProgressBar("Database Snapshot Complete");
          error = true;
          close();
        } catch (InterruptedException | ExecutionException e) {
          endProgressBar("An Error Occurred");
          if (e.getCause() instanceof DatabaseDifferenceCheckerException) {
            error((DatabaseDifferenceCheckerException) e.getCause());
          } else {
            error(new DatabaseDifferenceCheckerException(e.getMessage().substring(e.getMessage().indexOf(":") + 1), e, 1005));
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
      protected Boolean doInBackground() throws DatabaseDifferenceCheckerException {
        setupDatabases();
        publish("Comparing Databases");
        sql = devDatabase.compare(liveDatabase);
        return true;
      }

      @Override
      protected void done() {
        try {
          get();
          endProgressBar("Database Comparison Complete");
          displayResult(liveDatabaseConnection);
          close();
        } catch (InterruptedException | ExecutionException e) {
          sw.stop();
          endProgressBar("An Error Occurred");
          if (e.getCause() instanceof DatabaseDifferenceCheckerException) {
            error((DatabaseDifferenceCheckerException) e.getCause());
          } else {
            error(new DatabaseDifferenceCheckerException(e.getMessage(), e, 1006));
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
   * @throws DatabaseDifferenceCheckerException if there was an error connnecting
   *         to a database.
   */
  private void setupDatabases() throws DatabaseDifferenceCheckerException {
    sw.start();
    if (this.type == 0) {
      devDatabaseConnection = createDevDatabaseConnection();
      devDatabase = createDatabase(devDatabaseConnection);
    } else {
      devDatabase = FileHandler.deserailizDatabase(salt);
    }
    liveDatabaseConnection = createLiveDatabaseConnection();
    liveDatabase = createDatabase(liveDatabaseConnection);
  }
}
