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
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.*;
import javax.swing.border.TitledBorder;

public class DBCompare1 extends JFrameV2 {
        // Instance variables
        private Db_conn db1, db2;
        private Database dab1, dab2;
        private ArrayList<String> sql = new ArrayList();
        private HashMap<String, String> update_tables = new HashMap();
        private JButton DB1btn = new JButton( "Compare" );
        private JTextField database3 = new JTextField( 10 ), host3 = new JTextField( 10 ),
                           port3 = new JTextField( 10 ), username3 = new JTextField( 10 );
        private JLabel jLabel18 = new JLabel( "Enter MySQL Username:" ),
                       jLabel19 = new JLabel( "Enter MySQL Password:" ),
                       jLabel20 = new JLabel( "Enter MySQL Host:" ),
                       jLabel21 = new JLabel(  "Enter MySQL Port:" ),
                       jLabel22 = new JLabel( "Enter MySQL Database:" ),
                       jLabel23 = new JLabel( "Enter Database Information Below", SwingConstants.CENTER );
        private JPasswordField password3 = new JPasswordField( 10 );
        private boolean done = false;

        /**
         * Creates new form DBCompare1
         * @author Peter Kaufman
         * @type constructor
         * @access public
         */
        public DBCompare1() {

                initComponents();
                clase = this.getClass().getName();
        }

        /**
         * InitComonents sets up the GUI Layout, sets up all action events,
         * and initializes instance variables
         * @author Peter Kaufman
         * @type function
         * @access private
         */
        private void initComponents() {
                // add components to the appropriate ArrayList
                cpnt.add( jLabel23 );
                cpnr.add( host3 );
                cpnr.add( port3 );
                cpnr.add( database3 );
                cpnr.add( password3 );
                cpnr.add( jLabel18 );
                cpnr.add( DB1btn );
                cpnr.add( jLabel19 );
                cpnr.add( jLabel20 );
                cpnr.add( jLabel21 );
                cpnr.add( jLabel22 );
                cpnr.add( username3 );
                // set up JFrame properties
                setMinimumSize( new Dimension( 100, 100 ));
                // set component properties
                pb.setVisible( false );
                jLabel23.setFont(new Font("Tahoma", 1, 14));
                DB1btn.setFont(new Font("Tahoma", 0, 18));
                // create JPanels
                JPanel header = new JPanel( new BorderLayout()), content = new JPanel( new GridLayout( 5, 2 )),
                       footer = new JPanel( new BorderLayout()), part1 = new JPanel( new FlowLayout()),
                       part2 = new JPanel( new FlowLayout()), part3 = new JPanel( new FlowLayout()),
                       part4 = new JPanel( new FlowLayout()), part5 = new JPanel( new FlowLayout()),
                       part6 = new JPanel( new FlowLayout()), part7 = new JPanel( new FlowLayout()),
                       part8 = new JPanel( new FlowLayout()), part9 = new JPanel( new FlowLayout()),
                       part10 = new JPanel( new FlowLayout()), footc = new JPanel( new FlowLayout());
                // add listeners
                DB1btn.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent evt) {
                                DB1btnActionPerformed(evt);
                        }
                });
                // add components
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
}
