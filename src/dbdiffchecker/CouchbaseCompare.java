package dbdiffchecker;

import java.awt.event.ActionEvent;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;
import dbdiffchecker.nosql.CouchbaseConn;
import dbdiffchecker.nosql.Bucket;

/**
 * A JFrame that takes user input to make a comparison between two databases or
 * to take a database snapshot.
 * @author Peter Kaufman
 * @version 5-24-19
 * @since 5-24-19
 */
public class CouchbaseCompare extends DBCompare {
  // Instance variables
  private JTextField liveHost = new JTextField(10);
  private JTextField liveUsername = new JTextField(10);
  private JTextField devHost = new JTextField(10);
  private JTextField devUsername = new JTextField(10);
  private JPasswordField devPassword = new JPasswordField(10);
  private JPasswordField livePassword = new JPasswordField(10);

  /**
   * Sets the title and text for its components based on the type of comparison
   * occurring.
   * @author Peter Kaufman
   * @param type The type of comparrison to perform.
   */
  public CouchbaseCompare(int type) {
    super(type);
    labelText = new String[] { "Enter Couchbase Username:", "Enter Couchbase Password:", "Enter Couchbase Host:",
        "Enter Couchbase Database:" };
    devDatabaseInputs = new JTextComponent[] { devUsername, devPassword, devHost, devDatabaseName };
    livevDatabaseInputs = new JTextComponent[] { liveUsername, livePassword, liveHost, liveDatabaseName };
    initComponents();
    salt = "Couchbase";
  }

  @Override
  protected void databaseConnection1btnActionPerformed(ActionEvent evt) {
    if (allFieldsFilledOut()) {
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
    return new CouchbaseConn(devUsername.getText(), new String(devPassword.getPassword()), devHost.getText(),
        devDatabaseName.getText());
  }

  @Override
  protected DbConn createLiveDatabaseConnection() throws DatabaseDiffernceCheckerException {
    return new CouchbaseConn(liveUsername.getText(), new String(livePassword.getPassword()), liveHost.getText(),
        liveDatabaseName.getText());
  }

  @Override
  protected Database createDevDatabase() throws DatabaseDiffernceCheckerException {
    return new Bucket(devDatabaseConnection);
  }

  @Override
  protected Database createLiveDatabase() throws DatabaseDiffernceCheckerException {
    return new Bucket(liveDatabaseConnection);
  }
}
