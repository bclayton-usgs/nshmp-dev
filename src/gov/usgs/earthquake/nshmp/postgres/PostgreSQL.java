package gov.usgs.earthquake.nshmp.postgres;

import static com.google.common.base.Preconditions.checkState;
import static gov.usgs.earthquake.nshmp.postgres.Util.Keys.ASCEND;
import static gov.usgs.earthquake.nshmp.postgres.Util.Keys.DESCEND;
import static gov.usgs.earthquake.nshmp.postgres.Util.Keys.FROM;
import static gov.usgs.earthquake.nshmp.postgres.Util.Keys.ORDER_BY;
import static gov.usgs.earthquake.nshmp.postgres.Util.Keys.SELECT;
import static gov.usgs.earthquake.nshmp.postgres.Util.Keys.SELECT_DISTINCT;
import static gov.usgs.earthquake.nshmp.postgres.Util.Keys.WHERE;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Connect and query a PostgeSQL database.
 * 
 * <p> Use {@code Builder} for new instance.
 * 
 * @author Brandon Clayton
 */
public class PostgreSQL {

  private static Connection connection;
  private static Statement statement;

  private final String database;
  private final String password;
  private final String table;
  private final String url;
  private final String username;

  private PostgreSQL(Builder builder) {
    database = builder.database;
    password = builder.password;
    table = builder.table;
    username = builder.username;
    url = builder.url;
  }

  /** The database table name */
  String table() {
    return table;
  }

  /** The database name */
  String database() {
    return database;
  }

  /**
   * Create a read only connection to the specified PostgreSQL database.
   * 
   * @throws ClassNotFoundException
   * @throws SQLException
   */
  void connect() throws ClassNotFoundException, SQLException {
    System.out.println("Connecting to [" + database + "." + table + "] database ...\n");
    Class.forName("org.postgresql.Driver");

    connection = DriverManager.getConnection(
        url + database,
        username,
        password);

    connection.setReadOnly(true);
  }

  /**
   * Query the PostgreSQL database with SQL.
   * 
   * @param sql The SQL query statement
   * @return The resulting query
   * @throws SQLException
   */
  ResultSet query(String sql) throws SQLException {
    statement = connection.createStatement();
    return statement.executeQuery(sql);
  }

  /**
   * Close the PostgreSQL database connection.
   * 
   * @throws SQLException
   */
  void close() throws SQLException {
    statement.close();
    connection.close();

    System.out.println("\nDisconnecting from [" + database + "." + table + "] database \n");
  }

  /** New PostgreSQL builder */
  static Builder builder() {
    return new Builder();
  }

  /** PostgreSQL builder */
  static class Builder {

    private String database;
    private String password;
    private String table;
    private String url;
    private String username;

    boolean built;

    private Builder() {
      built = false;
    }

    /** Set the PostgreSQL database name. */
    Builder database(String database) {
      this.database = database;
      return this;
    }

    /** Set the PostgreSQL database password. */
    Builder password(String password) {
      this.password = password;
      return this;
    }

    /** Set the PostgreSQL table name to query. */
    Builder table(String table) {
      this.table = table;
      return this;
    }

    /** Set the PostgreSQL url path. */
    Builder url(String url) {
      this.url = url;
      return this;
    }

    /** Set the PostgreSQL database username. */
    Builder username(String username) {
      this.username = username;
      return this;
    }

    /** Return new PostgreSQL. */
    PostgreSQL build() {
      validateState();
      built = true;
      return new PostgreSQL(this);
    }

    private void validateState() {
      checkState(!built);
      checkState(database != null);
      checkState(password != null);
      checkState(table != null);
      checkState(url != null);
      checkState(username != null);
    }

  }

  /** New query builder */
  static QueryBuilder queryBuilder() {
    return new QueryBuilder();
  }

  /** Build a SQL query */
  static class QueryBuilder {

    private StringBuilder query = new StringBuilder();

    private QueryBuilder() {}

    /**
     * Set the SQL SELECT statement.
     * 
     * @param select The select statement
     * @return this builder
     */
    QueryBuilder select(String select) {
      query.append(SELECT + " " + select).append("\n");
      return this;
    }

    /**
     * Set the SQL SELECT DISTINCT statement.
     * 
     * @param select The select statement
     * @return this builder
     */
    QueryBuilder selectDistinct(String select) {
      query.append(SELECT_DISTINCT + " " + select).append("\n");
      return this;
    }

    /**
     * Set the SQL FROM statement.
     * 
     * @param from The from statement
     * @return this builder
     */
    QueryBuilder from(String from) {
      query.append(FROM + " " + from).append("\n");
      return this;
    }

    /**
     * Set the SQL WHERE statement.
     * 
     * @param where The where statement
     * @return this builder
     */
    QueryBuilder where(String where) {
      query.append(WHERE + " " + where).append("\n");
      return this;
    }

    /**
     * Set the SQL ORDER BY ASC statement.
     * 
     * @param orderBy The order by statement
     * @return this builder
     */
    QueryBuilder orderByAscend(String orderBy) {
      query.append(ORDER_BY + " " + orderBy + " " + ASCEND).append("\n");
      return this;
    }

    /**
     * Set the SQL ORDER BY DESC statment.
     * 
     * @param orderBy The order by statement
     * @return this builder
     */
    QueryBuilder orderByDesencd(String orderBy) {
      query.append(ORDER_BY + " " + orderBy + " " + DESCEND).append("\n");
      return this;
    }

    /** Return the SQL string */
    String toSql() {
      return query.deleteCharAt(query.length() - 1).toString() + ";";
    }

    /** Query the PostgreSQL data base */
    ResultSet query(PostgreSQL postgres) throws SQLException {
      return postgres.query(toSql());
    }

  }

}
