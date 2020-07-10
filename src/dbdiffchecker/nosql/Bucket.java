package dbdiffchecker.nosql;

import dbdiffchecker.Database;
import dbdiffchecker.DatabaseDifferenceCheckerException;
import dbdiffchecker.DbConn;
import dbdiffchecker.sql.Index;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 * Models a Couchbase bucket by keeping track of all indices and documents.
 *
 * @author Peter Kaufman
 * @version 6-20-20
 * @since 5-24-19
 */
public class Bucket extends Database {
  protected static final String BUCKET_PLACE_HOLDER = "dbDiffBucket";
  protected static final String PRIMARY_KEY_NAME = "dbDiffKey";
  private static final long serialVersionUID = 1L;
  private Map<String, String> documents = new HashMap<>();
  private Map<String, Index> indices = new HashMap<>();
  private String name = "";

  /**
   * Creates a database that models the Couchbase bucket using the Couchbase
   * connection in orde to get a list of documents, idndices, and other pertinent
   * information.
   *
   * @param conn The connection to the Couchbase bucket.
   * @throws DatabaseDifferenceCheckerException Error connecting to the Couchbase
   *                                            bucket.
   */
  public Bucket(DbConn conn) throws DatabaseDifferenceCheckerException {
    CouchbaseConn connection = (CouchbaseConn) conn;
    connection.establishDatabaseConnection();
    // check to see if a primary index already exists
    connection.testConnection();
    connection.getDocuments(documents);
    connection.getIndices(indices);
    name = connection.getDatabaseName();
    // drop the primary key that was added manually if it exists
    if (connection.primaryAdded()) {
      connection.runStatement("DROP INDEX `" + name + "`.`" + PRIMARY_KEY_NAME + "`;");
    }
    connection.closeDatabaseConnection();
  }

  /**
   * <b>Needed for Serialization</b>
   */
  public Bucket() {
  }

  /**
   * Returns the list of Couchbase documents where the name is the key and value.
   *
   * @return The list of documents that exist in the bucket.
   */
  public Map<String, String> getDocuments() {
    return documents;
  }

  /**
   * Returns the list of Couchbase indices where the name is the key.
   *
   * @return The list of indices that exist in the bucket.
   */
  public Map<String, Index> getIndices() {
    return indices;
  }

  @Override
  public List<String> compare(Database liveBucket) {
    Bucket live = (Bucket) liveBucket;
    List<String> n1ql = new ArrayList<>();
    String liveBucketName = live.name;
    for (String documnetName : documents.keySet()) {
      if (!live.getDocuments().containsKey(documnetName)) {
        n1ql.add("Create document: " + documnetName);
      }
    }

    for (String documnetName : live.getDocuments().keySet()) {
      if (!documents.containsKey(documnetName)) {
        n1ql.add("Drop document: " + documnetName);
      }
    }
    // check to see if any indices need to be dropped or modified
    Index couchbaseIndex;
    for (String indexName : live.getIndices().keySet()) {
      couchbaseIndex = live.indices.get(indexName);
      if (!indices.containsKey(indexName)) {
        n1ql.add(couchbaseIndex.getDrop());
      } else if (!couchbaseIndex.equals(indices.get(indexName))) {
        n1ql.add(couchbaseIndex.getDrop());
        n1ql.add(indices.get(indexName).getCreateStatement().replace(BUCKET_PLACE_HOLDER, liveBucketName) + ";");
      }
    }
    // check to see if any indices need to be added or modified
    for (Map.Entry<String, Index> index : indices.entrySet()) {
      if (!live.getIndices().containsKey(index.getKey())) {
        n1ql.add(index.getValue().getCreateStatement().replace(BUCKET_PLACE_HOLDER, liveBucketName) + ";");
      }
    }
    return n1ql;
  }
}
