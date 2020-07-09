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
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JComponent;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JScrollPane;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import javax.swing.SwingUtilities;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.Color;

/**
 * A JFrame that has several tabs and includes the entire frontend.
 *
 * @author Peter Kaufman
 * @version 7-6-20
 * @since 9-20-17
 */
public class DBDiffCheckerGUI extends JFrame {
  private static final String FONT_FAMILY = "Tahoma";
  private DatabaseType[] databaseTypeOptions = DatabaseType.values();
  private DatabaseType selectedDBType = databaseTypeOptions[0];
  private PaneType[] paneTypeOptions = PaneType.values();
  private PaneType tabType;
  private Map<String, List<String>> statementsLists = new HashMap<>(paneTypeOptions.length - 3);
  private transient HashMap<String, DbConn> liveConnectionLists = new HashMap<>(paneTypeOptions.length - 3);
  private JTabbedPane jtp = new JTabbedPane();
  private Database devDatabase;
  private Database liveDatabase;
  private transient DbConn devDatabaseConnection;
  private transient DbConn liveDatabaseConnection;
  private transient StopWatch sw = new StopWatch();
  private String currentTab = PaneType.getTabText(0);
  private List<String> statements;
  private List<Component> cpnt = new ArrayList<>();
  private List<Component> cpnBtn = new ArrayList<>();
  private List<Component> cpnr = new ArrayList<>();
  private List<JButton> runBtnList = new ArrayList<>(paneTypeOptions.length - 2);
  private List<JButton> execBtnList = new ArrayList<>(paneTypeOptions.length - 2);
  private List<JLabel> errorMsgList = new ArrayList<>(paneTypeOptions.length);
  private List<JPanel> inputForms = new ArrayList<>(paneTypeOptions.length - 2);
  private List<JComboBox<String>> databaseOptionList = new ArrayList<>(paneTypeOptions.length - 2);
  private List<JProgressBar> progressBarList = new ArrayList<>(paneTypeOptions.length);
  private List<JTextArea> dataDisplayList = new ArrayList<>(paneTypeOptions.length);
  private List<List<JTextComponent>> inputsList = new ArrayList<>(paneTypeOptions.length - 2);
  private List<DatabaseType> selectedDBTypeList = new ArrayList<>(paneTypeOptions.length - 3);
  private List<List<JPanel>> inputFormsList = new ArrayList<>(paneTypeOptions.length - 2);
  private Font reg = new Font(FONT_FAMILY, Font.PLAIN, 12);
  private Font tabFont = new Font(FONT_FAMILY, Font.PLAIN, 16);
  private int tabPos = 0;
  private JButton runButton;
  private JButton executeButton;
  private JTextArea dataDisplay;
  private JLabel errorMsg;
  private JComboBox<String> databaseOptions;
  private List<JTextComponent> inputs;
  private JProgressBar progressBar;

