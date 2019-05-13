import static org.junit.Assert.assertEquals;
import org.junit.Test;
import java.util.ArrayList;
import dbdiffchecker.Column;
import dbdiffchecker.Index;
import dbdiffchecker.Table;
import dbdiffchecker.SQLiteTable;

/**
 * TableTest is a unit test that makes sure that the Table object works as intended.
 * @author Peter Kaufman
 * @version 5-11-19
 * @since 5-10-19
 */
public class SQLiteTableTest {

  private Table table1, table2;
  private String name, create, details, columns;

  @Test
  /**
   * Tests whether the equals function catches the addition of several columns.
   * @author Peter Kaufman
   */
  public void testAddColumns() {
    ArrayList<String> sql;
    String expectedSQL = "ALTER TABLE `helper`\n\tADD COLUMN `Thor` INTEGER (67) DEFAULT (12);";
    // setup table1
    name = "helper";
    create = "CREATE TABLE helper (hulk STRING (12), Thor INTEGER (67) DEFAULT (12))";
    table1 = new SQLiteTable(name, create);
    // add columns
    name = "hulk";
    details = "STRING (12)";
    table1.addColumn(new Column(name, details));
    // setup table2
    name = "helper";
    create = "CREATE TABLE helper (hulk STRING (12))";
    table2 = new SQLiteTable(name, create);
    // add columns
    name = "hulk";
    details = "STRING (12)";
    table2.addColumn(new Column(name, details));
   // no column is needed to be added
    sql = table1.equals(table2);
    assertEquals("There should be no sql generated for two tables with the exact same columns",
      0, sql.size()); 
    // add a column
    name = "Thor";
    details = "INTEGER (67) DEFAULT (12)";
    table1.addColumn(new Column(name, details));
    sql = table1.equals(table2);
    assertEquals("The sql generated should add a column", expectedSQL, sql.get(0)); 
    // add two columns to a table
    name = "truthtable";
    details = "STRING (12)";
    table1.addColumn(new Column(name, details));
    expectedSQL = "ALTER TABLE `helper`\n\tADD COLUMN `Thor` INTEGER (67) DEFAULT (12);\n" + 
      "ALTER TABLE `helper`\n\tADD COLUMN `truthtable` STRING (12);";  
    sql = table1.equals(table2);
    assertEquals("The sql generated should add two columns", expectedSQL, sql.get(0)); 
  }

  @Test
  /**
   * Tests whether the equals function catches the addition of regular indices (not testing primary key).
   * @author Peter Kaufman
   */
  public void testAddIndicesRegular() {
    ArrayList<String> sql;
    String expectedSQL = "CREATE UNIQUE INDEX `add2` ON `helper` (`Thor`);\n" + 
      "CREATE INDEX `add1` ON `helper` (`hulk`,`Thor`);";
    // setup table1
    name = "helper";
    create = "CREATE TABLE helper (hulk STRING (12), Thor INTEGER (67) DEFAULT (12))";
    table1 = new SQLiteTable(name, create);
    // add indices
    name = "addition";
    columns = "`hulk`";
    create = "CREATE INDEX `" + name + "` ON `" + table1.getName() + "` (" + columns + ")";
    table1.addIndex(new Index(name, create, columns));
    // setup table2
    name = "helper";
    create = "CREATE TABLE helper (hulk STRING (12))";
    table2 = new SQLiteTable(name, create);
    // add indices
    name = "addition";
    columns = "`hulk`";
    create = "CREATE INDEX `" + name + "` ON `" + table1.getName() + "` (" + columns + ")";
    table2.addIndex(new Index(name, create, columns));
    // no index is needed to be added
    sql = table1.equals(table2);
    assertEquals("There should be no sql generated for two tables with the exact same indices",
      0, sql.size()); 
    // one index different of type Unique and of a regular type is needed to be added
    name = "add1";
    columns = "`hulk`,`Thor`";
    create = "CREATE INDEX `" + name + "` ON `" + table1.getName() + "` (" + columns + ")";
    table1.addIndex(new Index(name, create, columns));
    name = "add2";
    columns = "`Thor`";
    create = "CREATE UNIQUE INDEX `" + name + "` ON `" + table1.getName() + "` (" + columns + ")";
    table1.addIndex(new Index(name, create, columns));
    sql = table1.equals(table2);
    assertEquals("The sql generated should add two indices", expectedSQL, sql.get(0));
  }

