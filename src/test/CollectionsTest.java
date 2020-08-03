package test;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import dbdiffchecker.nosql.Collection;

public class CollectionsTest {
  private Collection test;
  private Collection test2;
  private String name;
  private boolean isCapped;
  private int size;

  @Test
  public void testGetStatements() {
    name = "work1";
    size = 1024;
    isCapped = true;

    test = new Collection(name, isCapped, size);

    assertEquals("The name of the collection should be the one passed into the constructor", name, test.getName());
    assertEquals("The size of the collection should be the one passed into the constructor", size, test.getSize());
    assertEquals("The capped flag of the collection should be the one passed into the constructor", isCapped,
        test.isCapped());
  }

  @Test
  public void testCollectionsNotEqualWhenNamesAreDifferent() {
    name = "work1";
    size = 1024;
    isCapped = true;
    test = new Collection(name, isCapped, size);
    name = "work2";
    test2 = new Collection(name, isCapped, size);

    assertEquals("The two collections should not be equal if the names are not the same", false, test.equals(test2));
  }

  @Test
  public void testCollectionsNotEqualIfCappedValueDiffers() {
    name = "work1";
    size = 1024;
    isCapped = true;
    test = new Collection(name, isCapped, size);
    isCapped = false;
    test2 = new Collection(name, isCapped, size);

    assertEquals("The two collections should not be equal if only one of them is capped", false, test.equals(test2));
  }

  @Test
  public void testCollectionsNotEqualIfSizesDiffer() {
    name = "work1";
    size = 1024;
    isCapped = true;
    test = new Collection(name, isCapped, size);
    size = 2048;
    test2 = new Collection(name, isCapped, size);

    assertEquals("The two collections should not be equal if they have two differenet sizes", false,
        test.equals(test2));
  }

  @Test
  public void testCollectionsAreEqualWhenInfoIsTheSame() {
    name = "work1";
    size = 1024;
    isCapped = true;
    test = new Collection(name, isCapped, size);
    test2 = new Collection(name, isCapped, size);

    assertEquals("The two collections should be equal if both of them have the same information", true,
        test.equals(test2));
  }
}
