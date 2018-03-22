package gov.usgs.earthquake.nshmp;

import com.google.common.collect.ImmutableList;

import gov.usgs.earthquake.nshmp.eq.fault.surface.RuptureScaling;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * Fault model parameter property container for a primary U.S. state,
 *    used as an input to {@link XMLExporter}.
 * Use {@code Builder} for new instance.
 * 
 * @author Brandon Clayton
 */
public class ModelParameters {
  ImmutableList<Double> birdRate = ImmutableList.of();
  ImmutableList<Double> depth = ImmutableList.of();
  ImmutableList<DefaultMfd> defaultMfds = ImmutableList.of();
  ImmutableList<Double> dip = ImmutableList.of();
  ImmutableList<String> faultTrace = ImmutableList.of();
  ImmutableList<Double> geoRate = ImmutableList.of();
  ImmutableList<MagUncertainty> magUncertainties = ImmutableList.of();
  ImmutableList<String> name = ImmutableList.of();
  ImmutableList<Double> rake = ImmutableList.of();
  ImmutableList<Double> width = ImmutableList.of();
  ImmutableList<Double> zengRate = ImmutableList.of();
  RuptureScaling ruptureScaling;
  final String stateAbbrev;
  
  private ModelParameters (Builder builder) {
    this.birdRate = builder.birdRate.build();
    this.depth = builder.depth.build();
    this.defaultMfds = builder.defaultMfds;
    this.dip = builder.dip.build();
    this.faultTrace = builder.faultTrace.build();
    this.geoRate = builder.geoRate.build();
    this.magUncertainties = builder.magUncertainties;
    this.name = builder.name.build();
    this.rake = builder.rake.build();
    this.ruptureScaling = builder.ruptureScaling;
    this.stateAbbrev = builder.stateAbbrev;
    this.width = builder.width.build();
    this.zengRate = builder.zengRate.build();
  }
  
  /** Returns the {@code ModelParameters} builder. */
  static Builder builder () {
    return new Builder();
  }
  
  /**
   * ModelParameters builder. 
   * Each field must be set explicitly using the appropriate builder method.
   * The builder methods simply adds a value to an array list 
   *    instead of bringing the entire array list in as a parameter.
   */
  static class Builder {
    private ImmutableList.Builder<Double> birdRate = ImmutableList.builder(); 
    private ImmutableList.Builder<Double> depth = ImmutableList.builder(); 
    private ImmutableList.Builder<Double> dip = ImmutableList.builder(); 
    private ImmutableList.Builder<String> faultTrace = ImmutableList.builder(); 
    private ImmutableList.Builder<Double> geoRate = ImmutableList.builder(); 
    private ImmutableList.Builder<String> name = ImmutableList.builder(); 
    private ImmutableList.Builder<Double> rake = ImmutableList.builder(); 
    private ImmutableList.Builder<Double> width = ImmutableList.builder(); 
    private ImmutableList.Builder<Double> zengRate = ImmutableList.builder(); 
    
    private ImmutableList<DefaultMfd> defaultMfds;
    private ImmutableList<MagUncertainty> magUncertainties;
    
    private RuptureScaling ruptureScaling;
    
    private String stateAbbrev;
    
    private Builder () {}
    
    /** Return {@code ModelParameters} instance */
    ModelParameters build () {
      validate();
      return new ModelParameters(this);
    }
    
    /** Add to the bird displacement rate array list */
    Builder addBirdRate (double birdRate) {
      this.birdRate.add(birdRate);
      return this;
    }
    
    /** Add to the depth array list */
    Builder addDepth (double depth) {
      this.depth.add(depth);
      return this;
    }
    
    /** Add to the dip array list */
    Builder addDip (double dip) {
      this.dip.add(dip);
      return this;
    }
    
    /** Add to the fault trace array list */
    Builder addFaultTrace (String faultTrace) {
      this.faultTrace.add(faultTrace);
      return this;
    }
    
    /** Add to the geo displacement rate array list */
    Builder addGeoRate (double geoRate) {
      this.geoRate.add(geoRate);
      return this;
    }
    
    /** Add to the name array list */
    Builder addName (String name) {
      this.name.add(name);
      return this;
    }
    
    /** Add to the rake array list */
    Builder addRake (double rake) {
      this.rake.add(rake);
      return this;
    }
    
    /** Add to the width array list */
    Builder addWidth (double width) {
      this.width.add(width);
      return this;
    }
    
    /** Add to the Zeng displacement rate array list */
    Builder addZengRate (double zengRate) {
      this.zengRate.add(zengRate);
      return this;
    }
    
    /** Set the default MFDs */
    Builder defaultMfds (ImmutableList<DefaultMfd> defaultMfds) {
      this.defaultMfds = defaultMfds;
      return this;
    }
    
    /** Set the mag uncertainties */
    Builder magUncertainties (ImmutableList<MagUncertainty> mags) {
      this.magUncertainties = mags;
      return this;
    }
    
    Builder ruptureScaling (RuptureScaling ruptureScaling) {
      this.ruptureScaling = ruptureScaling;
      return this;
    }
    
    /** Set the primary US state abbreviation */
    Builder stateAbbrev (String stateAbbrev) {
      this.stateAbbrev = stateAbbrev;
      return this;
    }
    
    /** Make sure all fields are set */
    void validate () {
      if (this.birdRate.build().isEmpty() ||
          this.defaultMfds.isEmpty() || 
          this.depth.build().isEmpty() ||
          this.dip.build().isEmpty() ||
          this.faultTrace.build().isEmpty() ||
          this.geoRate.build().isEmpty() ||
          this.magUncertainties.isEmpty() ||
          this.name.build().isEmpty() ||
          this.rake.build().isEmpty() ||
          isNullOrEmpty(this.ruptureScaling.toString()) ||
          isNullOrEmpty(this.stateAbbrev) ||
          this.width.build().isEmpty() ||
          this.zengRate.build().isEmpty()) {
        throw new IllegalStateException("Not all fields are set");
      }     
    } 
  }
  
}
