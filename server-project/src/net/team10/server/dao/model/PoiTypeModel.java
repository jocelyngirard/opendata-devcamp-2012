package net.team10.server.dao.model;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import net.team10.bo.PoiType.OpenDataSource;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

/**
 * @author Édouard Mercier
 * @since 2012.02.18
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public final class PoiTypeModel
    implements Serializable
{

  private static final long serialVersionUID = 6149010778953321236L;

  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  @PrimaryKey
  private Key key;

  @Persistent
  private Date creationDate;

  @Persistent
  private String openDataDataSetId;

  @Persistent
  private String openDataTypeId;

  @Persistent
  private String label;

  @Persistent
  private String poiTypeFolderUid;

  @Persistent
  private OpenDataSource openDataSource;

  public PoiTypeModel()
  {
  }

  public PoiTypeModel(Date creationDate, String openDataDataSetId, String openDataTypeId, String label, String poiTypeFolderUid, OpenDataSource openDataSource)
  {
    this.creationDate = creationDate;
    this.openDataDataSetId = openDataDataSetId;
    this.openDataTypeId = openDataTypeId;
    this.label = label;
    this.poiTypeFolderUid = poiTypeFolderUid;
    this.openDataSource = openDataSource;
  }

  public String getUid()
  {
    return KeyFactory.keyToString(key);
  }

  public Date getCreationDate()
  {
    return creationDate;
  }

  public String getOpenDataDataSetId()
  {
    return openDataDataSetId;
  }

  public String getOpenDataTypeId()
  {
    return openDataTypeId;
  }

  public String getLabel()
  {
    return label;
  }

  public String getPoiTypeFolderUid()
  {
    return poiTypeFolderUid;
  }

  public OpenDataSource getOpenDataSource()
  {
    return openDataSource;
  }

}
