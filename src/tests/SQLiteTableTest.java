import static org.junit.Assert.assertEquals;
import org.junit.Test;
import java.util.ArrayList;
import dbdiffchecker.sql.SQLiteTable;

/**
 * A unit test that makes sure that the SQLiteTable object works as intended.
 * @author Peter Kaufman
 * @version 5-31-19
 * @since 5-10-19
 */
public class SQLiteTableTest {
  private SQLiteTable table1, table2;
  private String name, create;

  @Test
  /**
   * Tests whether the get statements inside of the SQLiteTable object work as
   * intended.
   * @author Peter Kaufman
   */
  public void testGetStatements() {
    name = "helper";
    create = "CREATE TABLE helper (hulk STRING (12), Thor INTEGER (67) DEFAULT (12))";
    table1 = new SQLiteTable(name, create);
    // start assertions
    assertEquals("The name of the table should be the one passed into the constructor", name, table1.getName());
    assertEquals("The create statement of the table should be the one passed into the constructor", create + ";",
        table1.getCreateStatement());
  }

  @Test
  /**
   * Tests whether the add column portion of column parsing works as intended.
   * @author Peter Kaufman
   */
  public void testAddColumn() {
    String column1 = "bloatware", column2 = "shipmentID";
    name = "bloat";
    create = "CREATE TABLE bloat (bloatware INTEGER (11) NOT NULL)";
    table1 = new SQLiteTable();
    assertEquals("The size of the column list for the table should be 0 when empty", 0, table1.getColumns().size());
    // make sure first column is added when the create statement has it
    table1 = new SQLiteTable(name, create);
    assertEquals("The size of the column list for the table should be 1 when one column has been added", 1,
        table1.getColumns().size());
    assertEquals("The column passed to addColumn should be in the tables column list", true,
        table1.getColumns().containsKey(column1));
    // make sure second column is added when the create statement has both
    create = "CREATE TABLE bloat (bloatware INTEGER (11) NOT NULL, shipmentID INTEGER (11) NOT NULL)";
    table1 = new SQLiteTable(name, create);
    assertEquals(
        "The size of the column list for the table should be 2 when two colmns have been added to the column list", 2,
        table1.getColumns().size());
    assertEquals("The column passed to addColumn should be in the tables column list", true,
        table1.getColumns().containsKey(column2));
  }

  @Test
  /**
   * Tests whether the addIndex function works as intended.
   * @author Peter Kaufman
   */
  public void testAddIndex() {
    String index1 = "shipment", index2 = "shipped";
    table1 = new SQLiteTable();
    name = "bloat";
    create = "CREATE TABLE " + name + " (bloatware INTEGER (11) NOT NULL, shipmentID INTEGER (11) NOT NULL);"
        + "\n CREATE INDEX shipment ON " + name + " (shippingID, bloatware)";
    assertEquals("The size of the index list for the table should be 0 when empty", 0, table1.getIndices().size());
    // make sure the one index is exists
    table1 = new SQLiteTable(name, create);
    assertEquals("The size of the column list for the table should be 1 when one index has been added", 1,
        table1.getIndices().size());
    assertEquals("The index passed to addIndex should be in the tables index list", true,
        table1.getIndices().containsKey(index1));
    // make sure the second index is created
    create = create + ";\n  CREATE INDEX shipped ON " + name + " (shippingID)";
    table1 = new SQLiteTable(name, create);
    assertEquals(
        "The size of the index list for the table should be 2 when two indices have been added to the column list", 2,
        table1.getIndices().size());
    assertEquals("The index passed to addIndex should be in the tables index list", true,
        table1.getIndices().containsKey(index2));
  }

  @Test
  /**
   * Tests whether the equals function catches the addition of several columns.
   * @author Peter Kaufman
   */
  public void testAddColumns() {
    ArrayList<String> sql;
    String expectedSQL = "ALTER TABLE helper\n\tADD COLUMN Thor INTEGER (67) DEFAULT (12);";
    // setup tables
    name = "helper";
    create = "CREATE TABLE helper (hulk STRING (12))";
    table1 = new SQLiteTable(name, create);
    table2 = new SQLiteTable(name, create);
    // no column is needed to be added
    sql = table1.equals(table2);
    assertEquals("There should be no sql generated for two tables with the exact same columns", 0, sql.size());
    // add a column
    create = "CREATE TABLE helper (hulk STRING (12), Thor INTEGER (67) DEFAULT (12))";
    table1 = new SQLiteTable(name, create);
    sql = table1.equals(table2);
    assertEquals("The sql generated should add a column", expectedSQL, sql.get(0));
    // add two columns to a table
    create = "CREATE TABLE helper (hulk STRING (12), Thor INTEGER (67) DEFAULT (12), truthtable STRING (12))";
    table1 = new SQLiteTable(name, create);
    expectedSQL = "ALTER TABLE helper\n\tADD COLUMN Thor INTEGER (67) DEFAULT (12);\n"
        + "ALTER TABLE helper\n\tADD COLUMN truthtable STRING (12);";
    sql = table1.equals(table2);
    assertEquals("The sql generated should add two columns", expectedSQL, sql.get(0));
  }

