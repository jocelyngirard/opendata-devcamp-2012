package net.team10.android;

import java.util.List;

import net.team10.android.ws.ReparonsParisServices;
import net.team10.bo.Poi;

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
{

	List<Poi> pois;
	
  public void onRetrieveDisplayObjects()
  {
    setContentView(R.layout.home_fragment);
  }

  public void onRetrieveBusinessObjects()
      throws BusinessObjectUnavailableException
  {
	  
	  
	  //Test of the openData POI
	  try {
		 pois = ReparonsParisServices.getInstance().getOpenDataPoi();
		
	} catch (CacheException exception)
	{
		throw new BusinessObjectUnavailableException(exception);
	}
	  pois.get(0);
	  
  }

  public void onFulfillDisplayObjects()
  {

  }

  public void onSynchronizeDisplayObjects()
  {

  }

}
