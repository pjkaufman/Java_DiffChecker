package dbdiffchecker;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

/**
 * Result is a JFrame that shows the provided data to the user.
 * @author Peter Kaufman
 * @version 5-11-19
 * @since 9-20-17
 */
public class Result extends JFrameV2 {
        // Instance variables
  private DbConn db = null;
  private JScrollPane SQL = new JScrollPane();
  private JTextArea SQLShow = new JTextArea(5, 20);
  private JButton btnRun = new JButton("Run");
  private JLabel instructLabel = new JLabel("Run the following SQL to make the two databases the same:");

  /**
   * Initializes a Result object and determines how to display info based on whether or not the
   * DbConn object is null.
   * @author Peter Kaufman
   * @param db Allows for connectio to the live database.
   */
  public Result(DbConn db) {

    this.db = db;
    initComponents();
    if (db == null) {

      btnRun.setVisible(false);
    }
    clase = this.getClass().getName();
  }

  /**
   * Sets up the GUI Layout, sets up all action events, and initializes instance variables.
   * @author Peter Kaufman
   */
  private void initComponents() {
    // add components to the appropriate ArrayList
    cpnt.add(instructLabel);
    cpnr.add(SQLShow);
    cpnr.add(btnRun);
    // set up JFrame properties
    setTitle("SQL To Run");
    setMinimumSize(new Dimension(600, 210));
    // set component properties
    instructLabel.setFont(new Font("Tahoma", 1, 18));
    SQL.setAutoscrolls(true);
    SQLShow.setEditable(false);
    SQL.setViewportView(SQLShow);
    // add listeners
    btnRun.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        btnRunActionPerformed(evt);
      }
    });
    // add components
    getContentPane().setLayout(new BorderLayout());
    add(instructLabel, BorderLayout.NORTH);
    add(SQL, BorderLayout.CENTER);
    add(btnRun, BorderLayout.EAST);
    add(pb, BorderLayout.SOUTH);
    pack();
  }

  /**
   * Runs the SQL to update the live database.
   * @author Peter Kaufman
   * @param evt btnRun button being clicked.
   */
  private void btnRunActionPerformed(ActionEvent evt) {

    btnRun.setVisible(false);
    prepProgressBar("Waiting On Action", false);
    SwingWorker<Boolean, Integer> swingW = new SwingWorker<Boolean, Integer>() {

      @Override
      protected Boolean doInBackground() throws Exception {

        // boolean cont = true;
        String temp = null;
        sw.start();
        for (int i = 0; i < sql.size(); i++) {

          temp = sql.get(i);
          db.runSequelStatement(temp);
          publish(i);
        }
        sw.stop();
        log("Ran SQL in " + sw.getElapsedTime().toMillis() / 1000.0 + "s with no errors.");

        return true;
      }

      @Override
      protected void done() {
        try {
          get();
          instructLabel.setText("The database has been updated.");
          endProgressBar("Done");
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
      protected void process(List<Integer> chunks) {

        btnRun.setVisible(false);
        newBorder("Running SQL.. ");
        pb.setValue((int)((chunks.get(chunks.size() - 1) + 1.0) * 100 / sql.size()));
        pb.setString(pb.getPercentComplete() * 100 + "%");
      }

    };

    swingW.execute();
  }

  /**
    * Takes in an ArrayList of Strings and adds them to the JTextArea.
    * @author Peter Kaufman
    * @param SQL The SQL statements.
    * @param title Determines what the title JLabel's text will be.
    */
  public void results(ArrayList<String> SQL, String title) {
    try{
      if (SQL.isEmpty()) {

        this.setSize(300, 75);
        if (this.db != null) {

          instructLabel.setText("The databases are in sync.");
        }
      } else {

        this.sql = SQL;
        for (String statement: SQL) {

          SQLShow.append(statement + "\n");
        }

        if (title.equals("Run the following SQL to make the two databases the same:")) {

          FileHandler.writeToFile(SQL);
        }
        instructLabel.setText(title);
        this.setSize(600, 210);
      }

      this.setVisible(true);
    } catch(IOException e) {

      error(new DatabaseDiffernceCheckerException("There was an error" 
          + " writing the SQL statement(s) to a file.", e));
    }
  }
}
