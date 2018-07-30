package dbdiffchecker;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentListener;
import java.awt.event.ComponentEvent;
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
 * DBCompare2 is a JFrame that takes user input to make a comparison between two databases.
 * @author Peter Kaufman
 * @version 5-24-18
 * @since 9-20-17
 */
public class DBCompare2 extends JFrameV2 {
  // Instance variables
  private DbConn databaseConnection1; 
  private DbConn databaseConnection2;
  private Database dab1;
  private Database dab2;
  private HashMap<String, String> updateTables = new HashMap<>();
  private JTextField database1 = new JTextField(10); 
  private JTextField database2 = new JTextField(10);
  private JTextField host1 = new JTextField(10); 
  private JTextField host2 = new JTextField(10);
  private JTextField port1 = new JTextField(10);
  private JTextField port2 = new JTextField(10);
  private JTextField username1 = new JTextField(10);
  private JTextField username2 = new JTextField(10);
  private JButton execute = new JButton("Compare");
  private JLabel devPortLabel = new JLabel("Enter MySQL Dev Port:     ");
  private JLabel devDatabaseLabel = new JLabel("Enter MySQL Dev Database: ");
  private JLabel liveUserNameLabel = new JLabel("Enter MySQL Live Username:");
  private JLabel livePassLabel = new JLabel("Enter MySQL Live Password:");
  private JLabel liveHostLabel = new JLabel("Enter MySQL Live Host:    ");
  private JLabel livePortLabel = new JLabel("Enter MySQL Live Port:    ");
  private JLabel liveDatabaseLabel = new JLabel("Enter MySQL Live Database:");
  private JLabel headT = new JLabel("Enter The Folowing Information:");
  private JLabel devUserNameLabel = new JLabel("Enter MySQL Dev Username: ");
  private JLabel devPassLabel = new JLabel("Enter MySQL Dev Password: ");
  private JLabel devHostLabel = new JLabel("Enter MySQL Dev Host:     ");
  private JPasswordField password1 = new JPasswordField(10);
  private JPasswordField password2 = new JPasswordField(10);
  private JPanel header = new JPanel(new BorderLayout()); 
  private JPanel content = new JPanel(new BorderLayout());
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
  private JPanel part11 = new JPanel(new FlowLayout());
  private JPanel part12 = new JPanel(new FlowLayout()); 
  private JPanel part13 = new JPanel(new FlowLayout());
  private JPanel part14 = new JPanel(new FlowLayout());
  private JPanel part15 = new JPanel(new FlowLayout());
  private JPanel part16 = new JPanel(new FlowLayout());
  private JPanel part17 = new JPanel(new FlowLayout());
  private JPanel part18 = new JPanel(new FlowLayout()); 
  private JPanel part19 = new JPanel(new FlowLayout());
  private JPanel part20 = new JPanel(new FlowLayout()); 
  private JPanel c1 = new JPanel(new GridLayout(5, 2));
  private JPanel c2 = new JPanel(new GridLayout(5, 2)); 
  private JPanel footc = new JPanel(new FlowLayout());

  /**
   * DBCompare2 initializes a DBCompare2 object. 
   * @author Peter Kaufman
   */
  public DBCompare2() {
    initComponents();
    clase = this.getClass().getName();
  }

