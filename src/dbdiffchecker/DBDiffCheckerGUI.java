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
import java.util.logging.Level;
import java.util.logging.Logger;
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
 * @version 7-8-20
 * @since 9-20-17
 */
public class DBDiffCheckerGUI extends JFrame {
  private static final long serialVersionUID = 1L;
  private static final String FONT_FAMILY = "Tahoma";
  private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
  private DatabaseType[] databaseTypeOptions = DatabaseType.values();
  private DatabaseType selectedType;
  private PaneType[] paneTypeOptions = PaneType.values();
  private PaneType tabType;
  private Font reg = new Font(FONT_FAMILY, Font.PLAIN, 12);
  private Database devDatabase;
  private Database liveDatabase;
  private transient DbConn devDatabaseConnection;
  private transient DbConn liveDatabaseConnection;
  private transient HashMap<String, DbConn> liveConnectionLists = new HashMap<>(paneTypeOptions.length - 3);
  private transient StopWatch sw = new StopWatch();
  private String currentTab = PaneType.getTabText(0);
  private List<String> statements;
  private Map<String, List<String>> statementsLists = new HashMap<>(paneTypeOptions.length - 3);
  private List<JPanel> inputForms = new ArrayList<>(paneTypeOptions.length - 2);
  private List<Component> cpnt = new ArrayList<>();
  private List<Component> cpnBtn = new ArrayList<>();
  private List<Component> cpnr = new ArrayList<>();
  private JTabbedPane jtp = new JTabbedPane();
  private TabPane selectedTab;
  private JButton runBtn;
  private JTextArea dataShow;
  private JLabel errorMsg;
  private JComboBox<String> databaseOptions;
  private List<JTextComponent> inputs;
  private JProgressBar progressBar;
  private int tabPos = 0;

