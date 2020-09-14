package test;

import java.util.stream.Stream;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.params.provider.Arguments;

public class TestParamProvider {
  // Collection instance variables
  private static final String collectionName1 = "work1";
  private static final String collectionName2 = "work2";
  private static final int size1 = 1024;
  private static final int size2 = 2048;
  private static final String createCollectionFmt = "Create Collection: %s";
  private static final String deleteCollectionFmt = "Delete Collection: %s";
  // Couchbase instance variables
  private static final String createDocumentFmt = "Create Document: %s";
  private static final String deleteDocumentFmt = "Delete Document: %s";
  private static final String createIndexFmt1 = "CREATE INDEX `%s` ON `development`";
  private static final String createIndexFmt2 = "CREATE INDEX `%s` ON `development`  WHERE (`abv` > 6)";
  private static final String dropIndexFmt = "DROP INDEX `%s`;";
  private static final String documentName1 = "blob";
  private static final String documentName2 = "Destruction";
  private static final String documentName3 = "leave";
  private static final String documentName4 = "DropDoc";
  private static final String indexName1 = "dev_primary";
  private static final String indexName2 = "devSpeed";
  private static final String indexName3 = "leave";
  private static final String indexName4 = "create";
  private static final String indexName5 = "drop";
  // FileHandler instance variables
  private static final String data1 = "Test addition of data";
  private static final String data2 = "Another addition of text...";
  private static final String data3 = "erroneous...";
  // Index instance variables
  private static final String createIndexFmt4 = "CREATE INDEX `%s` ON `%s` (`shippingID`,`vendor`)";
  private static final String createIndexFmt5 = "CREATE UNIQUE INDEX `%s` ON `%s` (`shippingID`)";
  private static final String createIndexFmt6 = "CREATE INDEX `%s` ON `%s` (`shippingID`)";
  // View instance variables
  private static final String viewName = "viewShipment";
  private static final String viewCreate = "CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `userlist` AS select `users`.`userid` AS `userid`,`users`.`remove` AS `add` from `users`";
  // SQLite Table instance variables
  private static final String tableName1 = "helper";
  private static final String tableName2 = "bloat";
  private static final String tableName3 = "Books";
  private static final String tableColumn1 = "hulk";
  private static final String tableColumn2 = "Thor";
  private static final String tableColumn3 = "bloatware";
  private static final String tableColumn4 = "shipmentID";
  private static final String indexName6 = "shipment";
  private static final String indexName7 = "shipped";
  private static final String tableCreate3 = String.format("CREATE TABLE %s (hulk STRING (12))", tableName1);
  private static final String tableCreate5 = String.format("CREATE TABLE %s (hulk STRING (12), Thor INTEGER (67) DEFAULT (12), truthtable STRING (12))", tableName1);
  private static final String tableCreate6 = String.format("CREATE TABLE %s (hulk STRING (12), Thor INTEGER (67) DEFAULT (12))", tableName1);
  private static final String tableCreate7 = String.format("CREATE TABLE %s (bloatware INTEGER (11) NOT NULL, shipmentID INTEGER (11) NOT NULL)", tableName2);
  private static final String tableCreate2 = tableCreate6 + String.format(";\nCREATE INDEX addition ON %s (hulk)", tableName1);
  private static final String tableCreate =  tableCreate7 + ";\n CREATE INDEX shipment ON " + tableName2 + " (shippingID, bloatware)"
    + ";\n  CREATE INDEX shipped ON " + tableName2 + " (shippingID)";
  private static final String tableCreate8 = String.format("CREATE TABLE %s (hulk STRING (11), truthtable STRING (12))", tableName1);
  private static final String tableCreate9 = String.format("CREATE TABLE %s (hulk STRING (12), truthtable INTEGER (67) DEFAULT (12))", tableName1);
  private static final String tableCreate10 = String.format("CREATE TABLE %s (Thor INTEGER (67) DEFAULT (12), ache BLOB (66) PRIMARY KEY)", tableName2);
  private static final String tableCreate11 = String.format("CREATE TABLE %s (Thor INTEGER (67) DEFAULT (12), ache BLOB (66))", tableName2);
  private static final String insertStatement = String.format("INSERT INTO %s (hulk)\n  SELECT hulk\n  FROM temp_table;", tableName1);
  private static final String insertStatement2 = String.format("INSERT INTO %s (truthtable,hulk)\n  SELECT truthtable,hulk\n  FROM temp_table;", tableName1);
  private static final String insertStatement3 = String.format("INSERT INTO %s (Thor,ache)\n  SELECT Thor,ache\n  FROM temp_table;", tableName2);
  private static final String extraCreate = "CREATE INDEX addition ON helper (Thor)";
  // MySQL Table instance variables
  private static final String tableCreate12 =  "CREATE TABLE `bloat` (\n  `bloatware` int(11) NOT NULL,\n  `shipmentID` int(11) NOT NULL\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
  private static final String tableCreate13 = "CREATE TABLE `shippingData` (\n  `shipmentID` int(11) NOT NULL,\n  `vendor` STRING(11) NOT NULL,\n"
    + "  KEY `shipment` (`shipmentID`,`vendor`),\n  KEY `shipped` (`shipmentID`)\n) ENGINE=InnoDB "
    + "DEFAULT CHARSET=latin1";
  private static final String tableCreate14 = "CREATE TABLE `products` (\n  `prd_id` int not null auto_increment,\n"
    + "  `prd_name` varchar(355) not null,\n  `prd_price` decimal,\n  `cat_id` int not null,\n  "
    + "CONSTRAINT `constraint_name` FOREIGN KEY `fk_cat`(`cat_id`) REFERENCES `categories`"
    + "(`cat_id`)\n  ON UPDATE CASCADE\n  ON DELETE RESTRICT\n  )ENGINE=InnoDB";
  private static final String tableName5 = "ci_sessions";
  private static final String tableCreate15 = "CREATE TABLE `ci_sessions` (\n  `id` varchar(40) NOT NULL,\n  "
      + "`ip_address` varchar(45) NOT NULL,\n  `timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  "
      + "`data` blob NOT NULL,\n  KEY `leave` (`data`,`id`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
  private static final String tableCreate16 = "CREATE TABLE `ci_sessions` (\n  `id` varchar(40) NOT NULL,\n  "
      + "`ip_address` varchar(45) NOT NULL,\n  `timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  "
      + "`data` blob NOT NULL,\n  KEY `leave` (`data`,`id`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
  private static final String tableCreate17 = "CREATE TABLE `ci_sessions` (\n  `id` varchar(40) NOT NULL,\n  "
      + "`ip_address` varchar(45) NOT NULL,\n  `timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  "
      + "`data` blob NOT NULL,\n  KEY `leave` (`data`,`id`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
  private static final String tableCreate18 = "CREATE TABLE `ci_sessions` (\n  `id` varchar(40) NOT NULL,\n  "
      + "`ip_address` varchar(45) NOT NULL,\n  `timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  "
      + "`data` blob NOT NULL,\n  KEY `leave` (`data`,`id`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
  private static final String tableCreate19 = "CREATE TABLE `ci_sessions` (\n  `ip_address` varchar(45) NOT NULL,\n  "
      + "`timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  "
      + "`data` blob NOT NULL\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
  private static final String tableCreate20 = "CREATE TABLE `ci_sessions` (\n  `ip_address` varchar(45) NOT NULL,\n  "
      + "`timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  `data` blob NOT NULL\n"
      + ") ENGINE=InnoDB DEFAULT CHARSET=latin1";
  private static final String tableCreate21 = "CREATE TABLE `ci_sessions` (\n  `id` varchar(45) NOT NULL\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
  private static final String tableCreate22 = "CREATE TABLE `bloat` (\n  `bloatware` int(11) NOT NULL,\n  PRIMARY KEY (`bloatware`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=17";
  private static final String tableName4 = "shippingData";
  private static final String viewName2 = "testView";
  private static final String viewName3 = "viewShipment";
  private static final String viewCreate2 = "CREATE VIEW `testView` AS SELECT * FROM Products";
  private static final String viewCreate3 = "CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `userlist` AS select `users`.`userid` AS `userid`,`users`.`remove` AS `add` from `users`";
  private static final String viewDropFmt = "DROP VIEW `%s`;";
  private static final String tableDropFmt = "DROP TABLE `%s`;";
  // All purpose instance variables
  private static final List<String> emptyList = new ArrayList<>();

