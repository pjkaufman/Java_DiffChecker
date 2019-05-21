package dbdiffchecker;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Table resembles a table in an SQL database and contains info about the table's 
 * columns and indices.
 * @author Peter Kaufman
 * @version 5-21-19
 * @since 9-10-17
 */
public abstract class Table extends Schema {

  protected String charSet = "";
  protected String collation = "";
  protected String autoIncrement = "";
  protected int count = 0;
  protected HashMap<String, Column> columns = new HashMap<>();
  protected HashMap<String, Index> indices = new HashMap<>();
  protected static final long serialVersionUID = 1L;

  /**
   * Initializes a Table object with a name create statement.
   * @author Peter Kaufman
   * @param name The name of the table.
   * @param create The create statement of the table info to create its columns.
   */
  public Table(String name, String create) {
    this.name = name;
    this.createStatement = create + ";";
    parseCreateStatement();
  }

  /**
   * This is the default constructor for this class, <b> Needed for Serialization</b>.
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
  public abstract ArrayList<String> equals(Table t1);

  /**
   * Parses the create statment of the table picking up columns and indices 
   * that need to be added.
   * @author Peter Kaufman
   */
  protected abstract void parseCreateStatement();
}
