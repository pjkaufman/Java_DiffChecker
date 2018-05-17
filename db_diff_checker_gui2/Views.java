/**
 * Views resembles an view in MySQL and contains view info
 * @author Peter Kaufman
 * @class Views
 * @access public
 * @version 5-17-18
 * @since 9-15-17
 */
package db_diff_checker_gui2;
public class Views extends Schema {
        // Instance variables
        private String drop = "";

        /**
         * Views initializes a Views object
         * @param name is a String which represents the name of the view
         * @param create is a String which represents the create statement of the view
         */
        public Views ( String name, String create ) {

                this.createStatement = create + ";";
                this.name = name;
                this.drop = "DROP VIEW `" + name + "`;";
        }

        public Views() {
                // defualt constructor - needed for file conversion
        }

        /**
         * getDrop returns the SQL drop statement for the view
         * @author Peter Kaufman
         * @type getter
         * @access public
         * @return drop is a String which represents the SQL drop statement for the view
         */
        public String getDrop() {

                return this.drop;
        }
}
