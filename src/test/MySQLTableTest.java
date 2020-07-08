package test;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import java.util.List;

import dbdiffchecker.sql.MySQLTable;

/**
 * A unit test that makes sure that the MySQLTable object works as intended.
 *
 * @author Peter Kaufman
 * @version 7-7-20
 * @since 5-10-19
 */
public class MySQLTableTest {
  private static final String TABLE_NAME_1 = "ci_sessions";
  private static final String CREATE_STATEMENT_1 = "CREATE TABLE `ci_sessions` (\n  `id` varchar(40) NOT NULL,\n  "
      + "`ip_address` varchar(45) NOT NULL,\n  `timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  "
      + "`data` blob NOT NULL,\n  KEY `leave` (`data`,`id`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
  private static final String CREATE_STATEMENT_2 = "CREATE TABLE `ci_sessions` (\n  `id` varchar(40) NOT NULL,\n  "
      + "`ip_address` varchar(45) NOT NULL,\n  `timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  "
      + "`data` blob NOT NULL,\n  KEY `leave` (`data`,`id`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
  private static final String CREATE_STATEMENT_3 = "CREATE TABLE `ci_sessions` (\n  `id` varchar(40) NOT NULL,\n  "
      + "`ip_address` varchar(45) NOT NULL,\n  `timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  "
      + "`data` blob NOT NULL,\n  KEY `leave` (`data`,`id`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
  private static final String CREATE_STATEMENT_4 = "CREATE TABLE `ci_sessions` (\n  `id` varchar(40) NOT NULL,\n  "
      + "`ip_address` varchar(45) NOT NULL,\n  `timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  "
      + "`data` blob NOT NULL,\n  KEY `leave` (`data`,`id`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
  private static final String CREATE_STATEMENT_5 = "CREATE TABLE `ci_sessions` (\n  `ip_address` varchar(45) NOT NULL,\n  "
      + "`timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  "
      + "`data` blob NOT NULL\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
  private static final String CREATE_STATEMENT_6 = "CREATE TABLE `ci_sessions` (\n  `ip_address` varchar(45) NOT NULL,\n  "
      + "`timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  `data` blob NOT NULL\n"
      + ") ENGINE=InnoDB DEFAULT CHARSET=latin1";
  private static final String CREATE_STATEMENT_7 = "CREATE TABLE `ci_sessions` (\n  `id` varchar(45) NOT NULL\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
  private MySQLTable table1;
  private String name;
  private String create1;
  private String create2;
  private String expectedSQL;
  private List<String> sql;

