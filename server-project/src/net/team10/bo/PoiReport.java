package net.team10.bo;

import java.io.Serializable;
import java.util.Date;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author Édouard Mercier
 * @since 2012.02.18
 */
public final class PoiReport
    implements Serializable
{

  private static final long serialVersionUID = -8753258797084937746L;

  public static enum ReportStatus
  {
    Open, Scheduled, InProgress, Closed
  }

  public static enum ReportKind
  {
    Broken, Stolen, Missing
  }

  public static enum ReportSeverity
  {
    Minor, Major, Severe
  }

  private final String uid;

  private final String openDataPoiId;

  private final String poiTypeUid;

  private final Account creationAccount;

  private final Date creationDate;

  private final Date modificationDate;

  private final ReportStatus reportStatus;

  private final Account modificationAccount;

  private final ReportKind reportKind;

  private final ReportSeverity reportSeverity;

  public PoiReport(@JsonProperty("uid") String uid, @JsonProperty("openDataPoiId") String openDataPoiId, @JsonProperty("poiTypeUid") String poiTypeUid,
      @JsonProperty("creationAccount") Account creationAccount, @JsonProperty("creationDate") Date creationDate,
      @JsonProperty("modificationDate") Date modificationDate, @JsonProperty("reportStatus") ReportStatus reportStatus,
      @JsonProperty("modificationAccount") Account modificationAccount, @JsonProperty("reportKind") ReportKind reportKind,
      @JsonProperty("reportSeverity") ReportSeverity reportSeverity)
  {
    this.uid = uid;
    this.openDataPoiId = openDataPoiId;
    this.poiTypeUid = poiTypeUid;
    this.creationAccount = creationAccount;
    this.creationDate = creationDate;
    this.modificationDate = modificationDate;
    this.reportStatus = reportStatus;
    this.modificationAccount = modificationAccount;
    this.reportKind = reportKind;
    this.reportSeverity = reportSeverity;
  }

  @JsonProperty("uid")
  public String getUid()
  {
    return uid;
  }

  @JsonProperty("openDataPoiId")
  public String getOpenDataPoiId()
  {
    return openDataPoiId;
  }

  @JsonProperty("poiTypeUid")
  public String getPoiTypeUid()
  {
    return poiTypeUid;
  }

  @JsonProperty("creationAccount")
  public Account getCreationAccount()
  {
    return creationAccount;
  }

  @JsonProperty("creationDate")
  public Date getCreationDate()
  {
    return creationDate;
  }

  @JsonProperty("modificationDate")
  public Date getModificationDate()
  {
    return modificationDate;
  }

  @JsonProperty("reportStatus")
  public ReportStatus getReportStatus()
  {
    return reportStatus;
  }

  @JsonProperty("modificationAccount")
  public Account getModificationAccount()
  {
    return modificationAccount;
  }

  @JsonProperty("reportKind")
  public ReportKind getReportKind()
  {
    return reportKind;
  }

  @JsonProperty("reportSeverity")
  public ReportSeverity getReportSeverity()
  {
    return reportSeverity;
  }

}
