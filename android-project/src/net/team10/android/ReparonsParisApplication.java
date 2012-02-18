package net.team10.android;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;

import com.smartnsoft.droid4me.app.ActivityController;
import com.smartnsoft.droid4me.app.SmartApplication;
import com.smartnsoft.droid4me.bo.Business.InputAtom;
import com.smartnsoft.droid4me.cache.DbPersistence;
import com.smartnsoft.droid4me.cache.Persistence;
import com.smartnsoft.droid4me.download.BasisDownloadInstructions;
import com.smartnsoft.droid4me.download.BitmapDownloader;
import com.smartnsoft.droid4me.download.DownloadInstructions;

/**
 * The entry point of the application.
 * 
 * @author Steeve Guillaume, Edouard Mercier, Willy Noel, Jocelyn Girard
 * @since 2012.02.18
 */
public final class ReparonsParisApplication
    extends SmartApplication
{

  public static final class GoogleAccountInformations
  {
    public String userName;

    public String email;
  }

  public static class CacheInstructions
      extends DownloadInstructions.AbstractInstructions
  {

    @Override
    public InputStream getInputStream(String imageUid, Object imageSpecs, String url, BasisDownloadInstructions.InputStreamDownloadInstructor downloadInstructor)
        throws IOException
    {
      final InputAtom inputAtom = Persistence.getInstance(1).extractInputStream(url);
      return inputAtom == null ? null : inputAtom.inputStream;
    }

    @Override
    public InputStream onInputStreamDownloaded(String imageUid, Object imageSpecs, String url, InputStream inputStream)
    {
      final InputAtom inputAtom = Persistence.getInstance(1).flushInputStream(url, new InputAtom(new Date(), inputStream));
      return inputAtom == null ? null : inputAtom.inputStream;
    }

  }

  public final static DownloadInstructions.Instructions CACHE_IMAGE_INSTRUCTIONS = new ReparonsParisApplication.CacheInstructions();

  public static String md5sum(String string)
      throws NoSuchAlgorithmException
  {
    // Taken from http://www.spiration.co.uk/post/1199/Java%20md5%20example%20with%20MessageDigest
    final MessageDigest algorithm = MessageDigest.getInstance("MD5");
    byte[] defaultBytes = string.getBytes();
    algorithm.reset();
    algorithm.update(defaultBytes);
    final byte messageDigest[] = algorithm.digest();
    final StringBuffer hexString = new StringBuffer();
    for (int md5Index = 0; md5Index < messageDigest.length; md5Index++)
    {
      hexString.append(Integer.toHexString(0xFF & messageDigest[md5Index]));
    }
    final String md5String = hexString.toString();

    return md5String;
  }

  public static GoogleAccountInformations getGoogleAccountInformations(Context context)
  {
    if (Build.VERSION.SDK_INT >= 7)
    {
      final GoogleAccountInformations googleAccount = new GoogleAccountInformations();
      try
      {
        final Class<?> accountManagerClass = Class.forName("android.accounts.AccountManager");
        final Method getMethod = accountManagerClass.getMethod("get", Context.class);
        final Object accountManager = getMethod.invoke(null, context);
        final Method getAccountsByTypeMethod = accountManagerClass.getMethod("getAccountsByType", String.class);
        final Object[] accounts = (Object[]) getAccountsByTypeMethod.invoke(accountManager, "com.google");
        if (accounts.length <= 0)
        {
          return null;
        }
        final Object account = accounts[0];
        final Class<?> accountClass = Class.forName("android.accounts.Account");
        final Field nameField = accountClass.getField("name");

        googleAccount.email = (String) nameField.get(account);
        googleAccount.userName = googleAccount.email;

        return googleAccount;
      }
      catch (Exception exception)
      {
        if (log.isWarnEnabled())
        {
          log.warn("Could not access to the Google account to get the e-mail", exception);
        }
      }
    }
    return null;
  }

  @Override
  protected int getLogLevel()
  {
    return Constants.LOG_LEVEL;
  }

  @Override
  protected SmartApplication.I18N getI18N()
  {
    return new SmartApplication.I18N(getText(R.string.problem), getText(R.string.unavailableItem), getText(R.string.unavailableService), getText(R.string.connectivityProblem), getText(R.string.connectivityProblemRetry), getText(R.string.unhandledProblem), getString(R.string.applicationName), getText(R.string.dialogButton_unhandledProblem), getString(R.string.progressDialogMessage_unhandledProblem));
  }

  @Override
  protected String getLogReportRecipient()
  {
    return Constants.REPORT_LOG_RECIPIENT_EMAIL;
  }

  @Override
  public void onCreateCustom()
  {
    super.onCreateCustom();

    // We initialize the persistence
    final String directoryName = getPackageManager().getApplicationLabel(getApplicationInfo()).toString();
    final File contentsDirectory = new File(Environment.getExternalStorageDirectory(), directoryName);
    Persistence.CACHE_DIRECTORY_PATHS = new String[] { contentsDirectory.getAbsolutePath(), contentsDirectory.getAbsolutePath() };
    DbPersistence.FILE_NAMES = new String[] { DbPersistence.DEFAULT_FILE_NAME, DbPersistence.DEFAULT_FILE_NAME };
    DbPersistence.TABLE_NAMES = new String[] { "data", "images" };
    Persistence.INSTANCES_COUNT = 2;
    Persistence.IMPLEMENTATION_FQN = DbPersistence.class.getName();

    // We set the BitmapDownloader instances
    BitmapDownloader.INSTANCES_COUNT = 1;
    BitmapDownloader.MAX_MEMORY_IN_BYTES = new long[] { 3 * 1024 * 1024 };
    BitmapDownloader.LOW_LEVEL_MEMORY_WATER_MARK_IN_BYTES = new long[] { 1 * 1024 * 1024 };
  }

  @Override
  protected ActivityController.Redirector getActivityRedirector()
  {
    return new ActivityController.Redirector()
    {
      public Intent getRedirection(Activity activity)
      {
        if (ReparonsParisSplashScreenActivity.isInitialized(ReparonsParisSplashScreenActivity.class) == null)
        {
          // We re-direct to the splash screen activity if the application has not been yet initialized
          if (activity instanceof ReparonsParisSplashScreenActivity)
          {
            return null;
          }
          else
          {
            return new Intent(activity, ReparonsParisSplashScreenActivity.class);
          }
        }
        return null;
      }
    };
  }

  @Override
  protected ActivityController.Interceptor getActivityInterceptor()
  {
    final Intent homeActivityIntent = new Intent(getApplicationContext(), HomeActivity.class);
    final TitleBar titleBar = new TitleBar(homeActivityIntent, R.drawable.title_bar_home, R.style.Theme_ReparonsParis);
    return new ActivityController.Interceptor()
    {
      public void onLifeCycleEvent(Activity activity, Object component, ActivityController.Interceptor.InterceptorEvent event)
      {

        titleBar.onLifeCycleEvent(activity, event);
      }
    };
  }

  @Override
  protected ActivityController.ExceptionHandler getExceptionHandler()
  {
    return super.getExceptionHandler();
  }

}
