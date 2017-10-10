/**
 * Db_Diff_Checker allows the user to choose which type of comparison to do 
 * then, the SQL that is needed to make the db(s) is output to make the db(s) 
 * the same
 * @author Peter Kaufman
 * @class Db_Diff_Checker
 * @access public
 * @version 9-9-17
 * @since 9-6-17 
 */
package db_diff_checker;
import java.util.Scanner;
import java.util.ArrayList;
import java.io.IOException;
import java.sql.SQLException;
public class Db_Diff_Checker {
 
    private static Db_conn db1, db2;
    private static Database dab1, dab2;
    private static ArrayList<String> sql = new ArrayList();
    private static ArrayList<String> update_tables = new ArrayList();
    private final static Scanner IN = new Scanner( System.in );
   
    /**
     * getDBInfo gets information about the db from the user and returns an 
     * array with the results
     * @author Peter Kaufman
     * @type function
     * @access public
     * @param type is the type of db to have info collected on
     * @return info is the array containing the db info
     */
    public static String[] getDBInfo( String type ) {
        
        String input;
        
        System.out.print( "Enter the " + type + " database information seperated by a space (username password host port database): " );
        input = IN.next();
        input += IN.nextLine();
        
        return input.split(" ");
    }
    
    /**
     * DBCompare1 compares two db's to determine if any SQL is needed to make 
     * them the exact same 
     * @author Peter Kaufman
     * @type function
     * @access public
     */
    public static void DBCompare1() { // works
        try{
            String[] info;
            info = getDBInfo( "development" );
            db1 = new Db_conn( info[0], info[1], info[2], info[3], info[4] );
            dab1 = new Database( db1 );

            info = getDBInfo( "live" );
            db2 = new Db_conn( info[0], info[1], info[2], info[3], info[4] );
            dab2 = new Database( db2 );

            sql.addAll( dab1.compareTables( dab2.getTables()));
            update_tables.addAll( dab1.tablesDiffs( dab2.getTables()));
            sql.addAll(dab1.updateTables( dab2.getTables(), update_tables ));
            sql.addAll(dab1.updateViews( dab2.getViews()));
        } catch ( SQLException e ) {
        
            System.err.println( e );
        }
    } 
    
     /**
     * DBCompare1 compares two db's to determine if any SQL is needed to make 
     * them the exact same 
     * @author Peter Kaufman
     * @type function
     * @access public
     */
    public static void DBCompare2() { // logic error
        
        try {
            
            String[] info;
            Database dab1 = FileConversion.readFrom(); 
            
            info = getDBInfo( "live" );
            db2 = new Db_conn( info[0], info[1], info[2], info[3], info[4] );
            dab2 = new Database( db2 );

            sql.addAll( dab1.compareTables( dab2.getTables()));
            update_tables.addAll( dab1.tablesDiffs( dab2.getTables()));
            sql.addAll( dab1.updateTables( dab2.getTables(), update_tables )); 
            sql.addAll( dab1.updateViews( dab2.getViews()));  
        } catch( IOException | SQLException e ) {
    
            System.err.println( e );
            e.printStackTrace();
        }
    }    
    /**
     * displaySQL displays the result of the comparison: SQL will only be 
     * displayed if the db's are out of sync
     * @author Peter Kaufman
     * @type function
     * @access public
     */
    public static void displaySQL() {
    
        if ( sql.isEmpty() ) {
        
            System.out.println( "The databases are exactly the same." );
        } else {
            
            System.out.println( "Databases are out of Sync. Run the following SQL:" );
            
            for ( int i = 0; i < sql.size(); i++ ) {
             
                System.out.println( sql.get(i));
            }
        }
    } 
   
    public static boolean takeSnapshot() { 
    
        try {
            
            String[] info;
            info = getDBInfo( "development" );
            db1 = new Db_conn( info[0], info[1], info[2], info[3], info[4] );
            dab1 = new Database( db1 );
            FileConversion.writeTo( dab1 );
            
            return true;
        } catch( Exception e ) {
        
            System.err.println( e );
            e.printStackTrace();
            
            return false;
        }
    }
    public static void main(String[] args) {
        
        int option;
        
        System.out.println( "/*****************************************************\\" );
        System.out.println( "|1-Database compare using 2 database connections      |" );
        System.out.println( "|2-Database compare using 1 database connection       |" );
        System.out.println( "|3-Take database snapshot using 1 database connection |" );
        System.out.println( "\\*****************************************************/" );
        System.out.print( "Enter a value in order to choose the method to use: " );
        
        try {
            
            option = IN.nextInt();
            
            if ( option == 1 ) {
               
                DBCompare1();
                displaySQL();
            } else if ( option == 2 ) {
            
                DBCompare2();
                displaySQL();
            } else {
                if ( takeSnapshot()) {
                
                    System.out.println( "The database snapshot was taken successfully." );
                } else {
                
                    System.out.println( "An error occcurred." );
                }
            }
        } catch ( Exception e ) {
        
            System.err.println( e );
            e.printStackTrace();
        }
        
    }
}