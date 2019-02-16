package dbdiffchecker;

import java.time.Duration;
import java.time.Instant;

/**
 * StopWatch models a stopwatch and can determine how much time has elapsed since
 * the last start and end method calls.
 * Program Name: Database Difference Checker
 * CSCI Course: 325
 * Grade Received: Pass
 * @author Jonas_Hess and Peter Kaufman
 * @version 2-16-19
 * @since 10-26-17
 * @see <a href="https://stackoverflow.com/questions/8255738/is-there-a-stopwatch-in-java">https://stackoverflow.com/questions/8255738/is-there-a-stopwatch-in-java</a>
 */
public class StopWatch {
  // Instance Variables
  private Instant startTime;
  private Instant endTime;
  private Duration duration;
  private boolean isRunning = false;

  /**
   * start checks to see if the program is running. If it is not running, then start time is set. 
   * Otherwise a RuntimeException error is thrown.
   * @author Jonas_Hess
   * @throws RuntimeException The stopwatch was running when this function was called.
   */
  public void start() throws RuntimeException {
    if (isRunning) {
      throw new RuntimeException("Stopwatch is already running.");
    }
    this.isRunning = true;
    startTime = Instant.now();
  }

  /**
   * Checks to see if the program is running. If it is running, then stop time is set, 
   * and the duration that the program has run is set and returned. Otherwise a RuntimeException
   * error is thrown.
   * @author Jonas_Hess
   * @return The amount of time elapsed since the last start method was called.
   * @throws RuntimeException The stopwatch has not been started.
   */
  public Duration stop() throws RuntimeException {
    this.endTime = Instant.now();
    if (!isRunning) {
      throw new RuntimeException("Stopwatch has not been started yet");
    }
    isRunning = false;
    Duration result = Duration.between(startTime, endTime);
    if (this.duration == null) {
      this.duration = result;
    } else {
      this.duration = duration.plus(result);
    }

    return this.getElapsedTime();
  }

  /**
   * Returns the duration between the last time the start and stop methods were run.
   * @author Jonas_Hess
   * @return The duration between the last time the start and stop methods were run.
   */
  public Duration getElapsedTime() {

    return this.duration;
  }

  /**
   * Stops the program if it is running and sets the duration to null.
   * @author Jonas_Hess
   */
  public void reset() {
    if (this.isRunning) {
      this.stop();
    }
    this.duration = null;
  }
}
