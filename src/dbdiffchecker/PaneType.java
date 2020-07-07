package dbdiffchecker;

public enum PaneType {
  COMPARE_WITH_DB(0), COMPARE_WITH_SNAPSHOT(1), SNAPSHOT(2), LOGS(3), LAST_RUN(4);

  private static final String tabTitleOptions[] = { "Development and Live Database Information",
      "Live Database Information", "Development Database Information", "Logs", "Last Set of Statements Run" };
  private static final String tabTextOptions[] = { "Compare to Database", "Compare to Snapshot", "Create Snapshot",
      "Logs", "Last Run" };
  int value;

  private PaneType(int type) {
    this.value = type;
  }

  public int getValue() {
    return value;
  }

  public static String getTabTitle(int paneType) {
    return tabTitleOptions[paneType];
  }

  public static String getTabText(int paneType) {
    return tabTextOptions[paneType];
  }
}