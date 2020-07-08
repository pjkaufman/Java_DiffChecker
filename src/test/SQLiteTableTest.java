package test;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;

import dbdiffchecker.sql.SQLiteTable;

/**
 * A unit test that makes sure that the SQLiteTable object works as intended.
 *
 * @author Peter Kaufman
 * @version 7-7-20
 * @since 5-10-19
 */
public class SQLiteTableTest {
  private static final String TABLE_NAME_1 = "helper";
  private static final String TABLE_NAME_2 = "bloat";
  private static final String CREATE_STATEMENT_1 = String
      .format("CREATE TABLE %s (hulk STRING (12), Thor INTEGER (67) DEFAULT (12))", TABLE_NAME_1);
  private static final String CREATE_STATEMENT_2 = CREATE_STATEMENT_1
      + String.format(";\nCREATE INDEX addition ON %s (hulk)", TABLE_NAME_1);
  private static final String CREATE_STATEMENT_3 = String.format("CREATE TABLE %s (hulk STRING (12))", TABLE_NAME_1);
  private static final String CREATE_STATEMENT_4 = String
      .format("CREATE TABLE %s (bloatware INTEGER (11) NOT NULL, shipmentID INTEGER (11) NOT NULL)", TABLE_NAME_2);
  private static final String CREATE_STATEMENT_5 = String.format(
      "CREATE TABLE %s (hulk STRING (12), Thor INTEGER (67) DEFAULT (12), truthtable STRING (12))", TABLE_NAME_1);
  private static final String INSERT_STATEMENT = String
      .format("INSERT INTO %s (hulk)\n\tSELECT hulk\n\tFROM temp_table;", TABLE_NAME_1);
  private SQLiteTable table1;
  private List<String> expectedSQL = new ArrayList<>();
  private List<String> sql;

  @Before
  public void clearExpectedSQL() {
    expectedSQL.clear();
  }

  @Test
  public void testGetStatements() {
    table1 = new SQLiteTable(TABLE_NAME_1, CREATE_STATEMENT_1);
    // start assertions
    assertEquals("The name of the table should be the one passed into the constructor", TABLE_NAME_1, table1.getName());
    assertEquals("The create statement of the table should be the one passed into the constructor",
        CREATE_STATEMENT_1 + ";", table1.getCreateStatement());
  }

  @Test
  public void testAddColumn() {
    String column1 = "bloatware";
    String column2 = "shipmentID";
    String create = "CREATE TABLE bloat (bloatware INTEGER (11) NOT NULL)";
    table1 = new SQLiteTable();
    assertEquals("The size of the column list for the table should be 0 when empty", 0, table1.getColumns().size());
    // make sure first column is added when the create statement has it
    table1 = new SQLiteTable(TABLE_NAME_2, create);
    assertEquals("The size of the column list for the table should be 1 when one column has been added", 1,
        table1.getColumns().size());
    assertEquals("The column passed to addColumn should be in the tables column list", true,
        table1.getColumns().containsKey(column1));
    // make sure second column is added when the create statement has both
    table1 = new SQLiteTable(TABLE_NAME_2, CREATE_STATEMENT_4);
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
    table1 = new SQLiteTable();
    String create = CREATE_STATEMENT_4 + ";\n CREATE INDEX shipment ON " + TABLE_NAME_2 + " (shippingID, bloatware)";
    assertEquals("The size of the index list for the table should be 0 when empty", 0, table1.getIndices().size());
    // make sure the one index is exists
    table1 = new SQLiteTable(TABLE_NAME_2, create);
    assertEquals("The size of the column list for the table should be 1 when one index has been added", 1,
        table1.getIndices().size());
    assertEquals("The index passed to addIndex should be in the tables index list", true,
        table1.getIndices().containsKey(index1));
    // make sure the second index is created
    create = create + ";\n  CREATE INDEX shipped ON " + TABLE_NAME_2 + " (shippingID)";
    table1 = new SQLiteTable(TABLE_NAME_2, create);
    assertEquals(
        "The size of the index list for the table should be 2 when two indices have been added to the column list", 2,
        table1.getIndices().size());
    assertEquals("The index passed to addIndex should be in the tables index list", true,
        table1.getIndices().containsKey(index2));
  }

