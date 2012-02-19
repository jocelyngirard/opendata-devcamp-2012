package net.team10.android;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

import net.team10.android.bo.OpenDataPoi;
import net.team10.android.ws.ReparonsParisServices;
import net.team10.bo.Account;
import net.team10.bo.PoiReport.ReportKind;
import net.team10.bo.PoiReport.ReportSeverity;
import net.team10.bo.PoiType;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.TextView;

import com.smartnsoft.droid4me.app.ProgressHandler;
import com.smartnsoft.droid4me.app.SmartCommands;
import com.smartnsoft.droid4me.app.SmartCommands.GuardedCommand;
import com.smartnsoft.droid4me.support.v4.app.SmartFragmentActivity;

public class CreatePoiReportActivity extends SmartFragmentActivity<TitleBar.TitleBarAggregate> implements View.OnClickListener
{
 private static final int PICK_IMAGE_REQUEST_CODE = 0;
	
	private ImageView photo;
	private Button type;
	private RatingBar gravity;
	private Button done;
	private InputStream photoInputStream;
	private  Account account;
	private OpenDataPoi openDataPoi;
	private PoiType poiType;

	private ArrayAdapter<String> adapterPlaceType;
	
	@Override
	  public void onActivityResult(int requestCode, int resultCode, Intent data)
	  {
	    switch (requestCode)
	    {
	    case CreatePoiReportActivity.PICK_IMAGE_REQUEST_CODE:
	      if (resultCode == Activity.RESULT_OK)
	      {
	        if (log.isDebugEnabled())
	        {
	          log.debug("Picked the image with data '" + data + "'");
	        }
	        final Uri photoUri = data.getData();

	        SmartCommands.execute(new GuardedCommand<Activity>(CreatePoiReportActivity.this)
	        {
	          @Override
	          protected void runGuarded()
	              throws Exception
	          {
	            final BitmapFactory.Options options = new BitmapFactory.Options();
	            options.inJustDecodeBounds = true;
	            final Rect paddingRectangle = new Rect();
	            // We first query the picture bounds
	            options.inJustDecodeBounds = true;
	            HomeActivity.decodeFileDescriptorAsBitmap(photoUri, paddingRectangle, options, getContentResolver().openAssetFileDescriptor(photoUri, "r"));
	            final int bitmapWidth = options.outWidth;
	            final int bitmapHeight = options.outHeight;
	            // And now, we decode the bitmap
	            options.inJustDecodeBounds = false;
	            final int edge = 256;
	            final int sampleSize = Math.max(1, (int) Math.min(bitmapWidth / edge, bitmapHeight / edge));
	            options.inSampleSize = sampleSize;
	            final Bitmap bitmap = HomeActivity.decodeFileDescriptorAsBitmap(photoUri, paddingRectangle, options, getContentResolver().openAssetFileDescriptor(photoUri, "r"));
	            runOnUiThread(new Runnable()
	            		{
							public void run() {
								photo.setImageBitmap(bitmap);
							}            	
	            		});
	            // We extract the input stream from the bitmap
	            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
	            final InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

	            account = ReparonsParisServices.getInstance().createAccount("accountId" + System.currentTimeMillis(), "accountNickname");
	            final List<PoiType> poiTypes = ReparonsParisServices.getInstance().getPoiTypes(false);
	            poiType = poiTypes.get(0);
	            final List<OpenDataPoi> openDataPois = ReparonsParisServices.getInstance().getOpenDataPois(poiType.getOpenDataDataSetId(),
	                poiType.getOpenDataTypeId(), 48.8566, 2.3522, 10000);
	             openDataPoi = openDataPois.get(0);
	             photoInputStream = null;// new ByteArrayInputStream(new byte[] { 1, 2, 3, 4 });

	          }
	        });
	      }
	      break;
	    default:
	      super.onActivityResult(requestCode, resultCode, data);
	      break;
	    }
	  }

	public void onRetrieveDisplayObjects() 
	{
		setContentView(R.layout.createpoireport);
		photo = (ImageView) findViewById(R.id.photo);
		type = (Button) findViewById(R.id.type);
		gravity = (RatingBar) findViewById(R.id.gravity);
		done = (Button) findViewById(R.id.ok);
	}

	public void onRetrieveBusinessObjects()
			throws BusinessObjectUnavailableException {
		
	}
	
