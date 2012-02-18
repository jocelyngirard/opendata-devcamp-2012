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
public final class PoiType
    implements Serializable
{

  public static enum OpenDataSource
  {
    OpenDataSoft, DataPublica
  }

  private static final long serialVersionUID = -3416068862484418433L;

  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  @PrimaryKey
  private Key uid;

  @Persistent
  private Date creationDate;

  @Persistent
  private String openDataDataSetId;

  @Persistent
  private String openDataTypeId;

  @Persistent
  private String label;

  @Persistent
  private String poiTyperFolderUid;

  @Persistent
  private OpenDataSource dataSource;

}
