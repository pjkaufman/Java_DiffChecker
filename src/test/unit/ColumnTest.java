package test.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import dbdiffchecker.sql.Column;

@DisplayName("Column Tests")
public class ColumnTest {

  @DisplayName("Getter Tests")
  @ParameterizedTest
  @MethodSource({"test.TestParamProvider#columnGetStatements"})
  public void testGetStatements(String name, String details) {
    Column test = new Column(name, details);

    assertAll(
      () -> assertEquals(name, test.getName()),
      () -> assertEquals(details, test.getDetails())
    );
  }
}
