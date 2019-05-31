import static org.junit.Assert.assertEquals;
import org.junit.Test;
import java.util.ArrayList;
import dbdiffchecker.sql.MySQLTable;

/**
 * A unit test that makes sure that the MySQLTable object works as intended.
 * @author Peter Kaufman
 * @version 5-31-19
 * @since 5-10-19
 */
public class MySQLTableTest {
  private MySQLTable table1, table2;
  private String name, create, collation, charSet, autoIncrement, expectedSQL;
  private ArrayList<String> sql;

  @Test
  /**
   * Tests whether the get statements inside of the MySQLTable object work as
   * intended.
   * @author Peter Kaufman
   */
  public void testGetStatements() {
    name = "bloat";
    create = "CREATE TABLE `bloat` (\n  `bloatware` int(11) NOT NULL,\n  PRIMARY KEY (`bloatware`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    charSet = "latin1";
    autoIncrement = "17";
    table1 = new MySQLTable(name, create);
    collation = "latin1_swedish_c";
    table1.setCollation(collation);
    table1.setAutoIncrement(autoIncrement);
    // start assertions
    assertEquals("The name of the table should be the one passed into the constructor", name, table1.getName());
    assertEquals("The create statement of the table should be the one passed into the constructor", create + ";",
        table1.getCreateStatement());
    assertEquals("The charSet of the table should be the same one from the create statement", charSet,
        table1.getCharSet());
    assertEquals("The autoIncrement value of the table should be equal to the one passed to setAutoIncrement",
        autoIncrement, table1.getAutoIncrement());
    assertEquals("The collation of the table should be equal to the one passed to setCollation", collation,
        table1.getCollation());
    autoIncrement = "19";
    table1.setAutoIncrement(autoIncrement);
    assertEquals("The autoIncrement value of the table should be equal to the one passed to setAutoIncrement",
        autoIncrement, table1.getAutoIncrement());
    charSet = "latin12";
    table1.setCharSet(charSet);
    assertEquals("The charSet of the table should be the same one passed into setCharSet", charSet,
        table1.getCharSet());
  }

  @Test
  /**
   * Tests whether the parsin function works as intended.
   * @author Peter Kaufman
   */
  public void testAddColumn() {
    String column1 = "bloatware", column2 = "shipmentID";
    name = "bloat";
    create = "CREATE TABLE `bloat` (\n  `bloatware` int(11) NOT NULL\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    table1 = new MySQLTable();
    assertEquals("The size of the column list for the table should be 0 when empty", 0, table1.getColumns().size());
    // make sure the first column has been added
    table1 = new MySQLTable(name, create);
    assertEquals("The size of the column list for the table should be 1 when one column has been added", 1,
        table1.getColumns().size());
    assertEquals("The column passed to addColumn should be in the tables column list", true,
        table1.getColumns().containsKey(column1));
    create = "CREATE TABLE `bloat` (\n  `bloatware` int(11) NOT NULL,\n  `shipmentID` int(11) NOT NULL\n"
        + ") ENGINE=InnoDB DEFAULT CHARSET=latin1";
    // make sure the second column has been added
    table1 = new MySQLTable(name, create);
    assertEquals(
        "The size of the column list for the table should be 2 when two colmns have been added to the column list", 2,
        table1.getColumns().size());
    assertEquals("The column passed to addColumn should be in the tables column list", true,
        table1.getColumns().containsKey(column2));
  }

  @Test
  /**
   * Tests whether the parsing function works as intended.
   * @author Peter Kaufman
   */
  public void testAddIndex() {
    String index1 = "shipment", index2 = "shipped";
    name = "shippingData";
    create = "CREATE TABLE `shippingData` (\n  `shippingID` int(11) NOT NULL,\n  `vendor` STRING(11) NOT NULL,\n"
        + "  KEY `shipment` (`shippingID`,`vendor`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    table1 = new MySQLTable();;
    assertEquals("The size of the index list for the table should be 0 when empty", 0, table1.getIndices().size());
    // make sure the first index has been added
    table1 = new MySQLTable(name, create);
    assertEquals("The size of the column list for the table should be 1 when one index has been added", 1,
        table1.getIndices().size());
    assertEquals("The index passed to addIndex should be in the tables index list", true,
        table1.getIndices().containsKey(index1));
    // make sure the second index has been added
    create = "CREATE TABLE `shippingData` (\n  `shippingID` int(11) NOT NULL,\n  `vendor` STRING(11) NOT NULL,\n"
        + "  KEY `shipment` (`shippingID`,`vendor`),\n  KEY `shipped` (`shippingID`)"
        + "\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    table1 = new MySQLTable(name, create);;
    assertEquals(
        "The size of the index list for the table should be 2 when two indices have been added to the column list", 2,
        table1.getIndices().size());
    assertEquals("The index passed to addIndex should be in the tables index list", true,
        table1.getIndices().containsKey(index2));
  }

