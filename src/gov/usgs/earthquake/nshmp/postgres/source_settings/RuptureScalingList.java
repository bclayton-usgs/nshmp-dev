package gov.usgs.earthquake.nshmp.postgres.source_settings;

import com.google.common.collect.ImmutableList;

import gov.usgs.earthquake.nshmp.eq.fault.surface.RuptureScaling;

/**
 * An immutable list of {@link RuptureScaling}.
 * Use the {@link RuptureScalingList.Builder} (via {@link #builder()}) to create
 * a list of {@link RuptureScaling}.
 * 
 * @author Brandon Clayton
 */
public class RuptureScalingList {
public ImmutableList<RuptureScaling> models;
  
  private RuptureScalingList(ImmutableList<RuptureScaling> models) {
    this.models = models;
  }
  
  /** Return a new {@code RuptureScalingList} builder. */
  public static Builder builder() {
    return new Builder();
  }
  
  /**
   * {@code RuptureScalingList} builder. Each call to {@code #build()}
   *    will return a new immutable list of {@code RuptureScaling}.
   */
  public static class Builder {
    private ImmutableList.Builder<RuptureScaling> models = ImmutableList.builder();
    
    private Builder() {}
    
    /** Return new {@code RuptureScalingList} instance */
    public RuptureScalingList build() {
      validate();
      return new RuptureScalingList(this.models.build());
    }
    
    /** Add a {@code RuptureScaling} to the {@code RuptureScalingList} */
    public Builder add (RuptureScaling model) {
      this.models.add(model);
      return this;
    }
    
    /* Validate list is not empty */
    private void validate() {
      if (this.models.build().isEmpty()) {
        throw new IllegalStateException("RuptureScalingList is empty");
      }
    }
  }
}
