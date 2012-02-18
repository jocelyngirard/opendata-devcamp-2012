package net.team10.bo;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author Willy Noel
 * @since 2012.02.18
 */

public class Poi
    implements Serializable
{

  private static final long serialVersionUID = -7596551429335353456L;

  private final String openDataTypeId;

  private final String recordId;

  private final String label;

  private final String openDataGeo;

  private final String openDataSetId;

  private String geoName;

  public Poi(@JsonProperty("Info") String openDataTypeId, @JsonProperty("recordid") String recordId, @JsonProperty("Libelle") String label,
      @JsonProperty("geom_x_y") String openDataGeo, @JsonProperty("geom_name") String geoName, @JsonProperty("datasetid") String openDataSetId)
  {
    super();
    this.openDataTypeId = openDataTypeId;
    this.recordId = recordId;
    this.label = label;
    this.openDataGeo = openDataGeo;
    this.openDataSetId = openDataSetId;
  }

  @JsonProperty("Info")
  public String getOpenDataTypeId()
  {
    return openDataTypeId;
  }

  @JsonProperty("geom_name")
  public String getGeoName()
  {
    return geoName;
  }

  @JsonProperty("recordid")
  public String getRecordId()
  {
    return recordId;
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
  public String getOpenDataSetId()
  {
    return openDataSetId;
  }

}
