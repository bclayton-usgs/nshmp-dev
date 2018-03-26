package gov.usgs.earthquake.nshmp.postgres.source_settings;

import com.google.common.collect.ImmutableList;

/**
 * An immutable list of {@link MagUncertainty}.
 * Use the {@link MagUncertiantyList.Builder} (via {@link #builder()}) to create
 * a list of {@link MagUncertainty}.
 * 
 * @author Brandon Clayton
 */
public class MagUncertaintyList {
  public ImmutableList<MagUncertainty> sigmas;
  
  private MagUncertaintyList (Builder builder) {
    this.sigmas = builder.sigmas.build();
  }
  
  /** Return a new {@code MagUncertaintyList} builder. */
  public static Builder builder() {
    return new Builder();
  }
  
  /**
   * {@code MagUncertaintyList} builder. Each call to {@code #build()}
   *    will return a new immutable list of {@code MagUncertainty}.
   */
  public static class Builder {
    private ImmutableList.Builder<MagUncertainty> sigmas = ImmutableList.builder();
    
    private Builder() {}
    
    /** Return new {@code MagUncertaintyList} instance */
    public MagUncertaintyList build() {
      validate();
      return new MagUncertaintyList(this);
    }
    
    /** Add a {@code MagUncertainty} to {@code MagUncertaintyList} */
    public Builder add (MagUncertainty sigma) {
      this.sigmas.add(sigma);
      return this;
    }
    
    /* Validate list is not empty */
    private void validate() {
      if (this.sigmas.build().isEmpty()) {
        throw new IllegalStateException("MagUncertaintyList is empty");
      }
    }
    
  }
}