  static Stream<Arguments> mongoCompare() {
    return Stream.of(
      Arguments.of("Empty Databases Have No Changes", emptyList, new ArrayList<Integer>(), new ArrayList<Boolean>(),
        emptyList, new ArrayList<Integer>(), new ArrayList<Boolean>(), emptyList),
      Arguments.of("Create Single Collection", Arrays.asList(collectionName1, collectionName2),
        Arrays.asList(0, 50000), Arrays.asList(false, true), Arrays.asList(collectionName2),
        Arrays.asList(50000), Arrays.asList(true), Arrays.asList(String.format(createCollectionFmt, collectionName1))),
      Arguments.of("Create Multiple Collections", Arrays.asList(collectionName1, collectionName2),
        Arrays.asList(0, 50000), Arrays.asList(false, true),  emptyList, new ArrayList<Integer>(), new ArrayList<Boolean>(),
        Arrays.asList(String.format(createCollectionFmt, collectionName2) + ", capped=true, size=50000", String.format(createCollectionFmt, collectionName1))),
      Arguments.of("Drop Single Collection", Arrays.asList(collectionName2), Arrays.asList(50000),
        Arrays.asList(true), Arrays.asList(collectionName1, collectionName2), Arrays.asList(0, 50000),
        Arrays.asList(false, true), Arrays.asList(String.format(deleteCollectionFmt, collectionName1))),
      Arguments.of("Drop Multiple Collections", emptyList, new ArrayList<Integer>(), new ArrayList<Boolean>(),
        Arrays.asList(collectionName1, collectionName2), Arrays.asList(0, 50000), Arrays.asList(false, true),
        Arrays.asList(String.format(deleteCollectionFmt, collectionName2), String.format(deleteCollectionFmt, collectionName1))),
      Arguments.of("Update Single Collection", Arrays.asList(collectionName1, collectionName2),
        Arrays.asList(0, 50000), Arrays.asList(false, true), Arrays.asList(collectionName1, collectionName2),
        Arrays.asList(67890, 50000), Arrays.asList(true, true), Arrays.asList(String.format(deleteCollectionFmt, collectionName1),
          String.format(createCollectionFmt, collectionName1))),
      Arguments.of("Update Multiple Collections", Arrays.asList(collectionName1, collectionName2),
        Arrays.asList(0, 50000), Arrays.asList(false, true), Arrays.asList(collectionName1, collectionName2),
        Arrays.asList(67890, 0), Arrays.asList(true, false), Arrays.asList(String.format(deleteCollectionFmt, collectionName2),
          String.format(createCollectionFmt, collectionName2) + ", capped=true, size=50000",
          String.format(deleteCollectionFmt, collectionName1), String.format(createCollectionFmt, collectionName1))),
      Arguments.of("Complex", Arrays.asList(collectionName1, collectionName2, "Commoner", "Pillager", "Villager"), Arrays.asList(0, 50000, 0, 0, 234560),
        Arrays.asList(false, true, false, false, true),
        Arrays.asList(collectionName1, collectionName2, "Commoner", "Creeper", "Creep"),
        Arrays.asList(67890, 0, 0, 0, 587390), Arrays.asList(true, false, false, false, true),
        Arrays.asList(String.format(createCollectionFmt, "Villager") + ", capped=true, size=234560",
          String.format(createCollectionFmt, "Pillager"), String.format(deleteCollectionFmt, "Creep"),
          String.format(deleteCollectionFmt, "Creeper"), String.format(deleteCollectionFmt, collectionName2),
          String.format(createCollectionFmt, collectionName2) + ", capped=true, size=50000",
          String.format(deleteCollectionFmt, collectionName1), String.format(createCollectionFmt, collectionName1)))
    );
  }

