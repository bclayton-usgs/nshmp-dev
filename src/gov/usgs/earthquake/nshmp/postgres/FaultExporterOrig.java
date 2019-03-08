package gov.usgs.earthquake.nshmp.postgres;

import static gov.usgs.earthquake.nshmp.postgres.Util.POSTGRES;
import static gov.usgs.earthquake.nshmp.postgres.Util.Keys.BIRD_RATE;
import static gov.usgs.earthquake.nshmp.postgres.Util.Keys.CFAULT_ID;
import static gov.usgs.earthquake.nshmp.postgres.Util.Keys.DEPTH;
import static gov.usgs.earthquake.nshmp.postgres.Util.Keys.DIP;
import static gov.usgs.earthquake.nshmp.postgres.Util.Keys.FAULT_TRACE;
import static gov.usgs.earthquake.nshmp.postgres.Util.Keys.GEO_RAKE;
import static gov.usgs.earthquake.nshmp.postgres.Util.Keys.GEO_RATE;
import static gov.usgs.earthquake.nshmp.postgres.Util.Keys.ID;
import static gov.usgs.earthquake.nshmp.postgres.Util.Keys.NAME;
import static gov.usgs.earthquake.nshmp.postgres.Util.Keys.PRIMARY_STATE;
import static gov.usgs.earthquake.nshmp.postgres.Util.Keys.PROBABILITY_OF_ACTIVITY;
import static gov.usgs.earthquake.nshmp.postgres.Util.Keys.RATE_MODELS;
import static gov.usgs.earthquake.nshmp.postgres.Util.Keys.STATE_ABBREV;
import static gov.usgs.earthquake.nshmp.postgres.Util.Keys.UPPER_DEPTH;
import static gov.usgs.earthquake.nshmp.postgres.Util.Keys.Q_FAULT_ID;
import static gov.usgs.earthquake.nshmp.postgres.Util.Keys.WKT_FAULT_TRACE;
import static gov.usgs.earthquake.nshmp.postgres.Util.Keys.ZENG_RATE;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.google.common.base.CharMatcher;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import gov.usgs.earthquake.nshmp.geo.LocationList;
import gov.usgs.earthquake.nshmp.geo.json.Feature;
import gov.usgs.earthquake.nshmp.geo.json.GeoJson;
import gov.usgs.earthquake.nshmp.geo.json.Properties;

/**
 * Query a PostgreSQL fault database and write a GeoJSON file for each fault.
 * 
 * <p> To run the main method: Must have a config.properties file in the root
 * source directory with the following fields: 
 *  <ul> 
 *    <li> database: database to query </li>
 *    <li> password: password for database </li>
 *    <li> table: table name in database to query </li>
 *    <li> url: Database url of form jdbc:postgresql:host:port </li>
 *    <li> username: Database username </li>
 * </ul>
 * 
 * @author Brandon Clayton
 */
public class FaultExporter {

  public static void main(String[] args) {
    Path out = Paths.get("output", POSTGRES.table());
    export(POSTGRES, out);
  }

