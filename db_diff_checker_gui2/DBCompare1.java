/**
 * DBCompare1 is a JFrame that takes user input to make a comparison between 1
 * database and a database snapshot or to take a database snapshot
 * @author Peter Kaufman
 * @version 5-24-18
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

public class DBCompare1 extends JFrameV2 {
        // Instance variables
        private Db_conn db1, db2;
        private Database dab1, dab2;
        private HashMap<String, String> update_tables = new HashMap<>();
        private JButton DB1btn = new JButton( "Compare" );
        private JTextField database = new JTextField( 10 ), host = new JTextField( 10 ),
                           port = new JTextField( 10 ), username = new JTextField( 10 );
        private JLabel usernameLabel = new JLabel( "Enter MySQL username:" ),
                       passLabel = new JLabel( "Enter MySQL Password:" ),
                       hostLabel = new JLabel( "Enter MySQL Host:" ),
                       portLabel = new JLabel(  "Enter MySQL Port:" ),
                       DBLabel = new JLabel( "Enter MySQL Database:" ),
                       headT = new JLabel( "Enter Database Information Below", SwingConstants.CENTER );
        private JPasswordField password = new JPasswordField( 10 );

        /**
         * DBCompare1 initializes a DBCompare1 object with a title and text for the its button.
         * @author Peter Kaufman
         * @param title is a String which is the title of this JFrame.
         * @param buttonTxt is a String which is the text to be displayed on the button DB1btn.
         */
        public DBCompare1( String title, String buttonTxt ) {
                // use parameters to set JFrame properties
                setTitle( "Take Database Snapshot" );
                DB1btn.setText( "Snapshot" );
                initComponents();
                clase = this.getClass().getName();
        }

        /**
         * InitComonents sets up the GUI Layout, sets up all action events, and initializes instance variables.
         * @author Peter Kaufmante
         */
        private void initComponents() {
                // add components to the appropriate ArrayList
                cpnt.add( headT );
                cpnr.add( host );
                cpnr.add( port );
                cpnr.add( database );
                cpnr.add( password );
                cpnr.add( usernameLabel );
                cpnr.add( DB1btn );
                cpnr.add( passLabel );
                cpnr.add( hostLabel );
                cpnr.add( portLabel );
                cpnr.add( DBLabel );
                cpnr.add( username );
                // set up JFrame properties
                setMinimumSize( new Dimension( 100, 100 ));
                // set component properties
                headT.setFont(new Font("Tahoma", 1, 14));
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
                header.add( headT, BorderLayout.CENTER );
                part1.add( usernameLabel );
                part2.add( username );
                part3.add( passLabel );
                part4.add( password );
                part5.add( hostLabel );
                part6.add( host );
                part7.add( portLabel );
                part8.add( port );
                part9.add( DBLabel );
                part10.add( database );
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
         * DB1btnActionPerformed determines if the user has put in the appropriate information and either 
         * takes a database snapshot or compares a database to a database snapshot.
         * @author Peter Kaufman
         * @param evt is an ActionEvent which is clicking the button DB1btn.
         */
        private void DB1btnActionPerformed(ActionEvent evt) {
                try {
                        if ( !( port.getText().equals( "" ) |username.getText().equals( "" ) |
                                new String(password.getPassword()).equals( "" ) | host.getText().equals( "" ) |
                                database.getText().equals( "" ))) {

                                this.error = false;
                                if ( this.getTitle().equals( "Compare Database to Snapshot" )) {

                                        compare();
                                } else {

                                        takeSnapshot();
                                }
                        } else {

                                headT.setText( "Please do not leave any fields blank." );
                        }
                } catch( IOException e ) {

                        error( "There was an error with the database snapshot file.", e );
                }
        }

        /**
         * takeSnapshot takes a database snapshot based on user input.
         * @author Peter Kaufman
         */
        private void takeSnapshot() {

                prepProgressBar( "Establishing Database Connection", true );
                SwingWorker<Boolean, String> swingW = new SwingWorker<Boolean, String>() {

                        @Override
                        protected Boolean doInBackground() throws Exception {
                                try {
                                        publish( "Establishing Database Connection" );
                                        sw.start();
                                        db1 = new Db_conn( username.getText(), new String(password.getPassword()),
                                                           host.getText(), port.getText(), database.getText(), "dev" );

                                        publish( "Gathering Database Information" );
                                        dab1 = new Database( db1 );
                                        publish( "Writing to JSON File" );
                                        FileConversion.writeTo( dab1 );
                                        sw.stop();
                                        log( "Took a DB Snapshot on " + sw.getDate() + " at " + sw.getHour() + " in " + sw.getElapsedTime().toMillis() / 1000.0 + "s with no errors.", stdLog );
                                } catch( IOException e ) {

                                        sw.stop();
                                        error( "There was an error when trying to take a database snapshot.", e );
                                        log( "Took a DB Snapshot on " + sw.getDate() + " at " + sw.getHour() + " in " + sw.getElapsedTime().toMillis() / 1000.0 + "s with an File error.", stdErr );
                                        throw new Exception( "There was an error when trying to take a database snapshot." );
                                } catch ( SQLException e ) {

                                        sw.stop();
                                        log( "Took a DB Snapshot on " + sw.getDate() + " at " + sw.getHour() + " in " + sw.getElapsedTime().toMillis() / 1000.0 + "s with an SQL error.", stdErr );
                                        error( "There was an error with the database connection. Please try again.", e );
                                        throw new Exception( "There was an error when trying to take a database snapshot." );
                                }

                                return true;
                        }

                        @Override
                        protected void done() {
                                try {

                                        get();
                                        endProgressBar( "Database Snapshot Complete" );
                                        error = true;
                                        close();
                                } catch ( Exception e ) {

                                        endProgressBar( "An Error Occurred" );
                                        error( e.getMessage().substring( e.getMessage().indexOf( ":" ) + 1 ), e );
                                }
                        }

                        @Override
                        protected void process( List<String> chunks ) {

                                newBorder( chunks.get( chunks.size() - 1 ));
                        }
                };

                swingW.execute();
        }

        /**
         * compare compares the database specified by the user to a database snapshot.
         * @author Peter Kaufman
         * @throws IOException an error occurred while accessing the database snapshot file
         */
        private void compare() throws IOException {

                prepProgressBar( "Reading in The DB Snapshot", true );
                SwingWorker<Boolean, String> swingW = new SwingWorker<Boolean, String>() {

                        @Override
                        protected Boolean doInBackground() throws Exception {
                                try {

                                        publish( "Reading in The DB Snapshot" );
                                        sw.start();
                                        dab1 = FileConversion.readFrom();
                                        publish( "Establishing Live Database Connection" );
                                        db2 = new Db_conn( username.getText(), new String(password.getPassword()),
                                                           host.getText(), port.getText(), database.getText(), "live" );
                                        publish( "Gathering Live Database Info" );
                                        dab2 = new Database( db2 );
                                        publish( "Checking Live First Steps" );
                                        sql.addAll( dab2.getFirstSteps());
                                        publish( "Finding Missing Or Unneccessary Tables" );
                                        sql.addAll( dab1.compareTables( dab2.getTables()));
                                        publish( "Comparing Tables" );
                                        update_tables.putAll( dab1.tablesDiffs( dab2.getTables()));
                                        sql.addAll( dab1.updateTables( dab2.getTables(), update_tables ));
                                        publish( "Checking Dev First Steps" );
                                        sql.addAll( dab1.getFirstSteps());
                                        publish( "Adding Dev's Views" );
                                        sql.addAll( dab1.updateViews( dab2.getViews()));
                                        sw.stop();
                                        log( "DB Snapshot Comparison Complete on " + sw.getDate() + " at " + sw.getHour() + " in " + sw.getElapsedTime().toMillis() / 1000.0 + "s with no errors.", stdLog );
                                } catch ( SQLException e ) {

                                        sw.stop();
                                        log( "DB Snapshot Comparison Complete on " + sw.getDate() + " at " + sw.getHour() + " in " + sw.getElapsedTime().toMillis() / 1000.0 + "s with an SQL error.", stdErr );
                                        throw new Exception( "There was an error with the database connection. Please try again." );
                                }

                                return true;
                        }

                        @Override
                        protected void done() {
                                try {

                                        get();
                                        endProgressBar( "Database Snapshot Comparison Complete" );
                                        displayResult( db2 );
                                        close();
                                } catch ( Exception e ) {

                                        endProgressBar( "An Error Occurred" );
                                        error( e.getMessage().substring( e.getMessage().indexOf( ":" ) + 1 ), e );
                                }
                        }

                        @Override
                        protected void process( List<String> chunks ) {

                                newBorder( chunks.get( chunks.size() - 1 ));
                        }
                };

                swingW.execute();
        }
}
