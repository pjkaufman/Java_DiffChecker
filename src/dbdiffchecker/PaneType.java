package dbdiffchecker;

/**
 * Deals with all pane types and their related data.
 *
 * @author Peter Kaufman
 * @version 7-6-20
 * @since 7-1-20
 */
public enum PaneType {
  COMPARE_WITH_DB(0), COMPARE_WITH_SNAPSHOT(1), SNAPSHOT(2), LOGS(3), LAST_RUN(4);

  private static final String[] tabTitleOptions = { "Development and Live Database Information",
      "Live Database Information", "Development Database Information", "Logs", "Last Set of Statements Run" };
  private static final String[] tabTextOptions = { "Compare to Database", "Compare to Snapshot", "Create Snapshot",
      "Logs", "Last Run" };
  int value;

  /**
   * Initializes the pane type.
   *
   * @param type The integer representation of the pane type.
   */
  private PaneType(int type) {
    value = type;
  }

  /**
   * Returns the value associated witht the pane type.
   *
   * @return The integer representation of the pane type.
   */
  public int getValue() {
    return value;
  }

  /**
   * Returns the tab title associated witht the pane type.
   *
   * @return The tab title representation of the pane type.
   */
  public static String getTabTitle(int paneType) {
    return tabTitleOptions[paneType];
  }

  /**
   * Returns the tab text associated witht the pane type.
   *
   * @return The tab text representation of the pane type.
   */
  public static String getTabText(int paneType) {
    return tabTextOptions[paneType];
  }
}