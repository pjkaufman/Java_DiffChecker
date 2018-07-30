package dbdiffchecker;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.border.TitledBorder;

/**
 * DBCompare1 is a JFrame that takes user input to make a comparison between 1
 * database and a database snapshot or to take a database snapshot.
 * @author Peter Kaufman
 * @version 5-24-18
 * @since 9-20-17
 */
public class DBCompare1 extends JFrameV2 {
  // Instance variables
  private DbConn databaseConnection1;
  private DbConn databaseConnection2;
  private Database database1; 
  private Database database2;
  private HashMap<String, String> updateTables = new HashMap<>();
  private JButton databaseConnection1btn = new JButton("Compare");
  private JTextField database = new JTextField(10); 
  private JTextField host = new JTextField(10);
  private JTextField port = new JTextField(10);
  private JTextField username = new JTextField(10);
  private JLabel usernameLabel = new JLabel("Enter MySQL username:");
  private JLabel passLabel = new JLabel("Enter MySQL Password:");
  private JLabel hostLabel = new JLabel("Enter MySQL Host:");
  private JLabel portLabel = new JLabel("Enter MySQL Port:");
  private JLabel databaseLabel = new JLabel("Enter MySQL Database:");
  private JLabel headT = new JLabel("Enter Database Information Below", TitledBorder.CENTER);
  private JPasswordField password = new JPasswordField(10);
  private JPanel header = new JPanel(new BorderLayout());
  private JPanel content = new JPanel(new GridLayout(5, 2));
  private JPanel footer = new JPanel(new BorderLayout());
  private JPanel part1 = new JPanel(new FlowLayout());
  private JPanel part2 = new JPanel(new FlowLayout()); 
  private JPanel part3 = new JPanel(new FlowLayout());
  private JPanel part4 = new JPanel(new FlowLayout()); 
  private JPanel part5 = new JPanel(new FlowLayout());
  private JPanel part6 = new JPanel(new FlowLayout()); 
  private JPanel part7 = new JPanel(new FlowLayout());
  private JPanel part8 = new JPanel(new FlowLayout()); 
  private JPanel part9 = new JPanel(new FlowLayout());
  private JPanel part10 = new JPanel(new FlowLayout()); 
  private JPanel footc = new JPanel(new FlowLayout());

  /**
   * DBCompare1 initializes a DBCompare1 object with a title and text for the its button.
   * @author Peter Kaufman
   * @param title the title of this JFrame.
   * @param buttonTxt the text to be displayed on the button databaseConnection1btn.
   */
  public DBCompare1(String title, String buttonTxt) {
    // use parameters to set JFrame properties
    setTitle("Take Database Snapshot");
    databaseConnection1btn.setText("Snapshot");
    initComponents();
    clase = this.getClass().getName();
  }

  /**
   * InitComonents sets up the GUI Layout, sets up all action events, and initializes
   * instance variables.
   * @author Peter Kaufmante
   */
  private void initComponents() {
    // add components to the appropriate ArrayList
    cpnt.add(headT);
    cpnr.add(host);
    cpnr.add(port);
    cpnr.add(database);
    cpnr.add(password);
    cpnr.add(usernameLabel);
    cpnr.add(databaseConnection1btn);
    cpnr.add(passLabel);
    cpnr.add(hostLabel);
    cpnr.add(portLabel);
    cpnr.add(databaseLabel);
    cpnr.add(username);
    // set up JFrame properties
    setMinimumSize(new Dimension(100, 100));
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
    part1.add(usernameLabel);
    part2.add(username);
    part3.add(passLabel);
    part4.add(password);
    part5.add(hostLabel);
    part6.add(host);
    part7.add(portLabel);
    part8.add(port);
    part9.add(databaseLabel);
    part10.add(database);
    content.add(part1);
    content.add(part2);
    content.add(part3);
    content.add(part4);
    content.add(part5);
    content.add(part6);
    content.add(part7);
    content.add(part8);
    content.add(part9);
    content.add(part10);
    footc.add(databaseConnection1btn);
    footer.add(footc, BorderLayout.CENTER);
    footer.add(pb, BorderLayout.SOUTH);
    add(header, BorderLayout.NORTH);
    add(content, BorderLayout.CENTER);
    add(footer, BorderLayout.SOUTH);
  }

