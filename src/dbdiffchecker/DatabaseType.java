package dbdiffchecker;

/**
 * Deals with all database types and their related data.
 *
 * @author Peter Kaufman
 */
public enum DatabaseType {
  NONE(0), MYSQL(1), SQLITE(2), COUCHBASE(3), MONGODB(4);

  private static final String[] databaseDropdownOptions = { "Select Database Type", "MySQL", "SQLite", "Couchbase",
      "MongoDB" };
  private static final String USERNAME = "Username";
  private static final String PASSWORD = "Password";
  private static final String DB_NAME = "Database Name";
  private static final String HOST = "Host";
  private static final String PORT = "Port";
  private static final String[][] databaseInputs = new String[][] {
      new String[] { USERNAME, PASSWORD, HOST, PORT, DB_NAME }, new String[] { "Database Path", DB_NAME },
      new String[] { USERNAME, PASSWORD, HOST, "Bucket Name" },
      new String[] { USERNAME, PASSWORD, HOST, PORT, DB_NAME } };
  int value;

  /**
   * Initializes the database type.
   *
   * @param type The integer representation of the database type.
   */
  private DatabaseType(int type) {
    value = type;
  }

  /**
   * Returns the value associated witht the database type.
   *
   * @return The integer representation of the database type.
   */
  public int getValue() {
    return value;
  }

  /**
   * Returns the name of the integer representation of a database type.
   *
   * @param databaseType The integer representation of a database type.
   * @return The name of the database type.
   */
  public static String getType(int databaseType) {
    return databaseDropdownOptions[databaseType];
  }

  /**
   * Returns the list of inputs to associated with the database type.
   *
   * @param databaseType The integer representation of a database type.
   * @return The list of inputs to associated with the database type.
   */
  public static String[] getInputs(int databaseType) {
    return databaseInputs[databaseType - 1];
  }

  /**
   * Returns the list of database types.
   *
   * @return The list of database types.
   */
  public static String[] getDropdownOptions() {
    return databaseDropdownOptions;
  }
}