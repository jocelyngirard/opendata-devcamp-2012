package net.team10.server.resource;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.team10.server.server.ReparonsParisApplication.BasisResource;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

abstract class ReparonsParisBasisResource
    extends BasisResource
{

  protected static final Logger logger = Logger.getLogger(ReparonsParisBasisResource.class.getName());

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