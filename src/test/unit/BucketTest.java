package test.unit;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import dbdiffchecker.nosql.Bucket;
import dbdiffchecker.sql.Index;

@DisplayName("Bucket Tests")
public class BucketTest {

  @DisplayName("Compare Tests")
  @ParameterizedTest
  @MethodSource({"test.TestParamProvider#bucketCompare"})
  public void testCompare(String testName, List<String> bucketDocumentNames,
    List<String> bucket2DocumentNames, List<String> bucketIndexNames, List<String> bucketIndexCreates,
    List<String> bucket2IndexNames, List<String> bucket2IndexCreates, List<String> expectedStatements) {

    Bucket bucket = new Bucket();
    Bucket bucket2 = new Bucket();
    for (String documentName: bucketDocumentNames) {
      bucket.getDocuments().put(documentName, documentName);
    }

    for (int i = 0; i < bucketIndexNames.size(); i++) {
      bucket.getIndices().put(bucketIndexNames.get(i), new Index(bucketIndexNames.get(i), bucketIndexCreates.get(i), "DROP INDEX `" + bucketIndexNames.get(i) + "`;"));
    }

    for (String documentName: bucket2DocumentNames) {
      bucket2.getDocuments().put(documentName, documentName);
    }

    for (int i = 0; i < bucket2IndexNames.size(); i++) {
      bucket2.getIndices().put(bucket2IndexNames.get(i), new Index(bucket2IndexNames.get(i), bucket2IndexCreates.get(i), "DROP INDEX `" + bucket2IndexNames.get(i) + "`;"));
    }

    List<String> actualStatements = bucket.compare(bucket2);

    assertIterableEquals(expectedStatements, actualStatements);
  }
}