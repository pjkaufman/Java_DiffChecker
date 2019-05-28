package dbdiffchecker.nosql;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.query.N1qlParams;
import com.couchbase.client.java.query.N1qlQueryRow;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.N1qlQueryResult;
import com.couchbase.client.java.query.dsl.element.IndexElement;
import com.couchbase.client.java.query.util.IndexInfo;
import dbdiffchecker.DatabaseDiffernceCheckerException;
import dbdiffchecker.DbConn;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.document.JsonDocument;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * @version 5-24-19
 * @since 5-23-19
 */
public class CouchbaseConn extends DbConn {
  // Instance variables
  private static final String bucketPlaceHolder = "dbDiffBucket";
  private static final String primaryKeyName = "dbDiffKey";
  private String username;
  private String password;
  private String bucketName;
  private String host;
  private Bucket bucket;
  private N1qlParams params = N1qlParams.build().adhoc(false);
  private N1qlQuery query;
  private N1qlQueryResult result;
  private boolean primaryAdded = false;

  public CouchbaseConn(String username, String password, String host, String bucketName) {
    this.username = username;
    this.password = password;
    this.host = host;
    this.bucketName = bucketName;
  }

  /**
   * Returns whether a primary key was added to the bucket.
   * @return Whether a primary key was added to the bucket.
   */
  public boolean primaryAdded() {
    return primaryAdded;
  }

  @Override
  public String getDatabaseName() {
    return bucketName;
  }

  /**
   * Gets and returns the bucket placeholder used in index create statements to
   * hole the place of the bucket to affect.
   * @return The bucket placeholder used in index create statements.
   */
  public String getBucketPlaceHolder() {
    return bucketPlaceHolder;
  }

  /**
   * Gets and returns the default name to use when creating a primary index which
   * will be removed later if it was added.
   * @return The default name used to create a primary index if it needed to be
   *         created.
   */
  public String getDefaultPrimaryName() {
    return primaryKeyName;
  }

  @Override
  public void establishDatabaseConnection() throws DatabaseDiffernceCheckerException {
    String connString = "couchbase://" + host + "/" + bucketName + "?operation_timeout=5.5&config_total_timeout=15&http_poolsize=0";
    try {
      Cluster cluster = CouchbaseCluster.fromConnectionString(connString);;
      cluster.authenticate(username, password);
      bucket = cluster.openBucket(bucketName);
      // check to see if a primary index already exists
      testConnection();
    } catch (Exception cause) {
      DatabaseDiffernceCheckerException error;
      if (cause instanceof DatabaseDiffernceCheckerException) {
        error = (DatabaseDiffernceCheckerException) cause;
      } else {
        error = new DatabaseDiffernceCheckerException("There was an error connecting to the bucket named " + bucketName,
            cause);
      }
      throw error;
    }
  }

  @Override
  public void closeDatabaseConnection() {
    bucket.close();
  }

  /**
   * Gets and lists all documents that exist in the Couchbase bucket.
   * @param documents A list where all of the document names will be stored for
   *        fast lookup later.
   */
  public void getDocuments(HashMap<String, String> documents) {
    query = N1qlQuery.simple("SELECT META().id AS document FROM `" + bucketName + "`", params);
    // Perform a N1QL Query
    result = bucket.query(query);
    // Print each found Row
    String documentName;
    for (N1qlQueryRow row : result) {
      documentName = row.value().getString("document");
      documents.put(documentName, documentName);
    }
  }

  /**
   * Gets and lists all indices that exist in the Couchbase bucket.
   * @param indices A list where all of the index names and data that will be
   *        stored for fast lookup later.
   */
  public void getIndices(HashMap<String, CouchbaseIndex> indices) {
    query = N1qlQuery.simple("SELECT indexes FROM system:indexes WHERE keyspace_id = \"" + bucketName + "\"", params);
    result = bucket.query(query);
    IndexInfo index = null;
    String create;
    int size;
    for (N1qlQueryRow row : result) {
      index = new IndexInfo(row.value().getObject("indexes"));
      if (primaryAdded && index.name().equals(primaryKeyName)) { // skip the manually added index
        continue;
      }
      create = new IndexElement(index.name(), index.isPrimary()).export() + " ON `" + bucketPlaceHolder + "`";
      size = index.indexKey().size();
      if (size != 0) {
        create += " (";
        for (int i = 0; i < size; i++) {
          create += index.indexKey().getString(i);
        }
        create += ")";
      }
      if (index.condition().length() > 0) {
        create += " WHERE" + index.condition();
      }
      // only add using statement if the key is not a primary index
      if (!index.isPrimary()) {
        create += " USING " + index.type();
      }
      indices.put(index.name(), new CouchbaseIndex(index.name(), create));
    }
  }

  /**
   * Takes in a N1QL statement and applies it to the bucket.
   * @param n1qlStatement A N1QL statement to be run on the bucket.
   */
  public void runStatement(String n1qlStatement) {
    if (n1qlStatement.startsWith("Create document: ")) {
      System.out.println("Creating: " + n1qlStatement.substring(n1qlStatement.indexOf(": ") + 2));
      JsonDocument document = JsonDocument.create(n1qlStatement.substring(n1qlStatement.indexOf(": ") + 2),
          JsonObject.empty());
      bucket.insert(document);
    } else if (n1qlStatement.startsWith("Drop document: ")) {
      System.out.println("Dropping: " + n1qlStatement.substring(n1qlStatement.indexOf(": ") + 2));
      bucket.remove(n1qlStatement.substring(n1qlStatement.indexOf(": ") + 2));
    } else {
      System.out.println("Running: " + n1qlStatement);
      query = N1qlQuery.simple(n1qlStatement, params);
      bucket.query(query);
    }
  }

  /**
   * Tests to see if the bucket can be queried immediately or if a primary key
   * needs to be added first. It will add a primary key if it is needed.
   * @throws DatabaseDiffernceCheckerException Error trying to connect to the
   *         bucket.
   */
  @Override
  public void testConnection() throws DatabaseDiffernceCheckerException {
    try {
      query = N1qlQuery.simple("SELECT META().id AS document FROM `" + bucketName + "`", params);
      // Perform a N1QL Query
      result = bucket.query(query);
    } catch (Exception error) {
      String errorMsg = error.getCause().toString();
      if (errorMsg.contains("4000") && errorMsg.contains("CREATE INDEX")) {
        // create a primary key with the
        query = N1qlQuery.simple("CREATE PRIMARY INDEX " + primaryKeyName + " ON `" + bucketName + "`", params);
        bucket.query(query);
        primaryAdded = true;
      } else {
        throw new DatabaseDiffernceCheckerException(
            "There was an error testing the connection to the bucket named " + bucketName, error);
      }
    }
  }
}
