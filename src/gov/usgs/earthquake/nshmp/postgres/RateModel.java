package gov.usgs.earthquake.nshmp.postgres;

import static gov.usgs.earthquake.nshmp.eq.fault.Faults.checkRake;

import java.util.List;

import com.google.common.collect.ImmutableList;

/**
 * Slip rate container class for Bird, Geo, and Zeng.
 * 
 * @author Brandon Clayton
 */
public class RateModel {

  /*
   * TODO How do we check rate?
   */

  private SlipModel id;
  private RateType type;
  private Double rake;
  private Double value;

  private RateModel(SlipModel id, RateType type, Double value, Double rake) {
    this.id = id;
    this.type = type;
    this.rake = rake == null ? null : checkRake(rake);
    this.value = value;
  }

  /** The id */
  public String id() {
    return id.toString();
  }

  /** The rake in degrees */
  public double rake() {
    return rake;
  }

  /** The rate type */
  public RateType type() {
    return type;
  }

  /** The slip rate */
  public double value() {
    return value;
  }

  /** New slip rate builder */
  static Builder builder() {
    return new Builder();
  }

  /** Slip rate builder for Bird, Geo, and Zeng. */
  static class Builder {

    private ImmutableList.Builder<RateModel> rateModels;

    private Builder() {
      rateModels = ImmutableList.builder();
    }

    /**
     * Set the probability of activity rate.
     * 
     * @param rate The rate
     * @param rake The rake in degrees
     * @return this builder
     */
    Builder aPriori(Double rate, Double rake) {
      rateModels.add(new RateModel(SlipModel.A_PRIORI, RateType.PROBABILITY_OF_ACTIVITY, rate, rake));
      return this;
    }

    /**
     * Set the Bird slip rate.
     * 
     * @param rate The Bird slip rate
     * @param rake The Bird rake in degrees
     * @return this builder
     */
    Builder bird(Double rate, Double rake) {
      rateModels.add(new RateModel(SlipModel.BIRD, RateType.DISPLACEMENT, rate, rake));
      return this;
    }

    /**
     * Set the Geo slip rate.
     * 
     * @param rate The Geo slip rate
     * @param rake The Geo rake in degrees
     * @return this builder
     */
    Builder geo(Double rate, Double rake) {
      rateModels.add(new RateModel(SlipModel.GEO, RateType.DISPLACEMENT, rate, rake));
      return this;
    }

    /**
     * Set the slip rate.
     * 
     * @param rate The slip rate
     * @param rake The rake in degrees
     * @return this builder
     */
    Builder slip(Double rate, Double rake) {
      rateModels.add(new RateModel(SlipModel.GEO, RateType.SLIP, rate, rake));
      return this;
    }

    /**
     * Set the Zeng slip rate.
     * 
     * @param rate The Zeng slip rate
     * @param rake The Zeng rake in degrees
     * @return this builder
     */
    Builder zeng(Double rate, Double rake) {
      rateModels.add(new RateModel(SlipModel.ZENG, RateType.DISPLACEMENT, rate, rake));
      return this;
    }

    /** Return a new list of RateModel */
    List<RateModel> build() {
      return rateModels.build();
    }

  }

  private static enum SlipModel {
    A_PRIORI,
    BIRD,
    GEO,
    ZENG;
  }

}
