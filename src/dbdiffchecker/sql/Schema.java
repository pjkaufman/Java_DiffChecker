package dbdiffchecker.sql;

import java.io.Serializable;

/**
 * Holds common methods and instance variables for Schema subclasses.
 *
 * @author Peter Kaufman
 */
public class Schema implements Serializable {
  protected static final long serialVersionUID = 1L;
  protected String name;
  protected String drop;
  protected String createStatement;

  /**
   * <b>Needed for Serialization</b>
   */
  public Schema() {
  }

  /**
   * Returns the name of the Schema object.
   *
   * @return The name of the Schema object.
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the create statement of the Schema object.
   *
   * @return The create statement of the of the Schema object.
   */
  public String getCreateStatement() {
    return createStatement;
  }

  /**
   * Returns the drop statement of the Schema object.
   *
   * @return The drop statement of the of the Schema object.
   */
  public String getDrop() {
    return drop;
  }
}
