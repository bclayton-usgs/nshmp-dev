package gov.usgs.earthquake.nshmp.postgres.sources;

import static gov.usgs.earthquake.nshmp.postgres.sources.SourceAttribute.ID;
import static gov.usgs.earthquake.nshmp.postgres.sources.SourceAttribute.NAME;

import com.google.common.collect.ImmutableMap;

import gov.usgs.earthquake.nshmp.postgres.source_settings.DeformationModelList;
import gov.usgs.earthquake.nshmp.postgres.source_settings.SourceGeometry.FaultGeometry;

/**
 * Fault source representation containing:
 *    <ul>
 *      <li> Deformation models: {@link gov.usgs.earthquake.nshmp.postgres
 *          .source_settings.DeformationModelList} </li>
 *      <li> Fault geometry: {@link gov.usgs.earthquake.nshmp.postgres
 *          .source_settings.SourceGeometry.FaultGeometry} </li>
 *    </ul>
 *  Use {@link FaultSource.Builder} (via {@link #builder()}) to create 
 *      new instance of {@code FaultSource}.
 * 
 * @author Brandon Clayton
 *
 */
public class FaultSource {
  public ImmutableMap<SourceAttribute, String> attributes;;
  public DeformationModelList deformationModels;
  public FaultGeometry geometry;
  
  private FaultSource(Builder builder) {
    this.attributes = builder.attributes.build();
    this.deformationModels = builder.deformationModels;
    this.geometry = builder.geometry;
  }
  
  /** Return new {@code FaultSource.Builder} */
  public static Builder builder() {
    return new Builder();
  }
  
  /** 
   * {@code FaultSource} builder. All properties must be set before 
   *    calling {@link #build()}.
   *
   */
  public static class Builder {
    private ImmutableMap.Builder<SourceAttribute, String> attributes = 
        ImmutableMap.builder();
    private DeformationModelList deformationModels;
    private FaultGeometry geometry;
    
    private Builder() {}
    
    /** Return new {@code FaultSource} instance */
    public FaultSource build() {
      validate();
      return new FaultSource(this);
    }
    
    /** Set the fault source id */
    public Builder id (String id) {
      this.attributes.put(ID, id);
      return this;
    }
    
    /** Set the fault source name */
    public Builder name (String name) {
      this.attributes.put(NAME, name);
      return this;
    }
    
    /** Set the deformation models */
    public Builder deformationModels (DeformationModelList models) {
      this.deformationModels = models;
      return this;
    }
    
    /** Set the fault geometry */
    public Builder geometry (FaultGeometry geometry) {
      this.geometry = geometry;
      return this;
    }
    
    /* Validate all fields are set */
    private void validate() {
      ImmutableMap<SourceAttribute, String> attrs = this.attributes.build();
      
      if (!attrs.containsKey(ID) || 
          !attrs.containsKey(NAME) ||
          this.deformationModels.models.isEmpty() ||
          !(this.geometry.trace.size() > 0)) {
        throw new IllegalStateException("FaultSource: not all fields are set");
      }
    }
  }
}
