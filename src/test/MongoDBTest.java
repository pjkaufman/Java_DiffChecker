package test;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import dbdiffchecker.nosql.MongoDB;
import dbdiffchecker.nosql.Collection;
import java.util.List;

/**
 * A unit test that makes sure that the MongoDB object works as intended.
 *
 * @author Peter Kaufman
 * @version 7-7-20
 * @since 5-10-19
 */
public class MongoDBTest {
  private static final String COLL_NAME_1 = "Skipper";
  private static final String COLL_NAME_2 = "Private";
  private Collection coll1;
  private Collection coll2;
  private List<String> statements;
  private String createPre = "Create Collection: ";
  private String deletePre = "Delete Collection: ";

  @Test
  public void testGetCollections() {
    coll1 = new Collection(COLL_NAME_1, false, 0);
    coll2 = new Collection(COLL_NAME_2, true, 50000);
    MongoDB test = new MongoDB();
    assertEquals("The Mongo database should be empty before anything is added to it", 0, test.getCollections().size());
    test.getCollections().put(COLL_NAME_1, coll1);
    assertEquals("The Mongo database should have 1 element after the first collection is added", 1,
        test.getCollections().size());
    assertEquals("The Mongo database should have the added collection", true,
        test.getCollections().get(COLL_NAME_1).equals(coll1));
    test.getCollections().put(COLL_NAME_2, coll2);
    assertEquals("The Mongo database should have 2 elements after the second collection is added", 2,
        test.getCollections().size());
    assertEquals("The Mongo database should have the added collection", true,
        test.getCollections().get(COLL_NAME_2).equals(coll2));
  }

  @Test
  public void testCompareCreateCollections() {
    coll1 = new Collection(COLL_NAME_1, false, 0);
    coll2 = new Collection(COLL_NAME_2, true, 50000);
    MongoDB test = new MongoDB();
    MongoDB test1 = new MongoDB();
    assertEquals("The Mongo database should have no differences when neither has been modified", 0,
        test.compare(test1).size());
    test.getCollections().put(COLL_NAME_1, coll1);
    test.getCollections().put(COLL_NAME_2, coll2);
    test1.getCollections().put(COLL_NAME_2, coll2);
    statements = test.compare(test1);
    assertEquals("The Mongo database should suggest 1 change when the dev database has 1 more collection", 1,
        statements.size());
    assertEquals("The statements from the compare should have include the addition of the collection that only dev has",
        true, statements.get(0).equals(createPre + COLL_NAME_1));
    test1.getCollections().remove(COLL_NAME_2);
    statements = test.compare(test1);
    assertEquals("The Mongo database compare should suggest 2 changes when the dev database has 2 more collections", 2,
        statements.size());
    assertEquals("The statements should have the addition of both collections", true,
        statements.contains(createPre + COLL_NAME_1)
            && statements.contains(createPre + COLL_NAME_2 + ", capped=true, size=50000"));
  }

  @Test
  public void testCompareDropCollections() {
    coll1 = new Collection(COLL_NAME_1, false, 0);
    coll2 = new Collection(COLL_NAME_2, true, 50000);
    MongoDB test = new MongoDB();
    MongoDB test1 = new MongoDB();
    assertEquals("The Mongo database should have no differences when neither has been modified", 0,
        test.compare(test1).size());
    test.getCollections().put(COLL_NAME_1, coll1);
    test.getCollections().put(COLL_NAME_2, coll2);
    test1.getCollections().put(COLL_NAME_2, coll2);
    statements = test1.compare(test);
    assertEquals("The Mongo database should suggest 1 change when the live database has 1 more collection", 1,
        statements.size());
    assertEquals(
        "The statements from the compare should have include the deletion of the collection that only live has", true,
        statements.get(0).equals(deletePre + COLL_NAME_1));
    test1.getCollections().remove(COLL_NAME_2);
    statements = test1.compare(test);
    assertEquals("The Mongo database compare should suggest 2 changes when the live database has 2 more collections", 2,
        statements.size());
    assertEquals("The statements should have the deletion of both collections", true,
        statements.contains(deletePre + COLL_NAME_1) && statements.contains(deletePre + COLL_NAME_2));
  }

