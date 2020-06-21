package dbdiffchecker.sql;

/**
 * Resembles a view in an SQL database and contains view info.
 *
 * @author Peter Kaufman
 * @version 6-20-20
 * @since 9-15-17
 */
public class View extends Schema {
  private String drop = "";

  /**
   * Sets the name, create statement, and drop statement of the view.
   *
   * @param name   The name of the view.
   * @param create The create statement of the view.
   */
  public View(String name, String create) {
    this.createStatement = create + ";";
    this.name = name;
    this.drop = "DROP VIEW `" + name + "`;";
  }

  /**
   * The default constructor for this class is needed for serialization.
   */
  public View() {
  }

  /**
   * Returns the SQL drop statement for the view.
   *
   * @return The SQL drop statement for the view.
   */
  public String getDrop() {
    return this.drop;
  }
}
