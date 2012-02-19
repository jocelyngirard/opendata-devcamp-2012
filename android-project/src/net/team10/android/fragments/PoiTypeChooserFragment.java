package net.team10.android.fragments;

import java.util.ArrayList;
import java.util.List;

import net.team10.android.PoiReportMapActivity;
import net.team10.android.PoiReportsListActivity;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.smartnsoft.droid4me.LifeCycle.BusinessObjectsRetrievalAsynchronousPolicy;
import com.smartnsoft.droid4me.framework.Commands;
import com.smartnsoft.droid4me.framework.SmartAdapters;
import com.smartnsoft.droid4me.framework.SmartAdapters.BusinessViewWrapper;
import com.smartnsoft.droid4me.framework.SmartAdapters.ObjectEvent;
import com.smartnsoft.droid4me.framework.SmartAdapters.SimpleBusinessViewWrapper;
import com.smartnsoft.droid4me.menu.StaticMenuCommand;

public class PoiTypeChooserFragment
    extends SmartListViewFragmentV4<TitleBar.TitleBarAggregate, ListView>
    implements BusinessObjectsRetrievalAsynchronousPolicy, TitleBarRefreshFeature
{

  public class PoiTypeViewAttributes
  {

    private final TextView title;

    private final TextView description;

    private final ImageView icon;

    public PoiTypeViewAttributes(View view)
    {
      title = (TextView) view.findViewById(R.id.title);
      description = (TextView) view.findViewById(R.id.description);
      icon = (ImageView) view.findViewById(R.id.icon);
    }

    public void update(Activity activity, PoiType businessObject, int position)
    {
      title.setText(businessObject.getLabel());
      description.setText(businessObject.getOpenDataSource().name());
      icon.setImageResource(activity.getResources().getIdentifier(businessObject.getOpenDataTypeId().toLowerCase(), "drawable", activity.getPackageName()));
    }

  }

  public class PoiTypeViewWrapper
      extends SimpleBusinessViewWrapper<PoiType>
  {

    public PoiTypeViewWrapper(PoiType businessObject)
    {
      super(businessObject, 0, R.layout.poitypechooser_list_item);
    }

    @Override
    protected Object extractNewViewAttributes(Activity activity, View view, PoiType businessObject)
    {
      return new PoiTypeViewAttributes(view);
    }

    @Override
    protected void updateView(Activity activity, Object viewAttributes, View view, PoiType businessObject, int position)
    {
      ((PoiTypeViewAttributes) viewAttributes).update(activity, businessObject, position);
    }

    @Override
    public Intent computeIntent(Activity activity, Object viewAttributes, View view, PoiType businessObject, ObjectEvent objectEvent, int position)
    {
      if (objectEvent == ObjectEvent.Clicked)
      {
        return new Intent(getCheckedActivity(), PoiReportsListActivity.class).putExtra(PoiReportMapActivity.POI_TYPE, businessObject);
      }

      return super.computeIntent(activity, viewAttributes, view, businessObject, objectEvent, position);

    }

  }

  private boolean fromCache = true;

  public List<? extends BusinessViewWrapper<?>> retrieveBusinessObjectsList()
      throws BusinessObjectUnavailableException
  {
    List<PoiType> poiTypes;

    try
    {
      poiTypes = ReparonsParisServices.getInstance().getPoiTypes(fromCache);
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

    fromCache = true;
    return wrappers;
  }

  @Override
  public void onFulfillDisplayObjects()
  {
    super.onFulfillDisplayObjects();

    setHasOptionsMenu(true);

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

  @Override
  public List<StaticMenuCommand> getMenuCommands()
  {
    final List<StaticMenuCommand> commands = new ArrayList<StaticMenuCommand>();
    commands.add(new StaticMenuCommand("refresh", '1', 's', android.R.drawable.ic_menu_preferences, new Commands.StaticEnabledExecutable()
    {
      @Override
      public void run()
      {
        onTitleBarRefresh();
      }
    }));
    return commands;
  }

  public void onTitleBarRefresh()
  {
    fromCache = false;
    refreshBusinessObjectsAndDisplay(true);
  }
}
