import static org.junit.Assert.assertEquals;
import org.junit.Test;
import java.util.ArrayList;
import dbdiffchecker.Column;
import dbdiffchecker.Index;
import dbdiffchecker.Table;

/**
 * TableTest is a unit test that makes sure that the Table object works as intended.
 * @author Peter Kaufman
 * @version 5-11-19
 * @since 5-10-19
 */
public class TableTest {

  private Table table1, table2;
  private String name, create, collation, charSet, autoIncrement, details, columns;
  private Column column1, column2;
  private Index index1, index2;

  @Test
  /**
   * Tests whether the get statements inside of the Table object work as intended.
   * @author Peter Kaufman
   */
  public void testGetStatements() {
    name = "bloat";
    create = "CREATE TABLE `bloat` (\n  `bloatware` int(11) NOT NULL,\n  PRIMARY KEY (`bloatware`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    charSet = "latin1";
    autoIncrement = "17";
    table1 = new Table(name, create);
    collation = "latin1_swedish_c";
    table1.setCollation(collation);
    table1.setAutoIncrement(autoIncrement);

    // start assertions
    assertEquals("The name of the table should be the one passed into the constructor",
      name, table1.getName());
    assertEquals("The create statement of the table should be the one passed into the constructor",
      create + ";", table1.getCreateStatement());
    assertEquals("The charSet of the table should be the same one from the create statement",
      charSet, table1.getCharSet());
    assertEquals("The autoIncrement value of the table should be equal to the one passed to setAutoIncrement",
      autoIncrement, table1.getAutoIncrement());
    assertEquals("The collation of the table should be equal to the one passed to setCollation",
      collation, table1.getCollation());
    
    autoIncrement = "19";
    table1.setAutoIncrement(autoIncrement);
    assertEquals("The autoIncrement value of the table should be equal to the one passed to setAutoIncrement",
      autoIncrement, table1.getAutoIncrement());
    
    charSet = "latin12";
    table1.setCharSet(charSet);
    assertEquals("The charSet of the table should be the same one passed into setCharSet",
      charSet, table1.getCharSet());
  }

  @Test
  /**
   * Tests whether the addColumn function works as intended.
   * @author Peter Kaufman
   */
  public void testAddColumn() {
    name = "bloat";
    create = "CREATE TABLE `bloat` (\n  `bloatware` int(11) NOT NULL,\n  PRIMARY KEY (`bloatware`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    table1 = new Table(name, create);
    name = "bloatware";
    details = name + " int(11) NOT NULL";
    column1 = new Column(name, details);
    name = "shipmentID";
    details = name + " int(11) NOT NULL";
    column2 = new Column(name, details);
    assertEquals("The size of the column list for the table should be 0 when empty",
      0, table1.getColumns().size());
    
    table1.addColumn(column1);
    assertEquals("The size of the column list for the table should be 1 when one column has been added",
      1, table1.getColumns().size());
    assertEquals("The column passed to addColumn should be in the tables column list",
      true, table1.getColumns().containsKey(column1.getName()));
    
    table1.addColumn(column2);
    assertEquals("The size of the column list for the table should be 2 when two colmns have been added to the column list",
      2, table1.getColumns().size());
    assertEquals("The column passed to addColumn should be in the tables column list",
      true, table1.getColumns().containsKey(column2.getName()));
  }

