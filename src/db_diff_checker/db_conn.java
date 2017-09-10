/**
 * db_conn establishes a connection with a MySQL database based on the password, 
 * username, port, host, and database provided.
 * @author Peter Kaufman
 * @class db_conn
 * @access public
 * @version 9-9-17
 * @since 9-6-17
 */
package db_diff_checker;
import java.sql.*;
import java.util.ArrayList;
public class db_conn {
    
    private final String username, password, host, db, conn_string, port;
    private Connection con = null;
    
    /**
     * db_conn initializes objects of type db_conn
     * @author Peter Kaufman
     * @type constructor
     * @access public
     * @param username is the username of the MySQL account
     * @param password is the password of the MySQL account
     * @param host is the host of the MySQL account
     * @param port is the port MySQL is using
     * @param database is the db in MySQL that the connection is to be established with
     */
    db_conn ( String username, String password, String host, String port, String database ) {
        
        this.username = username;
        this.password = password;
        this.host = host;
        this.db = database;
        this.port = port;
        this.conn_string = "jdbc:mysql://" + this.host + ":" +  this.port + "/" + this.db + "?autoReconnect=true&useSSL=false&maxReconnects=150";
    }
    
    /**
     * make_conn makes a connection with the desired db
     * @author Juan Nadal
     * @type function
     * @access public
     * @see https://www.youtube.com/watch?v=e3gnhsGqNmI&t=158s        
     */
    public void make_conn() {
        
        try {
            
            this.con = DriverManager.getConnection( this.conn_string , this.username, this.password );  
        } catch (SQLException e) {
            
            System.err.println( e );
        }
    }
    
    /**
     * kill_conn kills the db connection
     * @author Peter Kaufman
     * @type function
     * @access public
     */
    public void kill_conn() {
        
        try {
            
            this.con.close();
        } catch (SQLException e) {
            
            System.err.println( e );
        }
    }
    
    public void query ( String sql ) {
    
        try {
            
            Statement query = this.con.createStatement();
            ResultSet set = query.executeQuery( sql );
            while (set.next()) {
                System.out.println(set.getInt("id"));
            }
        } catch (SQLException e) {
            
            System.err.println( e );
        }    
    }
    
    /**
     * getCreateStatement gets and returns the create statement of the desired 
     * table
     * @author Peter Kaufman
     * @type function
     * @access public
     * @param table is a String which is the name of the table for which the
     * create statement should be retrieved
     * @return a String which is the table's create statement
     */
    public String getCreateStatement( String table ) {
    
        try {
            
            Statement query = this.con.createStatement();
            ResultSet set = query.executeQuery( "SHOW CREATE TABLE `" + table + "` -- create table" );
            set.next(); // move to the first result
            
            return set.getString( "Create Table" );
        } catch (SQLException e) {
            
            System.err.println( e );
        }
        
        return "";
    }
    
    /**
     * getTables gets the tables of the db
     * @author Peter Kaufman
     * @type getter
     * @access public
     * @return tables is an ArrayList of Strings which are the names of the tables
     */
    public ArrayList<String> getTables() {
            
        ArrayList<String> tables = new ArrayList();
        
        try {
            
            String sql = "SHOW TABLES", column = "Tables_in_" + this.db;
            Statement query = this.con.createStatement();
            ResultSet set = query.executeQuery( sql );
                    
            while (set.next()) {
                
                tables.add( set.getString( column ));
            }
            
            return tables;
        } catch (SQLException e) {
            
            System.err.println( e );
        }
        
        return tables;
    }
    
    public static void main ( String[] args ) {
    }
}
