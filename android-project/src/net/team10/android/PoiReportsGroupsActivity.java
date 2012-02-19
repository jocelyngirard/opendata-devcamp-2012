package net.team10.android;

import net.team10.bo.PoiType;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.smartnsoft.droid4me.app.SmartGroupActivity;

public class PoiReportsGroupsActivity
    extends SmartGroupActivity<TitleBar.TitleBarAggregate>
    implements View.OnClickListener
{

  private Button mapButton;

  private Button listButton;

  private PoiType poiType;

  @Override
  protected View getHeaderView()
  {
    final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    final LinearLayout headerLayout = (LinearLayout) inflater.inflate(R.layout.poireportsgroup_header, null);
    mapButton = (Button) headerLayout.findViewById(R.id.mapButton);
    listButton = (Button) headerLayout.findViewById(R.id.listButton);
    return headerLayout;
  }

  @Override
  protected Intent getSubIntent(String activityId)
  {
    if (activityId.equals(PoiReportMapActivity.class.getName()))
    {
      return new Intent(PoiReportsGroupsActivity.this, PoiReportMapActivity.class).putExtra(PoiReportMapActivity.POI_TYPE, poiType).setFlags(
          Intent.FLAG_ACTIVITY_SINGLE_TOP);
    }
    else
    {
      return new Intent(PoiReportsGroupsActivity.this, PoiReportsListActivity.class).putExtra(PoiReportMapActivity.POI_TYPE, poiType).setFlags(
          Intent.FLAG_ACTIVITY_SINGLE_TOP);
    }
  }

  public void onRetrieveBusinessObjects()
      throws BusinessObjectUnavailableException
  {
    poiType = (PoiType) getIntent().getSerializableExtra(PoiReportMapActivity.POI_TYPE);

    if (poiType == null)
    {
      throw new BusinessObjectUnavailableException("The PoiType is missing in the intent, we cannot continuous this view.");
    }

    addSubActivity(PoiReportMapActivity.class.getName());
    addSubActivity(PoiReportsListActivity.class.getName());
    switchToActivity(PoiReportMapActivity.class.getName());
  }

  public void onFulfillDisplayObjects()
  {
    mapButton.setOnClickListener(this);
    listButton.setOnClickListener(this);
  }

  public void onClick(View view)
  {
    if (view == mapButton)
    {
      if (!getCurrentActivityId().equals(PoiReportMapActivity.class.getName()))
      {
        switchToActivity(PoiReportMapActivity.class.getName());
      }
    }
    else if (view == listButton)
    {
      if (!getCurrentActivityId().equals(PoiReportsListActivity.class.getName()))
      {
        switchToActivity(PoiReportsListActivity.class.getName());
      }
    }
  }

}
