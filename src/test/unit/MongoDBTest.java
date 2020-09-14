package test.unit;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import dbdiffchecker.nosql.Collection;
import dbdiffchecker.nosql.MongoDB;

@DisplayName("MongoDB Tests")
public class MongoDBTest {

  @DisplayName("Get Colletions Tests")
  @ParameterizedTest
  @MethodSource({"test.TestParamProvider#mongoGetStatements"})
  public void testGetCollections(String testName, List<String> collectionNames,
    List<Integer> collectionSizes, List<Boolean> collectionIsCapped) {

    MongoDB mongoDB = new MongoDB();
    Map<String, Collection> expectedCollections = new HashMap<>();
    for (int i = 0; i < collectionNames.size(); i++) {
      Collection collection = new Collection(collectionNames.get(i), collectionIsCapped.get(i), collectionSizes.get(i));
      expectedCollections.put(collectionNames.get(i), collection);
      mongoDB.getCollections().put(collectionNames.get(i), collection);
    }

    assertEquals(expectedCollections, mongoDB.getCollections());
  }

  @DisplayName("Compare Tests")
  @ParameterizedTest
  @MethodSource({"test.TestParamProvider#mongoCompare"})
  public void testCompare(String testName, List<String> collectionNames,
    List<Integer> collectionSizes, List<Boolean> collectionIsCapped, List<String> collectionNames2,
    List<Integer> collectionSizes2, List<Boolean> collectionIsCapped2, List<String> expectedStatements) {

    MongoDB mongoDB = new MongoDB();
    for (int i = 0; i < collectionNames.size(); i++) {
      Collection collection = new Collection(collectionNames.get(i), collectionIsCapped.get(i), collectionSizes.get(i));
      mongoDB.getCollections().put(collectionNames.get(i), collection);
    }

    MongoDB mongoDBLive = new MongoDB();
    for (int i = 0; i < collectionNames2.size(); i++) {
      Collection collection = new Collection(collectionNames2.get(i), collectionIsCapped2.get(i), collectionSizes2.get(i));
      mongoDBLive.getCollections().put(collectionNames2.get(i), collection);
    }

    assertIterableEquals(expectedStatements, mongoDB.compare(mongoDBLive));
  }

  // @Test
  // public void testCompareMixedCollections() {
    // String names1 = "Commoner";
    // String names2 = "Creeper";
    // String names3 = "Creep";
    // String names4 = "Pillager";
    // String names5 = "Villager";
  //   expectedStatements.add(createPre + names5 + ", capped=true, size=234560");
  //   expectedStatements.add(createPre + names4);
  //   expectedStatements.add(deletePre + names3);
  //   expectedStatements.add(deletePre + names2);
  //   expectedStatements.add(deletePre + COLL_NAME_1);
  //   expectedStatements.add(createPre + COLL_NAME_1);
  //   expectedStatements.add(deletePre + COLL_NAME_2);
  //   expectedStatements.add(createPre + COLL_NAME_2 + ", capped=true, size=50000");

  //   Collection coll12 = new Collection(COLL_NAME_1, true, 67890);
  //   Collection coll22 = new Collection(COLL_NAME_2, false, 0);
  //   Collection coll3 = new Collection(names1, false, 0);
  //   Collection coll4 = new Collection(names2, false, 0);
  //   Collection coll5 = new Collection(names1, true, 587390);
  //   Collection coll6 = new Collection(names4, false, 0);
  //   Collection coll7 = new Collection(names5, true, 234560);
  //   MongoDB test = new MongoDB();
  //   MongoDB test1 = new MongoDB();
  //   test.getCollections().put(COLL_NAME_1, coll1); // collection to modify
  //   test.getCollections().put(COLL_NAME_2, coll2); // collection to modify
  //   test.getCollections().put(names1, coll3); // common collection
  //   test.getCollections().put(names4, coll6); // collection to add
  //   test.getCollections().put(names5, coll7); // collection to add
  //   test1.getCollections().put(COLL_NAME_1, coll12); // collection to modify
  //   test1.getCollections().put(COLL_NAME_2, coll22); // collection to modify
  //   test1.getCollections().put(names1, coll3); // common collection
  //   test1.getCollections().put(names2, coll4); // collection to drop
  //   test1.getCollections().put(names3, coll5); // collection to drop

  //   statements = test.compare(test1);

  //   assertEquals(
  //       "The Mongo database should suggest 8 changes when 2 collections need to be updated (4),"
  //           + " 2 collections need to be created (2), and 2 collections need to be dropped (2).",
  //       expectedStatements, statements);
  // }
}
