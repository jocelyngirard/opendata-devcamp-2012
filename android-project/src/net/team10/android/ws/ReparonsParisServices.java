package net.team10.android.ws;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.List;

import net.team10.android.Constants;
import net.team10.android.bo.PoiTypesResponse;
import net.team10.bo.Account;
import net.team10.bo.Poi;
import net.team10.bo.PoiReport;
import net.team10.bo.PoiReport.ReportKind;
import net.team10.bo.PoiReport.ReportSeverity;
import net.team10.bo.PoiReportStatement;
import net.team10.bo.PoiResponse;
import net.team10.bo.PoiType;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
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

  private final BackedWSUriStreamParser.BackedUriStreamedValue<List<PoiType>, Void, JSONException, PersistenceException> poiTypeStreamParser = new BackedWSUriStreamParser.BackedUriStreamedValue<List<PoiType>, Void, JSONException, PersistenceException>(Persistence.getInstance(0), this)
  {

    public KeysAggregator<Void> computeUri(Void parameter)
    {
      return SimpleIOStreamerSourceKey.fromUriStreamerSourceKey(new HttpCallTypeAndBody(computeUri(Constants.API_URL, "poitypes", null)), null);
    }

    public List<PoiType> parse(Void parameter, InputStream inputStream)
        throws JSONException
    {
      return deserializeJson(inputStream, PoiTypesResponse.class).content;
    }

  };

  public synchronized List<PoiType> getPoiTypes()
      throws CacheException
  {
    if (log.isInfoEnabled())
    {
      log.info("Retrieving the list of POI types");
    }
    return poiTypeStreamParser.backed.getRetentionValue(true, Constants.WEBSERVICE_RETENTION_PERIOD_IN_MILLISECONDS, null, null);
  }

  private final BackedWSUriStreamParser.BackedUriStreamedValue<List<Poi>, Void, JSONException, PersistenceException> poiStreamParser = new BackedWSUriStreamParser.BackedUriStreamedValue<List<Poi>, Void, JSONException, PersistenceException>(Persistence.getInstance(0), this)
  {

    public KeysAggregator<Void> computeUri(Void parameter)
    {
      return SimpleIOStreamerSourceKey.fromUriStreamerSourceKey(
          new HttpCallTypeAndBody(Constants.OPEN_DATA_SOFT_URL + "/eclairageparis2011/?format=json&pretty_print=false&q=LEA"), null);
    }

    public List<Poi> parse(Void parameter, InputStream inputStream)
        throws JSONException
    {
      PoiResponse poiResponse = (PoiResponse) deserializeJson(inputStream, PoiResponse.class);
      return poiResponse.getPois();
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

  public void postPoiReportStatement(String accountUid, String poiTypeUid, ReportKind reportKind, ReportSeverity reportSeverity, String openDataPoiId,
      String comment, InputStream photoInputStream)
      throws CallException
  {
    if (log.isInfoEnabled())
    {
      log.info("Posting a new POI report with the account with UID '" + accountUid + "'");
    }
    final MultipartEntity multipartEntity = new MultipartEntity();
    try
    {
      multipartEntity.addPart("photo", new InputStreamBody(photoInputStream, "photo.png"));
      multipartEntity.addPart(
          "poiReport",
          new StringBody(serializeObject(new PoiReport(null, openDataPoiId, poiTypeUid, new Account(accountUid, null, null), null, null, null, null, reportKind, reportSeverity))));
      multipartEntity.addPart("poiReportStatement", new StringBody(serializeObject(new PoiReportStatement(null, null, null, null, comment, null))));
    }
    catch (Exception exception)
    {
      throw new CallException("Cannot properly encode one of the multipart parameter", exception);
    }
    deserializeJson(getInputStream(computeUri(Constants.API_URL, "poireport", null), CallType.Post, multipartEntity), String.class);
  }

  private <T> T deserializeJson(InputStream inputStream, Class<T> valueType)
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
        log.error("Error while parsing a JSON object via Jackson !", jsonParseException);
      }
    }
    catch (JsonMappingException jsonMappingException)
    {
      if (log.isErrorEnabled())
      {
        log.error("Error while mapping a JSON object via Jackson !", jsonMappingException);
      }
    }
    catch (IOException ioException)
    {
      if (log.isErrorEnabled())
      {
        log.error("I/O error while reading the JSON object via Jackson !", ioException);
      }
    }
    return null;
  }

  private String serializeObject(Object object)
      throws IOException
  {
    final ObjectMapper objectMapper = new ObjectMapper();
    final JsonFactory jacksonFactory = new JsonFactory();
    final StringWriter writer = new StringWriter();
    final JsonGenerator jsonGenerator = jacksonFactory.createJsonGenerator(writer);
    objectMapper.writeValue(jsonGenerator, object);
    return writer.toString();
  }

}
