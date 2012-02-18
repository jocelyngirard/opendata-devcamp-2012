package net.team10.server.resource;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.team10.bo.Account;
import net.team10.bo.PoiReport;
import net.team10.bo.PoiReport.ReportKind;
import net.team10.bo.PoiReport.ReportSeverity;
import net.team10.bo.PoiReport.ReportStatus;
import net.team10.bo.PoiType;
import net.team10.bo.PoiType.OpenDataSource;
import net.team10.server.server.ReparonsParisApplication.BasisResource;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Form;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;

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
      final String bottomRight = form.getFirstValue("bottomRight");
      if (logger.isLoggable(Level.INFO))
      {
        logger.info("Asking the POI reports belonging to the dataset (" + openDataDataSetId + "," + openDataTypeId + ")");
      }
      final List<PoiReport> result = new ArrayList<PoiReport>();
      for (int index = 0; index < 10; index++)
      {
        result.add(new PoiReport("uid" + index, "openDataPoiId" + index, poiTypeUid, new Account("creationAccountUid", new Date(), "nickname " + index), new Date(), new Date(), ReportStatus.Open, new Account("modificationAccountUid", new Date(), "nickname " + index), ReportKind.Broken, ReportSeverity.Severe));
      }

      final String message = "Here are the POI reports!";
      return generateCollectionJsonRepresentation(result, "poiTypes", message);
    }

  }

}
