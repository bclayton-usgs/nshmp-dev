package gov.usgs.earthquake.nshmp.postgres.source_settings;

import static gov.usgs.earthquake.nshmp.postgres.sources.SourceAttribute.*;

import java.util.Arrays;
import com.google.common.collect.ImmutableMap;

import gov.usgs.earthquake.nshmp.postgres.sources.SourceAttribute;

/**
 * Container to set magnitude frequency distribution attributes, 
 *    either single, Gutenberg-Richter, tapered Gutenberg-Richter, or
 *    incremental.
 * 
 * @author Brandon Clayton
 *
 */
public class DefaultMfd {
  public ImmutableMap<SourceAttribute, String> attributes;
  
  private DefaultMfd(ImmutableMap<SourceAttribute, String> attributes) {
    this.attributes = attributes;
  }
  
  /**
   * Return {@code DefaultMfd} of type Gutenberg-Richter.
   * A Gutenberg-Richter type MFD contains:
   *    <ul>
   *      <li> a = double </li>
   *      <li> b = double</li>
   *      <li> dMag = double </li>
   *      <li> mMin = double </li>
   *      <li> mMax = double </li>
   *      <li> weight = double </li>
   *    <ul>
   */
  public static class GutenbergRichterMfd {
    final static SourceAttribute MFD_TYPE = GR;
    
    private GutenbergRichterMfd() {}
    
    /**
     * Returns the {@code GutenbergRichterMfd} builder.
     * All fields must be set before calling build.
     * 
     * @see GutenbergRichterMfd.Builder
     */
    public static Builder builder () {
      return new Builder();
    }
    
    /** GutenbergRichterMfd builder */
    public static class Builder {
      private ImmutableMap.Builder<SourceAttribute, String> attributes = 
          ImmutableMap.builder();
      
      private Builder () {}
      
      /** Return {@code DefaultMfd} instance. */
      public DefaultMfd build () {
        this.attributes.put(TYPE, MFD_TYPE.toString());
        validate();
        return new DefaultMfd(this.attributes.build());
      }
      
      /** Set a attribute */
      public Builder a (double a) {
        this.attributes.put(A, Double.toString(a));
        return this;
      }
      
      /** Set b attribute */
      public Builder b (double b) {
        this.attributes.put(B, Double.toString(b));
        return this;
      }
      
      /** Set dMag attribute */
      public Builder dMag (double dMag) {
        this.attributes.put(D_MAG, Double.toString(dMag));
        return this;
      }
      
      /** Set mMax attribute */
      public Builder mMax (double mMax ) {
        this.attributes.put(M_MAX, Double.toString(mMax));
        return this;
      }
      
      /** Set mMin attribute */
      public Builder mMin (double mMin ) {
        this.attributes.put(M_MIN, Double.toString(mMin));
        return this;
      }
      
      /** Set the weight attribute */
      public Builder weight (double weight) {
        this.attributes.put(WEIGHT, Double.toString(weight));
        return this;
      }
      
      /** Validate that all fields are set */
      private void validate () {
        ImmutableMap<SourceAttribute, String> mfdCheck = this.attributes.build();
        
        if (!mfdCheck.containsKey(A) ||
            !mfdCheck.containsKey(B) ||
            !mfdCheck.containsKey(D_MAG) ||
            !mfdCheck.containsKey(M_MAX) ||
            !mfdCheck.containsKey(M_MIN) ||
            !mfdCheck.containsKey(TYPE) || 
            !mfdCheck.containsKey(WEIGHT)) {
          throw new IllegalStateException("Not all fields are set");
        }
      }
    }  
  }
  
  /**
   * Return {@code DefaultMfd} of type single. 
   * A single type MFD contains:
   *    <ul>
   *      <li> floats = Boolean </li>
   *      <li> mags = Array<double> </li>
   *      <li> rates = Array<double> </li>
   *      <li> weight = double </li>
   *    <ul>
   */
  public static class IncrementalMfd {
    final static SourceAttribute MFD_TYPE = INCR;
    
    private IncrementalMfd() {}
    
    /**
     * Returns the {@code IncrementalMfd} builder.
     * All fields must be set before calling build.
     * 
     * @see IncrementalMfd.Builder
     */
    public static Builder builder () {
      return new Builder();
    }
    
    /** IncrementalMfd builder */
    public static class Builder {
      private ImmutableMap.Builder<SourceAttribute, String> attributes = 
          ImmutableMap.builder();
      
      private Builder () {}
      
      /** Return {@code DefaultMfd} instance */
      public DefaultMfd build () {
        this.attributes.put(TYPE, MFD_TYPE.toString());
        validate();
        return new DefaultMfd(this.attributes.build());
      }
      
      /** Set floats attribute */
      public Builder floats (Boolean floats) {
        this.attributes.put(FLOATS, floats.toString());
        return this;
      }
      
      /** Set mags attribute */
      public Builder mags (double[] mags) {
        this.attributes.put(MAGS, Arrays.toString(mags));
        return this;
      }
      
      /** Set the rates attribute */
      public Builder rates (double[] rates) {
        this.attributes.put(RATES, Arrays.toString(rates));
        return this;
      }
      
      /** Set the weight attribute */
      public Builder weight (double weight) {
        this.attributes.put(WEIGHT, Double.toString(weight));
        return this;
      }
      
      /** Validate all fields are set */
      private void validate () {
        ImmutableMap<SourceAttribute, String> mfdCheck = this.attributes.build();
        
        if (!mfdCheck.containsKey(FLOATS) ||
            !mfdCheck.containsKey(MAGS) ||
            !mfdCheck.containsKey(RATES) ||
            !mfdCheck.containsKey(TYPE) || 
            !mfdCheck.containsKey(WEIGHTS)) {
          throw new IllegalStateException("Not all fields are set");
        }
      }
    }  
  }
  
