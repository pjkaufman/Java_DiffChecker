package dbdiffchecker.sql;

import dbdiffchecker.Database;
import dbdiffchecker.DatabaseDifferenceCheckerException;
import dbdiffchecker.DbConn;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Models an SQL database schema.
 *
 * @author Peter Kaufman
 * @version 7-9-20
 * @since 9-18-17
 */
public class SQLDatabase extends Database {
  private static final long serialVersionUID = 1L;
  private static final String[] foreignKeysOn = { "SET FOREIGN_KEY_CHECKS=0;", "PRAGMA foreign_keys=on;" };
  private static final String[] foreignKeysOff = { "SET FOREIGN_KEY_CHECKS=1;", "PRAGMA foreign_keys=off;" };
  private Map<String, Table> tables = new HashMap<>();
  private Map<String, String> exclude = new HashMap<>();
  private List<View> views = new ArrayList<>();
  private List<String> firstSteps = new ArrayList<>();
  private int type;

  /**
   * Uses a database connection to initialize a HashMap of tables and views that
   * exist in the database provided. <b>Note: It will also generate SQL statements
   * to drop all Primary Keys and remove all auot_increments in the database
   * provided which will only be used on the live database</b>
   *
   * @param db   The database connection used to get the database information
   * @param type The type of datbase implimentation which is being used. <b>Note:
   *             this allows for the appropriate turning off of Foreign Keys</b>
   * @throws DatabaseDifferenceCheckerException Error connecting or closing the
   *                                            database connection.
   */
  public SQLDatabase(DbConn db, int type) throws DatabaseDifferenceCheckerException {
    db.establishDatabaseConnection();
    views = ((SQLDbConn) db).getViews();
    tables = ((SQLDbConn) db).getTableList();
    db.closeDatabaseConnection();
    // get SQL statements to drop all Primary Keys and remove all auot_increments
    firstSteps = ((SQLDbConn) db).getFirstSteps();
    // make sure that the type is valid
    if (type >= foreignKeysOn.length) {
      throw new DatabaseDifferenceCheckerException("Unable to determine the database implimentation being used.",
          new Exception(), 1019);
    }
    this.type = type;
  }

  /**
   * <b>Needed for Serialization</b>.
   */
  public SQLDatabase() {
  }

  /**
   * Returns the first steps to be taken in order to run the SQL statements. These
   * SQL statements are used to drop Primary Keys and remove auto_increments on
   * the database provided.
   *
   * @return The first steps to be taken in order to run the SQL statements.
   */
  public List<String> getFirstSteps() {
    checkFirstSteps();
    return firstSteps;
  }

  /**
   * Returns the list of tables in the database provided. The key is the name of
   * the table.
   *
   * @return A list of all the tables in the provided database.
   */
  public Map<String, Table> getTables() {
    return tables;
  }

  /**
   * Returns all of the views in the database.
   *
   * @return All of the views in the database.
   */
  public List<View> getViews() {
    return views;
  }

  @Override
  public List<String> compare(Database liveDatabase) {
    List<String> sql = new ArrayList<>();
    HashMap<String, String> updateTables = new HashMap<>();
    sql.addAll(compareTables(((SQLDatabase) liveDatabase).getTables()));
    updateTables.putAll(tablesDiffs(((SQLDatabase) liveDatabase).getTables(), ((SQLDatabase) liveDatabase)));
    sql.addAll(0, ((SQLDatabase) liveDatabase).getFirstSteps());
    sql.addAll(updateTables(((SQLDatabase) liveDatabase).getTables(), updateTables));
    sql.addAll(getFirstSteps());
    sql.addAll(updateViews(((SQLDatabase) liveDatabase).getViews()));
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
   *
   * @param liveViews All of the views in the live database.
   * @return The SQL statements to run in order to make the live database have the
   *         same views as the dev one.
   */
  public List<String> updateViews(List<View> liveViews) {
    List<String> sql = new ArrayList<>();
    for (View liveView : liveViews) {
      sql.add(liveView.getDrop());
    }

    for (View devView : views) {
      sql.add(devView.getCreateStatement());
    }
    return sql;
  }

  /**
   * Determines which tables are to be created and which are to be dropped.
   *
   * @param liveTables All tables in the live database where the key is the name
   *                   of the table.
   * @return The SQL statements to run in order to remove and/or create tables in
   *         the live database.
   */
  public List<String> compareTables(Map<String, Table> liveTables) {
    List<String> sql = new ArrayList<>();
    String tableName;
    for (Map.Entry<String, Table> table : tables.entrySet()) {
      tableName = table.getKey();
      if (!liveTables.containsKey(tableName)) {
        sql.add(table.getValue().getCreateStatement());
        exclude.put(tableName, tableName);
      }
    }

    for (Map.Entry<String, Table> table : liveTables.entrySet()) {
      tableName = table.getKey();
      if (!tables.containsKey(tableName)) {
        sql.add(table.getValue().getDrop());
        exclude.put(tableName, tableName);
      }
    }
    return sql;
  }

  /**
   * Takes in two table lists and returns the SQL statements to run in order to
   * make the live database have the same table definitions as the dev one.
   *
   * @param live         All tables in the live database where the key is the name
   *                     of the table.
   * @param updateTables All the tables which are not the same in the live and
   *                     development databases.
   * @return The SQL statements to run in order to make the live database have the
   *         same table definitions as the dev one.
   */
  public List<String> updateTables(Map<String, Table> live, Map<String, String> updateTables) {
    List<String> sql = new ArrayList<>();
    for (String tableName : updateTables.keySet()) {
      if (!exclude.containsKey(tableName)) {
        sql.addAll(tables.get(tableName).generateStatements(live.get(tableName)));
      }
    }
    return sql;
  }

  /**
   * Compares the tables in the live and dev databases, and returns a list of
   * table names whose structure did not match between the development and live
   * databases.
   *
   * @param liveTables   All tables in the live database where the key is the name
   *                     of the table.
   * @param liveDatabase The live database connection which allows the live
   *                     exclusions to be updated here to prevent unnecessary
   *                     Primary Key dropping.
   * @return The tables that are to be updated because their structures did not
   *         match between the development and live databases.
   */
  public Map<String, String> tablesDiffs(Map<String, Table> liveTables, SQLDatabase liveDatabase) {
    Map<String, String> updateTables = new HashMap<>();
    String tableName;
    for (Map.Entry<String, Table> table : tables.entrySet()) {
      tableName = table.getKey();
      if (!exclude.containsKey(tableName)
          && !table.getValue().getCreateStatement().equals(liveTables.get(tableName).getCreateStatement())) {
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
