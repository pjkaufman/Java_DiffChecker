package dbdiffchecker;

import java.io.File;
import java.io.FileInputStream;
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
 * Deals with all data coming from and going out to files.
 * @author Peter Kaufman and Chris Dail
 * @version 6-15-19
 * @since 9-12-17
 */
public class FileHandler {
  // static variables
  public static final String logFileName = "activity.log";
  public static final String lastSequelStatementFileName = "lastRun.txt";
  public static final String databaseSnapshotFileName = "dbsnapshot";
  public static final String logFolder = "logs";

  /**
   * Serializes a database with all of its table and view data.
   * @author Peter Kaufman
   * @param database The database to serialize.
   * @param prefix The prefix of the database snapshot to deserailize. <b>Note: it
   *        is the name of the database implimentation of the database</b>
   * @throws DatabaseDifferenceCheckerException Error serializing the database.
   */
  public static void serializeDatabase(Database database, String prefix) throws DatabaseDifferenceCheckerException {
    try (
        FileOutputStream fileOutput = new FileOutputStream(
            new File(logFolder + File.separator + prefix + "_" + databaseSnapshotFileName));
        ObjectOutputStream outputStream = new ObjectOutputStream(fileOutput);) {
      // Write object to file
      outputStream.writeObject(database);
      outputStream.close();
      fileOutput.close();
    } catch (IOException cause) {
      throw new DatabaseDifferenceCheckerException("There was an error when trying to take a snapshot of the database.",
          cause, 1000);
    }
  }

  /**
   * Takes SQL statements and writes them to the last run file.
   * @author Peter Kaufman
   * @param sequelStatements Statements to be logged.
   * @throws DatabaseDifferenceCheckerException Error writing the statements to the
   *         last run file.
   */
  public static void writeToFile(ArrayList<String> sequelStatements) throws DatabaseDifferenceCheckerException {
    try (PrintWriter out = new PrintWriter(
        new FileWriter(new File(logFolder + File.separator + lastSequelStatementFileName)));) {
      for (String statement : sequelStatements) {
        out.println(statement);
      }
    } catch (IOException cause) {
      throw new DatabaseDifferenceCheckerException(
          "There was an error writing the statements to the last run file", cause, 1001);
    }
  }

  /**
   * Takes a String and writes it to the log file.
   * @author Peter Kaufman
   * @param data The data to be written to the log file.
   * @throws DatabaseDifferenceCheckerException Error writing the data to
   *         the log file.
   */
  public static void writeToFile(String data) throws DatabaseDifferenceCheckerException {
    Date currentTime = new Date();
    try (PrintWriter out = new PrintWriter(new FileWriter(new File(logFolder + File.separator + logFileName), true));) {
      out.println(currentTime.toString() + " " + data);
    } catch (IOException cause) {
      throw new DatabaseDifferenceCheckerException("There was an error writing the to the log file", cause,
          1002);
    }
  }

  /**
   * Deserializes a database file.
   * @author Peter Kaufman
   * @param prefix The prefix of the database snapshot to deserailize. <b>Note: it
   *        is the name of the database implimentation of the database</b>
   * @return The database created through deserialization with table and view
   *         data.
   * @throws DatabaseDifferenceCheckerException Error deserializing the database
   *         file.
   */
  public static Database deserailizDatabase(String prefix) throws DatabaseDifferenceCheckerException {
    Database database = null;
    try (
        FileInputStream fileInput = new FileInputStream(
            new File(logFolder + File.separator + prefix + "_" + databaseSnapshotFileName));
        ObjectInputStream inputStream = new ObjectInputStream(fileInput);) {
      // Read object
      database = (Database) inputStream.readObject();
    } catch (Exception cause) {
      throw new DatabaseDifferenceCheckerException("There was an error when trying to get the database snapshot.", cause,
          1003);
    } finally {}
    return database;
  }

  /**
   * Returns the contents of the specified file.
   * @author Peter Kaufman
   * @param file The name of the file to be read from.
   * @return Data that was stored in the file that was read from.
   * @throws DatabaseDifferenceCheckerException Error reading in the data
   *         from the specified file.
   */
  public static ArrayList<String> readFrom(String file) throws DatabaseDifferenceCheckerException {
    ArrayList<String> fileLines = new ArrayList<>();
    try ( Scanner in = new Scanner(new File(logFolder + File.separator + file))) {
      while (in.hasNextLine()) {
        fileLines.add(in.nextLine());
      }
    } catch (IOException cause) {
      throw new DatabaseDifferenceCheckerException("There was an error when trying to read from the specified file.",
          cause, 1004);
    }
    return fileLines;
  }

  /**
   * Takes a file path and determines whether the file exists or not.
   * @author Chris Dail
   * @param file The file path to the file.
   * @return Whether the file exists or not.
   * @see <a href=
   *      "https://stackoverflow.com/questions/1816673/how-do-i-check-if-a-file-exists-in-java">https://stackoverflow.com/questions/1816673/how-do-i-check-if-a-file-exists-in-java</a>
   */
  public static boolean fileExists(String file) {
    return new File(logFolder + File.separator + file).isFile();
  }
}
