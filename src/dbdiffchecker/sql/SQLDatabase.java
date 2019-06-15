package dbdiffchecker.sql;

import dbdiffchecker.Database;
import dbdiffchecker.DatabaseDifferenceCheckerException;
import dbdiffchecker.DbConn;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Models an SQL database schema.
 * @author Peter Kaufman
 * @version 6-15-19
 * @since 9-18-17
 */
public class SQLDatabase extends Database {
  // Instance variables
  private final static String foreignKeysOn[] = { "SET FOREIGN_KEY_CHECKS=0;", "PRAGMA foreign_keys=on;" };
  private final static String foreignKeysOff[] = { "SET FOREIGN_KEY_CHECKS=1;", "PRAGMA foreign_keys=off;" };
  private HashMap<String, Table> tables = new HashMap<>();
  private HashMap<String, String> exclude = new HashMap<>();
  private ArrayList<View> views = new ArrayList<>();
  private ArrayList<String> firstSteps = new ArrayList<>();
  private int type;

  /**
   * Uses a database connection to initialize a HashMap of tables and views that
   * exist in the database provided. <b>Note: It will also SQL statements to drop
   * all Primary Keys and remove all auot_increments in the database provided
   * which will only be used on the live database</b>
   * @author Peter Kaufman
   * @param db The database connection used to get the database information
   * @param type The type of datbase implimentation which is being used. <b>Note:
   *        this allows for the appropriate turning off of Foreign Keys</b>
   * @throws DatabaseDifferenceCheckerException Error connecting or closing the
   *         database connection.
   */
  public SQLDatabase(DbConn db, int type) throws DatabaseDifferenceCheckerException {
    // get tables and views
    db.establishDatabaseConnection();
    this.views = ((SQLDbConn) db).getViews();
    this.tables = ((SQLDbConn) db).getTableList();
    db.closeDatabaseConnection();
    // get SQL statements to drop all Primary Keys and remove all auot_increments
    this.firstSteps = ((SQLDbConn) db).getFirstSteps();
    // make sure that the type is valid
    if (type >= foreignKeysOn.length) {
      throw new DatabaseDifferenceCheckerException("Unable to determine the database implimentation being used.",
          new Exception(), 1019);
    }
    this.type = type;
  }

  /**
   * This is the default constructor for this class, <b>Needed for
   * Serialization</b>.
   */
  public SQLDatabase() {}

  /**
   * Returns the first steps to be taken in order to run the SQL statements. These
   * SQL statements are used to drop Primary Keys and remove auto_increments on
   * the database provided. <b>Note: this function will return an empty ArrayList
   * if the function is called for the development database</b>
   * @author Peter Kaufman
   * @return The first steps to be taken in order to run the SQL statements.
   */
  public ArrayList<String> getFirstSteps() {
    checkFirstSteps();
    return this.firstSteps;
  }

  /**
   * Returns the list of tables in the database provided. The key is the name of
   * the table.
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

  @Override
  public ArrayList<String> compare(Database liveDatabase) {
    ArrayList<String> sql = new ArrayList<>();
    HashMap<String, String> updateTables = new HashMap<>();
    sql.addAll(this.compareTables(((SQLDatabase) liveDatabase).getTables()));
    updateTables.putAll(this.tablesDiffs(((SQLDatabase) liveDatabase).getTables(), ((SQLDatabase) liveDatabase)));
    sql.addAll(0, ((SQLDatabase) liveDatabase).getFirstSteps());
    sql.addAll(this.updateTables(((SQLDatabase) liveDatabase).getTables(), updateTables));
    sql.addAll(this.getFirstSteps());
    sql.addAll(this.updateViews(((SQLDatabase) liveDatabase).getViews()));
    if (!sql.isEmpty()) {
      sql.add(0, foreignKeysOn[type]);
      sql.add(foreignKeysOff[type]);
    }
    return sql;
  }

  /**
   * Takes in a list of views and returns the SQL statements needed to make the
   * two databases have the exact same views. <b>Note: all views in the live
   * database will be dropped and all from the dev database will be created</b>
   * @author Peter Kaufman
   * @param liveViews All of the views in the live database.
   * @return The SQL statements to run in order to make the live database have the
   *         same views as the dev one.
   */
  public ArrayList<String> updateViews(ArrayList<View> liveViews) {
    ArrayList<String> sql = new ArrayList<>();
    // drop all views
    for (View liveView : liveViews) {
      sql.add(liveView.getDrop());
    }
    // add all views
    for (View devView : this.views) {
      sql.add(devView.getCreateStatement());
    }
    return sql;
  }

  /**
   * Determines which tables are to be created and which are to be dropped.
   * @author Peter Kaufman
   * @param liveTables All tables in the live database where the key is the name
   *        of the table.
   * @return The SQL statements to run in order to remove and/or create tables in
   *         the live database.
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
        sql.add(liveTables.get(tableName).getDrop());
        this.exclude.put(tableName, tableName);
      }
    }
    return sql;
  }

  /**
   * Takes in two table lists and returns the SQL statements to run in order to
   * make the live database have the same table definitions as the dev one.
   * @author Peter Kaufman
   * @param live All tables in the live database where the key is the name of the
   *        table.
   * @param updateTables All the tables which are not the same in the live and
   *        development databases.
   * @return The SQL statements to run in order to make the live database have the
   *         same table definitions as the dev one.
   */
  public ArrayList<String> updateTables(HashMap<String, Table> live, HashMap<String, String> updateTables) {
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
   * Compares the tables in the live and dev databases, and returns a list of
   * table names whose structure did not match between the development and live
   * databases.
   * @author Peter Kaufman
   * @param liveTables All tables in the live database where the key is the name
   *        of the table.
   * @param liveDatabase The live database connection which allows the live
   *        exclusions to be updated here to prevent unnecessary Primary Key
   *        dropping.
   * @return The tables that are to be updated because their structures did not
   *         match between the development and live databases.
   */
  public HashMap<String, String> tablesDiffs(HashMap<String, Table> liveTables, SQLDatabase liveDatabase) {
    HashMap<String, String> updateTables = new HashMap<>();
    for (String tableName : this.tables.keySet()) {
      if (!this.exclude.containsKey(tableName)
          && !this.tables.get(tableName).getCreateStatement().equals(liveTables.get(tableName).getCreateStatement())) {
        updateTables.put(tableName, tableName);
      } else { // all tables that do not need to be updated are to be removed from firstSteps
        liveDatabase.exclude.put(tableName, tableName);
      }
    }
    return updateTables;
  }

  /**
   * Removes any of the SQL statements in fistSteps that affect any tables in the
   * exclusion list.
   * @author Peter Kaufman
   */
  private void checkFirstSteps() {
    for (String table : exclude.keySet()) {
      for (int i = 0; i < firstSteps.size(); i++) {
        if (firstSteps.get(i).contains("ALTER TABLE `" + table + "`")) {
          firstSteps.remove(i);
          break;
        }
      }
    }
  }
}
