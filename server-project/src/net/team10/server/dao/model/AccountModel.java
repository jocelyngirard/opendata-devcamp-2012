package net.team10.server.dao.model;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import net.team10.bo.Account;

import com.google.appengine.api.datastore.Key;

/**
 * @author Édouard Mercier
 * @since 2012.02.18
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public final class AccountModel
    implements Serializable
{

  private static final long serialVersionUID = 4424395186363387988L;

  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  @PrimaryKey
  private Key key;

  @Persistent
  private String uid;

  @Persistent
  private Date creationDate;

  @Persistent
  private String nickname;

  public AccountModel()
  {
  }

  public AccountModel(String uid, Date creationDate, String nickname)
  {
    this.uid = uid;
    this.creationDate = creationDate;
    this.nickname = nickname;
  }

  public String getUid()
  {
    return uid;
  }

  public Date getCreationDate()
  {
    return creationDate;
  }

  public String getNickname()
  {
    return nickname;
  }

  public Account toPojo()
  {
    return new Account(getUid(), getCreationDate(), getNickname());
  }

}
