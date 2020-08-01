package dbdiffchecker.sql;

/**
 * Resembles a view in an SQL database and contains view info.
 *
 * @author Peter Kaufman
 */
public class View extends Schema {
  private static final long serialVersionUID = 1L;

  /**
   * Sets the name, create statement, and drop statement of the view.
   *
   * @param name   The name of the view.
   * @param create The create statement of the view.
   */
  public View(String name, String create) {
    createStatement = create + ";";
    this.name = name;
    drop = "DROP VIEW `" + name + "`;";
  }

  /**
   * <b>Needed for Serialization</b>
   */
  public View() {
  }
}
