package dbdiffchecker;

import java.awt.event.ActionEvent;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;
import dbdiffchecker.sql.MySQLConn;

/**
 * A JFrame that takes user input to make a comparison between two databases or
 * to take a database snapshot.
 * @author Peter Kaufman
 * @version 1-6-20
 * @since 5-21-19
 */
public class MySQLCompare extends DBCompare {
  // Instance variables
  private JTextField liveHost = new JTextField(10);
  private JTextField livePort = new JTextField(10);
  private JTextField liveUsername = new JTextField(10);
  private JTextField devHost = new JTextField(10);
  private JTextField devPort = new JTextField(10);
  private JTextField devUsername = new JTextField(10);
  private JPasswordField devPassword = new JPasswordField(10);
  private JPasswordField livePassword = new JPasswordField(10);

  /**
   * Sets the title and text for its components based on the type of comparison
   * occurring.
   * @author Peter Kaufman
   * @param type The type of comparrison to perform.
   */
  public MySQLCompare(int type) {
    super(type);
    implimentation = 0;
    labelText = new String[] { "Enter MySQL Username:", "Enter MySQL Password:", "Enter MySQL Host:",
        "Enter MySQL Port:", "Enter MySQL Database:" };
    devDatabaseInputs = new JTextComponent[] { devUsername, devPassword, devHost, devPort, devDatabaseName };
    livevDatabaseInputs = new JTextComponent[] { liveUsername, livePassword, liveHost, livePort, liveDatabaseName };
    initComponents();
    salt = "MySQL";
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
  protected DbConn createDevDatabaseConnection() throws DatabaseDifferenceCheckerException {
    return new MySQLConn(devUsername.getText(), new String(devPassword.getPassword()), devHost.getText(),
        devPort.getText(), devDatabaseName.getText(), "dev");
  }

  @Override
  protected DbConn createLiveDatabaseConnection() throws DatabaseDifferenceCheckerException {
    return new MySQLConn(liveUsername.getText(), new String(livePassword.getPassword()), liveHost.getText(),
        livePort.getText(), liveDatabaseName.getText(), "live");
  }
}
