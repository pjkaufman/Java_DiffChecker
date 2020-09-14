package test.unit;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import dbdiffchecker.sql.Index;

@DisplayName("Index Tests")
public class IndexTest {
  private static final String CREATE_FORMAT = "CREATE INDEX `%s` ON `%s` (`shippingID`,`vendor`)";
  private static final String DROP_FORMAT = "DROP INDEX `%s`";

  @DisplayName("Getter Tests")
  @ParameterizedTest
  @MethodSource({"test.TestParamProvider#indexGetStatements"})
  public void testGetStatements(String name, String tableName) {
    String create = String.format(CREATE_FORMAT, name, tableName);
    String drop = String.format(DROP_FORMAT, name);
    Index index = new Index(name, create, drop);

    assertAll(
      () -> assertEquals(name, index.getName()),
      () -> assertEquals(create, index.getCreateStatement()),
      () -> assertEquals(drop, index.getDrop())
    );
  }

  @DisplayName("Equality Tests")
  @ParameterizedTest
  @MethodSource({"test.TestParamProvider#indexEquality"})
  public void testIndexEquality(String testName, String name, String tableName,
    String name2, String tableName2, String createFormat, String createFormat2, boolean expectedResult) {

    Index index = new Index(name, String.format(createFormat, name, tableName), String.format(DROP_FORMAT, name));
    Index index2 = new Index(name2, String.format(createFormat2, name2, tableName2), String.format(DROP_FORMAT, name2));

    assertEquals(expectedResult, index.getName().equals(index2.getName()) && index.equals(index2));
  }
}
