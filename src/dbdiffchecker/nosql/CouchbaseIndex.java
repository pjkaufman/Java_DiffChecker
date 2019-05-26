package dbdiffchecker.nosql;

import dbdiffchecker.sql.Schema;

/**
 * Resembles an Couchbase index and contains index info.
 * @author Peter Kaufman
 * @version 5-24-19
 * @since 5-23-19
 */
public class CouchbaseIndex extends Schema {
  /**
   * Sets the name and create statement of the index.
   * @author Peter Kaufman
   * @param name The name of the index.
   * @param create The create statement of the index.
   */
  public CouchbaseIndex(String name, String create) {
    this.name = name;
    this.createStatement = create;
  }

  /**
   * This is the default constructor for this class, <b> Needed for
   * Serialization</b>.
   */
  public CouchbaseIndex() {}

  /**
   * Determines whether or not the indexes are the same by comparing their create
   * statements.
   * @author Peter Kaufman
   * @param index An index with the same name as the current index.
   * @return Whether or not the indices are the same.
   */
  public boolean equals(CouchbaseIndex index) {
    return this.createStatement.equals(index.createStatement);
  }
}
