/**
 * Db_conn establishes a connection with a MySQL database based on the password, 
 * username, port, host, and database provided.
 * @author Peter Kaufman
 * @class Db_conn
 * @access public
 * @version 9-9-17
 * @since 9-6-17
 */
package db_diff_checker;
import java.sql.*;
import java.util.ArrayList;
public class Db_conn {
    
    private String username = "", password = "", host = "", db = "", conn_string = "", port = "";
    private Connection con = null;
    
    /**
     * Db_conn initializes objects of type db_conn
     * @author Peter Kaufman
     * @type constructor
     * @access public
     * @param username is the username of the MySQL account
     * @param password is the password of the MySQL account
     * @param host is the host of the MySQL account
     * @param port is the port MySQL is using
     * @param database is the db in MySQL that the connection is to be established with
     */
    Db_conn ( String username, String password, String host, String port, String database ) {
        
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
     * 
     * @author Peter Kaufman
     * @type function
     * @access public
     * @param sql
     * @return 
     */
    public ResultSet query( String sql) {
        
        try {
            
            Statement query = this.con.createStatement();
            ResultSet set = query.executeQuery( sql );
            
            return set;
        } catch (SQLException e) {
            
            System.err.println( e );
        }
        
        return null;
    }
    
    /**
     * getDB returns the name of the db
     * @author Peter Kaufman
     * @type getter
     * @access public
     * @return db is a String which is the name of the db
     */
    public String getDB() {
    
        return this.db;
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
    
    /**
     * getTableCreateStatement gets and returns the create statement of the desired 
     * table
     * @author Peter Kaufman
     * @type function
     * @access public
     * @param table is a String which is the name of the table for which the
     * create statement should be retrieved
     * @return a String which is the table's create statement
     */
    public String getTableCreateStatement( String table ) {
    
        try {
            
            Statement query = this.con.createStatement();
            ResultSet set = query.executeQuery( "SHOW CREATE TABLE `" + table + "` -- create table;" );
            set.next(); // move to the first result
            return set.getString( "Create Table" );
        } catch (SQLException e) {
      
            System.err.println( e );
        }
        
        return "";
    }
    
     /**
     * getViewCreateStatement gets and returns the create statement of the desired 
     * table
     * @author Peter Kaufman
     * @type function
     * @access public
     * @param view is a String which is the name of the view for which the
     * create statement should be retrieved
     * @return a String which is the view's create statement
     */
    public String getViewCreateStatement( String view ) {
    
        try {
            
            Statement query = this.con.createStatement();
            ResultSet set = query.executeQuery( "SHOW CREATE VIEW `" + view + "` -- create view" );
            set.next(); // move to the first result
            
            return set.getString( "Create View" );
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
    public ArrayList<String> getTableList() {
            
        ArrayList<String> tables2 = new ArrayList();
        
        try {
            String sql = "SHOW TABLES", column = "Tables_in_" + this.db;
            Statement query = this.con.createStatement();
            ResultSet set = query.executeQuery( sql );
                    
            while (set.next()) {
                
                tables2.add( set.getString( column ));
            }
            
            return tables2;
        } catch (SQLException e) {
            
            System.err.println( e );
        }
        
        return tables2;
    }
    
    /**
     * getViews gets the views of the db
     * @author Peter Kaufman
     * @type function
     * @access public
     * @return tables is an ArrayList of Strings which are the names of the tables
     */
    public ArrayList<String> getViewNames() {
            
        ArrayList<String> views1 = new ArrayList();
 
        try {
            
            String sql = "SHOW FULL TABLES IN `" + this.db + "` WHERE TABLE_TYPE = 'VIEW';", column = "Tables_in_" + this.db;
            Statement query = this.con.createStatement();
            ResultSet set = query.executeQuery( sql );
                    
            while (set.next()) {
                
                views1.add( set.getString( column ));
            }
            
            return views1;
        } catch (SQLException e) {
            
            System.err.println( e );
        }
        
        return views1;
    }    
}