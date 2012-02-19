package net.team10.server.ws;

import net.team10.bo.PoiReport;
import net.team10.bo.PoiReportStatement;
import net.team10.server.dao.ReparonsParisDal;

import com.google.appengine.api.datastore.Blob;

/**
 * @author Édouard Mercier
 * @since 2012.02.19
 */
public final class ReparonsParisServices
{

  private static ReparonsParisServices instance = null;

  private static final ReparonsParisDal reparonsParisDal = new ReparonsParisDal();

  public synchronized static final ReparonsParisServices getInstance()
  {
    if (instance == null)
    {
      instance = new ReparonsParisServices();
    }
    return instance;
  }

  public void addPoiReport(PoiReport poiReport, PoiReportStatement poiReportStatement, Blob imageBlob)
  {
    reparonsParisDal.addPoiReport(poiReport, poiReportStatement, imageBlob);
  }

}
