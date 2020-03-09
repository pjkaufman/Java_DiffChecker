import static org.junit.Assert.assertEquals;
import org.junit.Test;
import dbdiffchecker.sql.View;

/**
 * A unit test that makes sure that the View object works as intended.
 * 
 * @author Peter Kaufman
 * @version 5-24-19
 * @since 5-10-19
 */
public class ViewTest {
  private View test;
  private String name, create;

  @Test
  /**
   * Tests whether the get statements inside of the View object work as intended.
   * 
   * @author Peter Kaufman
   */
  public void testGetStatements() {
    name = "viewShipment";
    create = "CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `userlist` AS select `users`.`userid` AS `userid`,`users`.`remove` AS `add` from `users`";
    test = new View(name, create);
    // start assertions
    assertEquals("The name of the view should be the one passed into the constructor", name, test.getName());
    assertEquals("The create statement of the view should be the one passed into the constructor", create + ";",
        test.getCreateStatement());
    assertEquals("The drop statement of the view should be DROP VIEW `name_of_view`;", "DROP VIEW `" + name + "`;",
        test.getDrop());
  }
}
