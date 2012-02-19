package net.team10.server.dao.model;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import net.team10.bo.PoiReport.ReportKind;
import net.team10.bo.PoiReport.ReportSeverity;
import net.team10.bo.PoiReport.ReportStatus;

import com.google.appengine.api.datastore.Key;

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

  public PoiReportModel()
  {
  }

  public PoiReportModel(String openDataPoiId, String poiTypeUid, String creationAccountUid, Date creationDate, Date modificationDate,
      ReportStatus reportStatus, String modificationAccountUid, ReportKind reportKind, ReportSeverity reportSeverity)
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
  }

}
