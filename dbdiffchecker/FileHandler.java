package dbdiffchecker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

/**
 * FileHandler deals with all data coming from and going out to files.
 * @author Peter Kaufman
 * @version 8-6-18
 * @since 9-12-17S
 */
public class FileHandler {

  // static variables
  static final String logFileName = "activity.log"; 
  static final String lastSequelStatementFileName = "lastRun.txt";
  static final String databaseSnapshotFileName = "dbsnapshot";

  /**
   * Serializes a Database object
   * @author Peter Kaufman
   * @param database The database to serialize.
   * @throws DatabaseDiffernceCheckerException Error serializing the database.
   */
  public static void serializeDatabase(Database database) throws DatabaseDiffernceCheckerException {
    try {
      FileOutputStream fileOutput = new FileOutputStream(
          new File("logs\\" + databaseSnapshotFileName));
      ObjectOutputStream outputStream = new ObjectOutputStream(fileOutput);

      // Write object to file
      outputStream.writeObject(database);

      outputStream.close();
      fileOutput.close();
    } catch (Exception cause) {
      String errorMessage = null;
      if (cause instanceof IOException) {
        errorMessage = "There was an error when trying to take a snapshot of the database.";
      } else {
        errorMessage = cause.getMessage().substring(cause.getMessage().indexOf(":") + 1);
      }
      throw new DatabaseDiffernceCheckerException(errorMessage, cause);
    }
  }


  /**
   * Takes SQL statements and writes them to the last run file.
   * @author Peter Kaufman
   * @param sequelStatements SQL statement(s).
   * @throws IOException Error writing the SQL statements to the last run file.
   */
  public static void writeToFile(ArrayList<String> sequelStatements) throws IOException {

    PrintWriter out = new PrintWriter(new FileWriter(
        new File("logs\\" + lastSequelStatementFileName)));
    for (String statement: sequelStatements) {

      out.println(statement);
    }
    out.close();
  }

  /**
   * Takes a String and writes it to the specified file.
   * @author Peter Kaufman
   * @param data To be written to the specified file.
   * @throws IOException Error writing the data to the specified file.
   */
  public static void writeToFile(String data) throws IOException {

    Date currentTime = new Date();
    PrintWriter out = new PrintWriter(new FileWriter(new File("logs\\" + logFileName), true));
    out.println(currentTime.toString() + " " + data);
    out.close();
  }

  /**
   * Deserializes the database file.
   * @author Peter Kaufman
   * @return The Database object created throug deserialization.
   * @throws DatabaseDiffernceCheckerException Error deserializing the database file.
   */
  public static Database deserailizDatabase() throws DatabaseDiffernceCheckerException {

    Database database = null;
    try {
      FileInputStream fileInput = new FileInputStream(
          new File("logs\\" + databaseSnapshotFileName));
      ObjectInputStream inputStream = new ObjectInputStream(fileInput);

      // Read object
      database = (Database) inputStream.readObject();

      inputStream.close();
      fileInput.close();

    } catch (Exception cause) {
      String errorMessage = null;
      if (cause instanceof IOException) {
        errorMessage = "There was an error when trying to take a get the database snapshot.";
      } else {
        errorMessage = cause.getMessage().substring(cause.getMessage()
            .indexOf(":") + 1);
      }
      throw new DatabaseDiffernceCheckerException(errorMessage, cause);
    } 

    return database;
  }

  /**
   * Returns the contents of the specified file.
   * @author Peter Kaufman
   * @param file The name of the file to be read from.
   * @return Data that was stored in the file that was read from.
   * @throws IOException Error reading in the data from the specified file.
   */
  public static ArrayList<String> readFrom(String file) throws IOException {

    Scanner in = new Scanner(new File("logs\\" + file));
    ArrayList<String> fileLines = new ArrayList<>();
    while (in.hasNextLine()) {

      fileLines.add(in.nextLine());
    }
    in.close();

    return fileLines;
  }

  /**
   * Ttakes a file path and determines whether the file exists or not.
   * @author Chris Dail
   * @param file The file path to the file.
   * @return Whether the file exists or not.
   * @see <a href="https://stackoverflow.com/questions/1816673/how-do-i-check-if-a-file-exists-in-java">https://stackoverflow.com/questions/1816673/how-do-i-check-if-a-file-exists-in-java</a>
   */
  public static boolean fileExists(String file) {

    return new File("logs\\" + file).isFile();
  }
}
