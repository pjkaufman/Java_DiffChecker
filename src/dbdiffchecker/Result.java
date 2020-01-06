package dbdiffchecker;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

/**
 * A JFrame that shows the provided data to the user.
 * @author Peter Kaufman
 * @version 6-15-19
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
   * Determines how to display info based on whether or not the database
   * connection is null.
   * @author Peter Kaufman
   * @param db Allows for connecting to the live database.
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
   * Takes a list of data and makes them visible to the user inside of a text
   * area.
   * @author Peter Kaufman
   * @param SQL SQL statements.
   * @param title Determines what the title label's text will be.
   */
  public void results(ArrayList<String> SQL, String title) {
    try {
      if (SQL.isEmpty()) {
        this.setSize(300, 75);
        if (this.db != null) {
          instructLabel.setText("The databases are in sync.");
          btnRun.setVisible(false);
        }
      } else {
        this.sql = SQL;
        for (String statement : SQL) {
          SQLShow.append(statement + "\n");
        }
        if (title.equals("Run the following SQL to make the two databases the same:")) {
          FileHandler.writeToFile(SQL);
        }
        instructLabel.setText(title);
        this.setSize(600, 210);
      }
      this.setVisible(true);
    } catch (DatabaseDifferenceCheckerException cause) {
      error(cause);
    } 
  }

  @Override
  protected void initComponents() {
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
      @Override
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
        db.establishDatabaseConnection();
        for (int i = 0; i < sql.size(); i++) {
          temp = sql.get(i);
          db.runStatement(temp);
          publish(i);
        }
        db.closeDatabaseConnection();
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
          if (e instanceof DatabaseDifferenceCheckerException) {
            error((DatabaseDifferenceCheckerException) e);
          } else {
            error(new DatabaseDifferenceCheckerException(e.getMessage().substring(e.getMessage().indexOf(":") + 1), e, 1008));
          }
        }
      }

      @Override
      protected void process(List<Integer> chunks) {
        btnRun.setVisible(false);
        newBorder("Running SQL.. ");
        pb.setValue((int) ((chunks.get(chunks.size() - 1) + 1.0) * 100 / sql.size()));
        pb.setString(pb.getPercentComplete() * 100 + "%");
      }
    };
    swingW.execute();
  }
}
