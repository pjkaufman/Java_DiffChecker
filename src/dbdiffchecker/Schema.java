package dbdiffchecker;

import java.io.Serializable;

/**
 * Schema holds common methods and instance variables for Schema subclasses.
 * Program Name: Database Difference Checker
 * CSCI Course: 325
 * Grade Received: Pass
 * @author Peter Kaufman
 * @version 2-16-19
 * @since 5-17-18
 */
public class Schema implements Serializable {
  
  // Defuat instance variables
  private static final long serialVersionUID = 1L;
  protected String name = "";
  protected String createStatement = "";

  /**
   * The default constructor is needed for serialization.
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
