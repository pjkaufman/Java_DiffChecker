package dbdiffchecker.nosql;

import java.util.Map;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.query.N1qlParams;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.N1qlQueryResult;
import com.couchbase.client.java.query.N1qlQueryRow;
import com.couchbase.client.java.query.dsl.element.IndexElement;
import com.couchbase.client.java.query.util.IndexInfo;

import dbdiffchecker.DatabaseDifferenceCheckerException;
import dbdiffchecker.DbConn;
import dbdiffchecker.sql.Index;

/**
 * Establishes a connection with a Couchbase bucket based on the password,
 * username, host, and bucket name provided.
 *
 * @author Peter Kaufman
 */
public class CouchbaseConn extends DbConn {
  private static final String CONN_STR_FORMAT = "couchbase://%s/%s?operation_timeout=5.5&config_total_timeout=15&http_poolsize=0";
  private String username;
  private String password;
  private String bucketName;
  private String host;
  private Bucket bucket;
  private N1qlParams params = N1qlParams.build().adhoc(false);
  private N1qlQuery query;
  private N1qlQueryResult result;
  private boolean primaryAdded = false;

  /**
   * Initializes the username, password, host and bucketName of the bucket
   * connection.
   *
   * @param username   The username of the Couchbase account.
   * @param password   The password of the Couchbase account.
   * @param host       The host of the Couchbase bucket.
   * @param bucketName The bucket in Couchbase that the connection is to be
   *                   established with.
   */
  public CouchbaseConn(String username, String password, String host, String bucketName) {
    this.username = username;
    this.password = password;
    this.host = host;
    this.bucketName = bucketName;
  }

  /**
   * Returns whether a primary key was added to the bucket.
   *
   * @return Whether a primary key was added to the bucket.
   */
  protected boolean primaryAdded() {
    return primaryAdded;
  }

  @Override
  public String getDatabaseName() {
    return bucketName;
  }

  @Override
  public void establishDatabaseConnection() throws DatabaseDifferenceCheckerException {
    try {
      Cluster cluster = CouchbaseCluster.fromConnectionString(String.format(CONN_STR_FORMAT, host, bucketName));
      cluster.authenticate(username, password);
      bucket = cluster.openBucket(bucketName);
    } catch (Exception cause) {
      throw new DatabaseDifferenceCheckerException("There was an error connecting to the bucket named " + bucketName,
          cause, 1009);
    }
  }

  @Override
  public void closeDatabaseConnection() {
    bucket.close();
  }

  /**
   * Gets and lists all documents that exist in the Couchbase bucket.
   *
   * @param documents A list where all of the document names will be stored for
   *                  fast lookup later.
   */
  protected void getDocuments(Map<String, String> documents) {
    query = N1qlQuery.simple("SELECT META().id AS document FROM `" + bucketName + "`", params);
    result = bucket.query(query);
    String documentName;
    for (N1qlQueryRow row : result) {
      documentName = row.value().getString("document");
      documents.put(documentName, documentName);
    }
  }

  /**
   * Gets and lists all indices that exist in the Couchbase bucket.
   *
   * @param indices A list where all of the index names and data that will be
   *                stored for fast lookup later.
   */
  protected void getIndices(Map<String, Index> indices) {
    query = N1qlQuery.simple("SELECT indexes FROM system:indexes WHERE keyspace_id = \"" + bucketName + "\"", params);
    result = bucket.query(query);
    IndexInfo index;
    StringBuilder create;
    String drop;
    int size;
    for (N1qlQueryRow row : result) {
      index = new IndexInfo(row.value().getObject("indexes"));
      boolean isManuallyAddedKey = primaryAdded && index.name().equals(dbdiffchecker.nosql.Bucket.PRIMARY_KEY_NAME);
      if (isManuallyAddedKey) {
        continue;
      }
      create = new StringBuilder(new IndexElement(index.name(), index.isPrimary()).export() + " ON `"
          + dbdiffchecker.nosql.Bucket.BUCKET_PLACE_HOLDER + "`");
      size = index.indexKey().size();
      if (size != 0) {
        create.append(" (");
        for (int i = 0; i < size; i++) {
          create.append(index.indexKey().getString(i));
        }
        create.append(")");
      }
      if (index.condition().length() > 0) {
        create.append(" WHERE" + index.condition());
      }
      if (!index.isPrimary()) {
        create.append(" USING " + index.type());
      }
      drop = "DROP INDEX `" + dbdiffchecker.nosql.Bucket.BUCKET_PLACE_HOLDER + "`.`" + index.name() + "`;";
      indices.put(index.name(), new Index(index.name(), create.toString(), drop));
    }
  }

  /**
   * Takes in a N1QL statement and applies it to the bucket.
   *
   * @param n1qlStatement A N1QL statement to be run on the bucket.
   */
  public void runStatement(String n1qlStatement) {
    if (n1qlStatement.startsWith(dbdiffchecker.nosql.Bucket.CREATE_DOC_IDENTIFIER)) {
      JsonDocument document = JsonDocument.create(n1qlStatement.substring(n1qlStatement.indexOf(": ") + 2),
          JsonObject.empty());
      bucket.insert(document);
    } else if (n1qlStatement.startsWith(dbdiffchecker.nosql.Bucket.DELETE_DOC_IDENTIFIER)) {
      bucket.remove(n1qlStatement.substring(n1qlStatement.indexOf(": ") + 2));
    } else {
      query = N1qlQuery.simple(n1qlStatement, params);
      bucket.query(query);
    }
  }

  /**
   * Tests to see if the bucket can be queried immediately or if a primary key
   * needs to be added first. It will add a primary key if it is needed.
   *
   * @throws DatabaseDifferenceCheckerException Error trying to connect to the
   *                                            bucket.
   */
  @Override
  public void testConnection() throws DatabaseDifferenceCheckerException {
    try {
      query = N1qlQuery.simple("SELECT META().id AS document FROM `" + bucketName + "`", params);
      result = bucket.query(query);
    } catch (Exception error) {
      String errorMsg = error.getCause().toString();
      if (errorMsg.contains("4000") && errorMsg.contains("CREATE INDEX")) {
        query = N1qlQuery.simple(
            "CREATE PRIMARY INDEX " + dbdiffchecker.nosql.Bucket.PRIMARY_KEY_NAME + " ON `" + bucketName + "`", params);
        bucket.query(query);
        primaryAdded = true;
      } else {
        throw new DatabaseDifferenceCheckerException(
            "There was an error testing the connection to the bucket named " + bucketName, error, 1010);
      }
    }
  }
}
