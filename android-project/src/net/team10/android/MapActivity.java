package net.team10.android;

import com.smartnsoft.droid4me.app.SmartMapActivity;

public class MapActivity
    extends SmartMapActivity<TitleBar.TitleBarAggregate>
{

  @Override
  protected String getGoogleMapsApiKey()
  {
    return Constants.GOOGLE_MAPS_API_KEY;
  }

  public void onRetrieveBusinessObjects()
      throws BusinessObjectUnavailableException
  {

  }

  public void onFulfillDisplayObjects()
  {
  }
}
