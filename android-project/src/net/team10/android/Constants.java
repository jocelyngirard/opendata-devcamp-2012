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
  public static final String WEBSERVICES_HTML_ENCODING = HTTP.ISO_8859_1;

  public static final long WEBSERVICE_RETENTION_PERIOD_IN_MILLISECONDS = 24l * 3600l * 1000l;

  public static final String API_URL = "http://reparonsparis.appspot.com/rest";

  public static final String GOOGLE_MAPS_API_KEY = "0yyOboPOlEVpjLWYbx-0ZU3Nn3HwjcLHrBvSLXg";

}
