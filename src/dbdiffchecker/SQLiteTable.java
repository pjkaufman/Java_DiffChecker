package dbdiffchecker;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Resembles a table in SQLite and contains info about the table's columns and
 * indices.
 * @author Peter Kaufman
 * @version 5-23-19
 * @since 9-10-17
 */
public class SQLiteTable extends Table {
  // Instance variables
  private boolean stopCompare = false;

  /**
   * Sets the name and create statement of the table.
   * @author Peter Kaufman
   * @param name The name of the table.
   * @param create The create statement of the table which will be used to create
   *        its columns and indices.
   */
  public SQLiteTable(String name, String create) {
    super(name, create);
  }

  /**
   * This is the default constructor for this class, <b> Needed for
   * Serialization</b>.
   */
  public SQLiteTable() {}

  @Override
  public ArrayList<String> equals(Table t1) {
    stopCompare = false;
    this.count = 0;
    ArrayList<String> sql = new ArrayList<>();
    String sql2 = "";
    sql2 += dropIndices(this.indices, t1.getIndices());
    sql2 += otherCols(this.columns, t1.getColumns());
    // if a column needs to be modified, recreate the table
    if (stopCompare) {
      sql.addAll(recreateTable(t1.getColumns()));
      return sql;
    }
    sql2 += dropCols(this.columns, t1.getColumns());
    // if a column needs to be dropped, recreate the table
    if (stopCompare) {
      sql.addAll(recreateTable(t1.getColumns()));
      return sql;
    }
    sql2 += otherIndices(this.indices, t1.getIndices());
    if (this.count != 0) {
      sql.add(sql2);
    }
    return sql;
  }

  @Override
  protected void parseCreateStatement() {
    String[] parts, columns;
    ArrayList<String> bodySections = new ArrayList<>();
    String indexIndicator = "KEY (";
    String name = "";
    String details = "";
    String create = "";
    String body;
    String indexColumns;
    int nameEnd = 0;
    create = createStatement.replace("CREATE TABLE " + this.name + " ", "");
    // separate the main create statement from pther add ons
    parts = create.split("\n");
    body = parts[0];
    // remove unneeded characters
    body = body.trim();
    if (body.startsWith("(")) {
      body = body.substring(1);
    }
    if (body.endsWith(");")) {
      body = body.substring(0, body.length() - 2);
    } else if (body.endsWith(")")) {
      body = body.substring(0, body.length() - 1);
    }
    // split the body into parts
    int comma, startParen, endParen;
    while (body.contains(",")) {
      comma = body.indexOf(",");
      startParen = body.indexOf("(");
      while (startParen != -1 && startParen < comma) {
        endParen = body.indexOf(")", startParen);
        // determine whether the comma is affected by the parentheses
        // and correct it if it is
        if (comma < endParen) {
          comma = body.indexOf(",", endParen);
        }
        startParen = body.indexOf("(", endParen);
      }
      bodySections.add(body.substring(0, comma));
      body = body.substring(comma + 1);
    }
    bodySections.add(body);
    // parse the columns, PRIMARY KEYs, FOREIGN KEYs, and constraints
    for (String part : bodySections) {
      part = part.trim();
      if (!part.contains(indexIndicator)) {
        nameEnd = part.indexOf(" ");
        name = part.substring(0, nameEnd);
        details = part.substring(nameEnd + 1);
        addColumn(new Column(name, details));
      } else {
        if (part.contains("PRIMARY KEY")) { // dealing with PRIMARY KEY
          String temp = part.substring(part.indexOf("(") + 1, part.indexOf(")"));
          columns = temp.split(",");
          // add PRIMARY KEY label to each column affected by the PRIMARY KEY
          for (String column : columns) {
            // recreate columns
            column = column.trim();
            addColumn(new Column(column, this.columns.get(column.trim()).getDetails().concat(" PRIMARY KEY")));
          }
        } else {
          System.out.println("Unknown key: " + part);
        }
      }
    }
    // parse the remaining indices ...
    for (int i = 1; i < parts.length; i++) {
      String part = parts[i];
      name = part.substring(part.indexOf("INDEX ") + 6, part.indexOf(" ON"));
      indexColumns = part.substring(part.indexOf("(") + 1, part.indexOf(")"));
      addIndex(new Index(name, part.replace(";", "").trim(), indexColumns));
    }
  }