  /**
   * Return {@code DefaultMfd} of type single. 
   * A single type MFD contains:
   *    <ul>
   *      <li> floats = Boolean </li>
   *      <li> m = double </li>
   *      <li> rate = double </li>
   *      <li> weight = double </li>
   *    <ul>
   */
  public static class SingleMfd {
    final static SourceAttribute MFD_TYPE = SINGLE;
    
    private SingleMfd() {}
    
    /**
     * Returns the {@code SingleMfd} builder.
     * All fields must be set before calling build.
     * 
     * @see SingleMfd.Builder
     */
    public static Builder builder () {
      return new Builder();
    }
    
    /** SingleMfd builder */
    public static class Builder {
      private ImmutableMap.Builder<SourceAttribute, String> attributes = 
          ImmutableMap.builder();
      
      private Builder () {}
      
      /** Return {@code DefaultMfd} instance */
      public DefaultMfd build () {
        this.attributes.put(TYPE, MFD_TYPE.toString());
        validate();
        return new DefaultMfd(this.attributes.build());
      }
      
      /** Set floats attribute */
      public Builder floats (Boolean floats) {
        this.attributes.put(FLOATS, floats.toString());
        return this;
      }
      
      /** Set m attribute */
      public Builder m (double m) {
        this.attributes.put(M, Double.toString(m));
        return this;
      }
      
      /** Set rate attribute */
      public Builder rate (double rate) {
        this.attributes.put(RATE, Double.toString(rate));
        return this;
      }
      
      /** Set weight attribute */
      public Builder weight (double weight) {
        this.attributes.put(WEIGHT, Double.toString(weight));
        return this;
      }
      
      /** Validate all fields are set */
      private void validate () {
        ImmutableMap<SourceAttribute, String> mfdCheck = this.attributes.build();
        
        if (!mfdCheck.containsKey(FLOATS) ||
            !mfdCheck.containsKey(M) ||
            !mfdCheck.containsKey(RATE) ||
            !mfdCheck.containsKey(TYPE) || 
            !mfdCheck.containsKey(WEIGHT)) {
          throw new IllegalStateException("Not all fields are set");
        }
      }
    }  
  }
  
  /**
   * Return {@code DefaultMfd} of type tapered Gutenberg-Richter.
   * A tapered Gutenberg-Richter type MFD contains:
   *    <ul>
   *      <li> a = double </li>
   *      <li> b = double</li>
   *      <li> cMag = double </li>
   *      <li> dMag = double </li>
   *      <li> mMax = double </li>
   *      <li> mMin = double </li>
   *      <li> weight = double </li>
   *    <ul>
   */
  public static class TaperedGutenbergRichterMfd {
    final static SourceAttribute MFD_TYPE = GR_TAPER;
    
    private TaperedGutenbergRichterMfd() {}
    
    /**
     * Returns the {@code TaperedGutenbergRichterMfd} builder.
     * All fields must be set before calling build.
     * 
     * @see TaperedGutenbergRichterMfd.Builder
     */
    public static Builder builder () {
      return new Builder();
    }
    
    /** TaperedGutenbergRichterMfd builder */
    public static class Builder {
      private ImmutableMap.Builder<SourceAttribute, String> attributes = 
          ImmutableMap.builder();
      
      private Builder () {}
      
      /** Returns {@code DefaultMfd} instance */
      public DefaultMfd build () {
        this.attributes.put(TYPE, MFD_TYPE.toString());
        validate();
        return new DefaultMfd(this.attributes.build());
      }
      
      /** Set a attribute */
      public Builder a (double a) {
        this.attributes.put(A, Double.toString(a));
        return this;
      }
      
      /** Set b attribute */
      public Builder b (double b) {
        this.attributes.put(A, Double.toString(b));
        return this;
      }
      
      /** Set cMag attribute */
      public Builder cMag (double cMag) {
        this.attributes.put(C_MAG, Double.toString(cMag));
        return this;
      }
      
      /** Set dMag attribute */
      public Builder dMag (double dMag) {
        this.attributes.put(D_MAG, Double.toString(dMag));
        return this;
      }
      
      /** Set mCut attribute */
      public Builder mCut (double mCut) {
        this.attributes.put(M_CUT, Double.toString(mCut));
        return this;
      }
      
      /** Set mMax attribute */
      public Builder mMax (double mMax ) {
        this.attributes.put(M_MAX, Double.toString(mMax));
        return this;
      }
      
      /** Set mMin attribute */
      public Builder mMin (double mMin ) {
        this.attributes.put(M_MIN, Double.toString(mMin));
        return this;
      }
      
      /** Set weight attribute */
      public Builder weight (double weight) {
        this.attributes.put(WEIGHT, Double.toString(weight));
        return this;
      }
      
      /** Validate all fields are set */
      private void validate () {
        ImmutableMap<SourceAttribute, String> mfdCheck = this.attributes.build();
        
        if (!mfdCheck.containsKey(A) ||
            !mfdCheck.containsKey(B) ||
            !mfdCheck.containsKey(C_MAG) ||
            !mfdCheck.containsKey(D_MAG) ||
            !mfdCheck.containsKey(M_MAX) ||
            !mfdCheck.containsKey(M_MIN) ||
            !mfdCheck.containsKey(TYPE) || 
            !mfdCheck.containsKey(WEIGHT)) {
          throw new IllegalStateException("Not all fields are set");
        }
      }
    }  
  }

}
