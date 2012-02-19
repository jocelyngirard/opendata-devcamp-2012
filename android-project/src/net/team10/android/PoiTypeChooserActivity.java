package net.team10.android;

import net.team10.android.TitleBar.TitleBarRefreshFeature;
import net.team10.android.fragments.PoiTypeChooserFragment;

import com.smartnsoft.droid4me.support.v4.app.SmartFragmentActivity;

public final class PoiTypeChooserActivity
    extends SmartFragmentActivity<TitleBar.TitleBarAggregate>
    implements TitleBarRefreshFeature
{

  public void onRetrieveDisplayObjects()
  {
    setContentView(R.layout.poitypechooser_fragment);
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

  public void onTitleBarRefresh()
  {
    final PoiTypeChooserFragment poiTypeChooserFragment = (PoiTypeChooserFragment) getSupportFragmentManager().findFragmentById(R.id.poiTypeChooserFragment);
    poiTypeChooserFragment.onTitleBarRefresh();
  }

}
