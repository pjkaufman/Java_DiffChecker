package dbdiffchecker;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * MySQLTable resembles a Table in MySQL and contains info about the Table's 
 * columns and indices.
 * @author Peter Kaufman
 * @version 5-16-19
 * @since 5-15-19
 */
public class MySQLTable extends Table {

  /**
   * Initializes a MySQLTable object with a name create statement.
   * @author Peter Kaufman
   * @param name The name of the MySQLTable.
   * @param create The create statement of the MySQLTable info to create its columns.
   */
  public MySQLTable(String name, String create) {
    super(name, create);
    String temp = create.substring(create.indexOf("DEFAULT CHARSET=") + 16) + " ";
    this.charSet = temp.substring(0, temp.indexOf(" "));
  }

  /**
   * This is the default constructor for this class, <b> Needed for Serialization</b>.
   */
  public MySQLTable() {}

  @Override
  public ArrayList<String> equals(Table t1) {
    ArrayList<String> sql = new ArrayList<>();
    this.count = 0;
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

  @Override
  protected void parseCreateStatement(){
    String[] parts;
    String columnIndicator = "`";
    String indexIndicator = "KEY";
    String name = "";
    String details = "";
    String columns = "";
    String create = "";
    parts = this.createStatement.split("\n");
    for (String part : parts) {
      part = part.trim();
      if (part.endsWith(",")) {
        part = part.substring(0, part.length() - 1);
      }
      if (part.startsWith(columnIndicator)) {
        name = part.substring(part.indexOf("`") + 1, part.lastIndexOf("`"));
        details = part.substring(part.lastIndexOf("`") + 2);
        // add the column
        addColumn(new Column(name, details));
      } else if (part.contains(indexIndicator)) {
        create = "ADD " + part;
        if (part.contains("PRIMARY KEY")) { // dealing with PRIMARY KEY
          name = "PRIMARY";
        } else {
          name = part.substring(part.indexOf("`") + 1, part.indexOf("`", part.indexOf("`") + 1));
          create = create.replace("KEY", "INDEX");
        }
        columns = part.substring(part.indexOf("(") + 1, part.lastIndexOf(")"));
        // add the index
        addIndex(new Index(name, create, columns));
      }
    }
  }

  /**
   * Takes two HashMaps of String and Column objects and returns part of an SQL statement 
   * that drops a column or several columns. 
   * @author Peter Kaufman
   * @param cols1 The column names and column data of the current MySQLTable.
   * @param cols2 The column names and column data of a different MySQLTable of the same name.
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
   * @param cols1 The column names and column data of the current MySQLTable.
   * @param cols2 The column names and column data of a different MySQLTable of the same name.
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
   * @param dev The index names and index data of the current MySQLTable.
   * @param live The index names and index data of a different MySQLTable of the same name.
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
   * @param dev The index names and index data of the current MySQLTable.
   * @param live The index names and index data of a different MySQLTable of the same name.
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
