/**
 * Table resembles a table in MySQL and contains info about the table's columns
 * @author Peter Kaufman
 * @class Table
 * @access public
 * @version 10-21-17
 * @since 9-10-17 
 */
package db_diff_checker_gui2;
import java.util.ArrayList;
public class Table {

    private String name = "", createStatement = "", charSet = "", collation = "", auto_increment = "";
    private int count = 0;
    private ArrayList<Column> columns = new ArrayList();
    private ArrayList<Index> indices = new ArrayList();
    
    /**
     * Table initializes a table object
     * @author Peter Kaufman
     * @type constructor
     * @access public
     * @param table is a String that is the name of the table
     * @param db is a Db_conn object that allows the table to get the necessary
     * @param create is a String which represents the create statement of the table
     * info to create its columns
     */
    public Table( String table, Db_conn db, String create ) {
    
        String temp = create.substring( create.indexOf( "DEFAULT CHARSET=" ) + 16 ) + " ";
        this.name = table;
        this.createStatement = create + ";";
        this.charSet = temp.substring( 0, temp.indexOf( " " ));
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
     * getAI returns the auto_increment count of the table
     * @author Peter Kaufman
     * @type getter
     * @access public
     * @return auto_increment is the auto_increment count of the table
     */
    public String getAI() {
    
        return this.auto_increment;
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
     * setCollation sets the collation of the table
     * @author Peter Kaufman
     * @type setter
     * @access public
     * @param collation is a String which represents the collation of the table
     */
    public void setCollation( String collation ) {
    
        this.collation = collation;
    }
    
    /**
     * setCharSet sets the character set of the table
     * @author Peter Kaufman
     * @type setter
     * @access public
     * @param charSet is a String which represents the character set of the table
     */
    public void setCharSet( String charSet ) {
    
         this.charSet = charSet;
    }
    
    /**
     * setAutoIncrement sets the autoIncrement count of the table
     * @author Peter Kaufman
     * @type setter
     * @access public
     * @param autoIncrement is a String which represents the autoIncrement count of the table
     */
    public void setAutoIncrement( String autoIncrement ) {
    
         this.auto_increment = autoIncrement;
    }
    
    /**
     * addColumn adds a column to the columns ArrayList
     * @author Peter Kaufman
     * @type function
     * @access public
     * @param col is a Column object which is to be added to the column list
     */
    public void addColumn( Column col ) {
    
        this.columns.add( col );
    }
    
    /**
     * addIndex adds an index to the indices ArrayList
     * @author Peter Kaufman
     * @type function
     * @access public
     * @param index is an Index object which is to be added to the index list
     */
    public void addIndex( Index index ) {
    
        this.indices.add( index );
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
        String sql2 = "ALTER TABLE `" + this.name + "`\n";

        if (  !this.charSet.equals( t1.charSet ) | !this.collation.equals( t1.collation )) {
        
            sql2 += "CHARACTER SET " + this.charSet + " COLLATE " + this.collation;
            this.count++;
        }
        
        sql2 += dropIndices( this.indices, t1.getIndices());
        sql2 += otherCols( this.columns, t1.getColumns());
        sql2 += dropCols( this.columns, t1.getColumns());
        sql2 += otherIndices( this.indices, t1.getIndices()) + ";";
        if ( this.count != 0 ) {
            
            sql.add( sql2 );
        }
        
        return sql;
    }
    
    /**
     * dropCols takes two ArrayLists of Column objects and returns part of a SQL statement that makes 
     * the columns the same-- checks for columns to drop
     * @author Peter Kaufman
     * @type function
     * @access private
     * @param cols1 is an AarrayList of Column objects
     * @param cols2 is an AarrayList of Column objects
     * @return sql is an String which represent part of a SQL statement that makes 
     * the columns the same
     */
    private String dropCols( ArrayList<Column> cols1, ArrayList<Column> cols2 ) {
    
        String sql = "";
        // check for columns to drop
        for ( Column col: cols2 ) {
            if (!inArray( col.getName(), cols1 )) {
                if ( this.count == 0 ) {

                    sql += "DROP COLUMN `" + col.getName() + "`"; 
                } else {

                    sql += ", \nDROP COLUMN `" + col.getName() + "`"; 
                }

                this.count++;
            }
        }
        
        return sql;
    }
    
    /**
     * otherCols takes two ArrayLists of Column objects and returns part of a SQL statement that makes 
     * the columns the same-- checks for columns to add and modify
     * @author Peter Kaufman
     * @type function
     * @access private
     * @param cols1 is an AarrayList of Column objects
     * @param cols2 is an AarrayList of Column objects
     * @return sql is an String which represent part of a SQL statement that makes 
     * the columns the same
     */
    private String otherCols( ArrayList<Column> cols1, ArrayList<Column> cols2 ) {
    
        String sql = "";
        String last = "";
        
         for ( Column col: cols1 ) {
            if ( !inArray( col.getName(), cols2 )) {
                if ( this.count == 0 ) {
                        
                    sql += "ADD COLUMN `" + col.getName() + "` " + col.getDetails() + last; 
                } else {

                    sql += ", \nADD COLUMN `" + col.getName() + "` " + col.getDetails() + last; 
                }

                this.count++;
            } else {
                for ( Column col2: cols2 ){
                    if( col.getName().equals( col2.getName())){ // columns are the same
                        if ( !col.getDetails().equals( col2.getDetails())) { // column details are different
                            if ( this.count == 0 ) {
                        
                                sql += "MODIFY COLUMN `" + col.getName() + "` " + col.getDetails(); 
                            } else {

                                sql += ", \nMODIFY COLUMN `" + col.getName() + "` " + col.getDetails(); 
                            }
                            
                            this.count++;
                        }
                        
                        break;
                    }
                }
            }
            
            last = " AFTER `" + col.getName() + "`";
        }
        
        return sql;
    }
    
    /**
     * dropIndices takes in two lists of Indices and returns part of a SQL statement
     * to make them the same-- checks for indices to drop
     * @author Peter Kaufman
     * @type function
     * @access private
     * @param dev is an ArrayList of Index objects that are in the dev db
     * @param live is an ArrayList of Index objects that are in the live db
     * @return sql is a String which represents part of a SQL statement
     * to make them the same
     */
    private String dropIndices( ArrayList<Index> dev, ArrayList<Index> live ) {
    
        String sql = "";
        // check for indices to remove
        for ( Index indices1: live ) {
            // if the index column is not present and the index name is not present, add the index
            if ( inArrayList( indices1, dev ) == -1 ) {
                if ( this.count == 0 ) {
                        
                    sql += "DROP INDEX `" + indices1.getName() + "`"; 
                } else {

                    sql += ", \nDROP INDEX `" + indices1.getName() + "`"; 
                }
                
                this.count++;
            } 
        }
        
        return sql;
    }
    
    /**
     * otherIndices takes in two lists of Indices and returns part of a SQL statement
     * to make them the same-- checks for indices to drop and add or just add
     * @author Peter Kaufman
     * @type function
     * @access private
     * @param dev is an ArrayList of Index objects that are in the dev db
     * @param live is an ArrayList of Index objects that are in the live db
     * @return sql is a String which represents part of a SQL statement
     * to make them the same
     */
    private String otherIndices( ArrayList<Index> dev, ArrayList<Index> live ) {
    
        String sql = "";
        // check for missing indices
        for ( Index indices1: dev ) {
            // if the index column is not present and the index name is not present, add the index
            if ( inArrayList( indices1, live ) == 1 ) {
                if ( this.count == 0 ) {
                        
                    sql += "DROP INDEX `" + indices1.getName() + "`"; 
                    sql += ", \nADD" + indices1.getCreateStatement() + "`" + indices1.getName() + "` (" + indices1.getColumn() + ")";
                } else {

                    sql += ", \nDROP INDEX `" + indices1.getName() + "`";
                    sql += ", \nADD" + indices1.getCreateStatement() + "`" + indices1.getName() + "` (" + indices1.getColumn() + ")";
                }
                
                this.count++;
            } else if ( inArrayList( indices1, live ) == -1 ) {   
                if ( this.count == 0 ) {
                        
                    sql += "ADD" + indices1.getCreateStatement() + "`" + indices1.getName() + "` (" + indices1.getColumn() + ")";
                } else {

                    sql += ", \nADD" + indices1.getCreateStatement() + "`" + indices1.getName() + "` (" + indices1.getColumn() + ")";
                }
                
                this.count++;
            } else {
            
                // do nothing, they are the exact same
            }
        }
        
        return sql;
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
     * inArrayList searches for toFind in check
     * @author Peter Kaufman
     * @type function
     * @access private
     * @param toFind is the Index to be searched for in check
     * @param check is an ArrayList of Indices to be searched for toFind
     * @return is either 0,1, or -1 depending on the action needed to be done
     */
    private int inArrayList( Index toFind, ArrayList<Index> check ) {
    
        for( Index toCheck: check ) {
            if ( toFind.getName().equals( toCheck.getName()) &
                toFind.getColumn().equals( toCheck.getColumn()) & 
                toFind.getCreateStatement().equals( toCheck.getCreateStatement())) {
                
                return 0; // the indices are the exact same
            } else if (( toFind.getName().equals( toCheck.getName()) & 
                    !toFind.getColumn().equals( toCheck.getColumn()) & 
                    !toFind.getCreateStatement().equals( toCheck.getCreateStatement())) 
                    | ( toFind.getName().equals( toCheck.getName()) & 
                    ( !toFind.getColumn().equals( toCheck.getColumn()) | 
                    !toFind.getCreateStatement().equals( toCheck.getCreateStatement())))) {
            
                return 1; // the index has to be dropped and added again
            }
        }
        
        return -1; // the index has to be added: it is not present
    }       
}