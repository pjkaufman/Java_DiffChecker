package dbdiffchecker;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Table resembles a table in MySQL and contains info about the table's columns.
 * @author Peter Kaufman
 * @version 5-24-18
 * @since 9-10-17
 */
public class Table extends Schema {

  private String charSet = "";
  private String collation = "";
  private String autoIncrement = "";
  private int count = 0;
  private HashMap<String, Column> columns = new HashMap<>();
  private HashMap<String, Index> indices = new HashMap<>();

  /**
   * Table initializes a Table object with a name create statement.
   * @author Peter Kaufman
   * @param name is a String that is the name of the table.
   * @param create is a String which is the create statement of the
   *     table info to create its columns.
   */
  public Table(String name, String create) {
    String temp = create.substring(create.indexOf("DEFAULT CHARSET=") + 16) + " ";
    this.name = name;
    this.createStatement = create + ";";
    this.charSet = temp.substring(0, temp.indexOf(" "));
  }

  /**
   * This is the default constructor for this class, which is needed for the file 
   * conversion to JSON. 
   */
  public Table() {}

  /**
   * getCollation returns the collation of the table.
   * @author Peter Kaufman
   * @return collation is the collation of the table.
   */
  public String getCollation() {

    return this.collation;
  }

  /**
   * getCharSet returns the character set of the table.
   * @author Peter Kaufman
   * @return charSet is the character set of the table.
   */
  public String getCharSet() {

    return this.charSet;
  }

  /**
   * getAutoIncrement returns the autoIncrement count of the table.
   * @author Peter Kaufman
   * @return autoIncrement is the autoIncrement count of the table.
   */
  public String getAutoIncrement() {

    return this.autoIncrement;
  }

  /**
   * getColumns returns a HashMap of Strings and Column objects.
   * @author Peter Kaufman
   * @return columns is a HashMap of String and Column object pairs which are the table's columns.
   */
  public HashMap<String, Column> getColumns() {

    return this.columns;
  }

  /**
   * getIndices returns a HashMap of Strings and Index objects.
   * @author Peter Kaufman
   * @return indices is a HashMap of String and Index object pairs
   *     which are the index names and index data.
   */
  public HashMap<String, Index> getIndices() {

    return this.indices;
  }

  /**
   * setCollation sets the collation of the table.
   * @author Peter Kaufman
   * @param collation is a String which is the collation of the table.
   */
  public void setCollation(String collation) {

    this.collation = collation;
  }

  /**
   * setCharSet sets the character set of the table.
   * @author Peter Kaufman
   * @param charSet is a String which is the character set of the table.
   */
  public void setCharSet(String charSet) {

    this.charSet = charSet;
  }

  /**
   * setAutoIncrement sets the autoIncrement count of the table.
   * @author Peter Kaufman
   * @param autoIncrement is a String which is the autoIncrement count of the table.
   */
  public void setAutoIncrement(String autoIncrement) {

    this.autoIncrement = autoIncrement;
  }

  /**
   * addColumn adds a column to the columns HashMap.
   * @author Peter Kaufman
   * @param col is a Column object which is to be added to the column list.
   */
  public void addColumn(Column col) {

    this.columns.put(col.getName(), col);
  }

  /**
   * addIndex adds an index to the indices HashMap.
   * @author Peter Kaufman
   * @param index is an Index object which is to be added to the index list.
   */
  public void addIndex(Index index) {

    this.indices.put(index.getName(), index);
  }

  /**
   * equals takes in a Table and compares it to the current one, the result is SQL statements 
   * to make them the same.
   * @author Peter Kaufman 
   * @param t1 is a Table object which is being compared to this Table object.
   * @return sql is an ArrayList of String which is the SQL needed to make the tables the same.
   */
  public ArrayList<String> equals(Table t1) {

    ArrayList<String> sql = new ArrayList<>();
    String sql2 = "ALTER TABLE `" + this.name + "`\n";

    if (!this.charSet.equals(t1.charSet) | !this.collation.equals(t1.collation)) {

      sql2 += "CHARACTER SET " + this.charSet + " COLLATE " + this.collation;
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
   * dropCols takes two HashMaps of String and Column objects and returns part 
   * of an SQL statement that drops a column or several columns. 
   * @author Peter Kaufman
   * @param cols1 is a HashMap of Strings and Column object pairs which are the
   *     column names and column data of the current table.
   * @param cols2 is a HashMap of Strings and Column object pairs which are the
   *     column names and column data of a different table of the same name.
   * @return sql is a String which is part of an SQL statement that drops a column
   *     or several columns.  
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
   * otherCols takes two HashMaps of String and Column objects and returns part
   * of an SQL statement that modifies and/or adds columns.
   * @author Peter Kaufman
   * @param cols1 is a HashMap of Strings and Column object pairs which are the 
   *     column names and column data of the current table.
   * @param cols2 is a HashMap of Strings and Column object pairs which are the 
   *     column names and column data of a different table of the same name.
   * @return sql is a String which is part of an SQL statement that modifies and/or adds columns.
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

          break;
        }
      }

      last = " AFTER `" + col.getName() + "`";
    }

    return sql;
  }

  /**
   * dropIndices takes in two HashMaps of Indices and returns part of an SQL statement
   * that drop indexes.
   * @author Peter Kaufman
   * @param dev is a HashMap of Strings and Index object pairs which are the index names 
   *     and index data of the current table.
   * @param live is a HashMap of Strings and Index object pairs which are the index names
   *     and index data of a different table of the same name.
   * @return sql is a String which is part of an SQL statement that drops indexes.
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
   * otherIndices takes in two lists of Indices and returns part of an SQL statement that 
   * either adds or drops and adds indexes.
   * @author Peter Kaufman
   * @param dev is a HashMap of Strings and Index object pairs which are the index names and index 
   *     data of the current table.
   * @param live is a HashMap of Strings and Index object pairs which are the index 
   *     names and index data of a different table of the same name.
   * @return sql is a String which is part of an SQL statement that either adds or 
   *     drops and adds indexes.
   */
  private String otherIndices(HashMap<String, Index> dev, HashMap<String, Index> live) {

    String sql = "";
    Index indices1 = null;
    // check for missing indices
    for (String indexName : dev.keySet()) {
      // if the index exists in both databases or only in the dev database then add it
      indices1 = dev.get(indexName);
      if (live.containsKey(indexName)) {
        if (indices1.compareTo(live.get(indexName)) == 1) {
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

          sql += "" + indices1.getCreateStatement();
        } else {

          sql += ", \n" + indices1.getCreateStatement();
        }

        this.count++;
      }
    }

    return sql;
  }
}
