package net.team10.server.server.bo;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Key;

/**
 * @author Édouard Mercier
 * @since 2012.02.18
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public final class PoiReportStatement
    implements Serializable
{

  private static final long serialVersionUID = -1581788997439273911L;

  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  @PrimaryKey
  private Key uid;

  @Persistent
  private String poiReportUid;

  @Persistent
  private String accountUid;

  @Persistent
  private Date creationDate;

  @Persistent
  private String comment;

  @Persistent
  private Blob photo;

}
