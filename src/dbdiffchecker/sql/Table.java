package dbdiffchecker.sql;

import java.util.Map;
import java.util.List;
import java.util.HashMap;

/**
 * Resembles a table in an SQL database and contains info about the table's
 * columns and indices.
 *
 * @author Peter Kaufman
 */
public abstract class Table extends Schema {
  private static final long serialVersionUID = 1L;
  protected boolean isFirstStatement = true;
  protected String newLineCreation;
  protected Map<String, Column> columns = new HashMap<>();
  protected Map<String, Index> indices = new HashMap<>();

  /**
   * Sets the name and create statement of the table.
   *
   * @param name   The name of the table.
   * @param create The create statement of the table which will be used to create
   *               its columns and indices.
   */
  public Table(String name, String create) {
    this.name = name;
    createStatement = create + ";";
    parseCreateStatement();
  }

  /**
   * <b>Needed for Serialization</b>
   */
  public Table() {
  }

  /**
   * Returns the column list where the column name is the key.
   *
   * @return The table's columns.
   */
  public Map<String, Column> getColumns() {
    return columns;
  }

  /**
   * Returns the index list where the index name is the key.
   *
   * @return The index names and index data.
   */
  public Map<String, Index> getIndices() {
    return indices;
  }

  /**
   * Adds a column to the column list.
   *
   * @param col A column to be added to the column list.
   */
  public void addColumn(Column col) {
    columns.put(col.getName(), col);
  }

  /**
   * Adds an index to the index list.
   *
   * @param index An index to be added to the index list.
   */
  public void addIndex(Index index) {
    indices.put(index.getName(), index);
  }

  /**
   * Takes in a table and compares it to the current one. The result is SQL
   * statements to make them the same.
   *
   * @param t1 A table object which is being compared to this table object.
   * @return The SQL needed to make the tables the same.
   */
  public abstract List<String> generateStatements(Table t1);

  /**
   * Appends the SQL addition after adding the appropriate line ending if needed.
   *
   * @param sql         The SQL statment to append to.
   * @param sqlAddition The SQL to append to the statement.
   */
  protected void appendSQLPart(StringBuilder sql, String sqlAddition) {
    if (!isFirstStatement) {
      sql.append(newLineCreation);
    }
    sql.append(sqlAddition);
    isFirstStatement = false;

  }

  /**
   * Parses the create statement of the table picking up columns and indices that
   * need to be added.
   */
  protected abstract void parseCreateStatement();

  /**
   * Takes column lists and returns part of an SQL statement that drops a column
   * or several columns.
   *
   * @param cols1 The column names and column data of the current table.
   * @param cols2 The column names and column data of a different table of the
   *              same name.
   * @return Part of an SQL statement that drops a column or several columns.
   */
  protected abstract String dropCols(Map<String, Column> cols1, Map<String, Column> cols2);

  /**
   * Takes two column lists and returns part of an SQL statement that modifies
   * and/or adds columns.
   *
   * @param cols1 The column names and column data of the current table.
   * @param cols2 The column names and column data of a different table of the
   *              same name.
   * @return Part of an SQL statement that modifies and/or adds columns.
   */
  protected abstract String otherCols(Map<String, Column> cols1, Map<String, Column> cols2);

  /**
   * Takes two index lists and returns part of an SQL statement that drop indices.
   *
   * @param dev  The index names and index data of the current table.
   * @param live The index names and index data of a different table of the same
   *             name.
   * @return Part of an SQL statement that drops indices.
   */
  protected abstract String dropIndices(Map<String, Index> dev, Map<String, Index> live);

  /**
   * Takes two lists of indices and returns part of an SQL statement that either
   * adds or drops and adds indices.
   *
   * @param dev  The index names and index data of the current table.
   * @param live The index names and index data of a different table of the same
   *             name.
   * @return Part of an SQL statement that either adds or drops and adds indices.
   */
  protected abstract String otherIndices(Map<String, Index> dev, Map<String, Index> live);
}
