package dbdiffchecker.nosql;

import java.util.HashMap;
import java.util.ArrayList;

/**
 * @version 5-24-19
 * @since 5-24-19
 */
public class Bucket {
  // Instance variables
  private HashMap<String, String> documents = new HashMap<>();
  private HashMap<String, CouchbaseIndex> indices = new HashMap<>();
  private CouchbaseConn conn = null;

  public static void main(String... args) {
    CouchbaseConn conn = new CouchbaseConn("Administrator", "ch@1RLes2", "localhost", "dev");
    Bucket devBucket = new Bucket(conn);
    CouchbaseConn conn1 = new CouchbaseConn("Administrator", "ch@1RLes2", "localhost", "test2");
    Bucket liveBucket = new Bucket(conn1);
    ArrayList<String> n1ql = devBucket.compareBuckets(liveBucket);
    conn1.establishDatabaseConnection();
    for (String statement : n1ql) {
      conn1.runStatement(statement);
    }
    conn1.closeDatabaseConnection();
  }

  public Bucket(CouchbaseConn conn) {
    this.conn = conn;
    this.conn.establishDatabaseConnection();
    this.conn.getDocuments(documents);
    this.conn.getIndices(indices);
    this.conn.closeDatabaseConnection();
  }

  public ArrayList<String> compareBuckets(Bucket liveBucket) {
    ArrayList<String> n1ql = new ArrayList<>();
    String liveBucketName = liveBucket.conn.getName();
    String bucketPlaceHolder = liveBucket.conn.getBucketPlaceHolder();
    String primaryKeyName = liveBucket.conn.getDefaultPrimaryName();
    // check for documents to create
    for (String documnetName : documents.keySet()) {
      if (!liveBucket.documents.containsKey(documnetName)) {
        n1ql.add("Create document: " + documnetName);
      }
    }
    // check for documents to drop
    for (String documnetName : liveBucket.documents.keySet()) {
      if (!documents.containsKey(documnetName)) {
        n1ql.add("Drop document: " + documnetName);
      }
    }
    // check to see if any indices need to be dropped
    for (String indexName : liveBucket.indices.keySet()) {
      if (!indices.containsKey(indexName)) {
        n1ql.add("DROP INDEX `" + liveBucketName + "`.`" + indexName + "`;");
      }
    }
    // check to see if any indices need to be added or modified
    for (String indexName : indices.keySet()) {
      if (liveBucket.indices.containsKey(indexName)) {
        // does the index need to be modified
        if (!liveBucket.indices.get(indexName).equals(indices.get(indexName))) {
          n1ql.add("DROP INDEX `" + liveBucketName + "`.`" + indexName + "`;");
          n1ql.add(indices.get(indexName).getCreateStatement().replace(bucketPlaceHolder, liveBucketName) + ";");
        }
      } else {
        n1ql.add(indices.get(indexName).getCreateStatement().replace(bucketPlaceHolder, liveBucketName) + ";");
      }
    }
    // drop the primary keys that were added manually if they exist
    if (this.conn.primaryAdded()) {
      n1ql.add("DROP INDEX `" + this.conn.getName() + "`.`" + primaryKeyName + "`;");
    }
    if (liveBucket.conn.primaryAdded()) {
      n1ql.add("DROP INDEX `" + liveBucketName + "`.`" + primaryKeyName + "`;");
    }
    return n1ql;
  }
}