  static Stream<Arguments> mongoGetStatements() {
    return Stream.of(
      Arguments.of("Collection list is empty initially", emptyList, new ArrayList<Integer>(), new ArrayList<Boolean>()),
      Arguments.of("Collection with one value", Arrays.asList(collectionName1), Arrays.asList(0), Arrays.asList(false)),
      Arguments.of("Collection with two values", Arrays.asList(collectionName1, collectionName2), Arrays.asList(0, 50000),
        Arrays.asList(false, true))
    );
  }

  static Stream<Arguments> sqlDatabaseFirstSteps() {
    return Stream.of(
      Arguments.of("No Changes With No First Steps", emptyList, emptyList, emptyList,
        emptyList, emptyList, emptyList),
      Arguments.of("No Changes With First Steps", emptyList, emptyList, emptyList, emptyList,
        Arrays.asList("ALTER TABLE `blob` ADD PRIMARY KEY (`pikapika`);", "ALTER TABLE `broach` ADD PRIMARY KEY (`mewtwo`);"),
        Arrays.asList("ALTER TABLE `blob` ADD PRIMARY KEY (`pikapika`);", "ALTER TABLE `broach` ADD PRIMARY KEY (`mewtwo`);")),
      Arguments.of("Single Removal", Arrays.asList("blob"), Arrays.asList("CREATE TABLE `blob` (\n  `pikapika` int(11) NOT NULL,\n  PRIMARY KEY (`bloatware`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1"),
        emptyList, emptyList,
        Arrays.asList("ALTER TABLE `blob` ADD PRIMARY KEY (`pikapika`);", "ALTER TABLE `broach` ADD PRIMARY KEY (`mewtwo`);"),
        Arrays.asList("ALTER TABLE `broach` ADD PRIMARY KEY (`mewtwo`);")),
      Arguments.of("Complete Removal", Arrays.asList("blob", "broach"), Arrays.asList("CREATE TABLE `blob` (\n  `pikapika` int(11) NOT NULL,\n  PRIMARY KEY (`bloatware`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1",
          "CREATE TABLE `broach` (\n  `mewtwo` int(11) NOT NULL\n) ENGINE=InnoDB DEFAULT CHARSET=latin1"),
        emptyList, emptyList,
        Arrays.asList("ALTER TABLE `blob` ADD PRIMARY KEY (`pikapika`);", "ALTER TABLE `broach` ADD PRIMARY KEY (`mewtwo`);"),
        emptyList)
    );
  }

  static Stream<Arguments> sqlDatabaseUpdateTables() {
    return Stream.of(
      Arguments.of("No Table Differences", Arrays.asList(tableName2, tableName5), Arrays.asList(tableCreate22, tableCreate20),
        Arrays.asList(tableCreate22, tableCreate20), emptyList),
      Arguments.of("Many Differences Single Table", Arrays.asList(tableName5), Arrays.asList("CREATE TABLE `ci_sessions` (\n  `id` varchar(40) NOT NULL,\n  "
        + "`ip_address` varchar(45) NOT NULL,\n  `timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  "
        + "`data` blob NOT NULL,\n  KEY `add` (`id`),\n  KEY `modify` (`data`),\n  "
        + "KEY `leave` (`data`,`id`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1"),
        Arrays.asList("CREATE TABLE `ci_sessions` (\n  `ip_address` varchar(40) NOT NULL,\n  "
          + "`timestamp` int(11) unsigned NOT NULL DEFAULT \'0\',\n  `data` blob NOT NULL,\n  "
          + "`data2` blob NOT NULL,\n  UNIQUE KEY `delete` (`id`)\n,  KEY `modify` (`data`,`ip_address`),\n  "
          + "KEY `leave` (`data`,`id`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin2"),
        Arrays.asList("ALTER TABLE `ci_sessions` CHARACTER SET latin1,\n  DROP INDEX `delete`,"
          + "\n  ADD COLUMN `id` varchar(40) NOT NULL,\n  MODIFY COLUMN `ip_address`"
          + " varchar(45) NOT NULL AFTER `id`,\n  MODIFY COLUMN `timestamp` int(10) unsigned "
          + "NOT NULL DEFAULT \'0\' AFTER `ip_address`,\n  DROP COLUMN `data2`,\n  ADD INDEX "
          + "`add` (`id`),\n  DROP INDEX `modify`,\n  ADD INDEX `modify` (`data`);")),
        Arguments.of("Many Differences Multiple Tables", Arrays.asList(tableName5, tableName2), Arrays.asList("CREATE TABLE `ci_sessions` (\n  `id` varchar(40) NOT NULL,\n  "
          + "`ip_address` varchar(45) NOT NULL,\n  `timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  "
          + "`data` blob NOT NULL,\n  KEY `add` (`id`),\n  KEY `modify` (`data`),\n  "
          + "KEY `leave` (`data`,`id`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1", tableCreate22),
          Arrays.asList("CREATE TABLE `ci_sessions` (\n  `ip_address` varchar(40) NOT NULL,\n  "
            + "`timestamp` int(11) unsigned NOT NULL DEFAULT \'0\',\n  `data` blob NOT NULL,\n  "
            + "`data2` blob NOT NULL,\n  UNIQUE KEY `delete` (`id`)\n,  KEY `modify` (`data`,`ip_address`),\n  "
            + "KEY `leave` (`data`,`id`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin2",
            "CREATE TABLE `bloat` (\n  `bloatware` int(10) NOT NULL\n) ENGINE=InnoDB DEFAULT CHARSET=latin1"),
          Arrays.asList("ALTER TABLE `ci_sessions` CHARACTER SET latin1,\n  DROP INDEX `delete`,"
            + "\n  ADD COLUMN `id` varchar(40) NOT NULL,\n  MODIFY COLUMN `ip_address`"
            + " varchar(45) NOT NULL AFTER `id`,\n  MODIFY COLUMN `timestamp` int(10) unsigned "
            + "NOT NULL DEFAULT \'0\' AFTER `ip_address`,\n  DROP COLUMN `data2`,\n  ADD INDEX "
            + "`add` (`id`),\n  DROP INDEX `modify`,\n  ADD INDEX `modify` (`data`);",
            "ALTER TABLE `bloat` MODIFY COLUMN `bloatware` int(11) NOT NULL,\n  ADD PRIMARY KEY (`bloatware`),\n  AUTO_INCREMENT=17;"))
      );
  }

