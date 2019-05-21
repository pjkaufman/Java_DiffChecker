package dbdiffchecker;

import java.awt.event.ActionEvent;
import java.sql.SQLException;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

/**
 * MySQLCompare is a JFrame that takes user input to make a comparison between 1
 * devDatabase and a devDatabase snapshot or to take a devDatabase snapshot.
 * @author Peter Kaufman
 * @version 5-11-19
 * @since 9-20-17
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
   * Initializes a MySQLCompare object with a title and text for the its button.
   * @author Peter Kaufman
   * @param type The type of JFrame to create.
   */
  public MySQLCompare(int type) {
      super(type);
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
  protected DbConn createDevDatabaseConnection() throws SQLException {
    return new MySQLConn(devUsername.getText(), new String(devPassword.getPassword()),
      devHost.getText(), devPort.getText(), devDatabaseName.getText(), "dev");
  }

  @Override
  protected DbConn createLiveDatabaseConnection() throws SQLException {
    return new MySQLConn(liveUsername.getText(), new String(livePassword.getPassword()),
      liveHost.getText(), livePort.getText(), liveDatabaseName.getText(), "live");
  }
}
