package dbdiffchecker.nosql;

import java.util.HashMap;
import java.util.ArrayList;
import dbdiffchecker.Database;
import dbdiffchecker.DatabaseDiffernceCheckerException;

/**
 * @version 5-24-19
 * @since 5-24-19
 */
public class Bucket extends Database {
  // Instance variables
  private HashMap<String, String> documents = new HashMap<>();
  private HashMap<String, CouchbaseIndex> indices = new HashMap<>();
  private CouchbaseConn conn = null;

  public static void main(String... args) throws DatabaseDiffernceCheckerException {
    CouchbaseConn conn = new CouchbaseConn("Administrator", "ch@1RLes2", "localhost", "dev");
    Bucket devBucket = new Bucket(conn);
    CouchbaseConn conn1 = new CouchbaseConn("Administrator", "ch@1RLes2", "localhost", "test2");
    Bucket liveBucket = new Bucket(conn1);
    ArrayList<String> n1ql = devBucket.compare(liveBucket);
    conn1.establishDatabaseConnection();
    for (String statement : n1ql) {
      conn1.runStatement(statement);
    }
    conn1.closeDatabaseConnection();
  }

  public Bucket(CouchbaseConn conn) throws DatabaseDiffernceCheckerException {
    this.conn = conn;
    this.conn.establishDatabaseConnection();
    this.conn.getDocuments(documents);
    this.conn.getIndices(indices);
    this.conn.closeDatabaseConnection();
  }

  @Override
  public ArrayList<String> compare(Database liveBucket) {
    ArrayList<String> n1ql = new ArrayList<>();
    String liveBucketName = ((Bucket)liveBucket).conn.getDatabaseName();
    String bucketPlaceHolder = ((Bucket)liveBucket).conn.getBucketPlaceHolder();
    String primaryKeyName = ((Bucket)liveBucket).conn.getDefaultPrimaryName();
    // check for documents to create
    for (String documnetName : documents.keySet()) {
      if (!((Bucket)liveBucket).documents.containsKey(documnetName)) {
        n1ql.add("Create document: " + documnetName);
      }
    }
    // check for documents to drop
    for (String documnetName : ((Bucket)liveBucket).documents.keySet()) {
      if (!documents.containsKey(documnetName)) {
        n1ql.add("Drop document: " + documnetName);
      }
    }
    // check to see if any indices need to be dropped
    for (String indexName : ((Bucket)liveBucket).indices.keySet()) {
      if (!indices.containsKey(indexName)) {
        n1ql.add("DROP INDEX `" + liveBucketName + "`.`" + indexName + "`;");
      }
    }
    // check to see if any indices need to be added or modified
    for (String indexName : indices.keySet()) {
      if (((Bucket)liveBucket).indices.containsKey(indexName)) {
        // does the index need to be modified
        if (!((Bucket)liveBucket).indices.get(indexName).equals(indices.get(indexName))) {
          n1ql.add("DROP INDEX `" + liveBucketName + "`.`" + indexName + "`;");
          n1ql.add(indices.get(indexName).getCreateStatement().replace(bucketPlaceHolder, liveBucketName) + ";");
        }
      } else {
        n1ql.add(indices.get(indexName).getCreateStatement().replace(bucketPlaceHolder, liveBucketName) + ";");
      }
    }
    // drop the primary keys that were added manually if they exist
    if (this.conn.primaryAdded()) {
      n1ql.add("DROP INDEX `" + this.conn.getDatabaseName() + "`.`" + primaryKeyName + "`;");
    }
    if (((Bucket)liveBucket).conn.primaryAdded()) {
      n1ql.add("DROP INDEX `" + liveBucketName + "`.`" + primaryKeyName + "`;");
    }
    return n1ql;
  }
}
