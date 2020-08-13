package test;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;

import dbdiffchecker.sql.SQLiteTable;

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
      .format("INSERT INTO %s (hulk)\n  SELECT hulk\n  FROM temp_table;", TABLE_NAME_1);
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

    assertEquals("The name of the table should be the one passed into the constructor", TABLE_NAME_1, table1.getName());
    assertEquals("The create statement of the table should be the one passed into the constructor",
        CREATE_STATEMENT_1 + ";", table1.getCreateStatement());
  }

  @Test
  public void testColumnListIsEmptyOnInitialization() {
    table1 = new SQLiteTable();

    assertEquals("The column list for a table should be empty initially", true, table1.getColumns().isEmpty());
  }

  @Test
  public void testTableConstructorColumnAddition() {
    String column1 = "bloatware";
    String column2 = "shipmentID";

    table1 = new SQLiteTable(TABLE_NAME_2, CREATE_STATEMENT_4);

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
    table1 = new SQLiteTable();

    assertEquals("The index list should be empty on initialization", true, table1.getIndices().isEmpty());
  }

  @Test
  public void testTableConstructorIndexAddition() {
    String index1 = "shipment";
    String index2 = "shipped";
    String create = CREATE_STATEMENT_4 + ";\n CREATE INDEX shipment ON " + TABLE_NAME_2 + " (shippingID, bloatware)"
        + ";\n  CREATE INDEX shipped ON " + TABLE_NAME_2 + " (shippingID)";

    table1 = new SQLiteTable(TABLE_NAME_2, create);

    assertEquals(
        "The size of the index list for the table should be 2 when two indices have been added to the column list", 2,
        table1.getIndices().size());
    assertEquals("The index passed to addIndex should be in the tables index list", true,
        table1.getIndices().containsKey(index1));
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
  public void testSingleColumnAdditionComparison() {
    expectedSQL.add("ALTER TABLE helper ADD COLUMN Thor INTEGER (67) DEFAULT (12);");

    sql = createTablesAndGenerateSQL(TABLE_NAME_1, CREATE_STATEMENT_1, TABLE_NAME_1, CREATE_STATEMENT_3);

    assertEquals("The sql generated should add a column", expectedSQL, sql);
  }

  @Test
  public void testMultipleColumnAdditionComparison() {
    expectedSQL.add("ALTER TABLE helper ADD COLUMN Thor INTEGER (67) DEFAULT (12);\n"
        + "ALTER TABLE helper ADD COLUMN truthtable STRING (12);");

    sql = createTablesAndGenerateSQL(TABLE_NAME_1, CREATE_STATEMENT_5, TABLE_NAME_1, CREATE_STATEMENT_3);

    assertEquals("The sql generated should add two columns", expectedSQL, sql);
  }

  @Test
  public void testMultipleRegularIndexAdditionComparison() {
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
  public void testMultipleRegularIndexDropComparison() {
    expectedSQL.add("DROP INDEX add2;\nDROP INDEX add1;");

    sql = createTablesAndGenerateSQL(TABLE_NAME_1, CREATE_STATEMENT_2, TABLE_NAME_1,
        CREATE_STATEMENT_2 + ";\nCREATE INDEX add1 ON helper (hulk, Thor);\nCREATE INDEX add2 ON helper (Thor)");

    assertEquals("The sql generated should add drop indices", expectedSQL, sql);
  }

  @Test
  public void testSingleRegularIndexModificationComparison() {
    expectedSQL.add("DROP INDEX add1;\nCREATE INDEX add1 ON helper (hulk, Thor);");

    String create1 = CREATE_STATEMENT_2 + ";\nCREATE INDEX add1 ON helper (hulk, Thor)";
    String create2 = CREATE_STATEMENT_2 + ";\nCREATE INDEX add1 ON helper (hulk)";

    sql = createTablesAndGenerateSQL(TABLE_NAME_1, create1, TABLE_NAME_1, create2);

    assertEquals("The sql generated should modify one index", expectedSQL, sql);
  }

  @Test
  public void testMultipleRegularIndexModificationComparison() {
    expectedSQL.add("DROP INDEX add2;\nCREATE INDEX add2 ON helper (Thor);\n"
        + "DROP INDEX add1;\nCREATE INDEX add1 ON helper (hulk, Thor);");

    String create1 = CREATE_STATEMENT_2
        + ";\nCREATE INDEX add1 ON helper (hulk, Thor);\nCREATE INDEX add2 ON helper (Thor)";
    String create2 = CREATE_STATEMENT_2
        + ";\nCREATE INDEX add1 ON helper (hulk);\nCREATE UNIQUE INDEX add2 ON helper (Thor)";

    sql = createTablesAndGenerateSQL(TABLE_NAME_1, create1, TABLE_NAME_1, create2);

    assertEquals("The sql generated should modify two indices", expectedSQL, sql);
  }

  @Test
  public void testRecreateTableDueToDropColumn() {
    setUpExpectedSQLForSpecialCases();

    sql = createTablesAndGenerateSQL(TABLE_NAME_1, CREATE_STATEMENT_3, TABLE_NAME_1, CREATE_STATEMENT_1);

    assertEquals("The sql generated should recreate the table to be like the dev one when dropping a column",
        expectedSQL, sql);
  }

  @Test
  public void testRecreateTableDueToDropColumns() {
    setUpExpectedSQLForSpecialCases();

    sql = createTablesAndGenerateSQL(TABLE_NAME_1, CREATE_STATEMENT_3, TABLE_NAME_1, CREATE_STATEMENT_5);

    assertEquals("The sql generated should recreate the table to be like the dev one when dropping several columns",
        expectedSQL, sql);
  }

  @Test
  public void testRecreateTableDueToModifyColumn() {
    setUpExpectedSQLForSpecialCases();

    String create = "CREATE TABLE helper (hulk STRING (11))";
    sql = createTablesAndGenerateSQL(TABLE_NAME_1, CREATE_STATEMENT_3, TABLE_NAME_1, create);

    assertEquals("The sql generated should recreate the table to be like the dev one when modifying a column",
        expectedSQL, sql);
  }

  @Test
  public void testRecreateTableDueToModifyColumns() {
    String insert = "INSERT INTO helper (truthtable,hulk)\n  SELECT truthtable,hulk\n  FROM temp_table;";
    String create = "CREATE TABLE helper (hulk STRING (11), truthtable STRING (12))";
    String create2 = "CREATE TABLE helper (hulk STRING (12), truthtable INTEGER (67) DEFAULT (12))";
    setUpExpectedSQLForSpecialCases();
    expectedSQL.set(1, create2 + ";");
    expectedSQL.set(2, insert);

    sql = createTablesAndGenerateSQL(TABLE_NAME_1, create2, TABLE_NAME_1, create);

    assertEquals("The sql generated should recreate the table to be like the dev one when modifying several columns",
        expectedSQL, sql);
  }

  @Test
  public void testRecreateTableDueToAddPrimaryKey() {
    expectedSQL.add("ALTER TABLE bloat RENAME TO temp_table;");
    expectedSQL.add("CREATE TABLE bloat (Thor INTEGER (67) DEFAULT (12), ache BLOB (66) PRIMARY KEY);");
    expectedSQL.add("INSERT INTO bloat (Thor,ache)\n  SELECT Thor,ache\n  FROM temp_table;");
    expectedSQL.add("DROP TABLE temp_table;");

    String create = "CREATE TABLE bloat (Thor INTEGER (67) DEFAULT (12), ache BLOB (66) PRIMARY KEY)";
    String create2 = "CREATE TABLE bloat (Thor INTEGER (67) DEFAULT (12), ache BLOB (66))";
    sql = createTablesAndGenerateSQL(TABLE_NAME_2, create, TABLE_NAME_2, create2);

    assertEquals("The sql generated should recreate the table to be like the dev one when adding a primary key",
        expectedSQL, sql);
  }

  @Test
  public void testRecreateTableDueToModifyPrimaryKey() {
    expectedSQL.add("ALTER TABLE bloat RENAME TO temp_table;");
    expectedSQL.add("CREATE TABLE bloat (Thor INTEGER (67) DEFAULT (12), ache BLOB (66) PRIMARY KEY);");
    expectedSQL.add("INSERT INTO bloat (Thor,ache)\n  SELECT Thor,ache\n  FROM temp_table;");
    expectedSQL.add("DROP TABLE temp_table;");

    String create = "CREATE TABLE bloat (Thor INTEGER (67) DEFAULT (12), ache BLOB (66) PRIMARY KEY)";
    String create2 = "CREATE TABLE bloat (Thor INTEGER (67) DEFAULT (12), ache BLOB (6) PRIMARY KEY)";
    sql = createTablesAndGenerateSQL(TABLE_NAME_2, create, TABLE_NAME_2, create2);

    assertEquals("The sql generated should recreate the table to be like the dev one when modifying a primary key",
        expectedSQL, sql);
  }

  @Test
  public void testRecreateTableDueToDropPrimaryKey() {
    expectedSQL.add("ALTER TABLE bloat RENAME TO temp_table;");
    expectedSQL.add("CREATE TABLE bloat (Thor INTEGER (67) DEFAULT (12), ache BLOB (66));");
    expectedSQL.add("INSERT INTO bloat (Thor,ache)\n  SELECT Thor,ache\n  FROM temp_table;");
    expectedSQL.add("DROP TABLE temp_table;");

    String create = "CREATE TABLE bloat (Thor INTEGER (67) DEFAULT (12), ache BLOB (66) PRIMARY KEY)";
    String create2 = "CREATE TABLE bloat (Thor INTEGER (67) DEFAULT (12), ache BLOB (66))";
    sql = createTablesAndGenerateSQL(TABLE_NAME_2, create2, TABLE_NAME_2, create);

    assertEquals("The sql generated should recreate the table to be like the dev one when dropping a primary key",
        expectedSQL, sql);
  }

  @Test
  public void testRecreateTableWithExtraCreateStatements() {
    setUpExpectedSQLForSpecialCases();
    String extraCreate = "CREATE INDEX addition ON helper (Thor)";
    expectedSQL.add(extraCreate + ";");

    sql = createTablesAndGenerateSQL(TABLE_NAME_1, CREATE_STATEMENT_3 + ";\n" + extraCreate, TABLE_NAME_1,
        CREATE_STATEMENT_1);

    assertEquals("The sql generated should recreate the table to be like the dev"
        + " one when extra create statements are present in table create statement", expectedSQL, sql);
  }

  @Test
  public void testRecreateTableWithNoCommonColumns() {
    String create = "CREATE TABLE helper (hawkeye STRING (12))";
    expectedSQL.add("DROP TABLE helper;");
    expectedSQL.add(create + ";");

    sql = createTablesAndGenerateSQL(TABLE_NAME_1, create, TABLE_NAME_1, CREATE_STATEMENT_1);

    assertEquals("The sql generated should recreate the table to be like the dev"
        + " one when no columns are common between the two", expectedSQL, sql);
  }

  @Test
  public void testRecreateTableWithNoCommonColumnsAndExtraCreateStatements() {
    String extraCreate = "CREATE INDEX addition ON helper (Thor)";
    String create = "CREATE TABLE helper (hawkeye STRING (12));\n" + extraCreate;
    expectedSQL.add("DROP TABLE helper;");
    expectedSQL.add(create + ";");

    sql = createTablesAndGenerateSQL(TABLE_NAME_1, create, TABLE_NAME_1, CREATE_STATEMENT_1);

    assertEquals("The sql generated should recreate the table to be like the dev"
        + " one when no columns are common between the two and there are extra create"
        + "statements in the table create statement", expectedSQL, sql);
  }

  @Test
  public void testGenerateStatments() {
    expectedSQL.add(
        "DROP INDEX drop1;\nDROP INDEX drop2;\n" + "ALTER TABLE helper ADD COLUMN Thor INTEGER (67) DEFAULT (12);\n"
            + "CREATE UNIQUE INDEX add2 ON helper (Thor);\nCREATE INDEX add1 ON helper (hulk, Thor);");

    String create = "CREATE TABLE helper (hulk STRING (12), Thor INTEGER (67) DEFAULT (12));\n"
        + "CREATE INDEX add1 ON helper (hulk, Thor);\n CREATE UNIQUE INDEX add2 ON helper (Thor)";
    sql = createTablesAndGenerateSQL(TABLE_NAME_1, create, TABLE_NAME_1, CREATE_STATEMENT_3
        + ";\nCREATE INDEX drop1 ON helper (hulk, Thor);\n CREATE UNIQUE INDEX drop2 ON helper (Thor)");

    assertEquals("The sql generated should add a column, drop two indexes, and add two indexes", expectedSQL, sql);
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
    return table1.generateStatements(table2);
  }
}
