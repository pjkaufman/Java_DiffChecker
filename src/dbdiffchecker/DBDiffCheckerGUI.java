package dbdiffchecker;

import dbdiffchecker.sql.SQLDatabase;
import dbdiffchecker.sql.MySQLConn;
import dbdiffchecker.sql.SQLiteConn;
import dbdiffchecker.nosql.Bucket;
import dbdiffchecker.nosql.CouchbaseConn;
import dbdiffchecker.nosql.MongoConn;
import dbdiffchecker.nosql.MongoDB;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.text.JTextComponent;
import javax.swing.JProgressBar;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.BorderFactory;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.SwingWorker;
import javax.swing.border.TitledBorder;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.lang.InterruptedException;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

/**
 * A JFrame that has several tabs and includes the entire frontend.
 *
 * @author Peter Kaufman
 * @version 7-2-20
 * @since 9-20-17
 */
public class DBDiffCheckerGUI extends JFrame {
  DatabaseType selectedType;
  PaneType[] paneTypeOptions = PaneType.values();
  PaneType tabType;
  TabPane selectedTab;
  private ArrayList<JPanel> inputForms = new ArrayList<JPanel>(paneTypeOptions.length - 2);
  private HashMap<String, ArrayList<String>> statementsLists = new HashMap<String, ArrayList<String>>(
      paneTypeOptions.length - 3);
  private HashMap<String, DbConn> liveConnectionLists = new HashMap<String, DbConn>(paneTypeOptions.length - 3);
  private JTabbedPane jtp = new JTabbedPane();
  private Database devDatabase, liveDatabase;
  private DbConn devDatabaseConnection, liveDatabaseConnection;
  private TitledBorder nBorder = null;
  private StopWatch sw = new StopWatch();
  private String currentTab = PaneType.getTabText(0);
  private ArrayList<String> statements;
  private ArrayList<Component> cpnt = new ArrayList<>(), cpnBtn = new ArrayList<>(), cpnr = new ArrayList<>();
  private Font reg = new Font("Tahoma", Font.PLAIN, 12), tabFont = new Font("Tahoma", Font.PLAIN, 16);
  private int tabPos = 0;
  private JButton runBtn;
  private JTextArea dataShow;
  private JLabel errorMsg;
  private JComboBox<String> databaseOptions;
  private ArrayList<JTextComponent> inputs;
  private JProgressBar progressBar;