  @Test
  /**
   * Tests whetehr the parsing function adds Foreign Keys
   * @author Peter Kaufman
   */
  public void testAddForeignKey() {
    name = "products";
    create = "CREATE TABLE `products` (\n  `prd_id` int not null auto_increment primary key,\n"
        + "  `prd_name` varchar(355) not null,\n  `prd_price` decimal,\n  `cat_id` int not null,\n  "
        + "CONSTRAINT `constraint_name`\n  FOREIGN KEY `fk_cat`(`cat_id`)\n  REFERENCES `categories`"
        + "(`cat_id`)\n  ON UPDATE CASCADE\n  ON DELETE RESTRICT\n  )ENGINE=InnoDB";
    table1 = new MySQLTable(name, create);
    assertEquals("There should be a Foreign Key in the index list", true, table1.getIndices().containsKey("fk_cat"));
  }

  @Test
  /**
   * Tests whether the equals function works as intended.
   * @author Peter Kaufman
   */
  public void testEquals() {
    expectedSQL = "ALTER TABLE `ci_sessions`\nCHARACTER SET latin1, \nDROP INDEX `delete`, "
        + "\nADD COLUMN `id` varchar(40) NOT NULL, \nMODIFY COLUMN `ip_address`"
        + " varchar(45) NOT NULL AFTER `id`, \nMODIFY COLUMN `timestamp` int(10) unsigned "
        + "NOT NULL DEFAULT \'0\' AFTER `ip_address`, \nDROP COLUMN `data2`, \nADD INDEX "
        + "`add` (`id`), \nDROP INDEX `modify`, \nADD INDEX `modify` (`data`);";
    // setup table1
    name = "ci_sessions";
    create = "CREATE TABLE `ci_sessions` (\n  `id` varchar(40) NOT NULL,\n  "
        + "`ip_address` varchar(45) NOT NULL,\n  `timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  "
        + "`data` blob NOT NULL,\n  KEY `add` (`id`),\n  KEY `modify` (`data`),\n  "
        + "KEY `leave` (`data`,`id`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    table1 = new MySQLTable(name, create);
    // setup table2
    create = "CREATE TABLE `ci_sessions` (\n  `ip_address` varchar(40) NOT NULL,\n  "
        + "`timestamp` int(11) unsigned NOT NULL DEFAULT \'0\',\n  `data` blob NOT NULL,\n  "
        + "`data2` blob NOT NULL,\n  UNIQUE KEY `delete` (`id`)\n,  KEY `modify` (`data`,`ip_address`),\n  "
        + "KEY `leave` (`data`,`id`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin2";
    table2 = new MySQLTable(name, create);
    // do comparison with equals
    sql = table1.equals(table2);
    assertEquals("The sql generated should add a column, drop a column, modify two columns, drop two indexes,"
        + " add two indexes, and add a charset", expectedSQL, sql.get(0));
  }

