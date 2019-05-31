import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import dbdiffchecker.sql.Column;
import dbdiffchecker.sql.SQLDatabase;
import dbdiffchecker.FileHandler;
import dbdiffchecker.Database;
import dbdiffchecker.sql.Index;
import dbdiffchecker.sql.Table;
import dbdiffchecker.sql.MySQLTable;
import dbdiffchecker.sql.SQLiteTable;
import dbdiffchecker.nosql.Bucket;

/**
 * A unit test that makes sure that the FileHandler object works as intended.
 * @author Peter Kaufman
 * @version 5-31-19
 * @since 5-11-19
 */
public class FileHandlerTest {
        private Table table1, table2;
        private String name, create, drop;
        private ArrayList<String> statements = new ArrayList<>();
        private ArrayList<String> expectedStatements = new ArrayList<>();
        private Database db;

        @Before
        /**
         * Sets up for the serailization compare by resetiing the list of expected
         * statements and statements.
         * @author Peter Kaufman
         */
        public void setupForCompare() {
                statements.clear();
                expectedStatements.clear();
        }

        @Test
        /**
         * Tests whether the writting and reading functions work as intended.
         * @author Peter Kaufman
         * @throws Exception Error writting to or reading from log files.
         */
        public void testWriteToAndReadFromFile() throws Exception {
                String data = "Test addition of data", data2 = "Another addition of text...", data3 = "erroneous...";
                ArrayList<String> fileContents = new ArrayList<>(), fileContents2 = new ArrayList<>(),
                                dataToAdd = new ArrayList<>();
                // log file test
                FileHandler.writeToFile(data);
                fileContents = FileHandler.readFrom(FileHandler.logFileName);
                assertEquals("The file should contain one element after the first addition", 1, fileContents.size());
                assertEquals("The file should contain the added content", true, fileContents.get(0).contains(data));
                FileHandler.writeToFile(data2);
                fileContents = FileHandler.readFrom(FileHandler.logFileName);
                assertEquals("The file should contain two element after the second addition", 2, fileContents.size());
                assertEquals("The file should contain the added content", true, fileContents.get(0).contains(data));
                assertEquals("The file should contain the added content", true, fileContents.get(1).contains(data2));
                assertEquals("The file should not contain content not added", false,
                                fileContents.get(0).contains(data3));
                assertEquals("The file should not contain content not added", false,
                                fileContents.get(1).contains(data3));
                // lastRun test
                dataToAdd.addAll(fileContents);
                FileHandler.writeToFile(dataToAdd);
                fileContents2 = FileHandler.readFrom(FileHandler.lastSequelStatementFileName);
                assertEquals("The file should contain the same amount of elements as were written to be written to the file",
                                fileContents.size(), fileContents2.size());
                assertEquals("The file should be equal to the content that was to be added", true,
                                fileContents.equals(fileContents2));
                dataToAdd.add("An addition was made");
                FileHandler.writeToFile(dataToAdd);
                fileContents = FileHandler.readFrom(FileHandler.lastSequelStatementFileName);
                assertEquals("The file should contain the same amount of elements as the data to be added",
                                dataToAdd.size(), fileContents.size());
                assertEquals("The file should contain the added content", true, fileContents.equals(dataToAdd));
                assertEquals("The file should only equal what was added to it in its entirity", false,
                                fileContents2.equals(dataToAdd));
        }

        @Test
        /**
         * Tests whether the fileExists function works as intended.
         * @author Peter Kaufman
         * @throws Exception Error writting to the log file.
         */
        public void testFileExists() throws Exception {
                String fileName = "eroniousFile.test";
                assertEquals("The file should not exist because it has not been created", false,
                                FileHandler.fileExists(fileName));
                String data = "Test addition of data";
                // log file test
                FileHandler.writeToFile(data);
                fileName = FileHandler.logFileName;
                assertEquals("The file should exist because it has been created in the previous test", true,
                                FileHandler.fileExists(fileName));
        }

