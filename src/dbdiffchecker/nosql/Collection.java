package dbdiffchecker.nosql;

import java.io.Serializable;

/**
 * Models a Collection in a Mongo database by keeping track of specific
 * properties of a collections.
 * 
 * @author Peter Kaufman
 * @version 3-9-20
 * @since 10-26-19
 */
public class Collection implements Serializable {
  private String name;
  private boolean isCapped;
  private int size;

  public Collection(String name, boolean isCapped, int size) {
    this.name = name;
    this.isCapped = isCapped;
    this.size = size;
  }

  /**
   * This is the default constructor for this class, <b>Needed for
   * Serialization</b>.
   */
  public Collection() {
  }

  /**
   * Returns whether the collection is capped or not.
   * 
   * @author Peter Kaufman
   * @return Whether or not the collection is capped.
   */
  public boolean isCapped() {
    return isCapped;
  }

  /**
   * Returns the name of the collection.
   * 
   * @author Peter Kaufman
   * @return The name of the collection.
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the max size of the collection.
   * 
   * @author Peter Kaufman
   * @return The max size of the collection.
   */
  public int getSize() {
    return size;
  }

  /**
   * Determines wheteher two collections are equal.
   * 
   * @param coll2 The collection to compare the current collection to.
   * @return Whether or not the two collections are the same.
   */
  public boolean equals(Collection coll2) {
    return name.equals(coll2.getName()) && isCapped == coll2.isCapped() && size == coll2.getSize();
  }
}