  @Test
  /**
   * Tests whether the addIndex function works as intended.
   * @author Peter Kaufman
   */
  public void testAddIndex() {
    name = "bloat";
    create = "CREATE TABLE `bloat` (\n  `bloatware` int(11) NOT NULL,\n  PRIMARY KEY (`bloatware`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    table1 = new Table(name, create);
    name = "shipment";
    columns = "`shippingID`,`vendor`";
    create = "CREATE INDEX `" + name + "` (" + columns + ")";
    index1 = new Index(name, create, columns);
    name = "shipped";
    columns = "`shippingID`";
    create = "CREATE INDEX `" + name + "` (" + columns + ")";
    index2 = new Index(name, create, columns);
    assertEquals("The size of the index list for the table should be 0 when empty",
    0, table1.getIndices().size());
  
    table1.addIndex(index1);
    assertEquals("The size of the column list for the table should be 1 when one index has been added",
      1, table1.getIndices().size());
    assertEquals("The index passed to addIndex should be in the tables index list",
      true, table1.getIndices().containsKey(index1.getName()));
    
    table1.addIndex(index2);
    assertEquals("The size of the index list for the table should be 2 when two indices have been added to the column list",
      2, table1.getIndices().size());
    assertEquals("The index passed to addIndex should be in the tables index list",
      true, table1.getIndices().containsKey(index2.getName())); 
  }

  @Test
  /**
   * Tests whether the equals function works as intended.
   * @author Peter Kaufman
   */
  public void testEquals() {
    ArrayList<String> sql;
    String expectedSQL = "ALTER TABLE `ci_sessions`\nCHARACTER SET latin1, \nDROP INDEX `delete`, " + 
      "\nADD COLUMN `id` varchar(40) NOT NULL AFTER `data`, \nMODIFY COLUMN `ip_address` varchar(45) NOT NULL, " + 
      "\nMODIFY COLUMN `timestamp` int(10) unsigned NOT NULL DEFAULT \'0\', \nDROP COLUMN `data2`, " +
      "\nCREATE INDEX `add` (`id`), \nDROP INDEX `modify`, \nCREATE INDEX `modify` (`data`);";
    // setup table1
    name = "ci_sessions";
    create = "CREATE TABLE `ci_sessions` (\n  `id` varchar(40) NOT NULL,\n  `ip_address` varchar(45) NOT NULL,\n  `timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  `data` blob NOT NULL,\n  PRIMARY KEY (`id`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    table1 = new Table(name, create);
    // add columns
    name = "id";
    details = "varchar(40) NOT NULL";
    table1.addColumn(new Column(name, details));
    name = "ip_address";
    details = "varchar(45) NOT NULL";
    table1.addColumn(new Column(name, details));
    name = "timestamp";
    details = "int(10) unsigned NOT NULL DEFAULT \'0\'";
    table1.addColumn(new Column(name, details));
    name = "data";
    details = "blob NOT NULL";
    table1.addColumn(new Column(name, details));
    // add indexes
    name = "add";
    columns = "`id`";
    create = "CREATE INDEX `" + name + "` (" + columns + ")";
    table1.addIndex(new Index(name, create, columns));
    name = "modify";
    columns = "`data`";
    create = "CREATE INDEX `" + name + "` (" + columns + ")";
    table1.addIndex(new Index(name, create, columns));
    name = "leave";
    columns = "`data`,`id`";
    create = "CREATE INDEX `" + name + "` (" + columns + ")";
    table1.addIndex(new Index(name, create, columns));
    // setup table2
    name = "ci_sessions";
    create = "CREATE TABLE `ci_sessions` (\n  `ip_address` varchar(40) NOT NULL,\n  `timestamp` int(11) unsigned NOT NULL DEFAULT \'0\',\n  `data` blob NOT NULL,\n  `data2` blob NOT NULL,\n PRIMARY KEY (`id`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin2";
    table2 = new Table(name, create);
    // add columns
    name = "ip_address";
    details = "varchar(40) NOT NULL";
    table2.addColumn(new Column(name, details));
    name = "timestamp";
    details = "int(11) unsigned NOT NULL DEFAULT \'0\'";
    table2.addColumn(new Column(name, details));
    name = "data";
    details = "blob NOT NULL";
    table2.addColumn(new Column(name, details));
    name = "data2";
    details = "blob NOT NULL";
    table2.addColumn(new Column(name, details));
    // add indexes
    name = "delete";
    columns = "`id`";
    create = "CREATE UNIQUE INDEX `" + name + "` (" + columns + ")";
    table2.addIndex(new Index(name, create, columns));
    name = "modify";
    columns = "`data`,`ip_address`";
    create = "CREATE INDEX `" + name + "` (" + columns + ")";
    table2.addIndex(new Index(name, create, columns));
    name = "leave";
    columns = "`data`,`id`";
    create = "CREATE INDEX `" + name + "` (" + columns + ")";
    table2.addIndex(new Index(name, create, columns));
    // do comparison with equals
    sql = table1.equals(table2);
    assertEquals("The sql generated should add a column, drop a column, modify two columns, drop two indexes," + 
      " add two indexes, and add a charset", true, sql.contains(expectedSQL));   
  }

