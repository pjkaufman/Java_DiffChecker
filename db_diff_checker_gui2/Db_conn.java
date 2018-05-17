/**
 * Db_conn establishes a connection with a MySQL database based on the password,
 * username, port, host, and database provided.
 * @author Peter Kaufman
 * @class Db_conn
 * @access public
 * @version 5-13-18
 * @since 9-6-17
 */
package db_diff_checker_gui2;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
public class Db_conn {

        private String username = "", password = "", host = "", db = "", conn_string = "", port = "", type ="";
        private Connection con = null;
        private ArrayList<String> firstSteps = new ArrayList<>();

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
         * @param type is a String which is to be either dev or live
         * @throws SQLException which represents an error that occured in making a db
         * connection
         */
        public Db_conn ( String username, String password, String host, String port, String database, String type ) throws SQLException {

                this.type = type;
                this.username = username;
                this.password = password;
                this.host = host;
                this.db = database;
                this.port = port;
                this.conn_string = "jdbc:mysql://" + this.host + ":" +  this.port + "/" + this.db + "?autoReconnect=true&useSSL=false&maxReconnects=150";
                this.testConn();
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
         * getFirstSteps returns the first steps to be taken in order to run the SQL statements
         * @author Peter Kaufman
         * @type getter
         * @access public
         * @return firstSteps is an ArrayList of Strings which represents the
         * first steps to be taken in order to run the SQL statements
         */
        public ArrayList<String> getFirstSteps () {

                return this.firstSteps;
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

                        this.con = DriverManager.getConnection( this.conn_string, this.username, this.password );
                } catch ( SQLException e ) {

                        error( "There was an error connecting to the " + this.db + " database."  );
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
                } catch ( SQLException e ) {

                        error( "There was an error closing  the " + this.db + " database."  );
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
                } catch ( SQLException e ) {

                        error( "There was an error getting the " + table + " table's create statement." );
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
                } catch ( SQLException e ) {

                        error( "There was an error getting the " + view + " view's create statement." );
                }

                return "";
        }

