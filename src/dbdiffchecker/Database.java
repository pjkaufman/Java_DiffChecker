package dbdiffchecker;

import java.io.Serializable;
import java.util.List;

/**
 * The model for the functions that all database representations should contain.
 *
 * @author Peter Kaufman
 * @version 7-6-20
 * @since 5-24-19
 */
public abstract class Database implements Serializable {
  protected static final long serialVersionUID = 1L;

  /**
   * <b>Needed for Serialization</b>
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
  public abstract List<String> compare(Database liveDatabase);
}