  /**
   * Initializes a JFrame which will be used by the user to navigate through the
   * application.
   */
  public DBDiffCheckerGUI() {
    PaneType tabPaneType;
    JPanel tabPane;
    for (int i = 0; i < paneTypeOptions.length; i++) {
      tabPos = i;
      tabPaneType = paneTypeOptions[i];
      tabPane = createNewTab(tabPaneType);
      jtp.addTab(PaneType.getTabText(tabPaneType.getValue()), tabPane);
      inputsList.add(new ArrayList<>());
    }
    tabPos = 0;
    jtp.setFont(tabFont);
    jtp.setTitleAt(0, "<html><b>" + currentTab + "</b></html>");
    updateTabVariables();
    // add listener for tab changes
    jtp.addChangeListener((ChangeEvent evt) -> {
      jtp.setTitleAt(tabPos, currentTab);
      tabPos = jtp.getSelectedIndex();
      currentTab = PaneType.getTabText(tabPos);
      jtp.setTitleAt(tabPos, "<html><b>" + currentTab + "</b></html>");
      updateTabVariables();
      if (tabType == PaneType.LOGS) {
        if (FileHandler.fileExists(FileHandler.LOG_FILE)) {
          displayLog(FileHandler.LOG_FILE);
        } else {
          dataDisplay.setText("There are no logs to display.");
        }
      } else if (tabType == PaneType.LAST_RUN) {
        if (FileHandler.fileExists(FileHandler.LAST_RUN_FILE)) {
          displayLog(FileHandler.LAST_RUN_FILE);
        } else {
          dataDisplay.setText("The application has no record of any statements run before.");
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

  /**
   * Updates the variables that are tab specific.
   */
  private void updateTabVariables() {
    if (tabPos < 3) {
      runButton = runBtnList.get(tabPos);
      databaseOptions = databaseOptionList.get(tabPos);
      progressBar = progressBarList.get(tabPos);
      inputForms = inputFormsList.get(tabPos);
      selectedDBType = selectedDBTypeList.get(tabPos);
      inputs = inputsList.get(tabPos);
      System.out.println(inputs);
    }

    tabType = paneTypeOptions[tabPos];
    dataDisplay = dataDisplayList.get(tabPos);
    errorMsg = errorMsgList.get(tabPos);
  }

  /**
   * Adds the action listeners used on the combobox and other user input
   * components.
   *
   * @param tabPane The tab which needs the action listeners added.
   * @param type    The type of pane that has is to have its listeners added.
   */
  private void addPaneActionListeners(JButton runBtn, JButton execBtn, PaneType type) {
    switch (type) {
      case COMPARE_WITH_DB:
      case COMPARE_WITH_SNAPSHOT:
        runBtn.addActionListener((ActionEvent evt) -> executeStatements());
        execBtn.addActionListener((ActionEvent evt) -> {
          errorMsg.setVisible(false);
          generateStatements();
        });
        break;
      case SNAPSHOT:
        execBtn.addActionListener((ActionEvent evt) -> {
          errorMsg.setVisible(false);
          createSnapshot();
        });
        break;
      default:
        break;
    }
    databaseOptions.addActionListener((ActionEvent evt) -> {
      newBorder("");
      updateComponents();
      selectedDBType = databaseTypeOptions[databaseOptions.getSelectedIndex()];
      inputs = inputsList.get(tabPos);
    });
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
        FileHandler.serializeDatabase(devDatabase, DatabaseType.getType(selectedDBType.getValue()));
        sw.stop();
        log(String.format("Took a DB Snapshot in %fs with no errors.", sw.getElapsedTime().toMillis() / 1000.0));
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
    runButton.setEnabled(false);
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
    TitledBorder nBorder = BorderFactory.createTitledBorder(title);
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
    if (DatabaseType.MYSQL == selectedDBType) {
      return new SQLDatabase(databaseConn, 0);
    } else if (DatabaseType.SQLITE == selectedDBType) {
      return new SQLDatabase(databaseConn, 1);
    } else if (DatabaseType.COUCHBASE == selectedDBType) {
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
    boolean isLive = false;
    if (DatabaseType.MYSQL == selectedDBType) {
      return new MySQLConn(inputs.get(0).getText().trim(), inputs.get(1).getText().trim(),
          inputs.get(2).getText().trim(), inputs.get(3).getText().trim(), inputs.get(4).getText().trim(), isLive);
    } else if (DatabaseType.SQLITE == selectedDBType) {
      return new SQLiteConn(fixPath(inputs.get(0).getText().trim()), inputs.get(1).getText().trim(), isLive);
    } else if (DatabaseType.COUCHBASE == selectedDBType) {
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
    boolean isLive = true;
    int startIndex = (inputs.size() / 2);
    if (PaneType.COMPARE_WITH_SNAPSHOT == tabType) {
      startIndex = 0;
    }
    if (DatabaseType.MYSQL == selectedDBType) {
      return new MySQLConn(inputs.get(startIndex).getText().trim(), inputs.get(startIndex + 1).getText().trim(),
          inputs.get(startIndex + 2).getText().trim(), inputs.get(startIndex + 3).getText().trim(),
          inputs.get(startIndex + 4).getText().trim(), isLive);
    } else if (DatabaseType.SQLITE == selectedDBType) {
      return new SQLiteConn(fixPath(inputs.get(startIndex).getText().trim()),
          inputs.get(startIndex + 1).getText().trim(), isLive);
    } else if (DatabaseType.COUCHBASE == selectedDBType) {
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
      List<String> statementList = FileHandler.readFrom(file);
      if (statementList.isEmpty()) {
        if (tabType == PaneType.LOGS) {
          dataDisplay.setText("There are no logs to display.");
        } else {
          dataDisplay.setText("The application has no record of any statements run before.");
        }
      } else {
        dataDisplay.setText(null);
        this.statements = statementList;
        for (String statement : statementList) {
          dataDisplay.append(statement + "\n");
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
        dataDisplay.setText("The databases are in sync.");
        runButton.setEnabled(false);
      } else {
        dataDisplay.setText(null);
        runButton.setEnabled(true);
        for (String statement : statements) {
          dataDisplay.append(statement + "\n");
        }
        FileHandler.writeToFile(statements);
      }
    } catch (DatabaseDifferenceCheckerException cause) {
      error(cause);
    }
  }

  /**
   * Creates an input listener which is used to validate the data that is input
   * and determine whether it is time to allow the user to submit the entered
   * data.
   *
   * @param input The component that will have the listener added to it.
   * @param type  The description of the input (i.e. Username, Password, etc.).
   *              This helps determine which listener to apply.
   */
  private void createInputListener(JComponent input, String type) {
    if (type.equals("Password")) {
      JPasswordField cpn = (JPasswordField) input;
      cpn.getDocument().addDocumentListener(new DocumentListener() {

        @Override
        public void insertUpdate(DocumentEvent e) {
          disableRunningStatements();
          validateInput();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
          disableRunningStatements();
          validateInput();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
          disableRunningStatements();
          validateInput();
        }
      });
    } else {
      JTextField cpn = (JTextField) input;
      if (type.equals("Port")) {
        cpn.getDocument().addDocumentListener(new DocumentListener() {
          @Override
          public void insertUpdate(DocumentEvent e) {
            Runnable format = new Runnable() {
              @Override
              public void run() {
                String text = cpn.getText();
                String regex = "\\d+";
                if (!text.matches(regex) && text.length() > 0) {
                  cpn.setText(text.substring(0, text.length() - 1));
                }
              }
            };
            SwingUtilities.invokeLater(format);
            disableRunningStatements();
            validateInput();
          }

          @Override
          public void removeUpdate(DocumentEvent e) {
            disableRunningStatements();
            validateInput();
          }

          @Override
          public void changedUpdate(DocumentEvent e) {
            disableRunningStatements();
            validateInput();
          }
        });
      } else {
        cpn.getDocument().addDocumentListener(new DocumentListener() {
          @Override
          public void insertUpdate(DocumentEvent e) {
            disableRunningStatements();
            validateInput();
          }

          @Override
          public void removeUpdate(DocumentEvent e) {
            disableRunningStatements();
            validateInput();
          }

          @Override
          public void changedUpdate(DocumentEvent e) {
            disableRunningStatements();
            validateInput();
          }
        });
      }
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
      devDatabase = FileHandler.deserailizDatabase(DatabaseType.getType(selectedDBType.getValue()));
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
    runButton.setEnabled(false);
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
        log(String.format("Ran SQL in %fs with no errors.", sw.getElapsedTime().toMillis() / 1000.0));
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
        runButton.setEnabled(false);
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

  /**
   * Resize components to compensate for the change in the GUI width.
   */
  private void resizeWindowComponents(double width) {
    Font title = new Font(FONT_FAMILY, Font.BOLD, (int) (width / 38));
    reg = new Font(FONT_FAMILY, Font.PLAIN, (int) (width / 58));
    Font button = new Font(FONT_FAMILY, Font.BOLD, (int) (width / 53));
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
    for (Component cpn : cpnt) {
      cpn.setFont(title);
    }
    for (Component cpn : cpnBtn) {
      cpn.setFont(button);
    }
  }

  /**
   * Initializes the new tab.
   *
   * @param paneType The pane type that the tab represents.
   */
  private JPanel createNewTab(PaneType paneType) {
    JPanel tab = new JPanel(new BorderLayout());
    JLabel errMsg = new JLabel("", SwingConstants.CENTER);
    errMsg.setForeground(Color.RED);
    errMsg.setVisible(false);
    cpnr.add(errMsg);
    errorMsgList.add(errMsg);
    JLabel tabTitle = new JLabel(PaneType.getTabTitle(paneType.getValue()), SwingConstants.CENTER);
    cpnt.add(tabTitle);
    JPanel tabHeader = new JPanel(new GridLayout(0, 1));
    tabHeader.add(tabTitle);
    tabHeader.add(errMsg);
    tab.add(tabHeader, BorderLayout.NORTH);
    String position = paneType.getValue() > 2 ? BorderLayout.CENTER : BorderLayout.SOUTH;
    if (paneType.getValue() < 3) {
      JComboBox<String> dbOptions = new JComboBox<>(DatabaseType.getDropdownOptions());
      dbOptions.addActionListener((ActionEvent evt) -> updateComponents());
      databaseOptions = dbOptions;
      databaseOptionList.add(dbOptions);
      selectedDBTypeList.add(databaseTypeOptions[0]);
      cpnr.add(dbOptions);
      tabHeader.add(dbOptions);
      tab.add(createTabBody(paneType), BorderLayout.CENTER);
    }
    if (paneType.getValue() != 2) {
      tab.add(createInformationDisplay(paneType), position);
    }
    return tab;
  }

  /**
   * Updates the values associated with the combobox.
   */
  public void updateComponents() {
    int databasePos = databaseOptions.getSelectedIndex();
    DatabaseType selectedDatabaseType = databaseTypeOptions[databasePos];
    boolean contCreation = true;
    errorMsg.setVisible(false);
    if (tabType == PaneType.LAST_RUN) {
      dataDisplay.setText(null);
      runButton.setEnabled(false);
    }
    if (tabType == PaneType.COMPARE_WITH_SNAPSHOT
        && !FileHandler
            .fileExists(DatabaseType.getType(selectedDatabaseType.getValue()) + "_" + FileHandler.DB_SNAPSHOT_FILE)
        && databasePos != 0) {
      errorMsg.setText("Unable to do comparison: " + DatabaseType.getType(selectedDatabaseType.getValue())
          + " snapshot does not exist. Please run a database snapshot first.");
      errorMsg.setVisible(true);
      contCreation = false;
    }
    List<JTextComponent> userInputComponents = new ArrayList<>();
    for (JPanel inputForm : inputForms) {
      if (databasePos == 0 || !contCreation) {
        inputForm.removeAll();
      } else {
        createComponents(inputForm, DatabaseType.getInputs(selectedDatabaseType.getValue()), userInputComponents);
      }
      inputForm.revalidate();
      inputForm.repaint();
    }
    inputsList.set(tabType.getValue(), userInputComponents);
    executeButton.setEnabled(false);
  }

  /**
   * Creates the view where data will be displayed to the user which is either
   * output or log data.
   */
  private JPanel createInformationDisplay(PaneType type) {
    JPanel informationDisplay;
    if (type.getValue() < 2) {
      JLabel footerTitle = new JLabel("Preview:", SwingConstants.CENTER);
      cpnt.add(footerTitle);
      informationDisplay = new JPanel(new BorderLayout());
      informationDisplay.add(footerTitle, BorderLayout.NORTH);
    } else {
      informationDisplay = new JPanel(new GridLayout(0, 1));
    }
    JScrollPane data = new JScrollPane();
    JTextArea dataShow = new JTextArea(5, 20);
    dataShow.setEditable(false);
    cpnr.add(dataShow);
    dataDisplayList.add(dataShow);
    data.setAutoscrolls(true);
    data.setViewportView(dataShow);
    informationDisplay.add(data);
    return informationDisplay;
  }

  /**
   * Creates the input form based on the component input list and adds it to the
   * provided panel.
   *
   * @param componentHolder The JPanel that will hold the components that are
   *                        generated.
   * @param componentList   The list of components to add to the panel as well as
   *                        the text to be displayed as the input's label.
   * @param formComponents  A list of all the form components that can have input
   *                        from the user - it is used for validating user input.
   */
  private void createComponents(JPanel componentHolder, String[] componentList, List<JTextComponent> formComponents) {
    JTextComponent txtcpn;
    JLabel cpnLabel;
    componentHolder.removeAll();
    for (int i = 0; i < componentList.length; i++) {
      cpnLabel = new JLabel(componentList[i] + ":");
      cpnLabel.setFont(reg);
      componentHolder.add(cpnLabel);
      txtcpn = componentList[i].equals("Password") ? new JPasswordField(10) : new JTextField(10);
      txtcpn.setFont(reg);
      createInputListener(txtcpn, componentList[i]);
      componentHolder.add(txtcpn);
      formComponents.add(txtcpn);
    }
  }

  /**
   * Creates the tab body based on the pane type.
   */
  private JPanel createTabBody(PaneType type) {
    JPanel body = new JPanel(new BorderLayout());
    JPanel mainBody = new JPanel(new GridLayout(0, 1));
    JPanel inputBody1 = new JPanel(new GridLayout(0, 2));
    JPanel bodyFooter = new JPanel(new GridLayout(0, 1));
    JPanel userInputs = type == PaneType.COMPARE_WITH_DB ? new JPanel(new GridLayout(0, 2))
        : new JPanel(new GridLayout(0, 1));
    JPanel inputBody2;
    JPanel buttons;
    List<JPanel> userInputForms = new ArrayList<>();
    JButton runBtn;
    JButton execBtn;
    userInputForms.add(inputBody1);
    // add panel content and specific panels
    if (type == PaneType.COMPARE_WITH_DB) {
      inputBody2 = new JPanel(new GridLayout(0, 2));
      buttons = new JPanel(new GridLayout(0, 4));
      inputBody1.setBorder(BorderFactory.createTitledBorder("Development Information"));
      inputBody2.setBorder(BorderFactory.createTitledBorder("Live Information"));
      userInputs.add(inputBody1);
      userInputs.add(inputBody2);
      execBtn = new JButton("Produce Statements");
      runBtn = new JButton("Run Statements");
      runBtn.setEnabled(false);
      cpnBtn.add(runBtn);
      buttons.add(new JLabel());
      buttons.add(execBtn);
      buttons.add(runBtn);
      buttons.add(new JLabel());
      userInputForms.add(inputBody2);
    } else if (type == PaneType.COMPARE_WITH_SNAPSHOT) {
      buttons = new JPanel(new GridLayout(0, 4));
      inputBody1.setBorder(BorderFactory.createTitledBorder("Live Information"));
      userInputs.add(inputBody1);
      execBtn = new JButton("Produce Statements");
      runBtn = new JButton("Run Statements");
      runBtn.setEnabled(false);
      cpnBtn.add(runBtn);
      buttons.add(new JLabel());
      buttons.add(execBtn);
      buttons.add(runBtn);
    } else {
      buttons = new JPanel(new GridLayout(0, 3));
      inputBody1.setBorder(BorderFactory.createTitledBorder("Development Information"));
      userInputs.add(inputBody1);
      execBtn = new JButton("Take Snapshot");
      buttons.add(new JLabel());
      buttons.add(execBtn);
      buttons.add(new JLabel());
      runBtn = new JButton();
    }
    inputFormsList.add(inputForms);
    execBtn.setEnabled(false);
    execBtnList.add(execBtn);
    addPaneActionListeners(runBtn, execBtn, type);
    runBtnList.add(runBtn);
    JProgressBar pb = new JProgressBar();
    progressBarList.add(pb);
    cpnBtn.add(execBtn);
    mainBody.add(userInputs);
    bodyFooter.add(buttons);
    bodyFooter.add(pb);
    body.add(mainBody);
    body.add(bodyFooter, BorderLayout.SOUTH);
    return body;
  }

  /**
   * Makes sure that all fields are filled in and makes sure that the buttons to
   * generate statements and execute statements are disabled if alll fields are
   * not filled out.
   */
  private void validateInput() {
    if (allFieldsFilled()) {
      executeButton.setEnabled(true);
    } else {
      executeButton.setEnabled(false);
      disableRunningStatements();
    }
  }

  /**
   * Determines whether or not all the user input fields for the current tab are
   * filled out.
   *
   * @return Whether all fields have a non-whitespace character in them.
   */
  private boolean allFieldsFilled() {
    for (JTextComponent cpn : inputs) {
      if (cpn.getText().trim().isEmpty()) {
        return false;
      }
    }
    return true;
  }

  /**
   * Disables the button that lets you run the statements.
   */
  private void disableRunningStatements() {
    if (runButton != null) {
      runButton.setEnabled(false);
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