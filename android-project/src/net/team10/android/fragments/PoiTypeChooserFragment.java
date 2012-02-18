package net.team10.android.fragments;

import java.util.ArrayList;
import java.util.List;

import net.team10.android.PoiReportMapActivity;
import net.team10.android.R;
import net.team10.android.TitleBar;
import net.team10.android.TitleBar.TitleBarRefreshFeature;
import net.team10.android.ws.ReparonsParisServices;
import net.team10.bo.PoiType;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.maps.MapActivity;
import com.smartnsoft.droid4me.LifeCycle.BusinessObjectsRetrievalAsynchronousPolicy;
import com.smartnsoft.droid4me.framework.SmartAdapters;
import com.smartnsoft.droid4me.framework.SmartAdapters.BusinessViewWrapper;
import com.smartnsoft.droid4me.framework.SmartAdapters.ObjectEvent;
import com.smartnsoft.droid4me.framework.SmartAdapters.SimpleBusinessViewWrapper;

public class PoiTypeChooserFragment
    extends SmartListViewFragmentV4<TitleBar.TitleBarAggregate, ListView>
    implements BusinessObjectsRetrievalAsynchronousPolicy, TitleBarRefreshFeature
{

  public class PoiTypeViewAttributes
  {

    private final TextView text;

    public PoiTypeViewAttributes(View view)
    {
      text = (TextView) view.findViewById(android.R.id.text1);
    }

    public void update(PoiType businessObject)
    {
      text.setText(businessObject.getLabel());
    }

  }

  public class PoiTypeViewWrapper
      extends SimpleBusinessViewWrapper<PoiType>
  {

    public PoiTypeViewWrapper(PoiType businessObject)
    {
      super(businessObject, 0, android.R.layout.simple_list_item_1);
    }

    @Override
    protected Object extractNewViewAttributes(Activity activity, View view, PoiType businessObject)
    {
      return new PoiTypeViewAttributes(view);
    }

    @Override
    protected void updateView(Activity activity, Object viewAttributes, View view, PoiType businessObject, int position)
    {
      ((PoiTypeViewAttributes) viewAttributes).update(businessObject);
    }

    @Override
    public Intent computeIntent(Activity activity, Object viewAttributes, View view, PoiType businessObject, ObjectEvent objectEvent, int position)
    {
      if (objectEvent == ObjectEvent.Clicked)
      {
        return new Intent(getCheckedActivity(), MapActivity.class).putExtra(PoiReportMapActivity.POI_TYPE, businessObject);
      }
      return super.computeIntent(activity, viewAttributes, view, businessObject, objectEvent, position);
    }

  }

  public List<? extends BusinessViewWrapper<?>> retrieveBusinessObjectsList()
      throws BusinessObjectUnavailableException
  {
    List<PoiType> poiTypes;

    try
    {
      poiTypes = ReparonsParisServices.getInstance().getPoiTypes();
    }
    catch (Exception exception)
    {
      throw new BusinessObjectUnavailableException(exception);
    }

    final List<BusinessViewWrapper<?>> wrappers = new ArrayList<SmartAdapters.BusinessViewWrapper<?>>();

    for (PoiType poiType : poiTypes)
    {
      wrappers.add(new PoiTypeViewWrapper(poiType));
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
