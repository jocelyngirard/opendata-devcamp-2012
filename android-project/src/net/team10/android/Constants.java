package net.team10.android;

import org.apache.http.protocol.HTTP;

import android.util.Log;

/**
 * Gathers in one place the constants of the application.
 * 
 * @author Steeve Guillaume, Edouard Mercier, Willy Noel, Jocelyn Girard
 * @since 2012.02.18
 */
public abstract class Constants
{

  /**
   * The logging level of the application and of the droid4me framework.
   */
  public static final int LOG_LEVEL = Log.DEBUG;

  /**
   * The e-mail that will receive error reports.
   */
  public static final String REPORT_LOG_RECIPIENT_EMAIL = "android@smartnsoft.com";

  /**
   * The encoding used for wrapping the URL of the HTTP requests.
   */
  public static final String WEBSERVICES_HTML_ENCODING = HTTP.UTF_8;

  public static final long WEBSERVICE_RETENTION_PERIOD_IN_MILLISECONDS = 24l * 3600l * 1000l;

  // The test environment
   public static final String API_URL = "http://10.0.2.2:8888/rest";

  // The production environment
//  public static final String API_URL = "http://reparonsparis.appspot.com/rest";

  public static final String OPEN_DATA_SOFT_URL = "http://demo2.opendatasoft.com/api/search/dataset";

  public static final String GOOGLE_MAPS_API_KEY = "0yyOboPOlEVpjLWYbx-0ZU3Nn3HwjcLHrBvSLXg";

  public static final String EMAIL_MD5_PREFERENCE_KEY = "emailMd5";

}
