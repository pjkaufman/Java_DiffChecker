package dbdiffchecker;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * The model for the functions that all database representations should contain.
 * 
 * @author Peter Kaufman
 * @version 5-30-19
 * @since 5-24-19
 */
public abstract class Database implements Serializable {
  // Instance variables
  protected static final long serialVersionUID = 1L;

  /**
   * This is the default constructor for this class, <b>Needed for
   * Serialization</b>.
   */
  public Database() {
  }

  /**
   * Compares two database and generates the statements of the appropriate query
   * language in order to make the two databases the same.
   * 
   * @param liveDatabase The live database model.
   * @return The statements needed to make the two databases the same.
   */
  public abstract ArrayList<String> compare(Database liveDatabase);
}
