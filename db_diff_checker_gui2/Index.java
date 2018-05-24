/**
 * Index resembles an index in MySQL and contains index info.
 * @author Peter Kaufman
 * @version 5-24-18
 * @since 9-12-17
 */
package db_diff_checker_gui2;
public class Index extends Schema {

        private String column = "";

        /**
         * Index initializes an Index object by setting the name create statement, and columns of the index.
         * @author Peter Kaufman
         * @param name is a String which is the name of the index.
         * @param create is a String which is the create statement of the index.
         * @param column is a String which is the column of the index.
         */
        public Index( String name, String create, String column ) {

                this.name = name;
                this.createStatement = create;
                this.column = column;
        }

        /**
         * This is the default constructor for this class, which is needed for the file conversion to JSON. 
         */
        public Index() { }

        /**
         * getColumn returns the name of the column or columns of the index. <b>Note: the column name(s) has/have 
         * already been formatted to work in SQL statements.</b>
         * @author Peter Kaufman
         * @return col is a String which is/are the name(s) of the column of the index.
         */
        public String getColumn() {

                return this.column;
        }

        /**
         * compareTo determines what if anything needs to be done to an index. The integer is either 0 or 1.
         * Zero means that the index exists and is the same. One means that the index does exist, but the 
         * definition is not the same. 
         * @author Peter Kaufman
         * @param ind is an Index object which has the same name as the current Index object.
         * @return is an integer which is either 0, or 1 depending on the action needed to be done.
         */
        public int compareTo( Index ind ) {
                if ( this.createStatement.equals( ind.createStatement ) && this.column.equals( ind.column)) {

                        return 0; // the index is the exact same
                } else {

                        return 1; // the index exists, but needs to be modified
                }
        }
}
