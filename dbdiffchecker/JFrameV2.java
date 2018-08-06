package dbdiffchecker;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.border.TitledBorder;

/**
 * JFrameV2 is a JFrame that has all of the common methods that any JFrame in this package uses.
 * @author Peter Kaufman
 * @version 5-15-18
 * @since 5-14-18
 */
public class JFrameV2 extends JFrame {
  // Defualt instance variables
  protected StopWatch sw = new StopWatch();
  protected Font myFont;
  protected boolean error = true;
  protected ArrayList<String> sql = new ArrayList<>();
  protected ArrayList<Component> cpnr = new ArrayList<>(); 
  protected ArrayList<Component> cpnt = new ArrayList<>();
  protected JProgressBar pb = new JProgressBar();
  protected String clase = "None";
  protected TitledBorder nBorder = null;

  /**
   * Sets up several JFrame settings and defualt listeners.
   * @author Peter Kaufman
   */
  public JFrameV2() {
    // set JFrame properties
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    setLocation(new Point(200, 200));
    setIconImage(new ImageIcon(getClass().getResource("/Images/DBCompare.png")).getImage());
    // set component properties
    pb.setVisible(false);
    // add listeners
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent evt) {
        formWindowClosing(evt);
      }
    });
    addComponentListener(new ComponentListener() {
      public void componentResized(ComponentEvent e) {

        double width = e.getComponent().getWidth();
        Font title = new Font("Tahoma", Font.BOLD, 14);
        Font reg = new Font("Tahoma", Font.PLAIN, 12);
        if (width >= 419  && (clase.contains("DBCompare1")
            || clase.contains("DB_Diff_Checker_GUI"))) {

          title = new Font("Tahoma", Font.BOLD, (int)(width / 25));
          reg = new Font("Tahoma", Font.PLAIN, (int)(width / 25) - 2);
        } else if (width >= 660 && clase.contains("Result")) {

          title = new Font("Tahoma", Font.BOLD, (int)(width / 33));
          reg = new Font("Tahoma", Font.PLAIN, (int)(width / 46));
        } else if (width >= 660 && clase.contains("DBCompare2")) {

          title = new Font("Tahoma", Font.BOLD, (int)(width / 25));
          reg = new Font("Tahoma", Font.PLAIN, (int)(width / 56));
        }
        for (Component cpn : cpnr) {

          cpn.setFont(reg);
        }
        for (Component cpn : cpnt) {

          cpn.setFont(title);
        }
        myFont = reg;
      }
      
      public void componentHidden(ComponentEvent e) {}

      public void componentShown(ComponentEvent e) {}

      public void componentMoved(ComponentEvent e) {}
    });
  }

  /**
   * Opens a JFrame with an the error message provided as a paramater.
   * @author Peter Kaufman
   * @param error The exception which contains a user friendly messand the error
   *     that is the cause. 
   */
  protected void error(DatabaseDiffernceCheckerException error) {
    new ErrorPopup(error);
    this.error = true;
  }

  /**
   * Opens a JFrame with the result of the comparison.
   * @author Peter Kaufman
   * @param db The connection for the live database.
   */
  protected void displayResult(DbConn db) {
    Result rs = new Result(db);
    rs.results(this.sql, "Run the following SQL to make the two databases the same:");
  }

  /**
   * Opens the start JFrame when the form is closing if an error has occurred 
   * or the application is not to shutdown from this JFrame.
   * @author Peter Kaufman
   * @param evt The JFrame closing.
   */
  protected void formWindowClosing(WindowEvent evt) {
    if (error) {

      DB_Diff_Checker_GUI start = new DB_Diff_Checker_GUI();
      start.setVisible(true);
    }
  }

  /**
   * Takes and sets the new title for the progressbar's border.
   * @author Peter Kaufman
   * @param title The new name of the titled borders.
   */
  protected void newBorder(String title) {

    nBorder = BorderFactory.createTitledBorder(title);
    nBorder.setTitleFont(myFont);
    pb.setBorder(nBorder);
  }

  /**
   * log takes two Strings and writes the first to either stdLog stdErr.
   * @author Peter Kaufman
   * @param info The data to be logged.
   * @throws DatabaseDiffernceCheckerException Error logging data to a file.
   */
  protected void log(String info) throws DatabaseDiffernceCheckerException {
    try {
      FileHandler.writeToFile(info);
    } catch (IOException e) {
      throw new DatabaseDiffernceCheckerException("There was an error writing to the logs.", e);
    }
  }

  /**
    * Closes the current JFrame object.
    * @author Peter Kaufman
    */
  protected void close() {
    // closes the window activating the formWindowClosing method
    this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
  }

  /**
    * Gets the progressBar ready by reseting the StopWatch object and determines 
    * which settings to turn on.
    * @author Peter Kaufman
    * @param title The title for the border of the progressBar.
    * @param indeterminate Whether or not the progressBar is to be indeterminate.
    */
  protected void prepProgressBar(String title, boolean indeterminate) {
    newBorder(title);
    pb.setIndeterminate(indeterminate);
    if (!indeterminate) {
      pb.setValue(0);
      pb.setStringPainted(true);
    }
    pb.setVisible(true);
    sw.reset();
  }

  /**
    * Stops the progressBar, sets the border to the given String, and then hides the progressBar.
    * @author Peter Kaufman
    * @param title The title for the border of the progressBar
    */
  protected void endProgressBar(String title) {
    newBorder(title);
    if (pb.isIndeterminate()) {
      pb.setIndeterminate(false);
    } else {
      pb.setValue(100);
    }
    pb.setVisible(false);
  }
}
