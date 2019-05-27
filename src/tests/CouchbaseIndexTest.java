import static org.junit.Assert.assertEquals;
import org.junit.Test;
import dbdiffchecker.nosql.CouchbaseIndex;

/**
 * A unit index that makes sure that the CouchbaseIndex object works as
 * intended.
 * @author Peter Kaufman
 * @version 5-24-19
 * @since 5-24-19
 */
public class CouchbaseIndexTest {
  private CouchbaseIndex index, index2;
  private String bucket, name, create;

  @Test
  /**
   * Tests whether the get statements inside of the CouchbaseIndex object work as
   * intended.
   * @author Peter Kaufman
   */
  public void testGetStatements() {
    bucket = "development";
    name = "dev_primary";
    create = "CREATE INDEX `" + name + "` ON `" + bucket + "`";
    index = new CouchbaseIndex(name, create);
    // start assertions
    assertEquals("The name of the index should be the one passed into the constructor", name, index.getName());
    assertEquals("The create statement of the index should be the one passed into the constructor", create,
        index.getCreateStatement());
  }

  @Test
  /**
   * Tests whether Index objects are equal when intended.
   * @author Peter Kaufman
   */
  public void testIndexEquality() {
    bucket = "development";
    name = "dev_primary";
    create = "CREATE INDEX `" + name + "` ON `" + bucket + "`";
    index = new CouchbaseIndex(name, create);
    index2 = new CouchbaseIndex(name, create);
    assertEquals("Two indexes created with the same inputs to the constructor should be equal", true,
        index.equals(index2));
    // index to see if it will catch an added where clause
    create = "CREATE INDEX `" + name + "` ON `" + bucket + "` WHERE (`abv` > 6)";
    index2 = new CouchbaseIndex(name, create);
    assertEquals("Two indexes on different columns should not be equal", false, index.equals(index2));
    // index to see it will catch a index type in the create statemet
    create = "CREATE Pirimary INDEX `" + name + "` ON `" + bucket + "`";
    index2 = new CouchbaseIndex(name, create);
    assertEquals("Two indexes of a different type should not be equal", false, index.equals(index2));
  }
}