  @Test
  /**
   * Tests whether the equals function catches the adding of indices.
   * @author Peter Kaufman
   */
  public void testIndexAddition() {
    expectedSQL = "ALTER TABLE `ci_sessions`\nADD INDEX `add` (`id`);";
    // setup tables
    name = "ci_sessions";
    create = "CREATE TABLE `ci_sessions` (\n  `id` varchar(40) NOT NULL,\n  "
        + "`ip_address` varchar(45) NOT NULL,\n  `timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  "
        + "`data` blob NOT NULL,\n  KEY `leave` (`data`,`id`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    table1 = new MySQLTable(name, create);
    table2 = new MySQLTable(name, create);
    // do comparison with equals
    sql = table1.equals(table2);
    assertEquals("The sql generated should be empty since all indexes are the same", 0, sql.size());
    // one index add
    create = "CREATE TABLE `ci_sessions` (\n  `id` varchar(40) NOT NULL,\n  "
        + "`ip_address` varchar(45) NOT NULL,\n  `timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  "
        + "`data` blob NOT NULL,\n  KEY `leave` (`data`,`id`),\n  "
        + "KEY `add` (`id`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    table1 = new MySQLTable(name, create);
    sql = table1.equals(table2);
    assertEquals("The sql generated should add one index", expectedSQL, sql.get(0));
    // two index add
    create = "CREATE TABLE `ci_sessions` (\n  `id` varchar(40) NOT NULL,\n  "
        + "`ip_address` varchar(45) NOT NULL,\n  `timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  "
        + "`data` blob NOT NULL,\n  KEY `leave` (`data`,`id`),\n  "
        + "KEY `add` (`id`),\n  PRIMARY KEY (`id`,`ip_address`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    table1 = new MySQLTable(name, create);
    sql = table1.equals(table2);
    expectedSQL = "ALTER TABLE `ci_sessions`\nADD INDEX `add` (`id`), \nADD PRIMARY KEY (`id`,`ip_address`);";
    assertEquals("The sql generated should add two indices", expectedSQL, sql.get(0));
  }

  @Test
  /**
   * Tests whether the equals function cathces the dropping of indices.
   * @author Peter Kaufman
   */
  public void testIndexDropping() {
    expectedSQL = "ALTER TABLE `ci_sessions`\nDROP INDEX `drop1`;";
    // setup tables
    name = "ci_sessions";
    create = "CREATE TABLE `ci_sessions` (\n  `id` varchar(40) NOT NULL,\n  "
        + "`ip_address` varchar(45) NOT NULL,\n  `timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  "
        + "`data` blob NOT NULL,\n  KEY `leave` (`data`,`id`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    table1 = new MySQLTable(name, create);
    table2 = new MySQLTable(name, create);
    // do comparison with equals
    sql = table1.equals(table2);
    assertEquals("The sql generated should be empty since all indexes are the same", 0, sql.size());
    // one index drop
    create = "CREATE TABLE `ci_sessions` (\n  `id` varchar(40) NOT NULL,\n  "
        + "`ip_address` varchar(45) NOT NULL,\n  `timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  "
        + "`data` blob NOT NULL,\n  KEY `leave` (`data`,`id`),\n  "
        + "KEY `drop1` (`id`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    table1 = new MySQLTable(name, create);
    sql = table2.equals(table1);
    assertEquals("The sql generated should drop one index", expectedSQL, sql.get(0));
    // two index drop
    create = "CREATE TABLE `ci_sessions` (\n  `id` varchar(40) NOT NULL,\n  "
        + "`ip_address` varchar(45) NOT NULL,\n  `timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  "
        + "`data` blob NOT NULL,\n  KEY `leave` (`data`,`id`),\n  "
        + "KEY `drop1` (`id`),\n  KEY `drop2` (`id`,`ip_address`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    table1 = new MySQLTable(name, create);
    sql = table2.equals(table1);
    expectedSQL = "ALTER TABLE `ci_sessions`\nDROP INDEX `drop1`, \nDROP INDEX `drop2`;";
    assertEquals("The sql generated should drop two indices", expectedSQL, sql.get(0));
  }

