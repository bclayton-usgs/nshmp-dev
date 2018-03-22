package gov.usgs.earthquake.nshmp;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * Connect and query a PostgeSQL database.
 * Use {@code Builder} for new instance.
 * 
 * @author Brandon Clayton
 */
public class Postgres {
  final String database;
  final String host;
  final String password;
  final String table;
  final String username;
  
  Connection connection;
  Statement statement;
  
  private Postgres(Builder builder) {
    this.database = builder.database;
    this.host = builder.host;
    this.password = builder.password;
    this.table = builder.table;
    this.username = builder.username;
  }
  
  /**
   * Create a read only connection to the specified PostgreSQL database.
   * 
   * @throws ClassNotFoundException
   * @throws SQLException
   */
  void connect () throws ClassNotFoundException, SQLException {
    System.out.println("Connecting to " + this.database +  " database ...\n");
    Class.forName("org.postgresql.Driver");
    this.connection = DriverManager.getConnection(this.host + this.database, 
        this.username, 
        this.password);
    this.connection.setReadOnly(true);
  }
  
  /**
   * Query the PostgreSQL database with SQL.
   * 
   * @param sql The SQL query statement
   * @return The resulting query
   * @throws SQLException
   */
  ResultSet query (String sql) throws SQLException {
    this.statement = this.connection.createStatement();
    ResultSet result = this.statement.executeQuery(sql);
    
    return result;
  }
  
  /**
   * Close the PostgreSQL database connection.
   * 
   * @throws SQLException
   */
  void close () throws SQLException {
    this.statement.close();
    this.connection.close();
    System.out.println("\nDisconnecting from " + this.database +  " database \n");
  }
  
  /** 
   * Returns the {@code Postgres} builder. 
   * Each field can be set using the appropriate builder 
   *    method or can build with a properties file
   *    using {@link Builder#withConfigFile(String)}.
   * 
   * @see Postgres.Builder
   */
  static Builder builder () {
    return new Builder();
  }
  
  /**
   * Postgres builder.
   */
  static class Builder {
    private String database;
    private String host;
    private String password;
    private String table;
    private String username;
  
    private Builder () {}
    
    /** Return {@code Postgres} instance */
    Postgres build () {
      validate();
      return new Postgres(this);
    }
    
    /** Set the PostgreSQL database */ 
    Builder database (String database) {
      this.database = database;
      return this;
    }
    
    /** Set the PostgreSQL host path */
    Builder host (String host) {
      this.host = host;
      return this;
    }
    
    /** Set the PostgreSQL database password */
    Builder password (String password) {
      this.password = password;
      return this;
    }
    
    /** Set the PostgreSQL table to query */
    Builder table (String table) {
      this.table = table;
      return this;
    }
    
    /** Set the PostgreSQL database username */
    Builder username (String username) {
      this.username = username;
      return this;
    }
    
    /**
     * Read in a properties file.
     * The properties file must be a proper Java properties file and contain:
     *    <ul>
     *      <li> database </li>
     *      <li> host </li>
     *      <li> password </li>
     *      <li> table </li>
     *      <li> username </li>
     *    </ul>
     */
    Builder withConfigFile (String filename) {
      Properties prop = new Properties();
      InputStream inputStream = null;
    
      try {
        inputStream = new FileInputStream(filename);
        prop.load(inputStream);
        
        this.database = prop.getProperty("database");
        this.host = prop.getProperty("host");
        this.password = prop.getProperty("password");
        this.table = prop.getProperty("table");
        this.username = prop.getProperty("username");
      } catch (IOException e) {
        e.printStackTrace();
      }
      
      return this;
    } 
    
    /** Validate all fields are set */
    void validate () {
      if (isNullOrEmpty(this.database) ||
          isNullOrEmpty(this.host) ||
          isNullOrEmpty(this.password) ||
          isNullOrEmpty(this.table) ||
          isNullOrEmpty(this.username)) {
        throw new IllegalStateException("Not all fields are set");
      }
    }
  }

}