  /**
   * databaseConnection1btnActionPerformed determines if the user has put in the
   * appropriate information and either 
   * takes a database snapshot or compares a database to a database snapshot.
   * @author Peter Kaufman
   * @param evt is an ActionEvent which is clicking the button databaseConnection1btn.
   */
  private void databaseConnection1btnActionPerformed(ActionEvent evt) {
    try {
      if (!(port.getText().equals("") | username.getText().equals("") 
          | new String(password.getPassword()).equals("") | host.getText().equals("") 
          | database.getText().equals(""))) {

        this.error = false;
        if (this.getTitle().equals("Compare Database to Snapshot")) {

          compare();
        } else {

          takeSnapshot();
        }
      } else {

        headT.setText("Please do not leave any fields blank.");
      }
    } catch (IOException e) {

      error(new DatabaseDiffernceCheckerException("There was an error" 
          + " with the database snapshot file.", e));
    }
  }

  /**
    * takeSnapshot takes a database snapshot based on user input.
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
          databaseConnection1 = new DbConn(username.getText(), new String(password.getPassword()),
                              host.getText(), port.getText(), database.getText(), "dev");

          publish("Gathering Database Information");
          database1 = new Database(databaseConnection1);
          publish("Writing to JSON File");
          FileConversion.writeToFile(database1);
          sw.stop();
          log("Took a DB Snapshot on " + sw.getDate() + " at " + sw.getHour() + " in " 
              + sw.getElapsedTime().toMillis() / 1000.0 + "s with no errors.");
        } catch (IOException e) {
          sw.stop();
          throw new DatabaseDiffernceCheckerException("There was an error" 
              + " when trying to take a database snapshot.", e);
        } catch (SQLException e) {
          sw.stop();
          throw new DatabaseDiffernceCheckerException("There was an error" 
              + " with the database connection. Please try again.", e);
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
            error((DatabaseDiffernceCheckerException)e);
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
  * compare compares the database specified by the user to a database snapshot.
  * @author Peter Kaufman
  * @throws IOException an error occurred while accessing the database snapshot file
  */
  private void compare() throws IOException {

    prepProgressBar("Reading in The DB Snapshot", true);
    SwingWorker<Boolean, String> swingW = new SwingWorker<Boolean, String>() {

      @Override
      protected Boolean doInBackground() throws Exception {
        try {
          publish("Reading in The DB Snapshot");
          sw.start();
          database1 = FileConversion.readFrom();
          publish("Establishing Live Database Connection");
          databaseConnection2 = new DbConn(username.getText(), new String(password.getPassword()),
                              host.getText(), port.getText(), database.getText(), "live");
          publish("Gathering Live Database Info");
          database2 = new Database(databaseConnection2);
          publish("Checking Live First Steps");
          sql.addAll(database2.getFirstSteps());
          publish("Finding Missing Or Unneccessary Tables");
          sql.addAll(database1.compareTables(database2.getTables()));
          publish("Comparing Tables");
          updateTables.putAll(database1.tablesDiffs(database2.getTables()));
          sql.addAll(database1.updateTables(database2.getTables(), updateTables));
          publish("Checking Dev First Steps");
          sql.addAll(database1.getFirstSteps());
          publish("Adding Dev's Views");
          sql.addAll(database1.updateViews(database2.getViews()));
          sw.stop();
          log("DB Snapshot Comparison Complete on " + sw.getDate() + " at " + sw.getHour() 
              + " in " + sw.getElapsedTime().toMillis() / 1000.0 + "s with no errors.");
        } catch (SQLException e) {
          sw.stop();
          throw new DatabaseDiffernceCheckerException("There was an error with" 
              + " the database connection. Please try again.", e);
        }

        return true;
      }

      @Override
      protected void done() {
        try {
          get();
          endProgressBar("Database Snapshot Comparison Complete");
          displayResult(databaseConnection2);
          close();
        } catch (Exception e) {
          endProgressBar("An Error Occurred");
          if (e instanceof DatabaseDiffernceCheckerException) {
            error((DatabaseDiffernceCheckerException)e);
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
}
