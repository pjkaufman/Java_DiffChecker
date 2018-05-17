/**
 * Table resembles a table in MySQL and contains info about the table's columns
 * @author Peter Kaufman
 * @class Table
 * @access public
 * @version 5-17-18
 * @since 9-10-17
 */
package db_diff_checker_gui2;
import java.util.ArrayList;
import java.util.HashMap;
public class Table extends Schema {

        private String charSet = "", collation = "", auto_increment = "";
        private int count = 0;
        private HashMap<String, Column> columns = new HashMap<>();
        private HashMap<String, Index> indices = new HashMap<>();

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
         * getColumns returns a HashMap of Strings and  column objects
         * @author Peter Kaufman
         * @type getter
         * @access public
         * @return columns is a HashMap of Strings which are the colum names and
         * column objects which represent the table's columns
         */
        public HashMap<String, Column> getColumns() {

                return this.columns;
        }

        /**
         * getIndices returns a HashMap of Strings and Index objects
         * @author Peter Kaufman
         * @type getter
         * @access public
         * @return indices is a HashMap of Strings which represent the index names
         * and Index objects which represent the table's indices
         */
        public HashMap<String, Index> getIndices() {

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

                this.columns.put( col.getName(), col );
        }

        /**
         * addIndex adds an index to the indices ArrayList
         * @author Peter Kaufman
         * @type function
         * @access public
         * @param index is an Index object which is to be added to the index list
         */
        public void addIndex( Index index ) {

                this.indices.put( index.getName(), index );
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

                ArrayList<String> sql = new ArrayList<>();
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
         * dropCols takes two HashMaps of String and Column objects and returns part of a SQL statement that makes
         * the columns the same-- checks for columns to drop
         * @author Peter Kaufman
         * @type function
         * @access private
         * @param cols1 is a HashMap of Strings which represent the column names
         * and Column objects which represent the columns of the current table
         * @param cols2 is a HashMap of Strings which represent the column names
         * and Column objects which represent the columns of a different table
         * @return sql is an String which represent part of a SQL statement that makes
         * the columns the same
         */
        private String dropCols( HashMap<String, Column> cols1, HashMap<String, Column> cols2 ) {

                String sql = "";
                Column col = null;
                // check for columns to drop
                for ( String cName : cols2.keySet()) {

                        col = cols2.get( cName );
                        if ( !cols1.containsKey( cName )) {
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
         * otherCols takes two HashMaps of String and Column objects and returns part of a SQL statement that makes
         * the columns the same-- checks for columns to add and modify
         * @author Peter Kaufman
         * @type function
         * @access private
         * @param cols1 is a HashMap of Strings which represent the column names
         * and Column objects which represent the columns of the current table
         * @param cols2 is a HashMap of Strings which represent the column names
         * and Column objects which represent the columns of a different table
         * @return sql is an String which represent part of a SQL statement that makes
         * the columns the same
         */
        private String otherCols( HashMap<String, Column> cols1, HashMap<String, Column> cols2 ) {

                String sql = "";
                String last = "";
                Column col = null, col2 = null;

                for ( String cName : cols1.keySet()) {

                        col = cols1.get( cName );
                        if ( !cols2.containsKey( cName )) {
                                if ( this.count == 0 ) {

                                        sql += "ADD COLUMN `" + col.getName() + "` " + col.getDetails() + last;
                                } else {

                                        sql += ", \nADD COLUMN `" + col.getName() + "` " + col.getDetails() + last;
                                }

                                this.count++;
                        } else {

                                col2 = cols2.get( cName );
                                if( col.getName().equals( col2.getName())) {         // columns are the same
                                        if ( !col.getDetails().equals( col2.getDetails())) {         // column details are different
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

                        last = " AFTER `" + col.getName() + "`";
                }

                return sql;
        }

        /**
         * dropIndices takes in two HashMaps of Indices and returns part of a SQL statement
         * to make them the same-- checks for indices to drop
         * @author Peter Kaufman
         * @type function
         * @access private
         * @param dev is a HashMap of Strings which are the name of the indices
         * and Index objects which represent that are in the dev db
         * @param live  is a HashMap of Strings which are the name of the indices
         * and Index objects which represent that are in the live db
         * @return sql is a String which represents part of a SQL statement
         * to make them the same
         */
        private String dropIndices( HashMap<String, Index> dev, HashMap<String, Index> live ) {

                String sql = "";
                // check for indices to remove
                for ( String iName: live.keySet()) {
                        // if the index does not exist in the dev database then drop it
                        if ( !dev.containsKey( iName )) {
                                if ( this.count == 0 ) {

                                        sql += "DROP INDEX `" + iName + "`";
                                } else {

                                        sql += ", \nDROP INDEX `" + iName + "`";
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
         * @param dev is a HashMap of Strings which are the name of the indices
         * and Index objects which represent that are in the dev db
         * @param live  is a HashMap of Strings which are the name of the indices
         * and Index objects which represent that are in the live db
         * @return sql is a String which represents part of a SQL statement
         * to make them the same
         */
        private String otherIndices( HashMap<String, Index> dev, HashMap<String, Index> live ) {

                String sql = "";
                Index indices1 = null;
                // check for missing indices
                for ( String iName : dev.keySet()) {
                        // if the index exists in both databases or only in the dev database then add it
                        indices1 = dev.get( iName );
                        if ( live.containsKey( iName )) {
                                if ( indices1.compareTo( live.get( iName )) == 1 ) {
                                        if ( this.count == 0 ) {

                                                sql += "DROP INDEX `" + indices1.getName() + "`";
                                                sql += ", \nADD" + indices1.getCreateStatement() + "`" + indices1.getName() + "` (" + indices1.getColumn() + ")";
                                        } else {

                                                sql += ", \nDROP INDEX `" + indices1.getName() + "`";
                                                sql += ", \nADD" + indices1.getCreateStatement() + "`" + indices1.getName() + "` (" + indices1.getColumn() + ")";
                                        }

                                        this.count++;
                                }
                        } else {
                                if ( this.count == 0 ) {

                                        sql += "ADD" + indices1.getCreateStatement() + "`" + indices1.getName() + "` (" + indices1.getColumn() + ")";
                                } else {

                                        sql += ", \nADD" + indices1.getCreateStatement() + "`" + indices1.getName() + "` (" + indices1.getColumn() + ")";
                                }

                                this.count++;

                        }
                }

                return sql;
        }
}
