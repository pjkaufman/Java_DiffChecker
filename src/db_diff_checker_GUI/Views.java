/**
 * Views resembles an view in MySQL and contains view info
 * @author Peter Kaufman
 * @class Views
 * @access public
 * @version 9-16-17
 * @since 9-15-17 
 */
package db_diff_checker_GUI;
public class Views {
    
    private String createStatement = "", name = "", drop = ""; 
    
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
     * getCreateStatement returns the create statement of the view
     * @author Peter Kaufman
     * @type getter
     * @access public
     * @return createStatement is a String which represents the create statement of the view
     */
    public String getCreateStatement() {
    
        return this.createStatement;
    }
    
    /**
     * getName returns the name of the view
     * @author Peter Kaufman
     * @type getter
     * @access public
     * @return name is a String which represents the name of the view
     */
    public String getName() {
    
        return this.name;
    }
    
    /**
     * getDrop returns the SQL drop statement for the view
     * @author Peter Kaufman
     * @type getter
     * @access public
     * @return name is a String which represents the SQL drop statement for the view
     */
    public String getDrop() {
    
        return this.drop;
    }
}