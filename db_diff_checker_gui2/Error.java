/**
 * Error is a JFrame that shows a message about an error that occurred.
 * @author Peter Kaufman
 * @version 5-24-18
 * @since 9-21-17
 */
package db_diff_checker_gui2;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.*;

public class Error extends JFrameV2 {
        // Instance variables
        private JLabel errorLabel = new JLabel(), titleLabel = new JLabel();

        /**
         * Error initializes an Error object with the provided error message which is displayed for the user to see.
         * @author Peter Kaufman
         * @param error is a String that is the error to be displayed to the user.
         */
        public Error( String error ) {

                this.error = false;
                initComponents();
                this.errorLabel.setText( error );
        }

        /**
         * InitComonents sets up the GUI Layout, sets up all action events, and initializes instance variables
         * @author Peter Kaufman
         */
        private void initComponents() {
                // set up JFrame properties
                setTitle( "Error" );
                setType( Window.Type.POPUP );
                setMinimumSize( new Dimension( 476, 90 ));
                setResizable( false );
                // set component properties
                titleLabel.setFont( new Font( "Tahoma", 1, 18 ));
                titleLabel.setText( "An Error Occured." );
                errorLabel.setFont( new Font( "Tahoma", 0, 14 ));
                // add components
                getContentPane().setLayout( new BorderLayout());
                add( titleLabel, BorderLayout.NORTH );
                add( errorLabel, BorderLayout.CENTER );
                pack();
        }
}
