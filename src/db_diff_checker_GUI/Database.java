/**
 * Database models a MYSQL database schema
 * @author Peter Kaufman
 * @class Database
 * @access public
 * @version 10-9-17
 * @since 9-18-17
 */
package db_diff_checker_GUI;
import java.util.ArrayList;
public class Database {
    
    private ArrayList<Table> tables = new ArrayList();
    private ArrayList<Views> views = new ArrayList();
    private ArrayList<String> exclude = new ArrayList();
    
    public Database( Db_conn db ) {
    
        // get tables
        db.make_conn();
        this.views = db.getViews();
        this.tables = db.getTableList();
        db.kill_conn();
    }
    
    public Database() {
        // defualt constructor - needed to make file conversion
    }
    
    /**
     * getTables returns an ArrayList of Table objects that are in the db
     * @author Petr Kaufman
     * @type getter
     * @access public
     * @return tables is an ArrayList of Table objects that are in the db
     */
    public ArrayList<Table> getTables() {
    
        return this.tables;
    }
    
    /**
     * getViews returns an ArrayList of Views objects that represents the all 
     * the views in the db
     * @author Peter Kaufman
     * @type getter
     * @access public
     * @return  views
     */
    public ArrayList<Views> getViews() {
    
        return this.views;
    }
    
    /**
     * updateViews takes in a list of views and returns the SQL statements needed
     * to make the two db's views to be th same
     * @author Peter Kaufman
     * @type function
     * @access public
     * @param views1 is an ArrayList of Views objects which represents all of 
     * the views in the live db
     * @return sql is an ArrayList of Strings which represents the SQL statements
     * to get the views in the db's to be the same
     */
    public ArrayList<String> updateViews ( ArrayList<Views> views1) {
    
        ArrayList<String> sql = new ArrayList();
        // drop all views
        for ( Views view1: views1 ) {
        
            sql.add( view1.getDrop() );
        }
        // add all views
        for ( Views view1: this.views ) {
        
            sql.add( view1.getCreateStatement());
        }
        
        return sql;
    }
    /**
     * compareTables determines which table(s) is/are only in tables1 and 
     * decides which SQL to use based on type
     * @author Peter Kaufman
     * @type function
     * @access public 
     * @param tables2 is an ArrayList of Table  objects which represent all tables
     * in the live db containing table names
     * @return sql which is an ArrayList of Strings which represents the SQL 
     * statements to be run to make all the tables in the db's the same
     */
    public ArrayList<String> compareTables( ArrayList<Table> tables2 ) {
    
        ArrayList<String> sql = new ArrayList();
            // get the create statement
            for ( Table table : this.tables ) {
                if( !inArrayList( table.getName(), tables2 )){

                    sql.add( table.getCreateStatement());
                    this.exclude.add( table.getName() );
                }
            }
            // drop the table
            for ( Table table : tables2 ) {
                if( !inArrayList( table.getName(), this.tables )){

                    sql.add( "DROP TABLE `" + table.getName() + "`;" );
                    this.exclude.add( table.getName() );
                }
        }
        
        return sql;
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
    private boolean inArray( String toFind, ArrayList<String> check ) {
        for ( int i = 0; i < check.size(); i++ ) {
            if ( toFind.equals( check.get( i ))) {
                
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * inArrayList searches for toFind in check
     * @author Peter Kaufman
     * @type function
     * @access public
     * @param toFind is the String to be searched for in check
     * @param check is an ArrayList of Strings to be searched for toFind
     * @return is either true or false depending on if toFind is in check
     */
    private boolean inArrayList( String toFind, ArrayList<Table> check ) {
        for ( Table table: check ) {
            if ( toFind.equals( table.getName() )) {
                
                return true;
            }
        }
        
        return false;
    }
     
    /**
     * updateTables takes in two table lists and updates the SQL statements to 
     * make them the same
     * @author Peter Kaufman
     * @type function
     * @access public
     * @param live is an ArrayList of Table objects that represents the names of 
     * each table in the live db
     * @param update_tables is an ArrayList of Strings which represents all the 
     * table differences between the db's
     * @return sql is an ArrayList of Strings which represents the SQL statements
     * to be run to make make the tables the same
     */
    public ArrayList<String> updateTables( ArrayList<Table> live, ArrayList<String> update_tables ) {
       
        ArrayList<String> sql = new ArrayList();
        // find the info that is differnet between the tables
        for ( Table table: this.tables ) {
            if ( !inArray( table.getName(), this.exclude ) & inArray( table.getName(), update_tables )) {
                for ( Table table2: live ) {
                    if ( table.getName().equals( table2.getName())) {
                        sql.addAll( table.equals( table2 ));
                    }
                }
            }
        }
        
        return sql;
    }
    
    /**
     * tablesDiffs updates the list of tables which are not the same in dev 
     * and live
     * @author Peter Kaufman
     * @type function
     * @access public
     * @param tables is an ArrayList of Table objects which are in the live db
     * @return update_tables is an ArrayList which represents the tables that 
     * are to be updated
     */
    public ArrayList<String> tablesDiffs( ArrayList<Table> tables ) {
    
        ArrayList<String> dev_structure = new ArrayList();
        ArrayList<String> live_structure = new ArrayList(), update_tables =new ArrayList(); 
        // get dev db's table structure
        for ( Table table: this.tables ) {
            if ( !inArray( table.getName(), this.exclude )) {
                
                dev_structure.add( table.getCreateStatement() + ";" );
            }
        }
        // get live db's table structures
        for ( Table table: tables ) {
            if ( !inArray( table.getName(), this.exclude )) {
                
                live_structure.add( table.getCreateStatement() + ";" );
            }
        }
        // compare dev and live table structures
        for ( String structure: dev_structure ) {
            if ( !inArray( structure, live_structure)) {
            
                update_tables.add( structure.replace( "CREATE TABLE `", "" )
                        .substring( 0, structure.replace( "CREATE TABLE `", "" )
                                .indexOf( "`" )));
            }
        }
        
        return update_tables;
    }
}