package test;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import dbdiffchecker.sql.Column;

/**
 * A unit test that makes sure that the Column object works as intended.
 *
 * @author Peter Kaufman
 * @version 7-2-20
 * @since 5-10-19
 */
public class ColumnTest {
  private Column test;
  private String name, details;

  /**
   * Tests whether the get statements inside of the Column object work as
   * intended.
   */
  @Test
  public void testGetStatements() {
    name = "shipmentID";
    details = "int(11) NOT NULL";
    test = new Column(name, details);
    assertEquals("The name of the column should be the one passed into the constructor", name, test.getName());
    assertEquals("The details of the column should be the one passed into the constructor", details, test.getDetails());
  }
}
