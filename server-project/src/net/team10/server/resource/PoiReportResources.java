package net.team10.server.resource;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import net.team10.bo.Account;
import net.team10.bo.PoiReport;
import net.team10.bo.PoiReport.ReportKind;
import net.team10.bo.PoiReport.ReportSeverity;
import net.team10.bo.PoiReport.ReportStatus;
import net.team10.bo.PoiReportStatement;
import net.team10.bo.PoiType;
import net.team10.bo.PoiType.OpenDataSource;
import net.team10.server.server.ReparonsParisApplication.BasisResource;
import net.team10.server.ws.ReparonsParisServices;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.engine.http.HttpCall;
import org.restlet.engine.http.HttpRequest;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.ext.servlet.internal.ServletCall;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;

import com.google.appengine.api.datastore.Blob;

/**
 * @author Édouard Mercier
 * @since 2012.02.18
 */
public final class PoiReportResources
{

  private static final Logger logger = Logger.getLogger(PoiReportResources.class.getName());

  protected abstract static class PoiBasisResource
      extends BasisResource
  {

    protected final Representation generateObjectJsonRepresentation(Object object, String message)
        throws ResourceException
    {
      if (object instanceof Collection)
      {
        final Collection<?> collection = (Collection<?>) object;
        final JSONArray jsonArray = new JSONArray();
        for (Iterator<?> iterator = collection.iterator(); iterator.hasNext();)
        {
          final Object anObject = (Object) iterator.next();
          jsonArray.put(generateJSONObject(anObject));
        }
        return ok(message, jsonArray);
      }
      else
      {
        return ok(message, generateJSONObject(object));
      }
    }

    protected final Representation generateCollectionJsonRepresentation(Collection<?> collection, String jsonObjectName, String message)
        throws ResourceException
    {
      final JSONArray jsonArray = new JSONArray();
      for (Iterator<?> iterator = collection.iterator(); iterator.hasNext();)
      {
        final Object anObject = (Object) iterator.next();
        jsonArray.put(generateJSONObject(anObject));
      }
      final JSONObject jsonObject = new JSONObject();
      try
      {
        jsonObject.put(jsonObjectName, jsonArray);
      }
      catch (JSONException exception)
      {
        throw handleJsonException(exception);
      }
      return ok(message, jsonObject);
    }

    private JSONObject generateJSONObject(Object object)
        throws ResourceException
    {
      final ObjectMapper objectMapper = new ObjectMapper();
      final JsonFactory jacksonFactory = new JsonFactory();
      final StringWriter writer = new StringWriter();
      final JsonGenerator jsonGenerator;
      try
      {
        jsonGenerator = jacksonFactory.createJsonGenerator(writer);
      }
      catch (IOException exception)
      {
        throw handleException(exception, "Could not create a Jackson generator");
      }
      try
      {
        objectMapper.writeValue(jsonGenerator, object);
      }
      catch (Exception exception)
      {
        throw handleException(exception, "Could not serialize the business object properly through the Jackson generator");
      }
      try
      {
        return new JSONObject(writer.toString());
      }
      catch (JSONException exception)
      {
        throw handleJsonException(exception);
      }
    }

    protected <T> T deserializeJson(String json, Class<T> valueType)
    {
      final ObjectMapper objectMapper = new ObjectMapper();
      try
      {
        return objectMapper.readValue(json, valueType);
      }
      catch (JsonParseException jsonParseException)
      {
        if (logger.isLoggable(Level.SEVERE))
        {
          logger.log(Level.SEVERE, "Error while parsing a JSON object via Jackson !", jsonParseException);
        }
      }
      catch (JsonMappingException jsonMappingException)
      {
        if (logger.isLoggable(Level.SEVERE))
        {
          logger.log(Level.SEVERE, "Error while mapping a JSON object via Jackson !", jsonMappingException);
        }
      }
      catch (IOException ioException)
      {
        if (logger.isLoggable(Level.SEVERE))
        {
          logger.log(Level.SEVERE, "I/O error while reading the JSON object via Jackson !", ioException);
        }
      }
      return null;
    }

  }

  public final static class PoiTypesResource
      extends PoiBasisResource
  {

    @Get
    public Representation get(Variant variant)
        throws ResourceException
    {
      if (logger.isLoggable(Level.INFO))
      {
        logger.info("Asking the POI types");
      }
      final List<PoiType> result = new ArrayList<PoiType>();
      result.add(new PoiType("uid", new Date(), "eclairageparis2011", "LEA", "Lanterne lectrique axiale", null, OpenDataSource.OpenDataSoft));
      result.add(new PoiType("uid", new Date(), "mobilierstationnementparis2011", "HOR", "Horodateur", null, OpenDataSource.OpenDataSoft));
      for (int index = 0; index < 10; index++)
      {
        result.add(new PoiType("uid" + index, new Date(), "openDataDataSetId" + index, "openDataTypeId" + index, "Label " + index, null, OpenDataSource.OpenDataSoft));
      }

      final String message = "Here are the POI types!";
      return generateObjectJsonRepresentation(result, message);
    }

  }

