package gov.usgs.earthquake.nshmp;

import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_UNDERSCORE;
import static com.google.common.base.CaseFormat.LOWER_CAMEL;

/**
 * Fault model parameter XML and PostgreSQL attributes.
 * @author bclayton
 *
 */
public enum SourceAttribute {
  /* Common */
  ID,
  NAME,
  DIP,
  
  /* Postgres specific*/
  BIRD_DISPLACEMENT_RATE,
  CALC_WIDTH,
  FAULT_TRACE,
  GEO_DISPLACEMENT_RATE,
  GEO_RAKE,
  PRIMARY_STATE,
  STATE_ABBREV,
  UPPER_DEPTH,
  ZENG_DISPLACEMENT_RATE,
  
  /* XML file specific */
  BIRD,
  DEFORMATION_MODEL,
  DEPTH,
  FAULTS,
  FAULT_SOURCE_SET,
  GEO,
  GEOMETRY,
  RATE,
  RAKE,
  SOURCE,
  TRACE,
  WEIGHT,
  WIDTH,
  ZENG,
  SETTINGS,
  DEFAULT_MFDS,
  INCREMENTAL_MFD,
  MAG_UNCERTAINTY,
  SOURCE_PROPERTIES,
  RUPTURE_SCALING,
  
  /* Mag uncertainty */
  TYPE,
  ALEATORY,
  COUNT,
  CUTOFF,
  MO_BALANCE,
  SIGMA,
  EPISTEMIC,
  DELTAS,
  WEIGHTS,
  
  /* MFD */
  GR,
  A,
  B,
  D_MAG,
  M_MAX,
  M_MIN,
  INCR,
  FLOATS,
  M,
  SINGLE,
  GR_TAPER,
  C_MAG,
  M_CUT,
  MAGS,
  RATES;
  
  
  /** Return lower case string */
  String toLowerCase () {
    return name().toLowerCase();
  }
  
  /** Return upper case string */
  String toUpperCase () {
    return name().toUpperCase();
  }
  
  /** Return upper camel case string */
  String toUpperCamelCase () {
    return UPPER_UNDERSCORE.to(UPPER_CAMEL, name());
  }
  
  /** Return lower camel case string */
  String toLowerCamelCase () {
    return UPPER_UNDERSCORE.to(LOWER_CAMEL, name());
  }
  
}