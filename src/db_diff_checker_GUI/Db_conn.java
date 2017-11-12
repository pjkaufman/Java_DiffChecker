/**
 * Db_conn establishes a connection with a MySQL database based on the password, 
 * username, port, host, and database provided.
 * @author Peter Kaufman
 * @class Db_conn
 * @access public
 * @version 10-9-17
 * @since 9-6-17
 */
package db_diff_checker_GUI;
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
     * @throws SQLException which represents an error that occured in making a db
     * connection
     */
    Db_conn ( String username, String password, String host, String port, String database ) throws SQLException {
        
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
     * make_conn makes a connection with the desired db
     * @author Juan Nadal
     * @type function
     * @access public
     * @see https://www.youtube.com/watch?v=e3gnhsGqNmI&t=158s        
     */
    public void make_conn() {
        
        try {
            
            this.con = DriverManager.getConnection( this.conn_string , this.username, this.password );  
        } catch ( SQLException e ) {
            
            System.err.println( e );
            e.printStackTrace();
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
            
            System.err.println( e );
            e.printStackTrace();
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
      
            System.err.println( e );
            e.printStackTrace();
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
            
            System.err.println( e );
            e.printStackTrace();
            error( "There was an error getting the " + view + " view's create statement." );
        }
        
        return "";
    }
    
    /**
     * getTableList gets the tables, columns, and indices of the db
     * @author Peter Kaufman
     * @type function
     * @access public
     * @return tables is an ArrayList of Strings which are the names of the tables
     */
    public ArrayList<Table> getTableList() {
            
        ArrayList<Table> tables2 = new ArrayList();
        
        try {
            String sql = "SELECT DISTINCT\n" +
                    "    (CONCAT(a.`TABLE_NAME`, `COLUMN_NAME`)) AS `distinct`,\n" +
                    "    a.`TABLE_NAME` AS `table`,\n" +
                    "    `CHARACTER_SET_NAME` AS `charSet`,\n" +
                    "    `COLLATION_NAME` AS `collation`,\n" +
                    "    `TABLE_TYPE` AS `table_type`,\n" +
                    "    `COLUMN_NAME` AS `name`,\n" +
                    "    `ORDINAL_POSITION` AS `pos`,\n" +
                    "    `COLUMN_TYPE` AS `type`,\n" +
                    "    `COLUMN_DEFAULT` AS `default`,\n" +
                    "    `EXTRA` AS `extra`,\n" +
                    "    `IS_NULLABLE`,\n" +
                    "    `AUTO_INCREMENT` AS `auto_increment`,\n" +
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
                    "	(CONCAT(SUBSTRING(t.`name`, " + ( this.db.length() + 2 ) + " ), i.`name`)) AS `distinct`,\n" +
                    "    SUBSTRING(t.`name`, " + ( this.db.length() + 2 ) + " ) AS `table`,\n" +
                    "    '' AS `charSet`,\n" +
                    "    '' AS `collation`,\n" +
                    "    '' AS `table_type`,\n" +
                    "	 '' AS `name`,\n" +
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
            String table = "", info = "";
            Table add = null;
            Statement query = this.con.createStatement();
            ResultSet set = query.executeQuery( sql );
                    
            while (set.next()) {
                if ( !table.equals( set.getString( "table" ))) {
                    //this.tables.add( set.getString( "table" ));
                    table = set.getString( "table" );
                    add = new Table( table, this, getTableCreateStatement( table ));
                    tables2.add( add );
                }
                
                if( set.getString( "indexType" ) != null) {
                
                    add.addIndex( new Index( set.getString( "index"), 
                            getType( Integer.parseInt( set.getString( "indexType" ))),
                            set.getString( "columns" )));
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

                            info += set.getString( "default" ) + " ";
                        } else if( set.getString( "default" ) != null && 
                                set.getString( "default" ).equals( "" )) {

                            info += "'' ";
                        } else {

                            info += "NULL ";
                        }
                    }

                    if ( set.getString( "extra" ).equals( "auto_increment" )){

                        info += "AUTO_INCREMENT";
                    }

                    if ( set.getString( "auto_increment" ) != null ) {

                        add.setAutoIncrement( set.getString( "auto_increment" ));
                    }

                    if ( set.getString( "charSet" ) != null ) {

                        add.setCharSet( set.getString( "charSet" ));
                    }

                    if ( set.getString( "collation" ) != null ) {

                        add.setCollation( set.getString( "collation" ));
                    }

                    add.addColumn( new Column( set.getString( "name" ), info ));
                    info = "";
                    }
                }
            
            return tables2;
        } catch ( SQLException e ) {
            
            System.err.println( e );
            e.printStackTrace();
            error( "There was an error getting the " + this.db + " database's table, column, and index details." );
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
    public ArrayList<Views> getViews() {
            
        ArrayList<Views> views1 = new ArrayList();
 
        try {
            
           String sql = "SELECT DISTINCT\n" +
            "    (CONCAT(a.`TABLE_NAME`, a.`TABLE_NAME`)) AS 'distinct',\n" +
            "    a.`TABLE_NAME` AS `table`,\n" +
            "    `TABLE_TYPE` AS `table_type`\n" +
            "FROM\n" +
            "    information_schema.COLUMNS a\n" +
            "        LEFT JOIN\n" +
            "    INFORMATION_SCHEMA.TABLES b ON a.`TABLE_NAME` = b.`TABLE_NAME`\n" +
            "WHERE\n" +
            "    a.`TABLE_SCHEMA` = '" + this.db + "'AND\n" +
            "    `TABLE_TYPE` = 'VIEW'\n" +
            "ORDER BY a.`TABLE_NAME`;";
            Statement query = this.con.createStatement();
            ResultSet set = query.executeQuery( sql );
                    
            while (set.next()) {
                
                views1.add( new Views( set.getString( "table" ), 
                        getViewCreateStatement( set.getString( "table" ))));
            }
            
            return views1;
        } catch ( SQLException e ) {
            
            System.err.println( e );
            e.printStackTrace();
            error( "There was an error getting the " + this.db + " database's view details." );
        }
        
        return views1;
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
        
        String type = "";
        
        if ( num == 3 ) {
            
            type = " PRIMARY KEY ";
        } else if ( num == 2 ) { 
            
            type = " UNIQUE INDEX ";
        } else if ( num == 64 ) {
        
            type = " SPATIAL INDEX ";
        } else if ( num == 32 ) {
        
            type = " FULLTEXT INDEX ";
        } else {
            
            type = " INDEX ";
        }
        
        return type;
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
    
            this.con = DriverManager.getConnection( "jdbc:mysql://" + this.host + ":" +  this.port + "/" + this.db + "?autoReconnect=true&useSSL=false&maxReconnects=5" , this.username, this.password );  
            this.con.close();
    }
}