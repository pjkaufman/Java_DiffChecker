import static org.junit.Assert.assertEquals;
import org.junit.Test;
import java.util.ArrayList;
import dbdiffchecker.Column;
import dbdiffchecker.Index;
import dbdiffchecker.Table;

public class TableTest {

  private Table table1, table2;
  private String name, create, collation, charSet, autoIncrement, details, columns;
  private Column column1, column2;
  private Index index1, index2;

  @Test
  public void testGetStatements() {
    name = "`bloat`";
    create = "CREATE TABLE `bloat` (\n  `bloatware` int(11) NOT NULL,\n  PRIMARY KEY (`bloatware`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    charSet = "latin1";
    autoIncrement = "17";
    table1 = new Table(name, create);
    collation = "latin1_swedish_c";
    table1.setCollation(collation);
    table1.setAutoIncrement(autoIncrement);

    // start assertions
    assertEquals("The name of the table should be the one passed into the constructor",
      name, table1.getName());
    assertEquals("The create statement of the table should be the one passed into the constructor",
      create + ";", table1.getCreateStatement());
    assertEquals("The charSet of the table should be the same one from the create statement",
      charSet, table1.getCharSet());
    assertEquals("The autoIncrement value of the table should be equal to the one passed to setAutoIncrement",
      autoIncrement, table1.getAutoIncrement());
    assertEquals("The collation of the table should be equal to the one passed to setCollation",
      collation, table1.getCollation());
    
    autoIncrement = "19";
    table1.setAutoIncrement(autoIncrement);
    assertEquals("The autoIncrement value of the table should be equal to the one passed to setAutoIncrement",
      autoIncrement, table1.getAutoIncrement());
    
    charSet = "latin12";
    table1.setCharSet(charSet);
    assertEquals("The charSet of the table should be the same one passed into setCharSet",
      charSet, table1.getCharSet());
  }

  @Test
  public void testAddColumn() {
    name = "`bloat`";
    create = "CREATE TABLE `bloat` (\n  `bloatware` int(11) NOT NULL,\n  PRIMARY KEY (`bloatware`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    table1 = new Table(name, create);
    name = "`bloatware`";
    details = name + " int(11) NOT NULL";
    column1 = new Column(name, details);
    name = "`shipmentID`";
    details = name + " int(11) NOT NULL";
    column2 = new Column(name, details);
    assertEquals("The size of the column list for the table should be 0 when empty",
      0, table1.getColumns().size());
    
    table1.addColumn(column1);
    assertEquals("The size of the column list for the table should be 1 when one column has been added",
      1, table1.getColumns().size());
    assertEquals("The column passed to addColumn should be in the tables column list",
      true, table1.getColumns().containsKey(column1.getName()));
    
    table1.addColumn(column2);
    assertEquals("The size of the column list for the table should be 2 when two colmns have been added to the column list",
      2, table1.getColumns().size());
    assertEquals("The column passed to addColumn should be in the tables column list",
      true, table1.getColumns().containsKey(column2.getName()));
  }

  @Test
  public void testAddIndex() {
    name = "bloat";
    create = "CREATE TABLE `bloat` (\n  `bloatware` int(11) NOT NULL,\n  PRIMARY KEY (`bloatware`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    table1 = new Table(name, create);
    name = "shipment";
    columns = "`shippingID`,`vendor`";
    create = "CREATE INDEX `" + name + "` ON `" + table1.getName() + "` (" + columns + ")";
    index1 = new Index(name, create, columns);
    name = "shipped";
    columns = "`shippingID`";
    create = "CREATE INDEX `" + name + "` ON `" + table1.getName() + "` (" + columns + ")";
    index2 = new Index(name, create, columns);
    assertEquals("The size of the index list for the table should be 0 when empty",
    0, table1.getIndices().size());
  
    table1.addIndex(index1);
    assertEquals("The size of the column list for the table should be 1 when one index has been added",
      1, table1.getIndices().size());
    assertEquals("The index passed to addIndex should be in the tables index list",
      true, table1.getIndices().containsKey(index1.getName()));
    
    table1.addIndex(index2);
    assertEquals("The size of the index list for the table should be 2 when two indices have been added to the column list",
      2, table1.getIndices().size());
    assertEquals("The index passed to addIndex should be in the tables index list",
      true, table1.getIndices().containsKey(index2.getName())); 
  }