  @Override
  protected String dropCols(HashMap<String, Column> cols1, HashMap<String, Column> cols2) {
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

  @Override
  protected String otherCols(HashMap<String, Column> cols1, HashMap<String, Column> cols2) {
    String sql = "";
    Column col = null;
    Column col2 = null;
    for (String columnName : cols1.keySet()) {
      col = cols1.get(columnName);
      if (!cols2.containsKey(columnName)) {
        if (this.count != 0) {
          sql += "\n";
        }
        sql += "ALTER TABLE " + this.name + "\n\tADD COLUMN " + col.getName() + " " + col.getDetails() + ";";
        this.count++;
      } else {
        col2 = cols2.get(columnName);
        if (col.getName().equals(col2.getName())) { // columns have the same name
          if (!col.getDetails().equals(col2.getDetails())) { // column details are different
            stopCompare = true;
            return sql;
          }
        }
      }
    }
    return sql;
  }

  @Override
  protected String dropIndices(HashMap<String, Index> dev, HashMap<String, Index> live) {
    String sql = "";
    // check for indices to remove
    for (String indexName : live.keySet()) {
      // if the index does not exist in the dev database then drop it
      if (!dev.containsKey(indexName)) {
        if (this.count != 0) {
          sql += "\n";
        }
        sql += "DROP INDEX " + indexName + ";";
        this.count++;
      }
    }
    return sql;
  }

  @Override
  protected String otherIndices(HashMap<String, Index> dev, HashMap<String, Index> live) {
    String sql = "";
    Index indices1 = null;
    // check for missing indices
    for (String indexName : dev.keySet()) {
      // if the index exists in both databases or only in the dev database then add it
      indices1 = dev.get(indexName);
      if (live.containsKey(indexName)) {
        if (!indices1.sameDetails(live.get(indexName))) {
          if (this.count != 0) {
            sql += "\n";
          }
          sql += "DROP INDEX " + indices1.getName() + ";\n" + indices1.getCreateStatement() + ";";
          this.count++;
        }
      } else {
        if (this.count != 0) {
          sql += "\n";
        }
        sql += indices1.getCreateStatement() + ";";
        this.count++;
      }
    }
    return sql;
  }

  /**
   * Recreates the structure of the table using the columns provided to copy over
   * old data into the new table based on common columns between the development
   * and live tables.
   * @param live A list of columns and their definitions which helps the transfer
   *        of data for common collumns.
   * @return The SQL statements needed to recreate the development table.
   */
  private ArrayList<String> recreateTable(HashMap<String, Column> live) {
    String commonColumns = "";
    boolean doExtraWork = this.createStatement.lastIndexOf("CREATE") > 6;
    ArrayList<String> sql = new ArrayList<>();
    for (String columnName : live.keySet()) {
      if (this.columns.containsKey(columnName)) {
        commonColumns += "" + columnName + ",";
      }
    }
    if (commonColumns.length() != 0) {
      // there are columns in common so the table needs to be renamed,
      // have its data copied into a new table of the same name, and then be deleted
      commonColumns = commonColumns.substring(0, commonColumns.length() - 1);
      // add the appropriate sql
      sql.add("PRAGMA foreign_keys=off;");
      sql.add("ALTER TABLE " + this.name + " RENAME TO temp_table;");
      if (!doExtraWork) {
        sql.add(this.createStatement);
      } else {
        sql.add(this.createStatement.substring(0, this.createStatement.indexOf("CREATE", 6) - 1));
      }
      sql.add(
          "INSERT INTO " + this.name + " (" + commonColumns + ")\n\tSELECT " + commonColumns + "\n\tFROM temp_table;");
      sql.add("DROP TABLE temp_table;");
      if (doExtraWork) {
        sql.add(this.createStatement.substring(this.createStatement.indexOf("CREATE", 6)));
      }
      sql.add("PRAGMA foreign_keys=on;");
    } else {
      sql.add("DROP TABLE " + this.name + ";");
      sql.add(this.createStatement);
    }
    return sql;
  }
}
