package dbdiffchecker.sql;

/**
 * Resembles a SQL column and contains column info.
 *
 * @author Peter Kaufman
 */
public class Column extends Schema {
  private static final long serialVersionUID = 1L;
  private String details;

  /**
   * Sets the name and details of the column.
   *
   * @param name    The name of the column
   * @param details The info of the column
   */
  public Column(String name, String details) {
    this.name = name;
    this.details = details;
    drop = "DROP COLUMN `" + name + "`";
  }

  /**
   * <b>Needed for Serialization</b>
   */
  public Column() {
  }

  /**
   * Returns the column's details.
   *
   * @return Details about the column.
   */
  public String getDetails() {
    return details;
  }
}