  @Test
  public void testEqualTableComparison() {
    String errorMsg = "There should be no sql generated for two tables with the exact create statements";

    sql = createTablesAndGenerateSQL(TABLE_NAME_1, CREATE_STATEMENT_2, TABLE_NAME_1, CREATE_STATEMENT_2);
    assertEquals(errorMsg, true, sql.isEmpty());

    sql = createTablesAndGenerateSQL(TABLE_NAME_1, CREATE_STATEMENT_3, TABLE_NAME_1, CREATE_STATEMENT_3);
    assertEquals(errorMsg, true, sql.isEmpty());
  }

  @Test
  public void testAddColumns() {
    expectedSQL.add("ALTER TABLE helper\n\tADD COLUMN Thor INTEGER (67) DEFAULT (12);");
    sql = createTablesAndGenerateSQL(TABLE_NAME_1, CREATE_STATEMENT_1, TABLE_NAME_1, CREATE_STATEMENT_3);
    assertEquals("The sql generated should add a column", expectedSQL, sql);

    expectedSQL.set(0, "ALTER TABLE helper\n\tADD COLUMN Thor INTEGER (67) DEFAULT (12);\n"
        + "ALTER TABLE helper\n\tADD COLUMN truthtable STRING (12);");
    sql = createTablesAndGenerateSQL(TABLE_NAME_1, CREATE_STATEMENT_5, TABLE_NAME_1, CREATE_STATEMENT_3);
    assertEquals("The sql generated should add two columns", expectedSQL, sql);
  }

  @Test
  public void testAddIndicesRegular() {
    expectedSQL.add("CREATE UNIQUE INDEX add2 ON helper (Thor);\nCREATE INDEX add1 ON helper (hulk, Thor);");
    sql = createTablesAndGenerateSQL(TABLE_NAME_1,
        CREATE_STATEMENT_1 + ";\nCREATE INDEX add1 ON helper (hulk, Thor);\nCREATE UNIQUE INDEX add2 ON helper (Thor);",
        TABLE_NAME_1, CREATE_STATEMENT_1);
    assertEquals("The sql generated should add two indices", expectedSQL, sql);
  }

  @Test
  public void testAddForeignKey() {
    String tableName = "Books";
    String create = String.format("CREATE TABLE %s (BookId INTEGER PRIMARY KEY, Title TEXT, AuthorId INTEGER,"
        + "FOREIGN KEY(AuthorId) REFERENCES Authors(AuthorId));", tableName);
    table1 = new SQLiteTable(tableName, create);
    assertEquals("There should be a Foreign Key in the index list", true,
        table1.getIndices().containsKey("FOREIGN KEY1"));
  }

  @Test
  public void testDropIndicesRegular() {
    expectedSQL.add("DROP INDEX add2;\nDROP INDEX add1;");
    sql = createTablesAndGenerateSQL(TABLE_NAME_1, CREATE_STATEMENT_2, TABLE_NAME_1,
        CREATE_STATEMENT_2 + ";\nCREATE INDEX add1 ON helper (hulk, Thor);\nCREATE INDEX add2 ON helper (Thor)");
    assertEquals("The sql generated should add drop indices", expectedSQL, sql);
  }

  @Test
  public void testModifyIndicesRegular() {
    expectedSQL.add("DROP INDEX add1;\nCREATE INDEX add1 ON helper (hulk, Thor);");
    String create1 = CREATE_STATEMENT_2 + ";\nCREATE INDEX add1 ON helper (hulk, Thor)";
    String create2 = CREATE_STATEMENT_2 + ";\nCREATE INDEX add1 ON helper (hulk)";
    sql = createTablesAndGenerateSQL(TABLE_NAME_1, create1, TABLE_NAME_1, create2);
    assertEquals("The sql generated should modify one index", expectedSQL, sql);

    expectedSQL.set(0, "DROP INDEX add2;\nCREATE INDEX add2 ON helper (Thor);\n"
        + "DROP INDEX add1;\nCREATE INDEX add1 ON helper (hulk, Thor);");
    create1 += ";\nCREATE INDEX add2 ON helper (Thor)";
    create2 += ";\nCREATE UNIQUE INDEX add2 ON helper (Thor)";
    sql = createTablesAndGenerateSQL(TABLE_NAME_1, create1, TABLE_NAME_1, create2);
    assertEquals("The sql generated should modify two indices", expectedSQL, sql);
  }

