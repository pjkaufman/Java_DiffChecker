/**
 * Table resembles a table in MySQL and contains info about the table's columns
 * @author Peter Kaufman
 * @class Table
 * @access public
 * @version 9-10-17
 * @since 9-10-17 
 */
package db_diff_checker;
import java.util.ArrayList;
import java.util.List;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
public class Table {

    private String name, createStatement, charSet, collation, auto_increment;
    private ArrayList<Column> columns = new ArrayList(), order_of_columns = new ArrayList();
    private ArrayList<Index> indices = new ArrayList();
    
    /**
     * Table initializes a table object
     * @author Peter Kaufman
     * @type constructor
     * @access public
     * @param table is a String that is the name of the table
     * @param db is a Db_conn object that allows the table to get the necessary 
     * info to create its columns
     */
    public Table( String table, Db_conn db ) {
    
        this.name = table;
        db.make_conn();
        this.createStatement = db.getTableCreateStatement( this.name ) + ";";
        getColumns( db );
        orderColumns();
        getIndices( db );
        db.kill_conn();
    }
    
    public Table() {
        // defualt constructor - needed for file conversion
    }
    
     /**
     * getCollation returns the collation of the table
     * @author Peter Kaufman
     * @type getter
     * @access public
     * @return collation is the collation of the table
     */
    public String getCollation() {
    
        return this.collation;
    }
    
    /**
     * getCharSet returns the character set of the table
     * @author Peter Kaufman
     * @type getter
     * @access public
     * @return charSet is the character set of the table
     */
    public String getCharSet() {
    
        return this.charSet;
    }
    
    /**
     * getCreateStatement returns the create statement of the table
     * @author Peter Kaufman
     * @type getter
     * @access public
     * @return create is the create statement of the table
     */
    public String getCreateStatement() {
    
        return this.createStatement;
    }
    
    /**
     * getName returns the name of the table
     * @author Peter Kaufman
     * @type getter
     * @access public
     * @return name is the name of the table
     */
    public String getName() {
    
        return this.name;
    }
    
       /**
     * getColumns returns an ArrayList of column objects
     * @author Peter Kaufman
     * @type getter
     * @access public
     * @return columns is an ArrayList of column objects
     */
    public ArrayList<Column> getColumns() {
    
        return this.columns;
    } 
    
    /**
     * getIndices returns an ArrayList of Index objects
     * @author Peter Kaufman
     * @type getter
     * @access public
     * @return indices is an ArrayList of Index objects
     */
    public ArrayList<Index> getIndices() {
    
        return this.indices;
    }
    
    /**
     * equals takes in a Table and compares it to the current one, the result is 
     * SQL to make them the same
     * @author Peter Kaufman
     * @type function
     * @access public
     * @param t1 is a Table object
     * @return sql is an ArrayList of String which represent the SQL needed to 
     * make the tables the same
     */
    public ArrayList<String> equals( Table t1 ) {
    
        ArrayList<String> sql = new ArrayList();
        
        // check to see if the table has any upper level differences
        if ( t1.auto_increment == null && this.auto_increment == null ) {
            // do nothing neither of the tables have auto_increment
        } else if ( !( t1.auto_increment == null | this.auto_increment == null )
                & !t1.auto_increment.equals( this.auto_increment )) {

            sql.add( "ALTER TABLE `" + this.name + "` AUTO_INCREMENT=" + 
                    this.auto_increment + ";" );
        } else if ( this.auto_increment != null & !t1.auto_increment.equals( 
                this.auto_increment )  ) {
        
            sql.add( "ALTER TABLE `" + this.name + "` AUTO_INCREMENT=" + 
                    this.auto_increment + ";" );
        }
        
        if ( !this.charSet.equals( t1.charSet ) | 
                !this.collation.equals( t1.collation )) {
        
            sql.add( "ALTER TABLE `" + this.name + "` CHARACTER SET " + 
                    this.charSet + " COLLATE " + this.collation + ";" );
        }
        // check to make sure the columns are the same
        sql.addAll( checkCols( this.columns, t1.getColumns()));
        // check the indidices of the two tables 
        sql.addAll( checkIndices( this.indices, t1.getIndices()));
        
        return sql;
    }
    
