/**
 * Database models a MYSQL database schema
 * @author Peter Kaufman
 * @version 5-24-18
 * @since 9-18-17
 */
package db_diff_checker_gui2;
import java.util.ArrayList;
import java.util.HashMap;
public class Database {
        // Instance variables
        private HashMap<String, Table> tables = new HashMap<>();
        private HashMap<String, String> exclude = new HashMap<>();
        private ArrayList<Views> views = new ArrayList<>();
        private ArrayList<String> firstSteps = new ArrayList<>();

        /**
         * Database initializes a Database object by using a database connection. the database provided connection is used to
         * initialize a HashMap of tables and views that exist in the database provided and get SQL statements to drop all 
         * Primary Keys and remove all auot_increments in the database provided which will only be used on the live database. 
         * @author Peter Kaufman
         * @param db is a Db_conn connection which is used to get db information
         */
        public Database( Db_conn db ) {

                // get tables and views
                db.makeConn();
                this.views = db.getViews();
                this.tables = db.getTableList();
                db.killConn();
                // get SQL statements to drop all Primary Keys and remove all auot_increments 
                this.firstSteps = db.getFirstSteps();
        }

        /**
         * This is the default constructor for this class which is needed for the file conversion to JSON. 
         */
        public Database() { }

        /**
         * getFirstSteps returns the first steps to be taken in order to run the SQL statements. These SQL 
         * statements are used to drop Primary Keys and remove auto_increments on the database provided. <b>Note: this funntion
         * will return an empty ArrayList if the function is called on the dev database.</b>
         * @author Peter Kaufman
         * @return firstSteps is an ArrayList of Strings which is the first steps to be taken 
         * in order to run the SQL statements
         */
        public ArrayList<String> getFirstSteps () {

                checkFirstSteps();
                return this.firstSteps;
        }

        /**
         * getTables returns an HashMap of tables that are in the database provided. The key is the name of the 
         * table and the value is a Table object.
         * @author Peter Kaufman
         * @return tables is an HashMap with a set of String and Table objects which is all of the tables 
         * in the database provided.
         */
        public HashMap<String, Table> getTables() {

                return this.tables;
        }

        /**
         * getViews returns an ArrayList of Views objects which is the all of the views in the database provided.
         * @return views is an ArrayList of View objects which represent all of the views in the database provided.  
         */
        public ArrayList<Views> getViews() {

                return this.views;
        }

        /**
         * updateViews takes in a list of views and returns the SQL statements needed to make the two databases
         * have the exact same views. <b>Note: all views in the live database will be dropped and all from the
         * dev database will be created.</b>
         * @author Peter Kaufman
         * @param liveViews is an ArrayList of Views objects which is all of the views in the live database.
         * @return sql is an ArrayList of Strings which is the SQL statements to run in order to make the
         * live database have the same views as the dev one. 
         */
        public ArrayList<String> updateViews ( ArrayList<Views> liveViews) {

                ArrayList<String> sql = new ArrayList<>();
                // drop all views
                for ( Views liveView: liveViews ) {

                        sql.add( liveView.getDrop() );
                }
                // add all views
                for ( Views devView: this.views ) {

                        sql.add( devView.getCreateStatement());
                }

                return sql;
        }

        /**
         * compareTables determines which table(s) is/are to be created or dropped.
         * @author Peter Kaufman
         * @param liveTables is a HashMap of String and Table object pairs which represent all tables in the 
         * live database.
         * @return sql is an ArrayList of Strings which is the SQL statements to run in order to
         * remove and/or create tables in the live database.
         */
        public ArrayList<String> compareTables( HashMap<String, Table> liveTables ) {

                ArrayList<String> sql = new ArrayList<>();
                // get the create statement
                for ( String tName : this.tables.keySet()) {
                        if ( !liveTables.containsKey( tName )) {

                                sql.add( this.tables.get( tName ).getCreateStatement());
                                this.exclude.put( tName, tName );
                        }
                }
                // drop the table
                for ( String tName : liveTables.keySet() ) {
                        if ( !this.tables.containsKey( tName ) ) {

                                sql.add( "DROP TABLE `" + tName + "`;" );
                                this.exclude.put( tName, tName );
                        }
                }

                return sql;
        }

        /**
         * updateTables takes in two table lists which are HashMaps, and returns the SQL statements to run in order
         * to make the live database have the same table definitions as the dev one.
         * @author Peter Kaufman
         * @param live is a HashMap of String and Table object pairs which are the names of the tables and
         * table data for each table in the live database.
         * @param update_tables is a HashMap of String and String pairs where the key and the value are the same,
         * which is all the tables which are not the same in the live and dev databases. 
         * @return sql is an ArrayList of Strings which is the SQL statements to run in order to make 
         * the live database have the same table definitions as the dev one.
         */
        public ArrayList<String> updateTables( HashMap<String, Table> live, HashMap<String, String> update_tables ) {

                ArrayList<String> sql = new ArrayList<>();
                // find the info that is differnet between the tables
                for ( String tName : this.tables.keySet()) {
                        if ( !exclude.containsKey( tName ) && update_tables.containsKey( tName )) {

                                sql.addAll( this.tables.get(tName).equals( live.get( tName )));
                        }
                }

                return sql;
        }

        /**
         * tablesDiffs compares the two HashMaps which represent the tables in the live and dev databases, and 
         * returns a HashMap of table names whose structure did not match between the dev and live databases.
         * @author Peter Kaufman
         * @param liveTables is a HashMap of String and Table object pairs which are the table names and table data for all tables in the live database.
         * @return update_tables is an HashMap which is the tables that are to be updated because their
         * structures did not match between the dev and live databases.
         */
        public HashMap<String, String> tablesDiffs( HashMap<String, Table> liveTables ) {

                HashMap<String, String> dev_structure = new HashMap<>(), live_structure = new HashMap<>(), update_tables = new HashMap<>();
                // get dev db's table structure
                String struct = null;
                for ( String tName : this.tables.keySet()) {
                        if ( !this.exclude.containsKey( tName )) {

                                struct = this.tables.get( tName ).getCreateStatement() + ";";
                                dev_structure.put( struct, struct );
                        }
                }
                // get live db's table structures
                for ( String tName: liveTables.keySet()) {
                        if ( !this.exclude.containsKey( tName )) {

                                struct = liveTables.get( tName ).getCreateStatement() + ";";
                                live_structure.put( struct, struct );
                        }
                }
                // compare dev and live table structures
                String temp = null;
                for ( String structure: dev_structure.keySet()) {
                        if ( !live_structure.containsKey( structure )) {

                                temp = structure.replace( "CREATE TABLE `", "" )
                                       .substring( 0, structure.replace( "CREATE TABLE `", "" )
                                                   .indexOf( "`" ));
                                update_tables.put( temp, temp );
                        }
                }

                return update_tables;
        }

        /**
         * checkFirstSteps checks to see if any of the SQL statements in the fistSteps
         * ArrayList are also in the exclusion list. If it is, it is removed.
         * @author Peter Kaufman
         */
        private void checkFirstSteps() {
                for ( String table: exclude.keySet()) {
                        for ( int i = 0; i < firstSteps.size(); i++ ) {
                                if ( firstSteps.get( i ).contains( "ALTER TABLE `" + table + "`" )) {

                                        firstSteps.remove( i );
                                        break;
                                }
                        }
                }
        }
}
