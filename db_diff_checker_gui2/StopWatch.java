/**
 * StopWatch models a stopwatch and can determine how much time has elapsed since
 * the last start and end method calls
 * @author Jonas_Hess and Peter Kaufman
 * @class StopWatch
 * @access public
 * @version 5-13-18
 * @since 10-26-17
 * @see https://stackoverflow.com/questions/8255738/is-there-a-stopwatch-in-java
 */
package db_diff_checker_gui2;
import java.time.Duration;
import java.time.Instant;
import java.text.SimpleDateFormat;
import java.util.Calendar;
public class StopWatch {

        private Instant startTime, endTime;
        private Duration duration;
        private boolean isRunning = false;
        // code by Artur: https://stackoverflow.com/questions/833768/java-code-for-getting-current-time
        private Calendar cal;
        private SimpleDateFormat date = new SimpleDateFormat( "yyyy-MM-dd" );
        private SimpleDateFormat hour = new SimpleDateFormat( "HH:mm" );

        /**
         * start checks to see if the program is running. If it is not running, then start time
         * is set. Otherwise a RuntimeException error is thrown.
         * @author Jonas_Hess
         * @type function
         * @access public
         */
        public void start() {
                if (isRunning) {
                        throw new RuntimeException("Stopwatch is already running.");
                }
                this.isRunning = true;
                startTime = Instant.now();
        }

        /**
         * stop checks to see if the program is running. If it is running, then stop time
         * is set, and the duration that the program has run is set and returned.
         * Otherwise a RuntimeException error is thrown.
         * @author Jonas_Hess
         * @type function
         * @access public
         * @return the amount of time elapsed since the last start method was called
         */
        public Duration stop() {
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
         * getElapsedTime returns the duration between the last time the start and
         * stop methods were run
         * @author Jonas_Hess
         * @type getter
         * @access public
         * @return duration the duration between the last time the start and stop
         * methods were run
         */
        public Duration getElapsedTime() {
                return this.duration;
        }

        /**
         * reset stops the program if it is running and sets the duration to null
         * @author Jonas_Hess
         * @type function
         * @access public
         */
        public void reset() {
                if (this.isRunning) {
                        this.stop();
                }
                this.duration = null;
        }

        /**
         * getDate gets the date when the method is called
         * @author Peter Kaufman
         * @type function
         * @access public
         */
        public String getDate() {

                cal = Calendar.getInstance();

                return date.format(cal.getTime()) + "";
        }

        /**
         * getHour gets the hour when the method is called
         * @author Peter Kaufman
         * @type function
         * @access public
         */
        public String getHour() {

                cal = Calendar.getInstance();

                return hour.format(cal.getTime()) + "";
        }
}
