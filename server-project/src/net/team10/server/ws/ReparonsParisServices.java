package net.team10.server.ws;

import java.util.List;

import net.team10.bo.Account;
import net.team10.bo.PoiReport;
import net.team10.bo.PoiReportStatement;
import net.team10.bo.PoiType;
import net.team10.bo.PoiType.OpenDataSource;
import net.team10.server.dao.ReparonsParisDal;
import net.team10.server.dao.ReparonsParisDal.BadAccountException;

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

  public Account createAccount(Account account)
      throws BadAccountException
  {
    return reparonsParisDal.addAccount(account);
  }

  public static PoiType createPoiType(PoiType poiType)
  {
    return reparonsParisDal.addPoiType(poiType);
  }

  public List<PoiType> getPoiTypes()
  {
    return reparonsParisDal.getPoiTypes();
  }

  public static List<PoiReport> getPoiReports(String openDataDataSetId, String openDataTypeId, String openDataSource, double topLeftLatitude,
      double topLeftLongitude, double bottomRightLatitude, double bottomRightLongitude)
  {
    return reparonsParisDal.getPoiReports(openDataDataSetId, openDataTypeId, OpenDataSource.valueOf(openDataSource), topLeftLatitude, topLeftLongitude, bottomRightLatitude,
        bottomRightLongitude);
  }

  public void declarePoiReportStatement(String accountUid, PoiReport poiReport, PoiReportStatement poiReportStatement, Blob imageBlob)
      throws BadAccountException
  {
    reparonsParisDal.declarePoiReportStatement(accountUid, poiReport, poiReportStatement, imageBlob);
  }

}
