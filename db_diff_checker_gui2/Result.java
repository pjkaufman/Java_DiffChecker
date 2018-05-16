/**
 * Result is a JFrame that shows the SQL statements to be run to make the
 * databases the same
 * @author Peter Kaufman
 * @class Result
 * @access public
 * @version 5-14-18
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

public class Result extends JFrameV2 {
        // Instance variables
        private Db_conn db;
        private JScrollPane SQL = new JScrollPane();
        private JTextArea SQLShow = new JTextArea( 5, 20 );
        private JButton btnRun = new JButton( "Run" );
        private JLabel jLabel17 = new JLabel( "Run the following SQL to make the two databases the same:" );
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
                        pb.setVisible( false );
                }
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
                cpnt.add( jLabel17 );
                cpnr.add( SQLShow );
                cpnr.add( btnRun );
                // set up JFrame properties
                setTitle("SQL To Run");
                setMinimumSize( new Dimension( 600, 210 ));
                // set component properties
                pb.setValue(0);
                pb.setStringPainted(true);
                TitledBorder border = BorderFactory.createTitledBorder( "Waiting On Action" );
                border.setTitleFont( myFont );
                pb.setBorder(border);
                jLabel17.setFont(new Font("Tahoma", 1, 18));
                SQL.setAutoscrolls(true);
                SQLShow.setEditable(false);
                SQL.setViewportView(SQLShow);
                // add listeners
                btnRun.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent evt) {
                                btnRunActionPerformed(evt);
                        }
                });
                // add components
                getContentPane().setLayout( new BorderLayout());
                add( jLabel17, BorderLayout.NORTH );
                add( SQL, BorderLayout.CENTER );
                add( btnRun, BorderLayout.EAST );
                add( pb, BorderLayout.SOUTH );
                pack();
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
                                        TitledBorder nBorder = BorderFactory.createTitledBorder( "Done" );
                                        nBorder.setTitleFont( myFont );
                                        pb.setBorder(nBorder);
                                        pb.setValue(100);
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
                                TitledBorder nBorder = BorderFactory.createTitledBorder( "Running SQL.. " );
                                nBorder.setTitleFont( myFont );
                                pb.setBorder(nBorder);
                                pb.setValue((int)(( chunks.get( chunks.size() - 1) + 1.0 ) * 100 / sql.size()));
                                pb.setString( pb.getPercentComplete() * 100 + "%" );
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
}
