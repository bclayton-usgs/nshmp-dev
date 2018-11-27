package gov.usgs.earthquake.nshmp.postgres;

import static gov.usgs.earthquake.nshmp.postgres.Util.POSTGRES;
import static gov.usgs.earthquake.nshmp.postgres.Util.Keys.BIRD_RAKE;
import static gov.usgs.earthquake.nshmp.postgres.Util.Keys.BIRD_RATE;
import static gov.usgs.earthquake.nshmp.postgres.Util.Keys.CALC_WIDTH;
import static gov.usgs.earthquake.nshmp.postgres.Util.Keys.DEPTH;
import static gov.usgs.earthquake.nshmp.postgres.Util.Keys.DIP;
import static gov.usgs.earthquake.nshmp.postgres.Util.Keys.FAULT_TRACE;
import static gov.usgs.earthquake.nshmp.postgres.Util.Keys.GEO_RAKE;
import static gov.usgs.earthquake.nshmp.postgres.Util.Keys.GEO_RATE;
import static gov.usgs.earthquake.nshmp.postgres.Util.Keys.ID;
import static gov.usgs.earthquake.nshmp.postgres.Util.Keys.NAME;
import static gov.usgs.earthquake.nshmp.postgres.Util.Keys.PRIMARY_STATE;
import static gov.usgs.earthquake.nshmp.postgres.Util.Keys.RAKE;
import static gov.usgs.earthquake.nshmp.postgres.Util.Keys.SLIP_RATES;
import static gov.usgs.earthquake.nshmp.postgres.Util.Keys.STATE_ABBREV;
import static gov.usgs.earthquake.nshmp.postgres.Util.Keys.TITLE;
import static gov.usgs.earthquake.nshmp.postgres.Util.Keys.UPPER_DEPTH;
import static gov.usgs.earthquake.nshmp.postgres.Util.Keys.WIDTH;
import static gov.usgs.earthquake.nshmp.postgres.Util.Keys.WKT_FAULT_TRACE;
import static gov.usgs.earthquake.nshmp.postgres.Util.Keys.ZENG_RAKE;
import static gov.usgs.earthquake.nshmp.postgres.Util.Keys.ZENG_RATE;

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

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import gov.usgs.earthquake.nshmp.geo.LocationList;
import gov.usgs.earthquake.nshmp.geo.json.Feature;
import gov.usgs.earthquake.nshmp.geo.json.GeoJson;
import gov.usgs.earthquake.nshmp.geo.json.Properties;
import gov.usgs.earthquake.nshmp.internal.UsRegion;

/**
 * Query a PostgreSQL fault database and write a GeoJSON file for each U.S.
 * state.
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
   * Query the PostgreSQL database and write a GeoJSON file for each U.S. state.
   * 
   * @param postgres PostgreSQL to query
   * @param out Output path for GeoJSON files
   */
  static void export(PostgreSQL postgres, Path out) {
    try {
      postgres.connect();
      Set<String> states = getDistinctStates(postgres);

      System.out.println("Creating GeoJSON for: ");

      for (String stateAbbrev : states) {
        String state = UsRegion.valueOf(stateAbbrev).toString();
        System.out.println(state);

        ResultSet result = queryFault(postgres, stateAbbrev);
        GeoJson.Builder geojson = resultToFeatureCollection(result);
        result.close();
        geojson.write(out.resolve(state + ".geojson"));
      }

      postgres.close();
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
    selectFields.add(BIRD_RAKE);
    selectFields.add(CALC_WIDTH);
    selectFields.add(DIP);
    selectFields.add(GEO_RATE);
    selectFields.add(GEO_RAKE);
    selectFields.add(ID);
    selectFields.add(NAME);
    selectFields.add(PRIMARY_STATE);
    selectFields.add(STATE_ABBREV);
    selectFields.add(UPPER_DEPTH);
    selectFields.add(WKT_FAULT_TRACE);
    selectFields.add(ZENG_RATE);
    selectFields.add(ZENG_RAKE);

    return selectFields.stream().collect(Collectors.joining(","));
  }

  /* Convert the query result to a feature collection */
  private static GeoJson.Builder resultToFeatureCollection(ResultSet result)
      throws SQLException, ParseException {

    GeoJson.Builder builder = GeoJson.builder();

    while (result.next()) {
      builder.add(resultToFeature(result));
    }

    return builder;
  }

  /* Convert the query to a feature */
  private static Feature resultToFeature(ResultSet result) throws ParseException, SQLException {
    String faultTraceWkt = result.getString(FAULT_TRACE);
    LocationList trace = wktToLocationList(faultTraceWkt);

    Map<String, Object> properties = Properties.builder()
        .put(TITLE, result.getString(NAME))
        .put(DEPTH, result.getDouble(UPPER_DEPTH))
        .put(DIP, result.getDouble(DIP))
        .put(RAKE, result.getDouble(GEO_RAKE))
        .put(WIDTH, result.getDouble(CALC_WIDTH))
        .put(SLIP_RATES, getSlipRates(result))
        .build();

    return Feature.polygon(trace)
        .id(result.getInt(ID))
        .properties(properties)
        .build();
  }

  /* Create the slip rates */
  private static List<SlipRate> getSlipRates(ResultSet result) throws SQLException {
    return SlipRate.builder()
        .bird(result.getDouble(BIRD_RAKE), result.getDouble(BIRD_RATE))
        .geo(result.getDouble(GEO_RAKE), result.getDouble(GEO_RATE))
        .zeng(result.getDouble(ZENG_RAKE), result.getDouble(ZENG_RATE))
        .build();
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

}
