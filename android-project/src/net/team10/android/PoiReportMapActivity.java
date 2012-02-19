package net.team10.android;

import net.team10.bo.PoiType;

import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.smartnsoft.droid4me.LifeCycle;
import com.smartnsoft.droid4me.app.SmartMapActivity;

import de.android1.overlaymanager.OverlayManager;

public class PoiReportMapActivity
    extends SmartMapActivity<TitleBar.TitleBarAggregate>
    implements LifeCycle.BusinessObjectsRetrievalAsynchronousPolicy
{
  public static final String POI_TYPE = "poiType";

  private MapView mapView;

  private MapController mapController;

  private OverlayManager overlayManager;

  private MyLocationOverlay myLocationOverlay = null;

  @Override
  protected String getGoogleMapsApiKey()
  {
    return Constants.GOOGLE_MAPS_API_KEY;
  }

  public void onRetrieveBusinessObjects()
      throws BusinessObjectUnavailableException
  {
    final PoiType poiType = (PoiType) getIntent().getSerializableExtra(PoiReportMapActivity.POI_TYPE);
    if (poiType == null)
    {
      throw new BusinessObjectUnavailableException("PoiType null pointer exception");
    }

  }

  public void onFulfillDisplayObjects()
  {

    mapView = getMapView();
    mapView.setBuiltInZoomControls(true); // Display zoom for non-multitouch

    // Géolocalisation
    myLocationOverlay = new MyLocationOverlay(getApplicationContext(), mapView);
    mapView.getOverlays().add(myLocationOverlay);
    myLocationOverlay.enableMyLocation();

    // Zoom to MyLocation
    mapController = mapView.getController();

    // mapController.animateTo(myLocationOverlay.getMyLocation()); FIXME
    // mapController.setZoom(15);

    // OverlayManager
    overlayManager = new OverlayManager(getApplication(), mapView);

  }
}
