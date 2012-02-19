package net.team10.server.dao.model;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import net.team10.bo.Account;
import net.team10.bo.PoiReportStatement;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

/**
 * @author Édouard Mercier
 * @since 2012.02.18
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public final class PoiReportStatementModel
    implements Serializable
{

  private static final long serialVersionUID = -7959802392485167155L;

  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  @PrimaryKey
  private Key key;

  @Persistent
  private String poiReportUid;

  @Persistent
  private String accountUid;

  @Persistent
  private Date creationDate;

  @Persistent
  private String comment;

  public PoiReportStatementModel()
  {
  }

  public PoiReportStatementModel(String poiReportUid, String accountUid, Date creationDate, String comment)
  {
    this.poiReportUid = poiReportUid;
    this.accountUid = accountUid;
    this.creationDate = creationDate;
    this.comment = comment;
  }

  public String getUid()
  {
    return KeyFactory.keyToString(key);
  }

  public final String getPoiReportUid()
  {
    return poiReportUid;
  }

  public final String getAccountUid()
  {
    return accountUid;
  }

  public final Date getCreationDate()
  {
    return creationDate;
  }

  public final String getComment()
  {
    return comment;
  }

  public PoiReportStatement toPojo(Account account)
  {
    return new PoiReportStatement(getUid(), getPoiReportUid(), account, getCreationDate(), getComment());
  }

}
