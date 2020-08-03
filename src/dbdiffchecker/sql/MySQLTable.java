package dbdiffchecker.sql;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Resembles a MySQL table and contains info about the table's columns and
 * indices.
 *
 * @author Peter Kaufman
 */
public class MySQLTable extends Table {
  private static final long serialVersionUID = 1L;
  private String charSet;
  private String collation = "";
  private String autoIncrement;

  /**
   * Sets the name and create statement of the table.
   *
   * @param name   The name of the table.
   * @param create The create statement of the table which will be used to create
   *               its columns and indices.
   */
  public MySQLTable(String name, String create) {
    super(name, create);
    drop = "DROP TABLE `" + name + "`;";
    String temp = create.substring(create.indexOf("DEFAULT CHARSET=") + 16) + " ";
    charSet = temp.substring(0, temp.indexOf(" "));
    newLineCreation = ", \n\t";
  }

  /**
   * <b>Needed for Serialization</b>
   */
  public MySQLTable() {
  }

  /**
   * Returns the collation of the table.
   *
   * @return The collation of the table.
   */
  public String getCollation() {
    return collation;
  }

  /**
   * Returns the character set of the table.
   *
   * @return The character set of the table.
   */
  public String getCharSet() {
    return charSet;
  }

  /**
   * Returns the auto increment count of the table.
   *
   * @return The auto increment count of the table.
   */
  public String getAutoIncrement() {
    return autoIncrement;
  }

  /**
   * Sets the collation of the table.
   *
   * @param collation The collation of the table.
   */
  public void setCollation(String collation) {
    this.collation = collation;
  }

  /**
   * Sets the character set of the table.
   *
   * @param charSet The character set of the table.
   */
  public void setCharSet(String charSet) {
    this.charSet = charSet;
  }

  /**
   * Sets the auto increment count of the table.
   *
   * @param autoIncrement The auto increment count of the table.
   */
  public void setAutoIncrement(String autoIncrement) {
    this.autoIncrement = autoIncrement;
  }

  @Override
  public List<String> generateStatements(Table t1) {
    List<String> sql = new ArrayList<>();
    isFirstStatement = true;
    String sql2 = "ALTER TABLE `" + name + "` ";
    if (!charSet.equals(((MySQLTable) t1).charSet) || !collation.equals(((MySQLTable) t1).collation)) {
      sql2 += "CHARACTER SET " + charSet;
      if (!collation.equals("")) {
        sql2 += " COLLATE " + collation;
      }
      isFirstStatement = false;
    }
    sql2 += dropIndices(indices, t1.getIndices());
    sql2 += otherCols(columns, t1.getColumns());
    sql2 += dropCols(columns, t1.getColumns());
    sql2 += otherIndices(indices, t1.getIndices()) + ";";
    if (!isFirstStatement) {
      sql.add(sql2);
    }
    return sql;
  }

  @Override
  protected void parseCreateStatement() {
    String[] parts;
    String columnIndicator = "`";
    String indexIndicator = "KEY";
    String name = "";
    String drop = "";
    String last = "";
    String details = "";
    String create = "";
    parts = createStatement.split("\n");
    for (String part : parts) {
      part = part.trim();
      if (part.endsWith(",")) {
        part = part.substring(0, part.length() - 1);
      }
      if (part.startsWith(columnIndicator)) {
        name = part.substring(part.indexOf("`") + 1, part.lastIndexOf("`"));
        details = part.substring(part.lastIndexOf("`") + 2) + last;
        addColumn(new Column(name, details));
        last = " AFTER `" + name + "`";
      } else if (part.contains(indexIndicator)) {
        create = "ADD " + part;
        if (part.contains("PRIMARY KEY")) {
          name = "PRIMARY";
          drop = "DROP PRIMARY KEY";
        } else if (part.contains("FOREIGN KEY")) {
          name = part.substring(part.indexOf("`") + 1, part.indexOf("`", part.indexOf("`") + 1));
          drop = "DROP FOREIGN KEY `" + name + "`";
        } else {
          name = part.substring(part.indexOf("`") + 1, part.indexOf("`", part.indexOf("`") + 1));
          drop = "DROP INDEX `" + name + "`";
          create = create.replace("KEY", "INDEX");
        }
        addIndex(new Index(name, create, drop));
      }
    }
  }

  @Override
  protected String dropCols(Map<String, Column> cols1, Map<String, Column> cols2) {
    StringBuilder sql = new StringBuilder();
    for (Map.Entry<String, Column> columnInfo : cols2.entrySet()) {
      if (!cols1.containsKey(columnInfo.getKey())) {
        appendSQLPart(sql, columnInfo.getValue().getDrop());
      }
    }
    return sql.toString();
  }

  @Override
  protected String otherCols(Map<String, Column> cols1, Map<String, Column> cols2) {
    StringBuilder sql = new StringBuilder();
    Column col;
    Column col2;
    for (Map.Entry<String, Column> columnInfo : cols1.entrySet()) {
      col = columnInfo.getValue();
      if (!cols2.containsKey(columnInfo.getKey())) {
        appendSQLPart(sql, "ADD COLUMN `" + col.getName() + "` " + col.getDetails());
      } else {
        col2 = cols2.get(columnInfo.getKey());
        if (col.getName().equals(col2.getName()) && !col.getDetails().equals(col2.getDetails())) {
          appendSQLPart(sql, "MODIFY COLUMN `" + col.getName() + "` " + col.getDetails());
        }
      }
    }
    return sql.toString();
  }

  @Override
  protected String dropIndices(Map<String, Index> dev, Map<String, Index> live) {
    StringBuilder sql = new StringBuilder();
    for (Map.Entry<String, Index> indexInfo : live.entrySet()) {
      if (!dev.containsKey(indexInfo.getKey())) {
        appendSQLPart(sql, indexInfo.getValue().getDrop());
      }
    }
    return sql.toString();
  }

  @Override
  protected String otherIndices(Map<String, Index> dev, Map<String, Index> live) {
    StringBuilder sql = new StringBuilder();
    Index index;
    for (Map.Entry<String, Index> indexInfo : dev.entrySet()) {
      index = indexInfo.getValue();
      if (live.containsKey(indexInfo.getKey())) {
        if (!index.equals(live.get(indexInfo.getKey()))) {
          appendSQLPart(sql, index.getDrop() + ", \n\t" + index.getCreateStatement());
        }
      } else {
        appendSQLPart(sql, index.getCreateStatement());
      }
    }
    return sql.toString();
  }
}
