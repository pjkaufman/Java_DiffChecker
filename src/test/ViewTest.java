package test;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import dbdiffchecker.sql.View;

/**
 * A unit test that makes sure that the View object works as intended.
 *
 * @author Peter Kaufman
 * @version 7-7-20
 * @since 5-10-19
 */
public class ViewTest {

  @Test
  public void testGetStatements() {
    String name = "viewShipment";
    String create = "CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `userlist` AS select `users`.`userid` AS `userid`,`users`.`remove` AS `add` from `users`";
    View test = new View(name, create);
    // start assertions
    assertEquals("The name of the view should be the one passed into the constructor", name, test.getName());
    assertEquals("The create statement of the view should be the one passed into the constructor", create + ";",
        test.getCreateStatement());
    assertEquals("The drop statement of the view should be DROP VIEW `name_of_view`;", "DROP VIEW `" + name + "`;",
        test.getDrop());
  }
}
