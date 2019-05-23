import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;
import java.io.IOException;
import java.util.ArrayList;
import dbdiffchecker.Column;
import dbdiffchecker.Database;
import dbdiffchecker.FileHandler;
import dbdiffchecker.Index;
import dbdiffchecker.Table;
import dbdiffchecker.MySQLTable;

/**
 * A unit test that makes sure that the FileHandler object works as intended.
 * @author Peter Kaufman
 * @version 5-23-19
 * @since 5-11-19
 */
public class FileHandlerTest {
  private Table table1, table2;
  private String name, create;

  @Test
  /**
   * Tests whether the writting and reading functions work as intended.
   * @author Peter Kaufman
   */
  public void testWriteToAndReadFromFile() {
    String data = "Test addition of data", data2 = "Another addition of text...", data3 = "erroneous...";
    ArrayList<String> fileContents = new ArrayList<>(), fileContents2 = new ArrayList<>(),
        dataToAdd = new ArrayList<>();
    // log file test
    try {
      FileHandler.writeToFile(data);
      fileContents = FileHandler.readFrom(FileHandler.logFileName);
      assertEquals("The file should contain one element after the first addition", 1, fileContents.size());
      assertEquals("The file should contain the added content", true, fileContents.get(0).contains(data));
      FileHandler.writeToFile(data2);
      fileContents = FileHandler.readFrom(FileHandler.logFileName);
      assertEquals("The file should contain two element after the second addition", 2, fileContents.size());
      assertEquals("The file should contain the added content", true, fileContents.get(0).contains(data));
      assertEquals("The file should contain the added content", true, fileContents.get(1).contains(data2));
      assertEquals("The file should not contain content not added", false, fileContents.get(0).contains(data3));
      assertEquals("The file should not contain content not added", false, fileContents.get(1).contains(data3));
    } catch (IOException e) {
      fail("An IO error occured during the log test: " + e.getMessage());
    }
    // lastRun test
    try {
      dataToAdd.addAll(fileContents);
      FileHandler.writeToFile(dataToAdd);
      fileContents2 = FileHandler.readFrom(FileHandler.lastSequelStatementFileName);
      assertEquals("The file should contain the same amount of elements as were written to be written to the file",
          fileContents.size(), fileContents2.size());
      assertEquals("The file should be equal to the content that was to be added", true,
          fileContents.equals(fileContents2));
      dataToAdd.add("An addition was made");
      FileHandler.writeToFile(dataToAdd);
      fileContents = FileHandler.readFrom(FileHandler.lastSequelStatementFileName);
      assertEquals("The file should contain the same amount of elements as the data to be added", dataToAdd.size(),
          fileContents.size());
      assertEquals("The file should contain the added content", true, fileContents.equals(dataToAdd));
      assertEquals("The file should only equal what was added to it in its entirity", false,
          fileContents2.equals(dataToAdd));
    } catch (IOException e) {
      fail("An IO error occured during the lastRun test: " + e.getMessage());
    }
  }

  @Test
  /**
   * Tests whether the fileExists function works as intended.
   * @author Peter Kaufman
   */
  public void testFileExists() {
    String fileName = "eroniousFile.test";
    assertEquals("The file should not exist because it has not been created", false, FileHandler.fileExists(fileName));
    fileName = FileHandler.logFileName;
    assertEquals("The file should exist because it has been created in the previous test", true,
        FileHandler.fileExists(fileName));
    fileName = FileHandler.lastSequelStatementFileName;
    assertEquals("The file should exist because it has been created in the previous test", true,
        FileHandler.fileExists(fileName));
  }

  @Test
  /**
   * Tests whether the serialization functions work as intended.
   * @author Peter Kaufman
   */
  public void testSerialization() {
    ArrayList<String> sql;
    Database db = new Database();
    String expectedSQL = "ALTER TABLE `ci_sessions`\nCHARACTER SET latin1, \nDROP INDEX `delete`, "
        + "\nADD COLUMN `id` varchar(40) NOT NULL AFTER `data`, \nMODIFY COLUMN `ip_address` varchar(45) NOT NULL, "
        + "\nMODIFY COLUMN `timestamp` int(10) unsigned NOT NULL DEFAULT \'0\', \nDROP COLUMN `data2`, "
        + "\nADD INDEX `add` (`id`), \nDROP INDEX `modify`, \nADD INDEX `modify` (`data`);";
    // setup tables
    name = "ci_sessions";
    create = "CREATE TABLE `ci_sessions` (\n  `id` varchar(40) NOT NULL,\n  "
        + "`ip_address` varchar(45) NOT NULL,\n  `timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  "
        + "`data` blob NOT NULL,\n  KEY `add` (`id`)\n  KEY `modify` (`data`)\n  "
        + "KEY `leave` (`data`, `id`)\n  PRIMARY KEY (`id`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    table1 = new MySQLTable(name, create);
    create = "CREATE TABLE `ci_sessions` (\n  `ip_address` varchar(40) NOT NULL,\n  "
        + "`timestamp` int(11) unsigned NOT NULL DEFAULT \'0\',\n  `data` blob NOT NULL,\n  "
        + "`data2` blob NOT NULL,\n  UNIQUE KEY `delete` (`id`)\n  KEY `modify` (`data`,`ip_address`)\n  "
        + "KEY `leave` (`data`, `id`)  \n  PRIMARY KEY (`id`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin2";
    table2 = new MySQLTable(name, create);
    db.getTables().put(table2.getName(), table2);
    try {
      // serialize table2
      FileHandler.serializeDatabase(db, "");
      // deserialize table2
      db = FileHandler.deserailizDatabase("");
      table2 = db.getTables().get(table2.getName());
    } catch (Exception e) {
      fail("There was an error with serializing or deserializing the database");
    }
    // do comparison with equals
    sql = table1.equals(table2);
    assertEquals("The sql generated should add a column, drop a column, modify two columns, drop two indexes,"
        + " add two indexes, add a charset", expectedSQL, sql.get(0));
  }
}