        /**
         * getTableList gets the tables, columns, and indices of the db
         * @author Peter Kaufman
         * @type function
         * @access public
         * @return tables is a HashMap of Strings which are the names of the tables
         * and Tables which contain data about the table
         */
        public HashMap<String, Table> getTableList() {

                HashMap<String, Table> tables2 = new HashMap<>();

                try {
                        String sql = "SELECT DISTINCT\n" +
                                     "    (CONCAT(a.`TABLE_NAME`, `COLUMN_NAME`)) AS `distinct`,\n" +
                                     "    a.`TABLE_NAME` AS `table`,\n" +
                                     "    IFNULL(`CHARACTER_SET_NAME`, '') AS `charSet`,\n" +
                                     "    IFNULL(`COLLATION_NAME`, '') AS `collation`,\n" +
                                     "    `TABLE_TYPE` AS `table_type`,\n" +
                                     "    `COLUMN_NAME` AS `name`,\n" +
                                     "    `ORDINAL_POSITION` AS `pos`,\n" +
                                     "    `COLUMN_TYPE` AS `type`,\n" +
                                     "    `COLUMN_DEFAULT` AS `default`,\n" +
                                     "    `EXTRA` AS `extra`,\n" +
                                     "    `IS_NULLABLE`,\n" +
                                     "    IFNULL(`AUTO_INCREMENT`, '') AS `auto_increment`,\n" +
                                     "    '' AS `index`,\n" +
                                     "    NULL AS `indexType`,\n" +
                                     "    '' AS `columns`\n" +
                                     "FROM\n" +
                                     "    information_schema.COLUMNS a\n" +
                                     "        LEFT JOIN\n" +
                                     "    INFORMATION_SCHEMA.TABLES b ON a.`TABLE_NAME` = b.`TABLE_NAME`\n" +
                                     "WHERE\n" +
                                     "    a.`TABLE_SCHEMA` = '" + this.db + "'\n" +
                                     "        AND `TABLE_TYPE` != 'VIEW' \n" +
                                     "GROUP BY `distinct` \n" +
                                     "UNION ALL\n" +
                                     "SELECT \n" +
                                     "	(CONCAT(SUBSTRING(t.`name`, "+ ( this.db.length() + 2 ) + " ), i.`name`)) AS `distinct`,\n" +
                                     "    SUBSTRING(t.`name`, " + ( this.db.length() + 2 ) + " ) AS `table`,\n" +
                                     "    '' AS `charSet`,\n" +
                                     "    '' AS `collation`,\n" +
                                     "    '' AS `table_type`,\n" +
                                     "	 '' AS `name`,\n"+
                                     "    0 AS `pos`,\n" +
                                     "    '' AS `type`,\n" +
                                     "    '' AS `default`,\n" +
                                     "    '' AS `extra`,\n" +
                                     "    '' AS `IS_NULLABLE`,\n" +
                                     "    '' AS `auto_increment`,\n" +
                                     "    i.`name` AS `Index`,\n" +
                                     "    i.`TYPE` AS `indexType`,\n" +
                                     "    GROUP_CONCAT(f.`name`\n" +
                                     "        ORDER BY f.`pos`) AS `Columns`\n" +
                                     "FROM\n" +
                                     "    information_schema.innodb_sys_tables t\n" +
                                     "        JOIN\n" +
                                     "    information_schema.innodb_sys_indexes i USING (`table_id`)\n" +
                                     "        JOIN\n" +
                                     "    information_schema.innodb_sys_fields f USING (`index_id`)\n" +
                                     "WHERE\n" +
                                     "    t.`name` LIKE \"" + this.db + "/%\"  AND i.`name` NOT LIKE \"FTS%\"\n" +
                                     "GROUP BY 1 , 2\n" +
                                     "ORDER BY `table`, `pos`;";
                        String table = "", info = "", primary = "", firstStep = "", create = "";
                        Table add = null;
                        int count = 0;
                        Statement query = this.con.createStatement();
                        ResultSet set = query.executeQuery( sql );

                        while ( set.next()) {
                                if ( !table.equals( set.getString( "table" ))) {

                                        String temp = "";
                                        table = set.getString( "table" );
                                        create = getTableCreateStatement( table );
                                        if ( this.type.equals( "live" ) && create.contains( "PRIMARY KEY" )) {
                                                // remove auto_increment value statement
                                                if ( create.contains( "AUTO_INCREMENT" )) {
                                                        if ( create.contains( "AUTO_INCREMENT=" )) {

                                                                create = create.substring( 0, create.indexOf( "AUTO_INCREMENT=" )) +
                                                                         create.substring( create.indexOf( "DEFAULT CHARSET" ));
                                                        }
                                                        create = create.replace( "AUTO_INCREMENT", "" ); // remove auto-increment from column
                                                }
                                                // determine how many columns are in the PRIMARY KEY and replace the PRIMARY KEY reference
                                                temp = create.substring( create.indexOf( "PRIMARY KEY"));
                                                create = create.substring( 0, create.indexOf( "PRIMARY KEY")) +
                                                         create.substring( create.indexOf( "PRIMARY KEY")
                                                                           +  temp.indexOf( ")" ) + 2 );

                                                // check to see if the PRIMARY KEY was the last table line inside the create statement
                                                if ( !create.contains( "KEY" )) {

                                                        create = create.substring( 0, create.lastIndexOf( "," )) + "\n" +
                                                                 create.substring( create.lastIndexOf( "," ) + 2 );
                                                }

                                                add = new Table( table, this, create );
                                        } else {

                                                add = new Table( table, this, create );
                                        }

                                        tables2.put( add.getName(), add );
                                        if ( count != 0 && this.type.equals( "live" )) {
                                                if ( !firstStep.contains( "COLUMN" )) {

                                                        firstSteps.add( firstStep + "DROP PRIMARY KEY;" );
                                                } else {


                                                        firstSteps.add( firstStep + ", \nDROP PRIMARY KEY;" );
                                                }
                                        } else if ( count != 0 && this.type.equals( "dev" )) {

                                                firstSteps.add( firstStep + ";" );
                                        }

                                        count = 0;
                                        primary = "";
                                        firstStep = "ALTER TABLE `" + table + "` \n";
                                }
                                if( set.getString( "indexType" ) != null ) {
                                        if ( getType( Integer.parseInt( set.getString( "indexType" ))).equals( " PRIMARY KEY " )) { // do not add to the indices list

                                                primary = set.getString( "columns" );
                                        } else {

                                                add.addIndex( new Index( set.getString( "index"),
                                                                         getType( Integer.parseInt( set.getString( "indexType" ))),
                                                                         set.getString( "columns" )));
                                        }
                                } else {

                                        info += set.getString( "type" );
                                        // determine if the column is nullable
                                        if ( set.getString( "IS_NULLABLE" ).equals( "NO" )) {

                                                info += " NOT NULL ";
                                        } else {

                                                info += " NULL ";
                                        }
                                        // determine the default of the column
                                        if ( !set.getString( "extra" ).equals("auto_increment")) { // makes the sql syntax correct when modifying a column that is AI
                                                info += "DEFAULT ";
                                                if ( set.getString( "default" ) != null &&
                                                     !set.getString( "default" ).equals( "" )) {

                                                        info += "'" + set.getString( "default" ) + "' ";
                                                } else if( set.getString( "default" ) != null &&
                                                           set.getString( "default" ).equals( "" )) {

                                                        info += "'' ";
                                                } else {

                                                        info += "NULL ";
                                                }
                                        }
                                        if ( set.getString( "extra" ).equals( "auto_increment" )) {

                                                info += "AUTO_INCREMENT";
                                                add.setAutoIncrement( set.getString( "auto_increment" ));
                                        }
                                        if ( !set.getString( "collation" ).equals( "" )) {

                                                add.setCollation( set.getString( "collation" ));
                                        }
                                        // law of noncontradiction check
                                        if ( info.contains( "NOT NULL DEFAULT NULL" )) {

                                                info = info.replace( "DEFAULT NULL", "").trim();
                                        }
                                        // the primary key column is an auto_increment column, so remove the auto_increment
                                        if ( primary.contains( set.getString( "name" )) && this.type.equals( "live" )) {
                                                if ( info.contains( "AUTO_INCREMENT" )) {
                                                        info = info.replace( "AUTO_INCREMENT", "" );
                                                        if ( count == 0 ) {

                                                                firstStep += "MODIFY COLUMN `" + set.getString( "name" ) + "` " + info;
                                                        } else {

                                                                firstStep += ", \n MODIFY COLUMN `" + set.getString( "name" ) + "` " + info;
                                                        }
                                                }

                                                count++;
                                        } else if ( primary.contains( set.getString( "name" )) && this.type.equals( "dev" )) {
                                                if ( count == 0 ) {

                                                        Index tempIndex = new Index();
                                                        firstStep += "ADD PRIMARY KEY (" + tempIndex.formatCols( primary ) + ")";

                                                        if ( info.contains( "AUTO_INCREMENT" )) {

                                                                firstStep += ", \nMODIFY COLUMN `" + set.getString( "name" ) + "` " + info.trim();
                                                                firstStep += ", \nAUTO_INCREMENT=" + add.getAI();
                                                                info = info.replace( " AUTO_INCREMENT", "" );
                                                        }
                                                } else {
                                                        if ( info.contains( "AUTO_INCREMENT" )) {

                                                                firstStep += ", \nMODIFY COLUMN `" + set.getString( "name" ) + "` " + info.trim();
                                                                firstStep += ", \nAUTO_INCREMENT=" + add.getAI();
                                                                info = info.replace( " AUTO_INCREMENT", "" );
                                                        }
                                                }

                                                count++;
                                        }

                                        add.addColumn( new Column( set.getString( "name" ), info ));
                                        info = "";
                                }
                        }
                        if ( count != 0 && this.type.equals( "live" )) {
                                if ( !firstStep.contains( "COLUMN" )) {

                                        firstSteps.add( firstStep + "DROP PRIMARY KEY;" );
                                } else {


                                        firstSteps.add( firstStep + ", \nDROP PRIMARY KEY;" );
                                }
                        } else if ( count != 0 && this.type.equals( "dev" )) {

                                firstSteps.add( firstStep + ";" );
                        }

                        return tables2;
                } catch ( SQLException e ) {

                        error( "There was an error getting the " + this.db + " database's table, column, and index details." );
                }

                return tables2;
        }

