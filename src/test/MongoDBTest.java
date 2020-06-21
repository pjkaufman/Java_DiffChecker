package test;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import dbdiffchecker.nosql.MongoDB;
import dbdiffchecker.nosql.Collection;
import java.util.ArrayList;

/**
 * A unit test that makes sure that the MongoDB object works as intended.
 *
 * @author Peter Kaufman
 * @version 1-6-20
 * @since 5-10-19
 */
public class MongoDBTest {
    private Collection coll1, coll2;
    private ArrayList<String> statements;
    private String createPre = "Create Collection: ", deletePre = "Delete Collection: ";

    @Test
    /**
     * Tests whether the get collections method is working as intended.
     *
     * @author Peter Kaufman
     */
    public void testGetCollections() {
        String name1 = "Skipper", name2 = "Private";
        coll1 = new Collection(name1, false, 0);
        coll2 = new Collection(name2, true, 50000);
        MongoDB test = new MongoDB();
        // start assertions
        assertEquals("The Mongo database should be empty before anything is added to it", 0,
                test.getCollections().size());
        test.getCollections().put(name1, coll1);
        assertEquals("The Mongo database should have 1 element after the first collection is added", 1,
                test.getCollections().size());
        assertEquals("The Mongo database should have the added collection", true,
                test.getCollections().get(name1).equals(coll1));
        test.getCollections().put(name2, coll2);
        assertEquals("The Mongo database should have 2 elements after the second collection is added", 2,
                test.getCollections().size());
        assertEquals("The Mongo database should have the added collection", true,
                test.getCollections().get(name2).equals(coll2));
    }

    @Test
    /**
     * Tests whether or not the compare function picks up that collections need to
     * be added.
     *
     * @author Peter Kaufman
     */
    public void testCompareCreateCollections() {
        String name1 = "Skipper", name2 = "Private";
        coll1 = new Collection(name1, false, 0);
        coll2 = new Collection(name2, true, 50000);
        MongoDB test = new MongoDB(), test1 = new MongoDB();
        // start assertions
        assertEquals("The Mongo database should have no differences when neither has been modified", 0,
                test.compare(test1).size());
        test.getCollections().put(name1, coll1);
        test.getCollections().put(name2, coll2);
        test1.getCollections().put(name2, coll2);
        statements = test.compare(test1);
        assertEquals("The Mongo database should suggest 1 change when the dev database has 1 more collection", 1,
                statements.size());
        assertEquals(
                "The statements from the compare should have include the addition of the collection that only dev has",
                true, statements.get(0).equals(createPre + name1));
        test1.getCollections().remove(name2);
        statements = test.compare(test1);
        assertEquals("The Mongo database compare should suggest 2 changes when the dev database has 2 more collections",
                2, statements.size());
        assertEquals("The statements should have the addition of both collections", true,
                statements.contains(createPre + name1)
                        && statements.contains(createPre + name2 + ", capped=true, size=50000"));
    }

    @Test
    /**
     * Tests whether or not the compare function picks up that collections need to
     * be dropped.
     *
     * @author Peter Kaufman
     */
    public void testCompareDropCollections() {
        String name1 = "Skipper", name2 = "Private";
        coll1 = new Collection(name1, false, 0);
        coll2 = new Collection(name2, true, 50000);
        MongoDB test = new MongoDB(), test1 = new MongoDB();
        // start assertions
        assertEquals("The Mongo database should have no differences when neither has been modified", 0,
                test.compare(test1).size());
        test.getCollections().put(name1, coll1);
        test.getCollections().put(name2, coll2);
        test1.getCollections().put(name2, coll2);
        statements = test1.compare(test);
        assertEquals("The Mongo database should suggest 1 change when the live database has 1 more collection", 1,
                statements.size());
        assertEquals(
                "The statements from the compare should have include the deletion of the collection that only live has",
                true, statements.get(0).equals(deletePre + name1));
        test1.getCollections().remove(name2);
        statements = test1.compare(test);
        assertEquals(
                "The Mongo database compare should suggest 2 changes when the live database has 2 more collections", 2,
                statements.size());
        assertEquals("The statements should have the deletion of both collections", true,
                statements.contains(deletePre + name1) && statements.contains(deletePre + name2));
    }

