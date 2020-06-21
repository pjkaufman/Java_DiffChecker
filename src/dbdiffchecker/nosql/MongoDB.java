package dbdiffchecker.nosql;

import dbdiffchecker.Database;
import dbdiffchecker.DatabaseDifferenceCheckerException;
import dbdiffchecker.DbConn;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * Models a Mongo Database by keeping track of all collections.
 *
 * @author Peter Kaufman
 * @version 6-20-20
 * @since 10-26-19
 */
public class MongoDB extends Database {
  private HashMap<String, Collection> collections = new HashMap<>();

  /**
   * Creates a database that models the Mongo database using the Mongo connection
   * in order to get a list of collections.
   *
   * @param conn The connection to the Mongo database.
   * @throws DatabaseDifferenceCheckerException Error connecting to the Mongo
   *                                            database.
   */
  public MongoDB(DbConn conn) throws DatabaseDifferenceCheckerException {
    MongoConn connection = (MongoConn) conn;
    connection.testConnection();
    connection.establishDatabaseConnection();
    connection.getCollections(collections);
    connection.closeDatabaseConnection();
  }

  /**
   * This is the default constructor for this class, <b>Needed for
   * Serialization</b>.
   */
  public MongoDB() {
  }

  /**
   * Returns the list of MongoDB collections where the name is the key and value.
   *
   * @return The list of documents that exist in the bucket.
   */
  public HashMap<String, Collection> getCollections() {
    return this.collections;
  }

  @Override
  public ArrayList<String> compare(Database liveDatabase) {
    MongoDB live = (MongoDB) liveDatabase;
    ArrayList<String> statements = new ArrayList<>(), updateCollections, common = new ArrayList<>();
    // check for collections to create and drop
    statements.addAll(compareCollections(live.getCollections(), common));
    // determine which collections need to be updated
    updateCollections = collectionDiffs(common, live.getCollections());
    // generate the statements needed to modify the collections
    statements.addAll(updateCollections(updateCollections));
    return statements;
  }

  /**
   * Determines which collections are in the live and dev database along with
   * those that need to be created and deleted.
   *
   * @param liveCollections The collections that exist in the live database.
   * @param common          The collections which are common between the live and
   *                        dev databases.
   * @return A set of statmentst that have to do with dropping and or creating
   *         collections.
   */
  private ArrayList<String> compareCollections(HashMap<String, Collection> liveCollections, ArrayList<String> common) {
    ArrayList<String> statements = new ArrayList<>();
    // check for collections to create
    String createStatement;
    for (String collectionName : collections.keySet()) {
      if (!liveCollections.containsKey(collectionName)) {
        createStatement = "Create Collection: " + collectionName;
        if (collections.get(collectionName).isCapped()) {
          createStatement += ", capped=true, size=" + collections.get(collectionName).getSize();
        }
        statements.add(createStatement);
      } else {
        common.add(collectionName);
      }
    }
    // check for collections to drop
    for (String collectionName : liveCollections.keySet()) {
      if (!collections.containsKey(collectionName)) {
        statements.add("Delete Collection: " + collectionName);
      }
    }
    return statements;
  }

  /**
   * Determines which collections out of the common ones have differences.
   *
   * @param common    The list of common collections between dev and live.
   * @param liveColls The collection list from the live database.
   * @return The list of collections that need to be updated.
   */
  private ArrayList<String> collectionDiffs(ArrayList<String> common, HashMap<String, Collection> liveColls) {
    ArrayList<String> updateCollections = new ArrayList<>();
    for (String collectionName : common) {
      if (!collections.get(collectionName).equals(liveColls.get(collectionName))) {
        updateCollections.add(collectionName);
      }
    }
    return updateCollections;
  }

  /**
   * Determines what needs to be done to each collection that needs to be updated.
   *
   * @param collectionsToUpdate The list of collections that need to be updated.
   * @return The statements needed to make the collections from the collections to
   *         updated the same.
   */
  private ArrayList<String> updateCollections(ArrayList<String> collectionsToUpdate) {
    ArrayList<String> statements = new ArrayList<>();
    String createStatement;
    for (String collectionName : collectionsToUpdate) {
      statements.add("Delete Collection: " + collectionName);
      createStatement = "Create Collection: " + collectionName;
      if (collections.get(collectionName).isCapped()) {
        createStatement += ", capped=true, size=" + collections.get(collectionName).getSize();
      }
      statements.add(createStatement);
    }
    return statements;
  }
}
