package gov.usgs.earthquake.nshmp;

import com.google.common.collect.ImmutableList;

import gov.usgs.earthquake.nshmp.eq.fault.surface.RuptureScaling;

public class Settings {
  ImmutableList<DefaultMfd> defaultMfds;
  ImmutableList<MagUncertainty> magUncertainties;
  RuptureScaling ruptureScaling;
  
  Settings (
      ImmutableList<DefaultMfd> defaultMfds, 
      ImmutableList<MagUncertainty> mags,
      RuptureScaling ruptureScaling) {
    this.defaultMfds = defaultMfds;
    this.magUncertainties = mags;
    this.ruptureScaling = ruptureScaling;
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
        
    ImmutableList.Builder<DefaultMfd> mfdBuilder = ImmutableList.builder();
    mfdBuilder.add(singleMfd);
    mfdBuilder.add(grMfd);
    
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
    
    ImmutableList.Builder<MagUncertainty> uncertaintyBuilder = 
        ImmutableList.builder();
    uncertaintyBuilder.add(aleatory);
    uncertaintyBuilder.add(epistemic);
    
    return new Settings(
        mfdBuilder.build(), 
        uncertaintyBuilder.build(),
        RuptureScaling.NSHM_FAULT_WC94_LENGTH);
  } 
  
  /*
  enum RuptureScaling {
    NONE,
    NSHM_FAULT_CA_ELLB_WC94_AREA,
    NSHM_FAULT_WC94_LENGTH,
    NSHM_POINT_WC94_LENGTH,
    NSHM_SOMERVILLE,
    NSHM_SUB_GEOMAT_LENGTH,
    PEER;
  }
  */
  
}