  @Test
  /**
   * Tests whether the equals function catches the addition of regular indices
   * (not testing primary key).
   * @author Peter Kaufman
   */
  public void testAddIndicesRegular() {
    ArrayList<String> sql;
    String expectedSQL = "CREATE UNIQUE INDEX add2 ON helper (Thor);\n" + "CREATE INDEX add1 ON helper (hulk, Thor);";
    // setup tables
    name = "helper";
    create = "CREATE TABLE helper (hulk STRING (12), Thor INTEGER (67) DEFAULT (12));\n"
        + " CREATE INDEX addition ON helper (hulk)";
    table1 = new SQLiteTable(name, create);
    table2 = new SQLiteTable(name, create);
    // no index is needed to be added
    sql = table1.equals(table2);
    assertEquals("There should be no sql generated for two tables with the exact same indices", 0, sql.size());
    // one index different of type Unique and of a regular type is needed to be
    // added
    create += ";\nCREATE INDEX add1 ON helper (hulk, Thor);\nCREATE UNIQUE INDEX add2 ON helper (Thor);  ";
    table1 = new SQLiteTable(name, create);
    sql = table1.equals(table2);
    assertEquals("The sql generated should add two indices", expectedSQL, sql.get(0));
  }

  @Test
  /**
   * Tests whether the equals function catches the dropping of regular indices
   * (not testing primary key).
   * @author Peter Kaufman
   */
  public void testDropIndicesRegular() {
    ArrayList<String> sql;
    String expectedSQL = "DROP INDEX add2;\nDROP INDEX add1;";
    // setup tables
    name = "helper";
    create = "CREATE TABLE helper (hulk STRING (12), Thor INTEGER (67) DEFAULT (12));\n"
        + " CREATE INDEX addition ON helper (hulk)";
    table1 = new SQLiteTable(name, create);
    table2 = new SQLiteTable(name, create);
    // no index is needed to be added
    sql = table1.equals(table2);
    assertEquals("There should be no sql generated for two tables with the exact same indices", 0, sql.size());
    // one index different of type Unique and of a regular type is needed to be
    // added
    create += ";\nCREATE INDEX add1 ON helper (hulk, Thor);\nCREATE INDEX add2 ON helper (Thor)";
    table1 = new SQLiteTable(name, create);
    sql = table2.equals(table1);
    assertEquals("The sql generated should add drop indices", expectedSQL, sql.get(0));
  }

  @Test
  /**
   * Tests whether the equals function catches the modifying of regular indices
   * (not testing primary key).
   * @author Peter Kaufman
   */
  public void testModifyIndicesRegular() {
    ArrayList<String> sql;
    String create1, create2;
    String expectedSQL = "DROP INDEX add1;\nCREATE INDEX add1 ON helper (hulk, Thor);";
    // setup table1
    name = "helper";
    create = "CREATE TABLE helper (hulk STRING (12), Thor INTEGER (67) DEFAULT (12));\n"
        + "CREATE INDEX addition ON helper (hulk)";
    table1 = new SQLiteTable(name, create);
    table2 = new SQLiteTable(name, create);
    // no index is needed to be modified
    sql = table1.equals(table2);
    assertEquals("There should be no sql generated for two tables with the exact same indices", 0, sql.size());
    // one index is needed to be modified
    create1 = create + ";\nCREATE INDEX add1 ON helper (hulk, Thor)";
    table1 = new SQLiteTable(name, create1);
    create2 = create + ";\nCREATE INDEX add1 ON helper (hulk)";
    table2 = new SQLiteTable(name, create2);
    sql = table1.equals(table2);
    assertEquals("The sql generated should modify one index", expectedSQL, sql.get(0));
    // two indices are needed to be modified
    create1 += ";\nCREATE INDEX add2 ON helper (Thor)";
    table1 = new SQLiteTable(name, create1);
    create2 += ";\nCREATE UNIQUE INDEX add2 ON helper (Thor)";
    table2 = new SQLiteTable(name, create2);
    sql = table1.equals(table2);
    expectedSQL = "DROP INDEX add2;\nCREATE INDEX add2 ON helper (Thor);\n"
        + "DROP INDEX add1;\nCREATE INDEX add1 ON helper (hulk, Thor);";
    assertEquals("The sql generated should modify two indices", expectedSQL, sql.get(0));
  }

