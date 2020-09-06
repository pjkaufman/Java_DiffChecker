package dbdiffchecker.nosql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dbdiffchecker.Database;
import dbdiffchecker.DatabaseDifferenceCheckerException;
import dbdiffchecker.DbConn;

/**
 * Models a Mongo Database by keeping track of all collections.
 *
 * @author Peter Kaufman
 */
public class MongoDB extends Database {
  private static final long serialVersionUID = 1L;
  private Map<String, Collection> collections = new HashMap<>();
  protected static final String CREATE_COLL_IDENTIFIER = "Create Collection: ";
  protected static final String DELETE_COLL_IDENTIFIER = "Delete Collection: ";

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
   * <b>Needed for Serialization</b>
   */
  public MongoDB() {
  }

  /**
   * Returns the list of MongoDB collections where the name is the key and value.
   *
   * @return The list of documents that exist in the bucket.
   */
  public Map<String, Collection> getCollections() {
    return collections;
  }

  @Override
  public List<String> compare(Database liveDatabase) {
    MongoDB live = (MongoDB) liveDatabase;
    List<String> statements = new ArrayList<>();
    List<String> common = new ArrayList<>();
    List<String> collectionsToUpdate;
    statements.addAll(compareCollections(live.getCollections(), common)); // check for collections to create and drop
    collectionsToUpdate = collectionDiffs(common, live.getCollections());
    statements.addAll(updateCollections(collectionsToUpdate));
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
  private List<String> compareCollections(Map<String, Collection> liveCollections, List<String> common) {
    List<String> statements = new ArrayList<>();

    StringBuilder createStatement;
    for (Map.Entry<String, Collection> coll : collections.entrySet()) {
      if (!liveCollections.containsKey(coll.getKey())) {
        createStatement = new StringBuilder(CREATE_COLL_IDENTIFIER + coll.getKey());
        if (coll.getValue().isCapped()) {
          createStatement.append(", capped=true, size=" + coll.getValue().getSize());
        }
        statements.add(createStatement.toString());
      } else {
        common.add(coll.getKey());
      }
    }

    for (String collectionName : liveCollections.keySet()) {
      if (!collections.containsKey(collectionName)) {
        statements.add(DELETE_COLL_IDENTIFIER + collectionName);
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
  private List<String> collectionDiffs(List<String> common, Map<String, Collection> liveColls) {
    List<String> updateCollections = new ArrayList<>();
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
  private List<String> updateCollections(List<String> collectionsToUpdate) {
    List<String> statements = new ArrayList<>();
    StringBuilder createStatement;
    for (String collectionName : collectionsToUpdate) {
      statements.add(DELETE_COLL_IDENTIFIER + collectionName);
      createStatement = new StringBuilder(CREATE_COLL_IDENTIFIER + collectionName);
      if (collections.get(collectionName).isCapped()) {
        createStatement.append(", capped=true, size=" + collections.get(collectionName).getSize());
      }
      statements.add(createStatement.toString());
    }
    return statements;
  }
}
