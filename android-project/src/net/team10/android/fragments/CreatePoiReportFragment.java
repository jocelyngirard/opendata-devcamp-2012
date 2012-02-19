package net.team10.android.fragments;

import java.util.concurrent.atomic.AtomicLong;

import net.team10.android.R;
import net.team10.android.TitleBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;

import com.smartnsoft.droid4me.support.v4.app.SmartFragment;

public class CreatePoiReportFragment extends
		SmartFragment<TitleBar.TitleBarAggregate> implements View.OnClickListener {

	private ImageView photo;
	private Spinner type;
	private RatingBar gravity;
	private Button done;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.createpoireport, null);
		photo = (ImageView) view.findViewById(R.id.photo);
		type = (Spinner) view.findViewById(R.id.type);
		gravity = (RatingBar) view.findViewById(R.id.gravity);
		done = (Button) view.findViewById(R.id.ok);

		return view;
	}

	public void onRetrieveDisplayObjects() 
	{

	}

	public void onRetrieveBusinessObjects()
			throws BusinessObjectUnavailableException {

	}

	public void onFulfillDisplayObjects() 
	{
		photo.setOnClickListener(this);
		done.setOnClickListener(this);
	}

	public void onSynchronizeDisplayObjects() {

	}

	public void onClick(View view) 
	{
		if(view == done)
		{
		    final View confirlDialogLayout = getActivity().getLayoutInflater().inflate(R.layout.confirm_dialog, null);

			CreatePoiReportFragment.createConfirmationDialog(getActivity() ,confirlDialogLayout).show();
		}
	}
	
  static Dialog createConfirmationDialog( final Context context, View view)
	  {
	    final EditText commentField = (EditText) view.findViewById(R.id.commentField);
	    final CheckBox twiterCheck = (CheckBox) view.findViewById(R.id.checkBox);
	    return new AlertDialog.Builder(context).setView(view).setTitle(context.getString(R.string.ConfirmDialog_title)).setPositiveButton(R.string.ConfirmDialog_ok,
	        new DialogInterface.OnClickListener()
	        {
	          public void onClick(DialogInterface dialog, int which)
	          {

	          }
	        }).setNegativeButton(R.string.ConfirmDialog_tcancel, new DialogInterface.OnClickListener()
	    {
	      public void onClick(DialogInterface dialog, int which)
	      {
	      }
	    }).create();
	  }

}
