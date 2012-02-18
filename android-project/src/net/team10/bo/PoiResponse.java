package net.team10.bo;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author Willy Noel
 * @since 2012.02.18
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PoiResponse 
{
	  private final List<Poi> pois;
	  
	  public PoiResponse(@JsonProperty("hits") List<Poi> pois)
	  {
	    super();
	    this.pois = pois;
	  }

	  @JsonProperty("hits")
	  public List<Poi> getPois()
	  {
	    return pois;
	  }
	  
}
