/**
 * DB_Diff_Checker_GUI is a JFrame that takes user input to decide which JFrame to open
 * @author Peter Kaufman
 * @version 5-24-18
 * @since 9-20-17
 */
package db_diff_checker_gui2;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.*;

public class DB_Diff_Checker_GUI extends JFrameV2 {
        // Instance variables
        private JTextField input = new JTextField( 1 );
        private JButton jContinue = new JButton( "Continue" );
        private JLabel jLabel1 = new JLabel( "1-Database compare using 2 database connections", SwingConstants.CENTER),
                       jLabel2 = new JLabel( "Database Options", SwingConstants.CENTER),
                       jLabel3 = new JLabel( "2-Database compare using 1 database connection", SwingConstants.CENTER),
                       jLabel4 = new JLabel( "3-Take database snapshot using 1 database connection", SwingConstants.CENTER),
                       jLabel5 = new JLabel( "Enter method to use:" ),
                       jLabel6 = new JLabel( "4-Review the SQL statement(s) from the last run ", SwingConstants.CENTER),
                       jLabel7 = new JLabel( "5-Review the run log", SwingConstants.CENTER),
                       jLabel8 = new JLabel( "6-View the error log", SwingConstants.CENTER);
        private JPanel jPanel1 = new JPanel(), jPanel2 = new JPanel();

        /**
         * DB_Diff_Checker_GUI initializes a JFrame which will be used by the user to navigate through 
         * the application.
         * @author Peter Kaufman
         */
        public DB_Diff_Checker_GUI() {

                initComponents();
                error = false;
                clase = this.getClass().getName();
        }

        /**
         * InitComonents sets up the GUI Layout, sets up all action events, and initializes instance variables.
         * @access private
         */
        private void initComponents() {
                // add components to the appropriate ArrayList
                cpnr.add( jLabel1 );
                cpnr.add( jLabel3 );
                cpnr.add( jLabel4 );
                cpnr.add( jLabel5 );
                cpnr.add( jLabel6 );
                cpnr.add( jLabel7 );
                cpnr.add( jLabel8 );
                cpnr.add( input );
                cpnr.add( jContinue );
                cpnt.add( jLabel2 );
                // set up JFrame properties
                setSize( 330, 230 );
                setMinimumSize( new Dimension( 370, 200 ));
                this.setTitle( "Database Difference Checker" );
                // set component properties
                jLabel2.setFont(new Font("Tahoma", 1, 11));
                // add listeners
                jContinue.addMouseListener(new MouseAdapter() {
                        public void mouseClicked(MouseEvent evt) {
                                jContinueMouseClicked(evt);
                        }
                });
                // add components
                jPanel1.setLayout(new GridLayout(6,1));
                jPanel1.add(jLabel1);
                jPanel1.add(jLabel3);
                jPanel1.add(jLabel4);
                jPanel1.add(jLabel6);
                jPanel1.add(jLabel7);
                jPanel1.add(jLabel8);
                jPanel2.setLayout(new FlowLayout());
                jPanel2.add(jLabel5);
                jPanel2.add(input);
                jPanel2.add(jContinue);
                getContentPane().setLayout(new BorderLayout());
                this.add(jLabel2, BorderLayout.NORTH);
                this.add(jPanel1, BorderLayout.CENTER);
                this.add(jPanel2, BorderLayout.SOUTH);
                pack();
        }

