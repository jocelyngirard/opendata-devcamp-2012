package net.team10.server.resource;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.team10.bo.PoiType;
import net.team10.bo.PoiType.OpenDataSource;
import net.team10.server.server.ReparonsParisApplication.BasisResource;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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

  public final static class PoiTypesResource
      extends BasisResource
  {

    @Get
    public Representation get(Variant variant)
        throws ResourceException
    {
      final List<PoiType> result = new ArrayList<PoiType>();
      for (int index = 0; index < 10; index++)
      {
        result.add(new PoiType("uid" + index, new Date(), "openDataDataSetId" + index, "openDataTypeId" + index, "Label " + index, null, OpenDataSource.OpenDataSoft));
      }

      final String message = "Here are the Poi types!";
      return generateCollectionJsonRepresentation(result, "poiTypes", message);
    }

    private Representation generateObjectJsonRepresentation(Object object, String message)
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

    private Representation generateCollectionJsonRepresentation(Collection<?> collection, String jsonObjectName, String message)
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

}