  @Test
  /**
   * Tests whether the equals function catches the need to recreate a table.
   * @author Peter Kaufman
   */
  public void testRecreateTable() {
    ArrayList<String> sql, expectedSQL = new ArrayList<>();
    String insert1 = "INSERT INTO helper (hulk)\n\tSELECT hulk\n\tFROM temp_table;";
    String insert2 = "INSERT INTO helper (truthtable,hulk)\n\tSELECT truthtable,hulk\n\tFROM temp_table;";
    expectedSQL.add("ALTER TABLE helper RENAME TO temp_table;");
    expectedSQL.add("CREATE TABLE helper (hulk STRING (12));");
    expectedSQL.add(insert1);
    expectedSQL.add("DROP TABLE temp_table;");
    // setup tables
    name = "helper";
    create = "CREATE TABLE helper (hulk STRING (12), Thor INTEGER (67) DEFAULT (12))";
    table1 = new SQLiteTable(name, create);
    create = "CREATE TABLE helper (hulk STRING (12))";
    table2 = new SQLiteTable(name, create);
    // dropping a column
    sql = table2.equals(table1);
    assertEquals("The sql generated should recreate the table to be like the dev one when dropping a column",
        expectedSQL, sql);
    // dropping two columns
    create = "CREATE TABLE helper (hulk STRING (12), Thor INTEGER (67) DEFAULT (12), truthtable STRING (12))";
    table1 = new SQLiteTable(name, create);
    sql = table2.equals(table1);
    assertEquals("The sql generated should recreate the table to be like the dev one when dropping several columns",
        expectedSQL, sql);
    // modifying a column
    create = "CREATE TABLE helper (hulk STRING (11))";
    table1 = new SQLiteTable(name, create);
    sql = table2.equals(table1);
    assertEquals("The sql generated should recreate the table to be like the dev one when modifying a column",
        expectedSQL, sql);
    // modifying two columns
    create = "CREATE TABLE helper (hulk STRING (11), truthtable STRING (12))";
    table1 = new SQLiteTable(name, create);
    create = "CREATE TABLE helper (hulk STRING (12), truthtable INTEGER (67) DEFAULT (12))";
    table2 = new SQLiteTable(name, create);
    expectedSQL.set(1, create + ";");
    expectedSQL.set(2, insert2);
    sql = table2.equals(table1);
    assertEquals("The sql generated should recreate the table to be like the dev one when modifying several columns",
        expectedSQL, sql);
    // add a primary key
    // setup tables
    name = "blooper";
    create = "CREATE TABLE blooper (Thor INTEGER (67) DEFAULT (12), ache BLOB (66) PRIMARY KEY)";
    table1 = new SQLiteTable(name, create);
    create = "CREATE TABLE blooper (Thor INTEGER (67) DEFAULT (12), ache BLOB (66))";
    table2 = new SQLiteTable(name, create);
    sql = table1.equals(table2);
    expectedSQL.clear();
    expectedSQL.add("ALTER TABLE blooper RENAME TO temp_table;");
    expectedSQL.add("CREATE TABLE blooper (Thor INTEGER (67) DEFAULT (12), ache BLOB (66) PRIMARY KEY);");
    expectedSQL.add("INSERT INTO blooper (Thor,ache)\n\tSELECT Thor,ache\n\tFROM temp_table;");
    expectedSQL.add("DROP TABLE temp_table;");
    assertEquals("The sql generated should recreate the table to be like the dev one when adding a primary key",
        expectedSQL, sql);
    // modify a primary key
    create = "CREATE TABLE blooper (Thor INTEGER (67) DEFAULT (12), ache BLOB (6) PRIMARY KEY)";
    table2 = new SQLiteTable(name, create);
    sql = table1.equals(table2);
    assertEquals("The sql generated should recreate the table to be like the dev one when modifying a primary key",
        expectedSQL, sql);
    // drop a primary key
    create = "CREATE TABLE blooper (Thor INTEGER (67) DEFAULT (12), ache BLOB (66))";
    table2 = new SQLiteTable(name, create);
    expectedSQL.clear();
    expectedSQL.add("ALTER TABLE blooper RENAME TO temp_table;");
    expectedSQL.add("CREATE TABLE blooper (Thor INTEGER (67) DEFAULT (12), ache BLOB (66));");
    expectedSQL.add("INSERT INTO blooper (Thor,ache)\n\tSELECT Thor,ache\n\tFROM temp_table;");
    expectedSQL.add("DROP TABLE temp_table;");
    sql = table2.equals(table1);
    assertEquals("The sql generated should recreate the table to be like the dev one when dropping a primary key",
        expectedSQL, sql);
  }

