package test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import dbdiffchecker.sql.Column;

public class ColumnTest {

  @Test
  public void testGetStatements() {
    String name = "shipmentID";
    String details = "int(11) NOT NULL";
    Column test = new Column(name, details);

    assertEquals("The name of the column should be the one passed into the constructor", name, test.getName());
    assertEquals("The details of the column should be the one passed into the constructor", details, test.getDetails());
  }
}
