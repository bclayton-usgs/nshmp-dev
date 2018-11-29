package gov.usgs.earthquake.nshmp.postgres;

import static com.google.common.base.Preconditions.checkState;
import static gov.usgs.earthquake.nshmp.eq.fault.Faults.checkRake;

import java.util.List;

import com.google.common.collect.ImmutableList;

/**
 * Slip rate container class for Bird, Geo, and Zeng.
 * 
 * @author Brandon Clayton
 */
public class SlipRate {

  /*
   * TODO How do we check rate?
   */

  private SlipModel id;
  private double rake;
  private double rate;

  private SlipRate(SlipModel id, double rake, double rate) {
    this.id = id;
    this.rake = checkRake(rake);
    this.rate = rate;
  }

  /** The id */
  public String id() {
    return id.toString();
  }

  /** The rake in degrees */
  public double rake() {
    return rake;
  }

  /** The slip rate */
  public double rate() {
    return rate;
  }

  /** New slip rate builder */
  static Builder builder() {
    return new Builder();
  }

  /** Slip rate builder for Bird, Geo, and Zeng. */
  static class Builder {

    private SlipRate bird;
    private SlipRate geo;
    private SlipRate zeng;

    private Builder() {}

    /**
     * Set the Bird slip rate.
     * 
     * @param rake The Bird rake in degrees
     * @param rate The Bird slip rate
     * @return this builder
     */
    Builder bird(double rake, double rate) {
      bird = new SlipRate(SlipModel.BIRD, rake, rate);
      return this;
    }

    /**
     * Set the Geo slip rate.
     * 
     * @param rake The Geo rake in degrees
     * @param rate The Geo slip rate
     * @return this builder
     */
    Builder geo(double rake, double rate) {
      geo = new SlipRate(SlipModel.GEO, rake, rate);
      return this;
    }

    /**
     * Set the Zeng slip rate.
     * 
     * @param rake The Zeng rake in degrees
     * @param rate The Zeng slip rate
     * @return this builder
     */
    Builder zeng(double rake, double rate) {
      zeng = new SlipRate(SlipModel.ZENG, rake, rate);
      return this;
    }

    /** Return a new list for the Bird, Geo, and Zeng slip rates. */
    List<SlipRate> build() {
      validateState();
      return ImmutableList.of(bird, geo, zeng);
    }

    private void validateState() {
      checkState(bird != null);
      checkState(geo != null);
      checkState(zeng != null);
    }

  }

  private static enum SlipModel {
    BIRD,
    GEO,
    ZENG;
  }

}
