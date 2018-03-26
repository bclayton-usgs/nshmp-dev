package gov.usgs.earthquake.nshmp.postgres.source_settings;

import com.google.common.collect.ImmutableList;

/**
 * An immutable list of {@link DeformationModel}s.
 * Use the {@link DeformationModelList.Builder} (via {@link #builder()}) to create
 *    a list of {@link DeformationModel}s. 
 * @author Brandon Clayton
 */
public class DeformationModelList {
  public ImmutableList<DeformationModel> models;
  
  private DeformationModelList(Builder builder) {
    this.models = builder.models.build();
  }
  
  /** Returns a new {@code DeformationModelList} builder */
  public static Builder builder() {
    return new Builder();
  }
  
  /**
   * {@code DeformationModelList} builder. Each call to {@code #build()}
   *    will return a new immutable list of {@code DeformationModel}s.
   */
  public static class Builder {
    private ImmutableList.Builder<DeformationModel> models = ImmutableList.builder();
    
    private Builder() {}
    
    /** Return new {@code DeformationModelList} instance */
    public DeformationModelList build() {
      validate();
      return new DeformationModelList(this);
    }
    
    /** Add a {@code DeformationModel} to the {@code DeformationModelList} */
    public Builder add (DeformationModel model) {
      this.models.add(model);
      return this;
    }
    
    /* Validate list is not empty */
    private void validate() {
      if (this.models.build().isEmpty()) {
        throw new IllegalStateException("DeformationModelList is empty");
      }
    }
  }
}
