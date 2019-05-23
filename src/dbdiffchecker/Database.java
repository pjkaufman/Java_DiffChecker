package dbdiffchecker;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Models an SQL database schema.
 * @author Peter Kaufman
 * @version 5-22-19
 * @since 9-18-17
 */
public class Database implements Serializable {
  // Instance variables
  private HashMap<String, Table> tables = new HashMap<>();
  private HashMap<String, String> exclude = new HashMap<>();
  private ArrayList<View> views = new ArrayList<>();
  private ArrayList<String> firstSteps = new ArrayList<>();

  /**
   * Uses a database connection to initialize a HashMap of tables 
   * and views that exist in the database provided. <b>Note: It will also 
   * SQL statements to drop all Primary Keys and remove all auot_increments 
   * in the database provided which will only be used on the live database</b>
   * @author Peter Kaufman
   * @param db The database connection used to get the database information
   * @throws DatabaseDiffernceCheckerException Error connecting or closing the database connection.
   */
  public Database(DbConn db) throws DatabaseDiffernceCheckerException {
    // get tables and views
    db.establishDatabaseConnection();
    this.views = db.getViews();
    this.tables = db.getTableList();
    db.closeDatabaseConnection();
    // get SQL statements to drop all Primary Keys and remove all auot_increments
    this.firstSteps = db.getFirstSteps();
  }

  /**
   * This is the default constructor for this class, <b>Needed for Serialization</b>.
   */
  public Database() {}

  /**
   * Returns the first steps to be taken in order to run the SQL statements.
   * These SQL statements are used to drop Primary Keys and remove auto_increments on the 
   * database provided. <b>Note: this function will return an empty ArrayList if the function
   * is called for the development database</b>
   * @author Peter Kaufman
   * @return The first steps to be taken in order to run the SQL statements
   */
  public ArrayList<String> getFirstSteps() {

    checkFirstSteps();
    return this.firstSteps;
  }

  /**
   * Returns the list of tables in the database provided. The key is the name 
   * of the table.
   * @author Peter Kaufman
   * @return A list of all the tables in the provided database.
   */
  public HashMap<String, Table> getTables() {

    return this.tables;
  }

  /**
   * Returns all of the views in the database.
   * @return All of the views in the database..
   */
  public ArrayList<View> getViews() {

    return this.views;
  }

  /**
   * Takes in a list of views and returns the SQL statements needed
   * to make the two databases have the exact same views. <b>Note: all 
   * views in the live database will be dropped and all from the dev database
   * will be created</b>
   * @author Peter Kaufman
   * @param liveViews All of the views in the live database.
   * @return The SQL statements to run in order to make the live database have
   * the same views as the dev one.
   */
  public ArrayList<String> updateViews(ArrayList<View> liveViews) {

    ArrayList<String> sql = new ArrayList<>();
    // drop all views
    for (View liveView: liveViews) {
      sql.add(liveView.getDrop());
    }
    // add all views
    for (View devView: this.views) {
      sql.add(devView.getCreateStatement());
    }

    return sql;
  }

  /**
   * Determines which tables are to be created or dropped.
   * @author Peter Kaufman
   * @param liveTables All tables in the live database where the key is the 
   * name of the table.
   * @return The SQL statements to run in order to remove and,or create tables
   * in the live database.
   */
  public ArrayList<String> compareTables(HashMap<String, Table> liveTables) {

    ArrayList<String> sql = new ArrayList<>();
    // get the create statement
    for (String tableName : this.tables.keySet()) {
      if (!liveTables.containsKey(tableName)) {

        sql.add(this.tables.get(tableName).getCreateStatement());
        this.exclude.put(tableName, tableName);
      }
    }
    // drop the table
    for (String tableName : liveTables.keySet()) {
      if (!this.tables.containsKey(tableName)) {

        sql.add("DROP TABLE `" + tableName + "`;");
        this.exclude.put(tableName, tableName);
      }
    }

    return sql;
  }

  /**
   * Takes in two table lists which are HashMaps, and returns 
   * the SQL statements to run in order to make the live database have the
   * same table definitions as the dev one.
   * @author Peter Kaufman
   * @param live is a HashMap of String and Table object pairs which are the names 
   * of the tables and table data for each table in the live database.
   * @param updateTables is a HashMap of String and String pairs where the key and
   * the value are the same, which is all the tables which are not the same in 
   * the live and dev databases.
   * @return An ArrayList of Strings which is the SQL statements to run in order to make
   * the live database have the same table definitions as the dev one.
   */
  public ArrayList<String> updateTables(HashMap<String, Table> live, 
          HashMap<String, String> updateTables) {

    ArrayList<String> sql = new ArrayList<>();
    // find the info that is differnet between the tables
    for (String tableName : updateTables.keySet()) {
      if (!exclude.containsKey(tableName)) {
        sql.addAll(this.tables.get(tableName).equals(live.get(tableName)));
      }
    }

    return sql;
  }

  /**
   * Compares the two HashMaps which represent the tables in the live
   * and dev databases, and returns a HashMap of table names whose structure did 
   * not match between the dev and live databases.
   * @author Peter Kaufman
   * @param liveTables is a HashMap of String and Table object pairs which are the 
   * table names and table data for all tables in the live database.
   * @param liveDatabase is the live database that is being connected to. It has 
   * its exclusions updated here to remove unnecessary Primary Key dropping.
   * @return A HashMap which is the tables that are to be updated because 
   * their structures did not match between the dev and live databases.
   */
  public HashMap<String, String> tablesDiffs(HashMap<String, Table> liveTables, Database liveDatabase) {
    HashMap<String, String> updateTables = new HashMap<>();

    for (String tableName : this.tables.keySet()) {
      if (!this.exclude.containsKey(tableName) && !this.tables.get(tableName).getCreateStatement()
          .equals(liveTables.get(tableName).getCreateStatement())) {
        updateTables.put(tableName, tableName);
       } else { // all tables that do not need to be updated are to be removed from firstSteps
          liveDatabase.exclude.put(tableName, tableName);
       }
    }

    return updateTables;
  }

  /**
   * Checks to see if any of the SQL statements in the fistSteps
   * ArrayList are also in the exclusion list. If it is, it is removed.
   * @author Peter Kaufman
   */
  private void checkFirstSteps() {
    for (String table: exclude.keySet()) {
      for (int i = 0; i < firstSteps.size(); i++) {
        if (firstSteps.get(i).contains("ALTER TABLE `" + table + "`")) {

          firstSteps.remove(i);
          break;
        }
      }
    }
  }
}
