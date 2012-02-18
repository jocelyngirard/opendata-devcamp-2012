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

  private final String uid;

  private final String poiReportUid;

  private final Account account;

  private final Date creationDate;

  private final String comment;

  private final String photoUrl;

  public PoiReportStatement(@JsonProperty("uid") String uid, @JsonProperty("poiReportUid") String poiReportUid, @JsonProperty("account") Account account,
      @JsonProperty("creationDate") Date creationDate, @JsonProperty("comment") String comment, @JsonProperty("photoUrl") String photoUrl)
  {
    this.uid = uid;
    this.poiReportUid = poiReportUid;
    this.account = account;
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

  @JsonProperty("account")
  public Account getAccount()
  {
    return account;
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