        /**
         * getViews gets the views of the db
         * @author Peter Kaufman
         * @type function
         * @access public
         * @return tables is an ArrayList of Views which contain the view's properties
         */
        public ArrayList<Views> getViews() {

                ArrayList<Views> views1 = new ArrayList<>();

                try {
                        // sql is from https://geeksww.com/tutorials/database_management_systems/mysql/tips_and_tricks/mysql_query_to_find_all_views_in_a_database.php
                        String sql = "SHOW FULL TABLES IN `" + this.db + "` WHERE TABLE_TYPE LIKE 'VIEW';";
                        Statement query = this.con.createStatement();
                        ResultSet set = query.executeQuery( sql );
                        while (set.next()) {

                                views1.add( new Views( set.getString( "Tables_in_" + this.db ),
                                                       getViewCreateStatement( set.getString( "Tables_in_" + this.db ))));
                        }

                        return views1;
                } catch ( SQLException e ) {

                        error( "There was an error getting the " + this.db + " database's view details." );
                }

                return views1;
        }

        /**
         * runSQL takes an SQL statement and runs it
         * @author Peter Kaufman
         * @type function
         * @access public
         * @param sql is a String which represents an SQL statement
         * @return either true or false depending on whether the SQL runs correctly
         */
        public boolean runSQL ( String sql ) {
                try{

                        this.make_conn();
                        Statement query = this.con.createStatement();
                        query.executeUpdate( sql );
                        this.kill_conn();

                        return true;
                }catch( SQLException e ) {

                        error( "There was an error running " + sql + " on the " + this.db + " database." );

                        return false;
                }

        }

        /**
         * getType takes in a number and returns the name of the type of index
         * @author Peter Kaufman
         * @type function
         * @access private
         * @param num is an integer which is the type of the index
         * @return type is a String which is the name of the type of index
         */
        private String getType( int num ) {

                switch( num ) {
                case 2:
                        return " UNIQUE INDEX ";
                case 3:
                        return " PRIMARY KEY ";
                case 32:
                        return " FULLTEXT INDEX ";
                case 64:
                        return " SPATIAL INDEX ";
                default:
                        return " INDEX ";
                }
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
                err.setSize( 430, 100 );
                err.setVisible( true );
        }

        /**
         * testConn determines if the connection to the db is correct or not
         * @author Peter Kaufman
         * @type function
         * @access private
         * @throws SQLException which represents an error that occurred in making a db
         * connection
         */
        private void testConn() throws SQLException {

                this.con = DriverManager.getConnection( "jdbc:mysql://" + this.host + ":" +  this.port + "/" + this.db + "?autoReconnect=true&useSSL=false&maxReconnects=5", this.username, this.password );
                this.con.close();
        }
}
