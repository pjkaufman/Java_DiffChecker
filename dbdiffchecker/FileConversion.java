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
 * FileConversion can convert a Database object into a JSON file and vice versa.
 * @author Peter Kaufman
 * @version 5-24-18
 * @since 9-12-17S
 */
public class FileConversion {

  // static variables
  static final String logFileName = "activity.log"; 
  static final String lastSequelStatementFileName = "lastRun.txt";
  static final String databaseSnapshotFileName = "dbsnapshot";

  /**
   * writeTo writes a Database object to a JSON file.
   * @author Peter Kaufman
   * @param obj is a Database object which is to be converted to a JSON file.
   * @throws DatabaseDiffernceCheckerException an error occurred while converting the Database object to a JSON file.
   */
  public static void writeToFile(Database obj) throws DatabaseDiffernceCheckerException {
    try {
      FileOutputStream f = new FileOutputStream(new File("logs\\" + databaseSnapshotFileName));
      ObjectOutputStream o = new ObjectOutputStream(f);

      // Write object to file
      o.writeObject(obj);

      o.close();
      f.close();
    } catch (Exception cause) {
      if (cause instanceof IOException) {
        throw new DatabaseDiffernceCheckerException("There was an error" 
            + " when trying to take a snapshot of the database.", cause);
      } else {
        throw new DatabaseDiffernceCheckerException(cause.getMessage().substring(cause.getMessage().indexOf( ":" ) + 1 ), cause);
      }
    }
  }


  /**
   * writeTo takes an ArrayList of Strings and writes them to LastRun.txt.
   * @author Peter Kaufman
   * @param SQL is an ArrayList of SQL statement(s).
   * @throws IOException an error occurred while writing the SQL statements to a LastRun.txt.
   */
  public static void writeToFile(ArrayList<String> SQL) throws IOException {

    PrintWriter out = new PrintWriter(new FileWriter(new File("logs\\" + lastSequelStatementFileName)));
    for (String statement: SQL) {

      out.println(statement);
    }
    out.close();
  }

  /**
   * writeTo takes a String and writes it to the specified file.
   * @author Peter Kaufman
   * @param data is a String which is to be written to the specified file.
   * @throws IOException an error occurred while writing the SQL statements to the file.
   */
  public static void writeToFile(String data) throws IOException {

    Date currentTime = new Date();
    PrintWriter out = new PrintWriter(new FileWriter(new File("logs\\" + logFileName), true));
    out.println(currentTime.toString() + " " + data);
    out.close();
  }

  /**
   * readFrom converts a JSON file into a Database object.
   * @author mkyong
   * @return database which is the Database object read in from the JSON file.
   * @throws IOException an error in converting the JSON file into a Database object.
   */
  public static Database readFrom() throws DatabaseDiffernceCheckerException {

    Database database = null;
    try {
      FileInputStream fi = new FileInputStream(new File("logs\\" + databaseSnapshotFileName));
      ObjectInputStream oi = new ObjectInputStream(fi);

      // Read object
      database = (Database) oi.readObject();

      oi.close();
      fi.close();

    } catch(Exception cause) {
      if (cause instanceof IOException) {
        throw new DatabaseDiffernceCheckerException("There was an error" 
            + " when trying to take a get the database snapshot.", cause);
      } else {
        throw new DatabaseDiffernceCheckerException(cause.getMessage().substring(cause.getMessage().indexOf( ":" ) + 1 ), cause);
      }
    } 

    return database;
  }

  /**
   * readFrom returns an ArrayList of Strings which were read from the specified file.
   * @author Peter Kaufman
   * @param file is a String which represents the name of the file to be written to
   * @return data is an ArrayList of data that was stored in the file that was read from.
   * @throws IOException an error occurred while reading in the data from the specified file.
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
   * fileExists takes a file path and determines whether the file exists or not.
   * @author Chris Dail
   * @param file is a String which is the file path.
   * @return is ethor true or false depending on whether the file exists or not.
   * @see <a href="https://stackoverflow.com/questions/1816673/how-do-i-check-if-a-file-exists-in-java">https://stackoverflow.com/questions/1816673/how-do-i-check-if-a-file-exists-in-java</a>
   */
  public static boolean fileExists(String file) {

    return new File("logs\\" + file).isFile();
  }
}
