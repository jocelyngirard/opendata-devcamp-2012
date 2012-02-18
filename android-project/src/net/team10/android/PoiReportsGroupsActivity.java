package net.team10.android;

import android.content.Intent;

import com.smartnsoft.droid4me.app.SmartGroupActivity;

public class PoiReportsGroupsActivity
    extends SmartGroupActivity<TitleBar.TitleBarAggregate>
{

  @Override
  protected Intent getSubIntent(String tabId)
  {
    return null;
  }

  public void onRetrieveBusinessObjects()
      throws BusinessObjectUnavailableException
  {

  }

  public void onFulfillDisplayObjects()
  {

  }

}
