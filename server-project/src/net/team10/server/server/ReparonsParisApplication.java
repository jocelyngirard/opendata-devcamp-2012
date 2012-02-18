package net.team10.server.server;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.jsr107cache.CacheException;
import net.team10.server.resource.PoiReportResources;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.EmptyRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.restlet.routing.Router;
import org.restlet.util.Template;

/**
 * The entry point of the Restlet side of the application.
 * 
 * @author Team10
 * @since 2012.02.18
 */
public final class ReparonsParisApplication
    extends Application
{

  private static final Logger logger = Logger.getLogger(ReparonsParisApplication.class.getName());

  public static class BasisResource
      extends ServerResource
  {

    protected final static double URL_FETCH_TIMEOUT_IN_SEONDS = 30d;

    private static final int UNKNOWN_PROBLEM_CODE = -1;

    private static final int JSON_PROBLEM_CODE = 1;

    private static final int CACHE_PROBLEM_CODE = 2;

    protected static final int START_PROBLEMCODE = 10;

    private final static String JSON_RESULT_FIELD = "result";

    private final static String JSON_CODE_FIELD = "code";

    private final static String JSON_STATUS_FIELD = "status";

    private final static String JSON_CONTENT_FIELD = "content";

    protected final Representation ok(String message)
        throws ResourceException
    {
      return ok(message, (JSONObject) null);
    }

    protected final Representation ok(String message, JSONArray jsonArray)
        throws ResourceException
    {
      final JSONObject result = new JSONObject();
      try
      {
        result.put(BasisResource.JSON_CODE_FIELD, "ok");
        result.put(BasisResource.JSON_RESULT_FIELD, message);
        if (jsonArray != null)
        {
          result.put(BasisResource.JSON_CONTENT_FIELD, jsonArray);
        }
      }
      catch (JSONException exception)
      {
        throw handleJsonException(exception);
      }
      return new JsonRepresentation(result);
    }

    protected final Representation ok(String message, JSONObject jsonObject)
        throws ResourceException
    {
      final JSONObject result = new JSONObject();
      try
      {
        result.put(BasisResource.JSON_CODE_FIELD, "ok");
        result.put(BasisResource.JSON_RESULT_FIELD, message);
        if (jsonObject != null)
        {
          result.put(BasisResource.JSON_CONTENT_FIELD, jsonObject);
        }
      }
      catch (JSONException exception)
      {
        throw handleJsonException(exception);
      }
      return new JsonRepresentation(result);
    }

    protected final Representation error(String code, String status, String message)
        throws ResourceException
    {
      final JSONObject jsonObject = new JSONObject();
      try
      {
        jsonObject.put(BasisResource.JSON_CODE_FIELD, code);
        jsonObject.put(BasisResource.JSON_RESULT_FIELD, message);
        if (status != null)
        {
          jsonObject.put(BasisResource.JSON_STATUS_FIELD, status);
        }
      }
      catch (JSONException exception)
      {
        throw handleJsonException(exception);
      }
      if (logger.isLoggable(Level.WARNING))
      {
        logger.warning(message);
      }
      return new JsonRepresentation(jsonObject);
    }

    protected final ResourceException handleProblem(Status status, int problemCode, String logMessage)
    {
      if (logger.isLoggable(Level.WARNING))
      {
        logger.log(Level.WARNING, logMessage);
      }
      setResponseBody(problemCode, logMessage);
      return new ResourceException(status, logMessage);
    }

    protected final ResourceException handleJsonException(JSONException exception)
    {
      if (logger.isLoggable(Level.WARNING))
      {
        logger.log(Level.WARNING, "Problem while serializing some JSON", exception);
      }
      setResponseBody(BasisResource.JSON_PROBLEM_CODE, "Problem while serializing some JSON");
      return new ResourceException(Status.SERVER_ERROR_INTERNAL, "Cannot serialize the JSON!", exception);
    }

    protected final ResourceException handleCacheException(CacheException exception)
    {
      if (logger.isLoggable(Level.WARNING))
      {
        logger.log(Level.WARNING, "Problem while using the cache", exception);
      }
      setResponseBody(BasisResource.CACHE_PROBLEM_CODE, "Problem while using the cache");
      return new ResourceException(Status.SERVER_ERROR_INTERNAL, "Cannot use properly the cache!", exception);
    }

    protected final ResourceException handleException(Exception exception, String message)
    {
      if (logger.isLoggable(Level.WARNING))
      {
        logger.log(Level.WARNING, message, exception);
      }
      setResponseBody(BasisResource.UNKNOWN_PROBLEM_CODE, message);
      return new ResourceException(Status.SERVER_ERROR_INTERNAL, message, exception);
    }

    private void setResponseBody(int problemCode, String problemMessage)
    {
      try
      {
        final JSONObject jsonObject = new JSONObject().put("code", problemCode);
        if (problemMessage != null)
        {
          jsonObject.put("message", problemMessage);
        }
        getResponse().setEntity(new JsonRepresentation(jsonObject));
      }
      catch (JSONException jsonException)
      {
        if (logger.isLoggable(Level.SEVERE))
        {
          logger.log(Level.SEVERE, "Could not embed the problem code '" + problemCode + "'", jsonException);
        }
      }
    }

  }

  public static final class NoResource
      extends ReparonsParisApplication.BasisResource
  {

    private final static int UNEXISTING_PROBLEM_CODE = 404;

    @Get
    public Representation get(Variant variant)
        throws ResourceException
    {
      throw handleProblem(Status.CLIENT_ERROR_NOT_FOUND, NoResource.UNEXISTING_PROBLEM_CODE, "The web service does not exist!");
    }

  }

  public static final class DummyResource
      extends ReparonsParisApplication.BasisResource
  {

    @Get
    public Representation get(Variant variant)
        throws ResourceException
    {
      final Form form = getRequest().getResourceRef().getQueryAsForm();
      final String arg1 = form.getFirstValue("arg1");
      final int arg2 = Integer.parseInt(form.getFirstValue("arg2"));
      return new EmptyRepresentation();
    }

  }

  @Override
  public Restlet createRoot()
  {
    // We create a router Restlet that routes each call to a resource
    final Router router = new Router(getContext());
    router.setDefaultMatchingMode(Template.MODE_STARTS_WITH);

    // We defines the default route
    router.attachDefault(ReparonsParisApplication.NoResource.class);

    // And declare a dummy route/web service
    router.attach("/dummy?{queryParameters}", ReparonsParisApplication.DummyResource.class);

    // Here are the actual services
    router.attach("/poitypes", PoiReportResources.PoiTypesResource.class);
    router.attach("/poireports?{queryParameters}", PoiReportResources.PoiReportsResource.class);

    return router;
  }

}

