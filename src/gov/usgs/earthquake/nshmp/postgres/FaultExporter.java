package gov.usgs.earthquake.nshmp.postgres;

import static gov.usgs.earthquake.nshmp.postgres.Util.CFAULT_ID_SKIP;
import static gov.usgs.earthquake.nshmp.postgres.Util.MAX_MAGNITUDES;
import static gov.usgs.earthquake.nshmp.postgres.Util.getSQLDouble;
import static gov.usgs.earthquake.nshmp.postgres.Util.Keys.CFAULT_ID;
import static gov.usgs.earthquake.nshmp.postgres.Util.Keys.DEPTH;
import static gov.usgs.earthquake.nshmp.postgres.Util.Keys.DIP;
import static gov.usgs.earthquake.nshmp.postgres.Util.Keys.FAULT_TRACE;
import static gov.usgs.earthquake.nshmp.postgres.Util.Keys.GEO_RAKE;
import static gov.usgs.earthquake.nshmp.postgres.Util.Keys.ID;
import static gov.usgs.earthquake.nshmp.postgres.Util.Keys.M_MAX;
import static gov.usgs.earthquake.nshmp.postgres.Util.Keys.NAME;
import static gov.usgs.earthquake.nshmp.postgres.Util.Keys.PRIMARY_STATE;
import static gov.usgs.earthquake.nshmp.postgres.Util.Keys.PROBABILITY_OF_ACTIVITY;
import static gov.usgs.earthquake.nshmp.postgres.Util.Keys.Q_FAULT_ID;
import static gov.usgs.earthquake.nshmp.postgres.Util.Keys.RATE_MODELS;
import static gov.usgs.earthquake.nshmp.postgres.Util.Keys.STATE_ABBREV;
import static gov.usgs.earthquake.nshmp.postgres.Util.Keys.UPPER_DEPTH;
import static gov.usgs.earthquake.nshmp.postgres.Util.Keys.WKT_FAULT_TRACE;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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
 * src directory with the following fields: 
 *  <ul> 
 *    <li> database: database to query </li>
 *    <li> password: password for database </li>
 *    <li> url: Database url of form jdbc:postgresql:host:port </li>
 *    <li> username: Database username </li>
 * </ul>
 * 
 * @author Brandon Clayton
 */
public class FaultExporter {
  
  PostgreSQL postgres;
  Path outputPath;
  
  FaultExporter(String table) throws IOException {
    postgres = Util.getPostgres(table);
    outputPath = Paths.get("output", postgres.table());
  }

  /**
   * Query the PostgreSQL database and write a GeoJSON file for each fault.
   * 
   * @param postgres PostgreSQL to query
   * @param out Output path for GeoJSON files
   */
  void export() {

    try {
      postgres.connect();
      Set<String> states = getDistinctStates(postgres);

      System.out.println("Creating GeoJSON files for: ");

      for (String stateAbbrev : states) {
        System.out.println(stateAbbrev);

        Path faultOut = outputPath.resolve(stateAbbrev);
        Files.createDirectories(faultOut);

        ResultSet result = queryFault(postgres, stateAbbrev);
        writeFiles(result, faultOut);

        result.close();
      }

      postgres.close();

      System.out.println("Files located in [" + outputPath.toString() + "]");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /** Returns a list of strings of all fields to query */
  List<String> getSQLSelectFields() {
    List<String> selectFields = new ArrayList<>();

    selectFields.add(CFAULT_ID);
    selectFields.add(DIP);
    selectFields.add(ID);
    selectFields.add(NAME);
    selectFields.add(PRIMARY_STATE);
    selectFields.add(PROBABILITY_OF_ACTIVITY);
    selectFields.add(STATE_ABBREV);
    selectFields.add(UPPER_DEPTH);
    selectFields.add(WKT_FAULT_TRACE);

    return selectFields;
  }

  /**
   * Returns a list of rate models.
   * 
   * @param result The PostgreSQL result set
   * @throws SQLException
   */
  List<RateModel> getRateModels(ResultSet result) throws SQLException {
    return checkProbabilityOfActivity(result, getSQLDouble(result, GEO_RAKE));
  }

  /**
   * Returns a rate model of a priori if POA < 1, else an empty list of rate models.
   * 
   * @param result The PostgreSQL result set
   * @param rake The rake
   * @throws SQLException
   */
  List<RateModel> checkProbabilityOfActivity(ResultSet result, Double rake) throws SQLException {
    Double probOfActivity = getSQLDouble(result, PROBABILITY_OF_ACTIVITY);
    RateModel.Builder rateModel = RateModel.builder();
    
    if (probOfActivity != null && probOfActivity < 1) {
      rateModel.aPriori(probOfActivity, getSQLDouble(result, GEO_RAKE));
    }
    
    return rateModel.build();
  }

  /* Create a list of states that are in the database */
  private Set<String> getDistinctStates(PostgreSQL postgres)
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
  private ResultSet queryFault(PostgreSQL postgres, String stateAbbrev) throws SQLException {
    String selectFields = getSQLSelectFields()
        .stream()
        .collect(Collectors.joining(","));
    
    return PostgreSQL.queryBuilder()
        .select(selectFields)
        .from(postgres.table())
        .where(STATE_ABBREV + "='" + stateAbbrev + "'")
        .orderByAscend(NAME)
        .query(postgres);
  }

  /* Write a GeoJson file for each fault */
  private void writeFiles(ResultSet result, Path faultOut)
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
  private Feature resultToFeature(ResultSet result) throws ParseException, SQLException {
    String faultTraceWkt = result.getString(FAULT_TRACE);
    LocationList trace = wktToLocationList(faultTraceWkt);

    String cfaultId = result.getString(CFAULT_ID);
    
    Properties.Builder builder = Properties.builder()
        .put(NAME, result.getString(NAME))
        .put(DEPTH, getSQLDouble(result, UPPER_DEPTH))
        .put(DIP, getSQLDouble(result, DIP))
        .put(Q_FAULT_ID, cfaultId)
        .put(RATE_MODELS, getRateModels(result));
    
    if (MAX_MAGNITUDES.containsKey(cfaultId) && !CFAULT_ID_SKIP.contains(cfaultId)) {
      builder.put(M_MAX, MAX_MAGNITUDES.get(cfaultId));
    }

    return Feature.lineString(trace)
        .id(result.getInt(ID))
        .properties(builder.build())
        .build();
  }

  /* Convert WKT to location list */
  private LocationList wktToLocationList(String wkt) throws ParseException {
    WKTReader wktReader = new WKTReader();
    Geometry faultTraceGeom = wktReader.read(wkt);
    LocationList.Builder trace = LocationList.builder();

    for (Coordinate coord : faultTraceGeom.getCoordinates()) {
      trace.add(coord.y, coord.x, 0.0);
    }

    return trace.build();
  }

  /* Clean up fault name for file output */
  private String cleanName(String name) {
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
