package net.team10.server.resource;

import java.util.StringTokenizer;
import java.util.logging.Level;

import net.team10.bo.PoiReport;
import net.team10.bo.PoiReportStatement;
import net.team10.bo.PoiType;
import net.team10.server.dao.ReparonsParisDal.BadAccountException;
import net.team10.server.ws.ReparonsParisServices;

import org.restlet.data.Form;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;

/**
 * @author Édouard Mercier
 * @since 2012.02.18
 */
public final class PoiReportResources
{

  public final static class PoiTypesResource
      extends ReparonsParisBasisResource
  {

    @Get
    public Representation get(Variant variant)
        throws ResourceException
    {
      if (logger.isLoggable(Level.INFO))
      {
        logger.info("Asking for the POI types");
      }
      return generateObjectJsonRepresentation(ReparonsParisServices.getInstance().getPoiTypes(), "Here are the POI types!");
    }

    @Post
    public Representation post(Representation entity)
        throws ResourceException
    {
      final PoiType poiType = deserializeJson(getPostData(entity, "poiType"), PoiType.class);
      if (logger.isLoggable(Level.INFO))
      {
        logger.info("Creating a POI type for the open-data data set id '" + poiType.getOpenDataDataSetId() + "' and type id '" + poiType.getOpenDataTypeId() + "'");
      }
      return generateObjectJsonRepresentation(ReparonsParisServices.createPoiType(poiType), "POI type created");
    }

  }

  public final static class PoiReportsResource
      extends ReparonsParisBasisResource
  {

    @Get
    public Representation get(Variant variant)
        throws ResourceException
    {
      final Form form = getRequest().getResourceRef().getQueryAsForm();
      final String openDataDataSetId = form.getFirstValue("openDataDataSetId");
      final String openDataTypeId = form.getFirstValue("openDataTypeId");
      final String openDataSource = form.getFirstValue("dataSource");
      final String poiTypeUid = form.getFirstValue("poiTypeUid");
      final String topLeft = form.getFirstValue("topLeft");
      final double topLeftLatitude;
      final double topLeftLongitude;
      {
        final StringTokenizer tokenizer = new StringTokenizer(topLeft, ",");
        topLeftLatitude = Double.parseDouble(tokenizer.nextToken());
        topLeftLongitude = Double.parseDouble(tokenizer.nextToken());
      }
      final String bottomRight = form.getFirstValue("bottomRight");
      final double bottomRightLatitude;
      final double bottomRightLongitude;
      {
        final StringTokenizer tokenizer = new StringTokenizer(bottomRight, ",");
        bottomRightLatitude = Double.parseDouble(tokenizer.nextToken());
        bottomRightLongitude = Double.parseDouble(tokenizer.nextToken());
      }
      if (logger.isLoggable(Level.INFO))
      {
        logger.info("Asking the POI reports belonging to the open-data dataset with id " + openDataDataSetId + "', open-data type id '" + openDataTypeId + "', open-data source '" + openDataSource + "' in the rectangular area (" + topLeftLatitude + "," + topLeftLongitude + ")x(" + bottomRightLatitude + "," + bottomRightLongitude + ")");
      }

      try
      {
        return generateObjectJsonRepresentation(ReparonsParisServices.getPoiReports(openDataDataSetId, openDataTypeId, openDataSource, topLeftLatitude,
            topLeftLongitude, bottomRightLatitude, bottomRightLongitude), "The POI reports");
      }
      catch (Exception exception)
      {
        throw handleException(exception, "Could not request the POI reports");
      }
    }

    @Post
    public Representation post(Representation entity)
        throws ResourceException
    {
      final Form form = getRequest().getResourceRef().getQueryAsForm();
      final String accountUid = form.getFirstValue("accountUid");
      if (logger.isLoggable(Level.INFO))
      {
        logger.info("Creating a POI report from the account with UID '" + accountUid + "'");
      }

      // // Taken from http://stackoverflow.com/questions/1513603/how-to-upload-and-store-an-image-with-google-app-engine-java
      // if (MediaType.MULTIPART_FORM_DATA.equals(entity.getMediaType(), true) == true)
      // {
      // HttpServletRequest httpServletRequest = null;
      // if (getRequest() instanceof HttpRequest)
      // {
      // final HttpCall httpCall = ((HttpRequest) getRequest()).getHttpCall();
      // if (httpCall instanceof ServletCall)
      // {
      // httpServletRequest = ((ServletCall) httpCall).getRequest();
      // }
      // }
      // if (httpServletRequest != null)
      // {
      // final ServletFileUpload upload = new ServletFileUpload();
      // final FileItemIterator iterator;
      // try
      // {
      // iterator = upload.getItemIterator(httpServletRequest);
      // Blob imageBlob = null;
      // String rawPoiReport = null;
      // String rawPoiReportStament = null;
      // while (iterator.hasNext())
      // {
      // final FileItemStream fileItemStream = iterator.next();
      // if (fileItemStream.isFormField() == false)
      // {
      // if (fileItemStream.getFieldName().equals("photo") == true)
      // {
      // imageBlob = new Blob(IOUtils.toByteArray(fileItemStream.openStream()));
      // }
      // }
      // else if (fileItemStream.getFieldName().equals("poiReport") == true)
      // {
      // final InputStream inputStream = fileItemStream.openStream();
      // rawPoiReport = new String(IOUtils.toByteArray(inputStream));
      // }
      // else if (fileItemStream.getFieldName().equals("poiReportStatement") == true)
      // {
      // rawPoiReportStament = new String(IOUtils.toByteArray(fileItemStream.openStream()));
      // }
      // }
      // // if (imageBlob != null)
      // {
      // final PoiReport poiReport = deserializeJson(rawPoiReport, PoiReport.class);
      // final PoiReportStatement poiReportStatement = deserializeJson(rawPoiReportStament, PoiReportStatement.class);
      // ReparonsParisServices.getInstance().addPoiReport(accountUid, poiReport, poiReportStatement, imageBlob);
      // return ok("POI report created");
      // }
      // }
      // catch (Exception exception)
      // {
      // throw handleException(exception, "Could not store the photo!");
      // }
      // }
      // }
      // return error("noPhotoAttached", "ko", "A POI report can only be created provided a photo is attached to it!");

      final Form postForm = new Form(entity);
      final PoiReport poiReport = deserializeJson(postForm.getFirstValue("poiReport"), PoiReport.class);
      final PoiReportStatement poiReportStatement = deserializeJson(postForm.getFirstValue("poiReportStatement"), PoiReportStatement.class);
      try
      {
        ReparonsParisServices.getInstance().declarePoiReportStatement(accountUid, poiReport, poiReportStatement, null);
        return ok("POI report created");
      }
      catch (BadAccountException exception)
      {
        throw handleDalException(exception, "Could not record a POI report");
      }
      catch (Exception exception)
      {
        throw handleException(exception, "A problem occurred while requesting the POI report");
      }
    }

  }

  public final static class PoiReportStatementsResource
      extends ReparonsParisBasisResource
  {

    @Get
    public Representation get(Variant variant)
        throws ResourceException
    {
      final String poiReportUid = (String) getRequest().getAttributes().get("poiReportUid");
      if (logger.isLoggable(Level.INFO))
      {
        logger.info("Asking the POI report statements regarding the POI report with UID " + poiReportUid + ")");
      }

      try
      {
        return generateObjectJsonRepresentation(ReparonsParisServices.getInstance().getPoiReportStatements(poiReportUid), "The POI report statements");
      }
      catch (Exception exception)
      {
        throw handleException(exception, "A problem occurred while requesting the POI report statements");
      }
    }

  }

}
