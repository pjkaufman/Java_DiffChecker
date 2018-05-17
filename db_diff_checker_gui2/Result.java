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

public class Result extends JFrameV2 {
        // Instance variables
        private Db_conn db;
        private JScrollPane SQL = new JScrollPane();
        private JTextArea SQLShow = new JTextArea( 5, 20 );
        private JButton btnRun = new JButton( "Run" );
        private JLabel instructLabel = new JLabel( "Run the following SQL to make the two databases the same:" );

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

                        btnRun.setVisible( false );
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
                cpnt.add( instructLabel );
                cpnr.add( SQLShow );
                cpnr.add( btnRun );
                // set up JFrame properties
                setTitle( "SQL To Run" );
                setMinimumSize( new Dimension( 600, 210 ));
                // set component properties
                instructLabel.setFont(new Font("Tahoma", 1, 18));
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
                add( instructLabel, BorderLayout.NORTH );
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

                btnRun.setVisible( false );
                prepProgressBar( "Waiting On Action", false );
                SwingWorker<Boolean, Integer> swingW = new SwingWorker<Boolean, Integer>() {

                        @Override
                        protected Boolean doInBackground() throws Exception {

                                boolean cont = true;
                                String temp = null;
                                sw.start();
                                for ( int i = 0; i < sql.size(); i++ ) {

                                        temp = sql.get( i );
                                        cont = db.runSQL( temp );
                                        if ( !cont ) {

                                                sw.stop();
                                                log( "Ran SQL on " + sw.getHour() + " at " + sw.getDate() + " with an error updating the database.", stdErr );
                                                throw new Exception( "There was an error running: " + temp );
                                        }
                                        publish(i);
                                }
                                sw.stop();
                                log( "Ran SQL on " + sw.getDate() + " at " + sw.getHour() + " in " + sw.getElapsedTime().toMillis() / 1000.0 + "s with no errors.", stdLog );

                                return true;
                        }

                        @Override
                        protected void done() {
                                try {

                                        get();
                                        instructLabel.setText( "The database has been updated." );
                                        endProgressBar( "Done" );
                                } catch ( Exception e ) {

                                        endProgressBar( "An Error Occurred" );
                                        error( e.getMessage().substring( e.getMessage().indexOf( ":" ) + 1 ), e);
                                }
                        }

                        @Override
                        protected void process( List<Integer> chunks ) {

                                btnRun.setVisible( false );
                                newBorder( "Running SQL.. " );
                                pb.setValue((int)(( chunks.get( chunks.size() - 1) + 1.0 ) * 100 / sql.size()));
                                pb.setString( pb.getPercentComplete() * 100 + "%" );
                        }

                };

                swingW.execute();
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

                                        instructLabel.setText( "The databases are in sync." );
                                }
                        } else {

                                this.sql = SQL;
                                for ( String statement: SQL ) {

                                        SQLShow.append( statement + "\n" );
                                }

                                if ( title.equals( "Run the following SQL to make the two databases the same:" )) {

                                        FileConversion.writeTo( SQL );
                                }
                                instructLabel.setText( title );
                                this.setSize( 600, 210 );
                        }

                        this.setVisible( true );
                } catch( IOException e ) {

                        log( "There was an error writing the SQL statement(s) to a file.", stdErr );
                        error( "There was an error writing the SQL statement(s) to a file.", e );
                }
        }
}