  @Test
  /**
   * Tests whether the equals function catches the adding of indices.
   * @author Peter Kaufman
   */
  public void testIndexAddition() {
    ArrayList<String> sql;
    String expectedSQL = "ALTER TABLE `ci_sessions`\nCREATE INDEX `add` (`id`);";
    // setup table1
    name = "ci_sessions";
    create = "CREATE TABLE `ci_sessions` (\n  `id` varchar(40) NOT NULL,\n  `ip_address` varchar(45) NOT NULL,\n  `timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  `data` blob NOT NULL,\n  PRIMARY KEY (`id`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    table1 = new Table(name, create);
    // add indexes
    name = "leave";
    columns = "`data`,`id`";
    create = "CREATE INDEX `" + name + "` (" + columns + ")";
    table1.addIndex(new Index(name, create, columns));
    // setup table2
    name = "ci_sessions";
    create = "CREATE TABLE `ci_sessions` (\n  `ip_address` varchar(40) NOT NULL,\n  `timestamp` int(11) unsigned NOT NULL DEFAULT \'0\',\n  `data` blob NOT NULL,\n  `data2` blob NOT NULL,\n PRIMARY KEY (`id`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    table2 = new Table(name, create);
    // add indexes
    name = "leave";
    columns = "`data`,`id`";
    create = "CREATE INDEX `" + name + "` (" + columns + ")";
    table2.addIndex(new Index(name, create, columns));
    // do comparison with equals
    sql = table1.equals(table2);
    assertEquals("The sql generated should be empty since all indexes are the same", 0, sql.size());
    // one index add
    name = "add";
    columns = "`id`";
    create = "CREATE INDEX `" + name + "` (" + columns + ")";
    table1.addIndex(new Index(name, create, columns));   
    sql = table1.equals(table2);
    assertEquals("The sql generated should add one index", true, sql.contains(expectedSQL));
    // two index add
    name = "PRIMARY";
    columns = "`id`,`ip_address`";
    create = "ADD PRIMARY KEY` (" + columns + ")";
    table1.addIndex(new Index(name, create, columns));   
    sql = table1.equals(table2);
    expectedSQL = expectedSQL.substring(0, expectedSQL.length() - 1) + ", \n" + create + ";";
    assertEquals("The sql generated should add two indices", expectedSQL, sql.get(0));
  }

