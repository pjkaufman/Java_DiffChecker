/**
* Result is a JFrame that shows the SQL statements to be run to make the
* databases the same
* @author Peter Kaufman
* @class Result
* @access public
* @version 10-31-17
* @since 9-20-17
*/
package db_diff_checker_gui2;
import java.util.ArrayList;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
public class Result extends JFrame {
    // Variable declaration
    private Db_conn db;
    private ArrayList<String> sql;
    private JScrollPane SQL;
    private JTextArea SQLShow;
    private JButton btnRun;
    private JLabel jLabel17;
    private StopWatch sw = new StopWatch();
    
    /**
     * Creates new form Result
     * @author Peter Kaufman
     * @type constructor
     * @access
     * @param db is a Db_conn object which is the connection for the live database
     */
    public Result( Db_conn db ) {

            this.db = db;
            initComponents();
            if ( db == null ) {

                    hideRun();
            }

            this.setIconImage( new ImageIcon( getClass().getResource( "/Images/DBCompare.png" )).getImage());
    }

    /**
    * InitComonents sets up the GUI Layout, sets up all action events, 
    * and initializes instance variables
    * @author Peter Kaufman
    * @type function
    * @access private
    */
    private void initComponents() {

            jLabel17 = new JLabel();
            SQL = new JScrollPane();
            SQLShow = new JTextArea();
            btnRun = new JButton();

            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            setTitle("SQL To Run");
            addWindowListener(new WindowAdapter() {
                    public void windowClosing(WindowEvent evt) {
                            formWindowClosing(evt);
                    }
            });

            jLabel17.setFont(new Font("Tahoma", 1, 18));
            jLabel17.setText("Run the following SQL to make the two databases the same:");

            SQL.setAutoscrolls(true);

            SQLShow.setEditable(false);
            SQLShow.setColumns(20);
            SQLShow.setRows(5);
            SQL.setViewportView(SQLShow);

            btnRun.setText("Run");
            setMinimumSize( new Dimension( 600, 210 ));
            btnRun.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                            btnRunActionPerformed(evt);
                    }
            });
            addComponentListener(new ComponentListener() {
                public void componentResized(ComponentEvent e) {

                    double width = e.getComponent().getWidth();
                    Font title = new Font("Tahoma", Font.BOLD, 18), reg = new Font("Tahoma", Font.PLAIN, 14);
                    if ( width >= 660 ) {

                        title = new Font("Tahoma", Font.BOLD, (int)( width / 33 ));
                        reg = new Font("Tahoma", Font.PLAIN, (int)( width / 46 ));
                    }

                    jLabel17.setFont( title );
                    SQLShow.setFont( reg );
                    btnRun.setFont( reg );

                }
                public void componentHidden(ComponentEvent e) {}
                public void componentShown(ComponentEvent e) {}
                public void componentMoved(ComponentEvent e) {}
            }); 

            getContentPane().setLayout( new BorderLayout());
            add( jLabel17, BorderLayout.NORTH );
            add( SQL, BorderLayout.CENTER );
            add( btnRun, BorderLayout.EAST );
            pack();
    }

    /**
     * formWindowClosing opens the start JFrame
     * @author Peter Kaufman
     * @type function
     * @access private
     * @param evt is a WindowEvent which represents the window closing
     */
    private void formWindowClosing(WindowEvent evt) {

            DB_Diff_Checker_GUI start = new DB_Diff_Checker_GUI();
            start.setSize( 375, 225 );
            start.setVisible( true );
    }

    /**
     * btnRunActionPerformed runs the SQL to update the live database
     * @author Peter Kaufman
     * @type function
     * @access private
     * @param evt is a WindowEvent which represents the btnRun button being clicked
     */
    private void btnRunActionPerformed(ActionEvent evt) {

        ArrayList<String> log = new ArrayList();
        // code by Artur: https://stackoverflow.com/questions/833768/java-code-for-getting-current-time
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat date = new SimpleDateFormat( "yyyy-MM-dd" );
        SimpleDateFormat hour = new SimpleDateFormat( "HH:mm" );
        hideRun();
        sw.start();
        boolean cont = this.db.runSQL( this.sql );
        sw.stop();
        if ( cont ) {

            log.add( "Ran on " + date.format(cal.getTime()) + " at " + hour.format(cal.getTime()) + " in " + sw.getElapsedTime().toMillis() / 1000.0 + "s with no errors." );
            jLabel17.setText( "The database has been updated." );
        } else {

            log.add( "Ran on " + date.format(cal.getTime()) + " at " + hour.format(cal.getTime()) + " with an error updating the database." );
        }
        try {

            FileConversion.writeTo( log, "Log.txt" );
        } catch( IOException e ) {

            e.printStackTrace();
            error( "There was an error writing to the log file" );
        }
    }

    /**
     * results takes in an ArrayList of Strings and adds them to a JTextArea
     * @author Peter Kaufman
     * @type function
     * @access public
     * @param SQL is an ArrayList of Strings which represents the SQL statements
     * @param title determines what the title JLabel's text will be
     */
    public void results( ArrayList<String> SQL, String title ) {
        try{
            if ( SQL.isEmpty()) {

                this.setSize( 300, 75 );
                if ( this.db != null ) {

                    jLabel17.setText( "The databases are in sync." );
                }
            } else {

                this.sql = SQL;
                for ( String statement: SQL ) {

                    SQLShow.append( statement + "\n" );
                }

                if ( title.equals( "Run the following SQL to make the two databases the same:" )) {

                    FileConversion.writeTo( SQL, "LastRun.txt" );
                }
                jLabel17.setText( title );
                this.setSize( 600, 210 );
            }

            this.setVisible( true );
        } catch( IOException e ) {

            System.err.println( e );
            e.printStackTrace();
            error( "There was an error writing the SQL statement(s) to a file." );
        }
    }
   
    /**
     * hideRun hides the btnRun
     * @author Peter Kaufman
     * @type function
     * @access public
     */
    public void hideRun() {

            this.btnRun.setVisible( false );
    }

    /**
     * error opens a JFrame with an error message
     * @author Peter Kaufman
     * @type function
     * @access private
     * @param error is a String which represents the error message to display
     */
    private void error( String error ) {

            Error err = new Error( error );
            err.setSize( 430, 100 );
            err.setVisible( true );
    }
}
