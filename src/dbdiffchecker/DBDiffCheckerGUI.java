package dbdiffchecker;

import dbdiffchecker.sql.SQLDatabase;
import dbdiffchecker.sql.MySQLConn;
import dbdiffchecker.sql.SQLiteConn;
import dbdiffchecker.nosql.Bucket;
import dbdiffchecker.nosql.CouchbaseConn;
import dbdiffchecker.nosql.MongoConn;
import dbdiffchecker.nosql.MongoDB;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.text.JTextComponent;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.TitledBorder;
import javax.swing.ImageIcon;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.lang.InterruptedException;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public class DBDiffCheckerGUI extends JFrame {
  private final String tabText[] = { "Compare to Database", "Compare to Snapshot", "Create Snapshot", "Logs",
      "Last Run" };
  private final String tabTitles[] = { "Development and Live Database Information", "Live Database Information",
      "Development Database Information", "Logs", "Last Set of Statements Run" };
  private final String[] databaseTypes = { "Select Database Type", "MySQL", "SQLite", "Couchbase", "MongoDB" };
  private final String[][] databaseInputs = new String[][] {
      new String[] { "Username", "Password", "Host", "Port", "Database Name" },
      new String[] { "Database Path", "Database Name" },
      new String[] { "Username", "Password", "Host", "Database Name" },
      new String[] { "Username", "Password:", "Host", "Port", "Database Name" } };
  private HashMap<String, JPanel> tabContent = new HashMap<String, JPanel>(tabText.length);
  private HashMap<String, JComboBox<String>> databaseDropdowns = new HashMap<String, JComboBox<String>>();
  private HashMap<String, JButton> executeButtons = new HashMap<String, JButton>();
  private HashMap<String, JButton> runButtons = new HashMap<String, JButton>();
  private HashMap<String, JProgressBar> progressBars = new HashMap<String, JProgressBar>();
  private HashMap<String, ArrayList<JPanel>> userInputForms = new HashMap<String, ArrayList<JPanel>>();
  private HashMap<String, ArrayList<JTextComponent>> userInputs = new HashMap<String, ArrayList<JTextComponent>>();
  private HashMap<String, JTextArea> informationDisplays = new HashMap<String, JTextArea>(tabText.length);
  private HashMap<String, JLabel> errorMessages = new HashMap<String, JLabel>(tabText.length);
  private JTabbedPane jtp = new JTabbedPane();
  private JButton currentRunBtn;
  private Database devDatabase;
  private Database liveDatabase;
  private DbConn devDatabaseConnection;
  private DbConn liveDatabaseConnection;
  private TitledBorder nBorder = null;
  private StopWatch sw = new StopWatch();
  private String currentTab = tabText[0];
  private ArrayList<String> statements;
  private ArrayList<Component> cpnt = new ArrayList<>();
  private ArrayList<Component> cpnBtn = new ArrayList<>();
  private ArrayList<Component> cpnr = new ArrayList<>();
  private Font regFont = new Font("Tahoma", Font.PLAIN, 12);
  private Font tabFont = new Font("Tahoma", Font.PLAIN, 16);

  public DBDiffCheckerGUI() {
    initComponents();
  }

  private void initComponents() {
    jtp.setFont(tabFont);
    getContentPane().add(jtp);
    // Setup for the tabs by creating their content
    JPanel temp = new JPanel(), tempHeader;
    JLabel tempErrorMsg, tempTitle;
    for (int i = 0; i < tabText.length; i++) {
      // create the temporary items
      temp = new JPanel(new BorderLayout());
      tempHeader = new JPanel(new GridLayout(0, 1));
      tempErrorMsg = new JLabel("Error Message", JLabel.CENTER);
      tempErrorMsg.setForeground(Color.RED);
      tempErrorMsg.setVisible(false);
      cpnr.add(tempErrorMsg);
      errorMessages.put(tabText[i], tempErrorMsg);
      tempTitle = new JLabel(tabTitles[i], JLabel.CENTER);
      tempHeader.add(tempTitle);
      cpnt.add(tempTitle);
      tempHeader.add(tempErrorMsg);
      if (i < 3) {
        JComboBox<String> databaseOptions = new JComboBox<String>(databaseTypes);
        cpnr.add(databaseOptions);
        databaseOptions.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent event) {
            int tabPos = jtp.getSelectedIndex();
            int databasePos = databaseDropdowns.get(currentTab).getSelectedIndex();
            errorMessages.get(currentTab).setVisible(false);
            if (!currentTab.equals(tabText[2])) {
              informationDisplays.get(currentTab).setText(null);
            }
            ArrayList<JTextComponent> userInputComponents = new ArrayList<>();
            for (JPanel inputForm : userInputForms.get(currentTab)) {
              createComponents(inputForm, databaseInputs[databasePos - 1], userInputComponents);
            }
            executeButtons.get(tabText[tabPos]).setEnabled(false);
            userInputs.put(tabText[tabPos], userInputComponents);
          }
        });
        tempHeader.add(databaseOptions);
        databaseDropdowns.put(tabText[i], databaseOptions);
      }
      temp.add(tempHeader, BorderLayout.NORTH);
      // add them all to the layout
      tabContent.put(tabText[i], temp);
      jtp.addTab(tabText[i], temp);
    }
    // create the content for the first 3 tab bodies
    JPanel body, mainBody, inputBody1, inputBody2, bodyFooter, inputs, buttons;
    buttons = new JPanel(new GridLayout(1, 4));
    ArrayList<JPanel> tempInputs;
    JButton tempExecute, tempRun;
    JProgressBar tempPB;
    for (int i = 0; i < 3; i++) {
      tempInputs = new ArrayList<>();
      // create common panels
      body = new JPanel(new BorderLayout());
      mainBody = new JPanel(new GridLayout(0, 1));
      inputBody1 = new JPanel(new GridLayout(0, 2));
      bodyFooter = new JPanel(new GridLayout(0, 1));
      tempPB = new JProgressBar();
      tempInputs.add(inputBody1);
      // add panel content and speicific panels
      if (i == 0) {
        inputs = new JPanel(new GridLayout(0, 2));
        inputBody2 = new JPanel(new GridLayout(0, 2));
        buttons = new JPanel(new GridLayout(0, 4));
        inputBody1.setBorder(BorderFactory.createTitledBorder("Development Information"));
        inputBody2.setBorder(BorderFactory.createTitledBorder("Live Information"));
        inputs.add(inputBody1);
        inputs.add(inputBody2);
        tempExecute = new JButton("Produce Statements");
        tempRun = new JButton("Run Statements");
        runButtons.put(tabText[i], tempRun);
        buttons.add(new JLabel());
        buttons.add(tempExecute);
        buttons.add(tempRun);
        buttons.add(new JLabel());
        tempInputs.add(inputBody2);
        tempRun.setEnabled(false);
        currentRunBtn = tempRun;
        cpnBtn.add(currentRunBtn);
        tempExecute.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent evt) {
            errorMessages.get(currentTab).setVisible(false);
            generateStatements();
          }
        });
        tempRun.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent evt) {
            executeStatements();
          }
        });
      } else if (i == 1) {
        inputs = new JPanel(new GridLayout(0, 1));
        buttons = new JPanel(new GridLayout(0, 4));
        inputBody1.setBorder(BorderFactory.createTitledBorder("Live Information"));
        inputs.add(inputBody1);
        tempExecute = new JButton("Produce Statements");
        tempRun = new JButton("Run Statements");
        buttons.add(new JLabel());
        buttons.add(tempExecute);
        buttons.add(tempRun);
        tempRun.setEnabled(false);
        runButtons.put(tabText[i], tempRun);
        currentRunBtn = tempRun;
        cpnBtn.add(currentRunBtn);
        tempRun.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent evt) {
            executeStatements();
          }
        });
        tempExecute.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent evt) {
            errorMessages.get(currentTab).setVisible(false);
            generateStatements();
          }
        });
      } else {
        inputs = new JPanel(new GridLayout(0, 1));
        buttons = new JPanel(new GridLayout(0, 3));
        inputBody1.setBorder(BorderFactory.createTitledBorder("Development Information"));
        inputs.add(inputBody1);
        tempExecute = new JButton("Take Snapshot");
        buttons.add(new JLabel());
        buttons.add(tempExecute);
        buttons.add(new JLabel());
        currentRunBtn = null;
        tempExecute.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent evt) {
            errorMessages.get(currentTab).setVisible(false);
            createSnapshot();
          }
        });
      }
      cpnBtn.add(tempExecute);
      tempExecute.setEnabled(false);
      mainBody.add(inputs);
      bodyFooter.add(buttons);
      bodyFooter.add(tempPB);
      body.add(mainBody);
      body.add(bodyFooter, BorderLayout.SOUTH);
      progressBars.put(tabText[i], tempPB);
      userInputForms.put(tabText[i], tempInputs);
      executeButtons.put(tabText[i], tempExecute);
      tabContent.get(tabText[i]).add(body);
    }
    // create the display for previewing statements or data
    JPanel informationDisplay;
    JScrollPane data;
    JTextArea dataShow;
    String position = BorderLayout.SOUTH;
    for (int i = 0; i < 5; i++) {
      if (i < 2) { // both compare tabs
        JLabel footerTitle = new JLabel("Preview:", JLabel.CENTER);
        cpnt.add(footerTitle);
        informationDisplay = new JPanel(new BorderLayout());
        informationDisplay.add(footerTitle, BorderLayout.NORTH);
      } else { // logs and last run tab preview
        if (i == 2) {
          position = BorderLayout.CENTER;
          i++;
        }
        informationDisplay = new JPanel(new GridLayout(0, 1));
      }
      data = new JScrollPane();
      dataShow = new JTextArea(5, 20);
      cpnr.add(dataShow);
      data.setAutoscrolls(true);
      dataShow.setEditable(false);
      data.setViewportView(dataShow);
      informationDisplay.add(data);
      tabContent.get(tabText[i]).add(informationDisplay, position);
      informationDisplays.put(tabText[i], dataShow);
    }
    // add listener for tab changes
    jtp.addChangeListener(new ChangeListener() {

      public void stateChanged(ChangeEvent e) {
        currentTab = tabText[jtp.getSelectedIndex()];
        if (currentTab.equals(tabText[3])) {
          if (FileHandler.fileExists(FileHandler.logFileName)) {
            displayLog(FileHandler.logFileName);
          } else {
            JTextArea dataShow = informationDisplays.get(currentTab);
            dataShow.setText("There are no logs to display.");
          }
        } else if (currentTab.equals(tabText[4])) {
          if (FileHandler.fileExists(FileHandler.lastSequelStatementFileName)) {
            displayLog(FileHandler.lastSequelStatementFileName);
          } else {
            JTextArea dataShow = informationDisplays.get(currentTab);
            dataShow.setText("The application has no record of any statments run before.");
          }
        }
      }
    });
    // set size differences
    addComponentListener(new ComponentListener() {
      @Override
      public void componentResized(ComponentEvent e) {
        double width = e.getComponent().getWidth();
        // determine font sizes based on width of the GUI
        Font title = new Font("Tahoma", Font.BOLD, (int) (width / 38));
        Font reg = new Font("Tahoma", Font.PLAIN, (int) (width / 58));
        Font button = new Font("Tahoma", Font.BOLD, (int) (width / 53));
        // regular components
        for (ArrayList<JPanel> inputForms : userInputForms.values()) {
          for (JPanel inputForm : inputForms) {
            ((TitledBorder) inputForm.getBorder()).setTitleFont(reg.deriveFont(Font.BOLD));
            for (Component cpn : inputForm.getComponents()) {
              cpn.setFont(reg);
            }
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
        regFont = reg;
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
    // set intrinsic properties
    setTitle("Databse Difference Checker");
    setMinimumSize(new Dimension(700, 300));
    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    setIconImage(new ImageIcon(getClass().getResource("/resources/DBCompare.png")).getImage());
    setSize(700, 600);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setVisible(true);
  }

  private void createComponents(JPanel componentHolder, String[] componentList,
      ArrayList<JTextComponent> formComponents) {
    JTextComponent temp;
    componentHolder.removeAll();
    for (int i = 0; i < componentList.length; i++) {
      componentHolder.add(new JLabel(componentList[i] + ":"));
      switch (componentList[i]) {
        case "Password":
          temp = new JPasswordField(10);
          break;
        default:
          temp = new JTextField(10);
          break;
      }
      temp.setFont(regFont);
      createInputListener(temp, componentList[i]);
      componentHolder.add(temp);
      formComponents.add(temp);
    }
    componentHolder.revalidate();
    componentHolder.repaint();
  }

  private void createInputListener(JComponent input, String type) {
    if (type.equals("Port")) { // the input should only be integers
      JTextField cpn = (JTextField) input;
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
          validateInput();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
          validateInput();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
          validateInput();
        }
      });
    } else { // the validation is just that content exists
      if (type.equals("Password")) {
        JPasswordField cpn = (JPasswordField) input;
        cpn.getDocument().addDocumentListener(new DocumentListener() {

          @Override
          public void insertUpdate(DocumentEvent e) {
            validateInput();
          }

          @Override
          public void removeUpdate(DocumentEvent e) {
            validateInput();
          }

          @Override
          public void changedUpdate(DocumentEvent e) {
            validateInput();
          }
        });
      } else {
        JTextField cpn = (JTextField) input;
        cpn.getDocument().addDocumentListener(new DocumentListener() {
          @Override
          public void insertUpdate(DocumentEvent e) {
            validateInput();
          }

          @Override
          public void removeUpdate(DocumentEvent e) {
            validateInput();
          }

          @Override
          public void changedUpdate(DocumentEvent e) {
            validateInput();
          }
        });
      }

    }
  }

  private boolean allFieldsFilled() {
    for (JTextComponent cpn : userInputs.get(currentTab)) {
      if (new String(cpn.getText()).trim().isEmpty()) {
        return false;
      }
    }
    return true;
  }

  /**
   * Takes a devDatabase snapshot based on user input.
   * 
   * @author Peter Kaufman
   */
  private void createSnapshot() {
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
        FileHandler.serializeDatabase(devDatabase, currentTab);
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
   * Compares two databases based on user input (one can be a snapshot).
   * 
   * @author Peter Kaufman
   */
  private void generateStatements() {
    prepProgressBar("Establishing Database Connection(s) and Collecting Database Info", true);
    SwingWorker<Boolean, String> swingW = new SwingWorker<Boolean, String>() {
      @Override
      protected Boolean doInBackground() throws DatabaseDifferenceCheckerException {
        setupDatabases();
        publish("Comparing Databases");
        statements = devDatabase.compare(liveDatabase);
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
        }
      }

      @Override
      protected void process(List<String> chunks) {
        newBorder(chunks.get(chunks.size() - 1));
      }
    };
    swingW.execute();
  }

  private void validateInput() {
    if (allFieldsFilled()) {
      executeButtons.get(currentTab).setEnabled(true);
    } else {
      executeButtons.get(currentTab).setEnabled(false);
      if (currentRunBtn != null) {
        currentRunBtn.setEnabled(false);
      }
    }
  }

  /**
   * Gets the progressBar ready by reseting the StopWatch object and determines
   * which settings to turn on.
   * 
   * @author Peter Kaufman
   * @param title         The title for the border of the progressBar.
   * @param indeterminate Whether or not the progressBar is to be indeterminate.
   */
  private void prepProgressBar(String title, boolean indeterminate) {
    JProgressBar currentPB = progressBars.get(currentTab);
    newBorder(title);
    currentPB.setIndeterminate(indeterminate);
    if (!indeterminate) {
      currentPB.setValue(0);
      currentPB.setStringPainted(true);
    }
    currentPB.setEnabled(true);
    sw.reset();
  }

  /**
   * Stops the progressBar, sets the border to the given String, and then hides
   * the progressBar.
   * 
   * @author Peter Kaufman
   * @param title The title for the border of the progressBar
   */
  private void endProgressBar(String title) {
    newBorder(title);
    JProgressBar currentPB = progressBars.get(currentTab);
    if (currentPB.isIndeterminate()) {
      currentPB.setIndeterminate(false);
    } else {
      currentPB.setValue(100);
    }
  }

  /**
   * Takes and sets the new title for the progressbar's border.
   * 
   * @author Peter Kaufman
   * @param title The new name of the titled borders.
   */
  private void newBorder(String title) {
    JProgressBar currentPB = progressBars.get(currentTab);
    nBorder = BorderFactory.createTitledBorder(title);
    nBorder.setTitleFont(regFont);
    currentPB.setBorder(nBorder);
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
    String selectedType = databaseTypes[databaseDropdowns.get(currentTab).getSelectedIndex()];
    if (databaseTypes[1].equals(selectedType)) {
      return new SQLDatabase(databaseConn, 0);
    } else if (databaseTypes[2].equals(selectedType)) {
      return new SQLDatabase(databaseConn, 1);
    } else if (databaseTypes[3].equals(selectedType)) {
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
    String selectedType = databaseTypes[databaseDropdowns.get(currentTab).getSelectedIndex()];
    String type = "dev";
    ArrayList<JTextComponent> inputs = userInputs.get(currentTab);
    if (databaseTypes[1].equals(selectedType)) {
      return new MySQLConn(inputs.get(0).getText().trim(), inputs.get(1).getText().trim(),
          inputs.get(2).getText().trim(), inputs.get(3).getText().trim(), inputs.get(4).getText().trim(), type);
    } else if (databaseTypes[2].equals(selectedType)) {
      return new SQLiteConn(inputs.get(0).getText().trim(), inputs.get(1).getText().trim(), type);
    } else if (databaseTypes[3].equals(selectedType)) {
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
    String selectedType = databaseTypes[databaseDropdowns.get(currentTab).getSelectedIndex()];
    String type = "live";
    ArrayList<JTextComponent> inputs = userInputs.get(currentTab);
    if (databaseTypes[1].equals(selectedType)) {
      return new MySQLConn(inputs.get(5).getText().trim(), inputs.get(6).getText().trim(),
          inputs.get(7).getText().trim(), inputs.get(8).getText().trim(), inputs.get(9).getText().trim(), type);
    } else if (databaseTypes[2].equals(selectedType)) {
      return new SQLiteConn(inputs.get(2).getText().trim(), inputs.get(3).getText().trim(), type);
    } else if (databaseTypes[3].equals(selectedType)) {
      return new CouchbaseConn(inputs.get(4).getText().trim(), inputs.get(5).getText().trim(),
          inputs.get(6).getText().trim(), inputs.get(7).getText().trim());
    } else {
      return new MongoConn(inputs.get(5).getText().trim(), inputs.get(6).getText().trim(),
          inputs.get(7).getText().trim(), inputs.get(8).getText().trim(), inputs.get(9).getText().trim());
    }
  }

  /**
   * Opens a JFrame with the error message provided as a paramater.
   * 
   * @author Peter Kaufman
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
    errorMessages.get(currentTab).setVisible(true);
    errorMessages.get(currentTab).setText(error.toString());
  }

  /**
   * Takes in data and writes it to a log file.
   * 
   * @author Peter Kaufman
   * @param info The data to be logged.
   * @throws DatabaseDifferenceCheckerException Error logging data.
   */
  private void log(String info) throws DatabaseDifferenceCheckerException {
    FileHandler.writeToFile(info);
  }

  /**
   * Opens a JFrame with log information based on what file name is passed to it.
   * 
   * @author Peter Kaufman
   * @param file The file to have its contents displayed.
   */
  private void displayLog(String file) {
    try {
      ArrayList<String> statementList = FileHandler.readFrom(file);
      JTextArea dataShow = informationDisplays.get(currentTab);
      if (statementList.isEmpty()) {
        if (currentTab.equals(tabText[3])) {
          dataShow.setText("There are no logs to display.");
        } else {
          dataShow.setText("The application has no record of any statments run before.");
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
        log("There was an error recovering " + file + ".");
      } catch (DatabaseDifferenceCheckerException logError) {
        System.out.println("unable to log error... " + logError.getMessage());
      }
    }
  }

  private void displayCompareResult() {
    try {
      JTextArea dataShow = informationDisplays.get(currentTab);
      if (statements.isEmpty()) {
        dataShow.setText("The databases are in sync.");
        runButtons.get(currentTab).setEnabled(false);
      } else {
        runButtons.get(currentTab).setEnabled(true);
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
   * Gets two databases setup based on the type of the JFrame.
   * 
   * @author Peter Kaufman
   * @throws DatabaseDifferenceCheckerException if there was an error connnecting
   *                                            to a database.
   */
  private void setupDatabases() throws DatabaseDifferenceCheckerException {
    sw.start();
    if (currentTab.equals(tabText[0])) {
      devDatabaseConnection = createDevDatabaseConnection();
      devDatabase = createDatabase(devDatabaseConnection);
    } else {
      devDatabase = FileHandler.deserailizDatabase(databaseTypes[databaseDropdowns.get(currentTab).getSelectedIndex()]);
    }
    liveDatabaseConnection = createLiveDatabaseConnection();
    liveDatabase = createDatabase(liveDatabaseConnection);
  }

  private void executeStatements() {
    runButtons.get(currentTab).setEnabled(false);
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
        }
      }

      @Override
      protected void process(List<Integer> chunks) {
        JProgressBar currentPB = progressBars.get(currentTab);
        runButtons.get(currentTab).setEnabled(false);
        newBorder("Running SQL.. ");
        currentPB.setValue((int) ((chunks.get(chunks.size() - 1) + 1.0) * 100 / statements.size()));
        currentPB.setString(currentPB.getPercentComplete() * 100 + "%");
      }
    };
    swingW.execute();
  }

  public static void main(String[] args) {
    new DBDiffCheckerGUI();
  }
}