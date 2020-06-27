package dbdiffchecker;

public enum DatabaseType {
  NONE(0), MYSQL(1), SQLITE(2), COUCHBASE(3), MONGODB(4);

  private final String[] databaseDropdownOptions = { "Select Database Type", "MySQL", "SQLite", "Couchbase",
      "MongoDB" };
  private final String[][] databaseInputs = new String[][] {
      new String[] { "Username", "Password", "Host", "Port", "Database Name" },
      new String[] { "Database Path", "Database Name" },
      new String[] { "Username", "Password", "Host", "Database Name" },
      new String[] { "Username", "Password", "Host", "Port", "Database Name" } };
  String dbType;
  int value;

  DatabaseType(int type) {
    this.value = type;
    this.dbType = databaseDropdownOptions[type];
  }

  public String getType() {
    return dbType;
  }

  public int getValue() {
    return value;
  }

  public String[] getDropdownOptions() {
    return databaseDropdownOptions;
  }

  public String[] getInputs() {
    return databaseInputs[value - 1];
  }
}