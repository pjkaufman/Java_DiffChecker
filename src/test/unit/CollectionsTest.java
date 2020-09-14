package test.unit;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import dbdiffchecker.nosql.Collection;

@DisplayName("Collection Tests")
public class CollectionsTest {

  @DisplayName("Getter Tests")
  @ParameterizedTest
  @MethodSource({"test.TestParamProvider#collectionGetStatements"})
  public void testGetStatements(String name, int size, boolean isCapped) {
    Collection collection = new Collection(name, isCapped, size);

    assertAll(
      () -> assertEquals(name, collection.getName()),
      () -> assertEquals(size, collection.getSize()),
      () -> assertEquals(isCapped, collection.isCapped())
    );
  }

  @DisplayName("Equality Tests")
  @ParameterizedTest
  @MethodSource({"test.TestParamProvider#collectionEquality"})
  public void testCollectionsEquality(String testName, String name1, int size1, boolean isCapped1,
    String name2, int size2, boolean isCapped2, boolean expectedResult) {

    Collection collection = new Collection(name1, isCapped1, size1);
    Collection collection2 = new Collection(name2, isCapped2, size2);

    assertEquals(expectedResult, collection.equals(collection2));
  }
}