  @Test
  /**
   * Tests whether the equals function catches the modifiying of indices.
   * @author Peter Kaufman
   */
  public void testIndexModification() {
    expectedSQL = "ALTER TABLE `ci_sessions`\nDROP INDEX `modify1`, \nADD UNIQUE INDEX `modify1` (`id`);";
    // setup tables
    name = "ci_sessions";
    create = "CREATE TABLE `ci_sessions` (\n  `id` varchar(40) NOT NULL,\n  "
        + "`ip_address` varchar(45) NOT NULL,\n  `timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  "
        + "`data` blob NOT NULL,\n  KEY `leave` (`data`,`id`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    table1 = new MySQLTable(name, create);
    table2 = new MySQLTable(name, create);
    // do comparison with equals
    sql = table1.equals(table2);
    assertEquals("The sql generated should be empty since all indexes are the same", 0, sql.size());
    // one index modify
    create = "CREATE TABLE `ci_sessions` (\n  `id` varchar(40) NOT NULL,\n  "
        + "`ip_address` varchar(45) NOT NULL,\n  `timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  "
        + "`data` blob NOT NULL,\n  KEY `leave` (`data`,`id`),\n  KEY `modify1` (`id`)\n"
        + ") ENGINE=InnoDB DEFAULT CHARSET=latin1";
    table1 = new MySQLTable(name, create);
    create = "CREATE TABLE `ci_sessions` (\n  `id` varchar(40) NOT NULL,\n  "
        + "`ip_address` varchar(45) NOT NULL,\n  `timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  "
        + "`data` blob NOT NULL,\n  KEY `leave` (`data`,`id`),\n  UNIQUE KEY `modify1` (`id`)\n"
        + ") ENGINE=InnoDB DEFAULT CHARSET=latin1";
    table2 = new MySQLTable(name, create);
    sql = table2.equals(table1);
    assertEquals("The sql generated should modify one index", expectedSQL, sql.get(0));
    // two column modify
    create = "CREATE TABLE `ci_sessions` (\n  `id` varchar(40) NOT NULL,\n  "
        + "`ip_address` varchar(45) NOT NULL,\n  `timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  "
        + "`data` blob NOT NULL,\n  KEY `leave` (`data`,`id`),\n  KEY `modify1` (`id`),\n"
        + "  KEY `modify2` (`id`,`ip_address`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    table1 = new MySQLTable(name, create);
    create = "CREATE TABLE `ci_sessions` (\n  `id` varchar(40) NOT NULL,\n  "
        + "`ip_address` varchar(45) NOT NULL,\n  `timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  "
        + "`data` blob NOT NULL,\n  KEY `leave` (`data`,`id`),\n  UNIQUE KEY `modify1` (`id`),\n"
        + "  KEY `modify2` (`ip_address`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    table2 = new MySQLTable(name, create);
    sql = table2.equals(table1);
    expectedSQL = expectedSQL.substring(0, expectedSQL.length() - 1)
        + ", \nDROP INDEX `modify2`, \nADD INDEX `modify2` (`ip_address`);";
    assertEquals("The sql generated should modify two indices", expectedSQL, sql.get(0));
  }

  @Test
  /**
   * Tests whether the equals function catches the adding of columns.
   * @author Peter Kaufman
   */
  public void testColumnAddition() {
    expectedSQL = "ALTER TABLE `ci_sessions`\nADD COLUMN `id` varchar(40) NOT NULL AFTER `data`;";
    // setup tables
    name = "ci_sessions";
    create = "CREATE TABLE `ci_sessions` (\n  `ip_address` varchar(45) NOT NULL,\n  "
        + "`timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  "
        + "`data` blob NOT NULL\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    table1 = new MySQLTable(name, create);
    table2 = new MySQLTable(name, create);
    // do comparison with equals
    sql = table1.equals(table2);
    assertEquals("The sql generated should be empty because all table columns are the same", 0, sql.size());
    // one column addition
    create = "CREATE TABLE `ci_sessions` (\n  `ip_address` varchar(45) NOT NULL,\n  "
        + "`timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  "
        + "`data` blob NOT NULL,\n  `id` varchar(40) NOT NULL\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    table1 = new MySQLTable(name, create);
    sql = table1.equals(table2);
    assertEquals("The sql generated should have one column addition", expectedSQL, sql.get(0));
    // two column addition
    create = "CREATE TABLE `ci_sessions` (\n  `ip_address` varchar(45) NOT NULL,\n  "
        + "`timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  "
        + "`data` blob NOT NULL,\n  `id` varchar(40) NOT NULL,\n"
        + "  `data2` blob NOT NULL\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    table1 = new MySQLTable(name, create);
    sql = table1.equals(table2);
    expectedSQL = "ALTER TABLE `ci_sessions`\nADD COLUMN `data2` blob NOT NULL AFTER `id`, \n"
        + "ADD COLUMN `id` varchar(40) NOT NULL AFTER `data`;";
    assertEquals("The sql generated should be a two column addition", expectedSQL, sql.get(0));
  }

