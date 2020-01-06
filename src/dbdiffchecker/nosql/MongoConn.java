package dbdiffchecker.nosql;

import com.mongodb.client.MongoDatabase;
import com.mongodb.MongoClientURI;
import com.mongodb.client.model.CreateCollectionOptions;
import org.bson.Document;
import com.mongodb.MongoClient;  
import com.mongodb.client.MongoIterable;
import java.util.logging.Logger;
import java.util.logging.Level;
import dbdiffchecker.DatabaseDifferenceCheckerException;
import dbdiffchecker.DbConn;
import java.util.HashMap;

/**
 * Establishes a connection with a Couchbase bucket based on the password,
 * username, host, and bucket name provided.
 * @author Peter Kaufman
 * @version 10-26-19
 * @since 10-26-19
 */
public class MongoConn extends DbConn {
  // Instance variables
  private String name;
  private MongoClientURI uri;
  private MongoDatabase database;
  private MongoClient mongo;

  /**
   * Initializes the username, password, host and bucketName of the bucket
   * connection.
   * @author Peter Kaufman
   * @param username The username of the Couchbase account.
   * @param password The password of the Couchbase account.
   * @param host The host of the Couchbase bucket.
   * @param name The bucket in Couchbase that the connection is to be
   *        established with.
   */
  public MongoConn(String username, String password, String host, String port, String name) {
    uri = new MongoClientURI("mongodb://" + username + ":" + password + "@" + host + "/?authSource=admin");
    this.name = name;
    Logger mongoLogger = Logger.getLogger( "org.mongodb.driver" );
    mongoLogger.setLevel(Level.WARNING);
  }

  @Override
  public String getDatabaseName() {
    return name;
  }

  @Override
  public void establishDatabaseConnection() throws DatabaseDifferenceCheckerException {
    try {
      mongo = new MongoClient(uri);     
      database = mongo.getDatabase(name);
    } catch (Exception cause) {
      DatabaseDifferenceCheckerException error;
      if (cause instanceof DatabaseDifferenceCheckerException) {
        error = (DatabaseDifferenceCheckerException) cause;
      } else {
        error = new DatabaseDifferenceCheckerException("There was an error connecting to the database named " + name,
            cause, 4019);
      }
      throw error;
    }
  }

  @Override
  public void closeDatabaseConnection() {
    mongo.close();
  }

  /**
   * Gets and lists all documents that exist in the Couchbase bucket.
   * @author Peter Kaufman
   * @param documents A list where all of the document names will be stored for
   *        fast lookup later.
   */
  public void getCollections(HashMap<String, Collection> collections) {
    MongoIterable <String> collectionList = database.listCollectionNames();
    boolean isCapped = false;
    int size = 0;
    for (String collectionName: collectionList) {
      Document collStats = database.runCommand(new Document("collStats", collectionName));
      //System.out.println(collStats);
      isCapped = collStats.get("capped").toString().equals("true");
      if (isCapped) {
        System.out.println(collectionName + " is capped");
        size = Integer.parseInt(collStats.get("storageSize").toString());
      } else {
        size = 0;
      }
      collections.put(collectionName, new Collection(collectionName, isCapped, size));
    }
  }

  /**
   * Takes in a statement and applies it to the Mongo Database.
   * @author Peter Kaufman
   * @param statement A statement to be run on the Mongo Database.
   */
  public void runStatement(String statement) {
    // determine if a collection is being dropped or added
    String name;
    String[] options;
    int size;
    if (statement.startsWith("Create Collection: ")) {
        options = statement.split(",");
        if (options.length > 1) { // capped collection
          name = options[0].replace("Create Collection: ", "");
          size = Integer.parseInt(options[2].replace(" size=", ""));
          CreateCollectionOptions collOptions = new CreateCollectionOptions();
          collOptions.capped(true);
          collOptions.sizeInBytes((long)size);
          database.createCollection(name, collOptions);
        } else {
          name = statement.replace("Create Collection: ", "");
          database.createCollection(name);
        }
    } else { // collection is being dropped
       database.getCollection(statement.replace("Delete Collection: ", "")).drop();
    }
  }

  /**
   * Tests to see if the bucket can be queried immediately or if a primary key
   * needs to be added first. It will add a primary key if it is needed.
   * @author Peter Kaufman
   * @throws DatabaseDifferenceCheckerException Error trying to connect to the
   *         bucket.
   */
  @Override
  public void testConnection() throws DatabaseDifferenceCheckerException {
    try {
      mongo = new MongoClient(uri);   
      database = mongo.getDatabase(name);
    } catch (Exception error) {
      throw new DatabaseDifferenceCheckerException(
            "There was an error testing the connection to the database named " + name, error, 4010);
    } finally {
      mongo.close();
    } 
  }
}