  @Test
  /**
   * Tests whether the equals function catches the dropping of regular indices (not testing primary key).
   * @author Peter Kaufman
   */
  public void testDropIndicesRegular() {
    ArrayList<String> sql;
    String expectedSQL = "DROP INDEX `add2`;\nDROP INDEX `add1`;";
    // setup table1
    name = "helper";
    create = "CREATE TABLE helper (hulk STRING (12), Thor INTEGER (67) DEFAULT (12))";
    table1 = new SQLiteTable(name, create);
    // add indices
    name = "addition";
    columns = "`hulk`";
    create = "CREATE INDEX `" + name + "` ON `" + table1.getName() + "` (" + columns + ")";
    table1.addIndex(new Index(name, create, columns));
    // setup table2
    name = "helper";
    create = "CREATE TABLE helper (hulk STRING (12))";
    table2 = new SQLiteTable(name, create);
    // add indices
    name = "addition";
    columns = "`hulk`";
    create = "CREATE INDEX `" + name + "` ON `" + table1.getName() + "` (" + columns + ")";
    table2.addIndex(new Index(name, create, columns));
    // no index is needed to be dropped
    sql = table1.equals(table2);
    assertEquals("There should be no sql generated for two tables with the exact same indices",
      0, sql.size()); 
    // one index different of type Unique and of a regular type is needed to be dropped
    name = "add1";
    columns = "`hulk`,`Thor`";
    create = "CREATE INDEX `" + name + "` ON `" + table1.getName() + "` (" + columns + ")";
    table2.addIndex(new Index(name, create, columns));
    name = "add2";
    columns = "`Thor`";
    create = "CREATE UNIQUE INDEX `" + name + "` ON `" + table1.getName() + "` (" + columns + ")";
    table2.addIndex(new Index(name, create, columns));
    sql = table1.equals(table2);
    assertEquals("The sql generated should drop two indices", expectedSQL, sql.get(0));
  }

  @Test
  /**
   * Tests whether the equals function catches the modifying of regular indices (not testing primary key).
   * @author Peter Kaufman
   */
  public void testModifyIndicesRegular() { 
    ArrayList<String> sql;
    String expectedSQL = "DROP INDEX `add1`;\nCREATE INDEX `add1` ON `helper` (`hulk`,`Thor`);";
    // setup table1
    name = "helper";
    create = "CREATE TABLE helper (hulk STRING (12), Thor INTEGER (67) DEFAULT (12))";
    table1 = new SQLiteTable(name, create);
    // add indices
    name = "addition";
    columns = "`hulk`";
    create = "CREATE INDEX `" + name + "` ON `" + table1.getName() + "` (" + columns + ")";
    table1.addIndex(new Index(name, create, columns));
    // setup table2
    name = "helper";
    create = "CREATE TABLE helper (hulk STRING (12))";
    table2 = new SQLiteTable(name, create);
    // add indices
    name = "addition";
    columns = "`hulk`";
    create = "CREATE INDEX `" + name + "` ON `" + table1.getName() + "` (" + columns + ")";
    table2.addIndex(new Index(name, create, columns));
    // no index is needed to be dropped
    sql = table1.equals(table2);
    assertEquals("There should be no sql generated for two tables with the exact same indices",
      0, sql.size()); 
    // one index is needed to be modified
    name = "add1";
    columns = "`hulk`,`Thor`";
    create = "CREATE INDEX `" + name + "` ON `" + table1.getName() + "` (" + columns + ")";
    table1.addIndex(new Index(name, create, columns));
    columns = "`Thor`";
    create = "CREATE INDEX `" + name + "` ON `" + table1.getName() + "` (" + columns + ")";
    table2.addIndex(new Index(name, create, columns));
    sql = table1.equals(table2);
    assertEquals("The sql generated should modify one index", expectedSQL, sql.get(0));
    // two indices are needed to be modified
    name = "add2";
    columns = "`Thor`";
    create = "CREATE INDEX `" + name + "` ON `" + table1.getName() + "` (" + columns + ")";
    table1.addIndex(new Index(name, create, columns));
    columns = "`Thor`";
    create = "CREATE UNIQUE INDEX `" + name + "` ON `" + table1.getName() + "` (" + columns + ")";
    table2.addIndex(new Index(name, create, columns));
    sql = table1.equals(table2);
    expectedSQL = "DROP INDEX `add2`;\nCREATE INDEX `add2` ON `helper` (`Thor`);\n" + 
      "DROP INDEX `add1`;\nCREATE INDEX `add1` ON `helper` (`hulk`,`Thor`);";
    assertEquals("The sql generated should modify two indices", expectedSQL, sql.get(0));
  }

