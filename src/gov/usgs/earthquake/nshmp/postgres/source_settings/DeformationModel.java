package gov.usgs.earthquake.nshmp.postgres.source_settings;

import static gov.usgs.earthquake.nshmp.postgres.sources.SourceAttribute.BIRD;
import static gov.usgs.earthquake.nshmp.postgres.sources.SourceAttribute.GEO;
import static gov.usgs.earthquake.nshmp.postgres.sources.SourceAttribute.ZENG;

import gov.usgs.earthquake.nshmp.postgres.sources.SourceAttribute;

/**
 * Container to set a Bird, Geo, or Zeng deformation model.
 * 
 * <p>Consider using the convenience methods ({@link #bird(double)} , {@link #geo(double)},
 *    or {@link #zeng(double)}) to generate new instance of {@code DeformationModel}. 
 *    
 * @author Brandon Clayton
 */
public class DeformationModel {
  public final String id;
  public final String rate;

  /**
   * Create a new {@code DeformationModel} 
   * @param id - Deformation id
   * @param rate - Deformation rate
   */
  public DeformationModel(SourceAttribute id, double rate) {
    validate(id);
    this.id = id.toUpperCase();
    this.rate = Double.toString(rate);
  }
  
  /* Validate deformation model is a Bird, Geo, or Zeng */
  private void validate(SourceAttribute id) {
    if (!id.equals(BIRD) &&
        !id.equals(GEO) &&
        !id.equals(ZENG)) {
      throw new IllegalStateException("Not an accepted deformation model.");
    }
  }
  
  /**
   * Return new instance of {@code DeformationModel} of type Bird.
   * @param rate
   */
  public static DeformationModel bird(double rate) {
    return new DeformationModel(BIRD, rate);
  }
  
  /**
   * Return new instance of {@code DeformationModel} of type Geo.
   * @param rate
   */
  public static DeformationModel geo(double rate) {
    return new DeformationModel(GEO, rate);
  }
  
  /**
   * Return new instance of {@code DeformationModel} of type Zeng.
   * @param rate
   */
  public static DeformationModel zeng(double rate) {
    return new DeformationModel(ZENG, rate);
  }
}
