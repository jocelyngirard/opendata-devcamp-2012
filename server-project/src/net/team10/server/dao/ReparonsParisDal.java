package net.team10.server.dao;

import java.util.List;

import javax.jdo.Query;

import net.team10.bo.PoiReport;
import net.team10.bo.PoiReportStatement;
import net.team10.server.dao.BasisDal.TransactionWrapper;
import net.team10.server.dao.model.PoiReportModel;

import com.google.appengine.api.datastore.Blob;

/**
 * @author Édouard Mercier
 * @since 2012.02.19
 */
public final class ReparonsParisDal
    extends BasisDal
{

  public void addPoiReport(PoiReport poiReport, PoiReportStatement poiReportStatement, Blob photoBlob)
  {
    final TransactionWrapper tw = new TransactionWrapper();
    try
    {
      final Query query = tw.persistenceManager.newQuery(PoiReportModel.class);
      query.setFilter("openDataPoiId == openDataPoiIdParameter");
      query.declareParameters(String.class.getName() + " openDataPoiIdParameter");
      final List<PoiReportModel> applicationModels = (List<PoiReportModel>) query.execute(poiReport.getOpenDataPoiId());
    }
    finally
    {
      tw.close();
    }
  }
  
}
