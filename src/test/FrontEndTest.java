package test;

import static org.junit.Assert.assertEquals;

import javax.swing.JComboBox;

import org.junit.Before;
import org.junit.Test;
import dbdiffchecker.TabPane;
import dbdiffchecker.PaneType;
import dbdiffchecker.DatabaseType;

/**
 * A unit test that makes sure that the frontend GUI tabs work as intended.
 *
 * @author Peter Kaufman
 * @version 7-2-20
 * @since 7-2-20
 */
@TestMethodOrder(OrderAnnotation.class)
public class FrontEndTest {
  private TabPane twoDBCompare = new TabPane(PaneType.COMPARE_WITH_DB),
      compareWithSnapshot = new TabPane(PaneType.COMPARE_WITH_SNAPSHOT), snapshot = new TabPane(PaneType.SNAPSHOT),
      logs = new TabPane(PaneType.LOGS), lastRun = new TabPane(PaneType.LAST_RUN);
  private PaneType[] paneTypeOptions = PaneType.values();
  private DatabaseType[] databaseTypeOptions = DatabaseType.values();
  private JComboBox<String> databaseOptions;

  /**
   *
   */
  @Test
  @Order(1)
  public void testGenericTabElements() {
    assertEquals("The default value of the database options of the tab should be the prompt", DatabaseType.NONE,
        twoDBCompare.getSelectedDatabase());
    int index;
    for (int i = 0; i < 10; i++) {
      index = (int) Math.random() * databaseTypeOptions.length;
      databaseOptions.setSelectedIndex(index);
      assertEquals("Selected database option should match returned databaseOptionType",
          twoDBCompare.getSelectedDatabase(), databaseTypeOptions[index]);
    }
  }

  /**
   *
   */
  @Test
  @Order(2)
  public void testCompareTwoDBTabLayout() {
    assertEquals("The tab type should match the tab passed in", paneTypeOptions[twoDBCompare.getType().getValue()],
        twoDBCompare.getType());
    assertEquals("The user inputs should be empty to start", null, twoDBCompare.getUserInputs());
    assertEquals("The run button should exist", true, twoDBCompare.getRunBtn() != null);
    assertEquals("The execute button should exist", true, twoDBCompare.getExecuteBtn() != null);
    assertEquals("The error message label should exist", true, twoDBCompare.getErrorMessage() != null);
    databaseOptions = twoDBCompare.getDatabaseOptions();
    for (int i = 1; i < databaseTypeOptions.length; i++) {
      databaseOptions.setSelectedIndex(i);
      twoDBCompare.updateComponents();
      assertEquals(
          "The amount of elements in users elements should be 2 times the amount listed in the default array for "
              + databaseOptions.getSelectedItem(),
          DatabaseType.getInputs(i).length * 2, twoDBCompare.getUserInputs().size());
    }
  }

  /**
   *
   */
  @Test
  @Order(3)
  public void testCompareSnapshotLayout() {
    assertEquals("The tab type should match the tab passed in",
        paneTypeOptions[compareWithSnapshot.getType().getValue()], compareWithSnapshot.getType());
    assertEquals("The user inputs should be empty to start", null, compareWithSnapshot.getUserInputs());
    assertEquals("The run button should exist", true, compareWithSnapshot.getRunBtn() != null);
    assertEquals("The execute button should exist", true, compareWithSnapshot.getExecuteBtn() != null);
    assertEquals("The error message label should exist", true, compareWithSnapshot.getErrorMessage() != null);
    databaseOptions = compareWithSnapshot.getDatabaseOptions();
    for (int i = 1; i < databaseTypeOptions.length; i++) {
      databaseOptions.setSelectedIndex(i);
      compareWithSnapshot.updateComponents();
      assertEquals(
          "The amount of elements in users elements should be the same as the amount listed in the default array for "
              + databaseOptions.getSelectedItem(),
          DatabaseType.getInputs(i).length, compareWithSnapshot.getUserInputs().size());
    }
  }

  @Test
  @Order(4)
  public void testSnapshotLayout() {
    assertEquals("The tab type should match the tab passed in", paneTypeOptions[snapshot.getType().getValue()],
        snapshot.getType());
    assertEquals("The user inputs should be empty to start", null, snapshot.getUserInputs());
    assertEquals("The run button should exist", true, snapshot.getRunBtn() != null);
    assertEquals("The execute button should not exist", true, snapshot.getExecuteBtn() == null);
    assertEquals("The error message label should exist", true, snapshot.getErrorMessage() != null);
    databaseOptions = snapshot.getDatabaseOptions();
    for (int i = 1; i < databaseTypeOptions.length; i++) {
      databaseOptions.setSelectedIndex(i);
      snapshot.updateComponents();
      assertEquals(
          "The amount of elements in users elements should be the same as the amount listed in the default array for "
              + databaseOptions.getSelectedItem(),
          DatabaseType.getInputs(i).length, snapshot.getUserInputs().size());
    }
  }

  @Test
  @Order(5)
  public void testLogsLayout() {
    assertEquals("The tab type should match the tab passed in", paneTypeOptions[logs.getType().getValue()],
        logs.getType());
    assertEquals("The user inputs should be empty to start", null, logs.getUserInputs());
    assertEquals("The run button should not exist", true, logs.getRunBtn() == null);
    assertEquals("The execute button should not exist", true, logs.getExecuteBtn() == null);
    assertEquals("The error message label should exist", true, logs.getErrorMessage() != null);
  }

  @Test
  @Order(6)
  public void testLastRunLayout() {
    assertEquals("The tab type should match the tab passed in", paneTypeOptions[lastRun.getType().getValue()],
        lastRun.getType());
    assertEquals("The user inputs should be empty to start", null, lastRun.getUserInputs());
    assertEquals("The run button should not exist", true, lastRun.getRunBtn() == null);
    assertEquals("The execute button should not exist", true, lastRun.getExecuteBtn() == null);
    assertEquals("The error message label should exist", true, lastRun.getErrorMessage() != null);
  }
}