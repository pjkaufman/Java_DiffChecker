package test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import dbdiffchecker.nosql.Collection;
import dbdiffchecker.nosql.MongoDB;

public class MongoDBTest {
  private static final String COLL_NAME_1 = "Skipper";
  private static final String COLL_NAME_2 = "Private";
  private Collection coll1 = new Collection(COLL_NAME_1, false, 0);
  private Collection coll2 = new Collection(COLL_NAME_2, true, 50000);
  private List<String> statements;
  private List<String> expectedStatements = new ArrayList<>();
  private String createPre = "Create Collection: ";
  private String deletePre = "Delete Collection: ";

  @Before
  public void clearExpectedStatements() {
    expectedStatements.clear();
  }

  @Test
  public void testGetCollections() {
    MongoDB test = new MongoDB();

    assertEquals("The Mongo database should be empty before anything is added to it", true,
        test.getCollections().isEmpty());

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
  public void testEmptyDatabasesHaveNoChangesOnCompare() {
    MongoDB test = new MongoDB();
    MongoDB test1 = new MongoDB();

    assertEquals("The Mongo database should have no differences when neither has been modified", true,
        test.compare(test1).isEmpty());
  }

  @Test
  public void testCompareCreateSingleCollection() {
    expectedStatements.add(createPre + COLL_NAME_1);

    MongoDB test = new MongoDB();
    MongoDB test1 = new MongoDB();
    test.getCollections().put(COLL_NAME_1, coll1);
    test.getCollections().put(COLL_NAME_2, coll2);
    test1.getCollections().put(COLL_NAME_2, coll2);

    statements = test.compare(test1);

    assertEquals("The statements from the compare should have include the addition of the collection that only dev has",
        expectedStatements, statements);
  }

  @Test
  public void testCompareCreateMultipleCollection() {
    expectedStatements.add(createPre + COLL_NAME_1);
    expectedStatements.add(createPre + COLL_NAME_2 + ", capped=true, size=50000");

    MongoDB test = new MongoDB();
    MongoDB test1 = new MongoDB();
    test.getCollections().put(COLL_NAME_1, coll1);
    test.getCollections().put(COLL_NAME_2, coll2);

    statements = test.compare(test1);

    assertEquals("The statements should have the addition of both collections", expectedStatements, statements);
  }

  @Test
  public void testCompareDropSingleCollection() {
    expectedStatements.add(deletePre + COLL_NAME_1);

    MongoDB test = new MongoDB();
    MongoDB test1 = new MongoDB();

    test.getCollections().put(COLL_NAME_1, coll1);
    test.getCollections().put(COLL_NAME_2, coll2);
    test1.getCollections().put(COLL_NAME_2, coll2);
    statements = test1.compare(test);

    assertEquals(
        "The statements from the compare should have include the deletion of the collection that only live has",
        expectedStatements, statements);

  }

  @Test
  public void testCompareDropMultipleCollections() {
    expectedStatements.add(deletePre + COLL_NAME_1);
    expectedStatements.add(deletePre + COLL_NAME_2);

    MongoDB test = new MongoDB();
    MongoDB test1 = new MongoDB();
    test.getCollections().put(COLL_NAME_1, coll1);
    test.getCollections().put(COLL_NAME_2, coll2);

    statements = test1.compare(test);

    assertEquals("The statements should have the deletion of both collections", expectedStatements, statements);
  }

  @Test
  public void testCompareUpdateSingleCollection() {
    expectedStatements.add(deletePre + COLL_NAME_1);
    expectedStatements.add(createPre + COLL_NAME_1);

    Collection coll12 = new Collection(COLL_NAME_1, true, 67890);
    MongoDB test = new MongoDB();
    MongoDB test1 = new MongoDB();
    test.getCollections().put(COLL_NAME_1, coll1);
    test.getCollections().put(COLL_NAME_2, coll2);
    test1.getCollections().put(COLL_NAME_1, coll12);
    test1.getCollections().put(COLL_NAME_2, coll2);

    statements = test.compare(test1);

    assertEquals(
        "The statements from the compare should have include the deletion of the collection that only live has",
        expectedStatements, statements);
  }

  @Test
  public void testCompareUpdateMultipleCollections() {
    expectedStatements.add(deletePre + COLL_NAME_1);
    expectedStatements.add(createPre + COLL_NAME_1);
    expectedStatements.add(deletePre + COLL_NAME_2);
    expectedStatements.add(createPre + COLL_NAME_2);

    Collection coll12 = new Collection(COLL_NAME_1, true, 67890);
    Collection coll22 = new Collection(COLL_NAME_2, false, 0);
    MongoDB test = new MongoDB();
    MongoDB test1 = new MongoDB();
    test.getCollections().put(COLL_NAME_1, coll1);
    test.getCollections().put(COLL_NAME_2, coll2);
    test1.getCollections().put(COLL_NAME_1, coll12);
    test1.getCollections().put(COLL_NAME_2, coll2);
    test.getCollections().put(COLL_NAME_2, coll22);

    statements = test.compare(test1);

    assertEquals("The statements should have the deletion and creation of both collections", expectedStatements,
        statements);
  }

  @Test
  public void testCompareMixedCollections() {
    String names1 = "Commoner";
    String names2 = "Creeper";
    String names3 = "Creep";
    String names4 = "Pillager";
    String names5 = "Villager";
    expectedStatements.add(createPre + names5 + ", capped=true, size=234560");
    expectedStatements.add(createPre + names4);
    expectedStatements.add(deletePre + names3);
    expectedStatements.add(deletePre + names2);
    expectedStatements.add(deletePre + COLL_NAME_1);
    expectedStatements.add(createPre + COLL_NAME_1);
    expectedStatements.add(deletePre + COLL_NAME_2);
    expectedStatements.add(createPre + COLL_NAME_2 + ", capped=true, size=50000");

    Collection coll12 = new Collection(COLL_NAME_1, true, 67890);
    Collection coll22 = new Collection(COLL_NAME_2, false, 0);
    Collection coll3 = new Collection(names1, false, 0);
    Collection coll4 = new Collection(names2, false, 0);
    Collection coll5 = new Collection(names1, true, 587390);
    Collection coll6 = new Collection(names4, false, 0);
    Collection coll7 = new Collection(names5, true, 234560);
    MongoDB test = new MongoDB();
    MongoDB test1 = new MongoDB();
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

    assertEquals(
        "The Mongo database should suggest 8 changes when 2 collections need to be updated (4),"
            + " 2 collections need to be created (2), and 2 collections need to be dropped (2).",
        expectedStatements, statements);
  }
}
