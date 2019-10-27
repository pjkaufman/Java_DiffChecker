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
  private HashMap<String, Collection> collections = new HashMap<>();
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
  public HashMap<String, Collection> getCollections() {
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
    ArrayList<String> statements = new ArrayList<>(), updateCollections, 
      common = new ArrayList<>();
    String liveDatabaseName = live.name;
    // check for collections to create and drop
    statements.addAll(compareCollections(live.getCollections(), common));
    // determine which collections need to be updated
    updateCollections = collectionDiffs(common, live.getCollections());
    // generate the statements needed to modify the collections
    statements.addAll(updateCollections(live.getCollections(), updateCollections));
    // check to see if any indices need to be dropped or modified
    // TODO
    // check to see if any indices need to be added or modified
    // TODO
    return statements;
  }

  public ArrayList<String> compareCollections(HashMap<String, Collection> liveCollections, ArrayList<String> common) {
    ArrayList<String> statements = new ArrayList<>();
    // check for collections to create
    for(String collectionName : liveCollections.keySet()) {
      if (!collections.containsKey(collectionName)) {
        statements.add("Create Collection: " + collectionName);
      }
    }
    // check for collections to drop
    for(String collectionName : collections.keySet()) {
      if (!liveCollections.containsKey(collectionName)) {
        statements.add("Delete Collection: " + collectionName);
      } else {
        common.add(collectionName);
      }
    } 
    return statements;
  }

  public ArrayList<String> collectionDiffs(ArrayList<String> common, 
          HashMap<String, Collection> liveColls) {
    ArrayList<String> updateCollections = new ArrayList<>();
    // make sure that all common collections are the same, but if not make sure to 
    // add them to the list of collections to update
    for(String collectionName : common) {
      if (!collections.get(collectionName).equals(liveColls.get(collectionName))) {
        updateCollections.add(collectionName);
      }
    }
    return updateCollections;
  }

  public ArrayList<String> updateCollections(HashMap<String, Collection> liveColls, 
          ArrayList<String> collectionsToUpdate) {
    // TODO
    return new ArrayList<String>();
  }
}
