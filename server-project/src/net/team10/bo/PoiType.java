package net.team10.bo;

import java.io.Serializable;
import java.util.Date;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author Édouard Mercier
 * @since 2012.02.18
 */
public final class PoiType
    implements Serializable
{

  public static enum OpenDataSource
  {
    OpenDataSoft, DataPublica
  }

  private static final long serialVersionUID = -3416068862484418433L;

  private String uid;

  private Date creationDate;

  private String openDataDataSetId;

  private String openDataTypeId;

  private String label;

  private String poiTypeFolderUid;

  private OpenDataSource dataSource;

  public PoiType(@JsonProperty("uid") String uid, @JsonProperty("creationDate") Date creationDate, @JsonProperty("openDataDataSetId") String openDataDataSetId,
      @JsonProperty("openDataTypeId") String openDataTypeId, @JsonProperty("label") String label, @JsonProperty("poiTypeFolderUid") String poiTypeFolderUid,
      @JsonProperty("dataSource") OpenDataSource dataSource)
  {
    this.uid = uid;
    this.creationDate = creationDate;
    this.openDataDataSetId = openDataDataSetId;
    this.openDataTypeId = openDataTypeId;
    this.label = label;
    this.poiTypeFolderUid = poiTypeFolderUid;
    this.dataSource = dataSource;
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

  @JsonProperty("openDataDataSetId")
  public String getOpenDataDataSetId()
  {
    return openDataDataSetId;
  }

  @JsonProperty("openDataTypeId")
  public String getOpenDataTypeId()
  {
    return openDataTypeId;
  }

  @JsonProperty("label")
  public String getLabel()
  {
    return label;
  }

  @JsonProperty("poiTypeFolderUid")
  public String getPoiTypeFolderUid()
  {
    return poiTypeFolderUid;
  }

  @JsonProperty("dataSource")
  public OpenDataSource getDataSource()
  {
    return dataSource;
  }

}
