package dbdiffchecker;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Table resembles a table in MySQL and contains info about the table's columns.
 * @author Peter Kaufman
 * @version 5-11-19
 * @since 9-10-17
 */
public class Table extends Schema {

  protected String charSet = "";
  protected String collation = "";
  protected String autoIncrement = "";
  protected int count = 0;
  protected HashMap<String, Column> columns = new HashMap<>();
  protected HashMap<String, Index> indices = new HashMap<>();

  /**
   * Initializes a Table object with a name create statement.
   * @author Peter Kaufman
   * @param name The name of the table.
   * @param create The create statement of the table info to create its columns.
   */
  public Table(String name, String create) {
    String temp = create.substring(create.indexOf("DEFAULT CHARSET=") + 16) + " ";
    this.name = name;
    this.createStatement = create + ";";
    this.charSet = temp.substring(0, temp.indexOf(" "));
  }

  /**
   * The default constructor is needed for serialization.
   */
  public Table() {}

  /**
   * Returns the collation of the table.
   * @author Peter Kaufman
   * @return The collation of the table.
   */
  public String getCollation() {

    return this.collation;
  }

  /**
   * Returns the character set of the table.
   * @author Peter Kaufman
   * @return The character set of the table.
   */
  public String getCharSet() {

    return this.charSet;
  }

  /**
   * Returns the autoIncrement count of the table.
   * @author Peter Kaufman
   * @return The autoIncrement count of the table.
   */
  public String getAutoIncrement() {

    return this.autoIncrement;
  }

  /**
   * Returns the name of the table.
   * @author Peter Kaufman
   * @return The name of the table.
   */
  public String getName() {
    
    return this.name;
  }

  /**
   * Returns a HashMap of Strings and Column objects.
   * @author Peter Kaufman
   * @return The table's columns.
   */
  public HashMap<String, Column> getColumns() {

    return this.columns;
  }

  /**
   * Returns a HashMap of Strings and Index objects.
   * @author Peter Kaufman
   * @return The index names and index data.
   */
  public HashMap<String, Index> getIndices() {

    return this.indices;
  }

  /**
   * Sets the collation of the table.
   * @author Peter Kaufman
   * @param collation The collation of the table.
   */
  public void setCollation(String collation) {

    this.collation = collation;
  }

  /**
   * Sets the character set of the table.
   * @author Peter Kaufman
   * @param charSet The character set of the table.
   */
  public void setCharSet(String charSet) {

    this.charSet = charSet;
  }

  /**
   * Sets the autoIncrement count of the table.
   * @author Peter Kaufman
   * @param autoIncrement The autoIncrement count of the table.
   */
  public void setAutoIncrement(String autoIncrement) {

    this.autoIncrement = autoIncrement;
  }

  /**
   * Adds a column to the columns HashMap.
   * @author Peter Kaufman
   * @param col A Column object which is to be added to the column list.
   */
  public void addColumn(Column col) {

    this.columns.put(col.getName(), col);
  }

  /**
   * Adds an index to the indices HashMap.
   * @author Peter Kaufman
   * @param index An Index object which is to be added to the index list.
   */
  public void addIndex(Index index) {

    this.indices.put(index.getName(), index);
  }

  /**
   * Takes in a Table and compares it to the current one, the result is SQL statements 
   * to make them the same.
   * @author Peter Kaufman 
   * @param t1 A Table object which is being compared to this Table object.
   * @return The SQL needed to make the tables the same.
   */
  public ArrayList<String> equals(Table t1) {
    ArrayList<String> sql = new ArrayList<>();
    String sql2 = "ALTER TABLE `" + this.name + "`\n";

    if (!this.charSet.equals(t1.charSet) | !this.collation.equals(t1.collation)) {

      sql2 += "CHARACTER SET " + this.charSet;
      if (!this.collation.equals("")) {
        sql2 += " COLLATE " + this.collation;
      }
      this.count++;
    }

    sql2 += dropIndices(this.indices, t1.getIndices());
    sql2 += otherCols(this.columns, t1.getColumns());
    sql2 += dropCols(this.columns, t1.getColumns());
    sql2 += otherIndices(this.indices, t1.getIndices()) + ";";
    if (this.count != 0) {

      sql.add(sql2);
    }

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
        if (this.count == 0) {

          sql += "DROP COLUMN `" + col.getName() + "`";
        } else {

          sql += ", \nDROP COLUMN `" + col.getName() + "`";
        }

        this.count++;
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
    String last = "";
    Column col = null;
    Column col2 = null;

    for (String columnName : cols1.keySet()) {

      col = cols1.get(columnName);
      if (!cols2.containsKey(columnName)) {
        if (this.count == 0) {

          sql += "ADD COLUMN `" + col.getName() + "` " + col.getDetails() + last;
        } else {

          sql += ", \nADD COLUMN `" + col.getName() + "` " + col.getDetails() + last;
        }

        this.count++;
      } else {

        col2 = cols2.get(columnName);
        if (col.getName().equals(col2.getName())) {         // columns are the same
          if (!col.getDetails().equals(col2.getDetails())) {         // column details are different
            if (this.count == 0) {

              sql += "MODIFY COLUMN `" + col.getName() + "` " + col.getDetails();
            } else {

              sql += ", \nMODIFY COLUMN `" + col.getName() + "` " + col.getDetails();
            }

            this.count++;
          }
        }
      }

      last = " AFTER `" + col.getName() + "`";
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
        if (this.count == 0) {

          sql += "DROP INDEX `" + indexName + "`";
        } else {

          sql += ", \nDROP INDEX `" + indexName + "`";
        }

        this.count++;
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
          if (this.count == 0) {

            sql += "DROP INDEX `" + indices1.getName() + "`";
            sql += ", \n" + indices1.getCreateStatement();
          } else {

            sql += ", \nDROP INDEX `" + indices1.getName() + "`";
            sql += ", \n" + indices1.getCreateStatement();
          }

          this.count++;
        }
      } else {
        if (this.count == 0) {

          sql += indices1.getCreateStatement();
        } else {

          sql += ", \n" + indices1.getCreateStatement();
        }

        this.count++;
      }
    }

    return sql;
  }
}
