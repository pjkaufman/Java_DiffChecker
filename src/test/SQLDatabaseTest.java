package test;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import dbdiffchecker.sql.SQLDatabase;
import dbdiffchecker.sql.Table;
import dbdiffchecker.sql.MySQLTable;
import dbdiffchecker.sql.View;

public class SQLDatabaseTest {
  private Table table1;
  private Table table2;
  private View view1;
  private View view2;
  private String name;
  private String create;
  private SQLDatabase db;
  private SQLDatabase liveDb;
  private List<String> expectedSQL = new ArrayList<>();
  private List<String> sql;

  @Before
  public void clearExpectedSQL() {
    expectedSQL.clear();
  }

  @Test
  public void testGetTables() {
    name = "bloat";
    create = "CREATE TABLE `bloat` (\n  `bloatware` int(11) NOT NULL,\n  PRIMARY KEY (`bloatware`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    table1 = new MySQLTable(name, create);
    create = "CREATE TABLE `bloat` (\n  `bloatware` int(11) NOT NULL\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    table2 = new MySQLTable(name, create);
    db = new SQLDatabase();
    Map<String, Table> tableList = db.getTables();

    assertEquals("The table list should be empty for a newly created database", 0, tableList.size());

    db.getTables().put(table1.getName(), table1);

    assertEquals("The table list should have the 1 table after the first addition", 1, tableList.size());
    assertEquals("The table list should have the exact table added to it", true, tableList.get(name).equals(table1));

    db.getTables().put(table2.getName(), table2);

    assertEquals("The table list size should not change when a table of the same name is added", 1, tableList.size());
    assertEquals("The table list should have the exact table added to it", true, tableList.get(name).equals(table2));
  }

  @Test
  public void testGetViews() {
    db = new SQLDatabase();
    name = "testView";
    create = "CREATE VIEW `testView` AS SELECT * FROM Products;";
    view1 = new View(name, create);
    name = "viewShipment";
    create = "CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `userlist` AS select `users`.`userid` AS `userid`,`users`.`remove` AS `add` from `users`";
    view2 = new View(name, create);
    List<View> viewList = db.getViews();

    assertEquals("The view list should be empty for a newly created database", 0, viewList.size());

    db.getViews().add(view1);

    assertEquals("The view list should contain 1 view after the first addition", 1, viewList.size());
    assertEquals("The view list should have the view that was added by the first addition", true,
        viewList.contains(view1));

    db.getViews().add(view2);

    assertEquals("The view list should contain 2 views after the second addition", 2, viewList.size());
    assertEquals("The view list should have both added views", true,
        viewList.contains(view1) && viewList.contains(view2));
  }

  @Test
  public void testUpdateViews() {
    db = new SQLDatabase();
    List<View> liveViews = new ArrayList<>();
    name = "testView";
    create = "CREATE VIEW `testView` AS SELECT * FROM Products";
    view1 = new View(name, create);
    name = "viewShipment";
    create = "CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `userlist` AS select `users`.`userid` AS `userid`,`users`.`remove` AS `add` from `users`";
    view2 = new View(name, create);
    db.getViews().add(view1);
    db.getViews().add(view2);
    liveViews.add(view1);

    sql = db.updateViews(liveViews);

    expectedSQL.add(view1.getDrop());
    expectedSQL.add(view1.getCreateStatement());
    expectedSQL.add(view2.getCreateStatement());

    assertEquals("The sql generated should contain a create for both view1 and view2 as well as a drop for view1",
        expectedSQL, sql);
  }

  @Test
  public void testTablesDiffs() {
    db = new SQLDatabase();
    liveDb = new SQLDatabase();
    Map<String, String> tablesToUpdate;
    Map<String, Table> liveTables = liveDb.getTables();
    name = "bloat";
    create = "CREATE TABLE `bloat` (\n  `bloatware` int(11) NOT NULL,\n  PRIMARY KEY (`bloatware`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    table1 = new MySQLTable(name, create);
    name = "broke";
    create = "CREATE TABLE `broke` (\n  `bloatware` int(11) NOT NULL\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    table2 = new MySQLTable(name, create);
    create = "CREATE TABLE `broke` (\n `bloated` int(11) NOT NULL\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    Table table3 = new MySQLTable(name, create);
    db.getTables().put(table1.getName(), table1);
    db.getTables().put(table2.getName(), table2);
    liveTables.put(table3.getName(), table3);

    db.compareTables(liveDb);
    tablesToUpdate = db.tablesDiffs(liveTables, new SQLDatabase());

    assertEquals("The expected tables to update should be 1 because only one table they have in common is different", 1,
        tablesToUpdate.size());
    assertEquals("The tables to update should be 1 which is the name of the only common table in this case", true,
        tablesToUpdate.containsKey(table3.getName()));
  }

