package dbdiffchecker.sql;

import java.io.Serializable;

/**
 * Holds common methods and instance variables for Schema subclasses.
 * @author Peter Kaufman
 * @version 5-30-19
 * @since 5-17-18
 */
public class Schema implements Serializable {
  // Defuat instance variables
  protected static final long serialVersionUID = 1L;
  protected String name = "";
  protected String createStatement = "";

  /**
   * This is the default constructor for this class, <b> Needed for
   * Serialization</b>.
   */
  public Schema() {}

  /**
   * Returns the name of the Schema object.
   * @author Peter Kaufman
   * @return The name of the Schema object.
   */
  public String getName() {
    return this.name;
  }

  /**
   * Returns the create statement of the Schema object.
   * @author Peter Kaufman
   * @return The create statement of the of the Schema object.
   */
  public String getCreateStatement() {
    return this.createStatement;
  }
}
