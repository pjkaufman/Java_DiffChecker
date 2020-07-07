package dbdiffchecker;

public enum DatabaseType {
  NONE(0), MYSQL(1), SQLITE(2), COUCHBASE(3), MONGODB(4);

  private static final String[] databaseDropdownOptions = { "Select Database Type", "MySQL", "SQLite", "Couchbase",
      "MongoDB" };
  private static final String[][] databaseInputs = new String[][] {
      new String[] { "Username", "Password", "Host", "Port", "Database Name" },
      new String[] { "Database Path", "Database Name" },
      new String[] { "Username", "Password", "Host", "Database Name" },
      new String[] { "Username", "Password", "Host", "Port", "Database Name" } };
  int value;

  private DatabaseType(int type) {
    this.value = type;
  }

  public int getValue() {
    return value;
  }

  public static String getType(int databaseType) {
    return databaseDropdownOptions[databaseType];
  }

  public static String[] getInputs(int databaseType) {
    return databaseInputs[databaseType - 1];
  }

  public static String[] getDropdownOptions() {
    return databaseDropdownOptions;
  }
}