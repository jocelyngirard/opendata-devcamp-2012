package net.team10.server.dao.model;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import net.team10.bo.Account;
import net.team10.bo.PoiReport;
import net.team10.bo.PoiReport.ReportKind;
import net.team10.bo.PoiReport.ReportSeverity;
import net.team10.bo.PoiReport.ReportStatus;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

/**
 * @author Édouard Mercier
 * @since 2012.02.18
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public final class PoiReportModel
    implements Serializable
{

  private static final long serialVersionUID = 1553399264484196748L;

  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  @PrimaryKey
  private Key key;

  @Persistent
  private String openDataPoiId;

  @Persistent
  private String poiTypeUid;

  @Persistent
  private String creationAccountUid;

  @Persistent
  private Date creationDate;

  @Persistent
  private Date modificationDate;

  @Persistent
  private ReportStatus reportStatus;

  @Persistent
  private String modificationAccountUid;

  @Persistent
  private ReportKind reportKind;

  @Persistent
  private ReportSeverity reportSeverity;

  @Persistent
  private Blob photo;

  public PoiReportModel()
  {
  }

  public PoiReportModel(String openDataPoiId, String poiTypeUid, String creationAccountUid, Date creationDate, Date modificationDate,
      ReportStatus reportStatus, String modificationAccountUid, ReportKind reportKind, ReportSeverity reportSeverity, Blob photo)
  {
    this.openDataPoiId = openDataPoiId;
    this.poiTypeUid = poiTypeUid;
    this.creationAccountUid = creationAccountUid;
    this.creationDate = creationDate;
    this.modificationDate = modificationDate;
    this.reportStatus = reportStatus;
    this.modificationAccountUid = modificationAccountUid;
    this.reportKind = reportKind;
    this.reportSeverity = reportSeverity;
    this.photo = photo;
  }

  public String getUid()
  {
    return KeyFactory.keyToString(key);
  }

  public final String getOpenDataPoiId()
  {
    return openDataPoiId;
  }

  public final String getPoiTypeUid()
  {
    return poiTypeUid;
  }

  public final String getCreationAccountUid()
  {
    return creationAccountUid;
  }

  public final Date getCreationDate()
  {
    return creationDate;
  }

  public final Date getModificationDate()
  {
    return modificationDate;
  }

  public final ReportStatus getReportStatus()
  {
    return reportStatus;
  }

  public final String getModificationAccountUid()
  {
    return modificationAccountUid;
  }

  public final ReportKind getReportKind()
  {
    return reportKind;
  }

  public final ReportSeverity getReportSeverity()
  {
    return reportSeverity;
  }

  public final Blob getPhoto()
  {
    return photo;
  }

  public PoiReport toPojo(Account creationAccount, Account modificationAccount)
  {
    return new PoiReport(getUid(), getOpenDataPoiId(), getPoiTypeUid(), creationAccount, getCreationDate(), getModificationDate(), getReportStatus(), modificationAccount, getReportKind(), getReportSeverity());
  }

}
