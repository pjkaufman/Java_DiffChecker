package test.unit;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import dbdiffchecker.sql.MySQLTable;
import dbdiffchecker.sql.SQLDatabase;
import dbdiffchecker.sql.Table;
import dbdiffchecker.sql.View;

@DisplayName("SQL Database Tests")
public class SQLDatabaseTest {

  @DisplayName("Getter Tests")
  @ParameterizedTest
  @MethodSource({"test.TestParamProvider#sqlDatabaseGetStatements"})
  public void testGetTables(String testName, List<String> tableNames, List<String> tableCreates,
    List<String> viewNames, List<String> viewCreates, List<String> firstSteps) {

    SQLDatabase db = new SQLDatabase();
    Map<String, Table> tableList = db.getTables();
    List<View> viewList = db.getViews();
    List<View> expectedViews = new ArrayList<>();

    for (int i = 0; i < tableNames.size(); i++) {
      tableList.put(tableNames.get(i), new MySQLTable(tableNames.get(i), tableCreates.get(i)));
    }

    View newView;
    for (int i = 0; i < viewNames.size(); i++) {
      newView = new View(viewNames.get(i), viewCreates.get(i));
      expectedViews.add(newView);
      viewList.add(newView);
    }

    for (String firstStep : firstSteps) {
      db.getFirstSteps().add(firstStep);
    }

    for (int i = 0; i < tableNames.size(); i++) {
      assertTrue(db.getTables().containsKey(tableNames.get(i)));
    }

    assertIterableEquals(expectedViews, db.getViews());

    assertIterableEquals(firstSteps, db.getFirstSteps());

    if (tableNames.isEmpty()) {
      assertTrue(db.getTables().isEmpty());
    }
  }

  @DisplayName("Update Views Tests")
  @ParameterizedTest
  @MethodSource({"test.TestParamProvider#sqlDatabaseUpdateViews"})
  public void testUpdateViews(List<String> viewNames, List<String> viewCreates,
    List<String> viewNames2, List<String> viewCreates2, List<String> expectedStatements) {

    SQLDatabase db = new SQLDatabase();
    for (int i = 0; i < viewNames.size(); i++) {
      db.getViews().add(new View(viewNames.get(i), viewCreates.get(i)));
    }

    List<View> liveViews = new ArrayList<>();
    for (int i = 0; i < viewNames2.size(); i++) {
      liveViews.add(new View(viewNames2.get(i), viewCreates2.get(i)));
    }

    assertIterableEquals(expectedStatements, db.updateViews(liveViews));
  }

  @DisplayName("Table Difference Tests")
  @ParameterizedTest
  @MethodSource({"test.TestParamProvider#sqlDatabaseTableDiffs"})
  public void testTableDiffs(String testName, List<String> tableNames, List<String> tableCreates,
    List<String> tableNames2, List<String> tableCreates2, List<String> expectedTableDiffs) {

    SQLDatabase db = new SQLDatabase();
    for (int i = 0; i < tableNames.size(); i++) {
      db.getTables().put(tableNames.get(i), (new MySQLTable(tableNames.get(i), tableCreates.get(i))));
    }

    SQLDatabase liveDb = new SQLDatabase();
    Map<String, Table> liveTables = liveDb.getTables();
    for (int i = 0; i < tableNames2.size(); i++) {
      liveTables.put(tableNames2.get(i), new MySQLTable(tableNames2.get(i), tableCreates2.get(i)));
    }

    Map<String, String> tablesToUpdate = db.tablesDiffs(liveTables, liveDb);
    assertEquals(expectedTableDiffs.size(), tablesToUpdate.size());
    for (String expectedTableDiff : expectedTableDiffs) {
      assertTrue(tablesToUpdate.containsKey(expectedTableDiff));
    }
  }

  @DisplayName("Table Compare Tests")
  @ParameterizedTest
  @MethodSource({"test.TestParamProvider#sqlDatabaseCompareTables"})
  public void testCompareTables(String testName, List<String> tableNames, List<String> tableCreates,
    List<String> tableNames2, List<String> tableCreates2, List<String> expectedStatements) {

    SQLDatabase db = new SQLDatabase();
    for (int i = 0; i < tableNames.size(); i++) {
      db.getTables().put(tableNames.get(i), (new MySQLTable(tableNames.get(i), tableCreates.get(i))));
    }

    SQLDatabase liveDb = new SQLDatabase();
    Map<String, Table> liveTables = liveDb.getTables();
    for (int i = 0; i < tableNames2.size(); i++) {
      liveTables.put(tableNames2.get(i), new MySQLTable(tableNames2.get(i), tableCreates2.get(i)));
    }

    assertIterableEquals(expectedStatements, db.compareTables(liveDb));
  }

  @DisplayName("Table Update Tests")
  @ParameterizedTest
  @MethodSource({"test.TestParamProvider#sqlDatabaseUpdateTables"})
  public void testCompareTables(String testName, List<String> tableNames, List<String> tableCreates,
    List<String> tableCreates2, List<String> expectedStatements) {

    SQLDatabase db = new SQLDatabase();
    Map<String, Table> liveTables = new HashMap<>();
    Map<String, String> tablesToUpdate = new HashMap<>();
    for (int i = 0; i < tableNames.size(); i++) {
      db.getTables().put(tableNames.get(i), (new MySQLTable(tableNames.get(i), tableCreates.get(i))));
      liveTables.put(tableNames.get(i), new MySQLTable(tableNames.get(i), tableCreates2.get(i)));
      tablesToUpdate.put(tableNames.get(i), tableNames.get(i));
    }

    assertIterableEquals(expectedStatements, db.updateTables(liveTables, tablesToUpdate));
  }

  @DisplayName("First Steps Tests")
  @ParameterizedTest
  @MethodSource({"test.TestParamProvider#sqlDatabaseFirstSteps"})
  public void testFirstSteps(String testName, List<String> tableNames, List<String> tableCreates,
    List<String> tableNames2, List<String> tableCreates2, List<String> intialFirstSteps,
    List<String> expectedFirstSteps) {

    SQLDatabase db = new SQLDatabase();
    for (int i = 0; i < tableNames.size(); i++) {
      db.getTables().put(tableNames.get(i), (new MySQLTable(tableNames.get(i), tableCreates.get(i))));
    }

    SQLDatabase liveDb = new SQLDatabase();
    Map<String, Table> liveTables = liveDb.getTables();
    for (int i = 0; i < tableNames2.size(); i++) {
      liveTables.put(tableNames2.get(i), new MySQLTable(tableNames2.get(i), tableCreates2.get(i)));
    }

    db.getFirstSteps().addAll(intialFirstSteps);

    db.compareTables(liveDb);

    assertIterableEquals(expectedFirstSteps, db.getFirstSteps());
  }
}
