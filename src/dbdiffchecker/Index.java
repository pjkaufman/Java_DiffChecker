package dbdiffchecker;

/**
 * Index resembles an SQL index and contains index info.
 * @author Peter Kaufman
 * @version 5-21-19
 * @since 9-12-17
 */
public class Index extends Schema {

  private String column = "";

  /**
   * Initializes an Index object by setting the name create statement, and columns of the index.
   * @author Peter Kaufman
   * @param name The name of the index.
   * @param create The create statement of the index.
   * @param column The column of the index.
   */
  public Index(String name, String create, String column) {
    this.name = name;
    this.createStatement = create;
    this.column = column;
  }

  /**
   * This is the default constructor for this class, <b> Needed for Serialization</b>.
   */
  public Index() {}

  /**
   * Returns the name of the column or columns of the index. <b>Note: the 
   * column name(s) has/have already been formatted to work in SQL statements.</b>
   * @author Peter Kaufman
   * @return The name(s) of columns that the index is on.
   */
  public String getColumn() {

    return this.column;
  }

  /**
   * Determines whether or not the indexes are the same. 
   * @author Peter Kaufman
   * @param index An index with the same name as the current Index object.
   * @return Whether or not the index needs to be changed.
   */
  public boolean sameDetails(Index index) {

    return this.createStatement.equals(index.createStatement) && this.column.equals(index.column);
  }
}
