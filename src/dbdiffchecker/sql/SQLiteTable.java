package dbdiffchecker.sql;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Resembles a table in SQLite and contains info about the table's columns and
 * indices.
 *
 * @author Peter Kaufman
 * @version 7-8-20
 * @since 5-11-19
 */
public class SQLiteTable extends Table {
  private boolean stopCompare = false;
  private int foreignKeyCount = 0;

  /**
   * Sets the name and create statement of the table.
   *
   * @param name   The name of the table.
   * @param create The create statement of the table which will be used to create
   *               its columns and indices.
   */
  public SQLiteTable(String name, String create) {
    super(name, create);
    this.drop = "DROP TABLE " + name + ";";
    newLineCreation = "\n";
  }

  /**
   * This is the default constructor for this class, <b> Needed for
   * Serialization</b>.
   */
  public SQLiteTable() {
  }

  @Override
  public List<String> equals(Table t1) {
    stopCompare = false;
    isFirstStatement = true;
    List<String> sql = new ArrayList<>();
    String sql2 = "";
    // if there are a different amount of foreing keys the table needs to be
    // recreated
    if (this.foreignKeyCount != ((SQLiteTable) t1).foreignKeyCount) {
      sql.addAll(recreateTable(t1.getColumns()));
      return sql;
    }
    sql2 += dropIndices(this.indices, t1.getIndices());
    // if a foreign key is to be dropped, recreate the table
    if (stopCompare) {
      sql.addAll(recreateTable(t1.getColumns()));
      return sql;
    }
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
    // if a foreign key needs to be added or modified, recreate the table
    if (stopCompare) {
      sql.addAll(recreateTable(t1.getColumns()));
      return sql;
    }
    if (!isFirstStatement) {
      sql.add(sql2);
    }
    return sql;
  }

  @Override
  protected void parseCreateStatement() {
    String[] parts;
    String[] columns;
    List<String> bodySections = new ArrayList<>();
    String indexIndicator = ".*([K|k][E|e][Y|y])(\\s)*(\\().*";
    String name = "";
    String drop = "";
    String details = "";
    String create = "";
    String body;
    int nameEnd = 0;
    create = createStatement.substring(createStatement.indexOf("(") + 1).trim();
    create = create.trim();
    if (create.endsWith(";")) {
      create = create.substring(0, create.indexOf(";", create.length() - 6));
    }
    // separate the main create statement from other add-ons
    parts = create.split(";");
    body = parts[0];
    body = body.trim();
    if (body.endsWith(");")) {
      body = body.substring(0, body.length() - 2);
    } else if (body.endsWith(")")) {
      body = body.substring(0, body.length() - 1);
    }
    int comma;
    int startParen;
    int endParen;
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
      if (!part.matches(indexIndicator)) {
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
        } else if (part.contains("FOREIGN KEY")) {
          foreignKeyCount++;
          if (part.contains("CONSTRAINT ")) {
            int start = part.indexOf("CONSTRAINT ") + 11;
            name = part.substring(start, part.indexOf(" ", start));
          } else {
            name = "FOREIGN KEY" + foreignKeyCount;
          }
          drop = "";
          addIndex(new Index(name, part.trim(), drop));
        }
      }
    }
    // parse the remaining indices ...
    for (int i = 1; i < parts.length; i++) {
      String part = parts[i];
      name = part.substring(part.indexOf("INDEX ") + 6, part.indexOf(" ON"));
      drop = "DROP INDEX " + name + ";";
      addIndex(new Index(name, part.replace(";", "").trim(), drop));
    }
  }

  @Override
  protected String dropCols(Map<String, Column> cols1, Map<String, Column> cols2) {
    for (String columnName : cols2.keySet()) {
      if (!cols1.containsKey(columnName)) {
        stopCompare = true;
        return "";
      }
    }
    return "";
  }

  @Override
  protected String otherCols(Map<String, Column> cols1, Map<String, Column> cols2) {
    StringBuilder sql = new StringBuilder();
    Column col;
    Column col2;
    for (String columnName : cols1.keySet()) {
      col = cols1.get(columnName);
      if (!cols2.containsKey(columnName)) {
        appendSQLPart(sql,
            "ALTER TABLE " + this.name + "\n\tADD COLUMN " + col.getName() + " " + col.getDetails() + ";");
      } else {
        col2 = cols2.get(columnName);
        if (col.getName().equals(col2.getName()) && !col.getDetails().equals(col2.getDetails())) {
          stopCompare = true;
          return sql.toString();
        }
      }
    }
    return sql.toString();
  }

  @Override
  protected String dropIndices(Map<String, Index> dev, Map<String, Index> live) {
    StringBuilder sql = new StringBuilder();
    for (String indexName : live.keySet()) {
      if (!dev.containsKey(indexName)) {
        if (live.get(indexName).getCreateStatement().contains("FOREIGN KEY")) {
          stopCompare = true;
          return sql.toString();
        }
        appendSQLPart(sql, live.get(indexName).getDrop());
      }
    }
    return sql.toString();
  }

  @Override
  protected String otherIndices(Map<String, Index> dev, Map<String, Index> live) {
    StringBuilder sql = new StringBuilder();
    Index indices1 = null;
    for (String indexName : dev.keySet()) {
      indices1 = dev.get(indexName);
      if (live.containsKey(indexName)) {
        if (!indices1.equals(live.get(indexName))) {
          if (live.get(indexName).getCreateStatement().contains("FOREIGN KEY")) {
            stopCompare = true;
            return sql.toString();
          }
          appendSQLPart(sql, live.get(indexName).getDrop() + "\n" + indices1.getCreateStatement() + ";");
        }
      } else {
        if (indices1.getCreateStatement().contains("FOREIGN KEY")) {
          stopCompare = true;
          return sql.toString();
        }
        appendSQLPart(sql, indices1.getCreateStatement() + ";");
      }
    }
    return sql.toString();
  }

  /**
   * Recreates the structure of the table using the columns provided to copy over
   * old data into the new table based on common columns between the development
   * and live tables.
   *
   * @param live A list of columns and their definitions which helps the transfer
   *             of data for common collumns.
   * @return The SQL statements needed to recreate the development table.
   */
  private List<String> recreateTable(Map<String, Column> live) {
    StringBuilder commonColumns = new StringBuilder();
    boolean doExtraWork = this.createStatement.lastIndexOf("CREATE") > 6;
    List<String> sql = new ArrayList<>();
    for (String columnName : live.keySet()) {
      if (this.columns.containsKey(columnName)) {
        commonColumns.append("" + columnName + ",");
      }
    }
    if (commonColumns.length() != 0) {
      // there are columns in common so the table needs to be renamed,
      // have its data copied into a new table of the same name, and then be deleted
      commonColumns = new StringBuilder(commonColumns.substring(0, commonColumns.length() - 1));
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
    } else {
      sql.add(this.drop);
      sql.add(this.createStatement);
    }
    return sql;
  }
}
