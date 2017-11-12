/**
* DB_Diff_Checker_GUI is a JFrame that takes user input to decide which JFrame
* to open
* @author Peter Kaufman
* @class DB_Diff_Checker_GUI
* @access public
* @version 10-31-17
* @since 9-20-17
*/
package db_diff_checker_gui2;
import java.io.IOException;
import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
public class DB_Diff_Checker_GUI extends JFrame {
    // Variable declaration
    private JFrame Start;
    private JTextField input;
    private JButton jContinue;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JLabel jLabel4;
    private JLabel jLabel5;
    private JLabel jLabel6;
    private JLabel jLabel7;
    private JLabel jLabel8;
    private JPanel jPanel1;
    private JPanel jPanel2;

    /**
     * Creates new form DB_Diff_Checker_GUI
     * @author Peter Kaufman
     * @type constructor
     * @access public
     */
    public DB_Diff_Checker_GUI() {

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

            Start = new JFrame();
            jPanel1 = new JPanel();
            jLabel2 = new JLabel( "", SwingConstants.CENTER);
            jLabel1 = new JLabel( "", SwingConstants.CENTER);
            jLabel3 = new JLabel( "", SwingConstants.CENTER);
            jLabel4 = new JLabel( "", SwingConstants.CENTER);
            jLabel6 = new JLabel( "", SwingConstants.CENTER);
            jLabel7 = new JLabel( "", SwingConstants.CENTER);
            jLabel8 = new JLabel( "", SwingConstants.CENTER);
            jPanel2 = new JPanel();
            jContinue = new JButton();
            input = new JTextField();
            jLabel5 = new JLabel();

            Start.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            setTitle("Database Difference Checker");
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            setLocation(new Point(200, 200));
            setMinimumSize( new Dimension( 370, 200 ));
            setName("Start"); 

            jLabel2.setFont(new Font("Tahoma", 1, 11));
            jLabel2.setText("Database Options");

            jLabel1.setText("1-Database compare using 2 database connections");
            jLabel3.setText("2-Database compare using 1 database connection");
            jLabel4.setText("3-Take database snapshot using 1 database connection");
            jLabel6.setText("4-Review the SQL statement(s) from the last run ");
            jLabel7.setText("5-Review the run log");
            jLabel8.setText("6-View the error log");

            jPanel1.setLayout(new GridLayout(6,1));
            jPanel1.add(jLabel1);
            jPanel1.add(jLabel3);
            jPanel1.add(jLabel4);
            jPanel1.add(jLabel6);
            jPanel1.add(jLabel7);
            jPanel1.add(jLabel8);

            jContinue.setText("Continue");
            jContinue.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent evt) {
                        jContinueMouseClicked(evt);
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

                    jLabel2.setFont( title );
                    jLabel1.setFont( reg );
                    jLabel3.setFont( reg );
                    jLabel4.setFont( reg );
                    jLabel6.setFont( reg );
                    jLabel5.setFont( reg );
                    jLabel7.setFont( reg );
                    jLabel8.setFont( reg );
                    input.setFont( reg );
                    jContinue.setFont( reg );
                }
                public void componentHidden(ComponentEvent e) {}
                public void componentShown(ComponentEvent e) {}
                public void componentMoved(ComponentEvent e) {}
            }); 

            jLabel5.setText("Enter method to use:");
            input.setColumns(1);
            jPanel2.setLayout(new FlowLayout());
            jPanel2.add(jLabel5);
            jPanel2.add(input);
            jPanel2.add(jContinue);          

            getContentPane().setLayout(new BorderLayout());
            this.add(jLabel2, BorderLayout.NORTH);
            this.add(jPanel1, BorderLayout.CENTER);
            this.add(jPanel2, BorderLayout.SOUTH);
            pack();
            setSize( 330, 230 );
    }

    /**
     * jContinueMouseClicked determines which JFrame to open based on user input
     * @author Peter Kaufman
     * @type function
     * @access public
     * @param evt is a MouseEvent which represents continue being clicked
     */
    private void jContinueMouseClicked(MouseEvent evt) {
        if ( input.getText().trim().equals( "1" )) {

            DBCompare2 compare2DBS = new DBCompare2();
            compare2DBS.setSize( 575, 325 );
            compare2DBS.setVisible( true );
            this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        } else if ( input.getText().trim().equals( "2" ) && FileConversion.fileExists( "dbsnapshot.json" )) {

            DBCompare1 compare1DB_DBSnapshot = new DBCompare1();
            compare1DB_DBSnapshot.setSize( 350, 275 );
            compare1DB_DBSnapshot.setButtonTxt( "Compare" );
            compare1DB_DBSnapshot.setTitle( "Compare Database to Snapshot" );
            compare1DB_DBSnapshot.setVisible( true );
            this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        } else if ( input.getText().trim().equals( "3" )) {

            DBCompare1 compare1DB_DBSnapshot = new DBCompare1();
            compare1DB_DBSnapshot.setSize( 350, 275 );
            compare1DB_DBSnapshot.setTitle( "Take Database Snapshot" );
            compare1DB_DBSnapshot.setButtonTxt( "Snapshot" );
            compare1DB_DBSnapshot.setVisible( true );
            this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        } else if ( input.getText().trim().equals( "2" ) && !FileConversion.fileExists( "dbsnapshot.json" )) {

            jLabel2.setText( "Please create a DB snapshot first." );
        } else if ( input.getText().trim().equals( "4" ) && FileConversion.fileExists( "LastRun.txt" )) {

            displayResult( "LastRun.txt" );
            this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        } else if (( input.getText().trim().equals( "4" ) && !FileConversion.fileExists( "LastRun.txt" )) || 
                ( input.getText().trim().equals( "5" ) && !FileConversion.fileExists( "Log.txt" ))) {

            jLabel2.setText( "The DBC has not been run before." );
        } else if ( input.getText().trim().equals( "5" ) && FileConversion.fileExists( "Log.txt" )) {

            displayResult( "Log.txt" );
            this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        } else if ( input.getText().trim().equals( "6" ) && !FileConversion.fileExists( "Error.txt" )) {

            jLabel2.setText( "An error has not occurred/error log was deleted." );
        } else if ( input.getText().trim().equals( "6" ) && FileConversion.fileExists( "Error.txt" )) {

            displayResult( "Error.txt" );
            this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        } else {

             jLabel2.setText( "Please enter a number 1 to 6." );
        }
    }

    /**
     * displayResult opens a JFrame with the result depending on what file name 
     * is passed to it
     * @author Peter Kaufman
     * @type function
     * @access private
     * @param file is a String which represents the file to have its contents 
     * displayed
     */
    private void displayResult( String file ) {
        try{

            String title;
            Result rs = new Result( null );
            if ( file.equals( "Log.txt" )) {

                title = "The Run Log:";
            } else if ( file.equals( "Error.txt" )) {

                title = "The Error Log:";
            } else {

                title = "Last Set of SQL Statements Run:";
            }
            rs.results( FileConversion.readFrom( file ), title );
            rs.setTitle( title.substring( 0, title.length() - 1 ));
        } catch( IOException e ) {

            System.err.println( e );
            e.printStackTrace();
            error( "There was an error recovering the last list of SQL statements." );

        }
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