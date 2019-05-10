package dbdiffchecker;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Table resembles a table in MySQL and contains info about the table's columns.
 * Program Name: Database Difference Checker
 * CSCI Course: 325
 * Grade Received: Pass
 * @author Peter Kaufman
 * @version 2-16-19
 * @since 9-10-17
 */
public class SQLiteTable extends Table {

  private boolean stopCompare = false;

  /**
   * Initializes a Table object with a name create statement.
   * @author Peter Kaufman
   * @param name The name of the table.
   * @param create The create statement of the table info to create its columns.
   */
  public SQLiteTable(String name, String create) {
    super(name, create);
  }

  /**
   * The default constructor is needed for serialization.
   */
  public SQLiteTable() {}
  
  /**
   * Takes in a Table and compares it to the current one, the result is SQL statements 
   * to make them the same.
   * @author Peter Kaufman 
   * @param t1 A Table object which is being compared to this Table object.
   * @return The SQL needed to make the tables the same.
   */
  public ArrayList<String> equals(Table t1) {
    stopCompare = false;
    
    ArrayList<String> sql = new ArrayList<>();
    String sql2 = "";

    sql2 += dropIndices(this.indices, t1.getIndices());
    // if a primary key needs to be deleted, recreate the table
    if ( stopCompare ) {
      sql.addAll(recreateTable(t1.getColumns()));

      return sql;
    }
    sql2 += otherCols(this.columns, t1.getColumns());
    // if a column needs to be modified, recreate the table
    if ( stopCompare ) {
      sql.addAll(recreateTable(t1.getColumns()));

      return sql;
    }
    sql2 += dropCols(this.columns, t1.getColumns());
    // if a column needs to be dropped, recreate the table
    if ( stopCompare ) {
      sql.addAll(recreateTable(t1.getColumns()));

      return sql;
    }
    sql2 += otherIndices(this.indices, t1.getIndices()) + ";";
    if ( stopCompare ) {
      sql.addAll(recreateTable(t1.getColumns()));

      return sql;
    }
    sql.add(sql2);

    return sql;
  }

  /**
   * Takes two HashMaps of String and Column objects and returns part of an SQL statement 
   * that drops a column or several columns. 
   * @author Peter Kaufman
   * @param cols1 The column names and column data of the current table.
   * @param cols2 The column names and column data of a different table of the same name.
   * @return Part of an SQL statement that drops a column or several columns.  
   */
  private String dropCols(HashMap<String, Column> cols1, HashMap<String, Column> cols2) {

    String sql = "";
    Column col = null;
    // check for columns to drop
    for (String columnName : cols2.keySet()) {

      col = cols2.get(columnName);
      if (!cols1.containsKey(columnName)) {
        stopCompare = true;

        return sql;
      }
    }

    return sql;
  }

  /**
   * Takes two HashMaps of String and Column objects and returns part of an SQL statement 
   * that modifies and/or adds columns.
   * @author Peter Kaufman
   * @param cols1 The column names and column data of the current table.
   * @param cols2 The column names and column data of a different table of the same name.
   * @return Part of an SQL statement that modifies and/or adds columns.
   */
  private String otherCols(HashMap<String, Column> cols1, HashMap<String, Column> cols2) {

    String sql = "";
    Column col = null;
    Column col2 = null;
    for (String columnName : cols1.keySet()) {

      col = cols1.get(columnName);
      if (!cols2.containsKey(columnName)) {

        sql += "ALTER TABLE `" + this.name + "`\n\tADD COLUMN `" + col.getName() + "` " + col.getDetails() + ";";
      } else {

        col2 = cols2.get(columnName);
        if (col.getName().equals(col2.getName())) {         // columns are the same
          if (!col.getDetails().equals(col2.getDetails())) {         // column details are different
           stopCompare = true;
           return sql;
          }
        }
      }
    }

    return sql;
  }

  /**
   * Takes in two HashMaps of Indices and returns part of an SQL statement that drop indexes.
   * @author Peter Kaufman
   * @param dev The index names and index data of the current table.
   * @param live The index names and index data of a different table of the same name.
   * @return Part of an SQL statement that drops indexes.
   */
  private String dropIndices(HashMap<String, Index> dev, HashMap<String, Index> live) {

    String sql = "";
    // check for indices to remove
    for (String indexName: live.keySet()) {
      // if the index does not exist in the dev database then drop it
      if (!dev.containsKey(indexName)) {
        // if the index is a primary key stop the comparison and just recreate the table
        if (live.get(indexName).isPrimaryKey()) {
          stopCompare = true;

          return sql;
        }
        sql += "\nDROP INDEX `" + indexName + "`;";
      }
    }

    return sql;
  }

  /**
   * Takes in two lists of Indices and returns part of an SQL statement that 
   * either adds or drops and adds indexes.
   * @author Peter Kaufman
   * @param dev The index names and index data of the current table.
   * @param live The index names and index data of a different table of the same name.
   * @return Part of an SQL statement that either adds or drops and adds indexes.
   */
  private String otherIndices(HashMap<String, Index> dev, HashMap<String, Index> live) {
    String sql = "";
    Index indices1 = null;
    // check for missing indices
    for (String indexName : dev.keySet()) {
      // if the index exists in both databases or only in the dev database then add it
      indices1 = dev.get(indexName);
      if (live.containsKey(indexName)) {
        if (!indices1.sameDetails(live.get(indexName))) {
          if (live.get(indexName).isPrimaryKey()) {
            stopCompare = true;
  
            return sql;
          }
         sql += "\nDROP INDEX `" + indices1.getName() + "`;\n" + indices1.getCreateStatement();
        }
      } else {
        // if a primary key needs to be added to the live database, recreate the table
        if (indices1.isPrimaryKey()) {
          stopCompare = true;

          return sql;
        }
  
        sql += "\n" + indices1.getCreateStatement();
      }
    }

    return sql;
  }

  private ArrayList<String> recreateTable(HashMap<String, Column> live) {
    String commonColumns = "";
    boolean doExtraWork = this.createStatement.lastIndexOf("CREATE") > 6 ;
    ArrayList<String> sql = new ArrayList<>();
    for (String columnName : live.keySet()) { 
      if (this.columns.containsKey(columnName)) {
        commonColumns += "`" + columnName + "`,";
      }
    }
    if (commonColumns.length() != 0) { 
      // there are columns in common so the table needs to be renamed, 
      // have its data copied into a new table of the same name, and then be deleted
      commonColumns = commonColumns.substring(0, commonColumns.length() -1);
      // add the appropriate sql
      sql.add("PRAGMA foreign_keys=off;");
      sql.add("ALTER TABLE `" + this.name + "` RENAME TO `temp_table`;");
      
      if (!doExtraWork) {
        sql.add(this.createStatement);
      } else {
        sql.add(this.createStatement.substring(0, this.createStatement.indexOf("CREATE", 6) - 1));
      }      
      sql.add("INSERT INTO `" + this.name +  "` (" + commonColumns + ")\n\tSELECT " + commonColumns + "\n\tFROM `temp_table`;");
      sql.add("DROP TABLE `temp_table`;");
      if (doExtraWork) {
        sql.add(this.createStatement.substring(this.createStatement.indexOf("CREATE", 6)));
      }
      sql.add("PRAGMA foreign_keys=on;");
    } else {
      sql.add("DROP TABLE `" + this.name + "`;");
      sql.add(this.createStatement);       
    }

    return sql;
  }
}
