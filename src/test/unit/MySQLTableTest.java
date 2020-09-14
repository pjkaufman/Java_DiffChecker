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

import dbdiffchecker.sql.MySQLTable;

@DisplayName("MySQL Table Tests")
public class MySQLTableTest {

  @DisplayName("Getter Tests")
  @ParameterizedTest
  @MethodSource({"test.TestParamProvider#mysqlTableGetStatements"})
  public void testGetStatements(String testName, String tableName, String tableCreate,
    String expectedCharSet, String collation, int expectedAutoInc, List<String> expectedColumns,
    List<String> expectedIndexes) {

    MySQLTable table1 = tableName != null ? new MySQLTable(tableName, tableCreate): new MySQLTable();
    if (tableName != null) {
      table1.setCollation(collation);
      assertAll(
          () -> assertEquals(tableName, table1.getName()),
          () -> assertEquals(tableCreate + ";", table1.getCreateStatement()),
          () -> assertEquals(expectedCharSet, table1.getCharSet()),
          () -> assertEquals(expectedAutoInc, table1.getAutoIncrement()),
          () -> assertEquals(collation, table1.getCollation())
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

  @DisplayName("MySQL Table Compare Tests")
  @ParameterizedTest
  @MethodSource({"test.TestParamProvider#mysqlCompare"})
  public void testSQLiteTableCompare(String testName, String tableName1, String tableCreate1,
    String tableName2, String tableCreate2, List<String> expectedStatements) {

    MySQLTable table1 = new MySQLTable(tableName1, tableCreate1);
    MySQLTable table2 = new MySQLTable(tableName2, tableCreate2);
    List<String> actualStatements = table1.generateStatements(table2);

    assertIterableEquals(expectedStatements, actualStatements);
  }
}
