package net.team10.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.LayoutInflater;
import android.view.Window;

import com.smartnsoft.droid4me.LifeCycle;
import com.smartnsoft.droid4me.app.SmartSplashScreenActivity;

/**
 * The first activity displayed while the application is loading.
 * 
 * @author Steeve Guillaume, Edouard Mercier, Willy Noel, Jocelyn Girard
 * @since 2012.02.18
 */
public final class ReparonsParisSplashScreenActivity
    extends SmartSplashScreenActivity<TitleBar.TitleBarAggregate>
    implements LifeCycle.BusinessObjectsRetrievalAsynchronousPolicy, TitleBar.TitleBarDiscarded
{

  private final static int MISSING_SD_CARD_DIALOG_ID = 0;

  @Override
  protected Dialog onCreateDialog(int id)
  {
    if (id == ReparonsParisSplashScreenActivity.MISSING_SD_CARD_DIALOG_ID)
    {
      return new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle(R.string.applicationName).setMessage(
          R.string.ReparonsParisSplashScreen_dialogMessage_noSdCard).setPositiveButton(android.R.string.ok, new OnClickListener()
      {
        public void onClick(DialogInterface dialog, int which)
        {
          finish();
        }
      }).create();
    }
    return super.onCreateDialog(id);
  }

  @Override
  protected boolean requiresExternalStorage()
  {
    return true;
  }

  @Override
  protected void onNoExternalStorage()
  {
    showDialog(ReparonsParisSplashScreenActivity.MISSING_SD_CARD_DIALOG_ID);
  }

  @Override
  protected Class<? extends Activity> getNextActivity()
  {
    return HomeActivity.class;
  }

  @Override
  protected void onRetrieveDisplayObjectsCustom()
  {
    requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
    setContentView(LayoutInflater.from(this).inflate(R.layout.reparonsparis_splash_screen, null));
    setProgressBarIndeterminateVisibility(true);
  }

  @Override
  protected void onRetrieveBusinessObjectsCustom()
      throws BusinessObjectUnavailableException
  {
    try
    {
      Thread.sleep(2500);
    }
    catch (InterruptedException exception)
    {
      if (log.isErrorEnabled())
      {
        log.error("An interruption occurred while displaying the splash screen", exception);
      }
    }
    markAsInitialized();
  }

}
