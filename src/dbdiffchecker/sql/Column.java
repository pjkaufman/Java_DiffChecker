package dbdiffchecker.sql;

/**
 * Resembles an SQL column and contains column info.
 *
 * @author Peter Kaufman
 * @version 5-30-19
 * @since 9-10-17
 */
public class Column extends Schema {
  private String details = "";

  /**
   * Sets the name and details of the column.
   *
   * @author Peter Kaufman
   * @param name    the name of the column
   * @param details the info of the column
   */
  public Column(String name, String details) {
    this.name = name;
    this.details = details;
    this.drop = "DROP COLUMN `" + name + "`";
  }

  /**
   * This is the default constructor for this class, <b>Needed for
   * Serialization</b>.
   */
  public Column() {
  }

  /**
   * Returns the column's details.
   *
   * @author Peter Kaufman
   * @return Details about the column.
   */
  public String getDetails() {
    return this.details;
  }
}
