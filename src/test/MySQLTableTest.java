package test;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;
import dbdiffchecker.sql.MySQLTable;

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
  private List<String> expectedSQL = new ArrayList<>();
  private List<String> sql;

  @Before
  public void clearExpectedSQL() {
    expectedSQL.clear();
  }

  @Test
  public void testGetStatements() {
    name = "bloat";
    create1 = "CREATE TABLE `bloat` (\n  `bloatware` int(11) NOT NULL,\n  PRIMARY KEY (`bloatware`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    String charSet = "latin1";
    int autoIncrement = 17;
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

    autoIncrement = 19;
    table1.setAutoIncrement(autoIncrement);

    assertEquals("The autoIncrement value of the table should be equal to the one passed to setAutoIncrement",
        autoIncrement, table1.getAutoIncrement());

    charSet = "latin12";
    table1.setCharSet(charSet);

    assertEquals("The charSet of the table should be the same one passed into setCharSet", charSet,
        table1.getCharSet());
  }

  @Test
  public void testColumnListIsEmptyOnInitialization() {
    table1 = new MySQLTable();

    assertEquals("The column list for a table should be empty initially", true, table1.getColumns().isEmpty());
  }

  @Test
  public void testConstructorColumnAddition() {
    String column1 = "bloatware";
    String column2 = "shipmentID";
    create1 = "CREATE TABLE `bloat` (\n  `bloatware` int(11) NOT NULL,\n  `shipmentID` int(11) NOT NULL\n"
        + ") ENGINE=InnoDB DEFAULT CHARSET=latin1";

    table1 = new MySQLTable(name, create1);

    assertEquals(
        "The size of the column list for the table should be 2 when two colmns have been added to the column list", 2,
        table1.getColumns().size());
    assertEquals("The column passed to addColumn should be in the tables column list", true,
        table1.getColumns().containsKey(column1));
    assertEquals("The column passed to addColumn should be in the tables column list", true,
        table1.getColumns().containsKey(column2));
  }

  @Test
  public void testIndexListIsEmptyOnInitialization() {
    table1 = new MySQLTable();

    assertEquals("The index list should be empty on initialization", true, table1.getIndices().isEmpty());
  }

  @Test
  public void testConstructorIndexAddition() {
    String index1 = "shipment";
    String index2 = "shipped";
    name = "shippingData";
    create1 = "CREATE TABLE `shippingData` (\n  `shippingID` int(11) NOT NULL,\n  `vendor` STRING(11) NOT NULL,\n"
        + "  KEY `shipment` (`shippingID`,`vendor`),\n  KEY `shipped` (`shippingID`)"
        + "\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";

    table1 = new MySQLTable(name, create1);

    assertEquals(
        "The size of the index list for the table should be 2 when two indices have been added to the column list", 2,
        table1.getIndices().size());
    assertEquals("The index passed to addIndex should be in the tables index list", true,
        table1.getIndices().containsKey(index1));
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
  public void testGenerateStatements() {
    expectedSQL.add("ALTER TABLE `ci_sessions` CHARACTER SET latin1,\n  DROP INDEX `delete`,"
        + "\n  ADD COLUMN `id` varchar(40) NOT NULL,\n  MODIFY COLUMN `ip_address`"
        + " varchar(45) NOT NULL AFTER `id`,\n  MODIFY COLUMN `timestamp` int(10) unsigned "
        + "NOT NULL DEFAULT \'0\' AFTER `ip_address`,\n  DROP COLUMN `data2`,\n  ADD INDEX "
        + "`add` (`id`),\n  DROP INDEX `modify`,\n  ADD INDEX `modify` (`data`);");

    create1 = "CREATE TABLE `ci_sessions` (\n  `id` varchar(40) NOT NULL,\n  "
        + "`ip_address` varchar(45) NOT NULL,\n  `timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  "
        + "`data` blob NOT NULL,\n  KEY `add` (`id`),\n  KEY `modify` (`data`),\n  "
        + "KEY `leave` (`data`,`id`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    create2 = "CREATE TABLE `ci_sessions` (\n  `ip_address` varchar(40) NOT NULL,\n  "
        + "`timestamp` int(11) unsigned NOT NULL DEFAULT \'0\',\n  `data` blob NOT NULL,\n  "
        + "`data2` blob NOT NULL,\n  UNIQUE KEY `delete` (`id`),\n  KEY `modify` (`data`,`ip_address`),\n  "
        + "KEY `leave` (`data`,`id`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin2";

    sql = createTablesAndGenerateSQL(TABLE_NAME_1, create1, TABLE_NAME_1, create2);

    assertEquals("The sql generated should add a column, drop a column, modify two columns, drop two indexes,"
        + " add two indexes, and add a charset", expectedSQL, sql);
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
  public void testSindleIndexAdditionComparison() {
    expectedSQL.add("ALTER TABLE `ci_sessions` ADD INDEX `add` (`id`);");

    create1 = "CREATE TABLE `ci_sessions` (\n  `id` varchar(40) NOT NULL,\n  "
        + "`ip_address` varchar(45) NOT NULL,\n  `timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  "
        + "`data` blob NOT NULL,\n  KEY `leave` (`data`,`id`),\n  "
        + "KEY `add` (`id`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";

    sql = createTablesAndGenerateSQL(TABLE_NAME_1, create1, TABLE_NAME_1, CREATE_STATEMENT_1);

    assertEquals("The sql generated should add one index", expectedSQL, sql);
  }

  @Test
  public void testMultipleIndexAdditionComparison() {
    expectedSQL.add("ALTER TABLE `ci_sessions` ADD INDEX `add` (`id`),\n  ADD PRIMARY KEY (`id`,`ip_address`);");

    create1 = "CREATE TABLE `ci_sessions` (\n  `id` varchar(40) NOT NULL,\n  "
        + "`ip_address` varchar(45) NOT NULL,\n  `timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  "
        + "`data` blob NOT NULL,\n  KEY `leave` (`data`,`id`),\n  "
        + "KEY `add` (`id`),\n  PRIMARY KEY (`id`,`ip_address`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";

    sql = createTablesAndGenerateSQL(TABLE_NAME_1, create1, TABLE_NAME_1, CREATE_STATEMENT_1);

    assertEquals("The sql generated should add two indices", expectedSQL, sql);
  }

  @Test
  public void testSingleIndexDropComparison() {
    expectedSQL.add("ALTER TABLE `ci_sessions` DROP INDEX `drop1`;");

    create2 = "CREATE TABLE `ci_sessions` (\n  `id` varchar(40) NOT NULL,\n  "
        + "`ip_address` varchar(45) NOT NULL,\n  `timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  "
        + "`data` blob NOT NULL,\n  KEY `leave` (`data`,`id`),\n  "
        + "KEY `drop1` (`id`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";

    sql = createTablesAndGenerateSQL(TABLE_NAME_1, CREATE_STATEMENT_3, TABLE_NAME_1, create2);

    assertEquals("The sql generated should drop one index", expectedSQL, sql);
  }

  @Test
  public void testMultipleIndexDropComparison() {
    expectedSQL.add("ALTER TABLE `ci_sessions` DROP INDEX `drop1`,\n  DROP INDEX `drop2`;");

    create1 = "CREATE TABLE `ci_sessions` (\n  `id` varchar(40) NOT NULL,\n  "
        + "`ip_address` varchar(45) NOT NULL,\n  `timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  "
        + "`data` blob NOT NULL,\n  KEY `leave` (`data`,`id`),\n  "
        + "KEY `drop1` (`id`),\n  KEY `drop2` (`id`,`ip_address`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";

    sql = createTablesAndGenerateSQL(TABLE_NAME_1, CREATE_STATEMENT_3, TABLE_NAME_1, create1);

    assertEquals("The sql generated should drop two indices", expectedSQL, sql);
  }

  @Test
  public void testSingleIndexModificationComparison() {
    expectedSQL.add("ALTER TABLE `ci_sessions` DROP INDEX `modify1`,\n  ADD UNIQUE INDEX `modify1` (`id`);");

    create1 = "CREATE TABLE `ci_sessions` (\n  `id` varchar(40) NOT NULL,\n  "
        + "`ip_address` varchar(45) NOT NULL,\n  `timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  "
        + "`data` blob NOT NULL,\n  KEY `leave` (`data`,`id`),\n  UNIQUE KEY `modify1` (`id`)\n"
        + ") ENGINE=InnoDB DEFAULT CHARSET=latin1";
    create2 = "CREATE TABLE `ci_sessions` (\n  `id` varchar(40) NOT NULL,\n  "
        + "`ip_address` varchar(45) NOT NULL,\n  `timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  "
        + "`data` blob NOT NULL,\n  KEY `leave` (`data`,`id`),\n  KEY `modify1` (`id`)\n"
        + ") ENGINE=InnoDB DEFAULT CHARSET=latin1";

    sql = createTablesAndGenerateSQL(TABLE_NAME_1, create1, TABLE_NAME_1, create2);

    assertEquals("The sql generated should modify one index", expectedSQL, sql);
  }

  @Test
  public void testMultipleIndexModificationComparison() {
    expectedSQL.add("ALTER TABLE `ci_sessions` DROP INDEX `modify1`,\n  ADD UNIQUE INDEX `modify1` (`id`)"
        + ",\n  DROP INDEX `modify2`,\n  ADD INDEX `modify2` (`ip_address`);");

    create1 = "CREATE TABLE `ci_sessions` (\n  `id` varchar(40) NOT NULL,\n  "
        + "`ip_address` varchar(45) NOT NULL,\n  `timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  "
        + "`data` blob NOT NULL,\n  KEY `leave` (`data`,`id`),\n  UNIQUE KEY `modify1` (`id`),\n"
        + "  KEY `modify2` (`ip_address`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    create2 = "CREATE TABLE `ci_sessions` (\n  `id` varchar(40) NOT NULL,\n  "
        + "`ip_address` varchar(45) NOT NULL,\n  `timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  "
        + "`data` blob NOT NULL,\n  KEY `leave` (`data`,`id`),\n  KEY `modify1` (`id`),\n"
        + "  KEY `modify2` (`id`,`ip_address`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";

    sql = createTablesAndGenerateSQL(TABLE_NAME_1, create1, TABLE_NAME_1, create2);

    assertEquals("The sql generated should modify two indices", expectedSQL, sql);
  }

  @Test
  public void testSingleColumnAdditionComparison() {
    expectedSQL.add("ALTER TABLE `ci_sessions` ADD COLUMN `id` varchar(40) NOT NULL AFTER `data`;");

    create1 = "CREATE TABLE `ci_sessions` (\n  `ip_address` varchar(45) NOT NULL,\n  "
        + "`timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  "
        + "`data` blob NOT NULL,\n  `id` varchar(40) NOT NULL\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";

    sql = createTablesAndGenerateSQL(TABLE_NAME_1, create1, TABLE_NAME_1, CREATE_STATEMENT_5);

    assertEquals("The sql generated should have one column addition", expectedSQL, sql);
  }

  @Test
  public void testMultipleColumnAdditionComparison() {
    expectedSQL.add("ALTER TABLE `ci_sessions` ADD COLUMN `data2` blob NOT NULL AFTER `id`,\n  "
        + "ADD COLUMN `id` varchar(40) NOT NULL AFTER `data`;");

    create1 = "CREATE TABLE `ci_sessions` (\n  `ip_address` varchar(45) NOT NULL,\n  "
        + "`timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  "
        + "`data` blob NOT NULL,\n  `id` varchar(40) NOT NULL,\n"
        + "  `data2` blob NOT NULL\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";

    sql = createTablesAndGenerateSQL(TABLE_NAME_1, create1, TABLE_NAME_1, CREATE_STATEMENT_5);

    assertEquals("The sql generated should be a two column addition", expectedSQL, sql);
  }

  @Test
  public void testSingleColumnDropComparison() {
    expectedSQL.add("ALTER TABLE `ci_sessions` DROP COLUMN `id`;");

    create1 = "CREATE TABLE `ci_sessions` (\n  `ip_address` varchar(45) NOT NULL,\n  "
        + "`timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  `data` blob NOT NULL,\n  "
        + "`id` varchar(40) NOT NULL\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";

    sql = createTablesAndGenerateSQL(TABLE_NAME_1, CREATE_STATEMENT_6, TABLE_NAME_1, create1);

    assertEquals("The sql generated should have one column drop", expectedSQL, sql);
  }

  @Test
  public void testMultipleColumnDropComparison() {
    expectedSQL.add("ALTER TABLE `ci_sessions` DROP COLUMN `data2`,\n  DROP COLUMN `id`;");

    create1 = "CREATE TABLE `ci_sessions` (\n  `ip_address` varchar(45) NOT NULL,\n  "
        + "`timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  `data` blob NOT NULL,\n  "
        + "`id` varchar(40) NOT NULL,\n  `data2` blob NOT NULL" + "\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";

    sql = createTablesAndGenerateSQL(TABLE_NAME_1, CREATE_STATEMENT_6, TABLE_NAME_1, create1);

    assertEquals("The sql generated should be a two column drop", expectedSQL, sql);
  }

  @Test
  public void testSingleColumnModificationComparison() {
    expectedSQL
        .add("ALTER TABLE `ci_sessions` MODIFY COLUMN `timestamp` int(10) unsigned NOT NULL DEFAULT \'0\' AFTER `id`;");

    create1 = "CREATE TABLE `ci_sessions` (\n  `id` varchar(45) NOT NULL,\n  "
        + "`timestamp` int(10) unsigned NOT NULL DEFAULT \'0\'\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    create2 = "CREATE TABLE `ci_sessions` (\n  `id` varchar(45) NOT NULL,\n  "
        + "`timestamp` int(11) unsigned NOT NULL DEFAULT \'0\'\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";

    sql = createTablesAndGenerateSQL(TABLE_NAME_1, create1, TABLE_NAME_1, create2);

    assertEquals("The sql generated should have one column modification", expectedSQL, sql);
  }

  @Test
  public void testMultipleColumnModificationComparison() {
    expectedSQL.add("ALTER TABLE `ci_sessions` MODIFY COLUMN `data2` blob NOT NULL AFTER `timestamp`,\n  "
        + "MODIFY COLUMN `timestamp` int(10) unsigned NOT NULL DEFAULT '0' AFTER `id`;");

    create1 = "CREATE TABLE `ci_sessions` (\n  `id` varchar(45) NOT NULL,\n  "
        + "`timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  "
        + "`data2` blob NOT NULL\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    create2 = "CREATE TABLE `ci_sessions` (\n  `id` varchar(45) NOT NULL,\n  "
        + "`timestamp` int(11) unsigned NOT NULL DEFAULT \'0\',\n  "
        + "`data2` blob\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";

    sql = createTablesAndGenerateSQL(TABLE_NAME_1, create1, TABLE_NAME_1, create2);

    assertEquals("The sql generated should have two column modifications", expectedSQL, sql);
  }

  private List<String> createTablesAndGenerateSQL(String tableName1, String createStatement1, String tableName2,
      String createStatement2) {
    table1 = new MySQLTable(tableName1, createStatement1);
    MySQLTable table2 = new MySQLTable(tableName2, createStatement2);
    return table1.generateStatements(table2);
  }
}
