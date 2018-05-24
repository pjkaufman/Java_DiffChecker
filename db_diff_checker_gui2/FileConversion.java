/**
 * FileConversion can convert a Database object into a JSON file and vice versa.
 * @author mkyong and Peter Kaufman
 * @version 5-24-18
 * @since 9-12-17
 * @see <a href="https://www.mkyong.com/java/jackson-2-convert-java-object-to-from-json/">https://www.mkyong.com/java/jackson-2-convert-java-object-to-from-json/</a>
 */
package db_diff_checker_gui2;
import java.io.File;
import java.io.IOException;
import com.fasterxml.jackson.databind.*;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.util.Scanner;
import java.util.ArrayList;
public class FileConversion {

        /**
         * writeTo writes a Database object to a JSON file.
         * @author mkyong
         * @param obj is a Database object which is to be converted to a JSON file.
         * @throws IOException an error occurred while converting the Database object to a JSON file.
         */
        public static void writeTo( Database obj ) throws IOException {

                ObjectMapper mapper = new ObjectMapper();
                //Object to JSON in file
                mapper.writeValue( new File( "logs\\dbsnapshot.json" ), obj );
        }

        /**
         * readFrom converts a JSON file into a Database object.
         * @author mkyong
         * @return database which is the Database object read in from the JSON file.
         * @throws IOException an error in converting the JSON file into a Database object.
         */
        public static Database readFrom() throws IOException {

                ObjectMapper mapper = new ObjectMapper();
                mapper.configure( DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false );
                //JSON from file to Object
                Database database = mapper.readValue( new File( "logs\\dbsnapshot.json" ), Database.class );

                return database;
        }

        /**
         * fileExists takes a file path and determines whether the file exists or not.
         * @author Chris Dail
         * @param file is a String which is the file path.
         * @return is ethor true or false depending on whether the file exists or not.
         * @see <a href="https://stackoverflow.com/questions/1816673/how-do-i-check-if-a-file-exists-in-java">https://stackoverflow.com/questions/1816673/how-do-i-check-if-a-file-exists-in-java</a>
         */
        public static boolean fileExists( String file ) {

                return new File( "logs\\" + file ).isFile();
        }

        /**
         * writeTo takes an ArrayList of Strings and writes them to LastRun.txt.
         * @author Peter Kaufman
         * @param SQL is an ArrayList of SQL statement(s).
         * @throws IOException an error occurred while writing the SQL statements to a LastRun.txt.
         */
        public static void writeTo( ArrayList<String> SQL ) throws IOException {

                PrintWriter out = new PrintWriter( new FileWriter( new File( "logs\\LastRun.txt" )));
                for ( String statement: SQL ) {

                        out.println( statement );
                }
                out.close();
        }

        /**
         * writeTo takes a String and writes it to the specified file.
         * @author Peter Kaufman
         * @param data is a String which is to be written to the specified file.
         * @param file is a String which is the name of the file to be written to.
         * @throws IOException an error occurred while writing the SQL statements to the file.
         */
        public static void writeTo( String data, String file ) throws IOException {

                PrintWriter out = new PrintWriter( new FileWriter( new File( "logs\\" + file ), true ));
                out.println( data );
                out.close();
        }

        /**
         * readFrom returns an ArrayList of Strings which were read from the specified file.
         * @author Peter Kaufman
         * @param file is a String which represents the name of the file to be written to
         * @return data is an ArrayList of data that was stored in the file that was read from.
         * @throws IOException an error occurred while reading in the data from the specified file.
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