  @Test
  /**
   * Tests whether the equals function catches the need to recreate a table.
   * @author Peter Kaufman
   */
  public void testRecreateTable() { 
    ArrayList<String> sql, expectedSQL = new ArrayList<>();
    expectedSQL.add("PRAGMA foreign_keys=off;");
    expectedSQL.add("ALTER TABLE `helper` RENAME TO `temp_table`;");
    expectedSQL.add("CREATE TABLE helper (hulk STRING (12));");
    expectedSQL.add("INSERT INTO `helper` (`hulk`)\n\tSELECT `hulk`\n\tFROM `temp_table`;");
    expectedSQL.add("DROP TABLE `temp_table`;");
    expectedSQL.add("PRAGMA foreign_keys=on;");
    // setup table1
    name = "helper";
    create = "CREATE TABLE helper (hulk STRING (12), Thor INTEGER (67) DEFAULT (12))";
    table1 = new SQLiteTable(name, create);
    // add columns
    name = "hulk";
    details = "STRING (12)";
    table1.addColumn(new Column(name, details));
    name = "Thor";
    details = "INTEGER (67) DEFAULT (12)";
    table1.addColumn(new Column(name, details));
    // setup table2
    name = "helper";
    create = "CREATE TABLE helper (hulk STRING (12))";
    table2 = new SQLiteTable(name, create);
    // add columns
    name = "hulk";
    details = "STRING (12)";
    table2.addColumn(new Column(name, details));
    // dropping a column
    sql = table2.equals(table1);
    assertEquals("The sql generated should recreate the table to be like the dev one when dropping a column",
      true, expectedSQL.equals(sql));
    // dropping two columns
    name = "truthtable";
    details = "STRING (12)";
    table1.addColumn(new Column(name, details));
    sql = table2.equals(table1);
    assertEquals("The sql generated should recreate the table to be like the dev one when dropping several columns",
      true, expectedSQL.equals(sql));
    // modifying a column
    name = "helper";
    create = "CREATE TABLE helper (hulk STRING (11), truthtable INT (67) DEFAULT (12))";
    table1 = new SQLiteTable(name, create);
    // add columns
    name = "hulk";
    details = "STRING (11)";
    table1.addColumn(new Column(name, details));
    sql = table2.equals(table1);
    assertEquals("The sql generated should recreate the table to be like the dev one when modifying a column",
      true, expectedSQL.equals(sql));
    // modifying two columns
    name = "truthtable";
    details = "INT (67) DEFAULT (12)";
    table1.addColumn(new Column(name, details));
    assertEquals("The sql generated should recreate the table to be like the dev one when modifying several columns",
      true, expectedSQL.equals(sql));
    // add a primary key
    // table1 setup
    name = "blooper";
    create = "CREATE TABLE blooper (Thor INTEGER (67) DEFAULT (12), ache BLOB (66) PRIMARY KEY)"; 
    table1 = new SQLiteTable(name, create);
    // add columns
    name = "Thor";
    details = "INTEGER (67) DEFAULT (12)";
    table1.addColumn(new Column(name, details));
    name = "ache";
    details = "BLOB (66) PRIMARY KEY";
    table1.addColumn(new Column(name, details));
    // table2 setup
    name = "blooper";
    create = "CREATE TABLE blooper (Thor INTEGER (67) DEFAULT (12), ache BLOB (66))"; 
    table2 = new SQLiteTable(name, create);
    // add columns
    name = "Thor";
    details = "INTEGER (67) DEFAULT (12)";
    table2.addColumn(new Column(name, details));
    name = "ache";
    details = "BLOB (66)";
    table2.addColumn(new Column(name, details));
    sql = table1.equals(table2);
    expectedSQL = new ArrayList<>();
    expectedSQL.add("PRAGMA foreign_keys=off;");
    expectedSQL.add("ALTER TABLE `blooper` RENAME TO `temp_table`;");
    expectedSQL.add("CREATE TABLE blooper (Thor INTEGER (67) DEFAULT (12), ache BLOB (66) PRIMARY KEY);");
    expectedSQL.add("INSERT INTO `blooper` (`Thor`,`ache`)\n\tSELECT `Thor`,`ache`\n\tFROM `temp_table`;");
    expectedSQL.add("DROP TABLE `temp_table`;");
    expectedSQL.add("PRAGMA foreign_keys=on;");
    assertEquals("The sql generated should recreate the table to be like the dev one when adding a primary key",
      true, expectedSQL.equals(sql));
    // modify a primary key
    name = "ache";
    details = "BLOB (6) PRIMARY KEY";
    table2.addColumn(new Column(name, details));
    sql = table1.equals(table2);
    assertEquals("The sql generated should recreate the table to be like the dev one when modifying a primary key",
      true, expectedSQL.equals(sql));
    // drop a primary key
    expectedSQL = new ArrayList<>();
    expectedSQL.add("PRAGMA foreign_keys=off;");
    expectedSQL.add("ALTER TABLE `blooper` RENAME TO `temp_table`;");
    expectedSQL.add("CREATE TABLE blooper (Thor INTEGER (67) DEFAULT (12), ache BLOB (66));");
    expectedSQL.add("INSERT INTO `blooper` (`Thor`,`ache`)\n\tSELECT `Thor`,`ache`\n\tFROM `temp_table`;");
    expectedSQL.add("DROP TABLE `temp_table`;");
    expectedSQL.add("PRAGMA foreign_keys=on;");
    name = "ache";
    details = "BLOB (66)";
    table2.addColumn(new Column(name, details));
    sql = table2.equals(table1);
    assertEquals("The sql generated should recreate the table to be like the dev one when dropping a primary key",
      true, expectedSQL.equals(sql));
  }

