package dbdiffchecker;

public enum PaneType {
  COMPARE_WITH_DB(0), COMPARE_WITH_SNAPSHOT(1), SNAPSHOT(2), LOGS(3), LAST_RUN(4);

  private final String tabTitleOptions[] = { "Development and Live Database Information", "Live Database Information",
      "Development Database Information", "Logs", "Last Set of Statements Run" };
  private final String tabTextOptions[] = { "Compare to Database", "Compare to Snapshot", "Create Snapshot", "Logs",
      "Last Run" };
  String tabTitle, tabText;
  int value;

  PaneType(int type) {
    this.value = type;
    this.tabTitle = tabTitleOptions[type];
    this.tabText = tabTextOptions[type];
  }

  public String getTabTitle() {
    return tabTitle;
  }

  public String getTabText() {
    return tabTitle;
  }

  public int getValue() {
    return value;
  }
}