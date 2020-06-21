package test;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import dbdiffchecker.nosql.Collection;

/**
 * A unit test that makes sure that the Collection object works as intended.
 *
 * @author Peter Kaufman
 * @version 1-2-20
 * @since 1-1-20
 */
public class CollectionsTest {
  private Collection test, test2;
  private String name;
  private boolean isCapped;
  private int size;

  @Test
  /**
   * Tests whether the get statements inside of the Collection object work as
   * intended.
   *
   * @author Peter Kaufman
   */
  public void testGetStatements() {
    name = "work1";
    size = 1024;
    isCapped = true;
    test = new Collection(name, isCapped, size);
    // // start assertions
    assertEquals("The name of the collection should be the one passed into the constructor", name, test.getName());
    assertEquals("The size of the collection should be the one passed into the constructor", size, test.getSize());
    assertEquals("The capped flag of the collection should be the one passed into the constructor", isCapped,
        test.isCapped());
  }

  @Test
  /**
   * Tests whether Collection objects are equal when intended.
   *
   * @author Peter Kaufman
   */
  public void testCollectionEquality() {
    // initial setup for collections
    name = "work1";
    size = 1024;
    isCapped = true;
    test = new Collection(name, isCapped, size);
    name = "work2";
    test2 = new Collection(name, isCapped, size);
    // testing of the collections
    // make sure that when the name is different the collections are labeled as
    // different
    assertEquals("The two collections should not be equal if the names are not the same", false, test.equals(test2));
    // make sure that when one of the collections is capped and the other is not
    // that they are labeled as different
    isCapped = false;
    test = new Collection(name, isCapped, size);
    assertEquals("The two collections should not be equal if only one of them is capped", false, test.equals(test2));
    // make sure that when the size of the two collections are different and the
    // collections are capped that they are labeled as different
    size = 2048;
    test = new Collection(name, isCapped, size);
    assertEquals("The two collections should not be equal if they have two differenet sizes", false,
        test.equals(test2));
    // make sure that two collections that have the same information are labeled as
    // the same
    test2 = new Collection(name, isCapped, size);
    assertEquals("The two collections should be equal if both of them have the same information", true,
        test.equals(test2));
  }
}