  /**
   * Query the PostgreSQL database and write a GeoJSON file for each fault.
   * 
   * @param postgres PostgreSQL to query
   * @param out Output path for GeoJSON files
   */
  static void export(PostgreSQL postgres, Path out) {

    try {
      postgres.connect();
      Set<String> states = getDistinctStates(postgres);

      System.out.println("Creating GeoJSON files for: ");

      for (String stateAbbrev : states) {
        System.out.println(stateAbbrev);

        Path faultOut = out.resolve(stateAbbrev);
        Files.createDirectories(faultOut);

        ResultSet result = queryFault(postgres, stateAbbrev);
        writeFiles(result, faultOut);

        result.close();
      }

      postgres.close();

      System.out.println("Files located in [" + out.toString() + "]");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /* Create a list of states that are in the database */
  private static Set<String> getDistinctStates(PostgreSQL postgres)
      throws SQLException {

    ResultSet result = PostgreSQL.queryBuilder()
        .selectDistinct(STATE_ABBREV)
        .from(postgres.table())
        .orderByAscend(STATE_ABBREV)
        .query(postgres);

    Set<String> states = new TreeSet<>();

    while (result.next()) {
      states.add(result.getString(STATE_ABBREV));
    }

    return states;
  }

  /* Query fault database */
  private static ResultSet queryFault(PostgreSQL postgres, String stateAbbrev) throws SQLException {
    
    return PostgreSQL.queryBuilder()
        .select(getSQLSelectFields())
        .from(postgres.table())
        .where(STATE_ABBREV + "='" + stateAbbrev + "'")
        .orderByAscend(NAME)
        .query(postgres);
  }

  /* Create string of all fields to query */
  private static String getSQLSelectFields() {
    List<String> selectFields = new ArrayList<>();

    selectFields.add(BIRD_RATE);
    selectFields.add(CFAULT_ID);
    selectFields.add(DIP);
    selectFields.add(GEO_RATE);
    selectFields.add(GEO_RAKE);
    selectFields.add(ID);
    selectFields.add(NAME);
    selectFields.add(PRIMARY_STATE);
    selectFields.add(PROBABILITY_OF_ACTIVITY);
    selectFields.add(STATE_ABBREV);
    selectFields.add(UPPER_DEPTH);
    selectFields.add(WKT_FAULT_TRACE);
    selectFields.add(ZENG_RATE);

    return selectFields.stream().collect(Collectors.joining(","));
  }

  /* Write a GeoJson file for each fault */
  private static void writeFiles(ResultSet result, Path faultOut)
      throws IOException, ParseException, SQLException {

    while (result.next()) {
      GeoJson.Builder geojson = GeoJson.builder();

      Feature feature = resultToFeature(result);
      String fileName = cleanName(feature.properties().getString(NAME));

      geojson.add(feature);
      geojson.write(faultOut.resolve(fileName + ".geojson"));
    }
  }

  /* Convert the query to a feature */
  private static Feature resultToFeature(ResultSet result) throws ParseException, SQLException {
    String faultTraceWkt = result.getString(FAULT_TRACE);
    LocationList trace = wktToLocationList(faultTraceWkt);

    Map<String, Object> properties = Properties.builder()
        .put(NAME, result.getString(NAME))
        .put(DEPTH, result.getDouble(UPPER_DEPTH))
        .put(DIP, result.getDouble(DIP))
        .put(Q_FAULT_ID, result.getString(CFAULT_ID))
        .put(RATE_MODELS, getSlipRates(result))
        .build();

    return Feature.lineString(trace)
        .id(result.getInt(ID))
        .properties(properties)
        .build();
  }

  /* Create the slip rates */
  private static List<RateModel> getSlipRates(ResultSet result) throws SQLException {
    double probOfActivity = result.getDouble(PROBABILITY_OF_ACTIVITY);
    
    if (probOfActivity < 1) {
      return RateModel.builder()
          .aPriori(probOfActivity, result.getDouble(GEO_RAKE))
          .build();
    } else {
      return RateModel.builder()
          .bird(result.getDouble(BIRD_RATE), result.getDouble(GEO_RAKE))
          .geo(result.getDouble(GEO_RATE), result.getDouble(GEO_RAKE))
          .zeng(result.getDouble(ZENG_RATE), result.getDouble(GEO_RAKE))
          .build();
    }
  }

  /* Convert WKT to location list */
  private static LocationList wktToLocationList(String wkt) throws ParseException {
    WKTReader wktReader = new WKTReader();
    Geometry faultTraceGeom = wktReader.read(wkt);
    LocationList.Builder trace = LocationList.builder();

    for (Coordinate coord : faultTraceGeom.getCoordinates()) {
      trace.add(coord.y, coord.x, 0.0);
    }

    return trace.build();
  }

  /* Clean up fault name for file output */
  private static String cleanName(String name) {
    return CharMatcher.whitespace().collapseFrom(name
        .replace("faults", "")
        .replace("fault", "")
        .replace("zone", "")
        .replace("-", " - ")
        .replace("/", " - ")
        .replace(" , ", " - ")
        .replace(", ", " - ")
        .replace(";", " : "),
        ' ').trim();
  }

}
