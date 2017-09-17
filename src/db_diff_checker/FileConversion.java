/**
 *
 * @author mkyong
 * @see https://www.mkyong.com/java/jackson-2-convert-java-object-to-from-json/
 */
package db_diff_checker;
import java.io.File;
import java.io.IOException;;
import com.fasterxml.jackson.databind.*;
public class FileConversion {
    
    public static void writeTo( Database obj) throws IOException {
    
        ObjectMapper mapper = new ObjectMapper();

        //Object to JSON in file
        mapper.writeValue(new File("dbsnapshot.json"), obj);
    }
    
    public static Database readFrom() throws IOException {
    
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
        //JSON from file to Object
        Database obj = mapper.readValue(new File("dbsnapshot.json"), Database.class);

        return obj;
    }
}
