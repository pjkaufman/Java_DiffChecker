/**
 * DBCompare2 is a JFrame that takes user input to make a comparison between 2
 * databases
 * @author Peter Kaufman
 * @class DBCompare2
 * @access public
 * @version 5-15-18
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

public class DBCompare2 extends JFrameV2 {
        // Instance variables
        private Db_conn db1, db2;
        private Database dab1, dab2;
        private HashMap<String, String> update_tables = new HashMap<>();
        private JTextField database1 = new JTextField( 10 ), database2 = new JTextField( 10 ),
                           host1 = new JTextField( 10 ), host2 = new JTextField( 10 ),
                           port1 = new JTextField( 10 ), port2 = new JTextField( 10 ),
                           username1 = new JTextField( 10 ), username2 = new JTextField( 10 );
        private JButton execute = new JButton( "Compare" );
        private JLabel devPortLabel = new JLabel( "Enter MySQL Dev Port:     " ),
                       devDBLabel = new JLabel( "Enter MySQL Dev Database: " ),
                       liveUserNameLabel = new JLabel( "Enter MySQL Live Username:" ),
                       livePassLabel = new JLabel( "Enter MySQL Live Password:" ),
                       liveHostLabel = new JLabel( "Enter MySQL Live Host:    " ),
                       livePortLabel = new JLabel( "Enter MySQL Live Port:    " ),
                       liveDBLabel = new JLabel( "Enter MySQL Live Database:" ),
                       headT = new JLabel( "Enter The Folowing Information:" ),
                       devUserNameLabel = new JLabel( "Enter MySQL Dev Username: " ),
                       devPassLabel = new JLabel( "Enter MySQL Dev Password: " ),
                       devHostLabel = new JLabel( "Enter MySQL Dev Host:     " );
        private JPasswordField password1 = new JPasswordField( 10 ), password2 = new JPasswordField( 10 );

        /**
         * Creates new form DBCompare2
         * @author Peter Kaufman
         * @type constructor
         * @access public
         */
        public DBCompare2() {

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
                cpnt.add( headT );
                cpnt.add( execute );
                cpnr.add( devUserNameLabel );
                cpnr.add( devPassLabel );
                cpnr.add( devHostLabel );
                cpnr.add( devPortLabel );
                cpnr.add( devDBLabel );
                cpnr.add( liveUserNameLabel );
                cpnr.add( livePassLabel );
                cpnr.add( liveHostLabel );
                cpnr.add( livePortLabel );
                cpnr.add( liveDBLabel );
                cpnr.add( username1 );
                cpnr.add( password1 );
                cpnr.add( username2 );
                cpnr.add( password2 );
                cpnr.add( host1 );
                cpnr.add( host2 );
                cpnr.add( port1 );
                cpnr.add( port2 );
                cpnr.add( database1 );
                cpnr.add( database2 );
                // set up JFrame properties
                setMinimumSize( new Dimension( 630, 325 ));
                setTitle("Compare Two Databases");
                // set component properties
                headT.setHorizontalAlignment( SwingConstants.CENTER );
                headT.setFont(new Font("Tahoma", 1, 24));
                execute.setFont(new Font("Tahoma", 0, 18));
                // create JPanels
                JPanel header = new JPanel( new BorderLayout()), content = new JPanel( new BorderLayout()),
                       footer = new JPanel( new BorderLayout()), part1 = new JPanel( new FlowLayout()),
                       part2 = new JPanel( new FlowLayout()), part3 = new JPanel( new FlowLayout()),
                       part4 = new JPanel( new FlowLayout()), part5 = new JPanel( new FlowLayout()),
                       part6 = new JPanel( new FlowLayout()), part7 = new JPanel( new FlowLayout()),
                       part8 = new JPanel( new FlowLayout()), part9 = new JPanel( new FlowLayout()),
                       part10 = new JPanel( new FlowLayout()), part11 = new JPanel( new FlowLayout()),
                       part12 = new JPanel( new FlowLayout()), part13 = new JPanel( new FlowLayout()),
                       part14 = new JPanel( new FlowLayout()), part15 = new JPanel( new FlowLayout()),
                       part16 = new JPanel( new FlowLayout()), part17 = new JPanel( new FlowLayout()),
                       part18 = new JPanel( new FlowLayout()), part19 = new JPanel( new FlowLayout()),
                       part20 = new JPanel( new FlowLayout()), c1 = new JPanel( new GridLayout( 5, 2 )),
                       c2 = new JPanel( new GridLayout( 5, 2 )), footc = new JPanel( new FlowLayout());
                // add listeners
                addComponentListener(new ComponentListener() {
                        public void componentResized(ComponentEvent e) {

                                double width = e.getComponent().getWidth();
                                Font title = new Font("Tahoma", Font.BOLD, 24), reg = new Font("Tahoma", Font.PLAIN, 11),
                                button = new Font("Tahoma", Font.BOLD, 18);
                                if ( width >= 660 ) {

                                        title = new Font("Tahoma", Font.BOLD, (int)( width / 25 ));
                                        reg = new Font("Tahoma", Font.PLAIN, (int)( width / 56 ));
                                        button = new Font("Tahoma", Font.BOLD, (int)( width / 34 ));
                                }

                                headT.setFont( title );
                                devUserNameLabel.setFont( reg );
                                devPassLabel.setFont( reg );
                                devHostLabel.setFont( reg );
                                devPortLabel.setFont( reg );
                                devDBLabel.setFont( reg );
                                liveUserNameLabel.setFont( reg );
                                livePassLabel.setFont( reg );
                                liveHostLabel.setFont( reg );
                                livePortLabel.setFont( reg );
                                liveDBLabel.setFont( reg );
                                username1.setFont( reg );
                                password1.setFont( reg );
                                username2.setFont( reg );
                                password2.setFont( reg );
                                host1.setFont( reg );
                                host2.setFont( reg );
                                port1.setFont( reg );
                                port2.setFont( reg );
                                database1.setFont( reg );
                                database2.setFont( reg );
                                execute.setFont( button );
                                myFont = reg;

                        }
                        public void componentHidden(ComponentEvent e) {
                        }
                        public void componentShown(ComponentEvent e) {
                        }
                        public void componentMoved(ComponentEvent e) {
                        }
                });
                execute.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent evt) {
                                executeActionPerformed(evt);
                        }
                });
                // add components
                header.add( headT, BorderLayout.CENTER );
                part1.add( devUserNameLabel );
                part2.add( liveUserNameLabel );
                part3.add( devPassLabel );
                part4.add( livePassLabel );
                part5.add( devHostLabel );
                part6.add( liveHostLabel );
                part7.add( devPortLabel );
                part8.add( livePortLabel );
                part9.add( devDBLabel );
                part10.add( liveDBLabel );
                part11.add( username1 );
                part12.add( username2 );
                part13.add( password1 );
                part14.add( password2 );
                part15.add( host1 );
                part16.add( host2 );
                part17.add( port1 );
                part18.add( port2 );
                part19.add( database1 );
                part20.add( database2 );
                c1.add( part1 );
                c1.add( part11 );
                c2.add( part2 );
                c2.add( part12 );
                c1.add( part3 );
                c1.add( part13 );
                c2.add( part4 );
                c2.add( part14 );
                c1.add( part5 );
                c1.add( part15 );
                c2.add( part6 );
                c2.add( part16 );
                c1.add( part7 );
                c1.add( part17 );
                c2.add( part8 );
                c2.add( part18 );
                c1.add( part9 );
                c1.add( part19 );
                c2.add( part10 );
                c2.add( part20 );
                content.add( c1, BorderLayout.WEST );
                content.add( c2, BorderLayout.EAST );
                footc.add( execute );
                footer.add( footc, BorderLayout.CENTER );
                footer.add( pb, BorderLayout.SOUTH );
                getContentPane().setLayout( new BorderLayout());
                add( header, BorderLayout.NORTH );
                add( content, BorderLayout.CENTER );
                add( footer, BorderLayout.SOUTH );
        }

        /**
         * executeActionPerformed determines whether the information supplied by the
         * user is adequate, if so 2 databases are compared otherwise a message is
         * displayed
         * @author Peter Kaufman
         * @type function
         * @access private
         * @param evt is an ActionEvent which results when the compare button is clicked
         */
        private void executeActionPerformed(ActionEvent evt) {
                if ( !( port1.getText().equals( "" ) | port2.getText().equals( "" ) |
                        username1.getText().equals( "" ) | username2.getText().equals( "" ) |
                        new String(password1.getPassword()).equals( "" ) | new String(password2.getPassword()).equals( "" ) |
                        host1.getText().equals( "" ) | host2.getText().equals( "" )|
                        database1.getText().equals( "" ) | database2.getText().equals( "" ))) {

                        this.error = false;
                        compare1();
                } else {

                        headT.setText( "Please do not leave any fields blank." );
                }
        }

        /**
         * compare1 compares two databases based on user input
         * @author Peter Kaufman
         * @type function
         * @access private
         */
        private void compare1() {

                prepProgressBar( "Establishing Dev Database Connection", true );
                SwingWorker<Boolean, String> swingW = new SwingWorker<Boolean, String>() {

                        @Override
                        protected Boolean doInBackground() throws Exception {
                                try {

                                        publish( "Establishing Dev Database Connection" );
                                        sw.start();
                                        db1 = new Db_conn( username1.getText(), new String(password1.getPassword()),
                                                           host1.getText(), port1.getText(), database1.getText(), "dev" );
                                        publish( "Gathering Dev Database Info" );
                                        dab1 = new Database( db1 );
                                        publish( "Establishing Live Database Connection" );
                                        db2 = new Db_conn( username2.getText(), new String(password2.getPassword()),
                                                           host2.getText(), port2.getText(), database2.getText(), "live" );
                                        publish( "Gathering Live Database Info" );
                                        dab2 = new Database( db2 );
                                        publish( "Checking Live First Steps" );
                                        sql.addAll( dab2.getFirstSteps());
                                        publish( "Comparing Tables" );
                                        sql.addAll( dab1.compareTables( dab2.getTables()));
                                        publish( "Comparing Tables" );
                                        update_tables.putAll( dab1.tablesDiffs( dab2.getTables()));
                                        sql.addAll(dab1.updateTables( dab2.getTables(), update_tables ));
                                        publish( "Checking Dev First Steps" );
                                        sql.addAll( dab1.getFirstSteps());
                                        publish( "Adding Dev's Views" );
                                        sql.addAll(dab1.updateViews( dab2.getViews()));
                                        sw.stop();
                                        log( "DB Comparison Complete on " + sw.getDate() + " at " + sw.getHour() + " in " + sw.getElapsedTime().toMillis() / 1000.0 + "s with no errors.", stdLog );
                                } catch ( SQLException e ) {

                                        sw.stop();
                                        log( "DB Comparison Complete on " + sw.getDate() + " at " + sw.getHour() + " in " + sw.getElapsedTime().toMillis() / 1000.0 + "s with an SQL error.", stdErr );
                                        throw new Exception( "There was an error with the database connection. Please try again." );
                                }

                                return true;
                        }

                        @Override
                        protected void done() {
                                try {

                                        get();
                                        endProgressBar( "Database Comparison Complete" );
                                        displayResult( db2 );
                                        close();
                                } catch ( Exception e ) {

                                        endProgressBar( "An Error Occurred" );
                                        error( e.getMessage().substring( e.getMessage().indexOf( ":" ) + 1 ), e);
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
