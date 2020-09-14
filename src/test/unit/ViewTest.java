package test.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import dbdiffchecker.sql.View;

@DisplayName("View Tests")
public class ViewTest {

  @DisplayName("Getter Tests")
  @ParameterizedTest
  @MethodSource({"test.TestParamProvider#viewGetStatements"})
  public void testGetStatements(String name, String create) {
    View view = new View(name, create);

    assertAll(
      () -> assertEquals(name, view.getName()),
      () -> assertEquals(create + ";", view.getCreateStatement()),
      () -> assertEquals("DROP VIEW `" + name + "`;", view.getDrop())
    );
  }
}
