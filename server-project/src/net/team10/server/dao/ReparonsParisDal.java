package net.team10.server.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.Query;

import net.team10.bo.Account;
import net.team10.bo.PoiReport;
import net.team10.bo.PoiReport.ReportStatus;
import net.team10.bo.PoiReportStatement;
import net.team10.bo.PoiType;
import net.team10.server.dao.model.AccountModel;
import net.team10.server.dao.model.PoiReportModel;
import net.team10.server.dao.model.PoiReportStatementModel;
import net.team10.server.dao.model.PoiTypeModel;
import net.team10.server.server.ReparonsParisApplication;

import com.google.appengine.api.datastore.Blob;

/**
 * @author Édouard Mercier
 * @since 2012.02.19
 */
public final class ReparonsParisDal
    extends BasisDal
{

  private static final Logger logger = Logger.getLogger(ReparonsParisDal.class.getName());

  public final static class BadAccountException
      extends DalException
  {

    private static final long serialVersionUID = -4067869298365407030L;

    public BadAccountException(String message, String status)
    {
      super(message, status);
    }

  }

  public Account addAccount(Account account)
      throws BadAccountException
  {
    final TransactionWrapper tw = new TransactionWrapper();
    try
    {
      final List<AccountModel> accountsModels = getAccounts(account.getUid(), tw);
      if (accountsModels.size() >= 1)
      {
        throw new BadAccountException("Account with UID '" + account.getUid() + "' already exists!", "AlreadyExistingAccount");
      }
      // Now, we can create the account
      tw.begin();
      final AccountModel accountModel = new AccountModel(account.getUid(), new Date(), account.getNickname());
      tw.persistenceManager.makePersistent(accountModel);
      tw.commit();
      return new Account(accountModel.getUid(), accountModel.getCreationDate(), accountModel.getNickname());
    }
    finally
    {
      tw.close();
    }
  }

  public List<PoiType> getPoiTypes()
  {
    final TransactionWrapper tw = new TransactionWrapper();
    try
    {
      final Query query = tw.persistenceManager.newQuery(PoiTypeModel.class);
      final List<PoiTypeModel> poiTypeModels = (List<PoiTypeModel>) query.execute();
      final List<PoiType> poiTypes = new ArrayList<PoiType>();
      for (PoiTypeModel poiTypeModel : poiTypeModels)
      {
        poiTypes.add(new PoiType(poiTypeModel.getUid(), poiTypeModel.getCreationDate(), poiTypeModel.getOpenDataDataSetId(), poiTypeModel.getOpenDataTypeId(), poiTypeModel.getLabel(), poiTypeModel.getPoiTypeFolderUid(), poiTypeModel.getOpenDataSource()));
      }
      return poiTypes;
    }
    finally
    {
      tw.close();
    }
  }

  public PoiType addPoiType(PoiType poiType)
  {
    final TransactionWrapper tw = new TransactionWrapper();
    try
    {
      tw.begin();
      final PoiTypeModel poiTypeModel = new PoiTypeModel(new Date(), poiType.getOpenDataDataSetId(), poiType.getOpenDataTypeId(), poiType.getLabel(), poiType.getPoiTypeFolderUid(), poiType.getOpenDataSource());
      tw.persistenceManager.makePersistent(poiTypeModel);
      tw.commit();
      return new PoiType(poiTypeModel.getUid(), poiTypeModel.getCreationDate(), poiTypeModel.getOpenDataDataSetId(), poiTypeModel.getOpenDataTypeId(), poiTypeModel.getLabel(), poiTypeModel.getPoiTypeFolderUid(), poiTypeModel.getOpenDataSource());
    }
    finally
    {
      tw.close();
    }
  }

  public void addPoiReport(String accountUid, PoiReport poiReport, PoiReportStatement poiReportStatement, Blob photoBlob)
      throws BadAccountException
  {
    if (logger.isLoggable(Level.INFO))
    {
      logger.log(Level.INFO, "Declaring a new POI report");
    }
    final TransactionWrapper tw = new TransactionWrapper();
    try
    {
      // We need to check for the account existence
      {
        final List<AccountModel> accountsModels = getAccounts(accountUid, tw);
        if (accountsModels.size() <= 0)
        {
          throw new BadAccountException("No account with UID '" + accountUid + "' exists!", "InexistentAccount");
        }
      }
      {
//        final Query query = tw.persistenceManager.newQuery(PoiReportModel.class);
//        query.setFilter("openDataPoiId == openDataPoiIdParameter && reportStatusParameter.contains(reportStatus)");
//        query.declareParameters(String.class.getName() + " openDataPoiIdParameter" + ", " + Collection.class.getName() + " reportStatusParameter");
//        final List<ReportStatus> notClosedStatuses = new ArrayList<ReportStatus>();
//        for (ReportStatus reportStatus : ReportStatus.values())
//        {
//          if (reportStatus != ReportStatus.InProgress)
//          {
//            notClosedStatuses.add(reportStatus);
//          }
//        }
        final List<PoiReportModel> poiReportModels = null;// = (List<PoiReportModel>) query.execute(poiReport.getOpenDataPoiId(), notClosedStatuses);
        final PoiReportModel poiReportModel;
        if (poiReportModels == null || poiReportModels.size() <= 0)
        {
          // No POI report is being currently not-closed for that POI
          tw.begin();
          poiReportModel = new PoiReportModel(poiReport.getOpenDataPoiId(), poiReport.getPoiTypeUid(), accountUid, new Date(), new Date(), poiReport.getReportStatus(), accountUid, poiReport.getReportKind(), poiReport.getReportSeverity());
          tw.persistenceManager.makePersistent(poiReportModel);
          tw.commit();
        }
        else
        {
          // We assume that only one non-closed POI report is available
          poiReportModel = poiReportModels.get(0);
        }
        // Now, we can create a new POI report statement
        {
          tw.begin();
          final PoiReportStatementModel poiReportStatementModel = new PoiReportStatementModel(poiReport.getUid(), accountUid, new Date(), poiReportStatement.getComment(), photoBlob);
          tw.persistenceManager.makePersistent(poiReportStatementModel);
          tw.commit();
        }
      }
    }
    finally
    {
      tw.close();
    }
  }

  private List<AccountModel> getAccounts(String accountUid, TransactionWrapper tw)
  {
    final Query query = tw.persistenceManager.newQuery(AccountModel.class);
    query.setFilter("uid == uidParameter");
    query.declareParameters(String.class.getName() + " uidParameter");
    final List<AccountModel> accountsModels = (List<AccountModel>) query.execute(accountUid);
    return accountsModels;
  }

}
