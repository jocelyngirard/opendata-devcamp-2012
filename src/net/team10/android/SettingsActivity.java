package net.team10.android;

import android.content.pm.PackageManager.NameNotFoundException;
import android.preference.Preference;

import com.smartnsoft.droid4me.app.SmartPreferenceActivity;

/**
 * The activity which enables to tune the application.
 * 
 * @author Steeve Guillaume, Édouard Mercier, Willy Noel, Jocelyn Girard
 * @since 2012.02.18
 */
public final class SettingsActivity
    extends SmartPreferenceActivity<TitleBar.TitleBarAggregate>
    implements TitleBar.TitleBarShowHomeFeature
{

  public void onRetrieveDisplayObjects()
  {
    addPreferencesFromResource(R.xml.settings);
    {
      final Preference versionPreference = findPreference("version");
      try
      {
        versionPreference.setSummary(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
      }
      catch (NameNotFoundException exception)
      {
        if (log.isErrorEnabled())
        {
          log.error("Cannot determine the application version name", exception);
        }
        versionPreference.setSummary("???");
      }
    }
  }

}
