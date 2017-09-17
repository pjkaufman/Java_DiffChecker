/**
 * Index resembles an index in MySQL and contains index info
 * @author Peter Kaufman
 * @class Index
 * @access public
 * @version 9-12-17
 * @since 9-12-17 
 */
package db_diff_checker;
public class Index {
    
    private String name, createStatement, column;
    
    /**
     * Index initializes an Index object    
     * @author Peter Kaufman
     * @type constructor
     * @access public
     * @param name is a String which represents the name of the index
     * @param create is a String which represents the create statement of an
     * index
     */
    public Index( String name, String create, String column ) {
    
        this.name = name;
        this.createStatement = create;
        this.column = formatCols( column );
    }
    
    /**
     * getCreateStatement returns the create statement of the index
     * @author Peter Kaufman
     * @type getter
     * @access public
     * @return create is a String which represents the create statement of the 
     * index
     */
    public String getCreateStatement() {
    
        return this.createStatement;
    }
    
    /**
     * getName returns the name of the index
     * @author Peter Kaufman
     * @type getter
     * @access public
     * @return name is a String which represents the name of the index
     */
    public String getName() {
    
        return this.name;
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
}
