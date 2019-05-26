package dbdiffchecker.nosql;

import java.util.HashMap;
import java.util.ArrayList;
import dbdiffchecker.Database;
import dbdiffchecker.DbConn;
import dbdiffchecker.DatabaseDiffernceCheckerException;

/**
 * @version 5-24-19
 * @since 5-24-19
 */
public class Bucket extends Database {
  // Instance variables
  private HashMap<String, String> documents = new HashMap<>();
  private HashMap<String, CouchbaseIndex> indices = new HashMap<>();
  private String name;
  private String bucketPlaceHolder;
  private String primaryKeyName;

  /**
   * Creates a database that models the Couchbase bucket using the Couchbase
   * connection in orde to get a list of documents, idndices, and other pertinent
   * information.
   * @param conn The connection to the Couchbase bucket.
   * @throws DatabaseDiffernceCheckerException Error connecting to the Couchbase
   *         bucket.
   */
  public Bucket(DbConn conn) throws DatabaseDiffernceCheckerException {
    CouchbaseConn connection = (CouchbaseConn) conn;
    connection.establishDatabaseConnection();
    connection.getDocuments(documents);
    connection.getIndices(indices);
    this.bucketPlaceHolder = connection.getBucketPlaceHolder();
    this.primaryKeyName = connection.getDefaultPrimaryName();
    this.name = connection.getDatabaseName();
    // drop the primary key that was added manually if it exists
    if (connection.primaryAdded()) {
      connection.runStatement("DROP INDEX `" + name + "`.`" + primaryKeyName + "`;");
    }
    connection.closeDatabaseConnection();
  }

  @Override
  public ArrayList<String> compare(Database liveBucket) {
    ArrayList<String> n1ql = new ArrayList<>();
    String liveBucketName = ((Bucket) liveBucket).name;
    // check for documents to create
    for (String documnetName : documents.keySet()) {
      if (!((Bucket) liveBucket).documents.containsKey(documnetName)) {
        n1ql.add("Create document: " + documnetName);
      }
    }
    // check for documents to drop
    for (String documnetName : ((Bucket) liveBucket).documents.keySet()) {
      if (!documents.containsKey(documnetName)) {
        n1ql.add("Drop document: " + documnetName);
      }
    }
    // check to see if any indices need to be dropped
    for (String indexName : ((Bucket) liveBucket).indices.keySet()) {
      if (!indices.containsKey(indexName)) {
        n1ql.add("DROP INDEX `" + liveBucketName + "`.`" + indexName + "`;");
      }
    }
    // check to see if any indices need to be added or modified
    for (String indexName : indices.keySet()) {
      if (((Bucket) liveBucket).indices.containsKey(indexName)) {
        // does the index need to be modified
        if (!((Bucket) liveBucket).indices.get(indexName).equals(indices.get(indexName))) {
          n1ql.add("DROP INDEX `" + liveBucketName + "`.`" + indexName + "`;");
          n1ql.add(indices.get(indexName).getCreateStatement().replace(bucketPlaceHolder, liveBucketName) + ";");
        }
      } else {
        n1ql.add(indices.get(indexName).getCreateStatement().replace(bucketPlaceHolder, liveBucketName) + ";");
      }
    }
    return n1ql;
  }
}