  static Stream<Arguments> sqlDatabaseCompareTables() {
    return Stream.of(
      Arguments.of("No Tables To Modify", Arrays.asList(tableName2, tableName5), Arrays.asList(tableCreate22, tableCreate20),
        Arrays.asList(tableName2, tableName5), Arrays.asList(tableCreate22, tableCreate20), emptyList),
      Arguments.of("Add Single Table", Arrays.asList(tableName2, tableName5), Arrays.asList(tableCreate22, tableCreate20),
        Arrays.asList(tableName2), Arrays.asList(tableCreate22), Arrays.asList(tableCreate20 + ";")),
      Arguments.of("Add All Tables", Arrays.asList(tableName2, tableName5), Arrays.asList(tableCreate22, tableCreate20),
        emptyList, emptyList, Arrays.asList(tableCreate20 + ";", tableCreate22 + ";")),
      Arguments.of("Drop Single Table", Arrays.asList(tableName2), Arrays.asList(tableCreate22),
        Arrays.asList(tableName2, tableName5), Arrays.asList(tableCreate22, tableCreate20),
        Arrays.asList(String.format(tableDropFmt, tableName5))),
      Arguments.of("Drop All Tables", emptyList, emptyList, Arrays.asList(tableName2, tableName5),
        Arrays.asList(tableCreate22, tableCreate20),
        Arrays.asList(String.format(tableDropFmt, tableName5), String.format(tableDropFmt, tableName2)))
    );
  }

  static Stream<Arguments> sqlDatabaseTableDiffs() {
    return Stream.of(
      Arguments.of("Same tables and table info", Arrays.asList(tableName2, tableName5), Arrays.asList(tableCreate22, tableCreate20),
        Arrays.asList(tableName2, tableName5), Arrays.asList(tableCreate22, tableCreate20), emptyList),
      Arguments.of("Common table has a difference", Arrays.asList(tableName2, tableName5), Arrays.asList(tableCreate22, tableCreate20),
        Arrays.asList(tableName5), Arrays.asList(tableCreate21), Arrays.asList(tableName5)),
      Arguments.of("No common tables", Arrays.asList(tableName2), Arrays.asList(tableCreate22),
        Arrays.asList(tableName5), Arrays.asList(tableCreate20), emptyList)
    );
  }

  static Stream<Arguments> sqlDatabaseUpdateViews() {
    return Stream.of(
      Arguments.of(Arrays.asList(viewName2, viewName3), Arrays.asList(viewCreate2, viewCreate3), Arrays.asList(viewName2),
      Arrays.asList(viewCreate2), Arrays.asList(String.format(viewDropFmt, viewName2), viewCreate2 + ";", viewCreate3 + ";"))
    );
  }

  static Stream<Arguments> sqlDatabaseGetStatements() {
    return Stream.of(
      Arguments.of("Getters test", Arrays.asList(tableName2), Arrays.asList(tableCreate22), emptyList,
        emptyList, emptyList),
      Arguments.of("No tables, views, or first steps", emptyList, emptyList, emptyList, emptyList, emptyList),
      Arguments.of("Views initialization", emptyList, emptyList, Arrays.asList(viewName2, viewName3),
        Arrays.asList(viewCreate2, viewCreate3), emptyList),
      Arguments.of("First steps initialization", emptyList, emptyList, emptyList, emptyList,
        Arrays.asList("ALTER TABLE `blob`\n ADD PRIMARY KEY (`pikapika`);", "ALTER TABLE `broach`\n ADD PRIMARY KEY (`mewtwo`);"))
    );
  }