  @Test
  /**
   * Tests whether the equals function correctly recreates a table when special
   * conditions apply.
   * @author Peter Kaufman
   */
  public void testRecreateTableSpecial() {
    ArrayList<String> sql, expectedSQL = new ArrayList<>();
    String extraCreate = "CREATE INDEX addition ON helper (Thor)";
    expectedSQL.add("ALTER TABLE helper RENAME TO temp_table;");
    expectedSQL.add("CREATE TABLE helper (hulk STRING (12));");
    expectedSQL.add("INSERT INTO helper (hulk)\n\tSELECT hulk\n\tFROM temp_table;");
    expectedSQL.add("DROP TABLE temp_table;");
    expectedSQL.add(extraCreate + ";");
    // setup tables
    name = "helper";
    create = "CREATE TABLE helper (hulk STRING (12), Thor INTEGER (67) DEFAULT (12))";
    table1 = new SQLiteTable(name, create);
    create = "CREATE TABLE helper (hulk STRING (12));\n" + extraCreate;
    table2 = new SQLiteTable(name, create);
    // extra create statements are present in the create statement of the table
    sql = table2.equals(table1);
    assertEquals("The sql generated should recreate the table to be like the dev"
        + " one when extra create statements are present in table create statement", true, expectedSQL.equals(sql));
    // the two tables have no columns in common
    // setup table2
    create = "CREATE TABLE helper (hawkeye STRING (12))";
    table2 = new SQLiteTable(name, create);
    sql = table2.equals(table1);
    expectedSQL = new ArrayList<>();
    expectedSQL.add("DROP TABLE helper;");
    expectedSQL.add(create + ";");
    assertEquals("The sql generated should recreate the table to be like the dev"
        + " one when no columns are common between the two", true, expectedSQL.equals(sql));
    // the two tables have no columns in common and there are extra create
    // statements
    // in the table create statement
    // setup table2
    create = "CREATE TABLE helper (hawkeye STRING (12));\n" + extraCreate;
    table2 = new SQLiteTable(name, create);
    sql = table2.equals(table1);
    expectedSQL = new ArrayList<>();
    expectedSQL.add("DROP TABLE helper;");
    expectedSQL.add(create + ";");
    assertEquals("The sql generated should recreate the table to be like the dev"
        + " one when no columns are common between the two and there are extra create"
        + "statements in the table create statement", true, expectedSQL.equals(sql));
  }

  @Test
  /**
   * Tests whether the equals function works as intended.
   * @author Peter Kaufman
   */
  public void testEquals() {
    ArrayList<String> sql;
    String expectedSQL = "DROP INDEX drop1;\nDROP INDEX drop2;\n"
        + "ALTER TABLE helper\n\tADD COLUMN Thor INTEGER (67) DEFAULT (12);\n"
        + "CREATE UNIQUE INDEX add2 ON helper (Thor);\nCREATE INDEX add1 ON helper (hulk, Thor);";
    // setup tables
    name = "helper";
    create = "CREATE TABLE helper (hulk STRING (12), Thor INTEGER (67) DEFAULT (12));\n"
        + "CREATE INDEX add1 ON helper (hulk, Thor);\n CREATE UNIQUE INDEX add2 ON helper (Thor)";
    table1 = new SQLiteTable(name, create);
    create = "CREATE TABLE helper (hulk STRING (12));\n"
        + "CREATE INDEX drop1 ON helper (hulk, Thor);\n CREATE UNIQUE INDEX drop2 ON helper (Thor)";
    table2 = new SQLiteTable(name, create);
    // do comparison
    sql = table1.equals(table2);
    assertEquals("The sql generated should add a column, drop two indexes, and add two indexes", true,
        sql.contains(expectedSQL));
  }
}
