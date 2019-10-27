package dbdiffchecker.nosql;

import com.mongodb.client.MongoDatabase; 
import org.bson.Document;
import com.mongodb.MongoClient;  
import com.mongodb.client.MongoIterable;
import java.util.logging.Logger;
import java.util.logging.Level;
import dbdiffchecker.DatabaseDifferenceCheckerException;
import dbdiffchecker.DbConn;
import dbdiffchecker.sql.Index;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * Establishes a connection with a Couchbase bucket based on the password,
 * username, host, and bucket name provided.
 * @author Peter Kaufman
 * @version 10-26-19
 * @since 10-26-19
 */
public class MongoConn extends DbConn {
  // Instance variables
  private String username;
  private String password;
  private String name;
  private String host;
  private String port;
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
    this.username = username;
    this.password = password;
    this.host = host;
    this.port = port;
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
      mongo = new MongoClient(host , Integer.parseInt(host)); 
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
      isCapped = "1" == collStats.get("capped").toString();
      if (isCapped) {
        size = Integer.parseInt(collStats.get("storageSize").toString());
      } else {
        size = 0;
      }
      collections.put(collectionName, new Collection(collectionName, new HashMap<String, Index>(), isCapped, size));
    }
  }

  /**
   * Gets and lists all indices that exist in the Couchbase bucket.
   * @author Peter Kaufman
   * @param indices A list where all of the index names and data that will be
   *        stored for fast lookup later.
   */
  public void getIndices(HashMap<String, Index> indices) {
   // TODO
  }

  /**
   * Takes in a N1QL statement and applies it to the bucket.
   * @author Peter Kaufman
   * @param n1qlStatement A N1QL statement to be run on the bucket.
   */
  public void runStatement(String n1qlStatement) {
    // TODO
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
      mongo = new MongoClient(host , Integer.parseInt(host)); 
      database = mongo.getDatabase(name);
    } catch (Exception error) {
      String errorMsg = error.getCause().toString();
      throw new DatabaseDifferenceCheckerException(
            "There was an error testing the connection to the database named " + name, error, 4010);
    } finally {
      mongo.close();
    } 
  }
}
