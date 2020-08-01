package dbdiffchecker.sql;

/**
 * Resembles a generic index and contains index info.
 *
 * @author Peter Kaufman
 */
public class Index extends Schema {
  private static final long serialVersionUID = 1L;

  /**
   * Sets the name and create statement of the index.
   *
   * @param name   The name of the index.
   * @param create The create statement of the index.
   * @param drop   The drop statment of the index;
   */
  public Index(String name, String create, String drop) {
    this.name = name;
    createStatement = create;
    this.drop = drop;
  }

  /**
   * <b>Needed for Serialization</b>
   */
  public Index() {
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof Index && createStatement.equals(((Index) obj).createStatement);
  }
}
