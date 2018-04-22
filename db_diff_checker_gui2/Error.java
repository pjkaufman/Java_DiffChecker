/**
* Error is a JFrame that shows a message about an error that occurred
* @author Peter Kaufman
* @class Error
* @access public
* @version 10-25-17
* @since 9-21-17
*/
package db_diff_checker_gui2;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
public class Error extends JFrame {
    // Variable declaration
    private JLabel errorLabel;
    private JLabel titleLabel;
    
    /**
     * Creates new form Error
     * @author Peter Kaufman
     * @type constructor
     * @access public
     * @param error is a String that is the error to be displayed
     */
    public Error( String error ) {

            initComponents();
            this.setIconImage( new ImageIcon( getClass().getResource( "/Images/DBCompare.png" )).getImage());
            this.errorLabel.setText( error );
            try{

                ArrayList<String> err = new ArrayList();
                // code by Artur: https://stackoverflow.com/questions/833768/java-code-for-getting-current-time
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat date = new SimpleDateFormat( "yyyy-MM-dd HH:mm" );
                err.add( date.format( cal.getTime()) + ": " + error );
                FileConversion.writeTo( err, "Error.txt" );
            } catch( IOException e ) {

                e.printStackTrace();
            }
    }

    /**
    * InitComonents sets up the GUI Layout, sets up all action events, 
    * and initializes instance variables
    * @author Peter Kaufman
    * @type function
    * @access private
    */
    private void initComponents() {

            titleLabel = new JLabel();
            errorLabel = new JLabel();

            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            setTitle("Error");
            setType(Window.Type.POPUP);
            setMinimumSize( new Dimension( 476, 90 ));

            titleLabel.setFont(new Font("Tahoma", 1, 18));
            titleLabel.setText("An Error Occured.");
            errorLabel.setFont(new Font("Tahoma", 0, 14));

            addComponentListener(new ComponentListener() {
                public void componentResized(ComponentEvent e) {

                    double width = e.getComponent().getWidth();
                    Font title = new Font("Tahoma", Font.BOLD, 18), reg = new Font("Tahoma", Font.PLAIN, 14);
                    if ( width >= 500 ) {

                        title = new Font("Tahoma", Font.BOLD, (int)( width / 26 ));
                        reg = new Font("Tahoma", Font.PLAIN, (int)( width / 34 ));
                    }

                    titleLabel.setFont( title );
                    errorLabel.setFont( reg );


                }
                public void componentHidden(ComponentEvent e) {}
                public void componentShown(ComponentEvent e) {}
                public void componentMoved(ComponentEvent e) {}
            }); 

            getContentPane().setLayout( new BorderLayout());
            add( titleLabel, BorderLayout.NORTH );
            add( errorLabel, BorderLayout.CENTER );
            pack();
    }
}