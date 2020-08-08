package test;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

/**
 * Runs all of the tests.
 *
 * @author Peter Kaufman
 */
public class TestRunner {
  private static long testTime = 0;
  private static int numTests = 0;
  private static int numFails = 0;
  private static String result = "OK";

  public static void main(String[] args) {

    System.out.println("Unit Tests:");
    runTestSuite(UnitTestSuite.class);

    System.out.println("Integration Tests:");
    runTestSuite(IntegrationTestSuite.class);

    System.out.printf("Result: %s tests: %d fails: %d in %dms %n", result, numTests, numFails, testTime);
  }

  public static void runTestSuite(Class c) {
    Result testSuiteResults = JUnitCore.runClasses(c);
    long tempTime = testSuiteResults.getRunTime();
    int tempTestCount = testSuiteResults.getRunCount();
    int tempFailCount = testSuiteResults.getFailureCount();

    for (Failure failure : testSuiteResults.getFailures()) {
      System.out.println(failure.toString());
    }

    if (!testSuiteResults.wasSuccessful()) {
      result = "FAIL";
    }

    System.out.printf("%s tests: %d fails: %d in %dms %n", result, tempTestCount, tempFailCount, tempTime);
    testTime += tempTime;
    numTests += tempTestCount;
    numFails += tempFailCount;
  }
}
