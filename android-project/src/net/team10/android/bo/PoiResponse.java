package net.team10.android.bo;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author Willy Noel
 * @since 2012.02.18
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class PoiResponse
{

  private final List<OpenDataPoi> pois;

  public PoiResponse(@JsonProperty("hits") List<OpenDataPoi> pois)
  {
    this.pois = pois;
  }

  @JsonProperty("hits")
  public List<OpenDataPoi> getPois()
  {
    return pois;
  }

}
