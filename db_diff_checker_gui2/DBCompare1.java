/**
 * DBCompare1 is a JFrame that takes user input to make a comparison between 1
 * database and a database snapshot or to take a database snapshot
 * @author Peter Kaufman
 * @class DBCompare1
 * @access public
 * @version 5-13-18
 * @since 9-20-17
 */
package db_diff_checker_gui2;
import java.sql.SQLException;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import javax.swing.*;
import java.awt.*;
import javax.swing.border.TitledBorder;
public class DBCompare1 extends JFrame {
        // Variable declaration
        private boolean error = true;
        private Db_conn db1, db2;
        private Database dab1, dab2;
        private ArrayList<String> sql = new ArrayList();
        private HashMap<String, String> update_tables = new HashMap();
        private JButton DB1btn;
        private JTextField database3;
        private JTextField host3;
        private JLabel jLabel18;
        private JLabel jLabel19;
        private JLabel jLabel20;
        private JLabel jLabel21;
        private JLabel jLabel22;
        private JLabel jLabel23;
        private JPasswordField password3;
        private JTextField port3;
        private JTextField username3;
        private JProgressBar pb = new JProgressBar();
        private StopWatch sw = new StopWatch();
        private boolean done = false;
        private Font myFont;

        /**
         * Creates new form DBCompare1
         * @author Peter Kaufman
         * @type constructor
         * @access public
         */
        public DBCompare1() {

                initComponents();
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

                host3 = new JTextField();
                port3 = new JTextField();
                database3 = new JTextField();
                password3 = new JPasswordField();
                jLabel23 = new JLabel( "", SwingConstants.CENTER );
                jLabel18 = new JLabel();
                DB1btn = new JButton();
                jLabel19 = new JLabel();
                jLabel20 = new JLabel();
                jLabel21 = new JLabel();
                jLabel22 = new JLabel();
                username3 = new JTextField();
                pb.setVisible( false );
                JPanel header = new JPanel( new BorderLayout()), content = new JPanel( new GridLayout( 5, 2 )),
                       footer = new JPanel( new BorderLayout()), part1 = new JPanel( new FlowLayout()),
                       part2 = new JPanel( new FlowLayout()), part3 = new JPanel( new FlowLayout()),
                       part4 = new JPanel( new FlowLayout()), part5 = new JPanel( new FlowLayout()),
                       part6 = new JPanel( new FlowLayout()), part7 = new JPanel( new FlowLayout()),
                       part8 = new JPanel( new FlowLayout()), part9 = new JPanel( new FlowLayout()),
                       part10 = new JPanel( new FlowLayout()), footc = new JPanel( new FlowLayout());

                jLabel19.setText("Enter MySQL Password:");
                jLabel20.setText("Enter MySQL Host:");
                jLabel21.setText("Enter MySQL Port:");
                jLabel22.setText("Enter MySQL Database:");
                jLabel23.setFont(new Font("Tahoma", 1, 14));
                jLabel23.setText("Enter Database Information Below");
                jLabel18.setText("Enter MySQL Username:");
                DB1btn.setFont(new Font("Tahoma", 0, 18));
                DB1btn.setText("Compare");
                database3.setColumns(10);
                host3.setColumns(10);
                port3.setColumns(10);
                password3.setColumns(10);
                username3.setColumns(10);
                setMinimumSize( new Dimension( 100, 100 ));

                setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                addWindowListener(new WindowAdapter() {
                        public void windowClosed(WindowEvent evt) {
                                formWindowClosed(evt);
                        }
                });

                addComponentListener(new ComponentListener() {
                        public void componentResized(ComponentEvent e) {

                                double width = e.getComponent().getWidth();
                                Font title = new Font("Tahoma", Font.BOLD, 14), reg = new Font("Tahoma", Font.PLAIN, 12);
                                if ( width >= 419 ) {

                                        title = new Font("Tahoma", Font.BOLD, (int)( width / 25 ));
                                        reg = new Font("Tahoma", Font.PLAIN, (int)( width / 25 ) - 2);
                                }

                                host3.setFont( reg );
                                port3.setFont( reg );
                                database3.setFont( reg );
                                password3.setFont( reg );
                                jLabel23.setFont( title );
                                jLabel18.setFont( reg );
                                DB1btn.setFont( reg );
                                jLabel19.setFont( reg );
                                jLabel20.setFont( reg );
                                jLabel21.setFont( reg );
                                jLabel22.setFont( reg );
                                username3.setFont( reg );
                                myFont = reg;
                        }
                        public void componentHidden(ComponentEvent e) {
                        }
                        public void componentShown(ComponentEvent e) {
                        }
                        public void componentMoved(ComponentEvent e) {
                        }
                });

                DB1btn.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent evt) {
                                DB1btnActionPerformed(evt);
                        }
                });

                getContentPane().setLayout( new BorderLayout());
                header.add( jLabel23, BorderLayout.CENTER );
                part1.add( jLabel18 );
                part2.add( username3 );
                part3.add( jLabel19 );
                part4.add( password3 );
                part5.add( jLabel20 );
                part6.add( host3 );
                part7.add( jLabel21 );
                part8.add( port3 );
                part9.add( jLabel22 );
                part10.add( database3 );
                content.add( part1 );
                content.add( part2 );
                content.add( part3 );
                content.add( part4 );
                content.add( part5 );
                content.add( part6 );
                content.add( part7 );
                content.add( part8 );
                content.add( part9 );
                content.add( part10 );
                footc.add( DB1btn );
                footer.add(  footc, BorderLayout.CENTER );
                footer.add( pb, BorderLayout.SOUTH );
                add( header, BorderLayout.NORTH);
                add( content, BorderLayout.CENTER);
                add( footer, BorderLayout.SOUTH);
        }

        /**
         * DB1btnActionPerformed determines if the user has put in the appropriate
         * Information and either takes a db snapshot or compares a database to a database
         * snapshot
         * @author Peter Kaufman
         * @type function
         * @access private
         * @param evt is an ActionEvent which represents clicking DB1btn
         */
        private void DB1btnActionPerformed(ActionEvent evt) {
                try {
                        if ( !( port3.getText().equals( "" ) |username3.getText().equals( "" ) |
                                new String(password3.getPassword()).equals( "" ) | host3.getText().equals( "" ) |
                                database3.getText().equals( "" ))) {
                                if ( this.getTitle().equals( "Compare Database to Snapshot" )) {

                                        this.error = false;
                                        compare2();
                                } else {

                                        this.error = false;
                                        takeSnapshot();
                                }

                        } else {

                                jLabel23.setText( "Please do not leave any fields blank." );
                        }
                } catch( IOException e ) {

                        error( "There was an error with the database snapshot file." );
                }
        }

        /**
         * formWindowClosed opens the start JFrame if the compare button has not been
         * clicked
         * @author Peter Kaufman
         * @type function
         * @access private
         * @param evt is a WindowEvent which represents this window closing
         */
        private void formWindowClosed(java.awt.event.WindowEvent evt) {
                if ( error ) {

                        DB_Diff_Checker_GUI start = new DB_Diff_Checker_GUI();
                        start.setSize( 375, 225 );
                        start.setVisible( true );
                }
        }

        /**
         * takeSnapshot takes a database snapshot based on user input
         * @author Peter Kaufman
         * @type function
         * @access private
         */
        public void takeSnapshot() {

                ArrayList<String> log = new ArrayList();
                pb.setIndeterminate( true );
                TitledBorder nBorder = BorderFactory.createTitledBorder( "Establishing Database Connection" );
                nBorder.setTitleFont( myFont );
                pb.setBorder( nBorder );
                pb.setVisible( true );

                SwingWorker<Boolean, Integer> swingW = new SwingWorker<Boolean, Integer>() {

                        @Override
                        protected Boolean doInBackground() throws Exception {

                                try {
                                        publish( 1 );
                                        sw.start();
                                        db1 = new Db_conn( username3.getText(), new String(password3.getPassword()),
                                                           host3.getText(), port3.getText(), database3.getText(), "dev" );

                                        publish( 2 );
                                        dab1 = new Database( db1 );
                                        publish( 3 );
                                        FileConversion.writeTo( dab1 );
                                        sw.stop();
                                        done = true;
                                } catch( IOException e ) {

                                        sw.stop();
                                        done = false;
                                        error( "There was an error when trying to take a database snapshot." );
                                } catch ( SQLException e ) {

                                        sw.stop();
                                        done = false;
                                        error( "There was an error with the database connection. Please try again." );
                                }

                                return true;

                        }

                        @Override
                        protected void done() {

                                try {

                                        get();
                                        TitledBorder nBorder2 = BorderFactory.createTitledBorder( "Database Snapshot Complete" );
                                        nBorder2.setTitleFont( myFont );
                                        pb.setBorder( nBorder2 );
                                        pb.setIndeterminate( false );
                                        if ( done ) {

                                                log.add( "Took a DB Snapshot on " + sw.getDate() + " at " + sw.getHour() + " in " + sw.getElapsedTime().toMillis() / 1000.0 + "s with no errors." );

                                        } else {

                                                log.add( "Took a DB Snapshot on " + sw.getDate() + " at " + sw.getHour() + " in " + sw.getElapsedTime().toMillis() / 1000.0 + "s with an error." );
                                        }
                                        try {

                                                FileConversion.writeTo( log, "Log.txt" );
                                        } catch( IOException e ) {

                                                e.printStackTrace();
                                                error( "There was an error writing to the log file" );
                                        }
                                        error = true;
                                        dispose();
                                } catch ( Exception e ) {

                                }
                        }

                        @Override
                        protected void process( List<Integer> chunks ) {

                                TitledBorder nBorder2 = BorderFactory.createTitledBorder( "Establishing Database Connection" );
                                if ( chunks.get( chunks.size() - 1) == 2 ) {

                                        nBorder2 = BorderFactory.createTitledBorder( "Gathering Database Information" );
                                } else if ( chunks.get( chunks.size() - 1) == 3 ) {

                                        nBorder2 = BorderFactory.createTitledBorder( "Writing to JSON File" );
                                }
                                nBorder2.setTitleFont( myFont );
                                pb.setBorder(nBorder2);
                        }
                };

                swingW.execute();
        }

        /**
         * setButtonTxt sets the text of DB1btn based on text input
         * @author Peter Kaufman
         * @type function
         * @access public
         * @param text is a String which will be the DB1btn's text
         */
        public void setButtonTxt( String text ) {

                DB1btn.setText( text );
        }

        /**
         * compare2 compares the database specified by the user to a database snapshot
         * @author Peter Kaufman
         * @type function
         * @access private
         * @throws IOException which occurs when there is an error with the database
         * snapshot file
         */
        private void compare2() throws IOException {

                pb.setIndeterminate( true );
                ArrayList<String> log = new ArrayList();
                TitledBorder b = BorderFactory.createTitledBorder( "Reading in The DB Snapshot" );
                b.setTitleFont( myFont );
                pb.setBorder( b );
                pb.setVisible( true );
                sw.reset();
                SwingWorker<Boolean, Integer> swingW = new SwingWorker<Boolean, Integer>() {

                        @Override
                        protected Boolean doInBackground() throws Exception {

                                try {

                                        publish( 1 );
                                        sw.start();
                                        dab1 = FileConversion.readFrom();
                                        publish( 2 );
                                        db2 = new Db_conn( username3.getText(), new String(password3.getPassword()),
                                                           host3.getText(), port3.getText(), database3.getText(), "live" );
                                        publish( 3 );
                                        dab2 = new Database( db2 );
                                        publish( 4 );
                                        sql.addAll( dab2.getFirstSteps());
                                        publish( 5 );
                                        sql.addAll( dab1.compareTables( dab2.getTables()));
                                        publish( 6 );
                                        update_tables.putAll( dab1.tablesDiffs( dab2.getTables()));
                                        publish( 7 );
                                        sql.addAll( dab1.updateTables( dab2.getTables(), update_tables ));
                                        publish( 8 );
                                        sql.addAll( dab1.getFirstSteps());
                                        publish( 9 );
                                        sql.addAll( dab1.updateViews( dab2.getViews()));
                                        sw.stop();
                                        log.add( "DB Snapshot Comparison Complete on " + sw.getDate() + " at " + sw.getHour() + " in " + sw.getElapsedTime().toMillis() / 1000.0 + "s with no errors." );
                                } catch ( SQLException e ) {

                                        sw.stop();
                                        log.add( "DB Snapshot Comparison Complete on " + sw.getDate() + " at " + sw.getHour() + " in " + sw.getElapsedTime().toMillis() / 1000.0 + "s with an error." );
                                        error( "There was an error with the database connection. Please try again." );
                                }

                                return true;

                        }

                        @Override
                        protected void done() {

                                try {

                                        get();
                                        TitledBorder b2 = BorderFactory.createTitledBorder( "Database Snapshot Comparison Complete" );
                                        b2.setTitleFont( myFont );
                                        pb.setBorder( b2 );
                                        pb.setIndeterminate( false );
                                        try {

                                                FileConversion.writeTo( log, "Log.txt" );
                                        } catch( IOException e ) {

                                                e.printStackTrace();
                                                error( "There was an error writing to the log file" );
                                        }
                                        displayResult( db2 );
                                        dispose();
                                } catch ( Exception e ) {

                                }
                        }

                        @Override
                        protected void process( List<Integer> chunks ) {

                                TitledBorder nBorder = BorderFactory.createTitledBorder( "Reading in The DB Snapshot" );
                                if ( chunks.get( chunks.size() - 1) == 2 ) {

                                        nBorder = BorderFactory.createTitledBorder( "Establishing Live Database Connection" );
                                } else if ( chunks.get( chunks.size() - 1) == 3 ) {

                                        nBorder = BorderFactory.createTitledBorder( "Gathering Live Database Info" );
                                } else if ( chunks.get( chunks.size() - 1) == 4 ) {

                                        nBorder = BorderFactory.createTitledBorder( "Checking Live First Steps" );
                                } else if ( chunks.get( chunks.size() - 1) == 5 ) {

                                        nBorder = BorderFactory.createTitledBorder( "Finding Missing Or Unneccessary Tables" );
                                } else if ( chunks.get( chunks.size() - 1) == 6 ) {

                                        nBorder = BorderFactory.createTitledBorder( "Comparing Tables" );
                                } else if ( chunks.get( chunks.size() - 1) == 7 ) {

                                        nBorder = BorderFactory.createTitledBorder( "Comparing Tables" );
                                } else if ( chunks.get( chunks.size() - 1) == 8 ) {

                                        nBorder = BorderFactory.createTitledBorder( "Checking Dev First Steps" );
                                } else if ( chunks.get( chunks.size() - 1) == 9 ) {

                                        nBorder = BorderFactory.createTitledBorder( "Adding Dev's Views" );
                                }
                                nBorder.setTitleFont( myFont );
                                pb.setBorder(nBorder);
                        }
                };

                swingW.execute();
        }

        /**
         * displayResult opens a JFrame with the result of the comparison
         * @author Peter Kaufman
         * @type function
         * @access private
         * @param db is a Db_conn object which is the connection for the live database
         */
        private void displayResult( Db_conn db ) {

                Result rs = new Result( db );
                rs.results( sql, "Run the following SQL to make the two databases the same:" );
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
                this.error = true;
        }
}