    @Test
    /**
     * Tests whether or not the compare function picks up that collections need to
     * be updated.
     *
     * @author Peter Kaufman
     */
    public void testCompareUpdateCollections() {
        String name1 = "Skipper", name2 = "Private";
        coll1 = new Collection(name1, false, 0);
        coll2 = new Collection(name2, true, 50000);
        Collection coll12 = new Collection(name1, true, 67890), coll22 = new Collection(name2, false, 0);
        MongoDB test = new MongoDB(), test1 = new MongoDB();
        // start assertions
        assertEquals("The Mongo database should have no differences when neither has been modified", 0,
                test.compare(test1).size());
        test.getCollections().put(name1, coll1);
        test.getCollections().put(name2, coll2);
        test1.getCollections().put(name1, coll12);
        test1.getCollections().put(name2, coll2);
        statements = test.compare(test1);
        assertEquals("The Mongo database should suggest 2 changes when a collection needs to be updated", 2,
                statements.size());
        assertEquals(
                "The statements from the compare should have include the deletion of the collection that only live has",
                true, statements.contains(deletePre + name1) && statements.contains(createPre + name1));
        test.getCollections().put(name2, coll22);
        statements = test.compare(test1);
        assertEquals("The Mongo database compare should suggest 4 changes when 2 collections need updating", 4,
                statements.size());
        assertEquals("The statements should have the deletion and creation of both collections", true,
                statements.contains(deletePre + name1) && statements.contains(deletePre + name2)
                        && statements.contains(createPre + name1) && statements.contains(createPre + name2));
    }

    @Test
    /**
     * Tests whether or not the compare function picks up when several operations
     * need to be done on a database.
     *
     * @author Peter Kaufman
     */
    public void testCompareMixedCollections() {
        String name1 = "Skipper", name2 = "Private", name3 = "Commoner", name4 = "Creeper", name5 = "Creep",
                name6 = "Pillager", name7 = "Villager";
        coll1 = new Collection(name1, false, 0);
        coll2 = new Collection(name2, true, 50000);
        Collection coll12 = new Collection(name1, true, 67890), coll22 = new Collection(name2, false, 0),
                coll3 = new Collection(name3, false, 0), coll4 = new Collection(name4, false, 0),
                coll5 = new Collection(name3, true, 587390), coll6 = new Collection(name6, false, 0),
                coll7 = new Collection(name7, true, 234560);
        MongoDB test = new MongoDB(), test1 = new MongoDB();
        // setup
        test.getCollections().put(name1, coll1); // collection to modify
        test.getCollections().put(name2, coll2); // collection to modify
        test.getCollections().put(name3, coll3); // common collection
        test.getCollections().put(name6, coll6); // collection to add
        test.getCollections().put(name7, coll7); // collection to add
        test1.getCollections().put(name1, coll12); // collection to modify
        test1.getCollections().put(name2, coll22); // collection to modify
        test1.getCollections().put(name3, coll3); // common collection
        test1.getCollections().put(name4, coll4); // collection to drop
        test1.getCollections().put(name5, coll5); // collection to drop
        statements = test.compare(test1);
        // start assertions
        assertEquals(
                "The Mongo database should suggest 8 changes when 2 collections need to be updated (4),"
                        + " 2 collections need to be created (2), and 2 collections need to be dropped (2).",
                8, statements.size());
        // 2 create statements
        assertEquals(
                "The statements from the compare should include the creations of the collections that only dev has",
                true, statements.contains(createPre + name6)
                        && statements.contains(createPre + name7 + ", capped=true, size=234560"));
        // 2 delete statements
        assertEquals(
                "The statements from the compare should include the deletions of the collections that only live has",
                true, statements.contains(deletePre + name4) && statements.contains(deletePre + name5));
        // 4 update statements
        assertEquals(
                "The statements from the compare should include the deletions and recreations of the collections that need to be updated",
                true,
                statements.contains(deletePre + name1) && statements.contains(deletePre + name2)
                        && statements.contains(createPre + name1)
                        && statements.contains(createPre + name2 + ", capped=true, size=50000"));
    }
}