  @Test
  /**
   * Tests whether the equals function correctly recreates a table when special conditions apply.
   * @author Peter Kaufman
   */
  public void testRecreateTableSpecial() {
    ArrayList<String> sql, expectedSQL = new ArrayList<>();
    String extraCreate = "CREATE INDEX addition ON helper (`Thor`)";
    expectedSQL.add("PRAGMA foreign_keys=off;");
    expectedSQL.add("ALTER TABLE `helper` RENAME TO `temp_table`;");
    expectedSQL.add("CREATE TABLE helper (hulk STRING (12));");
    expectedSQL.add("INSERT INTO `helper` (`hulk`)\n\tSELECT `hulk`\n\tFROM `temp_table`;");
    expectedSQL.add("DROP TABLE `temp_table`;");
    expectedSQL.add(extraCreate + ";");
    expectedSQL.add("PRAGMA foreign_keys=on;");
    name = "helper";
    create = "CREATE TABLE helper (hulk STRING (12), Thor INTEGER (67) DEFAULT (12))";
    table1 = new SQLiteTable(name, create);
    // add columns
    name = "hulk";
    details = "STRING (12)";
    table1.addColumn(new Column(name, details));
    name = "Thor";
    details = "INTEGER (67) DEFAULT (12)";
    table1.addColumn(new Column(name, details));
    // setup table2
    name = "helper";
    create = "CREATE TABLE helper (hulk STRING (12));\n" + extraCreate;
    table2 = new SQLiteTable(name, create);
    // add columns
    name = "hulk";
    details = "STRING (12)";
    table2.addColumn(new Column(name, details));
    // extra create statements are present in the create statement of the table
    sql = table2.equals(table1);
    assertEquals("The sql generated should recreate the table to be like the dev" + 
      " one when extra create statements are present in table create statement",
      true, expectedSQL.equals(sql));
    // the two tables have no columns in common
    // setup table2
    name = "helper";
    create = "CREATE TABLE helper (hawkeye STRING (12))";
    table2 = new SQLiteTable(name, create);
    // add columns
    name = "hawkeye";
    details = "STRING (12)";
    table2.addColumn(new Column(name, details));
    sql = table2.equals(table1);
    expectedSQL = new ArrayList<>();
    expectedSQL.add("DROP TABLE `helper`;");
    expectedSQL.add(create + ";");
    assertEquals("The sql generated should recreate the table to be like the dev" + 
      " one when no columns are common between the two", true, expectedSQL.equals(sql));
    // the two tables have no columns in common and there are extra create statements
    // in the table create statement
     // setup table2
     name = "helper";
     create = "CREATE TABLE helper (hawkeye STRING (12));\n" + extraCreate;
     table2 = new SQLiteTable(name, create);
     // add columns
     name = "hawkeye";
     details = "STRING (12)";
     table2.addColumn(new Column(name, details));
     sql = table2.equals(table1);
     expectedSQL = new ArrayList<>();
     expectedSQL.add("DROP TABLE `helper`;");
     expectedSQL.add(create + ";");
     assertEquals("The sql generated should recreate the table to be like the dev" + 
      " one when no columns are common between the two and there are extra create" +
      "statements in the table create statement", true, expectedSQL.equals(sql));
  }

