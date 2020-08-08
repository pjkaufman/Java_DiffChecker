package test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ FileHandlerTest.class, SQLDatabaseTest.class, SQLiteTableTest.class, MySQLTableTest.class,
    ColumnTest.class, BucketTest.class, IndexTest.class, MongoDBTest.class, CollectionsTest.class, ViewTest.class })
public class UnitTestSuite {
  // This class remains empty, it is used only as a holder for the above
  // annotations
}