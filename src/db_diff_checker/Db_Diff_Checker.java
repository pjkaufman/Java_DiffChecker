/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db_diff_checker;

/**
 *
 * @author topse
 */
import java.util.Scanner;
public class Db_Diff_Checker {
 
    /**
     * getDBInfo gets information about the db from the user and returns an array with the results
     * @param type is the type of db to have info collected on
     * @return info is the array containing the db info
     */
    public static String[] getDBInfo( String type ) {
        
        Scanner in = new Scanner( System.in );
        String input;
        
        System.out.print( "Enter the " + type + " database information seperated by a space (username password host port database): " );
        input = in.next();
        input = in.nextLine();
        
        return input.split(" ");
    }
    
    public static void main(String[] args) {
        
        db_conn db1 = null, db2 = null;
        String[] info;
        Scanner in = new Scanner( System.in );
        int option;
        
        System.out.println( "/***********************************************\\" );
        System.out.println( "|1-Database compare using 2 database connections|" );
        System.out.println( "|2-Database compare using 1 database connection |" );
        System.out.println( "\\***********************************************/" );
        System.out.print( "Enter a value in order to choose the method to use: " );
        
        try {
            
            option = in.nextInt();
            
            if ( option == 1 ) {
                
                info = getDBInfo( "development" );
                db1 = new db_conn( info[0], info[1], info[2], info[3], info[4] );
                
                info = getDBInfo( "live" );
                db2 = new db_conn( info[0], info[1], info[2], info[3], info[4] );
            } else {
            
                System.out.println( "Option 2 was selected." );
            }
        } catch ( Exception e ) {
        
            System.err.println( e );
        }
        
    }
    
}
