package dbdiffchecker;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Resembles a table in an SQL database and contains info about the table's
 * columns and indices.
 * @author Peter Kaufman
 * @version 5-23-19
 * @since 9-10-17
 */
public abstract class Table extends Schema {
  // Instance variables
  protected int count = 0;
  protected HashMap<String, Column> columns = new HashMap<>();
  protected HashMap<String, Index> indices = new HashMap<>();

  /**
   * Sets the name and create statement of the table.
   * @author Peter Kaufman
   * @param name The name of the table.
   * @param create he create statement of the table which will be used to create
   *        its columns and indices.
   */
  public Table(String name, String create) {
    this.name = name;
    this.createStatement = create + ";";
    parseCreateStatement();
  }

  /**
   * This is the default constructor for this class, <b> Needed for
   * Serialization</b>.
   */
  public Table() {}

  /**
   * Returns the column list where the column name is the key.
   * @author Peter Kaufman
   * @return The table's columns.
   */
  public HashMap<String, Column> getColumns() {
    return this.columns;
  }

  /**
   * Returns the index list where the index name is the key.
   * @author Peter Kaufman
   * @return The index names and index data.
   */
  public HashMap<String, Index> getIndices() {
    return this.indices;
  }

  /**
   * Adds a column to the column list.
   * @author Peter Kaufman
   * @param col A Ccolumn to be added to the column list.
   */
  public void addColumn(Column col) {
    this.columns.put(col.getName(), col);
  }

  /**
   * Adds an index to the index list
   * @author Peter Kaufman
   * @param index An index to be added to the index list.
   */
  public void addIndex(Index index) {
    this.indices.put(index.getName(), index);
  }

  /**
   * Takes in a Table and compares it to the current one, the result is SQL
   * statements to make them the same.
   * @author Peter Kaufman
   * @param t1 A Table object which is being compared to this Table object.
   * @return The SQL needed to make the tables the same.
   */
  public abstract ArrayList<String> equals(Table t1);

  /**
   * Parses the create statment of the table picking up columns and indices that
   * need to be added.
   * @author Peter Kaufman
   */
  protected abstract void parseCreateStatement();

  /**
   * Takes column lists and returns part of an SQL statement that drops a column
   * or several columns.
   * @author Peter Kaufman
   * @param cols1 The column names and column data of the current table.
   * @param cols2 The column names and column data of a different table of the
   *        same name.
   * @return Part of an SQL statement that drops a column or several columns.
   */
  abstract protected String dropCols(HashMap<String, Column> cols1, HashMap<String, Column> cols2);

  /**
   * Takes two column lists and returns part of an SQL statement that modifies
   * and/or adds columns.
   * @author Peter Kaufman
   * @param cols1 The column names and column data of the current table.
   * @param cols2 The column names and column data of a different table of the
   *        same name.
   * @return Part of an SQL statement that modifies and,or adds columns.
   */
  abstract protected String otherCols(HashMap<String, Column> cols1, HashMap<String, Column> cols2);

  /**
   * Takes two index lists and returns part of an SQL statement that drop indexes.
   * @author Peter Kaufman
   * @param dev The index names and index data of the current table.
   * @param live The index names and index data of a different table of the same
   *        name.
   * @return Part of an SQL statement that drops indexes.
   */
  abstract protected String dropIndices(HashMap<String, Index> dev, HashMap<String, Index> live);

  /**
   * Takes two lists of indices and returns part of an SQL statement that either
   * adds or drops and adds indices.
   * @author Peter Kaufman
   * @param dev The index names and index data of the current table.
   * @param live The index names and index data of a different table of the same
   *        name.
   * @return Part of an SQL statement that either adds or drops and adds indexes.
   */
  abstract protected String otherIndices(HashMap<String, Index> dev, HashMap<String, Index> live);
}
