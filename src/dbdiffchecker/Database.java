package dbdiffchecker;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * The model for the functions that all database representations should contain.
 * @author Peter Kaufman
 * @version 5-24-19
 * @since 5-24-19
 */
public abstract class Database implements Serializable {

  public abstract ArrayList<String> compare(Database liveDatabase);
}
