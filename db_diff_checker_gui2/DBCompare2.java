/**
 * DBCompare2 is a JFrame that takes user input to make a comparison between 2
 * databases
 * @author Peter Kaufman
 * @class DBCompare2
 * @access public
 * @version 5-13-18
 * @since 9-20-17
 */
package db_diff_checker_gui2;
import java.awt.event.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.io.IOException;
import javax.swing.*;
import javax.swing.border.TitledBorder;
public class DBCompare2 extends JFrame {
        // Variable declaration
        private boolean error = true;
        private Db_conn db1, db2;
        private Database dab1, dab2;
        private ArrayList<String> sql = new ArrayList();
        private HashMap<String, String> update_tables = new HashMap();
        private JTextField database1;
        private JTextField database2;
        private JTextField host1;
        private JTextField host2;
        private JButton jButton1;
        private JLabel jLabel10;
        private JLabel jLabel11;
        private JLabel jLabel12;
        private JLabel jLabel13;
        private JLabel jLabel14;
        private JLabel jLabel15;
        private JLabel jLabel16;
        private JLabel jLabel6;
        private JLabel jLabel7;
        private JLabel jLabel8;
        private JLabel jLabel9;
        private JPasswordField password1;
        private JPasswordField password2;
        private JTextField port1;
        private JTextField port2;
        private JTextField username1;
        private JTextField username2;
        private StopWatch sw = new StopWatch();
        private JProgressBar pb = new JProgressBar();
        private Font myFont;

        /**
         * Creates new form DBCompare2
         * @author Peter Kaufman
         * @type constructor
         * @access public
         */
        public DBCompare2() {

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

                pb.setVisible( false );
                password2 = new JPasswordField();
                port1 = new JTextField();
                host2 = new JTextField();
                database1 = new JTextField();
                port2 = new JTextField();
                password1 = new JPasswordField();
                database2 = new JTextField();
                jLabel6 = new JLabel();
                jLabel12 = new JLabel();
                jLabel7 = new JLabel();
                jLabel13 = new JLabel();
                jLabel8 = new JLabel();
                jLabel14 = new JLabel();
                jLabel9 = new JLabel();
                jLabel15 = new JLabel();
                jLabel10 = new JLabel();
                jLabel16 = new JLabel();
                jLabel11 = new JLabel();
                jButton1 = new JButton();
                username1 = new JTextField();
                username2 = new JTextField();
                host1 = new JTextField();
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

                setMinimumSize( new Dimension( 630, 325 ));
                setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                setTitle("Compare Two Databases");
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
                addWindowListener(new WindowAdapter() {
                        public void windowClosing(WindowEvent evt) {
                                formWindowClosing(evt);
                        }
                });

                jLabel6.setHorizontalAlignment( SwingConstants.CENTER );
                jLabel6.setFont(new Font("Tahoma", 1, 24));
                jLabel6.setText( "Enter The Folowing Information:" );
                jLabel12.setText( "Enter MySQL Live Username:" );
                jLabel7.setText( "Enter MySQL Dev Username: " );
                jLabel13.setText( "Enter MySQL Live Password:" );
                jLabel8.setText( "Enter MySQL Dev Password: " );
                jLabel14.setText( "Enter MySQL Live Host:    " );
                jLabel9.setText( "Enter MySQL Dev Host:     " );
                jLabel15.setText( "Enter MySQL Live Port:    " );
                jLabel10.setText( "Enter MySQL Dev Port:     " );
                jLabel16.setText( "Enter MySQL Live Database:" );
                jLabel11.setText( "Enter MySQL Dev Database: " );
                database1.setColumns(10);
                host1.setColumns(10);
                port1.setColumns(10);
                password1.setColumns(10);
                username1.setColumns(10);
                database2.setColumns(10);
                host2.setColumns(10);
                port2.setColumns(10);
                password2.setColumns(10);
                username2.setColumns(10);

                jButton1.setFont(new Font("Tahoma", 0, 18));
                jButton1.setText("Compare");
                jButton1.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent evt) {
                                jButton1ActionPerformed(evt);
                        }
                });

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
         * formWindowClosing opens the start JFrame when the form is closing and the
         * compare button has not been clicked
         * @author Peter Kaufman
         * @type function
         * @access private
         * @param evt is a WindowEvent which represents the JFrame closing
         */
        private void formWindowClosing(WindowEvent evt) {
                if ( error ) {

                        DB_Diff_Checker_GUI start = new DB_Diff_Checker_GUI();
                        start.setSize( 375, 225 );
                        start.setVisible( true );
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