  @Test
  public void testGetStatements() {
    name = "bloat";
    create1 = "CREATE TABLE `bloat` (\n  `bloatware` int(11) NOT NULL,\n  PRIMARY KEY (`bloatware`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    String charSet = "latin1";
    String autoIncrement = "17";
    table1 = new MySQLTable(name, create1);
    String collation = "latin1_swedish_c";
    table1.setCollation(collation);
    table1.setAutoIncrement(autoIncrement);
    assertEquals("The name of the table should be the one passed into the constructor", name, table1.getName());
    assertEquals("The create statement of the table should be the one passed into the constructor", create1 + ";",
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
  public void testAddColumn() {
    String column1 = "bloatware";
    String column2 = "shipmentID";
    name = "bloat";
    create1 = "CREATE TABLE `bloat` (\n  `bloatware` int(11) NOT NULL\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    table1 = new MySQLTable();
    assertEquals("The size of the column list for the table should be 0 when empty", 0, table1.getColumns().size());
    // make sure the first column has been added
    table1 = new MySQLTable(name, create1);
    assertEquals("The size of the column list for the table should be 1 when one column has been added", 1,
        table1.getColumns().size());
    assertEquals("The column passed to addColumn should be in the tables column list", true,
        table1.getColumns().containsKey(column1));
    create1 = "CREATE TABLE `bloat` (\n  `bloatware` int(11) NOT NULL,\n  `shipmentID` int(11) NOT NULL\n"
        + ") ENGINE=InnoDB DEFAULT CHARSET=latin1";
    // make sure the second column has been added
    table1 = new MySQLTable(name, create1);
    assertEquals(
        "The size of the column list for the table should be 2 when two colmns have been added to the column list", 2,
        table1.getColumns().size());
    assertEquals("The column passed to addColumn should be in the tables column list", true,
        table1.getColumns().containsKey(column2));
  }

  @Test
  public void testAddIndex() {
    String index1 = "shipment";
    String index2 = "shipped";
    name = "shippingData";
    create1 = "CREATE TABLE `shippingData` (\n  `shippingID` int(11) NOT NULL,\n  `vendor` STRING(11) NOT NULL,\n"
        + "  KEY `shipment` (`shippingID`,`vendor`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    table1 = new MySQLTable();
    assertEquals("The size of the index list for the table should be 0 when empty", 0, table1.getIndices().size());
    // make sure the first index has been added
    table1 = new MySQLTable(name, create1);
    assertEquals("The size of the column list for the table should be 1 when one index has been added", 1,
        table1.getIndices().size());
    assertEquals("The index passed to addIndex should be in the tables index list", true,
        table1.getIndices().containsKey(index1));
    // make sure the second index has been added
    create1 = "CREATE TABLE `shippingData` (\n  `shippingID` int(11) NOT NULL,\n  `vendor` STRING(11) NOT NULL,\n"
        + "  KEY `shipment` (`shippingID`,`vendor`),\n  KEY `shipped` (`shippingID`)"
        + "\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    table1 = new MySQLTable(name, create1);
    assertEquals(
        "The size of the index list for the table should be 2 when two indices have been added to the column list", 2,
        table1.getIndices().size());
    assertEquals("The index passed to addIndex should be in the tables index list", true,
        table1.getIndices().containsKey(index2));
  }

  @Test
  public void testAddForeignKey() {
    name = "products";
    create1 = "CREATE TABLE `products` (\n  `prd_id` int not null auto_increment primary key,\n"
        + "  `prd_name` varchar(355) not null,\n  `prd_price` decimal,\n  `cat_id` int not null,\n  "
        + "CONSTRAINT `constraint_name`\n  FOREIGN KEY `fk_cat`(`cat_id`)\n  REFERENCES `categories`"
        + "(`cat_id`)\n  ON UPDATE CASCADE\n  ON DELETE RESTRICT\n  )ENGINE=InnoDB";
    table1 = new MySQLTable(name, create1);
    assertEquals("There should be a Foreign Key in the index list", true, table1.getIndices().containsKey("fk_cat"));
  }

  @Test
  public void testEquals() {
    expectedSQL = "ALTER TABLE `ci_sessions`\nCHARACTER SET latin1, \nDROP INDEX `delete`, "
        + "\nADD COLUMN `id` varchar(40) NOT NULL, \nMODIFY COLUMN `ip_address`"
        + " varchar(45) NOT NULL AFTER `id`, \nMODIFY COLUMN `timestamp` int(10) unsigned "
        + "NOT NULL DEFAULT \'0\' AFTER `ip_address`, \nDROP COLUMN `data2`, \nADD INDEX "
        + "`add` (`id`), \nDROP INDEX `modify`, \nADD INDEX `modify` (`data`);";

    create1 = "CREATE TABLE `ci_sessions` (\n  `id` varchar(40) NOT NULL,\n  "
        + "`ip_address` varchar(45) NOT NULL,\n  `timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  "
        + "`data` blob NOT NULL,\n  KEY `add` (`id`),\n  KEY `modify` (`data`),\n  "
        + "KEY `leave` (`data`,`id`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    create2 = "CREATE TABLE `ci_sessions` (\n  `ip_address` varchar(40) NOT NULL,\n  "
        + "`timestamp` int(11) unsigned NOT NULL DEFAULT \'0\',\n  `data` blob NOT NULL,\n  "
        + "`data2` blob NOT NULL,\n  UNIQUE KEY `delete` (`id`)\n,  KEY `modify` (`data`,`ip_address`),\n  "
        + "KEY `leave` (`data`,`id`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin2";
    sql = createTablesAndGenerateSQL(TABLE_NAME_1, create1, TABLE_NAME_1, create2);
    assertEquals("The sql generated should add a column, drop a column, modify two columns, drop two indexes,"
        + " add two indexes, and add a charset", expectedSQL, sql.get(0));
  }

  @Test
  public void testEqualTableComparison() {
    String errorMsg = "There should be no sql generated for two tables with the exact create statements";

    sql = createTablesAndGenerateSQL(TABLE_NAME_1, CREATE_STATEMENT_1, TABLE_NAME_1, CREATE_STATEMENT_1);
    assertEquals(errorMsg, true, sql.isEmpty());

    sql = createTablesAndGenerateSQL(TABLE_NAME_1, CREATE_STATEMENT_2, TABLE_NAME_1, CREATE_STATEMENT_2);
    assertEquals(errorMsg, true, sql.isEmpty());

    sql = createTablesAndGenerateSQL(TABLE_NAME_1, CREATE_STATEMENT_3, TABLE_NAME_1, CREATE_STATEMENT_3);
    assertEquals(errorMsg, true, sql.isEmpty());

    sql = createTablesAndGenerateSQL(TABLE_NAME_1, CREATE_STATEMENT_4, TABLE_NAME_1, CREATE_STATEMENT_4);
    assertEquals(errorMsg, true, sql.isEmpty());

    sql = createTablesAndGenerateSQL(TABLE_NAME_1, CREATE_STATEMENT_5, TABLE_NAME_1, CREATE_STATEMENT_5);
    assertEquals(errorMsg, true, sql.isEmpty());

    sql = createTablesAndGenerateSQL(TABLE_NAME_1, CREATE_STATEMENT_6, TABLE_NAME_1, CREATE_STATEMENT_6);
    assertEquals(errorMsg, true, sql.isEmpty());

    sql = createTablesAndGenerateSQL(TABLE_NAME_1, CREATE_STATEMENT_7, TABLE_NAME_1, CREATE_STATEMENT_7);
    assertEquals(errorMsg, true, sql.isEmpty());
  }

  @Test
  public void testIndexAddition() {
    expectedSQL = "ALTER TABLE `ci_sessions`\nADD INDEX `add` (`id`);";

    create1 = "CREATE TABLE `ci_sessions` (\n  `id` varchar(40) NOT NULL,\n  "
        + "`ip_address` varchar(45) NOT NULL,\n  `timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  "
        + "`data` blob NOT NULL,\n  KEY `leave` (`data`,`id`),\n  "
        + "KEY `add` (`id`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    sql = createTablesAndGenerateSQL(TABLE_NAME_1, create1, TABLE_NAME_1, CREATE_STATEMENT_1);
    assertEquals("The sql generated should add one index", expectedSQL, sql.get(0));
    // two index add
    create1 = "CREATE TABLE `ci_sessions` (\n  `id` varchar(40) NOT NULL,\n  "
        + "`ip_address` varchar(45) NOT NULL,\n  `timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  "
        + "`data` blob NOT NULL,\n  KEY `leave` (`data`,`id`),\n  "
        + "KEY `add` (`id`),\n  PRIMARY KEY (`id`,`ip_address`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    sql = createTablesAndGenerateSQL(TABLE_NAME_1, create1, TABLE_NAME_1, CREATE_STATEMENT_1);
    expectedSQL = "ALTER TABLE `ci_sessions`\nADD INDEX `add` (`id`), \nADD PRIMARY KEY (`id`,`ip_address`);";
    assertEquals("The sql generated should add two indices", expectedSQL, sql.get(0));
  }

  @Test
  public void testIndexDropping() {
    expectedSQL = "ALTER TABLE `ci_sessions`\nDROP INDEX `drop1`;";

    create2 = "CREATE TABLE `ci_sessions` (\n  `id` varchar(40) NOT NULL,\n  "
        + "`ip_address` varchar(45) NOT NULL,\n  `timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  "
        + "`data` blob NOT NULL,\n  KEY `leave` (`data`,`id`),\n  "
        + "KEY `drop1` (`id`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    sql = createTablesAndGenerateSQL(TABLE_NAME_1, CREATE_STATEMENT_3, TABLE_NAME_1, create2);
    assertEquals("The sql generated should drop one index", expectedSQL, sql.get(0));
    // two index drop
    create2 = "CREATE TABLE `ci_sessions` (\n  `id` varchar(40) NOT NULL,\n  "
        + "`ip_address` varchar(45) NOT NULL,\n  `timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  "
        + "`data` blob NOT NULL,\n  KEY `leave` (`data`,`id`),\n  "
        + "KEY `drop1` (`id`),\n  KEY `drop2` (`id`,`ip_address`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    sql = createTablesAndGenerateSQL(TABLE_NAME_1, CREATE_STATEMENT_3, TABLE_NAME_1, create2);
    expectedSQL = "ALTER TABLE `ci_sessions`\nDROP INDEX `drop1`, \nDROP INDEX `drop2`;";
    assertEquals("The sql generated should drop two indices", expectedSQL, sql.get(0));
  }

  @Test
  public void testIndexModification() {
    expectedSQL = "ALTER TABLE `ci_sessions`\nDROP INDEX `modify1`, \nADD UNIQUE INDEX `modify1` (`id`);";

    create1 = "CREATE TABLE `ci_sessions` (\n  `id` varchar(40) NOT NULL,\n  "
        + "`ip_address` varchar(45) NOT NULL,\n  `timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  "
        + "`data` blob NOT NULL,\n  KEY `leave` (`data`,`id`),\n  UNIQUE KEY `modify1` (`id`)\n"
        + ") ENGINE=InnoDB DEFAULT CHARSET=latin1";
    create2 = "CREATE TABLE `ci_sessions` (\n  `id` varchar(40) NOT NULL,\n  "
        + "`ip_address` varchar(45) NOT NULL,\n  `timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  "
        + "`data` blob NOT NULL,\n  KEY `leave` (`data`,`id`),\n  KEY `modify1` (`id`)\n"
        + ") ENGINE=InnoDB DEFAULT CHARSET=latin1";
    sql = createTablesAndGenerateSQL(TABLE_NAME_1, create1, TABLE_NAME_1, create2);
    assertEquals("The sql generated should modify one index", expectedSQL, sql.get(0));
    // two column modify
    create1 = "CREATE TABLE `ci_sessions` (\n  `id` varchar(40) NOT NULL,\n  "
        + "`ip_address` varchar(45) NOT NULL,\n  `timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  "
        + "`data` blob NOT NULL,\n  KEY `leave` (`data`,`id`),\n  UNIQUE KEY `modify1` (`id`),\n"
        + "  KEY `modify2` (`ip_address`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    create2 = "CREATE TABLE `ci_sessions` (\n  `id` varchar(40) NOT NULL,\n  "
        + "`ip_address` varchar(45) NOT NULL,\n  `timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  "
        + "`data` blob NOT NULL,\n  KEY `leave` (`data`,`id`),\n  KEY `modify1` (`id`),\n"
        + "  KEY `modify2` (`id`,`ip_address`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    sql = createTablesAndGenerateSQL(TABLE_NAME_1, create1, TABLE_NAME_1, create2);
    expectedSQL = expectedSQL.substring(0, expectedSQL.length() - 1)
        + ", \nDROP INDEX `modify2`, \nADD INDEX `modify2` (`ip_address`);";
    assertEquals("The sql generated should modify two indices", expectedSQL, sql.get(0));
  }

  @Test
  public void testColumnAddition() {
    expectedSQL = "ALTER TABLE `ci_sessions`\nADD COLUMN `id` varchar(40) NOT NULL AFTER `data`;";

    create1 = "CREATE TABLE `ci_sessions` (\n  `ip_address` varchar(45) NOT NULL,\n  "
        + "`timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  "
        + "`data` blob NOT NULL,\n  `id` varchar(40) NOT NULL\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    sql = createTablesAndGenerateSQL(TABLE_NAME_1, create1, TABLE_NAME_1, CREATE_STATEMENT_5);
    assertEquals("The sql generated should have one column addition", expectedSQL, sql.get(0));
    // two column addition
    create1 = "CREATE TABLE `ci_sessions` (\n  `ip_address` varchar(45) NOT NULL,\n  "
        + "`timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  "
        + "`data` blob NOT NULL,\n  `id` varchar(40) NOT NULL,\n"
        + "  `data2` blob NOT NULL\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    sql = createTablesAndGenerateSQL(TABLE_NAME_1, create1, TABLE_NAME_1, CREATE_STATEMENT_5);
    expectedSQL = "ALTER TABLE `ci_sessions`\nADD COLUMN `data2` blob NOT NULL AFTER `id`, \n"
        + "ADD COLUMN `id` varchar(40) NOT NULL AFTER `data`;";
    assertEquals("The sql generated should be a two column addition", expectedSQL, sql.get(0));
  }

  @Test
  public void testColumnDropping() {
    expectedSQL = "ALTER TABLE `ci_sessions`\nDROP COLUMN `id`;";
    // one column drop
    create2 = "CREATE TABLE `ci_sessions` (\n  `ip_address` varchar(45) NOT NULL,\n  "
        + "`timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  `data` blob NOT NULL,\n  "
        + "`id` varchar(40) NOT NULL\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    sql = createTablesAndGenerateSQL(TABLE_NAME_1, CREATE_STATEMENT_6, TABLE_NAME_1, create2);
    assertEquals("The sql generated should have one column drop", expectedSQL, sql.get(0));
    // two column drop
    create2 = "CREATE TABLE `ci_sessions` (\n  `ip_address` varchar(45) NOT NULL,\n  "
        + "`timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  `data` blob NOT NULL,\n  "
        + "`id` varchar(40) NOT NULL,\n  `data2` blob NOT NULL" + "\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    sql = createTablesAndGenerateSQL(TABLE_NAME_1, CREATE_STATEMENT_6, TABLE_NAME_1, create2);
    expectedSQL = "ALTER TABLE `ci_sessions`\nDROP COLUMN `data2`, \nDROP COLUMN `id`;";
    assertEquals("The sql generated should be a two column drop", expectedSQL, sql.get(0));
  }

  @Test
  public void testColumnModification() {
    expectedSQL = "ALTER TABLE `ci_sessions`\nMODIFY COLUMN `timestamp` int(10) unsigned NOT NULL DEFAULT \'0\' AFTER `id`;";

    create1 = "CREATE TABLE `ci_sessions` (\n  `id` varchar(45) NOT NULL,\n  "
        + "`timestamp` int(10) unsigned NOT NULL DEFAULT \'0\'\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    create2 = "CREATE TABLE `ci_sessions` (\n  `id` varchar(45) NOT NULL,\n  "
        + "`timestamp` int(11) unsigned NOT NULL DEFAULT \'0\'\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    sql = createTablesAndGenerateSQL(TABLE_NAME_1, create1, TABLE_NAME_1, create2);
    assertEquals("The sql generated should have one column modification", expectedSQL, sql.get(0));
    // two column modify
    create1 = "CREATE TABLE `ci_sessions` (\n  `id` varchar(45) NOT NULL,\n  "
        + "`timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  "
        + "`data2` blob NOT NULL\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    create2 = "CREATE TABLE `ci_sessions` (\n  `id` varchar(45) NOT NULL,\n  "
        + "`timestamp` int(11) unsigned NOT NULL DEFAULT \'0\',\n  "
        + "`data2` blob\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    sql = createTablesAndGenerateSQL(TABLE_NAME_1, create1, TABLE_NAME_1, create2);
    expectedSQL = "ALTER TABLE `ci_sessions`\nMODIFY COLUMN `data2` blob NOT NULL AFTER `timestamp`, \n"
        + "MODIFY COLUMN `timestamp` int(10) unsigned NOT NULL DEFAULT '0' AFTER `id`;";
    assertEquals("The sql generated should have two column modifications", expectedSQL, sql.get(0));
  }

  private List<String> createTablesAndGenerateSQL(String tableName1, String createStatement1, String tableName2,
      String createStatement2) {
    table1 = new MySQLTable(tableName1, createStatement1);
    MySQLTable table2 = new MySQLTable(tableName2, createStatement2);
    return table1.equals(table2);
  }
}
