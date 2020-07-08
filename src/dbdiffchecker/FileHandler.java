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
import java.util.List;
import java.util.Scanner;

/**
 * Deals with all data coming from and going out to files.
 *
 * @author Peter Kaufman and Chris Dail
 * @version 6-20-20
 * @since 9-12-17
 */
public class FileHandler {
  public static final String LOG_FILE = "activity.log";
  public static final String LAST_RUN_FILE = "lastRun.txt";
  public static final String DB_SNAPSHOT_FILE = "dbsnapshot";
  public static final String LOG_FOLDER = "log" + File.separator;

  /**
   * Makes sure that noone can try to instantiate the utility class.
   */
  private FileHandler() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Serializes a database with all of its table and view data.
   *
   * @param database The database to serialize.
   * @param prefix   The prefix of the database snapshot to deserailize. <b>Note:
   *                 it is the name of the database implimentation of the
   *                 database</b>
   * @throws DatabaseDifferenceCheckerException Error serializing the database.
   */
  public static void serializeDatabase(Database database, String prefix) throws DatabaseDifferenceCheckerException {
    try (
        FileOutputStream fileOutput = new FileOutputStream(
            new File(String.format("%s%s_%s", LOG_FOLDER, prefix, DB_SNAPSHOT_FILE)));
        ObjectOutputStream outputStream = new ObjectOutputStream(fileOutput)) {
      outputStream.writeObject(database);
    } catch (IOException cause) {
      throw new DatabaseDifferenceCheckerException("There was an error when trying to take a snapshot of the database.",
          cause, 1000);
    }
  }

  /**
   * Takes SQL statements and writes them to the last run file.
   *
   * @param sequelStatements Statements to be logged.
   * @throws DatabaseDifferenceCheckerException Error writing the statements to
   *                                            the last run file.
   */
  public static void writeToFile(List<String> sequelStatements) throws DatabaseDifferenceCheckerException {
    try (PrintWriter out = new PrintWriter(new FileWriter(new File(LOG_FOLDER + LAST_RUN_FILE)))) {
      for (String statement : sequelStatements) {
        out.println(statement);
      }
    } catch (IOException cause) {
      throw new DatabaseDifferenceCheckerException("There was an error writing the statements to the last run file",
          cause, 1001);
    }
  }

  /**
   * Takes a String and writes it to the log file.
   *
   * @param data The data to be written to the log file.
   * @throws DatabaseDifferenceCheckerException Error writing the data to the log
   *                                            file.
   */
  public static void writeToFile(String data) throws DatabaseDifferenceCheckerException {
    Date currentTime = new Date();
    try (PrintWriter out = new PrintWriter(new FileWriter(new File(LOG_FOLDER + LOG_FILE), true))) {
      out.println(currentTime.toString() + " " + data);
    } catch (IOException cause) {
      throw new DatabaseDifferenceCheckerException("There was an error writing the to the log file", cause, 1002);
    }
  }

  /**
   * Deserializes a database file.
   *
   * @param prefix The prefix of the database snapshot to deserailize. <b>Note: it
   *               is the name of the database implimentation of the database</b>
   * @return The database created through deserialization with table and view
   *         data.
   * @throws DatabaseDifferenceCheckerException Error deserializing the database
   *                                            file.
   */
  public static Database deserailizDatabase(String prefix) throws DatabaseDifferenceCheckerException {
    Database database = null;
    try (FileInputStream fileInput = new FileInputStream(new File(LOG_FOLDER + prefix + "_" + DB_SNAPSHOT_FILE));
        ObjectInputStream inputStream = new ObjectInputStream(fileInput)) {
      database = (Database) inputStream.readObject();
    } catch (Exception cause) {
      throw new DatabaseDifferenceCheckerException("There was an error when trying to get the database snapshot.",
          cause, 1003);
    }
    return database;
  }

  /**
   * Returns the contents of the specified file.
   *
   * @param file The name of the file to be read from.
   * @return Data that was stored in the file that was read from.
   * @throws DatabaseDifferenceCheckerException Error reading in the data from the
   *                                            specified file.
   */
  public static List<String> readFrom(String file) throws DatabaseDifferenceCheckerException {
    List<String> fileLines = new ArrayList<>();
    try (Scanner in = new Scanner(new File(LOG_FOLDER + file))) {
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
   *
   * @param file The file path to the file.
   * @return Whether the file exists or not.
   * @see <a href=
   *      "https://stackoverflow.com/questions/1816673/how-do-i-check-if-a-file-exists-in-java">https://stackoverflow.com/questions/1816673/how-do-i-check-if-a-file-exists-in-java</a>
   */
  public static boolean fileExists(String file) {
    return new File(LOG_FOLDER + file).isFile();
  }
}