  @Test
  public void testRecreateTableDropColumn() {
    setUpExpectedSQLForSpecialCases();
    sql = createTablesAndGenerateSQL(TABLE_NAME_1, CREATE_STATEMENT_3, TABLE_NAME_1, CREATE_STATEMENT_1);
    assertEquals("The sql generated should recreate the table to be like the dev one when dropping a column",
        expectedSQL, sql);
    // dropping two columns
    sql = createTablesAndGenerateSQL(TABLE_NAME_1, CREATE_STATEMENT_3, TABLE_NAME_1, CREATE_STATEMENT_5);
    assertEquals("The sql generated should recreate the table to be like the dev one when dropping several columns",
        expectedSQL, sql);
  }

  @Test
  public void testRecreateTableModifyColumn() {
    String insert = "INSERT INTO helper (truthtable,hulk)\n\tSELECT truthtable,hulk\n\tFROM temp_table;";
    setUpExpectedSQLForSpecialCases();
    // modifying a column
    String create = "CREATE TABLE helper (hulk STRING (11))";
    sql = createTablesAndGenerateSQL(TABLE_NAME_1, CREATE_STATEMENT_3, TABLE_NAME_1, create);
    assertEquals("The sql generated should recreate the table to be like the dev one when modifying a column",
        expectedSQL, sql);
    // modifying two columns
    create = "CREATE TABLE helper (hulk STRING (11), truthtable STRING (12))";
    String create2 = "CREATE TABLE helper (hulk STRING (12), truthtable INTEGER (67) DEFAULT (12))";
    sql = createTablesAndGenerateSQL(TABLE_NAME_1, create2, TABLE_NAME_1, create);
    expectedSQL.set(1, create2 + ";");
    expectedSQL.set(2, insert);
    assertEquals("The sql generated should recreate the table to be like the dev one when modifying several columns",
        expectedSQL, sql);
  }

  @Test
  public void testRecreateTablePrimaryKey() {
    expectedSQL.add("ALTER TABLE bloat RENAME TO temp_table;");
    expectedSQL.add("CREATE TABLE bloat (Thor INTEGER (67) DEFAULT (12), ache BLOB (66) PRIMARY KEY);");
    expectedSQL.add("INSERT INTO bloat (Thor,ache)\n\tSELECT Thor,ache\n\tFROM temp_table;");
    expectedSQL.add("DROP TABLE temp_table;");
    // add a primary key
    String create = "CREATE TABLE bloat (Thor INTEGER (67) DEFAULT (12), ache BLOB (66) PRIMARY KEY)";
    String create2 = "CREATE TABLE bloat (Thor INTEGER (67) DEFAULT (12), ache BLOB (66))";
    sql = createTablesAndGenerateSQL(TABLE_NAME_2, create, TABLE_NAME_2, create2);
    assertEquals("The sql generated should recreate the table to be like the dev one when adding a primary key",
        expectedSQL, sql);
    // modify a primary key
    create2 = "CREATE TABLE bloat (Thor INTEGER (67) DEFAULT (12), ache BLOB (6) PRIMARY KEY)";
    sql = createTablesAndGenerateSQL(TABLE_NAME_2, create, TABLE_NAME_2, create2);
    assertEquals("The sql generated should recreate the table to be like the dev one when modifying a primary key",
        expectedSQL, sql);
    // drop a primary key
    create2 = "CREATE TABLE bloat (Thor INTEGER (67) DEFAULT (12), ache BLOB (66))";
    sql = createTablesAndGenerateSQL(TABLE_NAME_2, create2, TABLE_NAME_2, create);
    expectedSQL.clear();
    expectedSQL.add("ALTER TABLE bloat RENAME TO temp_table;");
    expectedSQL.add("CREATE TABLE bloat (Thor INTEGER (67) DEFAULT (12), ache BLOB (66));");
    expectedSQL.add("INSERT INTO bloat (Thor,ache)\n\tSELECT Thor,ache\n\tFROM temp_table;");
    expectedSQL.add("DROP TABLE temp_table;");
    assertEquals("The sql generated should recreate the table to be like the dev one when dropping a primary key",
        expectedSQL, sql);
  }

