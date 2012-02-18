package net.team10.android.fragments;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import net.team10.android.AboutActivity;
import net.team10.android.Constants;
import net.team10.android.PoiTypeChooserActivity;
import net.team10.android.R;
import net.team10.android.ReparonsParisApplication;
import net.team10.android.ReparonsParisApplication.GoogleAccountInformations;
import net.team10.android.SettingsActivity;
import net.team10.android.TitleBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.smartnsoft.droid4me.LifeCycle.BusinessObjectsRetrievalAsynchronousPolicy;
import com.smartnsoft.droid4me.framework.Commands;
import com.smartnsoft.droid4me.menu.StaticMenuCommand;
import com.smartnsoft.droid4me.support.v4.app.SmartFragment;

/**
 * The starting screen of the application.
 * 
 * @author Steeve Guillaume, Edouard Mercier, Willy Noel, Jocelyn Girard
 * @since 2012.02.18
 */
public final class HomeFragment
    extends SmartFragment<TitleBar.TitleBarAggregate>
    implements BusinessObjectsRetrievalAsynchronousPolicy, View.OnClickListener
{

  private Button reportButton;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
  {
    final View view = inflater.inflate(R.layout.home, null);

    reportButton = (Button) view.findViewById(R.id.reportButton);

    return view;
  }

  public void onRetrieveDisplayObjects()
  {
  }

  public void onRetrieveBusinessObjects()
      throws BusinessObjectUnavailableException
  {

  }

  public void onFulfillDisplayObjects()
  {
    reportButton.setOnClickListener(this);
  }

  public void onSynchronizeDisplayObjects()
  {

  }

  @Override
  public List<StaticMenuCommand> getMenuCommands()
  {
    final List<StaticMenuCommand> commands = new ArrayList<StaticMenuCommand>();
    commands.add(new StaticMenuCommand(R.string.Home_menu_settings, '1', 's', android.R.drawable.ic_menu_preferences, new Commands.StaticEnabledExecutable()
    {
      @Override
      public void run()
      {
        startActivity(new Intent(getCheckedActivity(), SettingsActivity.class));
      }
    }));
    commands.add(new StaticMenuCommand(R.string.Home_menu_about, '2', 'a', android.R.drawable.ic_menu_info_details, new Commands.StaticEnabledExecutable()
    {
      @Override
      public void run()
      {
        startActivity(new Intent(getCheckedActivity(), AboutActivity.class));
      }
    }));
    return commands;
  }

  public void onClick(View view)
  {
    if (view == reportButton)
    {
      if (getPreferences().contains(Constants.EMAIL_MD5) == false)
      {
        final GoogleAccountInformations googleAccount = ReparonsParisApplication.getGoogleAccountInformations(getCheckedActivity());
        final AlertDialog.Builder builder = new AlertDialog.Builder(getCheckedActivity());
        builder.setMessage(googleAccount.email);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface dialog, int which)
          {
            try
            {
              getPreferences().edit().putString(Constants.EMAIL_MD5, ReparonsParisApplication.md5sum(googleAccount.email)).commit();
            }
            catch (NoSuchAlgorithmException exception)
            {
              if (log.isErrorEnabled())
              {
                log.error("Cannot compute a local contact e-mail MD5: cannot check whether it belongs to the suggested contacts", exception);
              }
            }
            startActivity(new Intent(getCheckedActivity(), PoiTypeChooserActivity.class));
          }
        });
        builder.setCancelable(true);
        builder.show();
      }
      else
      {
        startActivity(new Intent(getCheckedActivity(), PoiTypeChooserActivity.class));
      }
    }
  }

}