  static Stream<Arguments> mysqlCompare() {
    return Stream.of(
      Arguments.of("Same Table Info", tableName5, tableCreate15, tableName5, tableCreate15,
        emptyList),
      Arguments.of("Single Index Addition", tableName5, "CREATE TABLE `ci_sessions` (\n  `id` varchar(40) NOT NULL,\n  "
        + "`ip_address` varchar(45) NOT NULL,\n  `timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  "
        + "`data` blob NOT NULL,\n  KEY `leave` (`data`,`id`),\n  KEY `add` (`id`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1",
        tableName5, tableCreate15, Arrays.asList("ALTER TABLE `ci_sessions` ADD INDEX `add` (`id`);")),
      Arguments.of("Multiple Index Addition", tableName5, "CREATE TABLE `ci_sessions` (\n  `id` varchar(40) NOT NULL,\n  "
        + "`ip_address` varchar(45) NOT NULL,\n  `timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  "
        + "`data` blob NOT NULL,\n  KEY `leave` (`data`,`id`),\n KEY `add` (`id`),\n  PRIMARY KEY (`id`,`ip_address`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1",
        tableName5, tableCreate15,  Arrays.asList("ALTER TABLE `ci_sessions` ADD INDEX `add` (`id`),\n  ADD PRIMARY KEY (`id`,`ip_address`);")),
      Arguments.of("Single Index Drop", tableName5, tableCreate17, tableName5,
        "CREATE TABLE `ci_sessions` (\n  `id` varchar(40) NOT NULL,\n  "
        + "`ip_address` varchar(45) NOT NULL,\n  `timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  "
        + "`data` blob NOT NULL,\n  KEY `leave` (`data`,`id`),\n  KEY `drop1` (`id`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1",
        Arrays.asList("ALTER TABLE `ci_sessions` DROP INDEX `drop1`;")),
      Arguments.of("Multiple Index Drop", tableName5, tableCreate17, tableName5,
        "CREATE TABLE `ci_sessions` (\n  `id` varchar(40) NOT NULL,\n  "
        + "`ip_address` varchar(45) NOT NULL,\n  `timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  "
        + "`data` blob NOT NULL,\n  KEY `leave` (`data`,`id`),\n  "
        + "KEY `drop1` (`id`),\n  KEY `drop2` (`id`,`ip_address`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1",
        Arrays.asList("ALTER TABLE `ci_sessions` DROP INDEX `drop1`,\n  DROP INDEX `drop2`;")),
      Arguments.of("Single Index Modification", tableName5, "CREATE TABLE `ci_sessions` (\n  `id` varchar(40) NOT NULL,\n  "
        + "`ip_address` varchar(45) NOT NULL,\n  `timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  "
        + "`data` blob NOT NULL,\n  KEY `leave` (`data`,`id`),\n  UNIQUE KEY `modify1` (`id`)\n"
        + ") ENGINE=InnoDB DEFAULT CHARSET=latin1", tableName5, "CREATE TABLE `ci_sessions` (\n  `id` varchar(40) NOT NULL,\n  "
        + "`ip_address` varchar(45) NOT NULL,\n  `timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  "
        + "`data` blob NOT NULL,\n  KEY `leave` (`data`,`id`),\n  KEY `modify1` (`id`)\n"
        + ") ENGINE=InnoDB DEFAULT CHARSET=latin1",
        Arrays.asList("ALTER TABLE `ci_sessions` DROP INDEX `modify1`,\n  ADD UNIQUE INDEX `modify1` (`id`);")),
        Arguments.of("Multiple Index Modification", tableName5, "CREATE TABLE `ci_sessions` (\n  `id` varchar(40) NOT NULL,\n  "
          + "`ip_address` varchar(45) NOT NULL,\n  `timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  "
          + "`data` blob NOT NULL,\n  KEY `leave` (`data`,`id`),\n  UNIQUE KEY `modify1` (`id`),\n"
          + "  KEY `modify2` (`ip_address`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1", tableName5,
          "CREATE TABLE `ci_sessions` (\n  `id` varchar(40) NOT NULL,\n  `ip_address` varchar(45) NOT NULL,\n  `timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  "
          + "`data` blob NOT NULL,\n  KEY `leave` (`data`,`id`),\n  KEY `modify1` (`id`),\n"
          + "  KEY `modify2` (`id`,`ip_address`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1",
          Arrays.asList("ALTER TABLE `ci_sessions` DROP INDEX `modify1`,\n  ADD UNIQUE INDEX `modify1` (`id`)"
            + ",\n  DROP INDEX `modify2`,\n  ADD INDEX `modify2` (`ip_address`);")),
        Arguments.of("Single Column Addition", tableName5, "CREATE TABLE `ci_sessions` (\n  `ip_address` varchar(45) NOT NULL,\n  "
          + "`timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  "
          + "`data` blob NOT NULL,\n  `id` varchar(40) NOT NULL\n) ENGINE=InnoDB DEFAULT CHARSET=latin1",
          tableName5, tableCreate19, Arrays.asList("ALTER TABLE `ci_sessions` ADD COLUMN `id` varchar(40) NOT NULL AFTER `data`;")),
        Arguments.of("Multiple Column Addition", tableName5, "CREATE TABLE `ci_sessions` (\n  `ip_address` varchar(45) NOT NULL,\n  "
          + "`timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  `data` blob NOT NULL,\n  `id` varchar(40) NOT NULL,\n"
          + "  `data2` blob NOT NULL\n) ENGINE=InnoDB DEFAULT CHARSET=latin1", tableName5,
        tableCreate19, Arrays.asList("ALTER TABLE `ci_sessions` ADD COLUMN `id` varchar(40) NOT NULL AFTER `data`,\n  "
            + "ADD COLUMN `data2` blob NOT NULL AFTER `id`;")),
        Arguments.of("Single Column Drop", tableName5, tableCreate20, tableName5,
          "CREATE TABLE `ci_sessions` (\n  `ip_address` varchar(45) NOT NULL,\n  "
          + "`timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  `data` blob NOT NULL,\n  `id` varchar(40) NOT NULL\n) ENGINE=InnoDB DEFAULT CHARSET=latin1",
          Arrays.asList("ALTER TABLE `ci_sessions` DROP COLUMN `id`;")),
        Arguments.of("Multiple Column Drop", tableName5, tableCreate20, tableName5,
          "CREATE TABLE `ci_sessions` (\n  `ip_address` varchar(45) NOT NULL,\n  "
          + "`timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  `data` blob NOT NULL,\n  "
          + "`id` varchar(40) NOT NULL,\n  `data2` blob NOT NULL" + "\n) ENGINE=InnoDB DEFAULT CHARSET=latin1",
          Arrays.asList("ALTER TABLE `ci_sessions` DROP COLUMN `data2`,\n  DROP COLUMN `id`;")),
        Arguments.of("Single Column Modification", tableName5, "CREATE TABLE `ci_sessions` (\n  `id` varchar(45) NOT NULL,\n  "
          + "`timestamp` int(10) unsigned NOT NULL DEFAULT \'0\'\n) ENGINE=InnoDB DEFAULT CHARSET=latin1",
          tableName5, "CREATE TABLE `ci_sessions` (\n  `id` varchar(45) NOT NULL,\n  "
          + "`timestamp` int(11) unsigned NOT NULL DEFAULT \'0\'\n) ENGINE=InnoDB DEFAULT CHARSET=latin1",
          Arrays.asList("ALTER TABLE `ci_sessions` MODIFY COLUMN `timestamp` int(10) unsigned NOT NULL DEFAULT \'0\' AFTER `id`;")),
        Arguments.of("Multiple Column Modification", tableName5, "CREATE TABLE `ci_sessions` (\n  `id` varchar(45) NOT NULL,\n  "
          + "`timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  `data2` blob NOT NULL\n) ENGINE=InnoDB DEFAULT CHARSET=latin1",
          tableName5, "CREATE TABLE `ci_sessions` (\n  `id` varchar(45) NOT NULL,\n  "
          + "`timestamp` int(11) unsigned NOT NULL DEFAULT \'0\',\n  `data2` blob\n) ENGINE=InnoDB DEFAULT CHARSET=latin1",
          Arrays.asList("ALTER TABLE `ci_sessions` MODIFY COLUMN `data2` blob NOT NULL AFTER `timestamp`,\n  "
            + "MODIFY COLUMN `timestamp` int(10) unsigned NOT NULL DEFAULT '0' AFTER `id`;")),
      Arguments.of("Complex", tableName5, "CREATE TABLE `ci_sessions` (\n  `id` varchar(40) NOT NULL,\n  "
        + "`ip_address` varchar(45) NOT NULL,\n  `timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  "
        + "`data` blob NOT NULL,\n  KEY `add` (`id`),\n  KEY `modify` (`data`),\n  "
        + "KEY `leave` (`data`,`id`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1", tableName5,
        "CREATE TABLE `ci_sessions` (\n  `ip_address` varchar(40) NOT NULL,\n  "
        + "`timestamp` int(11) unsigned NOT NULL DEFAULT \'0\',\n  `data` blob NOT NULL,\n  "
        + "`data2` blob NOT NULL,\n  UNIQUE KEY `delete` (`id`),\n  KEY `modify` (`data`,`ip_address`),\n  "
        + "KEY `leave` (`data`,`id`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin2",
        Arrays.asList("ALTER TABLE `ci_sessions` CHARACTER SET latin1,\n  DROP INDEX `delete`,"
          + "\n  ADD COLUMN `id` varchar(40) NOT NULL,\n  MODIFY COLUMN `ip_address`"
          + " varchar(45) NOT NULL AFTER `id`,\n  MODIFY COLUMN `timestamp` int(10) unsigned "
          + "NOT NULL DEFAULT \'0\' AFTER `ip_address`,\n  DROP COLUMN `data2`,\n  ADD INDEX "
          + "`add` (`id`),\n  DROP INDEX `modify`,\n  ADD INDEX `modify` (`data`);"))
    );
  }

