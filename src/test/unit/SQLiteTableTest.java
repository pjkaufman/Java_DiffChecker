package test.unit;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import dbdiffchecker.sql.SQLiteTable;

@DisplayName("SQLite Table Tests")
public class SQLiteTableTest {

  @DisplayName("Getter Tests")
  @ParameterizedTest
  @MethodSource({"test.TestParamProvider#sqliteTableGetStatements"})
  public void testGetStatements(String testName, String tableName, String createStatment,
    List<String> expectedColumns, List<String> expectedIndexes) {

    SQLiteTable table1 = tableName != null ? new SQLiteTable(tableName, createStatment) : new SQLiteTable();

    if (tableName != null) {
      assertAll(
        () -> assertEquals(tableName, table1.getName()),
        () -> assertEquals(createStatment + ";", table1.getCreateStatement())
      );
    } else {
      assertAll(
        () -> assertTrue(table1.getColumns().isEmpty()),
        () -> assertTrue(table1.getIndices().isEmpty())
      );
    }

    for (String expectedColumn : expectedColumns) {
      assertTrue(table1.getColumns().containsKey(expectedColumn), "The " + expectedColumn + " should exist on the table");
    }

    for (String expectedIndex : expectedIndexes) {
      assertTrue(table1.getIndices().containsKey(expectedIndex), "The " + expectedIndex + " should exist on the table");
    }
  }

  @DisplayName("SQLite Table Compare Tests")
  @ParameterizedTest
  @MethodSource({"test.TestParamProvider#sqliteCompare"})
  public void testSQLiteTableCompare(String testName, String tableName1, String tableCreate1,
    String tableName2, String tableCreate2, List<String> expectedStatements) {

    SQLiteTable table1 = new SQLiteTable(tableName1, tableCreate1);
    SQLiteTable table2 = new SQLiteTable(tableName2, tableCreate2);
    List<String> actualStatements = table1.generateStatements(table2);

    assertIterableEquals(expectedStatements, actualStatements);
  }
}