  /**
   * Initializes a JFrame which will be used by the user to navigate through the
   * application.
   */
  public DBDiffCheckerGUI() {
    PaneType tabPaneType;
    TabPane tabPane;
    for (int i = 0; i < paneTypeOptions.length; i++) {
      tabPaneType = paneTypeOptions[i];
      tabPane = new TabPane(tabPaneType);
      if (i < 3) {
        selectedTab = tabPane;
        updateTabVariables();
        addActionListeners(tabPane, tabPaneType);
      }
      tabPane.addComponentsToResizeList(cpnr, cpnBtn, cpnt);
      jtp.addTab(PaneType.getTabText(tabPaneType.getValue()), tabPane);
    }
    jtp.setFont(tabFont);
    jtp.setTitleAt(0, "<html><b>" + currentTab + "</b></html>");
    selectedTab = (TabPane) jtp.getComponentAt(0);
    updateTabVariables();
    // add listener for tab changes
    jtp.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        jtp.setTitleAt(tabPos, currentTab);
        tabPos = jtp.getSelectedIndex();
        currentTab = PaneType.getTabText(tabPos);
        jtp.setTitleAt(tabPos, "<html><b>" + currentTab + "</b></html>");
        selectedTab = (TabPane) jtp.getSelectedComponent();
        updateTabVariables();
        if (tabType == PaneType.LOGS) {
          if (FileHandler.fileExists(FileHandler.logFileName)) {
            displayLog(FileHandler.logFileName);
          } else {
            dataShow.setText("There are no logs to display.");
          }
        } else if (tabType == PaneType.LAST_RUN) {
          if (FileHandler.fileExists(FileHandler.lastSequelStatementFileName)) {
            displayLog(FileHandler.lastSequelStatementFileName);
          } else {
            dataShow.setText("The application has no record of any statements run before.");
          }
        }
        // copy over statements from a previous run on the tab and the previous live
        // connection
        if (liveConnectionLists.containsKey(currentTab)) {
          liveDatabaseConnection = liveConnectionLists.get(currentTab);
        }
        if (statementsLists.containsKey(currentTab)) {
          statements = statementsLists.get(currentTab);
        }
      }
    });
    addComponentListener(new ComponentListener() {
      @Override
      public void componentResized(ComponentEvent e) {
        resizeWindowComponents(e.getComponent().getWidth());
      }

      @Override
      public void componentHidden(ComponentEvent e) {
      }

      @Override
      public void componentShown(ComponentEvent e) {
      }

      @Override
      public void componentMoved(ComponentEvent e) {
      }
    });
    getContentPane().add(jtp);
    setTitle("Databse Difference Checker");
    setMinimumSize(new Dimension(700, 300));
    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    setIconImage(new ImageIcon(getClass().getResource("/resources/DBCompare.png")).getImage());
    setSize(700, 600);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setVisible(true);
  }

  private void updateTabVariables() {
    runBtn = selectedTab.getRunBtn();
    tabType = selectedTab.getType();
    dataShow = selectedTab.getDataShow();
    databaseOptions = selectedTab.getDatabaseOptions();
    errorMsg = selectedTab.getErrorMessage();
    progressBar = selectedTab.getProgressBar();
    inputs = selectedTab.getUserInputs();
    inputForms = selectedTab.getInputForms();
  }

  private void addActionListeners(TabPane tabPane, PaneType type) {
    JButton runBtn = tabPane.getRunBtn(), execBtn = tabPane.getExecuteBtn();
    switch (type) {
      case COMPARE_WITH_DB:
      case COMPARE_WITH_SNAPSHOT:
        runBtn.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent evt) {
            executeStatements();
          }
        });
        execBtn.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent evt) {
            errorMsg.setVisible(false);
            generateStatements();
          }
        });
        break;
      case SNAPSHOT:
        execBtn.addActionListener(new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent evt) {
            errorMsg.setVisible(false);
            createSnapshot();
          }
        });
        break;
      default:
        break;
    }
    databaseOptions.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent event) {
        updateInfo();
      }
    });
  }

  private void updateInfo() {
    newBorder("");
    selectedTab.updateComponents();
    selectedType = selectedTab.getSelectedDatabase();
    inputs = selectedTab.getUserInputs();
  }

  /**
   * Takes a snapshot of what the user indicates is the development database.
   * <i>Note: most of this function is run in a background thread.</i>
   */
  private void createSnapshot() {
    databaseOptions.setEnabled(false);
    prepProgressBar("Establishing Database Connection", true);
    SwingWorker<Boolean, String> worker = new SwingWorker<Boolean, String>() {
      @Override
      protected Boolean doInBackground() throws Exception {
        publish("Establishing Database Connection");
        sw.start();
        devDatabaseConnection = createDevDatabaseConnection();
        publish("Gathering Database Information");
        devDatabase = createDatabase(devDatabaseConnection);
        publish("Writing to JSON File");
        FileHandler.serializeDatabase(devDatabase, DatabaseType.getType(selectedTab.getSelectedDatabase().getValue()));
        sw.stop();
        log("Took a DB Snapshot in " + sw.getElapsedTime().toMillis() / 1000.0 + "s with no errors.");
        return true;
      }

      @Override
      protected void done() {
        try {
          get();
          endProgressBar("Database Snapshot Complete");
        } catch (InterruptedException | ExecutionException e) {
          endProgressBar("An Error Occurred");
          if (e.getCause() instanceof DatabaseDifferenceCheckerException) {
            error((DatabaseDifferenceCheckerException) e.getCause());
          } else {
            error(new DatabaseDifferenceCheckerException(e.getMessage().substring(e.getMessage().indexOf(":") + 1), e,
                1005));
          }
        } finally {
          databaseOptions.setEnabled(true);
        }
      }

      @Override
      protected void process(List<String> chunks) {
        newBorder(chunks.get(chunks.size() - 1));
      }
    };
    worker.execute();
  }

  /**
   * Compares two databases based on user input (one can be a snapshot) and
   * generates the statements needed to make them the same. <i>Note: most of this
   * function runs in a background thread.</i>
   *
   */
  private void generateStatements() {
    databaseOptions.setEnabled(false);
    runBtn.setEnabled(false);
    prepProgressBar("Establishing Database Connection(s) and Collecting Database Info", true);
    SwingWorker<Boolean, String> swingW = new SwingWorker<Boolean, String>() {
      @Override
      protected Boolean doInBackground() throws DatabaseDifferenceCheckerException {
        setupDatabases();
        publish("Comparing Databases");
        statements = devDatabase.compare(liveDatabase);
        statementsLists.put(currentTab, statements);
        return true;
      }

      @Override
      protected void done() {
        try {
          get();
          endProgressBar("Database Comparison Complete");
          displayCompareResult();
        } catch (InterruptedException | ExecutionException e) {
          sw.stop();
          endProgressBar("An Error Occurred");
          if (e.getCause() instanceof DatabaseDifferenceCheckerException) {
            error((DatabaseDifferenceCheckerException) e.getCause());
          } else {
            error(new DatabaseDifferenceCheckerException(e.getMessage(), e, 1006));
          }
        } finally {
          databaseOptions.setEnabled(true);
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
   * Gets the progressBar ready by reseting the StopWatch object and determines
   * which settings to turn on.
   *
   * @param title         The title for the border of the progressBar.
   * @param indeterminate Whether or not the progressBar is to be indeterminate.
   */
  private void prepProgressBar(String title, boolean indeterminate) {
    newBorder(title);
    progressBar.setIndeterminate(indeterminate);
    if (!indeterminate) {
      progressBar.setStringPainted(true);
    } else {
      progressBar.setString(null);
      progressBar.setStringPainted(false);
    }
    progressBar.setValue(0);
    progressBar.setEnabled(true);
    sw.reset();
  }

  /**
   * Stops the progressBar and sets the border to the given String.
   *
   * @param title The title for the border of the progressBar
   */
  private void endProgressBar(String title) {
    newBorder(title);
    if (progressBar.isIndeterminate()) {
      progressBar.setIndeterminate(false);
    } else {
      progressBar.setValue(100);
    }
  }

  /**
   * Takes and sets the new title for the progressbar's border.
   *
   * @param title The new name of the titled borders.
   */
  private void newBorder(String title) {
    nBorder = BorderFactory.createTitledBorder(title);
    nBorder.setTitleFont(reg);
    progressBar.setBorder(nBorder);
  }

  /**
   * Creates a database for the database connection provided.
   *
   * @param databaseConn The database connection to use to make the database.
   * @return A database for the development database
   * @throws DatabaseDifferenceCheckerException Error getting data from the
   *                                            development database.
   */
  private Database createDatabase(DbConn databaseConn) throws DatabaseDifferenceCheckerException {
    if (DatabaseType.MYSQL == selectedType) {
      return new SQLDatabase(databaseConn, 0);
    } else if (DatabaseType.SQLITE == selectedType) {
      return new SQLDatabase(databaseConn, 1);
    } else if (DatabaseType.COUCHBASE == selectedType) {
      return new Bucket(databaseConn);
    } else {
      return new MongoDB(databaseConn);
    }
  }

  /**
   * Creates a database connection for the development database based on user
   * input.
   *
   * @return A database connection for the development database.
   * @throws DatabaseDifferenceCheckerException Error connecting to the
   *                                            development database.
   */
  private DbConn createDevDatabaseConnection() throws DatabaseDifferenceCheckerException {
    String type = "dev";
    if (DatabaseType.MYSQL == selectedType) {
      return new MySQLConn(inputs.get(0).getText().trim(), inputs.get(1).getText().trim(),
          inputs.get(2).getText().trim(), inputs.get(3).getText().trim(), inputs.get(4).getText().trim(), type);
    } else if (DatabaseType.SQLITE == selectedType) {
      return new SQLiteConn(fixPath(inputs.get(0).getText().trim()), inputs.get(1).getText().trim(), type);
    } else if (DatabaseType.COUCHBASE == selectedType) {
      return new CouchbaseConn(inputs.get(0).getText().trim(), inputs.get(1).getText().trim(),
          inputs.get(2).getText().trim(), inputs.get(3).getText().trim());
    } else {
      return new MongoConn(inputs.get(0).getText().trim(), inputs.get(1).getText().trim(),
          inputs.get(2).getText().trim(), inputs.get(3).getText().trim(), inputs.get(4).getText().trim());
    }
  }

  /**
   * Creates a database connection for the live database based on user input.
   *
   * @return A database connection for the live database.
   * @throws DatabaseDifferenceCheckerException Error connecting to the live
   *                                            database.
   */
  private DbConn createLiveDatabaseConnection() throws DatabaseDifferenceCheckerException {
    String type = "live";
    int startIndex = (inputs.size() / 2);
    if (PaneType.COMPARE_WITH_SNAPSHOT == tabType) {
      startIndex = 0;
    }
    if (DatabaseType.MYSQL == selectedType) {
      return new MySQLConn(inputs.get(startIndex).getText().trim(), inputs.get(startIndex + 1).getText().trim(),
          inputs.get(startIndex + 2).getText().trim(), inputs.get(startIndex + 3).getText().trim(),
          inputs.get(startIndex + 4).getText().trim(), type);
    } else if (DatabaseType.SQLITE == selectedType) {
      return new SQLiteConn(fixPath(inputs.get(startIndex).getText().trim()),
          inputs.get(startIndex + 1).getText().trim(), type);
    } else if (DatabaseType.COUCHBASE == selectedType) {
      return new CouchbaseConn(inputs.get(startIndex).getText().trim(), inputs.get(startIndex + 1).getText().trim(),
          inputs.get(startIndex + 2).getText().trim(), inputs.get(startIndex + 3).getText().trim());
    } else {
      return new MongoConn(inputs.get(startIndex).getText().trim(), inputs.get(startIndex + 1).getText().trim(),
          inputs.get(startIndex + 2).getText().trim(), inputs.get(startIndex + 3).getText().trim(),
          inputs.get(startIndex + 4).getText().trim());
    }
  }

  /**
   * Displays the error message to the user on the current tab and logs the error.
   * If an error occurs while logging the error, the error is not logged.
   *
   * @param error The exception which contains a user friendly message and the
   *              error that is the cause.
   */
  private void error(DatabaseDifferenceCheckerException error) {
    StringWriter strw = new StringWriter();
    error.printStackTrace(new PrintWriter(strw));
    String exceptionAsString = strw.toString();
    try {
      log(exceptionAsString);
    } catch (DatabaseDifferenceCheckerException err) {
      System.out.println("Could not log the error...");
    }
    errorMsg.setVisible(true);
    errorMsg.setText(error.toString());
  }

  /**
   * Takes in data and writes it to the log file.
   *
   * @param info The data to be logged.
   * @throws DatabaseDifferenceCheckerException Error logging data.
   */
  private void log(String info) throws DatabaseDifferenceCheckerException {
    FileHandler.writeToFile(info);
  }

  /**
   * Reads in data from a log file and displays it to the user by adding it to the
   * current tab's JTextArea.
   *
   * @param file The file to have its contents displayed.
   */
  private void displayLog(String file) {
    try {
      ArrayList<String> statementList = FileHandler.readFrom(file);
      if (statementList.isEmpty()) {
        if (tabType == PaneType.LOGS) {
          dataShow.setText("There are no logs to display.");
        } else {
          dataShow.setText("The application has no record of any statements run before.");
        }
      } else {
        dataShow.setText(null);
        this.statements = statementList;
        for (String statement : statementList) {
          dataShow.append(statement + "\n");
        }
      }
    } catch (DatabaseDifferenceCheckerException cause) {
      error(cause);
      try {
        log("There was an error finding " + file + ".");
      } catch (DatabaseDifferenceCheckerException logError) {
        System.out.println("unable to log error... " + logError.getMessage());
      }
    }
  }

  /**
   * Displays the generated statements or displays that the databases are in sync
   * in the tab's JTextArea based upon the amount of statements generated.
   */
  private void displayCompareResult() {
    try {
      if (statements.isEmpty()) {
        dataShow.setText("The databases are in sync.");
        runBtn.setEnabled(false);
      } else {
        dataShow.setText(null);
        runBtn.setEnabled(true);
        for (String statement : statements) {
          dataShow.append(statement + "\n");
        }
        FileHandler.writeToFile(statements);
      }
    } catch (DatabaseDifferenceCheckerException cause) {
      error(cause);
    }
  }

  /**
   * Gets the two databases ready for the database comparison based on the current
   * tab that is active.
   *
   * @throws DatabaseDifferenceCheckerException if there was an error connnecting
   *                                            to a database.
   */
  private void setupDatabases() throws DatabaseDifferenceCheckerException {
    sw.start();
    if (tabType == PaneType.COMPARE_WITH_DB) {
      devDatabaseConnection = createDevDatabaseConnection();
      devDatabase = createDatabase(devDatabaseConnection);
    } else {
      devDatabase = FileHandler.deserailizDatabase(DatabaseType.getType(selectedTab.getSelectedDatabase().getValue()));
    }
    liveDatabaseConnection = createLiveDatabaseConnection();
    liveConnectionLists.put(currentTab, liveDatabaseConnection);
    liveDatabase = createDatabase(liveDatabaseConnection);
  }

  /**
   * Takes the statements that were generated by the database comparison and runs
   * them on the live database. <i>Note: most of this function is run in a
   * background thread.</i>
   */
  private void executeStatements() {
    runBtn.setEnabled(false);
    databaseOptions.setEnabled(false);
    prepProgressBar("Waiting On Action", false);
    SwingWorker<Boolean, Integer> swingW = new SwingWorker<Boolean, Integer>() {
      @Override
      protected Boolean doInBackground() throws Exception {
        String temp = null;
        sw.start();
        liveDatabaseConnection.establishDatabaseConnection();
        for (int i = 0; i < statements.size(); i++) {
          temp = statements.get(i);
          liveDatabaseConnection.runStatement(temp);
          publish(i);
        }
        liveDatabaseConnection.closeDatabaseConnection();
        sw.stop();
        log("Ran SQL in " + sw.getElapsedTime().toMillis() / 1000.0 + "s with no errors.");
        return true;
      }

      @Override
      protected void done() {
        try {
          get();
          endProgressBar("Sucessfully updated the database");
        } catch (InterruptedException | ExecutionException e) {
          endProgressBar("An Error Occurred");
          if (e.getCause() instanceof DatabaseDifferenceCheckerException) {
            error((DatabaseDifferenceCheckerException) e.getCause());
          } else {
            error(new DatabaseDifferenceCheckerException(e.getMessage().substring(e.getMessage().indexOf(":") + 1), e,
                1008));
          }
        } finally {
          databaseOptions.setEnabled(true);
        }
      }

      @Override
      protected void process(List<Integer> chunks) {
        runBtn.setEnabled(false);
        newBorder("Running SQL.. ");
        progressBar.setValue((int) ((chunks.get(chunks.size() - 1) + 1.0) * 100 / statements.size()));
        progressBar.setString(progressBar.getPercentComplete() * 100 + "%");
      }
    };
    swingW.execute();
  }

  /**
   * Takes in a path and makes sure that it ends with a file separator.
   *
   * @param path The path to assure has a file separator at the end.
   * @return The original path ending with a file separator if it did not already
   *         have one.
   */
  private String fixPath(String path) {
    if (!path.endsWith(File.separator)) {
      path += File.separator;
    }
    return path;
  }

  private void resizeWindowComponents(double width) {
    // determine font sizes based on width of the GUI
    Font title = new Font("Tahoma", Font.BOLD, (int) (width / 38));
    reg = new Font("Tahoma", Font.PLAIN, (int) (width / 58));
    Font button = new Font("Tahoma", Font.BOLD, (int) (width / 53));
    // input body components
    for (JPanel inputForm : inputForms) {
      ((TitledBorder) inputForm.getBorder()).setTitleFont(reg.deriveFont(Font.BOLD));
      for (Component cpn : inputForm.getComponents()) {
        cpn.setFont(reg);
      }
    }
    for (Component cpn : cpnr) {
      cpn.setFont(reg);
    }
    // title components
    for (Component cpn : cpnt) {
      cpn.setFont(title);
    }
    // button components
    for (Component cpn : cpnBtn) {
      cpn.setFont(button);
    }
  }

  /**
   * Initializes UI.
   *
   * @param args Parameters from the user. <b>Note it is not used</b>
   */
  public static void main(String[] args) {
    try {
      // Set System L&F
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException
        | IllegalAccessException e) {
      System.out.println("Unable to get the system's look and feel...");
    }
    new DBDiffCheckerGUI();
  }
}