  static Stream<Arguments> mysqlTableGetStatements() {
    return Stream.of(
      Arguments.of("Getters test", tableName2, tableCreate22,
        "latin1", "latin1_swedish_c", 17, Arrays.asList(tableColumn3), Arrays.asList("PRIMARY")),
      Arguments.of("Column and index list empty on initialization", null, "", "", "", 0, emptyList, emptyList),
      Arguments.of("Multiple column creation on initalization", tableName2, tableCreate12, "latin1",
        "latin1_swedish_c", 0, Arrays.asList(tableColumn3, tableColumn4), emptyList),
      Arguments.of("Multiple index creation on initalization", tableName4, tableCreate13, "latin1",
        "latin1_swedish_c", 0, Arrays.asList(tableColumn4, "vendor"), Arrays.asList(indexName6, indexName7)),
      Arguments.of("Foreign key creation on initalization", "products", tableCreate14, "",
        "latin1_swedish_c", 0, Arrays.asList("prd_id", "prd_name", "prd_price", "cat_id"), Arrays.asList("constraint_name"))
    );
  }

  static Stream<Arguments> sqliteCompare() {
    return Stream.of(
      Arguments.of("Same Table Info", tableName1, tableCreate2, tableName2, tableCreate2,
        emptyList),
      Arguments.of("Single Column Addition", tableName1, tableCreate6,
        tableName1, tableCreate3, Arrays.asList("ALTER TABLE helper ADD COLUMN Thor INTEGER (67) DEFAULT (12);")),
      Arguments.of("Multiple Column Addition", tableName1, tableCreate5, tableName1,
        tableCreate3, Arrays.asList("ALTER TABLE helper ADD COLUMN Thor INTEGER (67) DEFAULT (12);\nALTER TABLE helper ADD COLUMN truthtable STRING (12);")),
      Arguments.of("Multiple Regular Index Addition", tableName1, tableCreate6 + ";\nCREATE INDEX add1 ON helper (hulk, Thor);\nCREATE UNIQUE INDEX add2 ON helper (Thor);", tableName1,
        tableCreate6, Arrays.asList("CREATE UNIQUE INDEX add2 ON helper (Thor);\nCREATE INDEX add1 ON helper (hulk, Thor);")),
      Arguments.of("Multiple Regular Index Drop", tableName1, tableCreate2, tableName1,
        tableCreate2 + ";\nCREATE INDEX add1 ON helper (hulk, Thor);\nCREATE INDEX add2 ON helper (Thor)", Arrays.asList("DROP INDEX add2;\nDROP INDEX add1;")),
      Arguments.of("Single Regular Index Modification", tableName1, tableCreate2 + ";\nCREATE INDEX add1 ON helper (hulk, Thor)",
        tableName1, tableCreate2 + ";\nCREATE INDEX add1 ON helper (hulk)",
        Arrays.asList("DROP INDEX add1;\nCREATE INDEX add1 ON helper (hulk, Thor);")),
      Arguments.of("Multiple Regular Index Modification", tableName1, tableCreate2 + ";\nCREATE INDEX add1 ON helper (hulk, Thor);\nCREATE INDEX add2 ON helper (Thor)",
        tableName1, tableCreate2 + ";\nCREATE INDEX add1 ON helper (hulk);\nCREATE UNIQUE INDEX add2 ON helper (Thor)",
        Arrays.asList("DROP INDEX add2;\nCREATE INDEX add2 ON helper (Thor);\nDROP INDEX add1;\nCREATE INDEX add1 ON helper (hulk, Thor);")),
      Arguments.of("Recreate Table No Common Columns", tableName1, tableCreate3, tableName1, tableCreate,
        expectedRecreateStatmentes(tableName1, tableCreate3, "", false, null)),
      Arguments.of("Recreate Table Due To Drop Column", tableName1, tableCreate3, tableName1, tableCreate6,
        expectedRecreateStatmentes(tableName1, tableCreate3, insertStatement, true, null)),
      Arguments.of("Recreate Table Due To Drop Columns", tableName1, tableCreate3, tableName1, tableCreate5,
        expectedRecreateStatmentes(tableName1, tableCreate3, insertStatement, true, null)),
      Arguments.of("Recreate Table Due To Modify Column", tableName1, tableCreate3, tableName1, "CREATE TABLE " + tableName1 + " (hulk STRING (11))",
        expectedRecreateStatmentes(tableName1, tableCreate3, insertStatement, true, null)),
      Arguments.of("Recreate Table Due To Modify Columns", tableName1, tableCreate8, tableName1, tableCreate9,
        expectedRecreateStatmentes(tableName1, tableCreate8, insertStatement2, true, null)),
      Arguments.of("Recreate Table Due To Add Primary Key", tableName2, tableCreate10, tableName2, tableCreate11,
        expectedRecreateStatmentes(tableName2, tableCreate10, insertStatement3, true, null)),
      Arguments.of("Recreate Table Due To Modify Primary Key", tableName2, "CREATE TABLE bloat (Thor INTEGER (67) DEFAULT (12), ache BLOB (66) PRIMARY KEY)",
        tableName2, "CREATE TABLE bloat (Thor INTEGER (67) DEFAULT (12), ache BLOB (6) PRIMARY KEY)",
        expectedRecreateStatmentes(tableName2, tableCreate10, insertStatement3, true, null)),
      Arguments.of("Recreate Table Due To Drop Primary Key", tableName2, tableCreate11, tableName2,
        tableCreate10, expectedRecreateStatmentes(tableName2, tableCreate11, insertStatement3, true, null)),
      Arguments.of("Recreate Table With No Common Columns And Extra Create Statements",
        tableName1, "CREATE TABLE helper (hawkeye STRING (12));\n" + extraCreate, tableName1,
        tableCreate6, expectedRecreateStatmentes(tableName1, "CREATE TABLE helper (hawkeye STRING (12));\n" + extraCreate, "", false, null)),
      Arguments.of("Recreate Table With Extra Create Statements", tableName1, tableCreate3 + ";\n" + extraCreate,
        tableName1, tableCreate6, expectedRecreateStatmentes(tableName1, tableCreate3, insertStatement, true, extraCreate)),
      Arguments.of("Complex", tableName1, tableCreate6 + ";\nCREATE INDEX add1 ON helper (hulk, Thor);\n CREATE UNIQUE INDEX add2 ON helper (Thor)",
        tableName1, tableCreate3 + ";\nCREATE INDEX drop1 ON helper (hulk, Thor);\n CREATE UNIQUE INDEX drop2 ON helper (Thor)",
        Arrays.asList("DROP INDEX drop1;\nDROP INDEX drop2;\n" + "ALTER TABLE helper ADD COLUMN Thor INTEGER (67) DEFAULT (12);\n"
          + "CREATE UNIQUE INDEX add2 ON helper (Thor);\nCREATE INDEX add1 ON helper (hulk, Thor);"))
    );
  }

