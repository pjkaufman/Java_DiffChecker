package test;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import dbdiffchecker.nosql.Bucket;
import dbdiffchecker.sql.Index;
import java.util.ArrayList;
import java.util.List;

public class BucketTest {
  private static final String CREATE_PRE = "Create Document: ";
  private static final String DELETE_PRE = "Delete Document: ";
  private Bucket bucket;
  private Bucket bucket2;
  private String name;
  private String create;
  private String drop;
  private List<String> statements = new ArrayList<>();
  private List<String> expectedStatements = new ArrayList<>();

  @Before
  public void setupForCompare() {
    expectedStatements.clear();
  }

  @Test
  public void testEmptyBucketsGenerateNoStatementsWhenCompared() {
    bucket = new Bucket();
    bucket2 = new Bucket();

    assertEquals("The statements needed to make two empty buckets the same should be none", true,
        bucket.compare(bucket2).isEmpty());
  }

  @Test
  public void testCompareCreateSingleDocument() {
    name = "blob";
    expectedStatements.add(CREATE_PRE + name);

    bucket = new Bucket();
    bucket2 = new Bucket();

    bucket.getDocuments().put(name, name);
    statements = bucket.compare(bucket2);

    assertEquals(
        "There should be one document create statement if the development database has 1 document and live has none (no indices)",
        expectedStatements, statements);
  }

  @Test
  public void testCompareCreateMultipleDocuments() {
    name = "blob";
    expectedStatements.add(CREATE_PRE + name);

    bucket = new Bucket();
    bucket2 = new Bucket();
    bucket.getDocuments().put(name, name);

    name = "Destruction";
    expectedStatements.add(CREATE_PRE + name);
    bucket.getDocuments().put(name, name);

    statements = bucket.compare(bucket2);

    assertEquals(
        "There should be two document create statements if the development database has 2 documents and live has none",
        expectedStatements, statements);
  }

  @Test
  public void testCompareDeleteSingleDocument() {
    name = "blob";
    expectedStatements.add(DELETE_PRE + name);

    bucket = new Bucket();
    bucket2 = new Bucket();

    bucket2.getDocuments().put(name, name);
    statements = bucket.compare(bucket2);

    assertEquals(
        "There should be one document drop statements if the live database has 1 document and development has none",
        expectedStatements, statements);
  }

  @Test
  public void testCompareDeleteMultipleDocuments() {
    name = "blob";
    expectedStatements.add(DELETE_PRE + name);

    bucket = new Bucket();
    bucket2 = new Bucket();
    bucket2.getDocuments().put(name, name);

    name = "Destruction";
    expectedStatements.add(DELETE_PRE + name);
    bucket2.getDocuments().put(name, name);

    statements = bucket.compare(bucket2);

    assertEquals(
        "There should be two document drop statements if the live database has 2 documents and development has none",
        expectedStatements, statements);
  }

  @Test
  public void testCompareAddSingleIndex() {
    name = "dev_primary";
    create = "CREATE INDEX `" + name + "` ON `development`";
    drop = "DROP INDEX `" + name + "`;";
    expectedStatements.add(create + ";");

    bucket = new Bucket();
    bucket2 = new Bucket();
    bucket.getIndices().put(name, new Index(name, create, drop));
    statements = bucket.compare(bucket2);

    assertEquals(
        "There should be one index create statements if the development database has 1 index and live has none",
        expectedStatements, statements);
  }

  @Test
  public void testCompareAddIndices() {
    name = "dev_primary";
    create = "CREATE INDEX `" + name + "` ON `development`";
    drop = "DROP INDEX `" + name + "`;";
    expectedStatements.add(create + ";");

    bucket = new Bucket();
    bucket2 = new Bucket();
    bucket.getIndices().put(name, new Index(name, create, drop));

    name = "devSpeed";
    create = "CREATE INDEX `" + name + "` ON `development` WHERE (`abv` > 6)";
    drop = "DROP INDEX `" + name + "`;";
    expectedStatements.add(0, create + ";");

    bucket.getIndices().put(name, new Index(name, create, drop));
    statements = bucket.compare(bucket2);

    assertEquals(
        "There should be two index create statements if the development database has 2 indices and live has none",
        expectedStatements, statements);
  }

