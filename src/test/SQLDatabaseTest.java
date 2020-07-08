package test;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dbdiffchecker.sql.SQLDatabase;
import dbdiffchecker.sql.Table;
import dbdiffchecker.sql.MySQLTable;
import dbdiffchecker.sql.View;

/**
 * A unit test that makes sure that the SQLDatabase object works as intended.
 *
 * @author Peter Kaufman
 * @version 7-7-20
 * @since 5-11-19
 */
public class SQLDatabaseTest {
  private Table table1;
  private Table table2;
  private View view1;
  private View view2;
  private String name;
  private String create;
  private SQLDatabase db;

  @Test
  public void testGetTables() {
    name = "bloat";
    create = "CREATE TABLE `bloat` (\n  `bloatware` int(11) NOT NULL,\n  PRIMARY KEY (`bloatware`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    table1 = new MySQLTable(name, create);
    create = "CREATE TABLE `bloat` (\n  `bloatware` int(11) NOT NULL\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    table2 = new MySQLTable(name, create);
    db = new SQLDatabase();
    assertEquals("The table list should be empty for a newly created database", 0, db.getTables().size());
    db.getTables().put(table1.getName(), table1);
    assertEquals("The table list should have the 1 table after the first addition", 1, db.getTables().size());
    assertEquals("The table list should have the exact table added to it", true,
        db.getTables().get(table1.getName()).getCreateStatement().equals(table1.getCreateStatement()));
    db.getTables().put(table2.getName(), table2);
    assertEquals("The table list size should not change when a table of the same name is added", 1,
        db.getTables().size());
    assertEquals("The table list should have the exact table as the one just added", true,
        db.getTables().get(table2.getName()).getCreateStatement().equals(table2.getCreateStatement()));
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
    assertEquals("The view list should be empty for a newly created database", 0, db.getViews().size());
    db.getViews().add(view1);
    assertEquals("The view list should contain 1 view after the first addition", 1, db.getViews().size());
    assertEquals("The view list should have the view that was added by the first addition", true,
        db.getViews().contains(view1));
    db.getViews().add(view2);
    assertEquals("The view list should contain 2 views after the second addition", 2, db.getViews().size());
    assertEquals("The view list should have both added views", true,
        db.getViews().contains(view1) && db.getViews().contains(view2));
  }

  @Test
  public void testUpdateViews() {
    db = new SQLDatabase();
    List<View> liveViews = new ArrayList<>();
    List<String> sql;
    name = "testView";
    create = "CREATE VIEW `testView` AS SELECT * FROM Products;";
    view1 = new View(name, create);
    name = "viewShipment";
    create = "CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `userlist` AS select `users`.`userid` AS `userid`,`users`.`remove` AS `add` from `users`";
    view2 = new View(name, create);
    db.getViews().add(view1);
    db.getViews().add(view2);
    liveViews.add(view1);
    sql = db.updateViews(liveViews);
    assertEquals("The expected sql statments are two create statements and a drop statement", 3, sql.size());
    assertEquals("The sql generated should contain a create for both view1 and view2 as well as a drop for view1", true,
        sql.contains(view1.getCreateStatement()) && sql.contains(view2.getCreateStatement())
            && sql.contains("DROP VIEW `" + view1.getName() + "`;"));
    // test to see if it will drop and add the view back
    db = new SQLDatabase();
    db.getViews().add(view1);
    sql = db.updateViews(liveViews);
    assertEquals("The expected sql statments are a create statement and a drop statement", 2, sql.size());
    assertEquals("The sql generated should contain a create for view1 and a drop for view1", true,
        sql.contains(view1.getCreateStatement()) && sql.contains("DROP VIEW `" + view1.getName() + "`;"));
  }

