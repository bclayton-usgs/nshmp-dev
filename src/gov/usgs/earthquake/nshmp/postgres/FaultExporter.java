package gov.usgs.earthquake.nshmp.postgres;

import static gov.usgs.earthquake.nshmp.postgres.sources.SourceAttribute.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.google.common.collect.ImmutableList;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import gov.usgs.earthquake.nshmp.geo.LocationList;
import gov.usgs.earthquake.nshmp.internal.UsRegion;
import gov.usgs.earthquake.nshmp.postgres.source_settings.DeformationModel;
import gov.usgs.earthquake.nshmp.postgres.source_settings.DeformationModelList;
import gov.usgs.earthquake.nshmp.postgres.source_settings.SourceGeometry.FaultGeometry;
import gov.usgs.earthquake.nshmp.postgres.sources.FaultSource;
import gov.usgs.earthquake.nshmp.postgres.sources.FaultSourceSet;
import gov.usgs.earthquake.nshmp.postgres.sources.FaultXMLExporter;
import gov.usgs.earthquake.nshmp.postgres.sources.SourceAttribute;

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
        String primaryState = UsRegion.valueOf(stateAbbrev).toString();
        System.out.println(primaryState);
        
        String sql = "select " + getSqlSelectFields() +
            " from " + postgres.table +
            " where " + STATE_ABBREV + "='" + stateAbbrev + "'" +  
            " order by " + NAME + " asc;";
        
        ResultSet result = postgres.query(sql);
        ImmutableList.Builder<FaultSource> sourceListBuilder = ImmutableList.builder();
        
        while (result.next()) {
          /* Get locationList from well know text */
          String faultTraceWkt = result.getString(FAULT_TRACE.toString());
          LocationList trace = wktToLocationList(faultTraceWkt);
          
          /* Set fault geometry */
          FaultGeometry geometry = FaultGeometry.builder()
              .depth(result.getDouble(UPPER_DEPTH.toLowerCase()))
              .dip(result.getDouble(DIP.toLowerCase()))
              .rake(result.getDouble(GEO_RAKE.toLowerCase()))
              .trace(trace)
              .width(result.getDouble(CALC_WIDTH.toLowerCase()))
              .build();
          
          double birdRate = result.getDouble(BIRD_DISPLACEMENT_RATE.toLowerCase());
          double geoRate = result.getDouble(GEO_DISPLACEMENT_RATE.toLowerCase());
          double zengRate = result.getDouble(ZENG_DISPLACEMENT_RATE.toLowerCase());
          
          DeformationModelList deformationModels = DeformationModelList.builder()
              .add(DeformationModel.bird(birdRate))
              .add(DeformationModel.geo(geoRate))
              .add(DeformationModel.zeng(zengRate))
              .build();
          
          FaultSource.Builder sourceBuilder = FaultSource.builder();
          sourceBuilder.deformationModels(deformationModels)
              .geometry(geometry)
              .id("?")
              .name(result.getString(NAME.toLowerCase()));
          
          sourceListBuilder.add(sourceBuilder.build());
        }
        result.close();  
        
        Settings settings = Settings.westernUS();
        FaultSourceSet sourceSet = FaultSourceSet.builder()
            .defaultMfds(settings.defaultMfds)
            .magUncertainties(settings.magUncertainties)
            .name(primaryState + " " + FAULTS.toUpperCamelCase())
            .ruptureScalingModels(settings.ruptureScalingModels)
            .sources(sourceListBuilder.build())
            .build();
   
        FaultXMLExporter.writeXML(sourceSet, postgres.table, primaryState);
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
   * Return a {@code LocationList} of the fault trace.
   * @param wkt The well know text.
   * @throws ParseException
   */
  static LocationList wktToLocationList (String wkt) throws ParseException {
    WKTReader wktReader = new WKTReader();
    Geometry faultTraceGeom = wktReader.read(wkt);
    LocationList.Builder trace = LocationList.builder();
    for (Coordinate coord : faultTraceGeom.getCoordinates()) {
        trace.add(coord.y, coord.x, 0.0);
    }
    
    return trace.build();
  }
  
}