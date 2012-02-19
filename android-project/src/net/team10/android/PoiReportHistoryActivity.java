package net.team10.android;

import com.smartnsoft.droid4me.support.v4.app.SmartFragmentActivity;

public class PoiReportHistoryActivity extends SmartFragmentActivity<TitleBar.TitleBarAggregate> {

	public void onRetrieveDisplayObjects() 
	{
		
		setContentView(R.layout.poireportshistory_fragment);
	}

	public void onRetrieveBusinessObjects()
			throws BusinessObjectUnavailableException 
	{
		
	}
	
	public void onFulfillDisplayObjects() {
		
	}



	public void onSynchronizeDisplayObjects() {
		
	}

}
