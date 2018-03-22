package gov.usgs.earthquake.nshmp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import gov.usgs.earthquake.nshmp.internal.UsRegion;

import static gov.usgs.earthquake.nshmp.SourceAttribute.*;

/**
 * Using {@link ModelParameters}, {@link Postgres}, {@link SourceAttribute},
 *    and {@link XMLExporter}, print out XML files for each U.S. state
 *    in the PostgreSQL database that is specified in {@link Postgres#builder()}
 *    
 * @author Brandon Clayton
 * @see XMLExporter#writeXML
 */
public class FaultExporter {

  public static void main(String[] args)  {
    String configFile = "config.properties";
    Postgres postgres = Postgres.builder()
        .withConfigFile(configFile)
        .build();
   
    try {
      postgres.connect();
      ArrayList<String> distinctStates = getDistinctStates(postgres);
     
      System.out.println("Creating XML for: ");
      for (String stateAbbrev : distinctStates) {
        System.out.println(UsRegion.valueOf(stateAbbrev));
        
        String sql = "select " + getSqlSelectFields() +
            " from " + postgres.table +
            " where " + STATE_ABBREV + "='" + stateAbbrev + "'" +  
            " order by " + NAME + " asc;";
        
        ResultSet result = postgres.query(sql);
        ModelParameters.Builder modelBuilder = ModelParameters.builder();
        
        Settings settings = Settings.westernUS();
        modelBuilder.defaultMfds(settings.defaultMfds)
            .magUncertainties(settings.magUncertainties)
            .ruptureScaling(settings.ruptureScaling)
            .stateAbbrev(stateAbbrev);
        
        while (result.next()) {
          String faultTraceWkt = result.getString(FAULT_TRACE.toString());
          String faultTraceString = wktToCoordinates(faultTraceWkt);
          modelBuilder.addBirdRate(result.getDouble(BIRD_DISPLACEMENT_RATE.toString()))
              .addDepth(result.getDouble(UPPER_DEPTH.toString()))
              .addDip(result.getDouble(DIP.toString()))
              .addFaultTrace(faultTraceString)
              .addGeoRate(result.getDouble(GEO_DISPLACEMENT_RATE.toString()))
              .addName(result.getString(NAME.toString()))
              .addRake(result.getDouble(GEO_RAKE.toString()))
              .addWidth(result.getDouble(CALC_WIDTH.toString()))
              .addZengRate(result.getDouble(ZENG_DISPLACEMENT_RATE.toString()));
        }
        result.close();
        
        XMLExporter.writeXML(modelBuilder.build(), postgres.table);
      }
      
      postgres.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  /**
   * Query the PostgreSQL database using {@link Postgres#query(String)} and
   *    obtain the distinct primart states.
   *    
   * @param postgres
   * @return Array list of primary states.
   * @throws SQLException
   */
  static ArrayList<String> getDistinctStates (Postgres postgres) 
      throws SQLException {
    String sql = "select distinct " + STATE_ABBREV.toString() + 
        " from " + postgres.table + 
        " order by " + STATE_ABBREV.toString() + " asc";
    ResultSet result = postgres.query(sql);
    ArrayList<String> states = new ArrayList<>();
    
    while (result.next()) {
      states.add(result.getString(STATE_ABBREV.toString()));
    }
    
    return states;
  }
  
  /**
   * Return a string for the SQL select method. 
   */
  static String getSqlSelectFields () {
    ArrayList<String> selectFields = new ArrayList<>();
    
    selectFields.add(BIRD_DISPLACEMENT_RATE.toLowerCase());
    selectFields.add(CALC_WIDTH.toLowerCase());
    selectFields.add(DIP.toLowerCase());
    selectFields.add(GEO_RAKE.toLowerCase());
    selectFields.add(GEO_DISPLACEMENT_RATE.toLowerCase());
    selectFields.add(NAME.toLowerCase());
    selectFields.add(PRIMARY_STATE.toLowerCase());
    selectFields.add("ST_AsText(" + FAULT_TRACE.toLowerCase() + ") as " + 
        FAULT_TRACE.toLowerCase());
    selectFields.add(STATE_ABBREV.toLowerCase());
    selectFields.add(UPPER_DEPTH.toLowerCase());
    selectFields.add(ZENG_DISPLACEMENT_RATE.toLowerCase());
     
    String select = selectFields.toString()
        .replace("[", " ")
        .replace("]", " ")
        .trim();
    
    return select;
  }
  
  /**
   * Return a string of the coordinates of the fault trace.
   * @param wkt The well know text.
   * @throws ParseException
   */
  static String wktToCoordinates (String wkt) throws ParseException {
    WKTReader wktReader = new WKTReader();
    Geometry faultTraceGeom = wktReader.read(wkt);
    String faultTraceString = "\n";
    for (Coordinate coord : faultTraceGeom.getCoordinates()) {
      faultTraceString = faultTraceString
          .concat(String.format("%.5f,%.5f,0.00000\n", coord.x, coord.y));
    }
    
    return faultTraceString;
  }
  
}