package dbdiffchecker;

/**
 * View resembles a view in MySQL and contains view info.
 * Program Name: Database Difference Checker
 * CSCI Course: 325
 * Grade Received: Pass
 * @author Peter Kaufman
 * @version 2-16-19
 * @since 9-15-17
 */
public class View extends Schema {
  // Instance variables
  private String drop = "";

  /**
   * Initializes a View object using a name and create statement.
   * @param name The name of the view.
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
  public View() {}

  /**
   * Returns the SQL drop statement for the view.
   * @author Peter Kaufman
   * @return The SQL drop statement for the view.
   */
  public String getDrop() {

    return this.drop;
  }
}
