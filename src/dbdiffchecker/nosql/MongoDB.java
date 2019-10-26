package dbdiffchecker.nosql;

import dbdiffchecker.Database;
import dbdiffchecker.DatabaseDifferenceCheckerException;
import dbdiffchecker.DbConn;
import dbdiffchecker.sql.Index;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * Models a Couchbase bucket by keeping track of all indices and documents.
 * @author Peter Kaufman
 * @version 10-26-19
 * @since 10-26-19
 */
public class MongoDB extends Database {
  // Instance variables
  private HashMap<String, String> collections = new HashMap<>();
  private HashMap<String, Index> indices = new HashMap<>();
  private String name = "";

  /**
   * Creates a database that models the Couchbase bucket using the Couchbase
   * connection in orde to get a list of documents, idndices, and other pertinent
   * information.
   * @author Peter Kaufman
   * @param conn The connection to the Couchbase bucket.
   * @throws DatabaseDifferenceCheckerException Error connecting to the Couchbase
   *         bucket.
   */
  public MongoDB(DbConn conn) throws DatabaseDifferenceCheckerException {
    MongoConn connection = (MongoConn) conn;
    connection.establishDatabaseConnection();
    // check to see if a primary index already exists
    connection.testConnection();
    connection.getCollections(collections);
    connection.getIndices(indices);
    this.name = connection.getDatabaseName();
    connection.closeDatabaseConnection();
  }

  /**
   * This is the default constructor for this class, <b>Needed for
   * Serialization</b>.
   */
  public MongoDB() {}

  /**
   * Returns the list of Couchbase documents where the name is the key and value.
   * @author Peter Kaufman
   * @return The list of documents that exist in the bucket.
   */
  public HashMap<String, String> getCollections() {
    return this.collections;
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
  public ArrayList<String> compare(Database liveDatabase) {
    MongoDB live = (MongoDB) liveDatabase;
    ArrayList<String> statements = new ArrayList<>();
    String liveDatabaseName = live.name;
    // check for documents to create
    for (String collectionName : collections.keySet()) {
      if (!live.getCollections().containsKey(collectionName)) {
        statements.add("Create document: " + collectionName);
      }
    }
    // check for documents to drop
    for (String collectionName : live.getCollections().keySet()) {
      if (!collections.containsKey(collectionName)) {
        statements.add("Drop document: " + collectionName);
      }
    }
    // check to see if any indices need to be dropped or modified
    // TODO
    // check to see if any indices need to be added or modified
    // TODO
    return statements;
  }
}
