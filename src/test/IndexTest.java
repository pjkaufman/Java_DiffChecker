package test;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import dbdiffchecker.sql.Index;

/**
 * A unit test that makes sure that the Index object works as intended.
 *
 * @author Peter Kaufman
 * @version 7-7-20
 * @since 5-10-19
 */
public class IndexTest {
  private static final String CREATE_FORMAT = "CREATE INDEX `%s` ON `%s` (`shippingID`,`vendor`)";
  private static final String DROP_FORMAT = "DROP INDEX `%s`";
  private Index test;
  private String table = "shippingData";
  private String name = "shipment";
  private String create = String.format(CREATE_FORMAT, name, table);
  private String drop = String.format(DROP_FORMAT, name);

  @Test
  public void testGetStatements() {
    test = new Index(name, create, drop);
    // start assertions
    assertEquals("The name of the index should be the one passed into the constructor", name, test.getName());
    assertEquals("The create statement of the index should be the one passed into the constructor", create,
        test.getCreateStatement());
    assertEquals("The drop statement of the index should be the one passed into the constructor", drop, test.getDrop());
  }

  @Test
  public void testIndexEquality() {
    test = new Index(name, create, drop);
    Index test2 = new Index(name, create, drop);
    assertEquals("Two indexes created with the same inputs to the constructor should be equal", true,
        test.getName().equals(test2.getName()) && test.equals(test2));
    // test to see if it will catch a different number of columns
    create = "CREATE INDEX `" + name + "` ON `" + table + "` (`shippingID`)";
    test2 = new Index(name, create, drop);
    assertEquals("Two indexes on different columns should not be equal", false,
        test.getName().equals(test2.getName()) && test.equals(test2));
    // test to see it will catch a index type in the create statemet
    create = "CREATE UNIQUE INDEX `" + name + "` ON `" + table + "` (`shippingID`)";
    test2 = new Index(name, create, drop);
    assertEquals("Two indexes of a different type should not be equal", false,
        test.getName().equals(test2.getName()) && test.equals(test2));
  }
}
