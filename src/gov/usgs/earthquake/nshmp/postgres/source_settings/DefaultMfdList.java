package gov.usgs.earthquake.nshmp.postgres.source_settings;

import com.google.common.collect.ImmutableList;

/**
 * An immutable list of {@link DefaultMfd}s.
 * Use the {@link DefaultMfdList.Builder} (via {@link #builder()}) to create
 * a list of {@link DefaultMfd}s.
 * 
 * @author Brandon Clayton
 */
public class DefaultMfdList {
  public ImmutableList<DefaultMfd> mfds;
  
  private DefaultMfdList (ImmutableList<DefaultMfd> mfds) {
    this.mfds = mfds;
  }
  
  /** Return a new {@code DefaultMfdList} builder. */
  public static Builder builder() {
    return new Builder();
  }
  
  /**
   * {@code DefaultMfdList} builder. Each call to {@code #build()}
   *    will return a new immutable list of {@code DefaultMfd}s.
   */
  public static class Builder {
    private ImmutableList.Builder<DefaultMfd> mfds = ImmutableList.builder();
    
    private Builder() {}
    
    /** Return new {@code DefaultMfdList} instance */
    public DefaultMfdList build() {
      validate();
      return new DefaultMfdList(this.mfds.build());
    }
    
    /** Add a {@code DefaultMfd} to the {@code DefaultMfdList}. */
    public Builder add (DefaultMfd mfd) {
      this.mfds.add(mfd);
      return this;
    }
    
    /* Validate list is not empty */
    private void validate() {
      if (this.mfds.build().isEmpty()) {
        throw new IllegalStateException("DefaultMfdList is empty");
      }
    }
    
  }
}
