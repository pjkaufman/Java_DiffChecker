/**
 * FileConversion can converts a Database object into a JSON file and vice versa 
 * @author mkyong
 * @class FileConversion
 * @access public
 * @version 10-9-17
 * @since 9-12-17 
 * @see https://www.mkyong.com/java/jackson-2-convert-java-object-to-from-json/
 */
package db_diff_checker;
import java.io.File;
import java.io.IOException;;
import com.fasterxml.jackson.databind.*;
public class FileConversion {
    
    /**
     * writeTo writes to a Database object to a JSON file
     * @author mkyong
     * @type function
     * @access public
     * @param obj is a Database object which is to be converted to a JSON file
     * @throws IOException which represents an error in converting the Database
     * object to JSON file
     */
    public static void writeTo( Database obj ) throws IOException {
    
        ObjectMapper mapper = new ObjectMapper();

        //Object to JSON in file
        mapper.writeValue( new File( "dbsnapshot.json" ), obj );
    }
    
    /**
     * readFrom converts a JSON file to a Database object
     * @author mkyong
     * @type function
     * @access public
     * @throws IOException which represents an error in converting the JSON file
     * to a Database object
     * @return obj which is a Database object
     */
    public static Database readFrom() throws IOException {
    
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure( DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false );
        
        //JSON from file to Object
        Database obj = mapper.readValue( new File( "dbsnapshot.json" ), Database.class );

        return obj;
    }
}