  @Test
  public void testRecreateTableSpecial() {
    String extraCreate = "CREATE INDEX addition ON helper (Thor)";
    setUpExpectedSQLForSpecialCases();
    expectedSQL.add(extraCreate + ";");
    // extra create statements are present in the create statement of the table
    sql = createTablesAndGenerateSQL(TABLE_NAME_1, CREATE_STATEMENT_3 + ";\n" + extraCreate, TABLE_NAME_1,
        CREATE_STATEMENT_1);
    assertEquals("The sql generated should recreate the table to be like the dev"
        + " one when extra create statements are present in table create statement", true, expectedSQL.equals(sql));
    // the two tables have no columns in common
    // setup table2
    String create = "CREATE TABLE helper (hawkeye STRING (12))";
    sql = createTablesAndGenerateSQL(TABLE_NAME_1, create, TABLE_NAME_1, CREATE_STATEMENT_1);
    expectedSQL.clear();
    expectedSQL.add("DROP TABLE helper;");
    expectedSQL.add(create + ";");
    assertEquals("The sql generated should recreate the table to be like the dev"
        + " one when no columns are common between the two", true, expectedSQL.equals(sql));
    // the two tables have no columns in common and there are extra create
    // statements
    // in the table create statement
    // setup table2
    create = "CREATE TABLE helper (hawkeye STRING (12));\n" + extraCreate;
    sql = createTablesAndGenerateSQL(TABLE_NAME_1, create, TABLE_NAME_1, CREATE_STATEMENT_1);
    expectedSQL.clear();
    expectedSQL.add("DROP TABLE helper;");
    expectedSQL.add(create + ";");
    assertEquals("The sql generated should recreate the table to be like the dev"
        + " one when no columns are common between the two and there are extra create"
        + "statements in the table create statement", true, expectedSQL.equals(sql));
  }

  @Test
  public void testEquals() {
    expectedSQL.add(
        "DROP INDEX drop1;\nDROP INDEX drop2;\n" + "ALTER TABLE helper\n\tADD COLUMN Thor INTEGER (67) DEFAULT (12);\n"
            + "CREATE UNIQUE INDEX add2 ON helper (Thor);\nCREATE INDEX add1 ON helper (hulk, Thor);");
    // setup tables
    String create = "CREATE TABLE helper (hulk STRING (12), Thor INTEGER (67) DEFAULT (12));\n"
        + "CREATE INDEX add1 ON helper (hulk, Thor);\n CREATE UNIQUE INDEX add2 ON helper (Thor)";
    sql = createTablesAndGenerateSQL(TABLE_NAME_1, create, TABLE_NAME_1, CREATE_STATEMENT_3
        + ";\nCREATE INDEX drop1 ON helper (hulk, Thor);\n CREATE UNIQUE INDEX drop2 ON helper (Thor)");
    assertEquals("The sql generated should add a column, drop two indexes, and add two indexes", true,
        sql.equals(expectedSQL));
  }

  private void setUpExpectedSQLForSpecialCases() {
    expectedSQL.add("ALTER TABLE helper RENAME TO temp_table;");
    expectedSQL.add(CREATE_STATEMENT_3 + ";");
    expectedSQL.add(INSERT_STATEMENT);
    expectedSQL.add("DROP TABLE temp_table;");
  }

  private List<String> createTablesAndGenerateSQL(String tableName1, String createStatement1, String tableName2,
      String createStatement2) {
    table1 = new SQLiteTable(tableName1, createStatement1);
    SQLiteTable table2 = new SQLiteTable(tableName2, createStatement2);
    return table1.equals(table2);
  }
}