  @Test
  /**
   * Tests whether the equals function cathces the dropping of columns.
   * @author Peter Kaufman
   */
  public void testColumnDropping() {
    expectedSQL = "ALTER TABLE `ci_sessions`\nDROP COLUMN `id`;";
    // setup tables
    name = "ci_sessions";
    create = "CREATE TABLE `ci_sessions` (\n  `ip_address` varchar(45) NOT NULL,\n  "
        + "`timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  `data` blob NOT NULL\n"
        + ") ENGINE=InnoDB DEFAULT CHARSET=latin1";
    table1 = new MySQLTable(name, create);
    table2 = new MySQLTable(name, create);
    // do comparison with equals
    sql = table2.equals(table1);
    assertEquals("The sql generated should be empty because all table columns are the same", 0, sql.size());
    // one column drop
    create = "CREATE TABLE `ci_sessions` (\n  `ip_address` varchar(45) NOT NULL,\n  "
        + "`timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  `data` blob NOT NULL,\n  "
        + "`id` varchar(40) NOT NULL\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    table1 = new MySQLTable(name, create);
    sql = table2.equals(table1);
    assertEquals("The sql generated should have one column drop", expectedSQL, sql.get(0));
    // two column drop
    create = "CREATE TABLE `ci_sessions` (\n  `ip_address` varchar(45) NOT NULL,\n  "
        + "`timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  `data` blob NOT NULL,\n  "
        + "`id` varchar(40) NOT NULL,\n  `data2` blob NOT NULL" + "\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    table1 = new MySQLTable(name, create);
    sql = table2.equals(table1);
    expectedSQL = "ALTER TABLE `ci_sessions`\nDROP COLUMN `data2`, \nDROP COLUMN `id`;";
    assertEquals("The sql generated should be a two column drop", expectedSQL, sql.get(0));
  }

  @Test
  /**
   * Tests whether the equals function catches the modifiying of columns.
   * @author Peter Kaufman
   */
  public void testColumnModification() {
    expectedSQL = "ALTER TABLE `ci_sessions`\nMODIFY COLUMN `timestamp` int(10) unsigned NOT NULL DEFAULT \'0\' AFTER `id`;";
    // setup tables
    name = "ci_sessions";
    create = "CREATE TABLE `ci_sessions` (\n  `id` varchar(45) NOT NULL\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    table1 = new MySQLTable(name, create);
    table2 = new MySQLTable(name, create);
    // do comparison with equals
    sql = table1.equals(table2);
    assertEquals("The sql generated should be empty because all table columns are the same", 0, sql.size());
    // one column modify
    create = "CREATE TABLE `ci_sessions` (\n  `id` varchar(45) NOT NULL,\n  "
        + "`timestamp` int(10) unsigned NOT NULL DEFAULT \'0\'\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    table1 = new MySQLTable(name, create);
    create = "CREATE TABLE `ci_sessions` (\n  `id` varchar(45) NOT NULL,\n  "
        + "`timestamp` int(11) unsigned NOT NULL DEFAULT \'0\'\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    table2 = new MySQLTable(name, create);
    sql = table1.equals(table2);
    assertEquals("The sql generated should have one column modification", expectedSQL, sql.get(0));
    // two column modify
    create = "CREATE TABLE `ci_sessions` (\n  `id` varchar(45) NOT NULL,\n  "
        + "`timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  "
        + "`data2` blob NOT NULL\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    table1 = new MySQLTable(name, create);
    create = "CREATE TABLE `ci_sessions` (\n  `id` varchar(45) NOT NULL,\n  "
        + "`timestamp` int(11) unsigned NOT NULL DEFAULT \'0\',\n  "
        + "`data2` blob\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    table2 = new MySQLTable(name, create);
    sql = table1.equals(table2);
    expectedSQL = "ALTER TABLE `ci_sessions`\nMODIFY COLUMN `data2` blob NOT NULL AFTER `timestamp`, \n"
        + "MODIFY COLUMN `timestamp` int(10) unsigned NOT NULL DEFAULT '0' AFTER `id`;";
    assertEquals("The sql generated should have two column modifications", expectedSQL, sql.get(0));
  }
}
