package dbdiffchecker;

import java.awt.event.ActionEvent;
import java.io.File;
import java.sql.SQLException;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;
import dbdiffchecker.sql.SQLiteConn;
import dbdiffchecker.sql.SQLDatabase;

/**
 * A JFrame that takes user input to make a comparison between two databases or
 * to take a database snapshot.
 * @author Peter Kaufman
 * @version 5-24-19
 * @since 9-20-17
 */
public class SQLiteCompare extends DBCompare {
  // Instance variables
  private JTextField devPath = new JTextField(10);
  private JTextField livePath = new JTextField(10);

  /**
   * Sets the title and text for its components based on the type of comparison
   * occurring.
   * @author Peter Kaufman
   * @param type The type of comparrison to perform.
   */
  public SQLiteCompare(int type) {
    super(type);
    labelText = new String[] { "Enter SQLite Database Path:", "Enter SQLite Database:" };
    devDatabaseInputs = new JTextComponent[] { devPath, devDatabaseName };
    livevDatabaseInputs = new JTextComponent[] { livePath, liveDatabaseName };
    initComponents();
    salt = "SQLite";
  }

  @Override
  protected void databaseConnection1btnActionPerformed(ActionEvent evt) {
    if (allFieldsFilledOut()) {
      fixDatabasePaths();
      this.error = false;
      switch (type) {
      case 0:
      case 1:
        getSequelStatementsInBackground();
        break;
      case 2:
        takeSnapshot();
        break;
      }
    } else {
      headT.setText("Please do not leave any fields blank.");
    }
  }

  @Override
  protected DbConn createDevDatabaseConnection() throws DatabaseDiffernceCheckerException {
    return new SQLiteConn(devPath.getText(), devDatabaseName.getText(), "dev");
  }

  @Override
  protected DbConn createLiveDatabaseConnection() throws DatabaseDiffernceCheckerException {
    return new SQLiteConn(livePath.getText(), liveDatabaseName.getText(), "dev");
  }

  @Override
  protected Database createDevDatabase() throws DatabaseDiffernceCheckerException {
    return new SQLDatabase(devDatabaseConnection);
  }

  @Override
  protected Database createLiveDatabase() throws DatabaseDiffernceCheckerException {
    return new SQLDatabase(liveDatabaseConnection);
  }

  /**
   * Takes the values of both of the user input paths and makes sure that they end
   * with a file separator if they do not already.
   */
  private void fixDatabasePaths() {
    String devPathText = devPath.getText();
    String livePathText = livePath.getText();
    if (!devPathText.endsWith(File.separator)) {
      devPath.setText(devPathText + File.separator);
    }
    if (!livePathText.endsWith(File.separator)) {
      livePath.setText(livePathText + File.separator);
    }
  }
}