  @Test
  public void testCompareDropSingleIndex() {
    name = "dev_primary";
    create = "CREATE INDEX `" + name + "` ON `development`";
    drop = "DROP INDEX `" + name + "`;";
    expectedStatements.add(drop);

    bucket = new Bucket();
    bucket2 = new Bucket();
    bucket2.getIndices().put(name, new Index(name, create, drop));
    statements = bucket.compare(bucket2);

    assertEquals("There should be one index drop statements if the live database has 1 index and dev has none",
        expectedStatements, statements);
  }

  @Test
  public void testCompareDropIndices() {
    name = "dev_primary";
    create = "CREATE INDEX `" + name + "` ON `development`";
    drop = "DROP INDEX `" + name + "`;";
    expectedStatements.add(drop);

    bucket = new Bucket();
    bucket2 = new Bucket();
    bucket2.getIndices().put(name, new Index(name, create, drop));

    name = "devSpeed";
    create = "CREATE INDEX `" + name + "` ON `development` WHERE (`abv` > 6)";
    drop = "DROP INDEX `" + name + "`;";
    expectedStatements.add(0, drop);

    bucket2.getIndices().put(name, new Index(name, create, drop));
    statements = bucket.compare(bucket2);

    assertEquals("There should be two index drop statements if the live database has 2 indices and dev has none",
        expectedStatements, statements);
  }

  @Test
  public void testCompareComplex() {
    name = "blob";
    expectedStatements.add(CREATE_PRE + name);

    bucket = new Bucket();
    bucket2 = new Bucket();

    bucket.getDocuments().put(name, name);
    name = "dropDoc";

    expectedStatements.add(DELETE_PRE + name);

    bucket2.getDocuments().put(name, name);
    name = "leave";
    bucket.getDocuments().put(name, name);
    bucket2.getDocuments().put(name, name);

    name = "create";
    drop = "DROP INDEX `" + name + "`;";
    create = "CREATE INDEX `" + name + "` ON `development`";

    expectedStatements.add(create + ";");

    bucket.getIndices().put(name, new Index(name, create, drop));
    name = "leave";
    drop = "DROP INDEX `" + name + "`;";
    create = "CREATE INDEX `" + name + "` ON `development`";
    bucket.getIndices().put(name, new Index(name, create, drop));
    bucket2.getIndices().put(name, new Index(name, create, drop));
    name = "drop";
    drop = "DROP INDEX `" + name + "`;";
    create = "CREATE INDEX `" + name + "` ON `development` WHERE (`abv` > 6)";

    expectedStatements.add(expectedStatements.size() - 1, drop);

    bucket2.getIndices().put(name, new Index(name, create, drop));
    statements = bucket.compare(bucket2);

    assertEquals(
        "There should be one index drop, one index create, one document drop, and one document create statment",
        expectedStatements, statements);
  }

  @Test
  public void testModifyIndex() {
    name = "blob";
    create = "CREATE INDEX `" + name + "` ON `development`";
    drop = "DROP INDEX `" + name + "`;";
    expectedStatements.add(drop);
    expectedStatements.add(create + ";");

    bucket = new Bucket();
    bucket2 = new Bucket();
    bucket.getIndices().put(name, new Index(name, create, drop));
    create = "CREATE INDEX `" + name + "` ON `development` WHERE (`abv` > 6)";
    bucket2.getIndices().put(name, new Index(name, create, drop));
    statements = bucket.compare(bucket2);

    assertEquals("There should be statements to modify the index", expectedStatements, statements);
  }
}
