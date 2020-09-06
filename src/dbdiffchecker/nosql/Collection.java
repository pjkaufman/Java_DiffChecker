package dbdiffchecker.nosql;

import java.io.Serializable;

/**
 * Models a collection in a Mongo database by keeping track of specific
 * properties of collections.
 *
 * @author Peter Kaufman
 */
public class Collection implements Serializable {
  private static final long serialVersionUID = 1L;
  private String name;
  private boolean isCapped;
  private int size;

  /**
   * Initializes a collection using the name provided and the size as well as
   * marking whether it is capped.
   *
   * @param name     The name of the collection.
   * @param isCapped Whether or not the collection has a max size.
   * @param size     The size of the collection.
   */
  public Collection(String name, boolean isCapped, int size) {
    this.name = name;
    this.isCapped = isCapped;
    this.size = size;
  }

  /**
   * <b>Needed for Serialization</b>
   */
  public Collection() {
  }

  /**
   * Returns whether the collection is capped or not.
   *
   * @return Whether or not the collection is capped.
   */
  public boolean isCapped() {
    return isCapped;
  }

  /**
   * Returns the name of the collection.
   *
   * @return The name of the collection.
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the max size of the collection.
   *
   * @return The max size of the collection.
   */
  public int getSize() {
    return size;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Collection) {
      Collection coll = (Collection) obj;
      return name.equals(coll.getName()) && isCapped == coll.isCapped() && size == coll.getSize();
    }

    return false;
  }
}