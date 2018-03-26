package gov.usgs.earthquake.nshmp.postgres.source_settings;

import static gov.usgs.earthquake.nshmp.postgres.sources.SourceAttribute.*;
import com.google.common.collect.ImmutableMap;
import gov.usgs.earthquake.nshmp.postgres.sources.SourceAttribute;

/**
 * Place holder class to set source property attributes.
 * 
 * @author Brandon Clayton
 */
public class SourcePropertyAttributes {
  public ImmutableMap<SourceAttribute, String> attributes = ImmutableMap.of();
  
  private SourcePropertyAttributes (ImmutableMap<SourceAttribute, String> attributes) {
    this.attributes = attributes;
  }

  /** Area source type */
  public static class Area { 
    private Area () {}
    
    public static Builder builder () {
      return new Builder();
    }
    
    public static class Builder {
      private ImmutableMap.Builder<SourceAttribute, String> attributes = 
          ImmutableMap.builder();
      
      private Builder () {}
      
      public SourcePropertyAttributes build () {
        validate();
        return new SourcePropertyAttributes(this.attributes.build());
      }
      
      public Builder magDepthMap (String magDepthMap) {
        this.attributes.put(MAG_DEPTH_MAP, magDepthMap);
        return this;
      }
      
      public Builder maxDepth (double maxDepth) {
        this.attributes.put(MAX_DEPTH, Double.toString(maxDepth));
        return this;
      }
      
      public Builder focalMechMap (String focalMechMap) {
        this.attributes.put(FOCAL_MECH_MAP, focalMechMap);
        return this;
      }
      
      public Builder strike (double strike) {
        this.attributes.put(STRIKE, Double.toString(strike));
        return this;
      }
      
      private void validate () {
        ImmutableMap<SourceAttribute, String> attr = this.attributes.build();
        
        if (!attr.containsKey(MAG_DEPTH_MAP) ||
            !attr.containsKey(MAX_DEPTH) ||
            !attr.containsKey(FOCAL_MECH_MAP) ||
            !attr.containsKey(STRIKE)) {
          throw new IllegalStateException("Not all fields are set");
        }
      }   
    }
  }
  
  /** Cluster source type */
  public static class Cluster { 
    private Cluster () {}
    
    public static Builder builder () {
      return new Builder();
    }
    
    public static class Builder {
      private ImmutableMap.Builder<SourceAttribute, String> attributes = ImmutableMap.builder();
      
      private Builder () {}
      
      public SourcePropertyAttributes build () {
        validate();
        return new SourcePropertyAttributes(this.attributes.build());
      }
          
      private void validate () {
        
      }
    }  
  }
  
  public static class Fault {
    private Fault () {}
    
    public static Builder builder () {
      return new Builder();
    }
    
    public static class Builder {
      private ImmutableMap.Builder<SourceAttribute, String> attributes = ImmutableMap.builder();
      
      private Builder () {}
      
      public SourcePropertyAttributes build () {
        return new SourcePropertyAttributes(this.attributes.build());
      }
      
      private void validate () {
        
      }
    }
  }
  
  /** Grid source type */
  public static class Grid {
    private Grid () {}
    
    public static Builder builder () {
      return new Builder();
    }
    
    public static class Builder {
      private ImmutableMap.Builder<SourceAttribute, String> attributes = 
          ImmutableMap.builder();
      
      private Builder () {}
      
      public SourcePropertyAttributes build () {
        validate();
        return new SourcePropertyAttributes(this.attributes.build());
      }
      
      public Builder magDepthMap (String magDepthMap) {
        this.attributes.put(MAG_DEPTH_MAP, magDepthMap);
        return this;
      }
      
      public Builder maxDepth (double maxDepth) {
        this.attributes.put(MAX_DEPTH, Double.toString(maxDepth));
        return this;
      }
      
      public Builder focalMechMap (String focalMechMap) {
        this.attributes.put(FOCAL_MECH_MAP, focalMechMap);
        return this;
      }
      
      public Builder strike (double strike) {
        this.attributes.put(STRIKE, Double.toString(strike));
        return this;
      }
      
      private void validate () {
        ImmutableMap<SourceAttribute, String> attr = this.attributes.build();
        
        if (!attr.containsKey(MAG_DEPTH_MAP) ||
            !attr.containsKey(MAX_DEPTH) ||
            !attr.containsKey(FOCAL_MECH_MAP) ||
            !attr.containsKey(STRIKE)) {
          throw new IllegalStateException("Not all fields are set");
        }
      }   
    }
  }
  
  /** Interface source type */
  public static class Interface {    
    private Interface () {}
    
    static Builder builder () {
      return new Builder();
    }
    
    public static class Builder {
      private ImmutableMap.Builder<SourceAttribute, String> attributes = ImmutableMap.builder();
      
      private Builder () {}
      
      public SourcePropertyAttributes build () {
        validate();
        return new SourcePropertyAttributes(this.attributes.build());
      }
     
      private void validate () {
       
      }
    }
  }
  
  /** Slab source type */
  public static class Slab {
    private Slab () {}
    
    public static Builder builder () {
      return new Builder();
    }
    
    public static class Builder {
      private ImmutableMap.Builder<SourceAttribute, String> attributes = 
          ImmutableMap.builder();
      
      private Builder () {}
      
      public SourcePropertyAttributes build () {
        validate();
        return new SourcePropertyAttributes(this.attributes.build());
      }
      
      public Builder magDepthMap (String magDepthMap) {
        this.attributes.put(MAG_DEPTH_MAP, magDepthMap);
        return this;
      }
      
      public Builder maxDepth (double maxDepth) {
        this.attributes.put(MAX_DEPTH, Double.toString(maxDepth));
        return this;
      }
      
      public Builder focalMechMap (String focalMechMap) {
        this.attributes.put(FOCAL_MECH_MAP, focalMechMap);
        return this;
      }
      
      public Builder strike (double strike) {
        this.attributes.put(STRIKE, Double.toString(strike));
        return this;
      }
      
      private void validate () {
        ImmutableMap<SourceAttribute, String> attr = this.attributes.build();
        
        if (!attr.containsKey(MAG_DEPTH_MAP) ||
            !attr.containsKey(MAX_DEPTH) ||
            !attr.containsKey(FOCAL_MECH_MAP) ||
            !attr.containsKey(STRIKE)) {
          throw new IllegalStateException("Not all fields are set");
        }
      }   
    }
  }
  
  /** System source type */
  public static class System {
    private System () {}
    
    public static Builder builder () {
      return new Builder();
    }
    
    public static class Builder {
      private Builder () {}
      
      void validate () {
        
      }
      
    }
    
  }
}