  /**
   * InitComonents sets up the GUI Layout, sets up all action events, and initializes 
   * instance variables.
   * @author Peter Kaufman
   */
  private void initComponents() {
    // add components to the appropriate ArrayList
    cpnt.add(headT);
    cpnt.add(execute);
    cpnr.add(devUserNameLabel);
    cpnr.add(devPassLabel);
    cpnr.add(devHostLabel);
    cpnr.add(devPortLabel);
    cpnr.add(devDatabaseLabel);
    cpnr.add(liveUserNameLabel);
    cpnr.add(livePassLabel);
    cpnr.add(liveHostLabel);
    cpnr.add(livePortLabel);
    cpnr.add(liveDatabaseLabel);
    cpnr.add(username1);
    cpnr.add(password1);
    cpnr.add(username2);
    cpnr.add(password2);
    cpnr.add(host1);
    cpnr.add(host2);
    cpnr.add(port1);
    cpnr.add(port2);
    cpnr.add(database1);
    cpnr.add(database2);
    // set up JFrame properties
    setMinimumSize(new Dimension(630, 325));
    setTitle("Compare Two Databases");
    // set component properties
    headT.setHorizontalAlignment(TitledBorder.CENTER);
    headT.setFont(new Font("Tahoma", 1, 24));
    execute.setFont(new Font("Tahoma", 0, 18));
    // add listeners
    addComponentListener(new ComponentListener() {
      public void componentResized(ComponentEvent e) {

        double width = e.getComponent().getWidth();
        Font title = new Font("Tahoma", Font.BOLD, 24);
        Font reg = new Font("Tahoma", Font.PLAIN, 11);
        Font button = new Font("Tahoma", Font.BOLD, 18);
        if (width >= 660) {
          title = new Font("Tahoma", Font.BOLD, (int)(width / 25));
          reg = new Font("Tahoma", Font.PLAIN, (int)(width / 56));
          button = new Font("Tahoma", Font.BOLD, (int)(width / 34));
        }

        headT.setFont(title);
        devUserNameLabel.setFont(reg);
        devPassLabel.setFont(reg);
        devHostLabel.setFont(reg);
        devPortLabel.setFont(reg);
        devDatabaseLabel.setFont(reg);
        liveUserNameLabel.setFont(reg);
        livePassLabel.setFont(reg);
        liveHostLabel.setFont(reg);
        livePortLabel.setFont(reg);
        liveDatabaseLabel.setFont(reg);
        username1.setFont(reg);
        password1.setFont(reg);
        username2.setFont(reg);
        password2.setFont(reg);
        host1.setFont(reg);
        host2.setFont(reg);
        port1.setFont(reg);
        port2.setFont(reg);
        database1.setFont(reg);
        database2.setFont(reg);
        execute.setFont(button);
        myFont = reg;
      }

      public void componentHidden(ComponentEvent e) {}

      public void componentShown(ComponentEvent e) {}

      public void componentMoved(ComponentEvent e) {}
    });
    execute.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        executeActionPerformed(evt);
      }
    });
    // add components
    header.add(headT, BorderLayout.CENTER);
    part1.add(devUserNameLabel);
    part2.add(liveUserNameLabel);
    part3.add(devPassLabel);
    part4.add(livePassLabel);
    part5.add(devHostLabel);
    part6.add(liveHostLabel);
    part7.add(devPortLabel);
    part8.add(livePortLabel);
    part9.add(devDatabaseLabel);
    part10.add(liveDatabaseLabel);
    part11.add(username1);
    part12.add(username2);
    part13.add(password1);
    part14.add(password2);
    part15.add(host1);
    part16.add(host2);
    part17.add(port1);
    part18.add(port2);
    part19.add(database1);
    part20.add(database2);
    c1.add(part1);
    c1.add(part11);
    c2.add(part2);
    c2.add(part12);
    c1.add(part3);
    c1.add(part13);
    c2.add(part4);
    c2.add(part14);
    c1.add(part5);
    c1.add(part15);
    c2.add(part6);
    c2.add(part16);
    c1.add(part7);
    c1.add(part17);
    c2.add(part8);
    c2.add(part18);
    c1.add(part9);
    c1.add(part19);
    c2.add(part10);
    c2.add(part20);
    content.add(c1, BorderLayout.WEST);
    content.add(c2, BorderLayout.EAST);
    footc.add(execute);
    footer.add(footc, BorderLayout.CENTER);
    footer.add(pb, BorderLayout.SOUTH);
    getContentPane().setLayout(new BorderLayout());
    add(header, BorderLayout.NORTH);
    add(content, BorderLayout.CENTER);
    add(footer, BorderLayout.SOUTH);
  }

  /**
    * executeActionPerformed determines whether the information supplied 
    * by the user is appropriate, if so two databases are compared otherwise
    * a message is displayed to the user.
    * @author Peter Kaufman
    * @param evt is an ActionEvent which results when the compare button is clicked.
    */
  private void executeActionPerformed(ActionEvent evt) {
    if (!(port1.getText().equals("") | port2.getText().equals("") 
        | username1.getText().equals("") | username2.getText().equals("") 
        | new String(password1.getPassword()).equals("") | new String(password2.getPassword()).equals("") 
        | host1.getText().equals("") | host2.getText().equals("")
        | database1.getText().equals("") | database2.getText().equals(""))) {

      this.error = false;
      compare();
    } else {

      headT.setText("Please do not leave any fields blank.");
    }
  }

  /**
    * compare compares two databases based on user input.
    * @author Peter Kaufman
    */
  private void compare() {

    prepProgressBar("Establishing Dev Database Connection", true);
    SwingWorker<Boolean, String> swingW = new SwingWorker<Boolean, String>() {

      @Override
      protected Boolean doInBackground() throws Exception {
        try {
          publish("Establishing Dev Database Connection");
          sw.start();
          databaseConnection1 = new DbConn(username1.getText(),
              new String(password1.getPassword()), host1.getText(), port1.getText(),
              database1.getText(), "dev");
          publish("Gathering Dev Database Info");
          dab1 = new Database(databaseConnection1);
          publish("Establishing Live Database Connection");
          databaseConnection2 = new DbConn(username2.getText(),
              new String(password2.getPassword()), host2.getText(), port2.getText(),
              database2.getText(), "live");
          publish("Gathering Live Database Info");
          dab2 = new Database(databaseConnection2);
          publish("Checking Live First Steps");
          sql.addAll(dab2.getFirstSteps());
          publish("Comparing Tables");
          sql.addAll(dab1.compareTables(dab2.getTables()));
          publish("Comparing Tables");
          updateTables.putAll(dab1.tablesDiffs(dab2.getTables()));
          sql.addAll(dab1.updateTables(dab2.getTables(), updateTables));
          publish("Checking Dev First Steps");
          sql.addAll(dab1.getFirstSteps());
          publish("Adding Dev's Views");
          sql.addAll(dab1.updateViews(dab2.getViews()));
          sw.stop();
          log("DB Comparison Complete on " + sw.getDate() + " at " + sw.getHour() + " in " 
              + sw.getElapsedTime().toMillis() / 1000.0 + "s with no errors.");
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
          endProgressBar("Database Comparison Complete");
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