        @Test
        /**
         * Tests whether the serialization functions work as intended on a MySQL
         * database.
         * @author Peter Kaufman
         * @throws Exception Error sereliazing or deserializing a MySQL database.
         */
        public void testSerializationMySQL() throws Exception {
                db = new SQLDatabase();
                expectedStatements.add("ALTER TABLE `ci_sessions`\nCHARACTER SET latin1, \nDROP INDEX `delete`, "
                                + "\nADD COLUMN `id` varchar(40) NOT NULL, \nMODIFY COLUMN `ip_address`"
                                + " varchar(45) NOT NULL AFTER `id`, \nMODIFY COLUMN `timestamp` int(10) unsigned "
                                + "NOT NULL DEFAULT \'0\' AFTER `ip_address`, \nDROP COLUMN `data2`, \nADD INDEX "
                                + "`add` (`id`), \nDROP INDEX `modify`, \nADD INDEX `modify` (`data`);");
                // setup tables
                name = "ci_sessions";
                create = "CREATE TABLE `ci_sessions` (\n  `id` varchar(40) NOT NULL,\n  "
                                + "`ip_address` varchar(45) NOT NULL,\n  `timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  "
                                + "`data` blob NOT NULL,\n  KEY `add` (`id`)\n  KEY `modify` (`data`)\n  "
                                + "KEY `leave` (`data`, `id`)\n  PRIMARY KEY (`id`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
                table1 = new MySQLTable(name, create);
                create = "CREATE TABLE `ci_sessions` (\n  `ip_address` varchar(40) NOT NULL,\n  "
                                + "`timestamp` int(11) unsigned NOT NULL DEFAULT \'0\',\n  `data` blob NOT NULL,\n  "
                                + "`data2` blob NOT NULL,\n  UNIQUE KEY `delete` (`id`)\n  KEY `modify` (`data`,`ip_address`)\n  "
                                + "KEY `leave` (`data`, `id`)  \n  PRIMARY KEY (`id`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin2";
                table2 = new MySQLTable(name, create);
                ((SQLDatabase) db).getTables().put(table2.getName(), table2);
                // serialize database
                FileHandler.serializeDatabase(db, "");
                // deserialize database
                db = FileHandler.deserailizDatabase("");
                table2 = ((SQLDatabase) db).getTables().get(table2.getName());
                // do comparison with equals
                statements = table1.equals(table2);
                assertEquals("The sql generated should add a column, drop a column, modify two columns, drop two indexes,"
                                + " add two indexes, add a charset", expectedStatements, statements);
        }

        @Test
        /**
         * Tests whether the serialization functions work as intended on an SQLite
         * database.
         * @author Peter Kaufman
         * @throws Exception Error serializing or deserializing the SQLite database.
         */
        public void testSerializationSQLite() throws Exception {
                db = new SQLDatabase();
                expectedStatements.add("DROP INDEX drop1;\nDROP INDEX drop2;\n"
                                + "ALTER TABLE helper\n\tADD COLUMN Thor INTEGER (67) DEFAULT (12);\n"
                                + "CREATE UNIQUE INDEX add2 ON helper (Thor);\nCREATE INDEX add1 ON helper (hulk, Thor);");
                // setup tables
                name = "helper";
                create = "CREATE TABLE helper (hulk STRING (12), Thor INTEGER (67) DEFAULT (12));\n"
                                + "CREATE INDEX add1 ON helper (hulk, Thor);\n CREATE UNIQUE INDEX add2 ON helper (Thor)";
                table1 = new SQLiteTable(name, create);
                create = "CREATE TABLE helper (hulk STRING (12));\n"
                                + "CREATE INDEX drop1 ON helper (hulk, Thor);\n CREATE UNIQUE INDEX drop2 ON helper (Thor)";
                table2 = new SQLiteTable(name, create);
                ((SQLDatabase) db).getTables().put(table2.getName(), table2);
                // serialize database
                FileHandler.serializeDatabase(db, "");
                // deserialize database
                db = FileHandler.deserailizDatabase("");
                table2 = ((SQLDatabase) db).getTables().get(table2.getName());
                // do comparison
                statements = table1.equals(table2);
                assertEquals("The sql generated should add a column, drop two indexes, and add two indexes",
                                expectedStatements, statements);
        }

        @Test
        /**
         * Tests whether the serialization functions work as intended on a Couchbase
         * database.
         * @author Peter Kaufman
         * @throws Exception Error serializing or deserializing the SQLite database.
         */
        public void testSerializationCouchbase() throws Exception {
                name = "blob";
                expectedStatements.add("Create document: " + name);
                db = new Bucket();
                Bucket bucket2 = new Bucket();
                // add documents to bucket and bucket2
                ((Bucket) db).getDocuments().put(name, name);
                name = "dropDoc";
                expectedStatements.add("Drop document: " + name);
                bucket2.getDocuments().put(name, name);
                name = "leave";
                ((Bucket) db).getDocuments().put(name, name);
                bucket2.getDocuments().put(name, name);
                // create indices
                name = "create";
                drop = "DROP INDEX ``.`" + name + "`;";
                create = "CREATE INDEX `" + name + "` ON `development`";
                expectedStatements.add(create + ";");
                ((Bucket) db).getIndices().put(name, new Index(name, create, drop));
                name = "leave";
                drop = "DROP INDEX ``.`" + name + "`;";
                create = "CREATE INDEX `" + name + "` ON `development`";
                ((Bucket) db).getIndices().put(name, new Index(name, create, drop));
                bucket2.getIndices().put(name, new Index(name, create, drop));
                name = "drop";
                drop = "DROP INDEX ``.`" + name + "`;";
                create = "CREATE INDEX `" + name + "` ON `development` WHERE (`abv` > 6)";
                expectedStatements.add(expectedStatements.size() - 1, "DROP INDEX ``.`" + name + "`;");
                bucket2.getIndices().put(name, new Index(name, create, drop));
                // serialize database
                FileHandler.serializeDatabase(db, "");
                // deserialize database
                db = FileHandler.deserailizDatabase("");
                statements = db.compare(bucket2);
                assertEquals("There should be one index drop, one index create, one document drop, and one document create statment",
                                expectedStatements, statements);
        }
}