  @Test
  public void testTablesDiffs() {
    db = new SQLDatabase();
    Map<String, String> tablesToUpdate;
    Map<String, Table> liveTables = new HashMap<>();
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
    db.compareTables(liveTables);
    tablesToUpdate = db.tablesDiffs(liveTables, new SQLDatabase());
    assertEquals("The expected tables to update should be 1 because only one table they have in common is different", 1,
        tablesToUpdate.size());
    assertEquals("The tables to update should be one which is the name of the only common table in this case", true,
        tablesToUpdate.containsKey(table3.getName()));
    // test to see if it will yield nothing if there are no tables in common
    db = new SQLDatabase();
    db.getTables().put(table1.getName(), table1);
    db.compareTables(liveTables);
    tablesToUpdate = db.tablesDiffs(liveTables, new SQLDatabase());
    assertEquals("The expected tables to update should be 0 because there are no tables in common", 0,
        tablesToUpdate.size());
    // test to see if it will yield nothing if all common tables are the same
    db.getTables().put(table3.getName(), table3);
    liveTables.put(table1.getName(), table1);
    tablesToUpdate = db.tablesDiffs(liveTables, new SQLDatabase());
    assertEquals("The expected tables to update should be 0 because all common tables are the same", 0,
        tablesToUpdate.size());
  }

  @Test
  public void testCompareTables() {
    db = new SQLDatabase();
    Map<String, Table> liveTables = new HashMap<>();
    List<String> sql;
    name = "bloat";
    create = "CREATE TABLE `bloat` (\n  `bloatware` int(11) NOT NULL,\n  PRIMARY KEY (`bloatware`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    table1 = new MySQLTable(name, create);
    name = "broke";
    create = "CREATE TABLE `broke` (\n  `bloatware` int(11) NOT NULL\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    table2 = new MySQLTable(name, create);
    // test to see if all tables will be dropped
    liveTables.put(table1.getName(), table1);
    liveTables.put(table2.getName(), table2);
    sql = db.compareTables(liveTables);
    assertEquals("The expected sql statments are two drop statements", 2, sql.size());
    assertEquals("The sql generated should contain a drop for table1 and table2", true,
        sql.contains("DROP TABLE `" + table2.getName() + "`;")
            && sql.contains("DROP TABLE `" + table1.getName() + "`;"));
    // test to see if a drop will be added
    db.getTables().put(table1.getName(), table1);
    sql = db.compareTables(liveTables);
    assertEquals("The expected sql statment is a drop statement", 1, sql.size());
    assertEquals("The sql generated should contain a drop for table2", true,
        sql.contains("DROP TABLE `" + table2.getName() + "`;"));
    // test to see if a table will be added
    liveTables = new HashMap<>();
    sql = db.compareTables(liveTables);
    assertEquals("The expected sql statment is a create statement", 1, sql.size());
    assertEquals("The sql generated should contain a create statement for table1", true,
        sql.contains(table1.getCreateStatement()));
    // test to see if all tables will be added
    db.getTables().put(table2.getName(), table2);
    sql = db.compareTables(liveTables);
    assertEquals("The expected sql statment are 2 create statements", 2, sql.size());
    assertEquals("The sql generated should contain a create statement for both table1 and table2", true,
        sql.contains(table1.getCreateStatement()) && sql.contains(table2.getCreateStatement()));
  }

