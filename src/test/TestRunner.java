package test;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import java.io.File;

/**
 * Runs all of the unit tests.
 *
 * @author Peter Kaufman
 */
public class TestRunner {
  public static void main(String[] args) {
    String testName;
    String result;
    long testTime = 0;
    long tempTime;
    int numTests = 0;
    int numFails = 0;
    int tempTestCount;
    int tempFailCount;
    boolean success = true;
    File dir = new File("src" + File.separator + "test");
    // get class file names from current directory
    File[] tests = dir.listFiles((d, name) -> name.endsWith(".class"));
    for (File test : tests) {
      // skip current file's class file
      if (test.getName().equals("TestRunner.class")) {
        continue;
      }
      testName = "test." + test.getName().replace(".class", "");
      try {
        result = "OK";
        System.out.println("Running Test for " + testName);
        Result testResult = JUnitCore.runClasses(Class.forName(testName));
        tempTime = testResult.getRunTime();
        tempTestCount = testResult.getRunCount();
        tempFailCount = testResult.getFailureCount();
        for (Failure failure : testResult.getFailures()) {
          System.out.println(failure.toString());
        }
        if (!testResult.wasSuccessful()) {
          result = "FAIL";
        }
        System.out.printf("%s tests: %d fails: %d in %dms %n", result, tempTestCount, tempFailCount, tempTime);
        testTime += tempTime;
        numTests += tempTestCount;
        numFails += tempFailCount;
        success = success && testResult.wasSuccessful();
      } catch (Exception e) {
        System.out.println("Unable to run test for " + testName);
        System.err.println(e);
      }
    }
    if (success) {
      result = "OK";
    } else {
      result = "FAIL";
    }
    System.out.printf("Result: %s tests: %d fails: %d in %dms %n", result, numTests, numFails, testTime);
  }
}
