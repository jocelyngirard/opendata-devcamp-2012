package net.team10.android.ws;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import net.team10.android.Constants;
import net.team10.bo.Poi;
import net.team10.bo.PoiResponse;
import net.team10.android.bo.PoiTypesResponse;
import net.team10.bo.PoiType;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;

import com.smartnsoft.droid4me.cache.Persistence;
import com.smartnsoft.droid4me.cache.Persistence.PersistenceException;
import com.smartnsoft.droid4me.cache.Values.CacheException;
import com.smartnsoft.droid4me.ws.WebServiceCaller;
import com.smartnsoft.droid4me.ws.WithCacheWSUriStreamParser.SimpleIOStreamerSourceKey;
import com.smartnsoft.droid4me.wscache.BackedWSUriStreamParser;

/**
 * A single point of access to the web services.
 * 
 * @author Steeve Guillaume, Edouard Mercier, Willy Noel, Jocelyn Girard
 * @since 2012.02.18
 */
public final class ReparonsParisServices
    extends WebServiceCaller
{

  private static volatile ReparonsParisServices instance;

  // We accept the "out-of-order writes" case
  public static ReparonsParisServices getInstance()
  {
    if (instance == null)
    {
      synchronized (ReparonsParisServices.class)
      {
        if (instance == null)
        {
          instance = new ReparonsParisServices();
        }
      }
    }
    return instance;
  }

  private ReparonsParisServices()
  {
  }

  @Override
  protected String getUrlEncoding()
  {
    return Constants.WEBSERVICES_HTML_ENCODING;
  }

  private Object deserializeJson(InputStream inputStream, Class<?> valueType)
  {
    final ObjectMapper objectMapper = new ObjectMapper();
    try
    {
      final String json = WebServiceCaller.getString(inputStream);
      return objectMapper.readValue(json, valueType);
    }
    catch (JsonParseException jsonParseException)
    {
      if (log.isErrorEnabled())
      {
        log.error("Error while parsing json !", jsonParseException);
      }
    }
    catch (JsonMappingException jsonMappingException)
    {
      if (log.isErrorEnabled())
      {
        log.error("Error while mapping json !", jsonMappingException);
      }
    }
    catch (IOException ioException)
    {
      if (log.isErrorEnabled())
      {
        log.error("Input/Output error while reading InputStream !", ioException);
      }
    }
    return null;
  }

  private final BackedWSUriStreamParser.BackedUriStreamedValue<List<PoiType>, Void, JSONException, PersistenceException> poiTypeStreamParser = new BackedWSUriStreamParser.BackedUriStreamedValue<List<PoiType>, Void, JSONException, PersistenceException>(Persistence.getInstance(0), this)
  {

    public KeysAggregator<Void> computeUri(Void parameter)
    {
      return SimpleIOStreamerSourceKey.fromUriStreamerSourceKey(new HttpCallTypeAndBody(Constants.API_URL + "/poitypes"), null);
    }

    public List<PoiType> parse(Void parameter, InputStream inputStream)
        throws JSONException
    {
      return ((PoiTypesResponse) deserializeJson(inputStream, PoiTypesResponse.class)).content;
    }
  };

  public synchronized List<PoiType> getPoiTypes()
      throws CacheException
  {
    if (log.isInfoEnabled())
    {
      log.info("Retrieving the list of POI types");
    }
    return null;
  }
  
  private final BackedWSUriStreamParser.BackedUriStreamedValue<List<Poi>, Void, JSONException, PersistenceException> poiStreamParser = new BackedWSUriStreamParser.BackedUriStreamedValue<List<Poi>, Void, JSONException, PersistenceException>(Persistence.getInstance(0), this)
  {

    public KeysAggregator<Void> computeUri(Void parameter)
    {
      return SimpleIOStreamerSourceKey.fromUriStreamerSourceKey(new HttpCallTypeAndBody(Constants.OPEN_DATA_SOFT_URL + "/eclairageparis2011/?format=json&pretty_print=false&q=LEA"), null);
    }

    public List<Poi> parse(Void parameter, InputStream inputStream)
        throws JSONException
    {
      PoiResponse poiResponse = (PoiResponse) deserializeJson(inputStream, PoiResponse.class);
      return  poiResponse.getPois();
    }
  };

  public synchronized List<Poi> getOpenDataPoi()
      throws CacheException
  {
    if (log.isInfoEnabled())
    {
      log.info("Retrieving the list of POI types");
    }
    return poiStreamParser.backed.getMemoryValue(true, null, null);
  }


}
