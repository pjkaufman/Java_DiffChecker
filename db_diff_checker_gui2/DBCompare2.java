/**
 * DBCompare2 is a JFrame that takes user input to make a comparison between 2
 * databases
 * @author Peter Kaufman
 * @class DBCompare2
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

public class DBCompare2 extends JFrameV2 {
        // Instance variables
        private Db_conn db1, db2;
        private Database dab1, dab2;
        private HashMap<String, String> update_tables = new HashMap();
        private JTextField database1 = new JTextField( 10 ), database2 = new JTextField( 10 ),
                           host1 = new JTextField( 10 ), host2 = new JTextField( 10 ),
                           port1 = new JTextField( 10 ), port2 = new JTextField( 10 ),
                           username1 = new JTextField( 10 ), username2 = new JTextField( 10 );
        private JButton jButton1 = new JButton( "Compare" );
        private JLabel jLabel10 = new JLabel( "Enter MySQL Dev Port:     " ),
                       jLabel11 = new JLabel( "Enter MySQL Dev Database: " ),
                       jLabel12 = new JLabel( "Enter MySQL Live Username:" ),
                       jLabel13 = new JLabel( "Enter MySQL Live Password:" ),
                       jLabel14 = new JLabel( "Enter MySQL Live Host:    " ),
                       jLabel15 = new JLabel( "Enter MySQL Live Port:    " ),
                       jLabel16 = new JLabel( "Enter MySQL Live Database:" ),
                       jLabel6 = new JLabel( "Enter The Folowing Information:" ),
                       jLabel7 = new JLabel( "Enter MySQL Dev Username: " ),
                       jLabel8 = new JLabel( "Enter MySQL Dev Password: " ),
                       jLabel9 = new JLabel( "Enter MySQL Dev Host:     " );
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
                cpnt.add( jLabel6 );
                cpnt.add( jButton1 );
                cpnr.add( jLabel7 );
                cpnr.add( jLabel8 );
                cpnr.add( jLabel9 );
                cpnr.add( jLabel10 );
                cpnr.add( jLabel11 );
                cpnr.add( jLabel12 );
                cpnr.add( jLabel13 );
                cpnr.add( jLabel14 );
                cpnr.add( jLabel15 );
                cpnr.add( jLabel16 );
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
                pb.setVisible( false );
                jLabel6.setHorizontalAlignment( SwingConstants.CENTER );
                jLabel6.setFont(new Font("Tahoma", 1, 24));
                jButton1.setFont(new Font("Tahoma", 0, 18));
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

                                jLabel6.setFont( title );
                                jLabel7.setFont( reg );
                                jLabel8.setFont( reg );
                                jLabel9.setFont( reg );
                                jLabel10.setFont( reg );
                                jLabel11.setFont( reg );
                                jLabel12.setFont( reg );
                                jLabel13.setFont( reg );
                                jLabel14.setFont( reg );
                                jLabel15.setFont( reg );
                                jLabel16.setFont( reg );
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
                                jButton1.setFont( button );
                                myFont = reg;

                        }
                        public void componentHidden(ComponentEvent e) {
                        }
                        public void componentShown(ComponentEvent e) {
                        }
                        public void componentMoved(ComponentEvent e) {
                        }
                });
                jButton1.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent evt) {
                                jButton1ActionPerformed(evt);
                        }
                });
                // add components
                header.add( jLabel6, BorderLayout.CENTER );
                part1.add( jLabel7 );
                part2.add( jLabel12 );
                part3.add( jLabel8 );
                part4.add( jLabel13 );
                part5.add( jLabel9 );
                part6.add( jLabel14 );
                part7.add( jLabel10 );
                part8.add( jLabel15 );
                part9.add( jLabel11 );
                part10.add( jLabel16 );
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
                footc.add( jButton1 );
                footer.add( footc, BorderLayout.CENTER );
                footer.add( pb, BorderLayout.SOUTH );
                getContentPane().setLayout( new BorderLayout());
                add( header, BorderLayout.NORTH );
                add( content, BorderLayout.CENTER );
                add( footer, BorderLayout.SOUTH );
        }

        /**
         * jButton1ActionPerformed determines whether the information supplied by the
         * user is adequate, if so 2 databases are compared otherwise a message is
         * displayed
         * @author Peter Kaufman
         * @type function
         * @access private
         * @param evt is an ActionEvent which results when the compare button is clicked
         */
        private void jButton1ActionPerformed(ActionEvent evt) {
                if ( !( port1.getText().equals( "" ) | port2.getText().equals( "" ) |
                        username1.getText().equals( "" ) | username2.getText().equals( "" ) |
                        new String(password1.getPassword()).equals( "" ) | new String(password2.getPassword()).equals( "" ) |
                        host1.getText().equals( "" ) | host2.getText().equals( "" )|
                        database1.getText().equals( "" ) | database2.getText().equals( "" ))) {

                        this.error = false;
                        compare1();
                } else {

                        jLabel6.setText( "Please do not leave any fields blank." );
                }
        }

        /**
         * compare1 compares two databases based on user input
         * @author Peter Kaufman
         * @type function
         * @access private
         */
        private void compare1() {

                pb.setIndeterminate( true );
                ArrayList<String> log = new ArrayList();
                pb.setBorder( BorderFactory.createTitledBorder( "Establishing Dev Database Connection" ));
                pb.setVisible( true );
                sw.reset();
                SwingWorker<Boolean, Integer> swingW = new SwingWorker<Boolean, Integer>() {

                        @Override
                        protected Boolean doInBackground() throws Exception {

                                try {

                                        publish( 1 );
                                        sw.start();
                                        db1 = new Db_conn( username1.getText(), new String(password1.getPassword()),
                                                           host1.getText(), port1.getText(), database1.getText(), "dev" );
                                        publish( 2 );
                                        dab1 = new Database( db1 );
                                        publish( 3 );
                                        db2 = new Db_conn( username2.getText(), new String(password2.getPassword()),
                                                           host2.getText(), port2.getText(), database2.getText(), "live" );
                                        publish( 4 );
                                        dab2 = new Database( db2 );
                                        publish( 5 );
                                        sql.addAll( dab2.getFirstSteps());
                                        publish( 6 );
                                        sql.addAll( dab1.compareTables( dab2.getTables()));
                                        publish( 7 );
                                        update_tables.putAll( dab1.tablesDiffs( dab2.getTables()));
                                        publish( 8 );
                                        sql.addAll(dab1.updateTables( dab2.getTables(), update_tables ));
                                        publish( 9 );
                                        sql.addAll( dab1.getFirstSteps());
                                        publish( 10 );
                                        sql.addAll(dab1.updateViews( dab2.getViews()));
                                        sw.stop();
                                        log.add( "DB Comparison Complete on " + sw.getDate() + " at " + sw.getHour() + " in " + sw.getElapsedTime().toMillis() / 1000.0 + "s with no errors." );
                                } catch ( SQLException e ) {

                                        sw.stop();
                                        log.add( "DB Comparison Complete on " + sw.getDate() + " at " + sw.getHour() + " in " + sw.getElapsedTime().toMillis() / 1000.0 + "s with an error." );
                                        error( "There was an error with the database connection. Please try again." );
                                }

                                return true;

                        }

                        @Override
                        protected void done() {

                                try {

                                        get();
                                        pb.setBorder( BorderFactory.createTitledBorder( "Database Comparison Complete" ));
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

                                TitledBorder nBorder = BorderFactory.createTitledBorder( "Establishing Dev Database Connection" );
                                if ( chunks.get( chunks.size() - 1) == 2 ) {

                                        nBorder = BorderFactory.createTitledBorder( "Gathering Dev Database Info" );
                                } else if ( chunks.get( chunks.size() - 1) == 3 ) {

                                        nBorder = BorderFactory.createTitledBorder( "Establishing Live Database Connection" );
                                } else if ( chunks.get( chunks.size() - 1) == 4 ) {

                                        nBorder = BorderFactory.createTitledBorder( "Gathering Live Database Info" );
                                } else if ( chunks.get( chunks.size() - 1) == 5 ) {

                                        nBorder = BorderFactory.createTitledBorder( "Checking Live First Steps" );
                                } else if ( chunks.get( chunks.size() - 1) == 6 ) {

                                        nBorder = BorderFactory.createTitledBorder( "Comparing Tables" );
                                } else if ( chunks.get( chunks.size() - 1) == 7 ) {

                                        nBorder = BorderFactory.createTitledBorder( "Comparing Tables" );
                                }  else if ( chunks.get( chunks.size() - 1) == 8 ) {

                                        nBorder = BorderFactory.createTitledBorder( "Comparing Tables" );
                                } else if ( chunks.get( chunks.size() - 1) == 9 ) {

                                        nBorder = BorderFactory.createTitledBorder( "Checking Dev First Steps" );
                                } else if ( chunks.get( chunks.size() - 1) == 10 ) {

                                        nBorder = BorderFactory.createTitledBorder( "Adding Dev's Views" );
                                }
                                pb.setBorder(nBorder);
                        }
                };

                swingW.execute();
        }
}