  @Test
  public void testUpdateTables() {
    db = new SQLDatabase();
    Map<String, Table> liveTables = new HashMap<>();
    Map<String, String> tablesToUpdate = new HashMap<>();
    List<String> sql;
    String expectedSQL = "ALTER TABLE `ci_sessions`\nCHARACTER SET latin1, \nDROP INDEX `delete`, "
        + "\nADD COLUMN `id` varchar(40) NOT NULL, \nMODIFY COLUMN `ip_address`"
        + " varchar(45) NOT NULL AFTER `id`, \nMODIFY COLUMN `timestamp` int(10) unsigned "
        + "NOT NULL DEFAULT \'0\' AFTER `ip_address`, \nDROP COLUMN `data2`, \nADD INDEX "
        + "`add` (`id`), \nDROP INDEX `modify`, \nADD INDEX `modify` (`data`);";
    String expectedSQL2 = "ALTER TABLE `bloat`\nMODIFY COLUMN `bloatware` int(11) NOT NULL, \n"
        + "ADD PRIMARY KEY (`bloatware`);";
    // test for two tables with many differences
    // setup table1
    name = "ci_sessions";
    create = "CREATE TABLE `ci_sessions` (\n  `id` varchar(40) NOT NULL,\n  "
        + "`ip_address` varchar(45) NOT NULL,\n  `timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  "
        + "`data` blob NOT NULL,\n  KEY `add` (`id`),\n  KEY `modify` (`data`),\n  "
        + "KEY `leave` (`data`,`id`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    table1 = new MySQLTable(name, create);
    // setup table2
    create = "CREATE TABLE `ci_sessions` (\n  `ip_address` varchar(40) NOT NULL,\n  "
        + "`timestamp` int(11) unsigned NOT NULL DEFAULT \'0\',\n  `data` blob NOT NULL,\n  "
        + "`data2` blob NOT NULL,\n  UNIQUE KEY `delete` (`id`)\n,  KEY `modify` (`data`,`ip_address`),\n  "
        + "KEY `leave` (`data`,`id`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin2";
    table2 = new MySQLTable(name, create);
    // do comparison
    db.getTables().put(table1.getName(), table1);
    liveTables.put(table2.getName(), table2);
    tablesToUpdate.put(table2.getName(), table2.getName());
    table2 = new MySQLTable(name, create);
    db.getTables().put(table1.getName(), table1);
    liveTables.put(table2.getName(), table2);
    tablesToUpdate.put(table2.getName(), table2.getName());
    sql = db.updateTables(liveTables, tablesToUpdate);
    assertEquals("The sql generated should add a column, drop a column, modify two columns, drop two indexes,"
        + " add two indexes, and add a charset", expectedSQL, sql.get(0));
    // test two table comparison
    name = "bloat";
    create = "CREATE TABLE `bloat` (\n  `bloatware` int(11) NOT NULL,\n  PRIMARY KEY (`bloatware`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    table1 = new MySQLTable(name, create);
    create = "CREATE TABLE `bloat` (\n  `bloatware` int(10) NOT NULL\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    table2 = new MySQLTable(name, create);
    db.getTables().put(table1.getName(), table1);
    liveTables.put(table2.getName(), table2);
    tablesToUpdate.put(table2.getName(), table2.getName());
    sql = db.updateTables(liveTables, tablesToUpdate);
    assertEquals("There should be two sql statements generated (one for each table that is to be modified)", 2,
        sql.size());
    assertEquals("The sql generated should add a column, drop a column, modify two columns, drop two indexes,"
        + " add two indexes, add a charset", expectedSQL, sql.get(0));
    assertEquals("The sql generated should also modify a column and add a primary key", true,
        sql.contains(expectedSQL2));
  }

  @Test
  public void testFirstSteps() {
    db = new SQLDatabase();
    Map<String, Table> liveTables = new HashMap<>();
    String first1 = "ALTER TABLE `blob`\n ADD PRIMARY KEY (`pikapika`);";
    String first2 = "ALTER TABLE `broach`\n ADD PRIMARY KEY (`mewtwo`);";
    // initial addition of elements tests
    assertEquals("First steps should be empty upon initialization", 0, db.getFirstSteps().size());
    db.getFirstSteps().add(first1);
    assertEquals("First steps should contain the first added sql statement", 1, db.getFirstSteps().size());
    db.getFirstSteps().add(first2);
    assertEquals("First steps should contain the second added sql statement", 2, db.getFirstSteps().size());
    // create tables
    name = "blob";
    create = "CREATE TABLE `blob` (\n  `pikapika` int(11) NOT NULL,\n  PRIMARY KEY (`bloatware`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    table1 = new MySQLTable(name, create);
    name = "broach";
    create = "CREATE TABLE `broach` (\n  `mewtwo` int(11) NOT NULL\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    table2 = new MySQLTable(name, create);
    // test to remove first steps associated with table1
    db.getTables().put(table1.getName(), table1);
    db.compareTables(liveTables);
    assertEquals("First steps should contain one sql statement after removal of exclusions", 1,
        db.getFirstSteps().size());
    assertEquals("First steps should contain the second sql statment added", true, db.getFirstSteps().contains(first2));
    liveTables.put(table2.getName(), table2);
    db.compareTables(liveTables);
    assertEquals("First steps should contain zero sql statement after removal of exclusions", 0,
        db.getFirstSteps().size());
  }
}