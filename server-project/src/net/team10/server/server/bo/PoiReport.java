package net.team10.server.server.bo;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

/**
 * @author Édouard Mercier
 * @since 2012.02.18
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
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

  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  @PrimaryKey
  private Key uid;

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

}
