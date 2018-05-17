/**
 * Schema holds common methods and instance variables for Schema subclasses
 * @author Peter Kaufman
 * @class Schema
 * @access public
 * @version 5-17-18
 * @since 5-17-18
 */
package db_diff_checker_gui2;
public class Schema {
        // Defuat instance variables
        protected String name = "", createStatement = "";

        public Schema () {

        }

        /**
         * getName returns the name of the Schema object
         * @author Peter Kaufman
         * @type getter
         * @access protected
         * @return name is a String which represents the name of the Schema object
         */
        protected String getName() {

                return this.name;
        }

        /**
         * getCreateStatement returns the create statement of the Schema object
         * @author Peter Kaufman
         * @type getter
         * @access protected
         * @return create is a String which represents the create statement of the
         * of the Schema object
         */
        protected String getCreateStatement() {

                return this.createStatement;
        }
}