  public final static class PoiReportsResource
      extends PoiBasisResource
  {

    @Get
    public Representation get(Variant variant)
        throws ResourceException
    {
      final Form form = getRequest().getResourceRef().getQueryAsForm();
      final String openDataDataSetId = form.getFirstValue("openDataDataSetId");
      final String openDataTypeId = form.getFirstValue("openDataTypeId");
      final String dataSource = form.getFirstValue("dataSource");
      final String poiTypeUid = form.getFirstValue("poiTypeUid");
      final String topLeft = form.getFirstValue("topLeft");
      final double topLeftLatitude;
      final double topLeftLongitude;
      {
        final StringTokenizer tokenizer = new StringTokenizer(",", topLeft);
        topLeftLatitude = Double.parseDouble(tokenizer.nextToken());
        topLeftLongitude = Double.parseDouble(tokenizer.nextToken());
      }
      final String bottomRight = form.getFirstValue("bottomRight");
      final double bottomRightLatitude;
      final double bottomRightLongitude;
      {
        final StringTokenizer tokenizer = new StringTokenizer(",", bottomRight);
        bottomRightLatitude = Double.parseDouble(tokenizer.nextToken());
        bottomRightLongitude = Double.parseDouble(tokenizer.nextToken());
      }
      if (logger.isLoggable(Level.INFO))
      {
        logger.info("Asking the POI reports belonging to the open-data dataset with id " + openDataDataSetId + "', open-data type id '" + openDataTypeId + "', data source '" + dataSource + "' in the rectangular area (" + topLeftLatitude + "," + topLeftLongitude + ")x(" + bottomRightLatitude + "," + bottomRightLongitude + ")");
      }
      final List<PoiReport> result = new ArrayList<PoiReport>();
      for (int index = 0; index < 10; index++)
      {
        result.add(new PoiReport("uid" + index, "openDataPoiId" + index, poiTypeUid, new Account("creationAccountUid", new Date(), "nickname " + index), new Date(), new Date(), ReportStatus.Open, new Account("modificationAccountUid", new Date(), "nickname " + index), ReportKind.Broken, ReportSeverity.Severe));
      }

      final String message = "Here are the POI reports!";
      return generateObjectJsonRepresentation(result, message);
    }

    @Post
    public Representation post(Representation entity)
        throws ResourceException
    {
      if (logger.isLoggable(Level.INFO))
      {
        logger.info("Creating a POI report");
      }

      // Taken from http://stackoverflow.com/questions/1513603/how-to-upload-and-store-an-image-with-google-app-engine-java
      if (MediaType.MULTIPART_FORM_DATA.equals(entity.getMediaType(), true) == true)
      {
        HttpServletRequest httpServletRequest = null;
        if (getRequest() instanceof HttpRequest)
        {
          final HttpCall httpCall = ((HttpRequest) getRequest()).getHttpCall();
          if (httpCall instanceof ServletCall)
          {
            httpServletRequest = ((ServletCall) httpCall).getRequest();
          }
        }
        if (httpServletRequest != null)
        {
          final ServletFileUpload upload = new ServletFileUpload();
          final FileItemIterator iterator;
          try
          {
            iterator = upload.getItemIterator(httpServletRequest);
            Blob imageBlob = null;
            while (iterator.hasNext())
            {
              final FileItemStream fileItemStream = iterator.next();
              if (fileItemStream.isFormField() == false)
              {
                if (fileItemStream.getFieldName().equals("photo") == true)
                {
                  imageBlob = new Blob(IOUtils.toByteArray(fileItemStream.openStream()));
                }
              }
            }
            if (imageBlob != null)
            {
              final PoiReport poiReport = deserializeJson(getPostData(entity, "poiReport"), PoiReport.class);
              final PoiReportStatement poiReportStatement = deserializeJson(getPostData(entity, "poiReportStament"), PoiReportStatement.class);
              ReparonsParisServices.getInstance().addPoiReport(poiReport, poiReportStatement, imageBlob);
              final JSONObject jsonObject = new JSONObject();
              return new JsonRepresentation(jsonObject);
            }
          }
          catch (Exception exception)
          {
            throw handleException(exception, "Could not store the image!");
          }
        }
        else
        {
          throw handleException(new IllegalStateException("Request cannot be turned into a servlet request"), "Could not store the image!");
        }
      }
      return error("noPhotoAttached", "ko", "A POI report can only be created provided a photo is attached to it!");
    }

  }

}