  @Test
  /**
   * Tests whether the equals function cathces the dropping of indices.
   * @author Peter Kaufman
   */
  public void testIndexDropping() {
    ArrayList<String> sql;
    String expectedSQL = "ALTER TABLE `ci_sessions`\nDROP INDEX `drop1`;";
    // setup table1
    name = "ci_sessions";
    create = "CREATE TABLE `ci_sessions` (\n  `id` varchar(40) NOT NULL,\n  `ip_address` varchar(45) NOT NULL,\n  `timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  `data` blob NOT NULL,\n  PRIMARY KEY (`id`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    table1 = new Table(name, create);
    // add indexes
    name = "leave";
    columns = "`data`,`id`";
    create = "CREATE INDEX `" + name + "` (" + columns + ")";
    table1.addIndex(new Index(name, create, columns));
    // setup table2
    name = "ci_sessions";
    create = "CREATE TABLE `ci_sessions` (\n  `ip_address` varchar(40) NOT NULL,\n  `timestamp` int(11) unsigned NOT NULL DEFAULT \'0\',\n  `data` blob NOT NULL,\n  `data2` blob NOT NULL,\n PRIMARY KEY (`id`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    table2 = new Table(name, create);
    // add indexes
    name = "leave";
    columns = "`data`,`id`";
    create = "CREATE INDEX `" + name + "` (" + columns + ")";
    table2.addIndex(new Index(name, create, columns));
    // do comparison with equals
    sql = table1.equals(table2);
    assertEquals("The sql generated should be empty since all indexes are the same", 0, sql.size());
    // one index drop
    name = "drop1";
    columns = "`id`";
    create = "CREATE INDEX `" + name + "` (" + columns + ")";
    table1.addIndex(new Index(name, create, columns));   
    sql = table2.equals(table1);
    assertEquals("The sql generated should drop one index", true, sql.contains(expectedSQL));
    // two index drop
    name = "drop2";
    columns = "`id`,`ip_address`";
    create = "CREATE INDEX `" + name + "` (" + columns + ")";
    table1.addIndex(new Index(name, create, columns));   
    sql = table2.equals(table1);
    expectedSQL = expectedSQL.substring(0, expectedSQL.length() - 1) + ", \nDROP INDEX `" + name + "`;";
    assertEquals("The sql generated should drop two indices", expectedSQL, sql.get(0));
  }

  @Test
  /**
   * Tests whether the equals function catches the modifiying of indices.
   * @author Peter Kaufman
   */
  public void testIndexModification() {
    ArrayList<String> sql;
    String expectedSQL = "ALTER TABLE `ci_sessions`\nDROP INDEX `modify1`, \nCREATE UNIQUE INDEX `modify1` (`id`);";
    // setup table1
    name = "ci_sessions";
    create = "CREATE TABLE `ci_sessions` (\n  `id` varchar(40) NOT NULL,\n  `ip_address` varchar(45) NOT NULL,\n  `timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  `data` blob NOT NULL,\n  PRIMARY KEY (`id`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    table1 = new Table(name, create);
    // add indexes
    name = "leave";
    columns = "`data`,`id`";
    create = "CREATE INDEX `" + name + "` (" + columns + ")";
    table1.addIndex(new Index(name, create, columns));
    // setup table2
    name = "ci_sessions";
    create = "CREATE TABLE `ci_sessions` (\n  `ip_address` varchar(40) NOT NULL,\n  `timestamp` int(11) unsigned NOT NULL DEFAULT \'0\',\n  `data` blob NOT NULL,\n  `data2` blob NOT NULL,\n PRIMARY KEY (`id`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    table2 = new Table(name, create);
    // add indexes
    name = "leave";
    columns = "`data`,`id`";
    create = "CREATE INDEX `" + name + "` (" + columns + ")";
    table2.addIndex(new Index(name, create, columns));
    // do comparison with equals
    sql = table1.equals(table2);
    assertEquals("The sql generated should be empty since all indexes are the same", 0, sql.size());
    // one column modify
    name = "modify1";
    columns = "`id`";
    create = "CREATE INDEX `" + name + "`(" + columns + ")";
    table1.addIndex(new Index(name, create, columns));
    columns = "`id`";
    create = "CREATE UNIQUE INDEX `" + name + "` (" + columns + ")";
    table2.addIndex(new Index(name, create, columns));      
    sql = table2.equals(table1);
    assertEquals("The sql generated should modify one index", true, sql.contains(expectedSQL));
    // two column modify
    name = "modify2";
    columns = "`id`,`ip_address`";
    create = "CREATE INDEX `" + name + "` (" + columns + ")";
    table1.addIndex(new Index(name, create, columns)); 
    columns = "`ip_address`";
    create = "CREATE INDEX `" + name + "` (" + columns + ")";
    table2.addIndex(new Index(name, create, columns)); 
    sql = table2.equals(table1);
    expectedSQL = expectedSQL.substring(0, expectedSQL.length() - 1) + ", \nDROP INDEX `" + name + "`, \n" + create + ";";
    assertEquals("The sql generated should modify two indices", expectedSQL, sql.get(0));
  }

