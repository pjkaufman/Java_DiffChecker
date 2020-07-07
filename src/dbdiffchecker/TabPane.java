package dbdiffchecker;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.text.JTextComponent;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import javax.swing.SwingUtilities;
import java.util.ArrayList;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

public class TabPane extends JPanel {
  DatabaseType[] databaseTypeOptions = DatabaseType.values();
  DatabaseType selectedDatabaseType = databaseTypeOptions[0];
  private JPanel tabHeader = new JPanel(new GridLayout(0, 1)), body = new JPanel(new BorderLayout()),
      informationDisplay;
  private ArrayList<JPanel> inputForms = new ArrayList<>();
  private ArrayList<JTextComponent> userInputComponents;
  private JLabel errorMsg = new JLabel("", JLabel.CENTER), tabTitle, footerTitle;
  private JButton executeBtn, runBtn = null;
  private JTextArea dataShow = new JTextArea(5, 20);
  private JComboBox<String> databaseOptions;
  private PaneType type;
  private JProgressBar progressBar = new JProgressBar();

  public TabPane(PaneType paneType) {
    super(new BorderLayout());
    type = paneType;
    errorMsg.setForeground(Color.RED);
    errorMsg.setVisible(false);
    tabTitle = new JLabel(PaneType.getTabTitle(type.getValue()), JLabel.CENTER);
    tabHeader.add(tabTitle);
    tabHeader.add(errorMsg);
    add(tabHeader, BorderLayout.NORTH);
    String position = paneType.getValue() > 2 ? BorderLayout.CENTER : BorderLayout.SOUTH;
    if (paneType.getValue() < 3) {
      databaseOptions = new JComboBox<String>(DatabaseType.getDropdownOptions());
      tabHeader.add(databaseOptions);
      createTabBody();
      add(body, BorderLayout.CENTER);
    }
    if (paneType.getValue() != 2) {
      createInformationDisplay();
      add(informationDisplay, position);
    }
  }

  public JButton getRunBtn() {
    return runBtn;
  }

  public JButton getExecuteBtn() {
    return executeBtn;
  }

  public DatabaseType getSelectedDatabase() {
    return selectedDatabaseType;
  }

  public PaneType getType() {
    return type;
  }

  public JTextArea getDataShow() {
    return dataShow;
  }

  public JComboBox<String> getDatabaseOptions() {
    return databaseOptions;
  }

  public JLabel getErrorMessage() {
    return errorMsg;
  }

  public JProgressBar getProgressBar() {
    return progressBar;
  }

  public ArrayList<JTextComponent> getUserInputs() {
    return userInputComponents;
  }

  public ArrayList<JPanel> getInputForms() {
    return inputForms;
  }

