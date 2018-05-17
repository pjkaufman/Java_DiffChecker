/**
 * Index resembles an index in MySQL and contains index info
 * @author Peter Kaufman
 * @class Index
 * @access public
 * @version 5-17-18
 * @since 9-12-17
 */
package db_diff_checker_gui2;
public class Index extends Schema {

        private String column = "";

        /**
         * Index initializes an Index object
         * @author Peter Kaufman
         * @type constructor
         * @access public
         * @param name is a String which represents the name of the index
         * @param create is a String which represents the create statement of an
         * @param column is a String which represents the column of an index
         */
        public Index( String name, String create, String column ) {

                this.name = name;
                this.createStatement = create;
                this.column = formatCols( column );
        }

        public Index() {
                // defualt constructor - needed for file conversion
        }

        /**
         * getColumn returns the name of the column of the index
         * @author Peter Kaufman
         * @type getter
         * @access public
         * @return col is a String which represents the name of the column of the index
         */
        public String getColumn() {

                return this.column;
        }

        /**
         * formatCols converts a String that represents the column(s) of an index
         * into a usable format for an index, meant especially for a composite index
         * @author Peter Kaufman
         * @type function
         * @access public
         * @param columns is a String which  represents the column(s) of an index
         * @return col a String which is a usable format for an index
         */
        public String formatCols( String columns ) {

                String col = "";
                String[] temp = columns.split(",");

                for ( int i = 0; i < temp.length; i++ ) {
                        if ( i == temp.length - 1 ) {

                                col += "`" + temp[ i ] + "`";
                        } else {

                                col += "`" + temp[ i ] + "`,";
                        }
                }

                return col;
        }

        /**
         * compareTo determines what if anything needs to be done to an index
         * @author Peter Kaufman
         * @type function
         * @access public
         * @param ind is an Index object which has the same name as the current Index object
         * @return is an integer which is either 0, or 1 depending on the action needed to be done
         */
        public int compareTo( Index ind ) {

                if ( this.createStatement.equals( ind.createStatement ) && this.column.equals( ind.column)) {

                        return 0; // the indices are the exact same
                } else {

                        return 1; // the index exists, but needs to be modified
                }
        }
}
