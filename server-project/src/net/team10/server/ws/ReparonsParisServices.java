package net.team10.server.ws;

import java.util.Collections;
import java.util.Comparator;
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
    final List<PoiType> poiTypes = reparonsParisDal.getPoiTypes();
    Collections.sort(poiTypes, new Comparator<PoiType>()
    {
      public int compare(PoiType object1, PoiType object2)
      {
        if (object1.getOpenDataDataSetId().equals(object2.getOpenDataDataSetId()) == true)
        {
          if (object1.getOpenDataTypeId() == null && object2.getOpenDataTypeId() != null)
          {
            return 1;
          }
          else if (object1.getOpenDataTypeId() != null && object2.getOpenDataTypeId() == null)
          {
            return -1;
          }
          else
          {
            return object1.getOpenDataTypeId().compareTo(object2.getOpenDataTypeId());
          }
        }
        return object1.getOpenDataDataSetId().compareTo(object2.getOpenDataDataSetId());
      }
    });
    return poiTypes;
  }

  public static List<PoiReport> getPoiReports(String openDataDataSetId, String openDataTypeId, String openDataSource, double topLeftLatitude,
      double topLeftLongitude, double bottomRightLatitude, double bottomRightLongitude)
  {
    return reparonsParisDal.getPoiReports(openDataDataSetId, openDataTypeId, OpenDataSource.valueOf(openDataSource), topLeftLatitude, topLeftLongitude,
        bottomRightLatitude, bottomRightLongitude);
  }

  public List<PoiReportStatement> getPoiReportStatements(String poiReportUid)
  {
    final List<PoiReportStatement> poiReportStatements = reparonsParisDal.getPoiReportStatements(poiReportUid);
    Collections.sort(poiReportStatements, new Comparator<PoiReportStatement>()
    {
      public int compare(PoiReportStatement object1, PoiReportStatement object2)
      {
        if (object1.getCreationDate().before(object2.getCreationDate()) == true)
        {
          return 1;
        }
        else if (object1.getCreationDate().before(object2.getCreationDate()) == false)
        {
          return -1;
        }
        return object1.getAccount().getNickname().compareTo(object2.getAccount().getNickname());
      }
    });
    return poiReportStatements;
  }

  public void declarePoiReportStatement(String accountUid, PoiReport poiReport, PoiReportStatement poiReportStatement, Blob imageBlob)
      throws BadAccountException
  {
    reparonsParisDal.declarePoiReportStatement(accountUid, poiReport, poiReportStatement, imageBlob);
  }

}