	public void onFulfillDisplayObjects() 
	{
		photo.setOnClickListener(this);
		done.setOnClickListener(this);
		type.setOnClickListener(this);
		
	    adapterPlaceType = new ArrayAdapter<String>(this, R.layout.report_kind_item_dialog_box, R.id.check_text, getResources().getStringArray(
	            R.array.reportKind))
	        {
	          @Override
	          public View getView(int position, View convertView, ViewGroup parent)
	          {
	            if (convertView == null)
	            {
	              convertView = getLayoutInflater().inflate(R.layout.report_kind_item_dialog_box, null);
	            }
	            final TextView content = (TextView) convertView.findViewById(R.id.check_text);
	            final RadioButton radio = (RadioButton) convertView.findViewById(R.id.radiobutton);
	            final ImageView image = (ImageView) convertView.findViewById(R.id.icon);
	            content.setText(adapterPlaceType.getItem(position));
	            final ReportKind reportKind = ReportKind.values()[position];
	            image.setImageResource(getReportResourceId(reportKind));
	            radio.setFocusable(false);
	            radio.setClickable(false);
	          boolean checked = type.getTag() != null && getItem(position).equals(type.getTag()) ? true
	               : type.getTag() == null && position == 0 ? true : false;
	          radio.setChecked(checked);
	            return convertView;
	          }
	        };
	        type.setText("Type d'incident");
	        
	}
	
	public int getReportResourceId(ReportKind reportKind) 
	{
		switch(reportKind)
		{
		case Broken:
			return R.drawable.link_broken;
		case Stolen:
			return R.drawable.exclamation;
		case Missing:
			return R.drawable.question;
		}
		return 0;
	}
	
	

	public void onSynchronizeDisplayObjects() 
	{
		
	}

	public void onClick(View view) 
	{
		if(view == done)
		{
		    final View confirlDialogLayout = getLayoutInflater().inflate(R.layout.confirm_dialog, null);
			CreatePoiReportActivity.createConfirmationDialog(CreatePoiReportActivity.this, CreatePoiReportActivity.this, confirlDialogLayout, account, poiType, openDataPoi, photoInputStream).show();
		}
		
		else if( view == photo)
		{
			startActivityForResult(new Intent(Intent.ACTION_PICK).setType("image/*"), CreatePoiReportActivity.PICK_IMAGE_REQUEST_CODE);
		}
		
		else if(view == type)
		{
		    new AlertDialog.Builder(view.getContext()).setTitle(R.string.ConfirmDialog_titleReportKind).setSingleChoiceItems(adapterPlaceType, 0,
		              new DialogInterface.OnClickListener()
		              {
						private ReportKind reportKind;

						public void onClick(DialogInterface dialog, int which) {
							  dialog.dismiss();
			                  reportKind = ReportKind.values()[which];
			                  String[] label = getResources().getStringArray(
			                      R.array.reportKind);
			                  type.setCompoundDrawables((type.getContext().getResources().getDrawable(getReportResourceId(reportKind))), null, null, null);
			                  type.setText(label[which]);
			                  type.setTag(label[which]);
						}
		              }).show();
		    
		    
		 
		}
	}
	
	  static Dialog createConfirmationDialog(final Activity activity, final Context context, View view, final Account account, final PoiType poiType, final OpenDataPoi openDataPoi, final InputStream photoInputStream)
	  {
	    final EditText commentField = (EditText) view.findViewById(R.id.commentField);
	    final CheckBox twiterCheck = (CheckBox) view.findViewById(R.id.checkBox);
	    return new AlertDialog.Builder(context).setView(view).setTitle(context.getString(R.string.ConfirmDialog_title)).setPositiveButton(R.string.ConfirmDialog_ok,
	        new DialogInterface.OnClickListener()
	        {
	          public void onClick(DialogInterface dialog, int which)
	          {
	        	  postReportPoi(context, account, poiType,openDataPoi, photoInputStream, commentField);
	          }
	        }).setNegativeButton(R.string.ConfirmDialog_tcancel, new DialogInterface.OnClickListener()
	    {
	      public void onClick(DialogInterface dialog, int which)
	      {
	    	  
	      }
	    }).create();
	  }
	  
	  public static void postReportPoi(final Context context, final Account account, final PoiType poiType, final OpenDataPoi openDataPoi, final InputStream photoInputStream, final EditText commentField)
	  {
		  
		     final ProgressDialog progressDialog = new ProgressDialog(context);
		      progressDialog.setMessage("envoie du rapport");
		      progressDialog.setIndeterminate(true);
		      progressDialog.show();

	/*	   SmartCommands.execute(new SmartCommands.ProgressGuardedCommand(activity, null, progressDialog, "Could not save the member properly") 
		   {
				
				@Override
				protected void runGuardedProgress() throws Exception {

					ReparonsParisServices.getInstance().postPoiReportStatement(account.getUid(), poiType.getUid(), ReportKind.Broken, ReportSeverity.Major,
					        openDataPoi.getPoiId(), commentField.getText().toString(), photoInputStream);
					
				}
			});*/
	  }
	  

}
