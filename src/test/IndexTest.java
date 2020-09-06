package test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import dbdiffchecker.sql.Index;

public class IndexTest {
  private static final String CREATE_FORMAT = "CREATE INDEX `%s` ON `%s` (`shippingID`,`vendor`)";
  private static final String DROP_FORMAT = "DROP INDEX `%s`";
  private String table = "shippingData";
  private String name = "shipment";
  private String create = String.format(CREATE_FORMAT, name, table);
  private String drop = String.format(DROP_FORMAT, name);
  private Index test = new Index(name, create, drop);
  private Index test2 = new Index(name, create, drop);

  @Test
  public void testGetStatements() {
    assertEquals("The name of the index should be the one passed into the constructor", name, test.getName());
    assertEquals("The create statement of the index should be the one passed into the constructor", create,
        test.getCreateStatement());
    assertEquals("The drop statement of the index should be the one passed into the constructor", drop, test.getDrop());
  }

  @Test
  public void testIndicesAreEqualForTheSameInputs() {
    test2 = new Index(name, create, drop);

    assertEquals("Two indexes created with the same inputs to the constructor should be equal", true,
        test.getName().equals(test2.getName()) && test.equals(test2));
  }

  @Test
  public void testIndicesAreNotEqualWhenOnDifferentColumns() {
    String create2 = "CREATE INDEX `" + name + "` ON `" + table + "` (`shippingID`)";
    test2 = new Index(name, create2, drop);

    assertEquals("Two indexes on different columns should not be equal", false,
        test.getName().equals(test2.getName()) && test.equals(test2));
  }

  @Test
  public void testIndicesAreNotEqualWhenTypeIsNotTheSame() {
    String create2 = "CREATE UNIQUE INDEX `" + name + "` ON `" + table + "` (`shippingID`)";
    test2 = new Index(name, create2, drop);

    assertEquals("Two indexes of a different type should not be equal", false,
        test.getName().equals(test2.getName()) && test.equals(test2));
  }
}