  @Test
  /**
   * Tests whether the equals function catches the adding of columns.
   * @author Peter Kaufman
   */
  public void testColumnAddition() {
    ArrayList<String> sql;
    String expectedSQL = "ALTER TABLE `ci_sessions`\nADD COLUMN `id` varchar(40) NOT NULL AFTER `ip_address`;";
    // setup table1
    name = "ci_sessions";
    create = "CREATE TABLE `ci_sessions` (\n  `id` varchar(40) NOT NULL,\n  `ip_address` varchar(45) NOT NULL,\n  `timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  `data` blob NOT NULL,\n  PRIMARY KEY (`id`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    table1 = new Table(name, create);
    // add columns
    name = "ip_address";
    details = "varchar(45) NOT NULL";
    table1.addColumn(new Column(name, details));
    name = "timestamp";
    details = "int(10) unsigned NOT NULL DEFAULT \'0\'";
    table1.addColumn(new Column(name, details));
    name = "data";
    details = "blob NOT NULL";
    table1.addColumn(new Column(name, details));
    // setup table2
    name = "ci_sessions";
    create = "CREATE TABLE `ci_sessions` (\n  `ip_address` varchar(45) NOT NULL,\n  `timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  `data` blob NOT NULL,\n  `data2` blob NOT NULL,\n PRIMARY KEY (`id`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    table2 = new Table(name, create);
    // add columns
    name = "ip_address";
    details = "varchar(45) NOT NULL";
    table2.addColumn(new Column(name, details));
    name = "timestamp";
    details = "int(10) unsigned NOT NULL DEFAULT \'0\'";
    table2.addColumn(new Column(name, details));
    name = "data";
    details = "blob NOT NULL";
    table2.addColumn(new Column(name, details));
    // do comparison with equals
    sql = table1.equals(table2);
    assertEquals("The sql generated should be empty because all table columns are the same",
      0, sql.size()); 
    // one column addition
    name = "id";
    details = "varchar(40) NOT NULL";
    table1.addColumn(new Column(name, details));
    sql = table1.equals(table2);
    assertEquals("The sql generated should have one column addition",
      true, sql.contains(expectedSQL)); 
    // two column addition  
    name = "data2";
    details = "blob NOT NULL";
    table1.addColumn(new Column(name, details));
    sql = table1.equals(table2);
    expectedSQL = "ALTER TABLE `ci_sessions`\nADD COLUMN `data2` blob NOT NULL AFTER `data`, \n" +
      "ADD COLUMN `id` varchar(40) NOT NULL AFTER `ip_address`;";
    assertEquals("The sql generated should be a two column addition",
      true, sql.contains(expectedSQL)); 
  }

