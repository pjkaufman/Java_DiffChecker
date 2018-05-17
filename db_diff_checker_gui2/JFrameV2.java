/**
 * JFrameV2 is a JFrame that has all of the common methods that any JFrame
 * in this package needs
 * @author Peter Kaufman
 * @class JFrameV2
 * @access public
 * @version 5-15-18
 * @since 5-14-18
 */
package db_diff_checker_gui2;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.TitledBorder;

public class JFrameV2 extends JFrame {
        // Defualt instance variables
        protected StopWatch sw = new StopWatch();
        protected Font myFont;
        protected boolean error = true;
        protected ArrayList<String> sql = new ArrayList<>();
        protected ArrayList<Component> cpnr = new ArrayList<>(), cpnt = new ArrayList<>();
        protected JProgressBar pb = new JProgressBar();
        protected String clase = "None";
        protected TitledBorder nBorder = null;
        protected final String stdLog = "Log.txt", stdErr = "Error.txt", stdOut = "LastRun.txt",
                               stdSnap = "dbsnapshot.json";

        /**
         * Sets up several JFrame settings and defualt listeners
         * @author Peter Kaufman
         * @type constructor
         * @access public
         */
        public JFrameV2() {
                // set JFrame properties
                setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );
                setCursor( new Cursor( Cursor.DEFAULT_CURSOR ));
                setLocation( new Point( 200, 200 ));
                setIconImage( new ImageIcon( getClass().getResource( "/Images/DBCompare.png" )).getImage());
                // set component properties
                pb.setVisible( false );
                // add listeners
                addWindowListener( new WindowAdapter() {
                        public void windowClosing(WindowEvent evt) {
                                formWindowClosing(evt);
                        }
                });
                addComponentListener( new ComponentListener() {
                        public void componentResized( ComponentEvent e ) {

                                double width = e.getComponent().getWidth();
                                Font title = new Font( "Tahoma", Font.BOLD, 14 ), reg = new Font( "Tahoma", Font.PLAIN, 12 );
                                if ( width >= 419  && ( clase.contains( "DBCompare1" ) || clase.contains( "DB_Diff_Checker_GUI" ))) {

                                        title = new Font( "Tahoma", Font.BOLD, (int)( width / 25 ));
                                        reg = new Font( "Tahoma", Font.PLAIN, (int)( width / 25 ) - 2 );
                                } else if ( width >= 660 && clase.contains( "Result" )) {

                                        title = new Font( "Tahoma", Font.BOLD, (int)( width / 33 ));
                                        reg = new Font( "Tahoma", Font.PLAIN, (int)( width / 46 ));
                                } else if (  width >= 660 && clase.contains( "DBCompare2" )) {

                                        title = new Font( "Tahoma", Font.BOLD, (int)( width / 25 ));
                                        reg = new Font( "Tahoma", Font.PLAIN, (int)( width / 56 ));
                                }
                                for ( Component cpn : cpnr ) {

                                        cpn.setFont( reg );
                                }
                                for ( Component cpn : cpnt ) {

                                        cpn.setFont( title );
                                }
                                myFont = reg;
                        }
                        public void componentHidden( ComponentEvent e) {
                        }
                        public void componentShown( ComponentEvent e) {
                        }
                        public void componentMoved( ComponentEvent e) {
                        }
                });
        }

        /**
         * error opens a JFrame with an error message
         * @author Peter Kaufman
         * @type function
         * @access protected
         * @param error is a String which represents the error message to display
         * @param e is an Exception that caused the error
         */
        protected void error( String error, Exception e ) {

                // Error err = new Error( e.getMessage().substring( e.getMessage().indexOf( ":" ) + 1 ));
                Error err = new Error( error );
                err.setSize( 430, 100 );
                err.setVisible( true );
                System.out.println( error );
                System.out.println( "Message: " + e.getMessage());
                System.out.println( "Cause: " + e.getCause());
                this.error = true;
        }

        /**
         * displayResult opens a JFrame with the result of the comparison
         * @author Peter Kaufman
         * @type function
         * @access protected
         * @param db is a Db_conn object which is the connection for the live database
         */
        protected void displayResult( Db_conn db ) {

                Result rs = new Result( db );
                rs.results( this.sql, "Run the following SQL to make the two databases the same:" );
        }

        /**
         * formWindowClosing opens the start JFrame when the form is closing and the
         * compare button has not been clicked
         * @author Peter Kaufman
         * @type function
         * @access protected
         * @param evt is a WindowEvent which represents the JFrame closing
         */
        protected void formWindowClosing( WindowEvent evt ) {
                if ( error ) {

                        DB_Diff_Checker_GUI start = new DB_Diff_Checker_GUI();
                        start.setSize( 375, 225 );
                        start.setVisible( true );
                }
        }

        /**
         * newBorder takes a String and sets the new title for the progressbar's border
         * @author Peter Kaufman
         * @type function
         * @access protected
         * @param title is a String which represents the new name of the titled borders
         */
        protected void newBorder( String title ) {

                nBorder = BorderFactory.createTitledBorder( title );
                nBorder.setTitleFont( myFont );
                pb.setBorder( nBorder );
        }

        /**
         * log takes two Strings and writes the first to either log.txt or Error.txt
         * @author Peter Kaufman
         * @type function
         * @access protected
         * @param info is a String which represents the data to be logged
         * @param file is the name of the file where the data is to be stored
         */
        protected void log ( String info, String file ) {
                try {

                        FileConversion.writeTo( info, file );
                } catch( IOException e ) {

                        error( "There was an error writing to the " + file, e );
                }
        }

        /**
         * close closes the current JFrame
         * @author Peter Kaufman
         * @type function
         * @access protected
         */
        protected void close() {
                // closes the window activating the formWindowClosing method
                this.dispatchEvent( new WindowEvent( this, WindowEvent.WINDOW_CLOSING ));
        }
        /**
         * prepProgressBar gets the progressBar ready by reseting the StopWatch
         * and determines which settings to turn on
         * @author Peter Kaufman
         * @type function
         * @access protected
         * @param title is a String which represents the title for the border
         * of the progressBar
         * @param indeterminate is a boolean that tells whether or not the
         * progressBar is to be indeterminate or not
         */
        protected void prepProgressBar( String title, boolean indeterminate ) {

                newBorder( title );
                pb.setIndeterminate( indeterminate );
                if ( !indeterminate ) {

                        pb.setValue(0);
                        pb.setStringPainted(true);
                }
                pb.setVisible( true );
                sw.reset();
        }

        /**
         * endProgressBar stops the progressBar and sets the border to the given
         * String and hides the progressBar
         * @author Peter Kaufman
         * @type function
         * @access protected
         * @param title is a String which represents the title for the border
         * of the progressBarS
         */
        protected void endProgressBar( String title ) {

                newBorder( title );
                if ( pb.isIndeterminate()) {

                        pb.setIndeterminate( false );
                } else {

                        pb.setValue( 100 );
                }
                pb.setVisible( false );
        }
}