  @Test
  public void testTableDiffsSameTable() {
    Map<String, String> tablesToUpdate;
    Map<String, Table> liveTables = new HashMap<>();
    name = "bloat";
    create = "CREATE TABLE `bloat` (\n  `bloatware` int(11) NOT NULL,\n  PRIMARY KEY (`bloatware`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    table1 = new MySQLTable(name, create);
    db = new SQLDatabase();
    db.getTables().put(name, table1);
    liveTables.put(table1.getName(), table1);

    tablesToUpdate = db.tablesDiffs(liveTables, new SQLDatabase());

    assertEquals("The expected tables to update should be 0 because all common tables are the same", 0,
        tablesToUpdate.size());
  }

  @Test
  public void testTableDiffsNoCommonTables() {
    Map<String, String> tablesToUpdate;
    liveDb = new SQLDatabase();
    Map<String, Table> liveTables = liveDb.getTables();
    db = new SQLDatabase();
    name = "bloat";
    create = "CREATE TABLE `bloat` (\n  `bloatware` int(11) NOT NULL,\n  PRIMARY KEY (`bloatware`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    table1 = new MySQLTable(name, create);
    name = "broke";
    create = "CREATE TABLE `broke` (\n `bloated` int(11) NOT NULL\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    table2 = new MySQLTable(name, create);
    liveTables.put(table2.getName(), table2);
    db.getTables().put(table1.getName(), table1);
    db.compareTables(liveDb);

    tablesToUpdate = db.tablesDiffs(liveTables, new SQLDatabase());

    assertEquals("There should be no tables to update when there are no common table names", true,
        tablesToUpdate.isEmpty());
  }

  @Test
  public void testCompareTablesDropAll() {
    liveDb = new SQLDatabase();
    Map<String, Table> liveTables = liveDb.getTables();
    name = "bloat";
    create = "CREATE TABLE `bloat` (\n  `bloatware` int(11) NOT NULL,\n  PRIMARY KEY (`bloatware`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    table1 = new MySQLTable(name, create);
    name = "broke";
    create = "CREATE TABLE `broke` (\n  `bloatware` int(11) NOT NULL\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    table2 = new MySQLTable(name, create);
    liveTables.put(table1.getName(), table1);
    liveTables.put(table2.getName(), table2);
    db = new SQLDatabase();

    expectedSQL.add(table2.getDrop());
    expectedSQL.add(table1.getDrop());

    sql = db.compareTables(liveDb);

    assertEquals("The sql generated should contain a drop for all tables in the liveTables", expectedSQL, sql);
  }

  @Test
  public void testCompareTablesDropTable() {
    liveDb = new SQLDatabase();
    Map<String, Table> liveTables = liveDb.getTables();
    name = "bloat";
    create = "CREATE TABLE `bloat` (\n  `bloatware` int(11) NOT NULL,\n  PRIMARY KEY (`bloatware`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    table1 = new MySQLTable(name, create);
    name = "broke";
    create = "CREATE TABLE `broke` (\n  `bloatware` int(11) NOT NULL\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    table2 = new MySQLTable(name, create);
    liveTables.put(table1.getName(), table1);
    liveTables.put(table2.getName(), table2);
    db = new SQLDatabase();
    db.getTables().put(table1.getName(), table1);

    expectedSQL.add(table2.getDrop());

    sql = db.compareTables(liveDb);

    assertEquals("The sql generated should contain a drop for table2", expectedSQL, sql);
  }

  @Test
  public void testCompareTablesAddTable() {
    liveDb = new SQLDatabase();
    name = "bloat";
    create = "CREATE TABLE `bloat` (\n  `bloatware` int(11) NOT NULL,\n  PRIMARY KEY (`bloatware`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    table1 = new MySQLTable(name, create);
    db = new SQLDatabase();
    db.getTables().put(table1.getName(), table1);

    expectedSQL.add(table1.getCreateStatement());

    sql = db.compareTables(liveDb);

    assertEquals("The sql generated should contain a create statement for table1", expectedSQL, sql);
  }

