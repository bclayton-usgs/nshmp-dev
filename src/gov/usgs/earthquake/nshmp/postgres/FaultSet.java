package gov.usgs.earthquake.nshmp.postgres;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static gov.usgs.earthquake.nshmp.eq.model.SourceType.FAULT;
import static gov.usgs.earthquake.nshmp.internal.TextUtils.validateName;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.ImmutableList;

import gov.usgs.earthquake.nshmp.eq.model.SourceType;
import gov.usgs.earthquake.nshmp.geo.json.FeatureCollection;
import gov.usgs.earthquake.nshmp.geo.json.GeoJson;

/**
 * Container class for faults.
 * 
 * <p> A fault set can only be created from a {@code FeatureCollection}
 * ({@link FaultSet.Builder#fromFeatureCollection(String, int, FeatureCollection)})
 * or a GeoJSON file ({@link FaultSet.Builder#fromGeoJson(String, int, Path)}).
 * 
 * @author Brandon Clayton
 */
public class FaultSet implements Iterable<Fault> {

  private final String name;
  private final int id;
  private final List<Fault> sources;

  private FaultSet(UncheckedBuilder builder) {
    name = builder.name;
    id = builder.id;
    sources = builder.sources.build();
  }

  /** Fault set name */
  public String name() {
    return name;
  }

  /** Fault set id */
  public int id() {
    return id;
  }

  /** The faults */
  public List<Fault> sources() {
    return sources;
  }

  /** The {@code SourceType} */
  public SourceType type() {
    return FAULT;
  }

  @Override
  public Iterator<Fault> iterator() {
    return sources.iterator();
  }

  /** New fault set builder */
  static Builder builder() {
    return new Builder();
  }

  /** Fault set builder */
  public static class Builder extends UncheckedBuilder {

    private Builder() {
      super();
    }

    @Override
    Builder add(Fault source) {
      return (Builder) super.add(checkNotNull(source));
    }

    @Override
    Builder id(int id) {
      return (Builder) super.id(id);
    }

    @Override
    Builder name(String name) {
      return (Builder) super.name(validateName(name));
    }

    /**
     * Return a new fault set from a GeoJSON file.
     * 
     * @param name Fault set name
     * @param id Fault set id
     * @param json GeoJSON file path
     */
    public static FaultSet fromGeoJson(String name, int id, Path json) {
      return fromFeatureCollection(name, id, GeoJson.fromJson(json));
    }

    /**
     * Return a new fault set from a GeoJSON feature collection.
     * 
     * @param name Fault set name
     * @param id Fault set id
     * @param fc The feature collection
     */
    public static FaultSet fromFeatureCollection(String name, int id, FeatureCollection fc) {
      Builder builder = builder()
          .id(id)
          .name(name);

      fc.features().stream()
          .map(Fault.Builder::fromFeature)
          .forEach(builder::add);

      return builder.build();
    }

  }

  /** New unchecked fault set builder */
  static UncheckedBuilder uncheckedBuilder() {
    return new UncheckedBuilder();
  }

  /** Unchecked fault set builder */
  static class UncheckedBuilder {

    private String name;
    private Integer id;
    private ImmutableList.Builder<Fault> sources;

    private boolean built;

    private UncheckedBuilder() {
      built = false;
      sources = new ImmutableList.Builder<>();
    }

    /**
     * Add a fault to the fault set.
     * 
     * @param source The fault
     * @return this builder
     */
    UncheckedBuilder add(Fault source) {
      sources.add(source);
      return this;
    }

    /**
     * Set the fault set id.
     * 
     * @param id Fault set id
     * @return this builder
     */
    UncheckedBuilder id(int id) {
      this.id = id;
      return this;
    }

    /**
     * Set the fault set name.
     * 
     * @param name Fault set name
     * @return this builder
     */
    UncheckedBuilder name(String name) {
      this.name = name;
      return this;
    }

    /** Create new fault set */
    FaultSet build() {
      validateState();
      built = true;
      return new FaultSet(this);
    }

    /**
     * Return a new fault set from a GeoJSON file.
     * 
     * @param name Fault set name
     * @param id Fault set id
     * @param json GeoJSON file path
     */
    static FaultSet fromGeoJson(String name, int id, Path json) {
      return fromFeatureCollection(name, id, GeoJson.fromJson(json));
    }

    /**
     * Return a new fault set from a GeoJSON feature collection.
     * 
     * @param name Fault set name
     * @param id Fault set id
     * @param fc The feature collection
     */
    static FaultSet fromFeatureCollection(String name, int id, FeatureCollection fc) {
      UncheckedBuilder builder = uncheckedBuilder()
          .id(id)
          .name(name);

      fc.features().stream()
          .map(Fault.UncheckedBuilder::fromFeature)
          .forEach(builder::add);

      return builder.build();
    }

    private void validateState() {
      checkState(!built);
      checkState(id != null);
      checkState(name != null);
      checkState(!sources.build().isEmpty());
    }
  }

}
