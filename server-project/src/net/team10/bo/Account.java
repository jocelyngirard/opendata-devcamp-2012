package net.team10.bo;

import java.io.Serializable;
import java.util.Date;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author Édouard Mercier
 * @since 2012.02.18
 */
public final class Account
    implements Serializable
{

  private static final long serialVersionUID = 2490336880980037251L;

  private final String uid;

  private final Date creationDate;

  private final String nickname;

  public Account(@JsonProperty("uid") String uid, @JsonProperty("creationDate") Date creationDate, @JsonProperty("nickname") String nickname)
  {
    this.uid = uid;
    this.creationDate = creationDate;
    this.nickname = nickname;
  }

  @JsonProperty("uid")
  public String getUid()
  {
    return uid;
  }

  @JsonProperty("creationDate")
  public Date getCreationDate()
  {
    return creationDate;
  }

  @JsonProperty("nickname")
  public String getNickname()
  {
    return nickname;
  }

}
