/**
 * Column resembles a column in MySQL and contains column info.
 * @author Peter Kaufman
 * @access public
 * @version 5-24-18
 * @since 9-10-17
 */
package db_diff_checker_gui2;
public class Column extends Schema {

        private String details = "";

        /**
         * Column initializes a column object by setting its name and details.
         * @author Peter Kaufman
         * @param name is a String which is the name of the column
         * @param details is a String which is the info of a column
         */
        public Column( String name, String details ) {

                this.name = name;
                this.details = details;
        }

        /**
         * This is the default constructor for this class, which is needed for the file conversion to JSON. 
         */
        public Column() { }

        /**
         * getDetails returns the info about the column.
         * @author Peter Kaufman
         * @return details is a String which is the info about the column
         */
        public String getDetails() {

                return this.details;
        }
}
