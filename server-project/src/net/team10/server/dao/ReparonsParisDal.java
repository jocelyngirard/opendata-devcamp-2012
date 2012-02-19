package net.team10.server.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.jdo.Query;

import net.team10.bo.Account;
import net.team10.bo.PoiReport;
import net.team10.bo.PoiReport.ReportStatus;
import net.team10.bo.PoiReportStatement;
import net.team10.bo.PoiType;
import net.team10.bo.PoiType.OpenDataSource;
import net.team10.server.dao.model.AccountModel;
import net.team10.server.dao.model.PoiReportModel;
import net.team10.server.dao.model.PoiReportStatementModel;
import net.team10.server.dao.model.PoiTypeModel;

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
        poiTypes.add(poiTypeModel.toPojo());
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
      final PoiTypeModel poiTypeModel = new PoiTypeModel(new Date(), poiType.getOpenDataDataSetId(), poiType.getOpenDataTypeId(), poiType.getLabel(), poiType.getPoiTypeFolderUid(), poiType.getOpenDataSource(), poiType.getImageUrl());
      tw.persistenceManager.makePersistent(poiTypeModel);
      tw.commit();
      return poiTypeModel.toPojo();
    }
    finally
    {
      tw.close();
    }
  }

  public List<PoiReport> getPoiReports(String openDataDataSetId, String openDataTypeId, OpenDataSource openDataSource, double topLeftLatitude,
      double topLeftLongitude, double bottomRightLatitude, double bottomRightLongitude)
  {
    final TransactionWrapper tw = new TransactionWrapper();
    try
    {
      // We first need to search for the POI type
      final String poiTypeUid;
      {
        final Query query = tw.persistenceManager.newQuery(PoiTypeModel.class);
        query.setFilter("openDataDataSetId == openDataDataSetIdParameter && openDataTypeId == openDataTypeIdParameter && openDataSource == openDataSourceParameter");
        query.declareParameters(String.class.getName() + " openDataDataSetIdParameter" + ", " + String.class.getName() + " openDataTypeIdParameter" + ", " + OpenDataSource.class.getName() + " openDataSourceParameter");
        final List<PoiTypeModel> poiTypeModels = (List<PoiTypeModel>) query.execute(openDataDataSetId, openDataTypeId, openDataSource);
        if (poiTypeModels.size() <= 0)
        {
          return null;
        }
        else
        {
          poiTypeUid = poiTypeModels.get(0).getUid();
        }
      }

      // Then, we can request the POI reports
      final List<PoiReportModel> poiReportModels;
      {
        final Query query = tw.persistenceManager.newQuery(PoiReportModel.class);
        query.setFilter("poiTypeUid == poiTypeUidParameter");
        query.declareParameters(String.class.getName() + " poiTypeUidParameter");
        poiReportModels = (List<PoiReportModel>) query.execute(poiTypeUid);
      }

      // Now, we need to request the accounts
      final Map<String, Account> accountsMap = new HashMap<String, Account>();
      if (poiReportModels.size() > 0)
      {
        final Set<String> accountUids = new HashSet<String>();
        for (PoiReportModel poiReportModel : poiReportModels)
        {
          accountUids.add(poiReportModel.getCreationAccountUid());
          accountUids.add(poiReportModel.getModificationAccountUid());
        }
        final Query query = tw.persistenceManager.newQuery(AccountModel.class);
        query.setFilter("uidParameter.contains(uid)");
        query.declareParameters(Collection.class.getName() + " uidParameter");
        final List<AccountModel> accountModels = (List<AccountModel>) query.execute(accountUids);
        for (AccountModel accountModel : accountModels)
        {
          if (accountsMap.containsKey(accountModel.getUid()) == false)
          {
            accountsMap.put(accountModel.getUid(), accountModel.toPojo());
          }
        }
      }

      final List<PoiReport> poiReports = new ArrayList<PoiReport>();
      for (PoiReportModel poiReportModel : poiReportModels)
      {
        poiReports.add(poiReportModel.toPojo(accountsMap.get(poiReportModel.getCreationAccountUid()),
            accountsMap.get(poiReportModel.getModificationAccountUid())));
      }
      return poiReports;
    }
    finally
    {
      tw.close();
    }
  }

  public List<PoiReportStatement> getPoiReportStatements(String poiReportUid)
  {
    final TransactionWrapper tw = new TransactionWrapper();
    try
    {

      final List<PoiReportStatementModel> poiReportStatementModels;
      {
        final Query query = tw.persistenceManager.newQuery(PoiReportStatementModel.class);
        query.setFilter("poiReportUid == poiReportUidParameter");
        query.declareParameters(String.class.getName() + " poiReportUidParameter");
         poiReportStatementModels = (List<PoiReportStatementModel>) query.execute(poiReportUid);
      }

      // Now, we need to request the accounts
      final Map<String, Account> accountsMap = new HashMap<String, Account>();
      if (poiReportStatementModels.size() > 0)
      {
        final Set<String> accountUids = new HashSet<String>();
        for (PoiReportStatementModel poiReportStatmentModel : poiReportStatementModels)
        {
          accountUids.add(poiReportStatmentModel.getAccountUid());
        }
        final Query query = tw.persistenceManager.newQuery(AccountModel.class);
        query.setFilter("uidParameter.contains(uid)");
        query.declareParameters(Collection.class.getName() + " uidParameter");
        final List<AccountModel> accountModels = (List<AccountModel>) query.execute(accountUids);
        for (AccountModel accountModel : accountModels)
        {
          if (accountsMap.containsKey(accountModel.getUid()) == false)
          {
            accountsMap.put(accountModel.getUid(), accountModel.toPojo());
          }
        }
      }

      final List<PoiReportStatement> poiReportStatements = new ArrayList<PoiReportStatement>();
      for (PoiReportStatementModel poiReportStatementModel : poiReportStatementModels)
      {
        poiReportStatements.add(poiReportStatementModel.toPojo(accountsMap.get(poiReportStatementModel.getAccountUid())));
      }
      return poiReportStatements;
    }
    finally
    {
      tw.close();
    }
  }

  public void declarePoiReportStatement(String accountUid, PoiReport poiReport, PoiReportStatement poiReportStatement, Blob photoBlob)
      throws BadAccountException
  {
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
        final Query query = tw.persistenceManager.newQuery(PoiReportModel.class);
        query.setFilter("openDataPoiId == openDataPoiIdParameter");
        query.declareParameters(String.class.getName() + " openDataPoiIdParameter");
        final List<PoiReportModel> poiReportModels = (List<PoiReportModel>) query.execute(poiReport.getOpenDataPoiId());
        PoiReportModel poiReportModel = null;
        for (PoiReportModel aPoiReportModel : poiReportModels)
        {
          if (aPoiReportModel.getReportStatus() != ReportStatus.Closed)
          {
            // We assume that only one non-closed POI report is available
            poiReportModel = aPoiReportModel;
            break;
          }
        }
        if (poiReportModel == null)
        {
          // No POI report is being currently not-closed for that POI
          tw.begin();
          poiReportModel = new PoiReportModel(poiReport.getOpenDataPoiId(), poiReport.getPoiTypeUid(), accountUid, new Date(), new Date(), ReportStatus.Open, accountUid, poiReport.getReportKind(), poiReport.getReportSeverity(), photoBlob);
          tw.persistenceManager.makePersistent(poiReportModel);
          tw.commit();
        }
        // Now, we can create a new POI report statement
        {
          tw.begin();
          final PoiReportStatementModel poiReportStatementModel = new PoiReportStatementModel(poiReportModel.getUid(), accountUid, new Date(), poiReportStatement.getComment());
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