  @Test
  /**
   * Tests whether the equals function works as intended.
   * @author Peter Kaufman
   */
  public void testEquals() {
    ArrayList<String> sql;
    String expectedSQL = "DROP INDEX `drop1`;\nDROP INDEX `drop2`;\n" + 
      "ALTER TABLE `helper`\n\tADD COLUMN `Thor` INTEGER (67) DEFAULT (12);\n" +
      "CREATE UNIQUE INDEX `add2` ON `helper` (`Thor`);\nCREATE INDEX `add1` ON `helper` (`hulk`,`Thor`);";
    // setup table1
    name = "helper";
    create = "CREATE TABLE helper (hulk STRING (12), Thor INTEGER (67) DEFAULT (12))";
    table1 = new SQLiteTable(name, create);
    // add columns
    name = "hulk";
    details = "STRING (12)";
    table1.addColumn(new Column(name, details));
    name = "Thor";
    details = "INTEGER (67) DEFAULT (12)";
    table1.addColumn(new Column(name, details));
    // add indices
    name = "add1";
    columns = "`hulk`,`Thor`";
    create = "CREATE INDEX `" + name + "` ON `" + table1.getName() + "` (" + columns + ")";
    table1.addIndex(new Index(name, create, columns));
    name = "add2";
    columns = "`Thor`";
    create = "CREATE UNIQUE INDEX `" + name + "` ON `" + table1.getName() + "` (" + columns + ")";
    table1.addIndex(new Index(name, create, columns));
    // setup table2
    name = "helper";
    create = "CREATE TABLE helper (hulk STRING (12))";
    table2 = new SQLiteTable(name, create);
    // add columns
    name = "hulk";
    details = "STRING (12)";
    table2.addColumn(new Column(name, details));
    // add indices
    name = "drop1";
    columns = "`hulk`,`Thor`";
    create = "CREATE INDEX `" + name + "` ON `" + table1.getName() + "` (" + columns + ")";
    table2.addIndex(new Index(name, create, columns));
    name = "drop2";
    columns = "`Thor`";
    create = "CREATE UNIQUE INDEX `" + name + "` ON `" + table1.getName() + "` (" + columns + ")";
    table2.addIndex(new Index(name, create, columns));
    // do comparison
    sql = table1.equals(table2);
    assertEquals("The sql generated should add a column, drop two indexes, and add two indexes", 
      true, sql.contains(expectedSQL));   
  }
}