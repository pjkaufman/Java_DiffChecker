/**
 * Views resembles an view in MySQL and contains view info.
 * @author Peter Kaufman
 * @version 5-24-18
 * @since 9-15-17
 */
package db_diff_checker_gui2;
public class Views extends Schema {
        // Instance variables
        private String drop = "";

        /**
         * Views initializes a Views object using a name and create statement.
         * @param name is a String which is the name of the view.
         * @param create is a String which is the create statement of the view.
         */
        public Views ( String name, String create ) {

                this.createStatement = create + ";";
                this.name = name;
                this.drop = "DROP VIEW `" + name + "`;";
        }

        /**
         * This is the default constructor for this class, which is needed for the file conversion to JSON. 
         */
        public Views() { }

        /**
         * getDrop returns the SQL drop statement for the view.
         * @author Peter Kaufman
         * @return drop is a String which is the SQL drop statement for the view.
         */
        public String getDrop() {

                return this.drop;
        }
}