  public void updateComponents() {
    int databasePos = databaseOptions.getSelectedIndex();
    selectedDatabaseType = databaseTypeOptions[databaseOptions.getSelectedIndex()];
    boolean contCreation = true;
    errorMsg.setVisible(false);
    if (type == PaneType.LAST_RUN) {
      dataShow.setText(null);
      runBtn.setEnabled(false);
    }
    userInputComponents = new ArrayList<>();
    if (type == PaneType.COMPARE_WITH_SNAPSHOT
        && !FileHandler.fileExists(
            DatabaseType.getType(selectedDatabaseType.getValue()) + "_" + FileHandler.databaseSnapshotFileName)
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
    executeBtn.setEnabled(false);
  }

  public void addComponentsToResizeList(ArrayList<Component> cpnr, ArrayList<Component> cpnBtn,
      ArrayList<Component> cpnt) {
    cpnr.add(errorMsg);
    cpnt.add(tabTitle);
    int currentType = type.getValue();
    if (currentType < 2) {
      cpnt.add(footerTitle);
      cpnBtn.add(runBtn);
    }
    if (currentType < 3) {
      cpnr.add(databaseOptions);
      cpnBtn.add(executeBtn);
    }
    if (currentType != 2) {
      cpnr.add(dataShow);
    }
  }

  private void createInformationDisplay() {
    if (type.getValue() < 2) {
      footerTitle = new JLabel("Preview:", JLabel.CENTER);
      informationDisplay = new JPanel(new BorderLayout());
      informationDisplay.add(footerTitle, BorderLayout.NORTH);
    } else {
      informationDisplay = new JPanel(new GridLayout(0, 1));
    }
    JScrollPane data = new JScrollPane();
    data.setAutoscrolls(true);
    dataShow.setEditable(false);
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
  private void createComponents(JPanel componentHolder, String[] componentList,
      ArrayList<JTextComponent> formComponents) {
    JTextComponent txtcpn;
    JLabel cpnLabel;
    componentHolder.removeAll();
    for (int i = 0; i < componentList.length; i++) {
      cpnLabel = new JLabel(componentList[i] + ":");
      // tempLabel.setFont(regFont);
      componentHolder.add(cpnLabel);
      switch (componentList[i]) {
        case "Password":
          txtcpn = new JPasswordField(10);
          break;
        default:
          txtcpn = new JTextField(10);
          break;
      }
      // temp.setFont(regFont);
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

  private void createTabBody() {
    JPanel mainBody = new JPanel(new GridLayout(0, 1)), inputBody1 = new JPanel(new GridLayout(0, 2)),
        bodyFooter = new JPanel(new GridLayout(0, 1)), buttons = new JPanel(new GridLayout(1, 4));
    JPanel inputs, inputBody2;
    inputForms.add(inputBody1);
    inputs = type == PaneType.COMPARE_WITH_DB ? new JPanel(new GridLayout(0, 2)) : new JPanel(new GridLayout(0, 1));
    // add panel content and specific panels
    switch (type) {
      case COMPARE_WITH_DB:
        inputBody2 = new JPanel(new GridLayout(0, 2));
        buttons = new JPanel(new GridLayout(0, 4));
        inputBody1.setBorder(BorderFactory.createTitledBorder("Development Information"));
        inputBody2.setBorder(BorderFactory.createTitledBorder("Live Information"));
        inputs.add(inputBody1);
        inputs.add(inputBody2);
        executeBtn = new JButton("Produce Statements");
        runBtn = new JButton("Run Statements");
        buttons.add(new JLabel());
        buttons.add(executeBtn);
        buttons.add(runBtn);
        buttons.add(new JLabel());
        inputForms.add(inputBody2);
        runBtn.setEnabled(false);
        break;
      case COMPARE_WITH_SNAPSHOT:
        buttons = new JPanel(new GridLayout(0, 4));
        inputBody1.setBorder(BorderFactory.createTitledBorder("Live Information"));
        inputs.add(inputBody1);
        executeBtn = new JButton("Produce Statements");
        runBtn = new JButton("Run Statements");
        buttons.add(new JLabel());
        buttons.add(executeBtn);
        buttons.add(runBtn);
        runBtn.setEnabled(false);
        break;
      case SNAPSHOT:
        buttons = new JPanel(new GridLayout(0, 3));
        inputBody1.setBorder(BorderFactory.createTitledBorder("Development Information"));
        inputs.add(inputBody1);
        executeBtn = new JButton("Take Snapshot");
        buttons.add(new JLabel());
        buttons.add(executeBtn);
        buttons.add(new JLabel());
        runBtn = null;
        break;
      default:
        break;
    }
    executeBtn.setEnabled(false);
    mainBody.add(inputs);
    bodyFooter.add(buttons);
    bodyFooter.add(progressBar);
    body.add(mainBody);
    body.add(bodyFooter, BorderLayout.SOUTH);
  }

  /**
   * Makes sure that all fields are filled in and makes sure that the buttons to
   * generate statements and execute statements are disabled if alll fields are
   * not filled out.
   */
  private void validateInput() {
    if (allFieldsFilled()) {
      executeBtn.setEnabled(true);
    } else {
      executeBtn.setEnabled(false);
      if (runBtn != null) {
        runBtn.setEnabled(false);
      }
    }
  }

  /**
   * Determines whether or not all the user input fields for the current tab are
   * filled out.
   *
   * @return A boolean that is false if not all fields have a non-whitespace
   *         character in them and true if all input fields have something other
   *         than non-whitespace characters in them.
   */
  private boolean allFieldsFilled() {
    for (JTextComponent cpn : userInputComponents) {
      if (new String(cpn.getText()).trim().isEmpty()) {
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
}