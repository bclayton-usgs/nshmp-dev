package gov.usgs.earthquake.nshmp;

import java.util.Arrays;
import com.google.common.collect.ImmutableMap;
import static gov.usgs.earthquake.nshmp.SourceAttribute.*;

/**
 * Container to set magnitude uncertainty attributes for either 
 *    aleatory or epistemic uncertainty.
 *    
 * @author Brandon Clayton
 */
public class MagUncertainty {
  ImmutableMap<SourceAttribute, String> attributes;
  
  private MagUncertainty(ImmutableMap<SourceAttribute, String> attributes) {
    this.attributes = attributes;
  }
  
  /**
   * Aleatory uncertainty contains: 
   *    <ul>
   *      <li> count = int </li>
   *      <li> cutoff = double </li>
   *      <li> moBalance  = Boolean </li>
   *      <li> sigma = double </li>
   *    </ul>
   */
  static class Aleatory {
    
    /**
     * Returns {@code Aleatory} builder.
     * All fields must be set before calling build.
     * 
     * @see Aleatory.Builder
     */
    static Builder builder () {
      return new Builder();
    }
    
    /** Aleatory builder */
    static class Builder {
      private ImmutableMap.Builder<SourceAttribute, String> attributes = 
          ImmutableMap.builder();
      
      private Builder () {}
      
      /** Returns {@code MagUncertainty} instance */
      MagUncertainty build () {
        this.attributes.put(TYPE, ALEATORY.toUpperCamelCase());
        validate();
        return new MagUncertainty(this.attributes.build());
      }
      
      /** Set count attribute */
      Builder count (int count) {
        this.attributes.put(COUNT, Integer.toString(count));
        return this;
      }
      
      /** Set cutoff attribute */
      Builder cutoff (double cutoff) {
        this.attributes.put(CUTOFF, Double.toString(cutoff));
        return this;
      }
      
      /** Set moBalance attribute */
      Builder moBalance (Boolean moBalance) {
        this.attributes.put(MO_BALANCE, moBalance.toString());
        return this;
      }
      
      /** Set sigma attribute */
      Builder sigma (double sigma) {
        this.attributes.put(SIGMA, Double.toString(sigma));
        return this;
      }
      
      /** Validate all fields are set */
      void validate () {
        ImmutableMap<SourceAttribute, String> magCheck = this.attributes.build();
        
        if (!magCheck.containsKey(COUNT) ||
            !magCheck.containsKey(CUTOFF) ||
            !magCheck.containsKey(MO_BALANCE) ||
            !magCheck.containsKey(SIGMA) ||
            !magCheck.containsKey(TYPE)) {
          throw new IllegalStateException("Not all fields are set.");
        }
      }
    }
  }
  
  /**
   * Epistemic uncertainty contains: 
   *    <ul>
   *      <li> cutoff = double </li>
   *      <li> deltas = Array<double> </li>
   *      <li> weights = Array<double> </li>
   *    <ul>
   *
   */
  static class Epistemic {
    
    /**
     * Returns {@code Epistemic} builder.
     * All fields must be set before calling build.
     * 
     * @see Epistemic.Builder
     */
    static Builder builder () {
      return new Builder();
    }
    
    /** Epistemic builder */
    static class Builder {
      private ImmutableMap.Builder<SourceAttribute, String> attributes = 
          ImmutableMap.builder();
      
      private Builder () {}
      
      /** Returns {@code MagUncertainty} instance */
      MagUncertainty build () {
        this.attributes.put(TYPE, EPISTEMIC.toUpperCamelCase());
        validate();
        return new MagUncertainty(this.attributes.build());
      }
      
      /** Set cutoff attribute */
      Builder cutoff (double cutoff) {
        this.attributes.put(CUTOFF, Double.toString(cutoff));
        return this;
      }
      
      /** Set deltas attribute */
      Builder deltas (double[] deltas) {
        this.attributes.put(DELTAS, Arrays.toString(deltas));
        return this;
      }
      
      /** Set weights attribute */
      Builder weights (double[] weights) {
        this.attributes.put(WEIGHTS, Arrays.toString(weights));
        return this;
      }
      
      /** Validate all fields are set */
      void validate () {
        ImmutableMap<SourceAttribute, String> magCheck = this.attributes.build();
        
        if (!magCheck.containsKey(CUTOFF) ||
            !magCheck.containsKey(DELTAS) ||
            !magCheck.containsKey(TYPE) ||
            !magCheck.containsKey(WEIGHTS)) {
          throw new IllegalStateException("Not all fields are set.");
        }
      }
    }
  }

}
