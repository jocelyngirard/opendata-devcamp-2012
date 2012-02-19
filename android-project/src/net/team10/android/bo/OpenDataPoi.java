package net.team10.android.bo;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author Willy Noel
 * @since 2012.02.18
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class OpenDataPoi
    implements Serializable
{

  private static final long serialVersionUID = -7596551429335353456L;

  private final String dataTypeId;

  private final String poiId;

  private final String label;

  private final String openDataGeo;

  private final String dataSetId;

  private final String typeId;

  public OpenDataPoi(@JsonProperty("Info") String openDataTypeId, @JsonProperty("recordid") String poiId, @JsonProperty("Libelle") String label,
      @JsonProperty("geom_x_y") String openDataGeo, @JsonProperty("geom_name") String typeId, @JsonProperty("datasetid") String dataSetId)
  {
    this.dataTypeId = openDataTypeId;
    this.poiId = poiId;
    this.label = label;
    this.openDataGeo = openDataGeo;
    this.typeId = typeId;
    this.dataSetId = dataSetId;
  }

  @JsonProperty("Info")
  public String getDataTypeId()
  {
    return dataTypeId;
  }

  @JsonProperty("geom_name")
  public String getTypeId()
  {
    return typeId;
  }

  @JsonProperty("recordid")
  public String getPoiId()
  {
    return poiId;
  }

  @JsonProperty("Libelle")
  public String getLabel()
  {
    return label;
  }

  @JsonProperty("geom_x_y")
  public String getOpenDataGeo()
  {
    return openDataGeo;
  }

  @JsonProperty("datasetid")
  public String getDataSetId()
  {
    return dataSetId;
  }

}
