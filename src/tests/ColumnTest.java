import static org.junit.Assert.assertEquals;
import org.junit.Test;
import dbdiffchecker.Column;

public class ColumnTest {

  private Column test;
  private String name, details;

  @Test
  public void testGetStatements() {
    name = "shipmentID";
    details = "int(11) NOT NULL";
    test = new Column(name, details);

    // start assertions
    assertEquals("The name of the column should be the one passed into the constructor",
      name, test.getName());
    assertEquals("The details of the column should be the one passed into the constructor",
      details, test.getDetails());
  }
}