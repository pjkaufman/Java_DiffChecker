package dbdiffchecker;

/**
 * Schema holds common methods and instance variables for Schema subclasses.
 * @author Peter Kaufman
 * @version 5-24-18
 * @since 5-17-18
 */
public class Schema {
  
  // Defuat instance variables
  protected String name = "";
  protected String createStatement = "";

  /**
   * This is the default constructor Schema, it initializes the object. 
   */
  public Schema () {}

  /**
   * getName returns the name of the Schema object.
   * @author Peter Kaufman
   * @return name is a String which is the name of the Schema object.
   */
  protected String getName() {

    return this.name;
  }

  /**
   * getCreateStatement returns the create statement of the Schema object.
   * @author Peter Kaufman
   * @return create is a String which is the create statement of the of the Schema object.
   */
  protected String getCreateStatement() {

    return this.createStatement;
  }
}