  static Stream<Arguments> sqliteTableGetStatements() {
    return Stream.of(
      Arguments.of("Getters test", tableName1, tableCreate6,
        Arrays.asList(tableColumn1, tableColumn2), emptyList),
      Arguments.of("No indexes or columns on initialization", null, null, emptyList, emptyList),
      Arguments.of("Two columns after initialization", tableName2, tableCreate7,
        Arrays.asList(tableColumn3, tableColumn4), emptyList),
      Arguments.of("Two indexes after initialization", tableName2, tableCreate,
        Arrays.asList(tableColumn3, tableColumn4),  Arrays.asList(indexName6, indexName7)),
      Arguments.of("Foreign key addittion", tableName3, String.format("CREATE TABLE %s (BookId INTEGER PRIMARY KEY, Title TEXT, AuthorId INTEGER,FOREIGN KEY(AuthorId) REFERENCES Authors(AuthorId));", tableName3),
        Arrays.asList("BookId", "Title", "AuthorId"), Arrays.asList("FOREIGN KEY1"))
    );
  }

  static Stream<Arguments> viewGetStatements() {
    return Stream.of(
      Arguments.of(viewName,viewCreate)
    );
  }

  static Stream<Arguments> indexGetStatements() {
    return Stream.of(
      Arguments.of("shipment","shippingData"),
      Arguments.of("happydays","wonderfulLife")
    );
  }

  static Stream<Arguments> indexEquality() {
    return Stream.of(
      Arguments.of("Same values - eq", "shipment", "shippingData", "shipment", "shippingData",
        createIndexFmt4, createIndexFmt4, true),
      Arguments.of("Different columns - not eq", "shipment", "shippingData", "shipment", "shippingData",
        createIndexFmt4, createIndexFmt5, false),
      Arguments.of("Different Type - not eq", "shipment", "shippingData", "shipment", "shippingData",
        createIndexFmt4, createIndexFmt6, false)
    );
  }

