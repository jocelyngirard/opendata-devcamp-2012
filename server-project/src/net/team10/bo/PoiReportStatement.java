package net.team10.bo;

import java.io.Serializable;
import java.util.Date;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author Édouard Mercier
 * @since 2012.02.18
 */
public final class PoiReportStatement
    implements Serializable
{

  private static final long serialVersionUID = -1581788997439273911L;

  private String uid;

  private String poiReportUid;

  private String accountUid;

  private Date creationDate;

  private String comment;

  private String photoUrl;

  public PoiReportStatement(@JsonProperty("uid") String uid, @JsonProperty("poiReportUid") String poiReportUid, @JsonProperty("accountUid") String accountUid,
      @JsonProperty("creationDate") Date creationDate, @JsonProperty("comment") String comment, @JsonProperty("photoUrl") String photoUrl)
  {
    this.uid = uid;
    this.poiReportUid = poiReportUid;
    this.accountUid = accountUid;
    this.creationDate = creationDate;
    this.comment = comment;
    this.photoUrl = photoUrl;
  }

  @JsonProperty("uid")
  public String getUid()
  {
    return uid;
  }

  @JsonProperty("poiReportUid")
  public String getPoiReportUid()
  {
    return poiReportUid;
  }

  @JsonProperty("accountUid")
  public String getAccountUid()
  {
    return accountUid;
  }

  @JsonProperty("creationDate")
  public Date getCreationDate()
  {
    return creationDate;
  }

  @JsonProperty("comment")
  public String getComment()
  {
    return comment;
  }

  @JsonProperty("photoUrl")
  public String getPhotoUrl()
  {
    return photoUrl;
  }

}
