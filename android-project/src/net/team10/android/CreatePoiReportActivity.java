package net.team10.android;

import com.smartnsoft.droid4me.support.v4.app.SmartFragmentActivity;

public class CreatePoiReportActivity extends SmartFragmentActivity<TitleBar.TitleBarAggregate> {

	public void onRetrieveDisplayObjects() 
	{
		setContentView(R.layout.createpoireport_fragment);
	}

	public void onRetrieveBusinessObjects()
			throws BusinessObjectUnavailableException {
		
	}
	
	public void onFulfillDisplayObjects() {
		
	}



	public void onSynchronizeDisplayObjects() {
		
	}

}
