package dbdiffchecker;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * DbConn establishes a connection with a MySQL database based on the password,
 * username, port, host, and database provided.
 * Program Name: Database Difference Checker
 * CSCI Course: 325
 * Grade Received: Pass
 * @author Peter Kaufman
 * @version 2-16-19
 * @since 9-6-17
 */
public class SQLiteConn {

  private String db = "";
  private String connString = ""; 
  private String path = ""; 
  private String type = ""; 
  private String firstStep = "";
  private int count = 0;
  private Connection con = null;
  private ArrayList<String> firstSteps = new ArrayList<>();

 /**
   * Initializes a DB_conn object by setting the instance variables and
   * testing the database connection to make sure that the database can be reached. 
   * @author Peter Kaufman
   * @param path The path of the SQLite database.
   * @param database The database in SQLite that the connection is to be established with.
   * @param type Is to either dev or live.
   * @throws SQLException The database could not be connected to using the provided information.
   */
  public SQLiteConn(String path, String database, String type) throws SQLException {


    Connection conn = null;
    this.type = type;
    this.db = database;
    this.path = path;
    this.connString = "jdbc:sqlite:" + this.path + this.db + ".db";
    System.out.println(connString);
    this.testConnection();
  }

    /**
   * Returns the name of the database to connect to.
   * @author Peter Kaufman
   * @return The name of the database to connect to.
   */
  public String getDatabaseName() {

    return this.db;
  }

  /**
   * Determines if the connection to the db is correct or not.
   * @author Peter Kaufman
   * @throws SQLException An error occurred while attempting to connect to the database.
   */
  private void testConnection() throws SQLException {

    this.con = DriverManager.getConnection(this.connString);
    this.con.close();
  }
}
