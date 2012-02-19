package net.team10.android.ws;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.team10.android.Constants;
import net.team10.android.bo.OpenDataPoi;
import net.team10.android.bo.PoiResponse;
import net.team10.bo.Account;
import net.team10.bo.PoiReport;
import net.team10.bo.PoiReport.ReportKind;
import net.team10.bo.PoiReport.ReportSeverity;
import net.team10.bo.PoiReportStatement;
import net.team10.bo.PoiType;
import net.team10.bo.PoiType.OpenDataSource;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;

import com.smartnsoft.droid4me.cache.Persistence;
import com.smartnsoft.droid4me.cache.Persistence.PersistenceException;
import com.smartnsoft.droid4me.cache.Values.CacheException;
import com.smartnsoft.droid4me.ws.WSUriStreamParser.KeysAggregator;
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

  private final static class OpenDataParameters
  {

    public final String openDataDataSetId;

    public final String openDataTypeId;

    public final double latidude;

    public final double longitude;

    public final int beamInMeters;

    public OpenDataParameters(String openDataDataSetId, String openDataTypeId, double latidude, double longitude, int beamInMeters)
    {
      this.openDataDataSetId = openDataDataSetId;
      this.openDataTypeId = openDataTypeId;
      this.latidude = latidude;
      this.longitude = longitude;
      this.beamInMeters = beamInMeters;
    }

    @Override
    public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      long temp;
      temp = Double.doubleToLongBits(beamInMeters);
      result = prime * result + (int) (temp ^ (temp >>> 32));
      temp = Double.doubleToLongBits(latidude);
      result = prime * result + (int) (temp ^ (temp >>> 32));
      temp = Double.doubleToLongBits(longitude);
      result = prime * result + (int) (temp ^ (temp >>> 32));
      result = prime * result + ((openDataDataSetId == null) ? 0 : openDataDataSetId.hashCode());
      result = prime * result + ((openDataTypeId == null) ? 0 : openDataTypeId.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj)
    {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      OpenDataParameters other = (OpenDataParameters) obj;
      if (Double.doubleToLongBits(beamInMeters) != Double.doubleToLongBits(other.beamInMeters))
        return false;
      if (Double.doubleToLongBits(latidude) != Double.doubleToLongBits(other.latidude))
        return false;
      if (Double.doubleToLongBits(longitude) != Double.doubleToLongBits(other.longitude))
        return false;
      if (openDataDataSetId == null)
      {
        if (other.openDataDataSetId != null)
          return false;
      }
      else if (!openDataDataSetId.equals(other.openDataDataSetId))
        return false;
      if (openDataTypeId == null)
      {
        if (other.openDataTypeId != null)
          return false;
      }
      else if (!openDataTypeId.equals(other.openDataTypeId))
        return false;
      return true;
    }

  }

  private final static class PoiReportParameters
  {

    public final String openDataDataSetId;

    public final String openDataTypeId;

    public final OpenDataSource dataSource;

    public final String poiTypeUid;

    public final double topLeftLatitude;

    public final double topLeftLongitude;

    public final double bottomRightLatitude;

    public final double bottomRightLongitude;

    public PoiReportParameters(String openDataDataSetId, String openDataTypeId, OpenDataSource dataSource, String poiTypeUid, double topLeftLatitude,
        double topLeftLongitude, double bottomRightLatitude, double bottomRightLongitude)
    {
      this.openDataDataSetId = openDataDataSetId;
      this.openDataTypeId = openDataTypeId;
      this.dataSource = dataSource;
      this.poiTypeUid = poiTypeUid;
      this.topLeftLatitude = topLeftLatitude;
      this.topLeftLongitude = topLeftLongitude;
      this.bottomRightLatitude = bottomRightLatitude;
      this.bottomRightLongitude = bottomRightLongitude;
    }

    @Override
    public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      long temp;
      temp = Double.doubleToLongBits(bottomRightLatitude);
      result = prime * result + (int) (temp ^ (temp >>> 32));
      temp = Double.doubleToLongBits(bottomRightLongitude);
      result = prime * result + (int) (temp ^ (temp >>> 32));
      result = prime * result + ((dataSource == null) ? 0 : dataSource.hashCode());
      result = prime * result + ((openDataDataSetId == null) ? 0 : openDataDataSetId.hashCode());
      result = prime * result + ((openDataTypeId == null) ? 0 : openDataTypeId.hashCode());
      result = prime * result + ((poiTypeUid == null) ? 0 : poiTypeUid.hashCode());
      temp = Double.doubleToLongBits(topLeftLatitude);
      result = prime * result + (int) (temp ^ (temp >>> 32));
      temp = Double.doubleToLongBits(topLeftLongitude);
      result = prime * result + (int) (temp ^ (temp >>> 32));
      return result;
    }

    @Override
    public boolean equals(Object obj)
    {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      PoiReportParameters other = (PoiReportParameters) obj;
      if (Double.doubleToLongBits(bottomRightLatitude) != Double.doubleToLongBits(other.bottomRightLatitude))
        return false;
      if (Double.doubleToLongBits(bottomRightLongitude) != Double.doubleToLongBits(other.bottomRightLongitude))
        return false;
      if (dataSource != other.dataSource)
        return false;
      if (openDataDataSetId == null)
      {
        if (other.openDataDataSetId != null)
          return false;
      }
      else if (!openDataDataSetId.equals(other.openDataDataSetId))
        return false;
      if (openDataTypeId == null)
      {
        if (other.openDataTypeId != null)
          return false;
      }
      else if (!openDataTypeId.equals(other.openDataTypeId))
        return false;
      if (poiTypeUid == null)
      {
        if (other.poiTypeUid != null)
          return false;
      }
      else if (!poiTypeUid.equals(other.poiTypeUid))
        return false;
      if (Double.doubleToLongBits(topLeftLatitude) != Double.doubleToLongBits(other.topLeftLatitude))
        return false;
      if (Double.doubleToLongBits(topLeftLongitude) != Double.doubleToLongBits(other.topLeftLongitude))
        return false;
      return true;
    }

  }

  public static class JsonTemplateResponse<ContentType>
  {

    public String code;

    public String result;

    public ContentType content;

  }

  public final static class PostReportResponse
      extends JsonTemplateResponse<Void>
  {
  }

  public final static class PoiReportsResponse
      extends JsonTemplateResponse<List<PoiReport>>
  {
  }

  public final static class AccountResponse
      extends JsonTemplateResponse<Account>
  {
  }

  public final static class PoiTypesResponse
      extends JsonTemplateResponse<List<PoiType>>
  {
  }

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

  public List<OpenDataPoi> getOpenDataPois(String openDataDataSetId, String openDataTypeId, double latitude, double longitude, int beamInMeters)
      throws CacheException
  {
    if (log.isInfoEnabled())
    {
      log.info("Retrieving the list of open-data POIs");
    }
    return poisStreamParser.backed.getMemoryValue(true, null, new OpenDataParameters(openDataDataSetId, openDataTypeId, latitude, longitude, beamInMeters));
  }

  public Account createAccount(String accountUid, String nickname)
      throws CallException
  {
    if (log.isInfoEnabled())
    {
      log.info("Creating an account creation with the account UID '" + accountUid + "' and nickname '" + nickname + "'");
    }
    final List<NameValuePair> postParams = new ArrayList<NameValuePair>();
    final UrlEncodedFormEntity entity;
    try
    {
      postParams.add(new BasicNameValuePair("account", serializeObject(new Account(accountUid, null, nickname))));
      entity = new UrlEncodedFormEntity(postParams);
    }
    catch (Exception exception)
    {
      throw new CallException("Cannot properly encode one of the multipart parameter", exception);
    }
    return deserializeJson(getInputStream(computeUri(Constants.API_URL, "account", null), CallType.Post, entity), AccountResponse.class).content;
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

  public synchronized List<PoiType> getPoiTypes(boolean fromCache)
      throws CacheException
  {
    if (log.isInfoEnabled())
    {
      log.info("Retrieving the list of POI types");
    }
    return poiTypeStreamParser.backed.getRetentionValue(fromCache, Constants.WEBSERVICE_RETENTION_PERIOD_IN_MILLISECONDS, null, null);
  }

  private final BackedWSUriStreamParser.BackedUriStreamedMap<List<PoiReport>, PoiReportParameters, JSONException, PersistenceException> poiReportsStreamParser = new BackedWSUriStreamParser.BackedUriStreamedMap<List<PoiReport>, PoiReportParameters, JSONException, PersistenceException>(Persistence.getInstance(0), this)
  {

    public KeysAggregator<PoiReportParameters> computeUri(PoiReportParameters parameter)
    {
      final Map<String, String> uriParameters = new HashMap<String, String>();
      uriParameters.put("openDataDataSetId", parameter.openDataDataSetId);
      uriParameters.put("openDataTypeId", parameter.openDataTypeId);
      uriParameters.put("dataSource", parameter.dataSource.name());
      uriParameters.put("poiTypeUid", parameter.poiTypeUid);
      uriParameters.put("topLeft", Double.toString(parameter.topLeftLatitude) + "," + Double.toString(parameter.topLeftLongitude));
      uriParameters.put("bottomRight", Double.toString(parameter.bottomRightLatitude) + "," + Double.toString(parameter.bottomRightLongitude));
      return SimpleIOStreamerSourceKey.fromUriStreamerSourceKey(new HttpCallTypeAndBody(computeUri(Constants.API_URL, "poireports", uriParameters)), parameter);
    }

    public List<PoiReport> parse(PoiReportParameters parameter, InputStream inputStream)
        throws JSONException
    {
      return deserializeJson(inputStream, PoiReportsResponse.class).content;
    }

  };

  public synchronized List<PoiReport> getPoiReports(boolean fromCache, String openDataDataSetId, String openDataTypeId, OpenDataSource openDataSource,
      String poiTypeUid, double topLeftLatitude, double topLeftLongitude, double bottomRightLatitude, double bottomRightLongitude)
      throws CacheException
  {
    if (log.isInfoEnabled())
    {
      log.info("Retrieving the list of POI Reports");
    }
    return poiReportsStreamParser.backed.getRetentionValue(
        fromCache,
        Constants.WEBSERVICE_RETENTION_PERIOD_IN_MILLISECONDS,
        null,
        new PoiReportParameters(openDataDataSetId, openDataTypeId, openDataSource, poiTypeUid, topLeftLatitude, topLeftLongitude, bottomRightLatitude, bottomRightLongitude));
  }

  private final BackedWSUriStreamParser.BackedUriStreamedMap<List<OpenDataPoi>, OpenDataParameters, JSONException, PersistenceException> poisStreamParser = new BackedWSUriStreamParser.BackedUriStreamedMap<List<OpenDataPoi>, OpenDataParameters, JSONException, PersistenceException>(Persistence.getInstance(0), this)
  {

    public KeysAggregator<OpenDataParameters> computeUri(OpenDataParameters parameter)
    {
      final Map<String, String> uriParameters = new HashMap<String, String>();
      uriParameters.put("format", "json");
      uriParameters.put("pretty_print", "false");
      // uriParameters.put("disp", "geo");
      uriParameters.put("location", parameter.latidude + "," + parameter.longitude);
      uriParameters.put("distance", Integer.toString(parameter.beamInMeters));
      return SimpleIOStreamerSourceKey.fromUriStreamerSourceKey(
          new HttpCallTypeAndBody(computeUri(Constants.OPEN_DATA_SOFT_URL, parameter.openDataDataSetId, uriParameters)), null);
    }

    public List<OpenDataPoi> parse(OpenDataParameters parameter, InputStream inputStream)
        throws JSONException
    {
      return deserializeJson(inputStream, PoiResponse.class).getPois();
    }

  };

  public void postPoiReportStatement(String accountUid, String poiTypeUid, ReportKind reportKind, ReportSeverity reportSeverity, String openDataPoiId,
      String comment, InputStream photoInputStream)
      throws CallException
  {
    if (log.isInfoEnabled())
    {
      log.info("Posting a new POI report with the account with UID '" + accountUid + "'");
    }

    // final MultipartEntity multipartEntity = new MultipartEntity();
    // try
    // {
    // if (photoInputStream != null)
    // {
    // multipartEntity.addPart("photo", new InputStreamBody(photoInputStream, "photo.png"));
    // }
    // multipartEntity.addPart(
    // "poiReport",
    // new StringBody(serializeObject(new PoiReport(null, openDataPoiId, poiTypeUid, new Account(accountUid, null, null), null, null, null, null,
    // reportKind, reportSeverity))));
    // multipartEntity.addPart("poiReportStatement", new StringBody(serializeObject(new PoiReportStatement(null, null, null, null, comment, null))));
    // }
    // catch (Exception exception)
    // {
    // throw new CallException("Cannot properly encode one of the multipart parameter", exception);
    // }

    final List<NameValuePair> postParams = new ArrayList<NameValuePair>();
    final UrlEncodedFormEntity entity;
    try
    {
      postParams.add(new BasicNameValuePair("poiReport", serializeObject(new PoiReport(null, openDataPoiId, poiTypeUid, new Account(accountUid, null, null), null, null, null, null, reportKind, reportSeverity))));
      postParams.add(new BasicNameValuePair("poiReportStatement", serializeObject(new PoiReportStatement(null, null, null, null, comment, null))));
      entity = new UrlEncodedFormEntity(postParams);
    }
    catch (Exception exception)
    {
      throw new CallException("Cannot properly encode one of the multipart parameter", exception);
    }

    final Map<String, String> uriParameters = new HashMap<String, String>();
    uriParameters.put("accountUid", accountUid);
    deserializeJson(getInputStream(computeUri(Constants.API_URL, "poireport", uriParameters), CallType.Post, entity), PostReportResponse.class);
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
