package net.team10.server.resource;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;

import net.team10.bo.Account;
import net.team10.bo.PoiReport;
import net.team10.bo.PoiReport.ReportKind;
import net.team10.bo.PoiReport.ReportSeverity;
import net.team10.bo.PoiReport.ReportStatus;
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
      // final List<PoiType> result = new ArrayList<PoiType>();
      // result.add(new PoiType("uid", new Date(), "eclairageparis2011", "LEA", "Lanterne électrique axiale", null, OpenDataSource.OpenDataSoft));
      // result.add(new PoiType("uid", new Date(), "mobilierstationnementparis2011", "HOR", "Horodateur", null, OpenDataSource.OpenDataSoft));
      // for (int index = 0; index < 10; index++)
      // {
      // result.add(new PoiType("uid" + index, new Date(), "openDataDataSetId" + index, "openDataTypeId" + index, "Label " + index, null,
      // OpenDataSource.OpenDataSoft));
      // }

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
      final Form form = getRequest().getResourceRef().getQueryAsForm();
      final String accountUid = form.getFirstValue("accountUid");
      if (logger.isLoggable(Level.INFO))
      {
        logger.info("Creating a POI report from the account with UID '" + accountUid + "'");
      }

      // Taken from http://stackoverflow.com/questions/1513603/how-to-upload-and-store-an-image-with-google-app-engine-java
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
      // if (logger.isLoggable(Level.FINE))
      // {
      // logger.fine("Deserializing the photo");
      // }
      // imageBlob = new Blob(IOUtils.toByteArray(fileItemStream.openStream()));
      // }
      // }
      // else if (fileItemStream.getFieldName().equals("poiReport") == true)
      // {
      // if (logger.isLoggable(Level.FINE))
      // {
      // logger.fine("Deserializing the POI report");
      // }
      // final InputStream inputStream = fileItemStream.openStream();
      // rawPoiReport = new String(IOUtils.toByteArray(inputStream));
      // }
      // else if (fileItemStream.getFieldName().equals("poiReportStatement") == true)
      // {
      // if (logger.isLoggable(Level.FINE))
      // {
      // logger.fine("Deserializing the POI report statement");
      // }
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
      // if (logger.isLoggable(Level.FINE))
      // {
      // logger.log(Level.FINE, "Cannot ---> 1");
      // }
      // throw handleException(exception, "Could not store the photo!");
      // }
      // }
      // }
      // if (logger.isLoggable(Level.FINE))
      // {
      // logger.log(Level.FINE, "Cannot ---> 2");
      // }
      //
      // if (logger.isLoggable(Level.FINE))
      // {
      // logger.log(Level.FINE, "Cannot ---> 3");
      // }
      // return error("noPhotoAttached", "ko", "A POI report can only be created provided a photo is attached to it!");

      final PoiReport poiReport = deserializeJson(getPostData(entity, "poiReport"), PoiReport.class);
      final PoiReportStatement poiReportStatement = deserializeJson(getPostData(entity, "poiReportStatement"), PoiReportStatement.class);
      try
      {
        ReparonsParisServices.getInstance().addPoiReport(accountUid, poiReport, poiReportStatement, null);
        return ok("POI report created");
      }
      catch (BadAccountException exception)
      {
        throw handleDalException(exception, "Could not record a POI report");
      }
    }

  }

}
