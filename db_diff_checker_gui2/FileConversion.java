/**
 * FileConversion can convert a Database object into a JSON file and vice versa
 * @author mkyong
 * @class FileConversion
 * @access public
 * @version 10-25-17
 * @since 9-12-17
 * @see https://www.mkyong.com/java/jackson-2-convert-java-object-to-from-json/
 */
package db_diff_checker_gui2;
import java.io.File;
import java.io.IOException;;
import com.fasterxml.jackson.databind.*;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.util.Scanner;
import java.util.ArrayList;
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
                mapper.writeValue( new File( "logs\\dbsnapshot.json" ), obj );
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
                Database obj = mapper.readValue( new File( "logs\\dbsnapshot.json" ), Database.class );

                return obj;
        }

        /**
         * fileExists takes a file path and determines whether it exists or not
         * @author Chris Dail
         * @type function
         * @access public
         * @param file is a String which represents the file path
         * @return true or false depending on whether the file exists or not
         * @see https://stackoverflow.com/questions/1816673/how-do-i-check-if-a-file-exists-in-java
         */
        public static boolean fileExists( String file ) {

                return new File( "logs\\" + file ).isFile();
        }

        /**
         * writeTo takes an ArrayList of Strings and writes them to a file
         * @author Peter Kaufman
         * @type function
         * @access public
         * @param SQL is an ArrayList of SQL statement(s)
         * @param file is a String which represents the name of the file to be written to
         * @throws IOException represents an error while writing SQL statements to a file
         */
        public static void writeTo( ArrayList<String> SQL, String file ) throws IOException {

                PrintWriter out;
                if ( !file.equals( "LastRun.txt" )) {

                        out = new PrintWriter(new FileWriter( new File( "logs\\" + file ), true));
                } else {

                        out = new PrintWriter( new FileWriter( new File( "logs\\" + file )));
                }
                for ( String statement: SQL ) {

                        out.println( statement );
                }
                out.close();
        }

        /**
         * readFrom returns an ArrayList of Strings which were read from a file
         * @author Peter Kaufman
         * @type function
         * @access public
         * @param file is a String which represents the name of the file to be written to
         * @return SQL is an ArrayList of SQL statement(s)
         * @throws IOException represents an error while taking in SQL statements from a file
         */
        public static ArrayList<String> readFrom( String file ) throws IOException {

                Scanner in = new Scanner( new File( "logs\\" + file ));
                ArrayList<String> SQL = new ArrayList<>();
                while( in.hasNextLine()) {

                        SQL.add( in.nextLine());
                }
                in.close();
                return SQL;
        }
}