  static Stream<Arguments> logFile() {
    return Stream.of(
      Arguments.of("Log file only has contents written to it", Arrays.asList(data1, data2), Arrays.asList(data3))
    );
  }

  static Stream<Arguments> lastRunFile() {
    return Stream.of(
      Arguments.of("Last run file only has contents written to it", Arrays.asList(data1, data2), Arrays.asList(data3))
    );
  }

  static Stream<Arguments> fileExistence() {
    return Stream.of(
      Arguments.of("File is labeled as nonexistent when not found", "eroniousFile.test", false)
    );
  }

  static Stream<Arguments> columnGetStatements() {
    return Stream.of(
      Arguments.of("shipmentID","int(11) NOT NULL")
    );
  }

  static Stream<Arguments> collectionGetStatements() {
    return Stream.of(
      Arguments.of(collectionName1, size1, true),
      Arguments.of(collectionName2, size2, false)
    );
  }

  static Stream<Arguments> collectionEquality() {
    return Stream.of(
      Arguments.of("Name differs - not eq", collectionName1, size1, true, collectionName2,
        size1, true, false),
      Arguments.of("Capped value differs - not eq", collectionName1, size1, true, collectionName1,
        size1, false, false),
      Arguments.of("Size value differs - not eq", collectionName1, size1, true, collectionName1,
        size2, true, false),
      Arguments.of("Same values - eq", collectionName1, size1, true, collectionName1,
        size1, true, true)
    );
  }

  static Stream<Arguments> bucketCompare() {
    return Stream.of(
      Arguments.of("Empty Bucket", emptyList, emptyList, emptyList, emptyList,
        emptyList, emptyList, emptyList),
      Arguments.of("Create one document", Arrays.asList(documentName1),
        emptyList, emptyList, emptyList, emptyList, emptyList,
        Arrays.asList(String.format(createDocumentFmt, documentName1))),
      Arguments.of("Create two documents", Arrays.asList(documentName1, documentName2),
        emptyList, emptyList, emptyList, emptyList, emptyList,
        Arrays.asList(String.format(createDocumentFmt, documentName1),
        String.format(createDocumentFmt, documentName2))),
      Arguments.of("Delete one document", emptyList, Arrays.asList(documentName1),
        emptyList, emptyList, emptyList, emptyList,
        Arrays.asList(String.format(deleteDocumentFmt, documentName1))),
      Arguments.of("Delete two documents",emptyList, Arrays.asList(documentName1, documentName2),
        emptyList, emptyList, emptyList, emptyList,
        Arrays.asList(String.format(deleteDocumentFmt, documentName1), String.format(deleteDocumentFmt, documentName2))),
      Arguments.of("Create one index", emptyList, emptyList, Arrays.asList(indexName1),
        Arrays.asList(String.format(createIndexFmt1, indexName1)), emptyList, emptyList,
        Arrays.asList(String.format(createIndexFmt1, indexName1) + ";")),
      Arguments.of("Create two indexes", emptyList, emptyList, Arrays.asList(indexName1, indexName2),
        Arrays.asList(String.format(createIndexFmt1, indexName1),
        String.format(createIndexFmt2, indexName2)), emptyList, emptyList,
        Arrays.asList(String.format(createIndexFmt2, indexName2) + ";", String.format(createIndexFmt1, indexName1) + ";")),
      Arguments.of("Drop one index", emptyList, emptyList, emptyList, emptyList,
        Arrays.asList(indexName1), Arrays.asList(String.format(createIndexFmt1, indexName1)),
        Arrays.asList(String.format(dropIndexFmt, indexName1))),
      Arguments.of("Drop two indexes", emptyList, emptyList, emptyList, emptyList,
        Arrays.asList(indexName1, indexName2),
        Arrays.asList(String.format(createIndexFmt1, indexName1),
        String.format(createIndexFmt2, indexName2)),
        Arrays.asList(String.format(dropIndexFmt, indexName2), String.format(dropIndexFmt, indexName1))),
      Arguments.of("Modify one index", emptyList, emptyList, Arrays.asList(indexName1),
        Arrays.asList(String.format(createIndexFmt1, indexName1)), Arrays.asList(indexName1),
        Arrays.asList(String.format(createIndexFmt2, indexName1)),
        Arrays.asList(String.format(dropIndexFmt, indexName1), String.format(createIndexFmt1, indexName1) + ";")),
      Arguments.of("Add, drop, and modify a document and index",
        Arrays.asList(documentName1, documentName3),
        Arrays.asList(documentName4, documentName3),
        Arrays.asList(indexName4, indexName3),
        Arrays.asList(String.format(createIndexFmt1, indexName4), String.format(createIndexFmt1, indexName3)),
        Arrays.asList(indexName3, indexName5),
        Arrays.asList(String.format(createIndexFmt1, indexName3), String.format(createIndexFmt2, indexName5)),
        Arrays.asList(String.format(createDocumentFmt, documentName1), String.format(deleteDocumentFmt, documentName4),
          String.format(dropIndexFmt, indexName5), String.format(createIndexFmt1, indexName4) + ";"))
    );
  }

  // helper functions for creating test params
  static List<String> expectedRecreateStatmentes(String tableName, String tableCreate,
    String insertStatement, boolean hasCommonColumns, String extraCreate) {

    if (hasCommonColumns) {
      if (extraCreate != null) {
        return  Arrays.asList(String.format("ALTER TABLE %s RENAME TO temp_table;", tableName),
          tableCreate + ";", insertStatement, "DROP TABLE temp_table;", extraCreate + ";");
      }

      return Arrays.asList(String.format("ALTER TABLE %s RENAME TO temp_table;", tableName),
        tableCreate + ";", insertStatement, "DROP TABLE temp_table;");
    } else if (extraCreate != null) {
      return  Arrays.asList("DROP TABLE " + tableName + ";\n", extraCreate + ";");
    }

    return Arrays.asList("DROP TABLE " + tableName + ";", tableCreate + ";");
  }
}
