package gov.usgs.earthquake.nshmp.postgres;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * PostgreSQL utilities.
 * 
 * @author Brandon Clayton
 */
class Util {

  static PostgreSQL POSTGRES;

  static {
    try {
      Properties props = new Properties();
      PostgreSQL.Builder builder = PostgreSQL.builder();
      InputStream inputStream = Util.class.getResourceAsStream("/config.properties");
      props.load(inputStream);

      POSTGRES = builder.database(props.getProperty(Keys.DATABASE))
          .password(props.getProperty(Keys.PASSWORD))
          .table(props.getProperty(Keys.TABLE))
          .url(props.getProperty(Keys.URL))
          .username(props.getProperty(Keys.USERNAME))
          .build();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  static class Keys {
    /* Fault database query keys */
    static final String BIRD_RATE = "bird_displacement_rate";
    static final String BIRD_RAKE = "bird_rake";
    static final String CALC_WIDTH = "calc_width";
    static final String DIP = "dip";
    static final String FAULT_TRACE = "fault_trace";
    static final String GEO_RATE = "geo_displacement_rate";
    static final String GEO_RAKE = "geo_rake";
    static final String ID = "cfault_or";
    static final String NAME = "name";
    static final String PRIMARY_STATE = "primary_state";
    static final String STATE_ABBREV = "state_abbrev";
    static final String UPPER_DEPTH = "upper_depth";
    static final String WKT_FAULT_TRACE = "ST_AsText(" + FAULT_TRACE + ") as " + FAULT_TRACE;
    static final String ZENG_RATE = "zeng_displacement_rate";
    static final String ZENG_RAKE = "zeng_rake";

    /* GeoJSON property keys */
    static final String DEPTH = "depth";
    static final String RAKE = "rake";
    static final String SLIP_RATE_TREE = "slipRateTree";

    /* PostgreSQL keys */
    static final String DATABASE = "database";
    static final String PASSWORD = "password";
    static final String TABLE = "table";
    static final String URL = "url";
    static final String USERNAME = "username";

    /* Query keys */
    static final String SELECT = "SELECT";
    static final String SELECT_DISTINCT = SELECT + " DISTINCT";
    static final String FROM = "FROM";
    static final String WHERE = "WHERE";
    static final String ORDER_BY = "ORDER BY";
    static final String ASCEND = "ASC";
    static final String DESCEND = "DESC";

  }

}
