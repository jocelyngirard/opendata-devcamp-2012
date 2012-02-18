package net.team10.android;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;

import com.smartnsoft.droid4me.app.SmartActivity;
import com.smartnsoft.droid4me.framework.Commands;
import com.smartnsoft.droid4me.menu.StaticMenuCommand;

/**
 * The starting screen of the application.
 * 
 * @author Steeve Guillaume, Edouard Mercier, Willy Noel, Jocelyn Girard
 * @since 2012.02.18
 */
public final class HomeActivity
    extends SmartActivity<TitleBar.TitleBarAggregate>
{

  public void onRetrieveDisplayObjects()
  {
    setContentView(R.layout.home);
    // TODO Auto-generated method stub

  }

  public void onRetrieveBusinessObjects()
      throws BusinessObjectUnavailableException
  {
    // TODO Auto-generated method stub

  }

  public void onFulfillDisplayObjects()
  {
    // TODO Auto-generated method stub

  }

  public void onSynchronizeDisplayObjects()
  {
    // TODO Auto-generated method stub

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
        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
      }
    }));
    commands.add(new StaticMenuCommand(R.string.Home_menu_about, '2', 'a', android.R.drawable.ic_menu_info_details, new Commands.StaticEnabledExecutable()
    {
      @Override
      public void run()
      {
        startActivity(new Intent(getApplicationContext(), AboutActivity.class));
      }
    }));
    return commands;
  }

}
