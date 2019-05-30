import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import dbdiffchecker.nosql.Bucket;
import dbdiffchecker.sql.Index;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A unit test that makes sure that the Bucket object works as intended.
 * @author Peter Kaufman
 * @version 5-30-19
 * @since 5-24-19
 */
public class BucketTest {
  private Bucket bucket, bucket2;
  private String bucketName, name, create;
  private ArrayList<String> statements = new ArrayList<>();
  private ArrayList<String> expectedStatements = new ArrayList<>();

  @Before
  /**
   * Sets up for the serailization compare by resetiing the list of expected
   * statements and statements.
   * @author Peter Kaufman
   */
  public void setupForCompare() {
    statements.clear();
    expectedStatements.clear();
  }

  @Test
  /**
   * Tests whether Bucket object's compare method works as intended on simpler
   * situations.
   * @author Peter Kaufman
   */
  public void testCompareSimple() {
    ArrayList<String> expectedStatements2 = new ArrayList<>();
    name = "blob";
    expectedStatements.add("Create document: " + name);
    expectedStatements2.add("Drop document: " + name);
    // two empty buckets
    bucket = new Bucket();
    bucket2 = new Bucket();
    assertEquals("The statements needed to make two empty buckets the same should be none", 0,
        bucket.compare(bucket2).size());
    // one empty bucket
    // create one document
    bucket.getDocuments().put(name, name);
    statements = bucket.compare(bucket2);
    assertEquals("The statements needed to make two buckets with a total of 1 document and 0 indices is 1 (create)", 1,
        statements.size());
    assertEquals(
        "There should be one document create statement if the development database has 1 document and live has none (no indices)",
        expectedStatements, statements);
    // drop one document
    statements = bucket2.compare(bucket);
    assertEquals("The statements needed to make two buckets with a total of 1 document and 0 indices is 1 (drop)", 1,
        statements.size());
    assertEquals(
        "There should be one document drop statement if the live database has 1 document and development has none (no indices)",
        expectedStatements2, statements);
    // create two documents
    name = "Destruction";
    bucket.getDocuments().put(name, name);
    statements = bucket.compare(bucket2);
    expectedStatements.add("Create document: " + name);
    expectedStatements2.add("Drop document: " + name);
    assertEquals(
        "There should be two document create statements if the development database has 2 documents and live has none (no indices)",
        expectedStatements, statements);
    // dtop two documents
    statements = bucket2.compare(bucket);
    assertEquals(
        "There should be two document drop statements if the live database has 2 documents and development has none (no indices)",
        expectedStatements2, statements);
    // create one index
    bucket.getDocuments().clear();
    expectedStatements.clear();
    expectedStatements2.clear();
    name = "dev_primary";
    create = "CREATE INDEX `" + name + "` ON `development`";
    expectedStatements.add(create + ";");
    bucket.getIndices().put(name, new Index(name, create));
    statements = bucket.compare(bucket2);
    assertEquals(
        "There should be one index create statements if the development database has 1 index and live has none (no documents)",
        expectedStatements, statements);
    // drop one
    expectedStatements2.add("DROP INDEX ``.`" + name + "`;");
    statements = bucket2.compare(bucket);
    assertEquals(
        "There should be one index drop statements if the live database has 1 index and development has none (no documents)",
        expectedStatements2, statements);
    // create two indices
    name = "devSpeed";
    create = "CREATE INDEX `" + name + "` ON `development` WHERE (`abv` > 6)";
    expectedStatements.add(0, create + ";");
    bucket.getIndices().put(name, new Index(name, create));
    statements = bucket.compare(bucket2);
    assertEquals(
        "There should be two index create statements if the development database has 2 indices and live has none (no documents)",
        expectedStatements, statements);
    // drop two indices
    expectedStatements2.add(0, "DROP INDEX ``.`" + name + "`;");
    statements = bucket2.compare(bucket);
    assertEquals(
        "There should be two index drop statements if the live database has 2 indices and development has none (no documents)",
        expectedStatements2, statements);
  }

  @Test
  /**
   * Tests whether Bucket object's compare method works as intended on complex
   * situations.
   * @author Peter Kaufman
   */
  public void testCompareComplex() {
    name = "blob";
    expectedStatements.add("Create document: " + name);
    bucket = new Bucket();
    bucket2 = new Bucket();
    // add documents to bucket and bucket2
    bucket.getDocuments().put(name, name);
    name = "dropDoc";
    expectedStatements.add("Drop document: " + name);
    bucket2.getDocuments().put(name, name);
    name = "leave";
    bucket.getDocuments().put(name, name);
    bucket2.getDocuments().put(name, name);
    // create indices
    name = "create";
    create = "CREATE INDEX `" + name + "` ON `development`";
    expectedStatements.add(create + ";");
    bucket.getIndices().put(name, new Index(name, create));
    name = "leave";
    create = "CREATE INDEX `" + name + "` ON `development`";
    bucket.getIndices().put(name, new Index(name, create));
    bucket2.getIndices().put(name, new Index(name, create));
    name = "drop";
    create = "CREATE INDEX `" + name + "` ON `development` WHERE (`abv` > 6)";
    expectedStatements.add(expectedStatements.size() - 1, "DROP INDEX ``.`" + name + "`;");
    bucket2.getIndices().put(name, new Index(name, create));
    statements = bucket.compare(bucket2);
    assertEquals(
        "There should be one index drop, one index create, one document drop, and one document create statment",
        expectedStatements, statements);
  }

  @Test
  /**
   * Tests whether Bucket object's compare method works as intended on modifying
   * indices.
   * @author Peter Kaufman
   */
  public void testModifyIndex() {
    name = "blob";
    create = "CREATE INDEX `" + name + "` ON `development`";
    expectedStatements.add("DROP INDEX ``.`" + name + "`;");
    expectedStatements.add(create + ";");
    bucket = new Bucket();
    bucket2 = new Bucket();
    // add indices
    bucket.getIndices().put(name, new Index(name, create));
    create = "CREATE INDEX `" + name + "` ON `development` WHERE (`abv` > 6)";
    bucket2.getIndices().put(name, new Index(name, create));
    statements = bucket.compare(bucket2);
    assertEquals("There should be statements to modify the statements", expectedStatements, statements);
  }
}