    /**
     * getIndices takes in a Db_conn and returns all the indices in the table
     * @author Peter Kaufman
     * @type function
     * @access private
     * @param db is a Db_conn object which allows all of the table's indices 
     * to be accessed
     */
    private void getIndices( Db_conn db ) {
    
        try {
            
            String query = "SELECT t.`name` AS `Table`, i.`name` AS `Index`, " + 
                "i.`TYPE`, GROUP_CONCAT(f.`name` ORDER BY f.`pos`) AS `Columns` "
                + "FROM information_schema.innodb_sys_tables t JOIN " + 
                "information_schema.innodb_sys_indexes i USING (`table_id`) " + 
                "JOIN information_schema.innodb_sys_fields f USING (`index_id`)" +
                " WHERE t.`name` = '" + db.getDB()+ "/" + this.name + 
                "' AND i.`name` NOT LIKE \"FTS%\" GROUP BY 1,2;";
            
            db.make_conn();
            
            ResultSet set = db.query( query ); 
            while ( set.next() ) {
                
                String index = set.getString( "Index" ), 
                       type = getType(Integer.parseInt( set.getString( "TYPE" )))
                       , column = set.getString( "Columns" );
 
                this.indices.add( new Index( index, type, column ));
            }
            
            db.kill_conn();
            
        } catch( SQLException e ) {
            
            System.err.println(e);
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
        
        String type = "";
        
        if ( num == 3 ) {
            
            type = " PRIMARY KEY ";
        } else if ( num == 3 ) {
            
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
     * getColumns gets the column data and initializes the columns ArrayList of 
     * the table
     * @author Peter Kaufman
     * @type function
     * @access private
     * @param db is a Db_conn object that allows the table to access the 
     * appropriate db
     */
    private void getColumns( Db_conn db) {
    
        try {
            String query = "SELECT DISTINCT\n" +
            "    (CONCAT(a.`TABLE_NAME`, `COLUMN_NAME`)) AS 'distinct',\n" +
            "    a.`TABLE_NAME` AS `table`,\n" +
            "    `COLUMN_NAME` AS `name`,\n" +
            "    `COLUMN_TYPE` AS `type`,\n" +
            "    `COLLATION_NAME` AS `collation`,\n" +
            "    `ChARACTER_SET_NAME` AS `charcter_set`,\n" +
            "    `COLUMN_DEFAULT` AS `default`,\n" +
            "    `EXTRA` AS `extra`,\n" +
            "    `IS_NULLABLE`,\n" +
            "    `AUTO_INCREMENT` AS `auto_increment`\n" +
            "FROM\n" +
            "    information_schema.COLUMNS a\n" +
            "        LEFT JOIN\n" +
            "    INFORMATION_SCHEMA.TABLES b ON a.`TABLE_NAME` = b.`TABLE_NAME`\n" +
            "WHERE\n" +
            "    a.`TABLE_SCHEMA` = \"" + db.getDB() + "\"\n" +
            "    AND a.`TABLE_NAME` = \"" + name + "\"\n" +
            "GROUP BY `distinct`\n" +
            "ORDER BY `collation` DESC;";
            
            db.make_conn();
            ResultSet set = db.query( query );
            // get the info 
            while ( set.next() ) {
                
                String info = "";
                if ( set.isFirst()) {
                    
                    this.charSet = set.getString( "charcter_set" );
                    this.collation = set.getString( "collation" );
                    this.auto_increment = set.getString( "auto_increment" ); 
                }
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
                
                columns.add( new Column( set.getString( "name" ), info ));
            }
            
            db.kill_conn();
        } catch( SQLException e ) {
        
            System.err.println( e );
        }
    } 
   
    /**
     * orderColumns orders the columns from first to last
     * @author Peter Kaufman
     * @type function
     * @access private
     */
    private void orderColumns() {
    
        List<Integer> cols = new ArrayList();
        for ( Column col : columns ) {
            
            cols.add( this.createStatement.indexOf( "`" + col.getName() + "`"));
        }
        
        Collections.sort( cols );
        
        for ( int i = 0; i < cols.size(); i++) {
            for ( Column col : columns ) {
                if ( this.createStatement.indexOf( "`" + col.getName() + "`") == 
                        cols.get( i )) {
                    
                    this.order_of_columns.add( col );
                }
            }
        }
    }
    
    /**
     * checkCols takes two ArrayLists of Column objects and returns SQL to make 
     * the columns the same
     * @author Peter Kaufman
     * @type function
     * @access private
     * @param cols1 is an AarrayList of Column objects
     * @param cols2 is an AarrayList of Column objects
     * @return sql is an ArrayList of Strings which represent the SQL to make 
     * the columns the same
     */
    private ArrayList<String> checkCols( ArrayList<Column> cols1, ArrayList<Column> cols2 ) {
    
        ArrayList<String> sql = new ArrayList();
        
         for ( Column col: cols1 ) {
            if ( !inArray( col.getName(), cols2 )) {
                
                String statement = placeStatement( col.getName());
                sql.add( "ALTER TABLE `" + this.name + "` ADD COLUMN `" + 
                        col.getName() + "` " + col.getDetails() + statement + 
                        ";" );
            } else {
                for ( Column col2: cols2 ){
                    if( col.getName().equals( col2.getName())){ // columns are the same
                        if ( !col.getDetails().equals( col2.getDetails())) { // column details are different
                            
                            sql.add( "ALTER TABLE `" + this.name + 
                                    "` MODIFY COLUMN `" + col.getName() + "` " + 
                                    col.getDetails() + ";" );
                        }
                        
                        break;
                    }
                }
            }
        }
        // check for columns to drop
        for ( Column col: cols2 ) {
            if (!inArray( col.getName(), cols1 )) {
            
                sql.add( "ALTER TABLE `" + this.name + "` DROP COLUMN `" + 
                        col.getName() + "`;" );
            }
        }
        
        return sql;
    }
    
    /**
     * placeStatement takes in the name of a colum and returns the SQL statement
     * for where it should be placed
     * @author Peter Kaufman
     * @type function
     * @access private
     * @param col is a String which represents the name of a column in the table
     * @return statement is a String which represents where the column will be 
     * placed in a table using SQL
     */
    private String placeStatement( String col ) {
    
        String last = "", statement = "";
        for ( Column col1: order_of_columns ) {
            if ( col.equals( col1.getName())) {
                if ( last.equals( "" )) {
                    
                    statement = " FIRST";
                } else {
                
                    statement = " AFTER `" + last + "`";
                }
                
                break;
            } else {
            
                last = col1.getName();
            }
        }
        
        return statement;
    }
    
     /**
     * inArray searches for toFind in check
     * @author Peter Kaufman
     * @type function
     * @access private
     * @param toFind is the String to be searched for in check
     * @param check is an ArrayList of Columns to be searched for toFind
     * @return is either true or false depending on if toFind is in check
     */
    private boolean inArray( String toFind, ArrayList<Column> check ) {
    
        for ( int i = 0; i < check.size(); i++ ) {
            if ( toFind.equals( check.get( i ).getName())) {
                
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * checkIndices takes in two lists of Indices and returns the SQL statements
     * to make them the same
     * @author Peter Kaufman
     * @type function
     * @access private
     * @param dev is an ArrayList of Index objects that are in the dev db
     * @param live is an ArrayList of Index objects that are in the live db
     * @return sql is an ArrayList of Strings which represents the SQL statements
     * to make the indices the same
     */
    private ArrayList<String> checkIndices( ArrayList<Index> dev, ArrayList<Index> live ) {
    
        ArrayList<String> sql = new ArrayList();
        
        // check for missing indices
        for ( Index indices1: dev ) {
            // if the index column is not present and the index name is not present, add the index
            if ( inArrayList( indices1, live ) == 1 ) {
                if ( indices1.getCreateStatement().trim().equals( "PRIMARY KEY" )){
                    
                    sql.add( "ALTER TABLE `" + this.name + "` DROP PRIMARY KEY;" );
                    sql.add( "ALTER TABLE `" + this.name + "` ADD PRIMARY KEY (" + indices1.getColumn() + ");" );
                } else {
                
                    sql.add( "ALTER TABLE `" + this.name + "` DROP INDEX `" + indices1.getName() + "`;" );
                    sql.add( "ALTER TABLE `" + this.name + "` ADD" + indices1.getCreateStatement() + "`" + indices1.getName() + "` (" + indices1.getColumn() + ");" );
                }               
            } else if ( inArrayList( indices1, live ) == -1 ) {   
                if ( indices1.getCreateStatement().trim().equals( "PRIMARY KEY" )){
                    
                    sql.add( "ALTER TABLE `" + this.name + "` ADD PRIMARY KEY (" + indices1.getColumn() + ");" );
                } else {
                
                    sql.add( "ALTER TABLE `" + this.name + "` ADD" + indices1.getCreateStatement() + "`" + indices1.getName() + "` (" + indices1.getColumn() + ");" );
                }
            } else {
            
                // do nothing, they are the exact same
            }
        }
        // check for indices to remove
        for ( Index indices1: live ) {
            // if the index column is not present and the index name is not present, add the index
            if ( inArrayList( indices1, dev ) == -1 ) {
                if ( indices1.getCreateStatement().equals( " PRIMARY KEY " )){
                    
                    sql.add( "ALTER TABLE `" + this.name + "` DROP PRIMARY KEY;" );
                } else {
                
                    sql.add( "ALTER TABLE `" + this.name + "` DROP INDEX `" + indices1.getName() + "`;" );
                }
            } else {
            
                // do nothing, index not needed to be dropped
            }
        }
        
        return sql;
    }
    
    /**
     * inArrayList searches for toFind in check
     * @author Peter Kaufman
     * @type function
     * @access private
     * @param toFind is the Index to be searched for in check
     * @param check is an ArrayList of Indices to be searched for toFind
     * @return is either 0,1, or -1 depending on the action needed to be done
     */
    private int inArrayList( Index toFind, ArrayList<Index> check ) {
    
        for ( int i = 0; i < check.size(); i++ ) {
            if ( toFind.getName().equals( check.get( i ).getName()) &
                toFind.getColumn().equals( check.get( i ).getColumn()) & 
                toFind.getCreateStatement().equals( check.get( i ).getCreateStatement())) {
                
                return 0; // the indices are the exact same
            } else if (( toFind.getName().equals( check.get( i ).getName()) & 
                    !toFind.getColumn().equals( check.get( i ).getColumn()) & 
                    !toFind.getCreateStatement().equals( check.get( i ).getCreateStatement())) 
                    | ( toFind.getName().equals( check.get( i ).getName()) & 
                    ( !toFind.getColumn().equals( check.get( i ).getColumn()) | 
                    !toFind.getCreateStatement().equals( check.get( i ).getCreateStatement())))) {
            
                return 1; // the index has to be dropped and added again
            }
        }
        
        return -1; // the index has to be added: it is not present
    }       
}