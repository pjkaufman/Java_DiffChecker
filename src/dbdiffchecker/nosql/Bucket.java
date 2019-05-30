package dbdiffchecker.nosql;

import dbdiffchecker.Database;
import dbdiffchecker.DatabaseDiffernceCheckerException;
import dbdiffchecker.DbConn;
import dbdiffchecker.sql.Index;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * Models a Couchbase bucket by keeping track of all indices and documents.
 * @author Peter Kaufman
 * @version 5-30-19
 * @since 5-24-19
 */
public class Bucket extends Database {
  // Instance variables
  private HashMap<String, String> documents = new HashMap<>();
  private HashMap<String, Index> indices = new HashMap<>();
  private String name = "";
  private String bucketPlaceHolder = "";
  private String primaryKeyName = "";

  /**
   * Creates a database that models the Couchbase bucket using the Couchbase
   * connection in orde to get a list of documents, idndices, and other pertinent
   * information.
   * @author Peter Kaufman
   * @param conn The connection to the Couchbase bucket.
   * @throws DatabaseDiffernceCheckerException Error connecting to the Couchbase
   *         bucket.
   */
  public Bucket(DbConn conn) throws DatabaseDiffernceCheckerException {
    CouchbaseConn connection = (CouchbaseConn) conn;
    connection.establishDatabaseConnection();
    // check to see if a primary index already exists
    connection.testConnection();
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

  /**
   * This is the default constructor for this class, <b>Needed for
   * Serialization</b>.
   */
  public Bucket() {}

  /**
   * Returns the list of Couchbase documents where the name is the key and value.
   * @author Peter Kaufman
   * @return The list of documents that exist in the bucket.
   */
  public HashMap<String, String> getDocuments() {
    return this.documents;
  }

  /**
   * Returns the list of Couchbase indices where the name is the key.
   * @author Peter Kaufman
   * @return The list of indices that exist in the bucket.
   */
  public HashMap<String, Index> getIndices() {
    return this.indices;
  }

  @Override
  public ArrayList<String> compare(Database liveBucket) {
    Bucket live = (Bucket) liveBucket;
    ArrayList<String> n1ql = new ArrayList<>();
    String liveBucketName = live.name;
    // check for documents to create
    for (String documnetName : documents.keySet()) {
      if (!live.getDocuments().containsKey(documnetName)) {
        n1ql.add("Create document: " + documnetName);
      }
    }
    // check for documents to drop
    for (String documnetName : live.getDocuments().keySet()) {
      if (!documents.containsKey(documnetName)) {
        n1ql.add("Drop document: " + documnetName);
      }
    }
    // check to see if any indices need to be dropped
    for (String indexName : live.getIndices().keySet()) {
      if (!indices.containsKey(indexName)) {
        n1ql.add("DROP INDEX `" + liveBucketName + "`.`" + indexName + "`;");
      }
    }
    // check to see if any indices need to be added or modified
    for (String indexName : indices.keySet()) {
      if (live.getIndices().containsKey(indexName)) {
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
