package gov.usgs.earthquake.nshmp.postgres.sources;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import gov.usgs.earthquake.nshmp.postgres.source_settings.DefaultMfdList;
import gov.usgs.earthquake.nshmp.postgres.source_settings.MagUncertaintyList;
import gov.usgs.earthquake.nshmp.postgres.source_settings.RuptureScalingList;

import static gov.usgs.earthquake.nshmp.postgres.sources.SourceAttribute.ID;
import static gov.usgs.earthquake.nshmp.postgres.sources.SourceAttribute.NAME;
import static gov.usgs.earthquake.nshmp.postgres.sources.SourceAttribute.WEIGHT;
import static gov.usgs.earthquake.nshmp.postgres.sources.SourceType.FAULT_SOURCE_SET;

/** 
 * Container class to hold {@link FaultSource}s. A {@code FaultSourceSet} contains:
 *    <ul> 
 *      <li> 
 *        MFDs: {@link gov.usgs.earthquake.nshmp.postgres.source_settings.DefaultMfdList} 
 *      </li>
 *      <li> 
 *        Magnitude uncertainties: {@link gov.usgs.earthquake.nshmp.postgres
 *            .source_settings.MagUncertaintyList}
 *      </li>
 *      <li> 
 *        Rupture scaling models: {@link gov.usgs.earthquake.nshmp.postgres
 *            .source_settings.RuptureScalingList}
 *      </li> Fault sources: {@link FaultSource} </li>
 *    </ul>
 *        
 * Use {@link FaultSourceSet.Builder} (via {@link #builder()}) to
 *    set all fields.
 *    
 * @author Brandon Clayton
 */
public class FaultSourceSet {
  public ImmutableMap<SourceAttribute, String> attributes;
  public DefaultMfdList defaultMfds;
  public MagUncertaintyList magUncertainties;
  public RuptureScalingList ruptureScalingModels;
  public ImmutableList<FaultSource> sources;
  
  public SourceType type = FAULT_SOURCE_SET;
  
  private FaultSourceSet (Builder builder) {
    this.attributes = builder.attributes.build();
    this.defaultMfds = builder.defaultMfds;
    this.magUncertainties = builder.magUncertainties;
    this.ruptureScalingModels = builder.ruptureScalingModels;
    this.sources = builder.sources;
  }
  
  /** Return new {@code FaultSourceSet.Builder} */
  public static Builder builder() {
    return new Builder();
  }
  
  /**
   * {@code FaultSourceSet} builder. All fields must be set before calling
   *    {@link #build()}.
   */
  public static class Builder {
    private ImmutableMap.Builder<SourceAttribute, String> attributes = 
        ImmutableMap.builder();
    private DefaultMfdList defaultMfds;
    private MagUncertaintyList magUncertainties;
    private RuptureScalingList ruptureScalingModels;
    private ImmutableList<FaultSource> sources;
    
    private Builder() {}
    
    /** Return new {@code FaultSourceSet} instance */
    public FaultSourceSet build() {
      this.attributes.put(ID, "-1");
      this.attributes.put(WEIGHT, "1.0");
      validate();
      return new FaultSourceSet(this);
    }
    
    /** Set default MFDs */
    public Builder defaultMfds (DefaultMfdList mfds) {
      this.defaultMfds = mfds;
      return this;
    }
   
    /** Set magnitude uncertainties */
    public Builder magUncertainties(MagUncertaintyList uncertainties) {
      this.magUncertainties = uncertainties;
      return this;
    }
    
    /** Set rupture scaling models */
    public Builder ruptureScalingModels(RuptureScalingList models) {
      this.ruptureScalingModels = models;
      return this;
    }
    
    /** Set @{code FaultSourceSet} name */
    public Builder name(String name) {
      this.attributes.put(NAME, name);
      return this;
    }
    
    /** Set {@link FaultSource}s */
    public Builder sources(ImmutableList<FaultSource> sources) {
      this.sources = sources;
      return this;
    }
    
    private void validate() {
      ImmutableMap<SourceAttribute, String> attrs = this.attributes.build();
      
      if (!attrs.containsKey(ID) || 
          !attrs.containsKey(NAME) || 
          !attrs.containsKey(WEIGHT) || 
          this.defaultMfds.mfds.isEmpty() ||
          this.magUncertainties.sigmas.isEmpty() ||
          this.ruptureScalingModels.models.isEmpty() ||
          this.sources.isEmpty()) {
        throw new IllegalStateException("FaultSourceSet: not all fields are set");
      }
    }
  }
}