        /**
         * jContinueMouseClicked determines which JFrame to open based on user input.
         * @author Peter Kaufman
         * @param evt is a MouseEvent which is the continue button being clicked.
         */
        private void jContinueMouseClicked(MouseEvent evt) {
                if ( input.getText().trim().equals( "1" )) {

                        DBCompare2 compare2DBS = new DBCompare2();
                        compare2DBS.setSize( 575, 325 );
                        compare2DBS.setVisible( true );
                        this.close();
                } else if ( input.getText().trim().equals( "2" ) && FileConversion.fileExists( stdSnap )) {

                        DBCompare1 compare1DB_DBSnapshot = new DBCompare1( "Compare Database to Snapshot", "Compare" );
                        compare1DB_DBSnapshot.setSize( 350, 275 );
                        compare1DB_DBSnapshot.setVisible( true );
                        this.close();
                } else if ( input.getText().trim().equals( "3" )) {

                        DBCompare1 compare1DB_DBSnapshot = new DBCompare1( "Take Database Snapshot", "Snapshot" );
                        compare1DB_DBSnapshot.setSize( 350, 275 );
                        compare1DB_DBSnapshot.setVisible( true );
                        this.close();
                } else if ( input.getText().trim().equals( "2" ) && !FileConversion.fileExists( stdSnap )) {

                        jLabel2.setText( "Please create a DB snapshot first." );
                } else if ( input.getText().trim().equals( "4" ) && FileConversion.fileExists( stdOut )) {

                        displayLog( stdOut );
                        this.close();
                } else if (( input.getText().trim().equals( "4" ) && !FileConversion.fileExists( stdOut )) ||
                           ( input.getText().trim().equals( "5" ) && !FileConversion.fileExists( stdLog ))) {

                        jLabel2.setText( "The DBC has not been run before." );
                } else if ( input.getText().trim().equals( "5" ) && FileConversion.fileExists( stdLog )) {

                        displayLog( stdLog );
                        this.close();
                } else if ( input.getText().trim().equals( "6" ) && !FileConversion.fileExists( stdErr )) {

                        jLabel2.setText( "An error has not occurred/error log was deleted." );
                } else if ( input.getText().trim().equals( "6" ) && FileConversion.fileExists( stdErr )) {

                        displayLog( stdErr );
                        this.close();
                } else {

                        jLabel2.setText( "Please enter a number 1 to 6." );
                }
        }

        /**
         * displayLog opens a JFrame with log information depending on what file name is passed to it.
         * @author Peter Kaufman
         * @param file is a String which is the file to have its contents displayed.
         */
        private void displayLog( String file ) {
                try{

                        String title;
                        Result rs = new Result( null );
                        if ( file.equals( stdLog )) {

                                title = "The Run Log:";
                        } else if ( file.equals( stdErr )) {

                                title = "The Error Log:";
                        } else {

                                title = "Last Set of SQL Statements Run:";
                        }
                        rs.results( FileConversion.readFrom( file ), title );
                        rs.setTitle( title.substring( 0, title.length() - 1 ));
                } catch( IOException e ) {

                        log( "There was an error recovering the last list of SQL statements on " + sw.getDate() + " at " + sw.getHour(), stdErr );
                        error( "There was an error recovering the last list of SQL statements.", e );
                }
        }

        /**
         * main is the main method which sets up and prepares the GUI for the user and initializes the first JFrame. 
         * @author Peter Kaufman
         * @param args is an array Strings which is not used.
         */
        public static void main(String args[]) {
                try {
                        for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                                if ("Nimbus".equals(info.getName())) {
                                        UIManager.setLookAndFeel(info.getClassName());
                                        break;
                                }
                        }
                } catch (ClassNotFoundException ex) {
                        java.util.logging.Logger.getLogger(DB_Diff_Checker_GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                } catch (InstantiationException ex) {
                        java.util.logging.Logger.getLogger(DB_Diff_Checker_GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                        java.util.logging.Logger.getLogger(DB_Diff_Checker_GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                } catch (javax.swing.UnsupportedLookAndFeelException ex) {
                        java.util.logging.Logger.getLogger(DB_Diff_Checker_GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                }

                /* Create and display the form */
                EventQueue.invokeLater(new Runnable() {
                        public void run() {
                                DB_Diff_Checker_GUI gui = new DB_Diff_Checker_GUI();
                                gui.setVisible(true);
                        }
                });
        }
}
