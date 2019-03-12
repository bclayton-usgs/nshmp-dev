package gov.usgs.earthquake.nshmp.postgres;

import static gov.usgs.earthquake.nshmp.postgres.Util.getSQLDouble;
import static gov.usgs.earthquake.nshmp.postgres.Util.Keys.BIRD_RATE;
import static gov.usgs.earthquake.nshmp.postgres.Util.Keys.GEO_RAKE;
import static gov.usgs.earthquake.nshmp.postgres.Util.Keys.GEO_RATE;
import static gov.usgs.earthquake.nshmp.postgres.Util.Keys.ZENG_RATE;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Query the hazfaults_2014 database.
 */
public class FaultExporter2014 extends FaultExporter {

  private static final String HAZFAULTS_2014 = "hazfaults_2014";

  FaultExporter2014(String table) throws IOException {
    super(table);
  }

  public static void main(String[] args) throws IOException {
    new FaultExporter2014(HAZFAULTS_2014).export();
  }

  @Override
  List<RateModel> getRateModels(ResultSet result) throws SQLException {
    List<RateModel> rateModels = super.getRateModels(result);

    if (!rateModels.isEmpty()) return rateModels;

    Double rake = getSQLDouble(result, GEO_RAKE);

    return RateModel.builder()
        .bird(getSQLDouble(result, BIRD_RATE), rake)
        .geo(getSQLDouble(result, GEO_RATE), rake)
        .zeng(getSQLDouble(result, ZENG_RATE), rake)
        .build();
  }

  @Override
  List<String> getSQLSelectFields() {
    List<String> selectFields = super.getSQLSelectFields();

    selectFields.add(BIRD_RATE);
    selectFields.add(GEO_RATE);
    selectFields.add(GEO_RAKE);
    selectFields.add(ZENG_RATE);

    return selectFields;
  }

}
