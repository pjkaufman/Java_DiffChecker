package dbdiffchecker;

import java.time.Duration;
import java.time.Instant;

/**
 * Models a stopwatch and can determine how much time has elapsed since the last
 * start and end method calls.
 *
 * @author Jonas_Hess and Peter Kaufman
 * @version 6-20-20
 * @since 10-26-17
 * @see <a href=
 *      "https://stackoverflow.com/questions/8255738/is-there-a-stopwatch-in-java">https://stackoverflow.com/questions/8255738/is-there-a-stopwatch-in-java</a>
 */
public class StopWatch {
  private Instant startTime;
  private Duration duration;
  private boolean isRunning = false;

  /**
   * Checks to see if the program is running. If it is not running, then start
   * time is set. Otherwise a RuntimeException error is thrown.
   */
  public void start() {
    if (isRunning) {
      throw new RuntimeException("Stopwatch is already running.");
    }
    isRunning = true;
    startTime = Instant.now();
  }

  /**
   * Checks to see if the program is running. If it is running, then stop time is
   * set, and the duration that the program has run is set and returned. Otherwise
   * a RuntimeException error is thrown.
   *
   * @return The amount of time elapsed since the last start method was called.
   */
  public Duration stop() {
    Instant endTime = Instant.now();
    if (!isRunning) {
      throw new RuntimeException("Stopwatch has not been started yet");
    }
    isRunning = false;
    Duration result = Duration.between(startTime, endTime);
    if (duration == null) {
      duration = result;
    } else {
      duration = duration.plus(result);
    }
    return getElapsedTime();
  }

  /**
   * Returns the duration between the last time the start and stop methods were
   * run.
   *
   * @return The duration between the last time the start and stop methods were
   *         run.
   */
  public Duration getElapsedTime() {
    return duration;
  }

  /**
   * Stops the program if it is running and sets the duration to null.
   */
  public void reset() {
    if (isRunning) {
      stop();
    }
    duration = null;
  }
}
