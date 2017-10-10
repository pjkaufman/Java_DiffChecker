/**
 * DBCompare2 is a JFrame that takes user input to make a comparison between 2 
 * databases
 * @author Peter Kaufman
 * @class DBCompare2
 * @access public
 * @version 9-21-17
 * @since 9-20-17 
 */
package db_diff_checker_GUI;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.ImageIcon;
public class DBCompare2 extends javax.swing.JFrame {

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
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        password2 = new javax.swing.JPasswordField();
        port1 = new javax.swing.JTextField();
        host2 = new javax.swing.JTextField();
        database1 = new javax.swing.JTextField();
        port2 = new javax.swing.JTextField();
        password1 = new javax.swing.JPasswordField();
        database2 = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        username1 = new javax.swing.JTextField();
        username2 = new javax.swing.JTextField();
        host1 = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Compare Two Databases");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel6.setText("Enter The Folowing Information:");

        jLabel12.setText("Enter MySQL Live Username:");

        jLabel7.setText("Enter MySQL Development Username:");

        jLabel13.setText("Enter MySQL  Live Password:");

        jLabel8.setText("Enter MySQL Development Password:");

        jLabel14.setText("Enter MySQL  Live Host:");

        jLabel9.setText("Enter MySQL Development Host:");

        jLabel15.setText("Enter MySQL  Live Port:");

        jLabel10.setText("Enter MySQL Development Port:");

        jLabel16.setText("Enter MySQL  Live Database:");

        jLabel11.setText("Enter MySQL Development Database:");

        jButton1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jButton1.setText("Compare");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(181, 181, 181)
                        .addComponent(jButton1)
                        .addGap(189, 189, 189))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel8)
                                    .addComponent(jLabel9)
                                    .addComponent(jLabel10)
                                    .addComponent(jLabel7))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addGap(6, 6, 6)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(database1)
                            .addComponent(username1, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(password1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                            .addComponent(host1)
                            .addComponent(port1, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel16)
                            .addComponent(jLabel12)
                            .addComponent(jLabel13)
                            .addComponent(jLabel14)
                            .addComponent(jLabel15))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(username2)
                                .addComponent(password2, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                                .addComponent(host2, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(port2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(database2, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(43, 43, 43)
                        .addComponent(jLabel6)))
                .addContainerGap(25, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(username1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(password1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(host1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel14))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(port1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel15))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11)
                            .addComponent(database1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(username2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(password2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel13))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(host2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(port2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(database2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel16))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton1))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * jButton1ActionPerformed determines whether the information supplied by the
     * user is adequate, if so 2 databases are compared otherwise a message is
     * displayed
     * @author Peter Kaufman
     * @type function
     * @access private
     * @param evt is an ActionEvent which results when the compare button is clicked
     */
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
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
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * formWindowClosing opens the start JFrame when the form is closing and the
     * compare button has not been clicked
     * @author Peter Kaufman
     * @type function
     * @access private
     * @param evt is a WindowEvent which represents the JFrame closing
     */
    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        if ( error ) {
            
            DB_Diff_Checker_GUI start = new DB_Diff_Checker_GUI();
            start.setSize( 375, 225 );
            start.setVisible( true );
        }
    }//GEN-LAST:event_formWindowClosing

    /**
     * compare1 compares two databases based on user input
     * @author Peter Kaufman
     * @type function
     * @access private
     */
    private void compare1() {
        try {
            db1 = new Db_conn( username1.getText(), new String(password1.getPassword()), 
                            host1.getText(), port1.getText(), database1.getText());
            dab1 = new Database( db1 );

            db2 = new Db_conn( username2.getText(), new String(password2.getPassword()), 
                    host2.getText(), port2.getText(), database2.getText());
            dab2 = new Database( db2 );

            sql.addAll( dab1.compareTables( dab2.getTables()));
            update_tables.addAll( dab1.tablesDiffs( dab2.getTables()));
            sql.addAll(dab1.updateTables( dab2.getTables(), update_tables ));
            sql.addAll(dab1.updateViews( dab2.getViews()));

            displayResult();
            this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        } catch ( SQLException e ) {
        
            error( "There was an error with the database connection. Please try again." );
        }
    }
    
    /**
     * displayResult opens a JFrame with the result of the comparison
     * @author Peter Kaufman
     * @type function
     * @access private
     */
    private void displayResult() {
           
        Result rs = new Result();
        rs.results( sql );
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
        err.setSize( 400, 100 );
        err.setVisible( true );
        this.error = true;
    }
            
    private boolean error = true;
    private Db_conn db1, db2;
    private Database dab1, dab2;
    private ArrayList<String> sql = new ArrayList();
    private ArrayList<String> update_tables = new ArrayList();
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField database1;
    private javax.swing.JTextField database2;
    private javax.swing.JTextField host1;
    private javax.swing.JTextField host2;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPasswordField password1;
    private javax.swing.JPasswordField password2;
    private javax.swing.JTextField port1;
    private javax.swing.JTextField port2;
    private javax.swing.JTextField username1;
    private javax.swing.JTextField username2;
    // End of variables declaration//GEN-END:variables
}