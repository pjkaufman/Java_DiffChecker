package dbdiffchecker.sql;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Resembles a table in SQLite and contains info about the table's columns and
 * indices.
 *
 * @author Peter Kaufman
 */
public class SQLiteTable extends Table {
  private static final long serialVersionUID = 1L;
  private static final String FOREIGN_KEY_IDENTIFIER = "FOREIGN KEY";
  private static final String MULTIPLE_CREATE_INDICATOR = "CREATE";
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
    drop = "DROP TABLE " + name + ";";
    newLineCreation = "\n";
  }

  /**
   * <b>Needed for Serialization</b>
   */
  public SQLiteTable() {
  }

  @Override
  public List<String> generateStatements(Table t1) {
    stopCompare = false;
    isFirstStatement = true;
    List<String> sql = new ArrayList<>();
    List<String> sqlBody = new ArrayList<>();
    // if there are a different amount of foreing keys the table needs to be
    // recreated
    if (foreignKeyCount != ((SQLiteTable) t1).foreignKeyCount) {
      sql.addAll(recreateTable(t1.getColumns()));

      return sql;
    }
    sqlBody.addAll(dropIndices(indices, t1.getIndices()));
    // if a foreign key is to be dropped, recreate the table
    if (stopCompare) {
      sql.addAll(recreateTable(t1.getColumns()));

      return sql;
    }
    sqlBody.addAll(otherCols(columns, t1.getColumns()));
    // if a column needs to be modified, recreate the table
    if (stopCompare) {
      sql.addAll(recreateTable(t1.getColumns()));

      return sql;
    }
    sqlBody.addAll(dropCols(columns, t1.getColumns()));
    // if a column needs to be dropped, recreate the table
    if (stopCompare) {
      sql.addAll(recreateTable(t1.getColumns()));

      return sql;
    }
    sqlBody.addAll(otherIndices(indices, t1.getIndices()));
    // if a foreign key needs to be added or modified, recreate the table
    if (stopCompare) {
      sql.addAll(recreateTable(t1.getColumns()));

      return sql;
    }

    if (!sqlBody.isEmpty()) {
      sql.add(String.join(newLineCreation, sqlBody));
    }

    return sql;
  }

  @Override
  protected void parseCreateStatement() {
    List<String> bodySections = new ArrayList<>();
    String[] parts = separateCreateIntoParts(bodySections);
    addBodyColumnsAndIndices(bodySections);
    addRemainingIndices(parts);
  }

  /**
   * Separates the create statement into several parts and returns them.
   *
   * @param bodySections The list of different sections of the body.
   * @return The parts of the create statements needed to create the table.
   */
  private String[] separateCreateIntoParts(List<String> bodySections) {
    String[] sections;
    String create = createStatement.substring(createStatement.indexOf("(") + 1).trim();
    String body;

    if (create.endsWith(";")) {
      create = create.substring(0, create.indexOf(";", create.length() - 6));
    }
    sections = create.split(";");
    body = sections[0];
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
    return sections;
  }

  /**
   * Goes through the body and adds indices and columns.
   *
   * @param bodySections The parts of the body.
   */
  private void addBodyColumnsAndIndices(List<String> bodySections) {
    String indexIndicator = ".*([K|k][E|e][Y|y])(\\s)*(\\().*";
    int nameEnd = 0;
    String name;
    String details;
    for (String part : bodySections) {
      part = part.trim();
      if (!part.matches(indexIndicator)) {
        nameEnd = part.indexOf(" ");
        name = part.substring(0, nameEnd);
        details = part.substring(nameEnd + 1);
        addColumn(new Column(name, details));
      } else {
        addPrimaryAndForeignIndices(part);
      }
    }
  }

  /**
   * Adds Primary and Foreign keys to the table by making sure the appropriate
   * steps are taken if the table needs to be updated.
   *
   * @param part The current part of the table's create statement that is being
   *             parsed.
   */
  private void addPrimaryAndForeignIndices(String part) {
    String[] columns;
    String drop;
    String name;
    if (part.contains("PRIMARY KEY")) {
      String temp = part.substring(part.indexOf("(") + 1, part.indexOf(")"));
      columns = temp.split(",");
      for (String column : columns) { // recreate columns with PRIMARY KEY label
        addColumn(new Column(column, this.columns.get(column.trim()).getDetails().concat(" PRIMARY KEY")));
      }
    } else if (part.contains(FOREIGN_KEY_IDENTIFIER)) {
      foreignKeyCount++;
      if (part.contains("CONSTRAINT ")) {
        int start = part.indexOf("CONSTRAINT ") + 11;
        name = part.substring(start, part.indexOf(" ", start));
      } else {
        name = FOREIGN_KEY_IDENTIFIER + foreignKeyCount;
      }
      drop = "";
      addIndex(new Index(name, part.trim(), drop));
    }
  }

  /**
   * Adds the rest of the indices that were not a part of the initial create
   * statement.
   *
   * @param parts The parts that make up the table's create statement(s).
   */
  private void addRemainingIndices(final String[] parts) {
    String name;
    String drop;
    for (int i = 1; i < parts.length; i++) {
      String part = parts[i];
      name = part.substring(part.indexOf("INDEX ") + 6, part.indexOf(" ON"));
      drop = "DROP INDEX " + name + ";";
      addIndex(new Index(name, part.replace(";", "").trim(), drop));
    }
  }

  @Override
  protected List<String> dropCols(Map<String, Column> cols1, Map<String, Column> cols2) {
    List<String> emptyList = new ArrayList<>();

    for (String columnName : cols2.keySet()) {
      if (!cols1.containsKey(columnName)) {
        stopCompare = true;
        break;
      }
    }

    return emptyList;
  }

  @Override
  protected List<String> otherCols(Map<String, Column> cols1, Map<String, Column> cols2) {
    List<String> sql = new ArrayList<>();
    Column col;
    Column col2;

    for (Map.Entry<String, Column> columnInfo : cols1.entrySet()) {
      col = columnInfo.getValue();
      if (!cols2.containsKey(col.getName())) {
        sql.add("ALTER TABLE " + name + " ADD COLUMN " + col.getName() + " " + col.getDetails() + ";");
      } else {
        col2 = cols2.get(col.getName());
        if (col.getName().equals(col2.getName()) && !col.getDetails().equals(col2.getDetails())) {
          stopCompare = true;
          break;
        }
      }
    }

    return sql;
  }

  @Override
  protected List<String> dropIndices(Map<String, Index> dev, Map<String, Index> live) {
    List<String> sql = new ArrayList<>();

    for (Map.Entry<String, Index> indexInfo : live.entrySet()) {
      if (!dev.containsKey(indexInfo.getKey())) {
        if (indexInfo.getValue().getCreateStatement().contains(FOREIGN_KEY_IDENTIFIER)) {
          stopCompare = true;
          break;
        }

        sql.add(indexInfo.getValue().getDrop());
      }
    }

    return sql;
  }

  @Override
  protected List<String> otherIndices(Map<String, Index> dev, Map<String, Index> live) {
    List<String> sql = new ArrayList<>();
    Index index;

    for (Map.Entry<String, Index> indexInfo : dev.entrySet()) {
      index = indexInfo.getValue();
      if (live.containsKey(indexInfo.getKey())) {
        if (!index.equals(live.get(indexInfo.getKey()))) {
          if (live.get(indexInfo.getKey()).getCreateStatement().contains(FOREIGN_KEY_IDENTIFIER)) {
            stopCompare = true;
            break;
          }
          sql.add(live.get(indexInfo.getKey()).getDrop() + newLineCreation + index.getCreateStatement() + ";");
        }
      } else {
        if (index.getCreateStatement().contains(FOREIGN_KEY_IDENTIFIER)) {
          stopCompare = true;
          break;
        }
        sql.add(index.getCreateStatement() + ";");
      }
    }
    return sql;
  }

  /**
   * Recreates the structure of the table using the columns provided to copy over
   * old data into the new table based on common columns between the development
   * and live tables.
   *
   * @param live A list of columns and their definitions which helps the transfer
   *             of data for common columns.
   * @return The SQL statements needed to recreate the development table.
   */
  private List<String> recreateTable(Map<String, Column> live) {
    StringBuilder commonColumns = new StringBuilder();
    boolean hasExtraParts = createStatement.lastIndexOf(MULTIPLE_CREATE_INDICATOR) > 6;
    List<String> sql = new ArrayList<>();
    for (String columnName : live.keySet()) {
      if (columns.containsKey(columnName)) {
        commonColumns.append(columnName + ",");
      }
    }
    if (commonColumns.length() != 0) {
      // there are columns in common so the table needs to be renamed,
      // have its data copied into a new table of the same name, and then be deleted
      commonColumns = new StringBuilder(commonColumns.substring(0, commonColumns.length() - 1));
      sql.add("ALTER TABLE " + name + " RENAME TO temp_table;");
      if (!hasExtraParts) {
        sql.add(createStatement);
      } else {
        sql.add(createStatement.substring(0, createStatement.indexOf(MULTIPLE_CREATE_INDICATOR, 6) - 1));
      }
      sql.add("INSERT INTO " + name + " (" + commonColumns + ")\n  SELECT " + commonColumns + "\n  FROM temp_table;");
      sql.add("DROP TABLE temp_table;");
      if (hasExtraParts) {
        sql.add(createStatement.substring(createStatement.indexOf(MULTIPLE_CREATE_INDICATOR, 6)));
      }
    } else {
      sql.add(drop);
      sql.add(createStatement);
    }
    return sql;
  }
}