  @Test
  /**
   * Tests whether the equals function cathces the dropping of columns.
   * @author Peter Kaufman
   */
  public void testColumnDropping() {
    ArrayList<String> sql;
    String expectedSQL = "ALTER TABLE `ci_sessions`\nDROP COLUMN `id`;";
    // setup table1
    name = "ci_sessions";
    create = "CREATE TABLE `ci_sessions` (\n  `id` varchar(40) NOT NULL,\n  `ip_address` varchar(45) NOT NULL,\n  `timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  `data` blob NOT NULL,\n  PRIMARY KEY (`id`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    table1 = new Table(name, create);
    // add columns
    name = "ip_address";
    details = "varchar(45) NOT NULL";
    table1.addColumn(new Column(name, details));
    name = "timestamp";
    details = "int(10) unsigned NOT NULL DEFAULT \'0\'";
    table1.addColumn(new Column(name, details));
    name = "data";
    details = "blob NOT NULL";
    table1.addColumn(new Column(name, details));
    // setup table2
    name = "ci_sessions";
    create = "CREATE TABLE `ci_sessions` (\n  `ip_address` varchar(45) NOT NULL,\n  `timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  `data` blob NOT NULL,\n  `data2` blob NOT NULL,\n PRIMARY KEY (`id`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    table2 = new Table(name, create);
    // add columns
    name = "ip_address";
    details = "varchar(45) NOT NULL";
    table2.addColumn(new Column(name, details));
    name = "timestamp";
    details = "int(10) unsigned NOT NULL DEFAULT \'0\'";
    table2.addColumn(new Column(name, details));
    name = "data";
    details = "blob NOT NULL";
    table2.addColumn(new Column(name, details));
    // do comparison with equals
    sql = table2.equals(table1);
    assertEquals("The sql generated should be empty because all table columns are the same",
      0, sql.size()); 
    // one column drop
    name = "id";
    details = "varchar(40) NOT NULL";
    table1.addColumn(new Column(name, details));
    sql = table2.equals(table1);
    assertEquals("The sql generated should have one column drop",
      true, sql.contains(expectedSQL)); 
    // two column drop  
    name = "data2";
    details = "blob NOT NULL";
    table1.addColumn(new Column(name, details));
    sql = table2.equals(table1);
    expectedSQL = "ALTER TABLE `ci_sessions`\nDROP COLUMN `data2`, \nDROP COLUMN `id`;";
    assertEquals("The sql generated should be a two column drop",
      true, sql.contains(expectedSQL));
  }

  @Test
  /**
   * Tests whether the equals function catches the modifiying of columns.
   * @author Peter Kaufman
   */
  public void testColumnModification() {
    ArrayList<String> sql;
    String expectedSQL = "ALTER TABLE `ci_sessions`\nMODIFY COLUMN `timestamp` int(10) unsigned NOT NULL DEFAULT \'0\';";
    // setup table1
    name = "ci_sessions";
    create = "CREATE TABLE `ci_sessions` (\n  `id` varchar(40) NOT NULL,\n  `ip_address` varchar(45) NOT NULL,\n  `timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  `data` blob NOT NULL,\n  PRIMARY KEY (`id`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    table1 = new Table(name, create);
    // add columns
    name = "ip_address";
    details = "varchar(45) NOT NULL";
    table1.addColumn(new Column(name, details));
    
    // setup table2
    name = "ci_sessions";
    create = "CREATE TABLE `ci_sessions` (\n  `ip_address` varchar(45) NOT NULL,\n  `timestamp` int(11) unsigned NOT NULL DEFAULT \'0\',\n  `data` blob NOT NULL,\n  `data2` blob NOT NULL,\n PRIMARY KEY (`id`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    table2 = new Table(name, create);
    // add columns
    name = "ip_address";
    details = "varchar(45) NOT NULL";
    table2.addColumn(new Column(name, details));
    // do comparison with equals
    sql = table1.equals(table2);
    assertEquals("The sql generated should be empty because all table columns are the same",
      0, sql.size()); 
    // one column modify
    name = "timestamp";
    details = "int(10) unsigned NOT NULL DEFAULT \'0\'";
    table1.addColumn(new Column(name, details));
    name = "timestamp";
    details = "int(11) unsigned NOT NULL DEFAULT \'0\'";
    table2.addColumn(new Column(name, details));
    sql = table1.equals(table2);
    assertEquals("The sql generated should have one column modification",
      true, sql.contains(expectedSQL)); 
    // two column modify  
    name = "data2";
    details = "blob NOT NULL";
    table1.addColumn(new Column(name, details));
    name = "data2";
    details = "blob";
    table2.addColumn(new Column(name, details));
    sql = table1.equals(table2);
    expectedSQL = "ALTER TABLE `ci_sessions`\nMODIFY COLUMN `data2` blob NOT NULL, \n" +
      "MODIFY COLUMN `timestamp` int(10) unsigned NOT NULL DEFAULT '0';";
    assertEquals("The sql generated should have two column modifications",
      true, sql.contains(expectedSQL));
  }
}