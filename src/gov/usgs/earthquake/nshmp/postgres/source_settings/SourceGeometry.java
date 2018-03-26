package gov.usgs.earthquake.nshmp.postgres.source_settings;

import static gov.usgs.earthquake.nshmp.postgres.sources.SourceAttribute.DEPTH;
import static gov.usgs.earthquake.nshmp.postgres.sources.SourceAttribute.DIP;
import static gov.usgs.earthquake.nshmp.postgres.sources.SourceAttribute.RAKE;
import static gov.usgs.earthquake.nshmp.postgres.sources.SourceAttribute.WIDTH;

import com.google.common.collect.ImmutableMap;

import gov.usgs.earthquake.nshmp.geo.LocationList;
import gov.usgs.earthquake.nshmp.postgres.sources.SourceAttribute;

/**
 * Container for different source geometry types.
 * 
 * @author Brandon Clayton
 */
public class SourceGeometry {

  /**
   * Specific geometry for a fault source type containing:
   *    <ul>
   *      <li> depth - double </li>
   *      <li> dip - double </li>
   *      <li> rake - double </li> 
   *      <li> trace - LocationList </li>
   *      <li> width - double </li>
   *    </ul>
   *  Use {@link FaultGeometry.Builder} (via {@link #builder()}) to set
   *      all attributes.
   */
  public static class FaultGeometry {
    public ImmutableMap<SourceAttribute, String> attributes;
    public LocationList trace;
    
    private FaultGeometry(Builder builder) {
      this.attributes = builder.attributes.build();
      this.trace = builder.trace;
    }
    
    /** Return a {@code FaultGeometry} builder. */
    public static Builder builder() {
      return new Builder();
    }
    
    /**
     * {@code FaultGeometry} builder. All attributes must be set before 
     *    calling {@link #build()}.
     */
    public static class Builder {
      private ImmutableMap.Builder<SourceAttribute, String> attributes = 
          ImmutableMap.builder();
      private LocationList trace;
      
      private Builder() {}
      
      /** Return a new {@code FaultGeometry} instance */
      public FaultGeometry build() {
        validate();
        return new FaultGeometry(this);
      }
      
      /** Set the upper depth in km */
      public Builder depth (double depth) {
        this.attributes.put(DEPTH, Double.toString(depth));
        return this;
      }
      
      /** Set the dip in degrees */
      public Builder dip (double dip) {
        this.attributes.put(DIP, Double.toString(dip));
        return this;
      }
      
      /** Set the rake in degrees */
      public Builder rake (double rake) {
        this.attributes.put(RAKE, Double.toString(rake));
        return this;
      }
      
      /** Set the fault trace */
      public Builder trace (LocationList trace) {
        this.trace = trace;
        return this;
      }
      
      /** Set the fault width in km */
      public Builder width (double width) {
        this.attributes.put(WIDTH, Double.toString(width));
        return this;
      }  
      
      /* Validate all fields are set */
      private void validate() {
        ImmutableMap<SourceAttribute, String> attrs = this.attributes.build();
        
        if (!attrs.containsKey(DEPTH) ||
            !attrs.containsKey(DIP) ||
            !attrs.containsKey(RAKE) ||
            !attrs.containsKey(WIDTH) ||
            !(this.trace.size() > 0)) {
          throw new IllegalStateException("FaultGeomerty: not all fields are set");
        }         
      }  
    }
  }
}