package dbdiffchecker;

/**
 * Column resembles a column in MySQL and contains column info.
 * @author Peter Kaufman
 * @version 5-11-19
 * @since 9-10-17
 */
public class Column extends Schema {

  private String details = "";

  /**
   * Initializes a column object by setting its name and details.
   * @author Peter Kaufman
   * @param name the name of the column
   * @param details the info of the column
   */
  public Column(String name, String details) {

    this.name = name;
    this.details = details;
  }

  /**
   * This is the default constructor for this class, <b> Needed for Serialization</b>.
   */
  public Column() {}

  /**
   * Returns the column detailts.
   * @author Peter Kaufman
   * @return Info about the column.
   */
  public String getDetails() {

    return this.details;
  }
}
