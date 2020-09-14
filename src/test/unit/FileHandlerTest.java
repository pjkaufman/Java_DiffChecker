package test.unit;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.File;

import dbdiffchecker.Database;
import dbdiffchecker.DatabaseDifferenceCheckerException;
import dbdiffchecker.FileHandler;
import dbdiffchecker.nosql.Bucket;
import dbdiffchecker.nosql.Collection;
import dbdiffchecker.nosql.MongoDB;
import dbdiffchecker.sql.Index;
import dbdiffchecker.sql.MySQLTable;
import dbdiffchecker.sql.SQLDatabase;
import dbdiffchecker.sql.SQLiteTable;
import dbdiffchecker.sql.Table;

@DisplayName("FileHandler Tests")
public class FileHandlerTest {
  private Table table1;
  private Table table2;
  private final String expectedStatementsMySQL = "ALTER TABLE `ci_sessions` CHARACTER SET latin1,\n  DROP INDEX `delete`,"
    + "\n  ADD COLUMN `id` varchar(40) NOT NULL,\n  MODIFY COLUMN `ip_address`"
    + " varchar(45) NOT NULL AFTER `id`,\n  MODIFY COLUMN `timestamp` int(10) unsigned "
    + "NOT NULL DEFAULT \'0\' AFTER `ip_address`,\n  DROP COLUMN `data2`,\n  ADD INDEX "
    + "`add` (`id`),\n  DROP INDEX `modify`,\n  ADD INDEX `modify` (`data`);";
  private final String mysqlTableCreate1 = "CREATE TABLE `ci_sessions` (\n  `id` varchar(40) NOT NULL,\n  `ip_address` varchar(45) NOT NULL,\n  `timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  `data` blob NOT NULL,\n  KEY `add` (`id`)\n  KEY `modify` (`data`)\n  KEY `leave` (`data`, `id`)\n  PRIMARY KEY (`id`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
  private final String mysqlTableCreate2 = "CREATE TABLE `ci_sessions` (\n  `ip_address` varchar(40) NOT NULL,\n  "
    + "`timestamp` int(11) unsigned NOT NULL DEFAULT \'0\',\n  `data` blob NOT NULL,\n  "
    + "`data2` blob NOT NULL,\n  UNIQUE KEY `delete` (`id`)\n  KEY `modify` (`data`,`ip_address`)\n  "
    + "KEY `leave` (`data`, `id`)  \n  PRIMARY KEY (`id`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin2";
  private List<String> actualStatements = new ArrayList<>();
  private List<String> expectedStatements = new ArrayList<>();
  private Database db;

  @BeforeEach
  public void setupForCompare() {
    File file = new File("log" +  File.separator);

    boolean success = file.delete();
    boolean dirCreated = file.mkdirs();
  }

  @AfterEach
  public void cleanUpAfterCompare() {
    File file = new File("log" +  File.separator);

    boolean success = file.delete();

    expectedStatements.clear();
  }

  @DisplayName("Log File Tests")
  @ParameterizedTest
  @MethodSource({"test.TestParamProvider#logFile"})
  public void testLogFile(String testName, List<String> expectedContents, List<String> dataNotToAdd) {
    List<String> actualContents = assertDoesNotThrow(()->{
      for (String data : expectedContents) {
        FileHandler.log(data);
      }

      return FileHandler.readFrom(FileHandler.LOG_FILE);
    });

    assertAll(
      () -> {
        for(int i = 0; i < expectedContents.size(); i++) {
          assertTrue(actualContents.get(i).contains(expectedContents.get(i)));
        }
      },
      () -> assertFalse(actualContents.contains(dataNotToAdd))
    );
  }

  @DisplayName("Last Run File Tests")
  @ParameterizedTest
  @MethodSource({"test.TestParamProvider#lastRunFile"})
  public void testLastRunFile(String testName, List<String> expectedContents, List<String> dataNotToAdd) {

    List<String> actualContents = assertDoesNotThrow(()->{
      FileHandler.writeToFile(expectedContents);

      return FileHandler.readFrom(FileHandler.LAST_RUN_FILE);
    });

    assertAll(
      () -> assertIterableEquals(expectedContents, actualContents),
      () -> assertFalse(actualContents.contains(dataNotToAdd))
    );
  }

