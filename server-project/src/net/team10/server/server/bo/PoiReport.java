package net.team10.server.server.bo;

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

  private String uid;

  private String poiTypeUid;

  private String creationAccountUid;

  private Date creationDate;

  private Date modificationDate;

  private ReportStatus reportStatus;

  private String modificationAccountUid;

  private ReportKind reportKind;

  private ReportSeverity reportSeverity;

  public PoiReport(@JsonProperty("uid") String uid, @JsonProperty("poiTypeUid") String poiTypeUid,
      @JsonProperty("creationAccountUid") String creationAccountUid, @JsonProperty("creationDate") Date creationDate,
      @JsonProperty("modificationDate") Date modificationDate, @JsonProperty("reportStatus") ReportStatus reportStatus,
      @JsonProperty("modificationAccountId") String modificationAccountUid, @JsonProperty("reportKind") ReportKind reportKind,
      @JsonProperty("reportSeverity") ReportSeverity reportSeverity)
  {
    this.uid = uid;
    this.poiTypeUid = poiTypeUid;
    this.creationAccountUid = creationAccountUid;
    this.creationDate = creationDate;
    this.modificationDate = modificationDate;
    this.reportStatus = reportStatus;
    this.modificationAccountUid = modificationAccountUid;
    this.reportKind = reportKind;
    this.reportSeverity = reportSeverity;
  }

  @JsonProperty("uid")
  public String getUid()
  {
    return uid;
  }

  @JsonProperty("poiTypeUid")
  public String getPoiTypeUid()
  {
    return poiTypeUid;
  }

  @JsonProperty("creationAccountUid")
  public String getCreationAccountUid()
  {
    return creationAccountUid;
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

  @JsonProperty("modificationAccountId")
  public String getModificationAccountUid()
  {
    return modificationAccountUid;
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
