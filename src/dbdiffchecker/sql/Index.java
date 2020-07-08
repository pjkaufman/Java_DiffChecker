package dbdiffchecker.sql;

/**
 * Resembles a generic index and contains index info.
 *
 * @author Peter Kaufman
 * @version 7-6-20
 * @since 9-12-17
 */
public class Index extends Schema {
  /**
   * Sets the name and create statement of the index.
   *
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

  @Override
  public boolean equals(Object index) {
    if (!(index instanceof Index)) {
      return false;
    }
    return this.createStatement.equals(((Index) index).createStatement);
  }
}
