/**
 * Result is a JFrame that shows the SQL statements to be run to make the
 * databases the same
 * @author Peter Kaufman
 * @class Result
 * @access public
 * @version 5-13-18
 * @since 9-20-17
 */
package db_diff_checker_gui2;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;;
import javax.swing.border.Border;

public class Result extends JFrame {
        // Variable declaration
        private Db_conn db;
        private ArrayList<String> sql;
        private JScrollPane SQL;
        private JTextArea SQLShow;
        private JButton btnRun;
        private JLabel jLabel17;
        private StopWatch sw = new StopWatch();
        private JProgressBar progressBar = new JProgressBar();
        private boolean done = false;

        /**
         * Creates new form Result
         * @author Peter Kaufman
         * @type constructor
         * @access public
         * @param db1 is a Db_conn object which is the connection for the live database
         */
        public Result( Db_conn db ) {

                this.db = db;
                initComponents();
                if ( db == null ) {

                        hideRun();
                        progressBar.setVisible( false );
                }
                setIconImage( new ImageIcon( getClass().getResource( "/Images/DBCompare.png" )).getImage());
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
                progressBar.setValue(0);
                progressBar.setStringPainted(true);
                Border border = BorderFactory.createTitledBorder( "Waiting On Action" );
                progressBar.setBorder(border);
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
                        public void componentHidden(ComponentEvent e) {
                        }
                        public void componentShown(ComponentEvent e) {
                        }
                        public void componentMoved(ComponentEvent e) {
                        }
                });

                getContentPane().setLayout( new BorderLayout());
                add( jLabel17, BorderLayout.NORTH );
                add( SQL, BorderLayout.CENTER );
                add( btnRun, BorderLayout.EAST );
                add( progressBar, BorderLayout.SOUTH );
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

                hideRun();
                ArrayList<String> log = new ArrayList();
                SwingWorker<Boolean, Integer> swingW = new SwingWorker<Boolean, Integer>() {

                        @Override
                        protected Boolean doInBackground() throws Exception {

                                ArrayList<String> temp = new ArrayList();
                                temp.add( "" );
                                boolean cont = true;
                                sw.start();
                                for ( int i = 0; i < sql.size(); i++ ) {

                                        temp.set(0, sql.get(i));
                                        cont = db.runSQL( temp );
                                        if ( !cont ) {

                                                throw new Exception( "There was an error running: " + temp.get(0));
                                        }
                                        publish(i);
                                }
                                sw.stop();

                                return true;
                        }

                        @Override
                        protected void done() {

                                try {

                                        get();
                                        done = true;
                                        jLabel17.setText( "The database has been updated." );
                                        Border nBorder = BorderFactory.createTitledBorder("Done");
                                        progressBar.setBorder(nBorder);
                                        progressBar.setValue(100);
                                        log.add( "Ran SQL on " + sw.getDate() + " at " + sw.getHour() + " in " + sw.getElapsedTime().toMillis() / 1000.0 + "s with no errors." );
                                        try {

                                                FileConversion.writeTo( log, "Log.txt" );
                                        } catch( IOException e ) {

                                                e.printStackTrace();
                                                error( "There was an error writing to the log file" );
                                        }
                                } catch ( Exception e ) {

                                }
                        }

                        @Override
                        protected void process( List<Integer> chunks ) {

                                hideRun();
                                Border nBorder = BorderFactory.createTitledBorder( "Running SQL.. " );
                                progressBar.setBorder(nBorder);
                                progressBar.setValue((int)(( chunks.get( chunks.size() - 1) + 1.0 ) * 100 / sql.size()));
                                progressBar.setString( progressBar.getPercentComplete() * 100 + "%" );
                        }

                };
                try {

                        swingW.execute();
                } catch ( Exception e ) {

                        sw.stop();
                        log.add( "Ran SQL on " + sw.getHour() + " at " + sw.getDate() + " with an error updating the database." );
                        try {

                                FileConversion.writeTo( log, "Log.txt" );
                        } catch( IOException e2 ) {

                                e2.printStackTrace();
                                error( "There was an error writing to the log file" );
                        }
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
