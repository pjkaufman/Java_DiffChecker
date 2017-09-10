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
public class Db_Diff_Checker {
 
    private static db_conn db1, db2;
    private static ArrayList<String> sql = new ArrayList();
    private static ArrayList<String> exclude = new ArrayList();
    private static ArrayList<String> dev_tables = new ArrayList();
    private static ArrayList<String> live_tables = new ArrayList();
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
    public static void DBCompare1() {
        
         String[] info;
         info = getDBInfo( "development" );
         db1 = new db_conn( info[0], info[1], info[2], info[3], info[4] );
            
         info = getDBInfo( "live" );
         db2 = new db_conn( info[0], info[1], info[2], info[3], info[4] );
         
        // get developement tables
         db1.make_conn();
         dev_tables = db1.getTables();
         db1.kill_conn();
         // get live tables
         db2.make_conn();
         live_tables = db2.getTables();
         db2.kill_conn();
         
         compareTables( dev_tables, live_tables, "add" );
         compareTables( live_tables, dev_tables, "drop" );
         
         
    }
    
    /**
     * displaySQL displays the result of the comparison: SQL will only be 
     * displayed if the db's are out of sync
     * @author Peter Kaufman
     * @type function
     * @access public
     */
    public static void displaySQL() {
    
        if ( sql.size() == 0 ) {
        
            System.out.println( "The databases are exactly the same." );
        } else {
            
            System.out.println( "Databases are out of Sync. Run the following SQL:" );
            
            for ( int i = 0; i < sql.size(); i++ ) {
             
                System.out.println( sql.get(i));
            }
        }
    }
    /**
     * inArray searches for toFind in check
     * @author Peter Kaufman
     * @type function
     * @access public
     * @param toFind is the String to be searched for in check
     * @param check is an ArrayList of Strings to be searched for toFind
     * @return is either true or false depending on if toFind is in check
     */
    public static boolean inArray( String toFind, ArrayList<String> check ) {
    
        for ( int i = 0; i < check.size(); i++ ) {
            if ( toFind.equals( check.get( i ))) {
                
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * compareTables determines which table(s) is/are only in tables1 and 
     * decides which SQL to use based on type
     * @author Peter Kaufman
     * @type function
     * @access public 
     * @param tables1 is an ArrayList of Strings containing table names
     * @param tables2 is an ArrayList of Strings containing table names
     * @param type is a String that determines whether to add or drop the tables
     * that are found to exist in only in tables1
     */
    public static void compareTables( ArrayList<String> tables1, ArrayList<String> tables2, String type ) {
    
        for ( String table : tables1 ) {
            if( !inArray( table, tables2 )){
                if ( type.equals( "add" )) {
                    // get the create statement
                    db1.make_conn();
                    sql.add( db1.getCreateStatement( table ) + ";" );
                    db1.kill_conn();
                } else {
                    // drop the table
                    sql.add( "DROP TABLE `" + table + "`;" );
                }
                exclude.add( table );
            }
        }
    }
    
    public static void main(String[] args) {
        
        int option;
        
        System.out.println( "/***********************************************\\" );
        System.out.println( "|1-Database compare using 2 database connections|" );
        System.out.println( "|2-Database compare using 1 database connection |" );
        System.out.println( "\\***********************************************/" );
        System.out.print( "Enter a value in order to choose the method to use: " );
        
        try {
            
            option = IN.nextInt();
            
            if ( option == 1 ) {
               
                DBCompare1(); 
            } else {
            
                System.out.println( "Option 2 was selected." );
            }
            
            displaySQL();
        } catch ( Exception e ) {
        
            System.err.println( e );
        }
        
    }
}
