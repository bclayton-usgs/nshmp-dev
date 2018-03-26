package gov.usgs.earthquake.nshmp.postgres.sources;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_UNDERSCORE;

public enum SourceType {
  /* Fault source type */
  FAULT,
  FAULT_SOURCE_SET;
  
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
