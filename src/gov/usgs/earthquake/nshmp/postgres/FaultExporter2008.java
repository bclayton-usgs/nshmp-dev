package gov.usgs.earthquake.nshmp.postgres;

import static gov.usgs.earthquake.nshmp.postgres.Util.getSQLDouble;
import static gov.usgs.earthquake.nshmp.postgres.Util.Keys.DISPLACEMENT_RATE;
import static gov.usgs.earthquake.nshmp.postgres.Util.Keys.PROBABILITY_OF_ACTIVITY;
import static gov.usgs.earthquake.nshmp.postgres.Util.Keys.RAKE;
import static gov.usgs.earthquake.nshmp.postgres.Util.Keys.SLIP_RATE;
import static gov.usgs.earthquake.nshmp.postgres.Util.Keys.STATE_ABBREV;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Query the hazfaults_2008 database.
 */
public class FaultExporter2008 extends FaultExporter {

  private static final String HAZFAULTS_2008 = "hazfaults_2008";

  public FaultExporter2008(String table) throws IOException {
    super(table);
  }

  public static void main(String[] args) throws IOException {
    new FaultExporter2008(HAZFAULTS_2008).export();
  }

  @Override
  List<String> getSQLSelectFields() {
    List<String> selectFields = super.getSQLSelectFields();

    selectFields.add(DISPLACEMENT_RATE);
    selectFields.add(RAKE);
    selectFields.add(SLIP_RATE);

    return selectFields;
  }

  @Override
  List<RateModel> getRateModels(ResultSet result) throws SQLException {

    Double rake = getSQLDouble(result, RAKE);
    List<RateModel> rateModels = checkProbabilityOfActivity(result, rake);

    if (!rateModels.isEmpty()) return rateModels;

    RateModel.Builder rateModel = RateModel.builder();

    if ("CA".equals(result.getString(STATE_ABBREV).trim())) {
      rateModel.slip(getSQLDouble(result, SLIP_RATE), rake).build();
    } else {
      rateModel.geo(getSQLDouble(result, DISPLACEMENT_RATE), rake).build();
    }

    return rateModel.build();
  }

  @Override
  List<RateModel> checkProbabilityOfActivity(ResultSet result, Double rake) throws SQLException {
    Double probOfActivity = getSQLDouble(result, PROBABILITY_OF_ACTIVITY);
    RateModel.Builder rateModel = RateModel.builder();

    if (probOfActivity != null && probOfActivity < 1) {
      rateModel.aPriori(probOfActivity, getSQLDouble(result, RAKE));
    }

    return rateModel.build();
  }

}
