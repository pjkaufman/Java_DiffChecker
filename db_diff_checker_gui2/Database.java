/**
 * Database models a MYSQL database schema
 * @author Peter Kaufman
 * @class Database
 * @access public
 * @version 5-13-18
 * @since 9-18-17
 */
package db_diff_checker_gui2;
import java.util.ArrayList;
import java.util.HashMap;
public class Database {
        // member variables
        private HashMap<String, Table> tables = new HashMap();
        private HashMap<String, String> exclude = new HashMap();
        private ArrayList<Views> views = new ArrayList();
        private ArrayList<String> firstSteps = new ArrayList();

        /*  private ArrayList<Table> tables = new ArrayList();
           private ArrayList<Views> views = new ArrayList();
           private ArrayList<String> exclude = new ArrayList(), firstSteps = new ArrayList();*/
        /**
         * Database initializes a Database object
         * @author Peter Kaufman
         * @type constructor
         * @access public
         * @param db is a Db_conn connection which is used to get db information
         */
        public Database( Db_conn db ) {

                // get tables
                db.make_conn();
                this.views = db.getViews();
                this.tables = db.getTableList();
                db.kill_conn();
                this.firstSteps = db.getFirstSteps();
        }

        public Database() {
                // defualt constructor - needed to make file conversion
        }

        /**
         * getFirstSteps returns the first steps to be taken in order to run the SQL statements
         * @author Peter Kaufman
         * @type getter
         * @access public
         * @return firstSteps is an ArrayList of Strings which represents the
         * first steps to be taken in order to run the SQL statements
         */
        public ArrayList<String> getFirstSteps () {

                checkFirstSteps();
                return this.firstSteps;
        }

        /**
         * getTables returns an ArrayList of Table objects that are in the db
         * @author Petr Kaufman
         * @type getter
         * @access public
         * @return tables is an ArrayList of Table objects that are in the db
         */
        public HashMap<String, Table> getTables() {

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
         * @param tables2 is a HashMap of Strings which are table names and Table
         * objects which represent all tables in the live db containing table names
         * @return sql which is an ArrayList of Strings which represents the SQL
         * statements to be run to make all the tables in the db's the same
         */
        public ArrayList<String> compareTables( HashMap<String, Table> tables2 ) {

                ArrayList<String> sql = new ArrayList();
                // get the create statement
                for ( String tName : this.tables.keySet()) {
                        if ( !tables2.containsKey( tName )) {

                                sql.add( this.tables.get( tName ).getCreateStatement());
                                this.exclude.put( tName, tName );
                        }
                }
                // drop the table
                for ( String tName : tables2.keySet() ) {
                        if ( !this.tables.containsKey( tName ) ) {

                                sql.add( "DROP TABLE `" + tName + "`;" );
                                this.exclude.put( tName, tName );
                        }
                }

                return sql;
        }

        /**
         * updateTables takes in two table lists and updates the SQL statements to
         * make them the same
         * @author Peter Kaufman
         * @type function
         * @access public
         * @param live is a HashMap of Strings which are the names of the tables and
         * Table objects that represents each table in the live db
         * @param update_tables is a HashMap of Strings which represents all the
         * table differences between the db's where the key and the value are the same
         * @return sql is an ArrayList of Strings which represents the SQL statements
         * to be run to make make the tables the same
         */
        public ArrayList<String> updateTables( HashMap<String, Table> live, HashMap<String, String> update_tables ) {

                ArrayList<String> sql = new ArrayList();
                // find the info that is differnet between the tables
                for ( String tName : this.tables.keySet()) {
                        if ( !exclude.containsKey( tName ) && update_tables.containsKey( tName )) {

                                sql.addAll( this.tables.get(tName).equals( live.get( tName )));
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
         * @param tables is a HashMap of Strings which are the table names and
         * Table objects which are in the live db
         * @return update_tables is an HashMap which represents the tables that
         * are to be updated
         */
        public HashMap<String, String> tablesDiffs( HashMap<String, Table> tables ) {

                HashMap<String, String> dev_structure = new HashMap(), live_structure = new HashMap(), update_tables = new HashMap();
                // get dev db's table structure
                String struct = null;
                for ( String tName : this.tables.keySet()) {
                        if ( !this.exclude.containsKey( tName )) {

                                struct = this.tables.get( tName ).getCreateStatement() + ";";
                                dev_structure.put( struct, struct );
                        }
                }
                // get live db's table structures
                for ( String tName: tables.keySet()) {
                        if ( !this.exclude.containsKey( tName )) {

                                struct = tables.get( tName ).getCreateStatement() + ";";
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
         * ArrayList in the exclusion list. If it is, it is removed.
         * @author Peter Kaufman
         * @type function
         * @access private
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
