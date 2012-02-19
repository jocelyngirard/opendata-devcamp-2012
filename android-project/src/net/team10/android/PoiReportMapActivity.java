package net.team10.android;

import java.util.List;

import net.team10.android.bo.OpenDataPoi;
import net.team10.android.ws.ReparonsParisServices;
import net.team10.bo.PoiType;
import android.view.MotionEvent;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.smartnsoft.droid4me.LifeCycle;
import com.smartnsoft.droid4me.app.SmartMapActivity;

import de.android1.overlaymanager.ManagedOverlay;
import de.android1.overlaymanager.ManagedOverlayGestureDetector.OnOverlayGestureListener;
import de.android1.overlaymanager.ManagedOverlayItem;
import de.android1.overlaymanager.OverlayManager;
import de.android1.overlaymanager.ZoomEvent;
import de.android1.overlaymanager.lazyload.LazyLoadCallback;
import de.android1.overlaymanager.lazyload.LazyLoadException;

public class PoiReportMapActivity
    extends SmartMapActivity<Void>
    implements LifeCycle.BusinessObjectsRetrievalAsynchronousPolicy, OnOverlayGestureListener, LazyLoadCallback
{
  public static final String POI_TYPE = "poiType";

  private MapView mapView;

  private MyLocationOverlay myLocationOverlay = null;

  private MapController mapController;

  private OverlayManager overlayManager;

  private ManagedOverlay PoiManagedOverlay;

  private final List<ManagedOverlayItem> items = null;

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

    if (myLocationOverlay != null)
    {
      List<OpenDataPoi> openDataPois;

      try
      {
        openDataPois = ReparonsParisServices.getInstance().getOpenDataPois(poiType.getOpenDataDataSetId(), poiType.getOpenDataTypeId(),
            myLocationOverlay.getMyLocation().getLatitudeE6() / 1E6, myLocationOverlay.getMyLocation().getLongitudeE6() / 1E6, 10000);
      }
      catch (Exception exception)
      {
        throw new BusinessObjectUnavailableException(exception);
      }

      for (OpenDataPoi openDataPoisItem : openDataPois)
      {
        PoiManagedOverlay.createItem(new GeoPoint(openDataPoisItem.getLatitudeE6(), openDataPoisItem.getLongitudeE6()));
      }
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
    myLocationOverlay.runOnFirstFix(new Runnable()
    {
      public void run()
      {
        runOnUiThread(new Runnable()
        {
          public void run()
          {
            mapController.animateTo(myLocationOverlay.getMyLocation());
            mapController.setZoom(15);
            refreshBusinessObjectsAndDisplay();
          }
        });
      }
    });

    // OverlayManager
    overlayManager = new OverlayManager(getApplication(), mapView);

    PoiManagedOverlay = overlayManager.createOverlay("POI", getResources().getDrawable(R.drawable.map_marker));
    PoiManagedOverlay.createItem(new GeoPoint(-17540798, -149549241), "Point fictif", "Initialisation OverlayManager");

    overlayManager.populate();
    PoiManagedOverlay.setOnOverlayGestureListener(this);
    // PoiManagedOverlay.setLazyLoadCallback(this);

  }

  @Override
  public void onSynchronizeDisplayObjects()
  {
    super.onSynchronizeDisplayObjects();
    // overlayManager.populate();
  }

  public boolean onDoubleTap(MotionEvent e, ManagedOverlay overlay, GeoPoint point, ManagedOverlayItem item)
  {
    mapController.animateTo(point);
    mapController.zoomIn();
    return true;
  }

  public void onLongPress(MotionEvent e, ManagedOverlay overlay)
  {
    // TODO Auto-generated method stub

  }

  public void onLongPressFinished(MotionEvent e, ManagedOverlay overlay, GeoPoint point, ManagedOverlayItem item)
  {
    // TODO Auto-generated method stub

  }

  public boolean onScrolled(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3, ManagedOverlay arg4)
  {
    return false;
  }

  public boolean onSingleTap(MotionEvent e, ManagedOverlay overlay, GeoPoint point, ManagedOverlayItem item)
  {
    if (item != null)
    {

    }
    return false;
  }

  public boolean onZoom(ZoomEvent arg0, ManagedOverlay arg1)
  {
    // TODO Auto-generated method stub
    return false;
  }

  public List<ManagedOverlayItem> lazyload(GeoPoint topLeft, GeoPoint bottomRight, ManagedOverlay overlay)
      throws LazyLoadException
  {
    if (items != null)
    {
      items.clear();
      // if(topLeft< && <bottomRight)
      {
        // refreshBusinessObjectsAndDisplay();
      }
      // else
      {
        items.addAll(PoiManagedOverlay.getOverlayItems());
      }
    }
    return items;
  }
}
