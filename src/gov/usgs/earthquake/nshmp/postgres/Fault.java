package gov.usgs.earthquake.nshmp.postgres;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static gov.usgs.earthquake.nshmp.eq.Earthquakes.checkCrustalDepth;
import static gov.usgs.earthquake.nshmp.eq.fault.Faults.checkDip;
import static gov.usgs.earthquake.nshmp.eq.fault.Faults.checkRake;
import static gov.usgs.earthquake.nshmp.eq.fault.Faults.checkTrace;
import static gov.usgs.earthquake.nshmp.eq.model.SourceType.FAULT;
import static gov.usgs.earthquake.nshmp.internal.TextUtils.validateName;
import static gov.usgs.earthquake.nshmp.postgres.Util.Keys.DEPTH;
import static gov.usgs.earthquake.nshmp.postgres.Util.Keys.DIP;
import static gov.usgs.earthquake.nshmp.postgres.Util.Keys.NAME;
import static gov.usgs.earthquake.nshmp.postgres.Util.Keys.RATE_MODELS;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import gov.usgs.earthquake.nshmp.eq.model.SourceType;
import gov.usgs.earthquake.nshmp.geo.LocationList;
import gov.usgs.earthquake.nshmp.geo.json.Feature;
import gov.usgs.earthquake.nshmp.geo.json.Properties;

/**
 * Fault source representation.
 *
 * @see FaultSet
 * @author Brandon Clayton
 */
public class Fault {

  private static final Gson GSON = new Gson();

  private final String name;
  private final int id;
  private final LocationList trace;
  private final List<RateModel> slipRateTree;
  private final double depth;
  private final double dip;

  private Fault(UncheckedBuilder builder) {
    name = builder.name;
    id = builder.id;
    trace = builder.trace;
    slipRateTree = builder.slipRateTree;
    depth = builder.depth;
    dip = builder.dip;
  }

  /** Fault name */
  public String name() {
    return name;
  }

  /** Fault id */
  public int id() {
    return id;
  }

  /** Fault trace */
  public LocationList trace() {
    return trace;
  }

  /** Fault slip rates */
  public List<RateModel> slipRateTree() {
    return slipRateTree;
  }

  /** Fault upper depth in km */
  public double depth() {
    return depth;
  }

  /** Fault dip in degrees */
  public double dip() {
    return dip;
  }

  /** The {@code SourceType} */
  public SourceType type() {
    return FAULT;
  }

  /** New fault builder */
  static Builder builder() {
    return new Builder();
  }

  /** Fault builder */
  static class Builder extends UncheckedBuilder {

    private Builder() {
      super();
    }

    @Override
    Builder depth(double depth) {
      return (Builder) super.depth(checkCrustalDepth(depth));
    }

    @Override
    Builder dip(double dip) {
      return (Builder) super.dip(checkDip(dip));
    }

    @Override
    Builder id(int id) {
      return (Builder) super.id(id);
    }

    @Override
    Builder name(String name) {
      return (Builder) super.name(validateName(name));
    }

    @Override
    Builder slipRateTree(List<RateModel> slipRateTree) {
      checkNotNull(slipRateTree);

      for (RateModel slipRate : slipRateTree) {
        checkRake(slipRate.rake());
        // checkWeight(slipRate.rate()); // How do we check rate?
      }

      return (Builder) super.slipRateTree(slipRateTree);
    }

    @Override
    Builder trace(LocationList trace) {
      return (Builder) super.trace(checkTrace(trace));
    }

    /**
     * Create a new fault from a GeoJSON feature.
     * 
     * @param feature The feature
     */
    static Fault fromFeature(Feature feature) {
      Properties properties = feature.properties();

      Builder builder = builder();

      builder.depth(properties.getDouble(DEPTH))
          .dip(properties.getDouble(DIP))
          .id(feature.idAsInt())
          .name(properties.getString(NAME))
          .slipRateTree(getSlipRates(properties))
          .trace(feature.asPolygonBorder());

      return builder.build();
    }

  }

  /** New unchecked fault builder */
  static UncheckedBuilder uncheckedBuilder() {
    return new UncheckedBuilder();
  }

  /** Fault unchecked builder */
  static class UncheckedBuilder {

    private String name;
    private Integer id;
    private LocationList trace;
    private ImmutableList<RateModel> slipRateTree;
    private Double depth;
    private Double dip;

    private boolean built;

    private UncheckedBuilder() {
      built = false;
      slipRateTree = ImmutableList.of();
    }

    /**
     * Set fault upper depth in km.
     * 
     * @param depth The depth in km
     * @return this builder
     */
    UncheckedBuilder depth(double depth) {
      this.depth = depth;
      return this;
    }

    /**
     * Set fault dip in degrees.
     * 
     * @param dip The fault dip in degrees
     * @return this builder
     */
    UncheckedBuilder dip(double dip) {
      this.dip = dip;
      return this;
    }

    /**
     * Set the fault id.
     * 
     * @param id Fault id
     * @return this builder
     */
    UncheckedBuilder id(int id) {
      this.id = id;
      return this;
    }

    /**
     * Set the fault name.
     * 
     * @param name Fault name
     * @return this builder
     */
    UncheckedBuilder name(String name) {
      this.name = name;
      return this;
    }

    /**
     * Set the slip rates.
     * 
     * @param slipRates Fault slip rates
     * @return this builder
     */
    UncheckedBuilder slipRateTree(List<RateModel> slipRateTree) {
      this.slipRateTree = ImmutableList.copyOf(slipRateTree);
      return this;
    }

    /**
     * Set the fault trace.
     * 
     * @param trace Fault trace
     * @return this builder
     */
    UncheckedBuilder trace(LocationList trace) {
      this.trace = trace;
      return this;
    }

    /** Create new fault */
    Fault build() {
      validateState();
      built = true;
      return new Fault(this);
    }

    /**
     * Create a new fault from a GeoJSON feature.
     * 
     * @param feature The feature
     */
    static Fault fromFeature(Feature feature) {
      Properties properties = feature.properties();

      UncheckedBuilder builder = uncheckedBuilder();

      builder.depth(properties.getDouble(DEPTH))
          .dip(properties.getDouble(DIP))
          .id(feature.idAsInt())
          .name(properties.getString(NAME))
          .slipRateTree(getSlipRates(properties))
          .trace(feature.asPolygonBorder());

      return builder.build();
    }

    private void validateState() {
      checkState(!built);
      checkState(depth != null);
      checkState(dip != null);
      checkState(id != null);
      checkState(name != null);
      checkState(slipRateTree != null);
      checkState(trace != null);
    }

  }

  /* Get slip rates from GeoJSON property */
  private static List<RateModel> getSlipRates(Properties properties) {
    JsonElement slipRatesEl = GSON.toJsonTree(properties.get(RATE_MODELS));
    return ImmutableList.copyOf(GSON.fromJson(slipRatesEl, RateModel[].class));
  }

}