  @DisplayName("File Existence Tests")
  @ParameterizedTest
  @MethodSource({"test.TestParamProvider#fileExistence"})
  public void testFileExistsIsFalseWhenFileIsNotPresent(String testName, String fileName, boolean expectedResult) {
    assertEquals(expectedResult, FileHandler.fileExists(fileName));
  }

  // @DisplayName("MySQL Serialization Tests")
  // @Disabled("Not in working condition yet")
  // @ParameterizedTest
  // @CsvSource({"Database contents are the same before and after serialization, ci_sessions,'', ci_sessions','','" + expectedStatementsMySQL + "'"})
  // public void testSerializationMySQL(String testName, String tableName, String tableCreate,
  //   String tableName2, String tableCreate2, String expectedStatement) {

  //   expectedStatements.add(expectedStatement);
  //   table1 = new MySQLTable(tableName, tableCreate);
  //   table2 = new MySQLTable(tableName2, tableCreate2);
  //   db = new SQLDatabase();

  //   ((SQLDatabase) db).getTables().put(table2.getName(), table2);
  //   db = assertDoesNotThrow(()->{
  //     FileHandler.serializeDatabase(db, "");

  //     return FileHandler.deserailizDatabase("");
  //   });

  //   table2 = ((SQLDatabase) db).getTables().get(table2.getName());
  //   actualStatements = table1.generateStatements(table2);

  //   assertIterableEquals(expectedStatements, actualStatements);
  // }

  // @Test
  // public void testSerializationSQLite() {
  //   expectedStatements.add(
  //       "DROP INDEX drop1;\nDROP INDEX drop2;\n" + "ALTER TABLE helper ADD COLUMN Thor INTEGER (67) DEFAULT (12);\n"
  //           + "CREATE UNIQUE INDEX add2 ON helper (Thor);\nCREATE INDEX add1 ON helper (hulk, Thor);");

  //   name = "helper";
  //   create = "CREATE TABLE helper (hulk STRING (12), Thor INTEGER (67) DEFAULT (12));\n"
  //       + "CREATE INDEX add1 ON helper (hulk, Thor);\n CREATE UNIQUE INDEX add2 ON helper (Thor)";
  //   table1 = new SQLiteTable(name, create);
  //   create = "CREATE TABLE helper (hulk STRING (12));\n"
  //       + "CREATE INDEX drop1 ON helper (hulk, Thor);\n CREATE UNIQUE INDEX drop2 ON helper (Thor)";
  //   table2 = new SQLiteTable(name, create);
  //   db = new SQLDatabase();

  //   ((SQLDatabase) db).getTables().put(table2.getName(), table2);
  //   try {
  //     FileHandler.serializeDatabase(db, "");

  //     db = FileHandler.deserailizDatabase("");
  //   } catch (DatabaseDifferenceCheckerException err) {
  //     fail(err.getMessage());
  //   }

  //   table2 = ((SQLDatabase) db).getTables().get(table2.getName());
  //   statements = table1.generateStatements(table2);

  //   assertEquals("The sql generated should add a column, drop two indexes, and add two indexes", expectedStatements,
  //       statements);
  // }

  // @Test
  // public void testSerializationCouchbase() {
  //   name = "blob";
  //   expectedStatements.add("Create Document: " + name);
  //   db = new Bucket();
  //   Bucket bucket2 = new Bucket();

  //   ((Bucket) db).getDocuments().put(name, name);
  //   name = "dropDoc";

  //   expectedStatements.add("Delete Document: " + name);

  //   bucket2.getDocuments().put(name, name);
  //   name = "leave";
  //   ((Bucket) db).getDocuments().put(name, name);
  //   bucket2.getDocuments().put(name, name);

  //   name = "create";
  //   String drop = "DROP INDEX `" + name + "`;";
  //   create = "CREATE INDEX `" + name + "` ON `development`";

