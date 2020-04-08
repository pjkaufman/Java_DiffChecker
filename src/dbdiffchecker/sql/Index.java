package dbdiffchecker.sql;

/**
 * Resembles an generic index and contains index info.
 * 
 * @author Peter Kaufman
 * @version 5-30-19
 * @since 9-12-17
 */
public class Index extends Schema {
  /**
   * Sets the name and create statement of the index.
   * 
   * @author Peter Kaufman
   * @param name   The name of the index.
   * @param create The create statement of the index.
   * @param drop   The drop statment of the index;
   */
  public Index(String name, String create, String drop) {
    this.name = name;
    this.createStatement = create;
    this.drop = drop;
  }

  /**
   * This is the default constructor for this class, <b> Needed for
   * Serialization</b>.
   */
  public Index() {
  }

  /**
   * Determines whether or not the indexes are the same by comparing their create
   * statements.
   * 
   * @author Peter Kaufman
   * @param index An index with the same name as the current index.
   * @return Whether or not the indices are the same.
   */
  public boolean equals(Index index) {
    return this.createStatement.equals(index.createStatement);
  }
}
