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
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class TabPane extends JPanel {
  DatabaseType[] databaseTypeOptions = DatabaseType.values();
  DatabaseType selectedDatabaseType = databaseTypeOptions[0];
  private JPanel tabHeader = new JPanel(new GridLayout(0, 1)), body, informationDisplay;
  private ArrayList<JPanel> inputForms = new ArrayList<>();
  protected ArrayList<JTextComponent> userInputComponents;
  protected JLabel errorMsg = new JLabel("", JLabel.CENTER);
  protected JButton executeBtn, runBtn = null;
  protected JTextArea dataShow = new JTextArea(5, 20);
  protected JComboBox<String> databaseOptions;
  protected PaneType type;
  protected JProgressBar progressBar = new JProgressBar();

  public TabPane(PaneType paneType) {
    super(new BorderLayout());
    type = paneType;
    errorMsg.setForeground(Color.RED);
    errorMsg.setVisible(false);
    // cpnr.add(errorMsg);
    JLabel tabTitle = new JLabel(paneType.getTabTitle(), JLabel.CENTER);
    tabHeader.add(tabTitle);
    // cpnt.add(tabTitle);
    tabHeader.add(errorMsg);
    String position = paneType.getValue() > 2 ? BorderLayout.CENTER : BorderLayout.SOUTH;
    if (paneType.getValue() < 3) {
      createTabbedPane();
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

  private void createInformationDisplay() {
    if (type.getValue() < 2) {
      JLabel footerTitle = new JLabel("Preview:", JLabel.CENTER);
      // cpnt.add(footerTitle);
      informationDisplay = new JPanel(new BorderLayout());
      informationDisplay.add(footerTitle, BorderLayout.NORTH);
    } else {
      informationDisplay = new JPanel(new GridLayout(0, 1));
    }
    // cpnr.add(dataShow);
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

  private void createTabbedPane() {
    databaseOptions = new JComboBox<String>(selectedDatabaseType.getDropdownOptions());
    // cpnr.add(databaseOptions);
    databaseOptions.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent event) {
        int databasePos = databaseOptions.getSelectedIndex();
        selectedDatabaseType = databaseTypeOptions[databaseOptions.getSelectedIndex()];
        boolean contCreation = true;
        // gui.newBorder("");
        errorMsg.setVisible(false);
        if (type == PaneType.LAST_RUN) {
          dataShow.setText(null);
          runBtn.setEnabled(false);
        }
        userInputComponents = new ArrayList<>();
        if (type == PaneType.COMPARE_WITH_SNAPSHOT
            && !FileHandler.fileExists(selectedDatabaseType.getType() + "_" + FileHandler.databaseSnapshotFileName)
            && databasePos != 0) {
          errorMsg.setText("Unable to do comparison: " + selectedDatabaseType.getType()
              + " snapshot does not exist. Please run a database snapshot first.");
          errorMsg.setVisible(true);
          contCreation = false;
        }
        for (JPanel inputForm : inputForms) {
          if (databasePos == 0 || !contCreation) {
            inputForm.removeAll();
          } else {
            createComponents(inputForm, selectedDatabaseType.getInputs(), userInputComponents);
          }
          inputForm.revalidate();
          inputForm.repaint();
        }
        executeBtn.setEnabled(false);
      }
    });
    createTabBody();
    add(body, BorderLayout.CENTER);
  }

  private void createTabBody() {
    JPanel mainBody, inputBody1, inputBody2, bodyFooter, inputs, buttons = new JPanel(new GridLayout(1, 4));
    // create common panels
    body = new JPanel(new BorderLayout());
    mainBody = new JPanel(new GridLayout(0, 1));
    inputBody1 = new JPanel(new GridLayout(0, 2));
    bodyFooter = new JPanel(new GridLayout(0, 1));
    inputForms.add(inputBody1);
    inputs = type == PaneType.COMPARE_WITH_DB ? new JPanel(new GridLayout(0, 2)) : new JPanel(new GridLayout(0, 1));
    // add panel content and speicific panels
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
        // cpnBtn.add(runBtn);
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
        // cpnBtn.add(runBtn);
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
    }
    // cpnBtn.add(executeBtn);
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