  //   expectedStatements.add(create + ";");

  //   ((Bucket) db).getIndices().put(name, new Index(name, create, drop));
  //   name = "leave";
  //   drop = "DROP INDEX `" + name + "`;";
  //   create = "CREATE INDEX `" + name + "` ON `development`";
  //   ((Bucket) db).getIndices().put(name, new Index(name, create, drop));
  //   bucket2.getIndices().put(name, new Index(name, create, drop));
  //   name = "drop";
  //   drop = "DROP INDEX `" + name + "`;";
  //   create = "CREATE INDEX `" + name + "` ON `development` WHERE (`abv` > 6)";

  //   expectedStatements.add(expectedStatements.size() - 1, "DROP INDEX `" + name + "`;");

  //   bucket2.getIndices().put(name, new Index(name, create, drop));
  //   try {
  //     FileHandler.serializeDatabase(db, "");

  //     db = FileHandler.deserailizDatabase("");
  //   } catch (DatabaseDifferenceCheckerException err) {
  //     fail(err.getMessage());
  //   }

  //   statements = db.compare(bucket2);

  //   assertEquals(
  //       "There should be one index drop, one index create, one document drop, and one document create statment",
  //       expectedStatements, statements);
  // }

  // @Test
  // public void testSerializationMongo() {
  //   String createPre = "Create Collection: ";
  //   String deletePre = "Delete Collection: ";
  //   String name1 = "Skipper";
  //   String name2 = "Private";
  //   String name3 = "Commoner";
  //   String name4 = "Creeper";
  //   String name5 = "Creep";
  //   String name6 = "Pillager";
  //   String name7 = "Villager";
  //   expectedStatements.add(createPre + name7 + ", capped=true, size=234560");
  //   expectedStatements.add(createPre + name6);
  //   expectedStatements.add(deletePre + name5);
  //   expectedStatements.add(deletePre + name4);
  //   expectedStatements.add(deletePre + name1);
  //   expectedStatements.add(createPre + name1);
  //   expectedStatements.add(deletePre + name2);
  //   expectedStatements.add(createPre + name2 + ", capped=true, size=50000");

  //   Collection coll1 = new Collection(name1, false, 0);
  //   Collection coll2 = new Collection(name2, true, 50000);
  //   Collection coll12 = new Collection(name1, true, 67890);
  //   Collection coll22 = new Collection(name2, false, 0);
  //   Collection coll3 = new Collection(name3, false, 0);
  //   Collection coll4 = new Collection(name4, false, 0);
  //   Collection coll5 = new Collection(name3, true, 587390);
  //   Collection coll6 = new Collection(name6, false, 0);
  //   Collection coll7 = new Collection(name7, true, 234560);
  //   db = new MongoDB();
  //   MongoDB test1 = new MongoDB();
  //   ((MongoDB) db).getCollections().put(name1, coll1); // collection to modify
  //   ((MongoDB) db).getCollections().put(name2, coll2); // collection to modify
  //   ((MongoDB) db).getCollections().put(name3, coll3); // common collection
  //   ((MongoDB) db).getCollections().put(name6, coll6); // collection to add
  //   ((MongoDB) db).getCollections().put(name7, coll7); // collection to add
  //   test1.getCollections().put(name1, coll12); // collection to modify
  //   test1.getCollections().put(name2, coll22); // collection to modify
  //   test1.getCollections().put(name3, coll3); // common collection
  //   test1.getCollections().put(name4, coll4); // collection to drop
  //   test1.getCollections().put(name5, coll5); // collection to drop

  //   try {
  //     FileHandler.serializeDatabase(db, "");

  //     db = FileHandler.deserailizDatabase("");
  //   } catch (DatabaseDifferenceCheckerException err) {
  //     fail(err.getMessage());
  //   }

  //   statements = db.compare(test1);

  //   assertEquals(
  //       "The Mongo database should suggest 8 changes when 2 collections need to be updated (4),"
  //           + " 2 collections need to be created (2), and 2 collections need to be dropped (2).",
  //       expectedStatements, statements);
  // }
}
