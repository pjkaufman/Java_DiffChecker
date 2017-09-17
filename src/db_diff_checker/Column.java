/**
 * Column resembles a column in MySQL and contains column info
 * @author Peter Kaufman
 * @class Column
 * @access public
 * @version 9-10-17
 * @since 9-10-17 
 */
package db_diff_checker;
public class Column {
    
    private String name = "", details = "";
    
    /**
     * Column initializes a column object    
     * @author Peter Kaufman
     * @type constructor
     * @access public
     * @param name is a String which represents the name of the column
     * @param details is a String which represents the info of a column
     */
    public Column( String name, String details ) {
    
        this.name = name;
        this.details = details;
    }
    
    public Column() {
        // defualt constructor - needed for file conversion
    }
    /**
     * getDetails returns the info about the column
     * @author Peter Kaufman
     * @type getter
     * @access public
     * @return details is a String which represents the info about the column
     */
    public String getDetails() {
    
        return this.details;
    }
    
    /**
     * getName returns the name of the column
     * @author Peter Kaufman
     * @type getter
     * @access public
     * @return name is a String which represents the name of the column
     */
    public String getName() {
    
        return this.name;
    }
}