package net.team10.android;

import com.smartnsoft.droid4me.LifeCycle.BusinessObjectsRetrievalAsynchronousPolicy;
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

  public void onRetrieveDisplayObjects()
  {
    setContentView(R.layout.home_fragment);
  }

  public void onRetrieveBusinessObjects()
      throws BusinessObjectUnavailableException
  {
  }

  public void onFulfillDisplayObjects()
  {
  }

  public void onSynchronizeDisplayObjects()
  {
  }

}