  @Test
  public void testEquals() {
    ArrayList<String> sql;
    String expectedSQL = "ALTER TABLE `ci_sessions`\nCHARACTER SET latin1, \nDROP INDEX `delete`, " + 
      "\nADD COLUMN `id` varchar(40) NOT NULL AFTER `data`, \nMODIFY COLUMN `ip_address` varchar(45) NOT NULL, " + 
      "\nMODIFY COLUMN `timestamp` int(10) unsigned NOT NULL DEFAULT \'0\', \nDROP COLUMN `data2`, " +
      "\nCREATE INDEX `add` ON `ci_sessions` (`id`), \nDROP INDEX `modify`, \nCREATE INDEX `modify` ON " +
      "`ci_sessions` (`data`);";
    // setup table1
    name = "ci_sessions";
    create = "CREATE TABLE `ci_sessions` (\n  `id` varchar(40) NOT NULL,\n  `ip_address` varchar(45) NOT NULL,\n  `timestamp` int(10) unsigned NOT NULL DEFAULT \'0\',\n  `data` blob NOT NULL,\n  PRIMARY KEY (`id`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin1";
    table1 = new Table(name, create);
    // add columns
    name = "id";
    details = "varchar(40) NOT NULL";
    table1.addColumn(new Column(name, details));
    name = "ip_address";
    details = "varchar(45) NOT NULL";
    table1.addColumn(new Column(name, details));
    name = "timestamp";
    details = "int(10) unsigned NOT NULL DEFAULT \'0\'";
    table1.addColumn(new Column(name, details));
    name = "data";
    details = "blob NOT NULL";
    table1.addColumn(new Column(name, details));
    // add indexes
    name = "add";
    columns = "`id`";
    create = "CREATE INDEX `" + name + "` ON `" + table1.getName() + "` (" + columns + ")";
    table1.addIndex(new Index(name, create, columns));
    name = "modify";
    columns = "`data`";
    create = "CREATE INDEX `" + name + "` ON `" + table1.getName() + "` (" + columns + ")";
    table1.addIndex(new Index(name, create, columns));
    name = "leave";
    columns = "`data`,`id`";
    create = "CREATE INDEX `" + name + "` ON `" + table1.getName() + "` (" + columns + ")";
    table1.addIndex(new Index(name, create, columns));
    // setup table2
    name = "ci_sessions";
    create = "CREATE TABLE `ci_sessions` (\n  `ip_address` varchar(40) NOT NULL,\n  `timestamp` int(11) unsigned NOT NULL DEFAULT \'0\',\n  `data` blob NOT NULL,\n  `data2` blob NOT NULL,\n PRIMARY KEY (`id`)\n) ENGINE=InnoDB DEFAULT CHARSET=latin2";
    table2 = new Table(name, create);
    // add columns
    name = "ip_address";
    details = "varchar(40) NOT NULL";
    table2.addColumn(new Column(name, details));
    name = "timestamp";
    details = "int(11) unsigned NOT NULL DEFAULT \'0\'";
    table2.addColumn(new Column(name, details));
    name = "data";
    details = "blob NOT NULL";
    table2.addColumn(new Column(name, details));
    name = "data2";
    details = "blob NOT NULL";
    table2.addColumn(new Column(name, details));
    // add indexes
    name = "delete";
    columns = "`id`";
    create = "CREATE UNIQUE INDEX `" + name + "` ON `" + table1.getName() + "` (" + columns + ")";
    table2.addIndex(new Index(name, create, columns));
    name = "modify";
    columns = "`data`,`ip_address`";
    create = "CREATE INDEX `" + name + "` ON `" + table1.getName() + "` (" + columns + ")";
    table2.addIndex(new Index(name, create, columns));
    name = "leave";
    columns = "`data`,`id`";
    create = "CREATE INDEX `" + name + "` ON `" + table1.getName() + "` (" + columns + ")";
    table2.addIndex(new Index(name, create, columns));
    // do comparison with equals
    sql = table1.equals(table2);
    assertEquals("The sql generated should add a column, drop a column, modify two columns, drop two indexes," + 
      " add two indexes, add a charset", true, sql.contains(expectedSQL));   
  }
}