  /**
   * Initializes a JFrame which will be used by the user to navigate through the
   * application.
   */
  public DBDiffCheckerGUI() {
    createTabbedPaneContent();
    selectedTab = (TabPane) jtp.getComponentAt(0);
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
          dataShow.setText("There are no logs to display.");
        }
      } else if (tabType == PaneType.LAST_RUN) {
        if (FileHandler.fileExists(FileHandler.LAST_RUN_FILE)) {
          displayLog(FileHandler.LAST_RUN_FILE);
        } else {
          dataShow.setText("The application has no record of any statements run before.");
        }
      }
      // get connection and statement information from the last run on the tab
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
      public void componentHidden(ComponentEvent e) { // add no new functionality
      }

      @Override
      public void componentShown(ComponentEvent e) { // add no new functionality
      }

      @Override
      public void componentMoved(ComponentEvent e) { // add no new functionality
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

  private void createTabbedPaneContent() {
    initializeTabs();
    Font tabFont = new Font(FONT_FAMILY, Font.PLAIN, 16);
    jtp.setFont(tabFont);
    jtp.setTitleAt(0, "<html><b>" + currentTab + "</b></html>");
  }

  private void initializeTabs() {
    PaneType tabPaneType;
    TabPane tabPane;
    for (int i = 0; i < paneTypeOptions.length; i++) {
      tabPaneType = paneTypeOptions[i];
      tabPane = new TabPane(tabPaneType);
      jtp.addTab(PaneType.getTabText(tabPaneType.getValue()), tabPane);
    }
  }

  /**
   * Updates the variables that are tab specific.
   */
  private void updateTabVariables() {
    selectedTab = (TabPane) jtp.getSelectedComponent();
    runBtn = selectedTab.runBtn;
    tabType = selectedTab.type;
    dataShow = selectedTab.dataShow;
    databaseOptions = selectedTab.dbOptions;
    errorMsg = selectedTab.errMsg;
    progressBar = selectedTab.pb;
    inputs = selectedTab.userInputComponents;
    inputForms = selectedTab.inputForms;
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
        FileHandler.serializeDatabase(devDatabase, DatabaseType.getType(selectedTab.selectedDatabaseType.getValue()));
        sw.stop();
        log(String.format("Took a Database Snapshot in %lds.", sw.getElapsedTime().toMillis() / 1000.0));
        return true;
      }

      @Override
      protected void done() {
        try {
          get();
          endProgressBar("Database Snapshot Complete");
        } catch (ExecutionException err) {
          handleThreadException(err);
        } catch (InterruptedException err) {
          Thread.currentThread().interrupt();
          handleThreadException(err);
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
        sw.start();
        setupDatabases();
        publish("Comparing Databases");
        statements = devDatabase.compare(liveDatabase);
        statementsLists.put(currentTab, statements);
        sw.stop();
        log(String.format("Completed database comparison in %lds.", sw.getElapsedTime().toMillis() / 1000.0));
        return true;
      }

      @Override
      protected void done() {
        try {
          get();
          endProgressBar("Database Comparison Complete");
          displayCompareResult();
        } catch (ExecutionException err) {
          handleThreadException(err);
        } catch (InterruptedException err) {
          Thread.currentThread().interrupt();
          handleThreadException(err);
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
    boolean isLive = false;
    if (DatabaseType.MYSQL == selectedType) {
      return new MySQLConn(inputs.get(0).getText().trim(), inputs.get(1).getText().trim(),
          inputs.get(2).getText().trim(), inputs.get(3).getText().trim(), inputs.get(4).getText().trim(), isLive);
    } else if (DatabaseType.SQLITE == selectedType) {
      return new SQLiteConn(fixPath(inputs.get(0).getText().trim()), inputs.get(1).getText().trim(), isLive);
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
    boolean isLive = true;
    int startIndex = (inputs.size() / 2);
    if (PaneType.COMPARE_WITH_SNAPSHOT == tabType) {
      startIndex = 0;
    }
    if (DatabaseType.MYSQL == selectedType) {
      return new MySQLConn(inputs.get(startIndex).getText().trim(), inputs.get(startIndex + 1).getText().trim(),
          inputs.get(startIndex + 2).getText().trim(), inputs.get(startIndex + 3).getText().trim(),
          inputs.get(startIndex + 4).getText().trim(), isLive);
    } else if (DatabaseType.SQLITE == selectedType) {
      return new SQLiteConn(fixPath(inputs.get(startIndex).getText().trim()),
          inputs.get(startIndex + 1).getText().trim(), isLive);
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
   *
   * @param error The exception which contains a user friendly message and the
   *              error that is the cause.
   */
  private void error(DatabaseDifferenceCheckerException error) {
    StringWriter strw = new StringWriter();
    error.printStackTrace(new PrintWriter(strw));
    String exceptionAsString = strw.toString();
    log(exceptionAsString);
    errorMsg.setVisible(true);
    errorMsg.setText(error.toString());
  }

  /**
   * Takes in data and writes it to the log file.
   *
   * @param info The data to be logged.
   */
  private void log(String info) {
    try {
      FileHandler.writeToFile(info);
    } catch (DatabaseDifferenceCheckerException err) {
      LOGGER.log(Level.SEVERE, String.format("Cannot write to log file: %s", err.getMessage()));
    }
  }

  /**
   * Reads in data from a log file and displays it to the user by adding it to the
   * current tab's JTextArea.
   *
   * @param file The file to have its contents displayed.
   */
  private void displayLog(String file) {
    try {
      statements = FileHandler.readFrom(file);
      dataShow.setText(null);
      for (String statement : statements) {
        dataShow.append(statement + "\n");
      }
    } catch (DatabaseDifferenceCheckerException cause) {
      error(cause);
      log("There was an error finding " + file + ".");
    }
  }

  /**
   * Displays the generated statements or displays that the databases are in sync.
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
      devDatabase = FileHandler.deserailizDatabase(DatabaseType.getType(selectedTab.selectedDatabaseType.getValue()));
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
    prepProgressBar("Running Statements...", false);
    SwingWorker<Boolean, Integer> swingW = new SwingWorker<Boolean, Integer>() {
      @Override
      protected Boolean doInBackground() throws Exception {
        String temp;
        sw.start();
        liveDatabaseConnection.establishDatabaseConnection();
        for (int i = 0; i < statements.size(); i++) {
          temp = statements.get(i);
          liveDatabaseConnection.runStatement(temp);
          publish(i);
        }
        liveDatabaseConnection.closeDatabaseConnection();
        sw.stop();
        log(String.format("Ran statements in %lds.", sw.getElapsedTime().toMillis() / 1000.0));
        return true;
      }

      @Override
      protected void done() {
        try {
          get();
          endProgressBar("Sucessfully updated the database");
        } catch (ExecutionException err) {
          handleThreadException(err);
        } catch (InterruptedException err) {
          Thread.currentThread().interrupt();
          handleThreadException(err);
        } finally {
          databaseOptions.setEnabled(true);
        }
      }

      @Override
      protected void process(List<Integer> chunks) {
        int percent = (chunks.get(chunks.size() - 1) + 1) * 100 / statements.size();
        progressBar.setValue(percent);
        progressBar.setString(percent + "%");
      }
    };
    swingW.execute();
  }

  private void handleThreadException(Exception err) {
    sw.stop();
    endProgressBar("An Error Occurred");
    Throwable cause = err.getCause();
    if (cause instanceof DatabaseDifferenceCheckerException) {
      error((DatabaseDifferenceCheckerException) cause);
    } else {
      error(new DatabaseDifferenceCheckerException(cause.getMessage(), err, 1005));
    }
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
   * Represents a tab in the tab pane layout.
   *
   * @author Peter Kaufman
   * @version 7-6-20
   * @since 7-1-20
   */
  private class TabPane extends JPanel {
    private static final long serialVersionUID = 1L;
    private DatabaseType selectedDatabaseType = databaseTypeOptions[0];
    private JPanel body = new JPanel(new BorderLayout());
    private JPanel informationDisplay;
    private List<JPanel> inputForms = new ArrayList<>();
    private List<JTextComponent> userInputComponents;
    private JLabel errMsg = new JLabel("", SwingConstants.CENTER);
    private JButton executeBtn;
    private JButton runBtn = null;
    private JTextArea dataShow = new JTextArea(5, 20);
    private JComboBox<String> dbOptions;
    private PaneType type;
    private JProgressBar pb = new JProgressBar();

    /**
     * Initializes the tab, but leaves some elements to be setup later.
     *
     * @param paneType The pane type that the tab represents.
     */
    public TabPane(PaneType paneType) {
      super(new BorderLayout());
      type = paneType;
      errMsg.setForeground(Color.RED);
      errMsg.setVisible(false);
      cpnr.add(errMsg);
      JLabel tabTitle = new JLabel(PaneType.getTabTitle(type.getValue()), SwingConstants.CENTER);
      cpnt.add(tabTitle);
      JPanel tabHeader = new JPanel(new GridLayout(0, 1));
      tabHeader.add(tabTitle);
      tabHeader.add(errMsg);
      add(tabHeader, BorderLayout.NORTH);
      String position = type.getValue() > 2 ? BorderLayout.CENTER : BorderLayout.SOUTH;
      if (type.getValue() < 3) {
        dbOptions = new JComboBox<>(DatabaseType.getDropdownOptions());
        dbOptions.addActionListener((ActionEvent evt) -> updateComponents());
        cpnr.add(dbOptions);
        tabHeader.add(dbOptions);
        createTabBody();
        add(body, BorderLayout.CENTER);
      }
      if (type.getValue() != 2) {
        createInformationDisplay();
        add(informationDisplay, position);
      }
    }

    /**
     * Updates the values associated with the combobox.
     */
    public void updateComponents() {
      int databasePos = databaseOptions.getSelectedIndex();
      selectedDatabaseType = databaseTypeOptions[databasePos];
      boolean contCreation = true;
      errorMsg.setVisible(false);
      if (type == PaneType.LAST_RUN) {
        dataShow.setText(null);
        runBtn.setEnabled(false);
      }
      userInputComponents = new ArrayList<>();
      if (type == PaneType.COMPARE_WITH_SNAPSHOT
          && !FileHandler
              .fileExists(DatabaseType.getType(selectedDatabaseType.getValue()) + "_" + FileHandler.DB_SNAPSHOT_FILE)
          && databasePos != 0) {
        errorMsg.setText("Unable to do comparison: " + DatabaseType.getType(selectedDatabaseType.getValue())
            + " snapshot does not exist. Please run a database snapshot first.");
        errorMsg.setVisible(true);
        contCreation = false;
      }
      for (JPanel inputForm : inputForms) {
        if (databasePos == 0 || !contCreation) {
          inputForm.removeAll();
        } else {
          createComponents(inputForm, DatabaseType.getInputs(selectedDatabaseType.getValue()), userInputComponents);
        }
        inputForm.revalidate();
        inputForm.repaint();
      }
      inputs = userInputComponents;
      executeBtn.setEnabled(false);
    }

    /**
     * Creates the view where data will be displayed to the user which is either
     * output or log data.
     */
    private void createInformationDisplay() {
      if (type.getValue() < 2) {
        JLabel footerTitle = new JLabel("Preview:", SwingConstants.CENTER);
        cpnt.add(footerTitle);
        informationDisplay = new JPanel(new BorderLayout());
        informationDisplay.add(footerTitle, BorderLayout.NORTH);
      } else {
        informationDisplay = new JPanel(new GridLayout(0, 1));
      }
      JScrollPane data = new JScrollPane();
      data.setAutoscrolls(true);
      dataShow.setEditable(false);
      cpnr.add(dataShow);
      data.setViewportView(dataShow);
      informationDisplay.add(data);
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
            insertUpdate(e);
          }

          @Override
          public void changedUpdate(DocumentEvent e) {
            insertUpdate(e);
          }
        });
      } else {
        JTextField cpn = (JTextField) input;
        if (type.equals("Port")) {
          cpn.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
              Runnable format = () -> {
                String text = cpn.getText();
                String regex = "\\d+";
                if (!text.matches(regex) && text.length() > 0) {
                  cpn.setText(text.substring(0, text.length() - 1));
                }
              };
              SwingUtilities.invokeLater(format);
              changedUpdate(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
              changedUpdate(e);
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
              insertUpdate(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
              insertUpdate(e);
            }
          });
        }
      }
    }

    /**
     * Creates the tab body based on the pane type.
     */
    private void createTabBody() {
      JPanel mainBody = new JPanel(new GridLayout(0, 1));
      JPanel inputBody1 = new JPanel(new GridLayout(0, 2));
      JPanel bodyFooter = new JPanel(new GridLayout(0, 1));
      JPanel userInputs = type == PaneType.COMPARE_WITH_DB ? new JPanel(new GridLayout(0, 2))
          : new JPanel(new GridLayout(0, 1));
      JPanel inputBody2;
      JPanel buttons;
      inputForms.add(inputBody1);
      // add panel content and specific panels
      if (type == PaneType.COMPARE_WITH_DB) {
        inputBody2 = new JPanel(new GridLayout(0, 2));
        buttons = new JPanel(new GridLayout(0, 4));
        inputBody1.setBorder(BorderFactory.createTitledBorder("Development Information"));
        inputBody2.setBorder(BorderFactory.createTitledBorder("Live Information"));
        userInputs.add(inputBody1);
        userInputs.add(inputBody2);
        executeBtn = new JButton("Produce Statements");
        runBtn = new JButton("Run Statements");
        runBtn.setEnabled(false);
        cpnBtn.add(runBtn);
        buttons.add(new JLabel());
        buttons.add(executeBtn);
        buttons.add(runBtn);
        buttons.add(new JLabel());
        inputForms.add(inputBody2);
      } else if (type == PaneType.COMPARE_WITH_SNAPSHOT) {
        buttons = new JPanel(new GridLayout(0, 4));
        inputBody1.setBorder(BorderFactory.createTitledBorder("Live Information"));
        userInputs.add(inputBody1);
        executeBtn = new JButton("Produce Statements");
        runBtn = new JButton("Run Statements");
        runBtn.setEnabled(false);
        cpnBtn.add(runBtn);
        buttons.add(new JLabel());
        buttons.add(executeBtn);
        buttons.add(runBtn);
      } else {
        buttons = new JPanel(new GridLayout(0, 3));
        inputBody1.setBorder(BorderFactory.createTitledBorder("Development Information"));
        userInputs.add(inputBody1);
        executeBtn = new JButton("Take Snapshot");
        buttons.add(new JLabel());
        buttons.add(executeBtn);
        buttons.add(new JLabel());
        runBtn = null;
      }
      executeBtn.setEnabled(false);
      addPaneActionListeners();
      cpnBtn.add(executeBtn);
      mainBody.add(userInputs);
      bodyFooter.add(buttons);
      bodyFooter.add(pb);
      body.add(mainBody);
      body.add(bodyFooter, BorderLayout.SOUTH);
    }

    /**
     * Makes sure that all fields are filled in and makes sure that the buttons to
     * generate statements and execute statements are disabled if all fields are not
     * filled out.
     */
    private void validateInput() {
      if (allFieldsFilled()) {
        executeBtn.setEnabled(true);
      } else {
        executeBtn.setEnabled(false);
        disableRunningStatements();
      }
    }

    /**
     * Determines whether or not all the user input fields for the current tab are
     * filled out.
     *
     * @return False if not all fields have a non-whitespace character in them and
     *         true if all input fields have something other than non-whitespace
     *         characters in them.
     */
    private boolean allFieldsFilled() {
      for (JTextComponent cpn : userInputComponents) {
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
      if (runBtn != null) {
        runBtn.setEnabled(false);
      }
    }

    /**
     * Adds the action listeners used on the combobox and other user input
     * components.
     */
    private void addPaneActionListeners() {
      switch (type) {
        case COMPARE_WITH_DB:
        case COMPARE_WITH_SNAPSHOT:
          runBtn.addActionListener((ActionEvent evt) -> executeStatements());
          executeBtn.addActionListener((ActionEvent evt) -> {
            errorMsg.setVisible(false);
            generateStatements();
          });
          break;
        case SNAPSHOT:
          executeBtn.addActionListener((ActionEvent evt) -> {
            errorMsg.setVisible(false);
            createSnapshot();
          });
          break;
        default:
          break;
      }
      dbOptions.addActionListener((ActionEvent evt) -> {
        newBorder("");
        updateComponents();
        selectedDatabaseType = databaseTypeOptions[databaseOptions.getSelectedIndex()];
      });
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
      LOGGER.log(Level.INFO, "Unable to get the system's look and feel.");
    }
    new DBDiffCheckerGUI();
  }
}