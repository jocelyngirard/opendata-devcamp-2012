package net.team10.android.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.team10.android.PoiReportMapActivity;
import net.team10.android.R;
import net.team10.android.bo.OpenDataPoi;
import net.team10.android.ws.ReparonsParisServices;
import net.team10.bo.PoiType;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.smartnsoft.droid4me.LifeCycle.BusinessObjectsRetrievalAsynchronousPolicy;
import com.smartnsoft.droid4me.framework.SmartAdapters;
import com.smartnsoft.droid4me.framework.SmartAdapters.BusinessViewWrapper;
import com.smartnsoft.droid4me.framework.SmartAdapters.ObjectEvent;
import com.smartnsoft.droid4me.framework.SmartAdapters.SimpleBusinessViewWrapper;

public class PoiReportsListFragment
    extends SmartListViewFragmentV4<Void, ListView>
    implements BusinessObjectsRetrievalAsynchronousPolicy
{

  public class OpenDataPoiViewAttributes
  {

    private final TextView title;

    private final TextView description;

    private final ImageView icon;

    public OpenDataPoiViewAttributes(View view)
    {
      title = (TextView) view.findViewById(R.id.title);
      description = (TextView) view.findViewById(R.id.description);
      icon = (ImageView) view.findViewById(R.id.icon);
    }

    public void update(Activity activity, OpenDataPoi businessObject, int position)
    {
      title.setText(businessObject.getLabel());
      description.setText(businessObject.getGeoName());
      icon.setImageResource(activity.getResources().getIdentifier(businessObject.getTypeId().toLowerCase(), "drawable", activity.getPackageName()));
    }
  }

  public class OpenDataPoiViewWrapper
      extends SimpleBusinessViewWrapper<OpenDataPoi>
  {

    public OpenDataPoiViewWrapper(OpenDataPoi businessObject)
    {
      super(businessObject, 0, R.layout.poitypechooser_list_item);
    }

    @Override
    protected Object extractNewViewAttributes(Activity activity, View view, OpenDataPoi businessObject)
    {
      return new OpenDataPoiViewAttributes(view);
    }

    @Override
    protected void updateView(Activity activity, Object viewAttributes, View view, OpenDataPoi businessObject, int position)
    {
      ((OpenDataPoiViewAttributes) viewAttributes).update(activity, businessObject, position);
    }

    @Override
    public Intent computeIntent(Activity activity, Object viewAttributes, View view, OpenDataPoi businessObject, ObjectEvent objectEvent, int position)
    {
      if (objectEvent == ObjectEvent.Clicked)
      {
        // return new Intent(getCheckedActivity(), MapActivity.class).putExtra(MapActivity.POI_TYPE, businessObject);
      }
      return super.computeIntent(activity, viewAttributes, view, businessObject, objectEvent, position);
    }

  }

  private final boolean fromCache = true;

  public List<? extends BusinessViewWrapper<?>> retrieveBusinessObjectsList()
      throws BusinessObjectUnavailableException
  {
    final PoiType poiType = (PoiType) getCheckedActivity().getIntent().getSerializableExtra(PoiReportMapActivity.POI_TYPE);

    if (poiType == null)
    {
      throw new BusinessObjectUnavailableException("The PoiType is missing in the intent, we cannot continuous this view.");
    }

    List<OpenDataPoi> openDataPois;
    // List<PoiReport> poiReports;
    Map<String, OpenDataPoi> openDataPoiMap = new HashMap<String, OpenDataPoi>();

    try
    {
      openDataPois = ReparonsParisServices.getInstance().getOpenDataPois(poiType.getOpenDataDataSetId(), poiType.getOpenDataTypeId(), 48.8566, 2.3522, 10000);
      // poiReports = ReparonsParisServices.getInstance().getPoiReports(fromCache, poiType.getOpenDataDataSetId(), poiType.getOpenDataTypeId(),
      // poiType.getOpenDataSource(), poiType.getPoiTypeFolderUid(), "1.23,4.56", "7.89,0.12");
    }
    catch (Exception exception)
    {
      throw new BusinessObjectUnavailableException(exception);
    }

    final List<BusinessViewWrapper<?>> wrappers = new ArrayList<SmartAdapters.BusinessViewWrapper<?>>();

    for (OpenDataPoi openDataPoi : openDataPois)
    {
      openDataPoiMap.put(openDataPoi.getPoiId(), openDataPoi);
    }

    for (OpenDataPoi openDataPoi : openDataPois)
    {
      wrappers.add(new OpenDataPoiViewWrapper(openDataPoi));
    }

    return wrappers;
  }

  @Override
  public void onFulfillDisplayObjects()
  {
    super.onFulfillDisplayObjects();

    if (!((LocationManager) getCheckedActivity().getSystemService(Activity.LOCATION_SERVICE)).isProviderEnabled(LocationManager.GPS_PROVIDER))
    {
      final AlertDialog.Builder builder = new AlertDialog.Builder(getCheckedActivity());
      builder.setMessage(R.string.PoiTypeChooser_enable_gps_messages).setCancelable(false).setPositiveButton(R.string.PoiTypeChooser_enable_gps_valid_button,
          new DialogInterface.OnClickListener()
          {
            public void onClick(DialogInterface dialog, int id)
            {
              startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
              getCheckedActivity().finish();
            }
          }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface dialog, int id)
        {
          dialog.dismiss();
          getCheckedActivity().finish();
        }
      });
      builder.show();
    }
  }

  public void onTitleBarRefresh()
  {
    refreshBusinessObjectsAndDisplay(true);
  }
}