  @Test
  public void testCompareTablesAddAll() {
    liveDb = new SQLDatabase();
    name = "bloat";
    create = "CREATE TABLE `bloat` (\n  `bloatware` int(11) NOT NULL,\n  PRIMARY KEY (`bloatware`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    table1 = new MySQLTable(name, create);
    name = "broke";
    create = "CREATE TABLE `broke` (\n  `bloatware` int(11) NOT NULL\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    table2 = new MySQLTable(name, create);
    db = new SQLDatabase();
    db.getTables().put(table1.getName(), table1);
    db.getTables().put(table2.getName(), table2);

    expectedSQL.add(table2.getCreateStatement());
    expectedSQL.add(table1.getCreateStatement());

    sql = db.compareTables(liveDb);

    assertEquals("The sql generated should contain a create statement for all tables", expectedSQL, sql);
  }

  @Test
  public void testUpdateTablesManyDifferencesTable() {
    expectedSQL.add("ALTER TABLE `ci_sessions` CHARACTER SET latin1, \n\tDROP INDEX `delete`, "
        + "\n\tADD COLUMN `id` varchar(40) NOT NULL, \n\tMODIFY COLUMN `ip_address`"
        + " varchar(45) NOT NULL AFTER `id`, \n\tMODIFY COLUMN `timestamp` int(10) unsigned "
        + "NOT NULL DEFAULT \'0\' AFTER `ip_address`, \n\tDROP COLUMN `data2`, \n\tADD INDEX "
        + "`add` (`id`), \n\tDROP INDEX `modify`, \n\tADD INDEX `modify` (`data`);");

    db = new SQLDatabase();
    Map<String, Table> liveTables = new HashMap<>();
    Map<String, String> tablesToUpdate = new HashMap<>();
    name = "ci_sessions";
    create = "CREATE TABLE `ci_sessions` (\n  `id` varchar(40) NOT NULL,\n  "
        + "`ip_address` varchar(45) NOT NULL,\n  `timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  "
        + "`data` blob NOT NULL,\n  KEY `add` (`id`),\n  KEY `modify` (`data`),\n  "
        + "KEY `leave` (`data`,`id`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    table1 = new MySQLTable(name, create);
    create = "CREATE TABLE `ci_sessions` (\n  `ip_address` varchar(40) NOT NULL,\n  "
        + "`timestamp` int(11) unsigned NOT NULL DEFAULT \'0\',\n  `data` blob NOT NULL,\n  "
        + "`data2` blob NOT NULL,\n  UNIQUE KEY `delete` (`id`)\n,  KEY `modify` (`data`,`ip_address`),\n  "
        + "KEY `leave` (`data`,`id`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin2";
    table2 = new MySQLTable(name, create);
    db.getTables().put(table1.getName(), table1);
    liveTables.put(table2.getName(), table2);

    tablesToUpdate.put(table2.getName(), table2.getName());
    sql = db.updateTables(liveTables, tablesToUpdate);

    assertEquals("The sql generated should add a column, drop a column, modify two columns, drop two indexes,"
        + " add two indexes, and add a charset", expectedSQL, sql);
  }

