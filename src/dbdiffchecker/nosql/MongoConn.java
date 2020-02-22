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
 * Establishes a connection with a Mongo database based on the password,
 * username, host, port, and database name provided.
 * @author Peter Kaufman
 * @version 1-6-20
 * @since 10-26-19
 */
public class MongoConn extends DbConn {
  // Instance variables
  private String name;
  private MongoClientURI uri;
  private MongoDatabase database;
  private MongoClient mongo;

  /**
   * Fills in the parts of the uri connections string using the username,
   * password, host, and port provided by the user.
   * @author Peter Kaufman
   * @param username The username of the Mongo account.
   * @param password The password of the Mongo account.
   * @param host The host of the Mongo database.
   * @param port The port of the host where the Mongo database is.
   * @param name The database in Mongo that the connection is to be
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
            cause, 1025);
      }
      throw error;
    }
  }

  @Override
  public void closeDatabaseConnection() {
    mongo.close();
  }

  /**
   * Gets and lists all of the collections that exist in the Mongo database.
   * @author Peter Kaufman
   * @param collections A list of all the collections in the Mongo database.
   */
  public void getCollections(HashMap<String, Collection> collections) {
    MongoIterable <String> collectionList = database.listCollectionNames();
    boolean isCapped = false;
    int size = 0;
    for (String collectionName: collectionList) {
      Document collStats = database.runCommand(new Document("collStats", collectionName));
      isCapped = collStats.get("capped").toString().equals("true");
      if (isCapped) {
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

  @Override
  public void testConnection() throws DatabaseDifferenceCheckerException {
    try {
      mongo = new MongoClient(uri);   
      database = mongo.getDatabase(name);
    } catch (Exception error) {
      throw new DatabaseDifferenceCheckerException(
            "There was an error testing the connection to the database named " + name, error, 1025);
    } finally {
      mongo.close();
    } 
  }
}