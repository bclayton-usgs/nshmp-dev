package gov.usgs.earthquake.nshmp.postgres;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.google.common.collect.Lists;

import gov.usgs.earthquake.nshmp.internal.Parsing;
import gov.usgs.earthquake.nshmp.internal.Parsing.Delimiter;

/**
 * PostgreSQL utilities.
 * 
 * @author Brandon Clayton
 */
class Util {

  static final List<String> CFAULT_ID_SKIP = Lists.newArrayList(
      "570n",
      "570m",
      "570s",
      "572",
      "572n",
      "572s");

  static Map<String, Double> MAX_MAGNITUDES;

  private static final String M_MAX_FILE = "WUSfixedMv2.csv";

  static {
    try {
      MAX_MAGNITUDES = getMaxMagnitudes();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  static PostgreSQL getPostgres(String table) throws IOException {
    Properties props = new Properties();
    InputStream inputStream = Util.class.getResourceAsStream("/config.properties");
    props.load(inputStream);

    return PostgreSQL.builder()
        .database(props.getProperty(Keys.DATABASE))
        .password(props.getProperty(Keys.PASSWORD))
        .table(table)
        .url(props.getProperty(Keys.URL))
        .username(props.getProperty(Keys.USERNAME))
        .build();
  }

  static Double getSQLDouble(ResultSet result, String key) throws SQLException {
    String value = result.getString(key);

    if (value == null) {
      return null;
    } else {
      return result.getDouble(key);
    }
  }

  static class Keys {
    /* Fault database query keys */
    static final String BIRD_RATE = "bird_displacement_rate";
    static final String CALC_WIDTH = "calc_width";
    static final String CFAULT_ID = "cfault_id";
    static final String DIP = "dip";
    static final String DISPLACEMENT_RATE = "displacement_rate";
    static final String FAULT_TRACE = "fault_trace";
    static final String GEO_RATE = "geo_displacement_rate";
    static final String GEO_RAKE = "geo_rake";
    static final String ID = "id";
    static final String NAME = "name";
    static final String PRIMARY_STATE = "primary_state";
    static final String PROBABILITY_OF_ACTIVITY = "probability_of_activity";
    static final String SLIP_RATE = "slip_rate";
    static final String STATE_ABBREV = "state_abbrev";
    static final String UPPER_DEPTH = "upper_depth";
    static final String WKT_FAULT_TRACE = "ST_AsText(" + FAULT_TRACE + ") as " + FAULT_TRACE;
    static final String ZENG_RATE = "zeng_displacement_rate";

    /* GeoJSON property keys */
    static final String DEPTH = "depth";
    static final String RAKE = "rake";
    static final String RATE_MODELS = "rateModels";
    static final String Q_FAULT_ID = "qFaultId";
    static final String M_MAX = "mMax";

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

  private static Map<String, Double> getMaxMagnitudes() throws IOException {
    String filePath = Util.class.getResource(M_MAX_FILE).getPath();
    Path path = Paths.get(filePath);
    List<String> lines = Files.readAllLines(path);

    List<String> keys = Parsing.splitToList(lines.get(0), Delimiter.COMMA);
    Map<String, Double> maxMagnitudes = new HashMap<>();

    int mMaxIndex = 0;
    int cfaultIdIndex = 0;

    for (int index = 0; index < keys.size(); index++) {
      String key = keys.get(index);

      switch (key) {
        case "cfault_id":
          cfaultIdIndex = index;
          break;
        case "mMax":
          mMaxIndex = index;
          break;
        case "name":
          break;
        default:
          throw new IllegalStateException("Unsupported key: " + key);
      }
    }

    for (String line : lines.subList(1, lines.size())) {
      if (line.startsWith("#") || line.trim().isEmpty()) {
        continue;
      }

      List<String> values = Parsing.splitToList(line, Delimiter.COMMA);
      maxMagnitudes.put(values.get(cfaultIdIndex), Double.parseDouble(values.get(mMaxIndex)));
    }

    return maxMagnitudes;
  }

}