  @Test
  public void testUpdateTablesManyDifferencesTables() {
    expectedSQL.add("ALTER TABLE `ci_sessions` CHARACTER SET latin1, \n\tDROP INDEX `delete`, "
        + "\n\tADD COLUMN `id` varchar(40) NOT NULL, \n\tMODIFY COLUMN `ip_address`"
        + " varchar(45) NOT NULL AFTER `id`, \n\tMODIFY COLUMN `timestamp` int(10) unsigned "
        + "NOT NULL DEFAULT \'0\' AFTER `ip_address`, \n\tDROP COLUMN `data2`, \n\tADD INDEX "
        + "`add` (`id`), \n\tDROP INDEX `modify`, \n\tADD INDEX `modify` (`data`);");
    expectedSQL
        .add("ALTER TABLE `bloat` MODIFY COLUMN `bloatware` int(11) NOT NULL, \n\t" + "ADD PRIMARY KEY (`bloatware`);");

    db = new SQLDatabase();
    Map<String, Table> liveTables = new HashMap<>();
    Map<String, String> tablesToUpdate = new HashMap<>();
    name = "ci_sessions";
    create = "CREATE TABLE `ci_sessions` (\n  `id` varchar(40) NOT NULL,\n  "
        + "`ip_address` varchar(45) NOT NULL,\n  `timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  "
        + "`data` blob NOT NULL,\n  KEY `add` (`id`),\n  KEY `modify` (`data`),\n  "
        + "KEY `leave` (`data`,`id`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    table1 = new MySQLTable(name, create);
    create = "CREATE TABLE `ci_sessions` (\n  `ip_address` varchar(40) NOT NULL,\n  "
        + "`timestamp` int(11) unsigned NOT NULL DEFAULT \'0\',\n  `data` blob NOT NULL,\n  "
        + "`data2` blob NOT NULL,\n  UNIQUE KEY `delete` (`id`)\n,  KEY `modify` (`data`,`ip_address`),\n  "
        + "KEY `leave` (`data`,`id`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin2";
    table2 = new MySQLTable(name, create);
    tablesToUpdate.put(table2.getName(), table2.getName());
    db.getTables().put(table1.getName(), table1);
    liveTables.put(table2.getName(), table2);
    name = "bloat";
    create = "CREATE TABLE `bloat` (\n  `bloatware` int(11) NOT NULL,\n  PRIMARY KEY (`bloatware`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    table1 = new MySQLTable(name, create);
    create = "CREATE TABLE `bloat` (\n  `bloatware` int(10) NOT NULL\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    table2 = new MySQLTable(name, create);
    db.getTables().put(table1.getName(), table1);
    liveTables.put(table2.getName(), table2);
    tablesToUpdate.put(table2.getName(), table2.getName());

    sql = db.updateTables(liveTables, tablesToUpdate);

    assertEquals("The sql generated should add a column, drop a column, modify two columns, drop two indexes,"
        + " add two indexes, add a charset, modify a column, and add a primary key", expectedSQL, sql);
  }

  @Test
  public void testFirstStepsInitiallyEmpty() {
    db = new SQLDatabase();

    assertEquals("First steps should be empty upon initialization", true, db.getFirstSteps().isEmpty());
  }

  @Test
  public void testFirstStepsContentAdditions() {
    db = new SQLDatabase();
    List<String> firstSteps = db.getFirstSteps();
    String first1 = "ALTER TABLE `blob`\n ADD PRIMARY KEY (`pikapika`);";
    String first2 = "ALTER TABLE `broach`\n ADD PRIMARY KEY (`mewtwo`);";

    db.getFirstSteps().add(first1);
    db.getFirstSteps().add(first2);

    assertEquals("First steps should contain the first added sql statement", true, firstSteps.contains(first1));
    assertEquals("First steps should contain the second added sql statement", true, firstSteps.contains(first2));
  }

  @Test
  public void testRemoveFirstStepsForTablesThatAreBeingDroppedOrAdded() {
    db = new SQLDatabase();
    liveDb = new SQLDatabase();
    String first1 = "ALTER TABLE `blob` ADD PRIMARY KEY (`pikapika`);";
    String first2 = "ALTER TABLE `broach` ADD PRIMARY KEY (`mewtwo`);";
    db.getFirstSteps().add(first1);
    db.getFirstSteps().add(first2);
    name = "blob";
    create = "CREATE TABLE `blob` (\n  `pikapika` int(11) NOT NULL,\n  PRIMARY KEY (`bloatware`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    table1 = new MySQLTable(name, create);
    name = "broach";
    create = "CREATE TABLE `broach` (\n  `mewtwo` int(11) NOT NULL\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    table2 = new MySQLTable(name, create);
    db.getTables().put(table1.getName(), table1);

    db.compareTables(liveDb);

    assertEquals("First steps should contain one sql statement after removal of exclusions", 1,
        db.getFirstSteps().size());
    assertEquals("First steps should contain the second sql statment added", true, db.getFirstSteps().contains(first2));

    db.getTables().put(table2.getName(), table2);
    db.compareTables(liveDb);

    assertEquals("First steps should contain zero sql statement after removal of exclusions", true,
        db.getFirstSteps().isEmpty());
  }
}
