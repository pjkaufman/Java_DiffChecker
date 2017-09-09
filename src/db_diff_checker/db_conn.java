/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db_diff_checker;
import java.sql.*;
/**
 *
 * @author topse
 */
public class db_conn {
    
    private String username, password, host, db, conn_string, port;
    private Connection con = null;
    
    /**
     * @author Peter Kaufman
     * @param username is the username of the mysql account
     * @param password is the password of the mysql account
     * @param host is the host of the mysql account
     * @param port is the port mysql is using
     * @param database is the db in mysql that the connection is to be established with
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
     * Make connection with the desired db
     * @author Juan Nadal
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
    
    public String[] getTables() {
            
        String[] tables = {};
        
        try {
            
            String sql = "SHOW TABLES", column = "Tables_in_" + this.db;
            int i = 0;
            Statement query = this.con.createStatement();
            ResultSet set = query.executeQuery( sql );
            set.last(); // get the last result
            tables = new String[ set.getRow() ]; 
            set.beforeFirst(); // reset the ResultSetObject
                    
            while (set.next()) {
                
                tables[ i ] = set.getString( column );
                i++;
            }
            
            return tables;
        } catch (SQLException e) {
            
            System.err.println( e );
        }
        
        return tables;
    }
    
    public static void main ( String[] args ) {
        
//        db_conn blob = new db_conn( "root", "ch@1RLes2", "localhost", "3306", "project1" );
//        blob.make_conn();
//        String[] tables = blob.getTables();
//        blob.kill_conn();
//        for ( int i = 0; i < tables.length; i++ ) {
//            
//            System.out.println( "Table #" + i + " is " + tables[i]  );
//        }
    }
}
