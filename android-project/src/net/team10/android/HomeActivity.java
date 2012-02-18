package net.team10.android;

import java.util.List;

import net.team10.android.bo.OpenDataPoi;
import net.team10.android.ws.ReparonsParisServices;

import com.smartnsoft.droid4me.LifeCycle.BusinessObjectsRetrievalAsynchronousPolicy;
import com.smartnsoft.droid4me.cache.Values.CacheException;
import com.smartnsoft.droid4me.support.v4.app.SmartFragmentActivity;

/**
 * The starting screen of the application.
 * 
 * @author Steeve Guillaume, Edouard Mercier, Willy Noel, Jocelyn Girard
 * @since 2012.02.18
 */
public final class HomeActivity
    extends SmartFragmentActivity<Void>
    implements BusinessObjectsRetrievalAsynchronousPolicy
{

  private List<OpenDataPoi> pois;

  public void onRetrieveDisplayObjects()
  {
    setContentView(R.layout.home_fragment);
  }

  public void onRetrieveBusinessObjects()
      throws BusinessObjectUnavailableException
  {

    // Test of the openData POI
    try
    {
      pois = ReparonsParisServices.getInstance().getOpenDataPois("eclairageparis2011", "", 48.8520930694d, 2.34738897685d, 5000);
    }
    catch (CacheException exception)
    {
      throw new BusinessObjectUnavailableException(exception);
    }
  }

  public void onFulfillDisplayObjects()
  {
  }

  public void onSynchronizeDisplayObjects()
  {
  }

}
