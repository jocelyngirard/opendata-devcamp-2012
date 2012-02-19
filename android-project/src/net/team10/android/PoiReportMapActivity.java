package net.team10.android;

import java.util.List;

import net.team10.android.bo.OpenDataPoi;
import net.team10.android.ws.ReparonsParisServices;
import net.team10.bo.PoiReport;
import net.team10.bo.PoiType;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.smartnsoft.droid4me.LifeCycle;
import com.smartnsoft.droid4me.app.SmartMapActivity;

import de.android1.overlaymanager.ManagedOverlay;
import de.android1.overlaymanager.ManagedOverlayGestureDetector.OnOverlayGestureListener;
import de.android1.overlaymanager.ManagedOverlayItem;
import de.android1.overlaymanager.MarkerRenderer;
import de.android1.overlaymanager.OverlayManager;
import de.android1.overlaymanager.ZoomEvent;

public class PoiReportMapActivity

    extends SmartMapActivity<Void>
    implements LifeCycle.BusinessObjectsRetrievalAsynchronousPolicy, OnOverlayGestureListener
{
  public static final String POI_TYPE = "poiType";

  private MapView mapView;

  private MyLocationOverlay myLocationOverlay = null;

  private MapController mapController;

  private OverlayManager overlayManager;

  private ManagedOverlay PoiManagedOverlay;

  private ManagedOverlay ManagedOverlayOpen;

  private ManagedOverlay ManagedOverlayScheduled;

  private ManagedOverlay ManagedOverlayInProgress;

  private ManagedOverlay ManagedOverlayClosed;

  private Drawable default_marker;

  private ImageView marker;

  private final boolean fromCache = true;

  public static enum ReportStatus
  {
    Open, Scheduled, InProgress, Closed
  }

  @Override
  protected String getGoogleMapsApiKey()
  {
    return Constants.GOOGLE_MAPS_API_KEY;
  }

  @Override
  public void onRetrieveDisplayObjects()
  {
    super.onRetrieveDisplayObjects();

    marker = new ImageView(this);
  }

  public void onRetrieveBusinessObjects()
      throws BusinessObjectUnavailableException
  {
    final PoiType poiType = (PoiType) getIntent().getSerializableExtra(PoiReportMapActivity.POI_TYPE);
    if (poiType == null)
    {
      throw new BusinessObjectUnavailableException("Le POI que vous demandez n'existe pas.");
    }

    // marker.setImageResource(getResources().getIdentifier(poiType.getOpenDataTypeId().toLowerCase(), "drawable", getPackageName()));
    // default_marker = marker.getDrawable();

    ManagedOverlayOpen = overlayManager.createOverlay("Open", default_marker);
    ManagedOverlayScheduled = overlayManager.createOverlay("Scheduled", default_marker);
    ManagedOverlayInProgress = overlayManager.createOverlay("InProgress", default_marker);
    ManagedOverlayClosed = overlayManager.createOverlay("Closed", default_marker);

    ManagedOverlayOpen.setCustomMarkerRenderer(setManagedOverlayMarker(ReportStatus.Open));
    ManagedOverlayScheduled.setCustomMarkerRenderer(setManagedOverlayMarker(ReportStatus.Scheduled));
    ManagedOverlayInProgress.setCustomMarkerRenderer(setManagedOverlayMarker(ReportStatus.InProgress));
    ManagedOverlayClosed.setCustomMarkerRenderer(setManagedOverlayMarker(ReportStatus.Closed));

    if (myLocationOverlay != null)
    {
      List<OpenDataPoi> openDataPois;
      List<PoiReport> poiReports;

      try
      {
        openDataPois = ReparonsParisServices.getInstance().getOpenDataPois(poiType.getOpenDataDataSetId(), poiType.getOpenDataTypeId(),
            myLocationOverlay.getMyLocation().getLatitudeE6() / 1E6, myLocationOverlay.getMyLocation().getLongitudeE6() / 1E6, 10000);
        poiReports = ReparonsParisServices.getInstance().getPoiReports(fromCache, poiType.getOpenDataDataSetId(), poiType.getOpenDataTypeId(),
            poiType.getOpenDataSource(), poiType.getPoiTypeFolderUid(), "1.23,4.56", "7.89,0.12");
      }
      catch (Exception exception)
      {
        throw new BusinessObjectUnavailableException(exception);
      }

      for (PoiReport poiReportItem : poiReports)
      {
        for (OpenDataPoi openDataPoisItem : openDataPois)
        {
          if (openDataPoisItem.getDataSetId().equals(poiReportItem.getOpenDataPoiId()))
          {
            switch (poiReportItem.getReportStatus())
            {
            case Open:
              ManagedOverlayOpen.createItem(new GeoPoint(openDataPoisItem.getLatitudeE6(), openDataPoisItem.getLongitudeE6()));
              break;
            case Scheduled:
              ManagedOverlayScheduled.createItem(new GeoPoint(openDataPoisItem.getLatitudeE6(), openDataPoisItem.getLongitudeE6()));
              break;
            case InProgress:
              ManagedOverlayInProgress.createItem(new GeoPoint(openDataPoisItem.getLatitudeE6(), openDataPoisItem.getLongitudeE6()));
              break;
            case Closed:
              ManagedOverlayClosed.createItem(new GeoPoint(openDataPoisItem.getLatitudeE6(), openDataPoisItem.getLongitudeE6()));
              break;
            }
          }
          else
            PoiManagedOverlay.createItem(new GeoPoint(openDataPoisItem.getLatitudeE6(), openDataPoisItem.getLongitudeE6()));
        }
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

    PoiManagedOverlay = overlayManager.createOverlay("POI", default_marker);
    PoiManagedOverlay.createItem(new GeoPoint(-17540798, -149549241), "Point fictif", "Initialisation OverlayManager");

    overlayManager.populate();
    PoiManagedOverlay.setOnOverlayGestureListener(this);

  }

  private MarkerRenderer setManagedOverlayMarker(final ReportStatus status)
  {
    return new MarkerRenderer()
    {
      public Drawable render(ManagedOverlayItem item, Drawable defaultMarker, int bitState)
      {
        BitmapDrawable b = (BitmapDrawable) defaultMarker;
        Bitmap poiCustomMarker = Bitmap.createBitmap(b.getBitmap().copy(Bitmap.Config.ARGB_8888, true));
        Bitmap infoCustomMarker = null;
        switch (status)
        {
        // case Open:
        // infoCustomMarker
        // break;
        // case Scheduled:
        // infoCustomMarker
        // break;
        // case InProgress:
        // infoCustomMarker
        // break;
        // case Closed:
        // infoCustomMarker
        // break;
        }

        Canvas canvas = new Canvas(poiCustomMarker);

        canvas.drawBitmap(infoCustomMarker, 0.f, 0.f, null);

        BitmapDrawable bd = new BitmapDrawable(poiCustomMarker);
        return bd;
      }
    };
  }

  @Override
  public void onSynchronizeDisplayObjects()
  {
    super.onSynchronizeDisplayObjects();

    overlayManager.populate();
  }

  public boolean onDoubleTap(MotionEvent e, ManagedOverlay overlay, GeoPoint point, ManagedOverlayItem item)
  {
    mapController.animateTo(point);
    mapController.zoomIn();
    return true;
  }

  public void onLongPress(MotionEvent e, ManagedOverlay overlay)
  {

  }

  public void onLongPressFinished(MotionEvent e, ManagedOverlay overlay, GeoPoint point, ManagedOverlayItem item)
  {
    overlay.createItem(point, "New");
  }

  public boolean onScrolled(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3, ManagedOverlay arg4)
  {
    return false;
  }

  public boolean onSingleTap(MotionEvent e, ManagedOverlay overlay, GeoPoint point, ManagedOverlayItem item)
  {
    // Intent intent = new Intent().putExtra("poiType", );
    if (item != null)
    {
      if (item.getTitle().equals("New"))
        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
      else
        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
    }
    return false;
  }

  public boolean onZoom(ZoomEvent arg0, ManagedOverlay arg1)
  {
    return false;
  }
}
