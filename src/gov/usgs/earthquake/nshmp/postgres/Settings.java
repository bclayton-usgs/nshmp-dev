package gov.usgs.earthquake.nshmp.postgres;

import gov.usgs.earthquake.nshmp.eq.fault.surface.RuptureScaling;
import gov.usgs.earthquake.nshmp.postgres.source_settings.DefaultMfd;
import gov.usgs.earthquake.nshmp.postgres.source_settings.MagUncertainty;
import gov.usgs.earthquake.nshmp.postgres.source_settings.DefaultMfdList;
import gov.usgs.earthquake.nshmp.postgres.source_settings.MagUncertaintyList;
import gov.usgs.earthquake.nshmp.postgres.source_settings.RuptureScalingList;
import gov.usgs.earthquake.nshmp.postgres.source_settings.SourcePropertyAttributes;

class Settings {
  public DefaultMfdList defaultMfds;
  public MagUncertaintyList magUncertainties;
  public SourcePropertyAttributes sourceProperties;
  public RuptureScalingList ruptureScalingModels;
  
  private Settings(Builder builder) {
   this.defaultMfds = builder.defaultMfds;
   this.magUncertainties = builder.magUncertainties;
   this.ruptureScalingModels = builder.ruptureScalingModels;
   this.sourceProperties = builder.sourceProperties;
  }

  static Builder builder() {
    return new Builder();
  }
  
  static class Builder {
    private DefaultMfdList defaultMfds;
    private MagUncertaintyList magUncertainties;
    private RuptureScalingList ruptureScalingModels;
    private SourcePropertyAttributes sourceProperties;
    
    private Builder() {}
    
    Settings build() {
      return new Settings(this);
    }
    
    Builder defaultMfds (DefaultMfdList mfds) {
      this.defaultMfds = mfds;
      return this;
    }
   
    Builder magUncertainties(MagUncertaintyList uncertainties) {
      this.magUncertainties = uncertainties;
      return this;
    }
    
    Builder sourceProperties(SourcePropertyAttributes sourceProperties) {
      this.sourceProperties = sourceProperties;
      return this;
    }
    
    Builder ruptureScalingModels(RuptureScalingList models) {
      this.ruptureScalingModels = models;
      return this;
    }
  }
  
  static Settings westernUS () {
    /* Set MFDs */
    DefaultMfd singleMfd = DefaultMfd.SingleMfd.builder()
        .floats(false)
        .m(6.5)
        .rate(0.0)
        .weight(1.0)
        .build();
    
    DefaultMfd grMfd = DefaultMfd.GutenbergRichterMfd.builder()
        .a(0.0)
        .b(0.8)
        .dMag(0.1)
        .mMax(7.5)
        .mMin(6.55)
        .weight(1.0)
        .build();
        
    DefaultMfdList mfds = DefaultMfdList.builder()
        .add(singleMfd)
        .add(grMfd)
        .build();
    
    /* Set mag uncertainty */
    MagUncertainty aleatory = MagUncertainty.Aleatory.builder()
        .count(11)
        .cutoff(6.5)
        .moBalance(true)
        .sigma(0.12)
        .build();
   
    MagUncertainty epistemic = MagUncertainty.Epistemic.builder()
        .cutoff(6.5)
        .deltas(new double[] {-0.2, 0.0, 0.2})
        .weights(new double[] {0.2, 0.6, 0.2})
        .build();
    
    MagUncertaintyList sigmas = MagUncertaintyList.builder()
        .add(aleatory)
        .add(epistemic)
        .build();
    
    /* Set rupture scaling models */
    RuptureScalingList ruptureModels = RuptureScalingList.builder()
        .add(RuptureScaling.NSHM_FAULT_WC94_LENGTH)
        .build();
    
    /* Set source properties */
    SourcePropertyAttributes sourceProps = SourcePropertyAttributes
        .Fault
        .builder()
        .build();
    
    /* Set final setting object */
    Settings settings = Settings.builder()
        .defaultMfds(mfds)
        .magUncertainties(sigmas)
        .ruptureScalingModels(ruptureModels)
        .sourceProperties(sourceProps)
        .build();
   
    
    return settings;
  } 
  
}