  @Test
  public void testCompareUpdateCollections() {
    coll1 = new Collection(COLL_NAME_1, false, 0);
    coll2 = new Collection(COLL_NAME_2, true, 50000);
    Collection coll12 = new Collection(COLL_NAME_1, true, 67890);
    Collection coll22 = new Collection(COLL_NAME_2, false, 0);
    MongoDB test = new MongoDB();
    MongoDB test1 = new MongoDB();
    assertEquals("The Mongo database should have no differences when neither has been modified", 0,
        test.compare(test1).size());
    test.getCollections().put(COLL_NAME_1, coll1);
    test.getCollections().put(COLL_NAME_2, coll2);
    test1.getCollections().put(COLL_NAME_1, coll12);
    test1.getCollections().put(COLL_NAME_2, coll2);
    statements = test.compare(test1);
    assertEquals("The Mongo database should suggest 2 changes when a collection needs to be updated", 2,
        statements.size());
    assertEquals(
        "The statements from the compare should have include the deletion of the collection that only live has", true,
        statements.contains(deletePre + COLL_NAME_1) && statements.contains(createPre + COLL_NAME_1));
    test.getCollections().put(COLL_NAME_2, coll22);
    statements = test.compare(test1);
    assertEquals("The Mongo database compare should suggest 4 changes when 2 collections need updating", 4,
        statements.size());
    assertEquals("The statements should have the deletion and creation of both collections", true,
        statements.contains(deletePre + COLL_NAME_1) && statements.contains(deletePre + COLL_NAME_2)
            && statements.contains(createPre + COLL_NAME_1) && statements.contains(createPre + COLL_NAME_2));
  }

  @Test
  public void testCompareMixedCollections() {
    String names1 = "Commoner";
    String names2 = "Creeper";
    String names3 = "Creep";
    String names4 = "Pillager";
    String names5 = "Villager";
    coll1 = new Collection(COLL_NAME_1, false, 0);
    coll2 = new Collection(COLL_NAME_2, true, 50000);
    Collection coll12 = new Collection(COLL_NAME_1, true, 67890);
    Collection coll22 = new Collection(COLL_NAME_2, false, 0);
    Collection coll3 = new Collection(names1, false, 0);
    Collection coll4 = new Collection(names2, false, 0);
    Collection coll5 = new Collection(names1, true, 587390);
    Collection coll6 = new Collection(names4, false, 0);
    Collection coll7 = new Collection(names5, true, 234560);
    MongoDB test = new MongoDB();
    MongoDB test1 = new MongoDB();
    // setup
    test.getCollections().put(COLL_NAME_1, coll1); // collection to modify
    test.getCollections().put(COLL_NAME_2, coll2); // collection to modify
    test.getCollections().put(names1, coll3); // common collection
    test.getCollections().put(names4, coll6); // collection to add
    test.getCollections().put(names5, coll7); // collection to add
    test1.getCollections().put(COLL_NAME_1, coll12); // collection to modify
    test1.getCollections().put(COLL_NAME_2, coll22); // collection to modify
    test1.getCollections().put(names1, coll3); // common collection
    test1.getCollections().put(names2, coll4); // collection to drop
    test1.getCollections().put(names3, coll5); // collection to drop
    statements = test.compare(test1);
    assertEquals("The Mongo database should suggest 8 changes when 2 collections need to be updated (4),"
        + " 2 collections need to be created (2), and 2 collections need to be dropped (2).", 8, statements.size());
    // 2 create statements
    assertEquals("The statements from the compare should include the creations of the collections that only dev has",
        true, statements.contains(createPre + names4)
            && statements.contains(createPre + names5 + ", capped=true, size=234560"));
    // 2 delete statements
    assertEquals("The statements from the compare should include the deletions of the collections that only live has",
        true, statements.contains(deletePre + names2) && statements.contains(deletePre + names3));
    // 4 update statements
    assertEquals(
        "The statements from the compare should include the deletions and recreations of the collections that need to be updated",
        true,
        statements.contains(deletePre + COLL_NAME_1) && statements.contains(deletePre + COLL_NAME_2)
            && statements.contains(createPre + COLL_NAME_1)
            && statements.contains(createPre + COLL_NAME_2 + ", capped=true, size=50000"));
  }
}
