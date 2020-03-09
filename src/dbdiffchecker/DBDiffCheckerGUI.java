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
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
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

/**
 * A JFrame that has several tabs and includes the entire frontend.
 * 
 * @author Peter Kaufman
 * @version 3-9-20
 * @since 9-20-17
 */
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
  private HashMap<String, JComboBox<String>> databaseDropdowns = new HashMap<String, JComboBox<String>>(
      tabText.length - 2);
  private HashMap<String, JButton> executeButtons = new HashMap<String, JButton>(tabText.length - 2);
  private HashMap<String, JButton> runButtons = new HashMap<String, JButton>(tabText.length - 3);
  private HashMap<String, JProgressBar> progressBars = new HashMap<String, JProgressBar>(tabText.length - 2);
  private HashMap<String, ArrayList<JPanel>> userInputForms = new HashMap<String, ArrayList<JPanel>>(
      tabText.length - 2);
  private HashMap<String, ArrayList<JTextComponent>> userInputs = new HashMap<String, ArrayList<JTextComponent>>(
      tabText.length - 2);
  private HashMap<String, JTextArea> informationDisplays = new HashMap<String, JTextArea>(tabText.length - 1);
  private HashMap<String, JLabel> errorMessages = new HashMap<String, JLabel>(tabText.length - 2);
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

  /**
   * Initializes a JFrame which will be used by the user to navigate through the
   * application.
   * 
   * @author Peter Kaufman
   */
  public DBDiffCheckerGUI() {
    initComponents();
  }

  /**
   * Sets up the GUI layout, all action events, and instance variables.
   * 
   * @author Peter Kaufman
   */
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
            if (tabPos == 1
                && !FileHandler.fileExists(databaseTypes[databasePos] + "_" + FileHandler.databaseSnapshotFileName)
                && databasePos != 0) {
              errorMessages.get(currentTab).setText("Unable to do comparison: " + databaseTypes[databasePos]
                  + " snapshot does not exist. Please run a database snapshot first.");
              errorMessages.get(currentTab).setVisible(true);
            } else {
              for (JPanel inputForm : userInputForms.get(currentTab)) {
                if (databasePos == 0) {
                  inputForm.removeAll();
                  inputForm.revalidate();
                  inputForm.repaint();
                } else {
                  createComponents(inputForm, databaseInputs[databasePos - 1], userInputComponents);
                }
              }
              executeButtons.get(tabText[tabPos]).setEnabled(false);
              userInputs.put(tabText[tabPos], userInputComponents);
            }
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
    jtp.setTitleAt(0, "<html><b>" + tabText[0] + "</b></html>");
    // create the content for the first 3 tab bodies
    JPanel body, mainBody, inputBody1, inputBody2, bodyFooter, inputs, buttons;
    buttons = new JPanel(new GridLayout(1, 4));
    ArrayList<JPanel> tempInputs;
    JButton tempExecute, tempRun;
    JProgressBar tempPB;
    for (int i = 0; i < tabText.length - 2; i++) {
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
    for (int i = 0; i < tabText.length; i++) {
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
        for (int i = 0; i < tabText.length; i++) {
          jtp.setTitleAt(i, tabText[i]);
        }
        int tabPos = jtp.getSelectedIndex();
        currentTab = tabText[tabPos];
        jtp.setTitleAt(tabPos, "<html><b>" + tabText[tabPos] + "</b></html>");
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
            dataShow.setText("The application has no record of any statements run before.");
          }
        }
      }
    });
    // set size for different types of components
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

  /**
   * Creates the input form based on the component input list and adds it to the
   * provided panel.
   * 
   * @author Peter Kaufman
   * @param componentHolder The JPanel that will hold the components that are
   *                        generated.
   * @param componentList   The list of components to add to the panel as well as
   *                        the text to be displayed as the input's label.
   * @param formComponents  A list of all the form components that can have input
   *                        from the user - it is used for validating user input.
   */
  private void createComponents(JPanel componentHolder, String[] componentList,
      ArrayList<JTextComponent> formComponents) {
    JTextComponent temp;
    JLabel tempLabel;
    componentHolder.removeAll();
    for (int i = 0; i < componentList.length; i++) {
      tempLabel = new JLabel(componentList[i] + ":");
      tempLabel.setFont(regFont);
      componentHolder.add(tempLabel);
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

  /**
   * Creates an input listener which is used to validate the data that is input
   * and determine whether it is time to allow the user to submit the entered
   * data.
   * 
   * @author Peter Kaufman
   * @param input The component that will have the listener added to it.
   * @param type  The description of the input (i.e. Username, Password, etc.).
   *              This helps determine which listener to apply.
   */
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

  /**
   * Determines whether or not all the user input fields for the current tab are
   * filled out.
   * 
   * @author Peter Kaufman
   * @return A boolean that is false if not all fields have a non-whitespace
   *         character in them and true if all input fields have something other
   *         than non-whitespace characters in them.
   */
  private boolean allFieldsFilled() {
    for (JTextComponent cpn : userInputs.get(currentTab)) {
      if (new String(cpn.getText()).trim().isEmpty()) {
        return false;
      }
    }
    return true;
  }

  /**
   * Takes a snapshot of what the user indicates is the development database.
   * <i>Note: most of this function is run in a background thread.</i>
   * 
   * @author Peter Kaufman
   */
  private void createSnapshot() {
    databaseDropdowns.get(currentTab).setEnabled(false);
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
        FileHandler.serializeDatabase(devDatabase, databaseTypes[databaseDropdowns.get(currentTab).getSelectedIndex()]);
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
          databaseDropdowns.get(currentTab).setEnabled(true);
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
   * @author Peter Kaufman
   */
  private void generateStatements() {
    databaseDropdowns.get(currentTab).setEnabled(false);
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
        } finally {
          databaseDropdowns.get(currentTab).setEnabled(true);
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
   * Makes sure that all fields are filled in and makes sure that the buttons to
   * generate statements and execute statements are disabled if alll fields are
   * not filled out.
   * 
   * @author Peter Kaufman
   */
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
   * Stops the progressBar and sets the border to the given String.
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
   * @author Peter Kaufman
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
   * @author Peter Kaufman
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
      return new SQLiteConn(fixPath(inputs.get(0).getText().trim()), inputs.get(1).getText().trim(), type);
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
   * @author Peter Kaufman
   * @return A database connection for the live database.
   * @throws DatabaseDifferenceCheckerException Error connecting to the live
   *                                            database.
   */
  private DbConn createLiveDatabaseConnection() throws DatabaseDifferenceCheckerException {
    String selectedType = databaseTypes[databaseDropdowns.get(currentTab).getSelectedIndex()];
    String type = "live";
    ArrayList<JTextComponent> inputs = userInputs.get(currentTab);
    int startIndex = (inputs.size() / 2);
    if (currentTab.equals(tabText[1])) {
      startIndex = 0;
    }
    if (databaseTypes[1].equals(selectedType)) {
      return new MySQLConn(inputs.get(startIndex).getText().trim(), inputs.get(startIndex + 1).getText().trim(),
          inputs.get(startIndex + 2).getText().trim(), inputs.get(startIndex + 3).getText().trim(),
          inputs.get(startIndex + 4).getText().trim(), type);
    } else if (databaseTypes[2].equals(selectedType)) {
      return new SQLiteConn(fixPath(inputs.get(startIndex).getText().trim()),
          inputs.get(startIndex + 1).getText().trim(), type);
    } else if (databaseTypes[3].equals(selectedType)) {
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
   * Takes in data and writes it to the log file.
   * 
   * @author Peter Kaufman
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
   * 
   * @author Peter Kaufman
   */
  private void displayCompareResult() {
    try {
      JTextArea dataShow = informationDisplays.get(currentTab);
      if (statements.isEmpty()) {
        dataShow.setText("The databases are in sync.");
        runButtons.get(currentTab).setEnabled(false);
      } else {
        dataShow.setText(null);
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
   * Gets the two databases ready for the database comparison based on the current
   * tab that is active.
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

  /**
   * Takes the statements that were generated by the database comparison and runs
   * them on the live database. <i>Note: most of this function is run in a
   * background thread.</i>
   */
  private void executeStatements() {
    runButtons.get(currentTab).setEnabled(false);
    databaseDropdowns.get(currentTab).setEnabled(false);
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
          databaseDropdowns.get(currentTab).setEnabled(true);
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

  /**
   * Takes in a path and makes sure that it ends with a file separator.
   * 
   * @author Peter Kaufman
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
   * Initializes UI.
   * 
   * @author Peter Kaufman
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