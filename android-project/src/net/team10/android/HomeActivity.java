package net.team10.android;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import net.team10.android.bo.OpenDataPoi;
import net.team10.android.ws.ReparonsParisServices;
import net.team10.bo.Account;
import net.team10.bo.PoiReport;
import net.team10.bo.PoiReport.ReportKind;
import net.team10.bo.PoiReport.ReportSeverity;
import net.team10.bo.PoiReportStatement;
import net.team10.bo.PoiType;
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;

import com.smartnsoft.droid4me.LifeCycle.BusinessObjectsRetrievalAsynchronousPolicy;
import com.smartnsoft.droid4me.app.SmartCommands;
import com.smartnsoft.droid4me.app.SmartCommands.GuardedCommand;
import com.smartnsoft.droid4me.framework.Commands;
import com.smartnsoft.droid4me.menu.StaticMenuCommand;
import com.smartnsoft.droid4me.support.v4.app.SmartFragmentActivity;

/**
 * The starting screen of the application.
 * 
 * @author Steeve Guillaume, Edouard Mercier, Willy Noel, Jocelyn Girard
 * @since 2012.02.18
 */
public final class HomeActivity
    extends SmartFragmentActivity<TitleBar.TitleBarAggregate>
    implements BusinessObjectsRetrievalAsynchronousPolicy
{

  private static final int PICK_IMAGE_REQUEST_CODE = 0;

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data)
  {
    switch (requestCode)
    {
    case HomeActivity.PICK_IMAGE_REQUEST_CODE:
      if (resultCode == Activity.RESULT_OK)
      {
        if (log.isDebugEnabled())
        {
          log.debug("Picked the image with data '" + data + "'");
        }
        final Uri photoUri = data.getData();

        SmartCommands.execute(new GuardedCommand<Activity>(HomeActivity.this)
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
            decodeFileDescriptorAsBitmap(photoUri, paddingRectangle, options);
            final int bitmapWidth = options.outWidth;
            final int bitmapHeight = options.outHeight;
            // And now, we decode the bitmap
            options.inJustDecodeBounds = false;
            final int edge = 256;
            final int sampleSize = Math.max(1, Math.min(bitmapWidth / edge, bitmapHeight / edge));
            options.inSampleSize = sampleSize;
            final Bitmap bitmap = decodeFileDescriptorAsBitmap(photoUri, paddingRectangle, options);
            // We extract the input stream from the bitmap
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            final InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

            final Account account = ReparonsParisServices.getInstance().createAccount("accountId" + System.currentTimeMillis(), "accountNickname");
            final List<PoiType> poiTypes = ReparonsParisServices.getInstance().getPoiTypes(false);
            final PoiType poiType = poiTypes.get(0);
            final double latitude = 48.8566;
            final double longitude = 2.3522;
            final List<OpenDataPoi> openDataPois = ReparonsParisServices.getInstance().getOpenDataPois(false, poiType.getOpenDataDataSetId(),
                null, latitude, longitude, 10000);
            final OpenDataPoi openDataPoi = openDataPois.get(0);
            final InputStream photoInputStream = null;// new ByteArrayInputStream(new byte[] { 1, 2, 3, 4 });
            ReparonsParisServices.getInstance().postPoiReportStatement(account.getUid(), poiType.getUid(), ReportKind.Broken, ReportSeverity.Major,
                openDataPoi.getPoiId(), "comment", photoInputStream);
            final List<PoiReport> poiReports = ReparonsParisServices.getInstance().getPoiReports(false, poiType.getOpenDataDataSetId(),
                poiType.getOpenDataTypeId(), poiType.getOpenDataSource(), poiType.getUid(), latitude + .02, longitude - .02, latitude - 0.02, longitude + 0.02);
            final PoiReport poiReport = poiReports.get(0);
            final List<PoiReportStatement> poiReportStatements = ReparonsParisServices.getInstance().getPoiReportStatements(false, poiReport.getUid());
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
    setContentView(R.layout.home_fragment);
  }

  public void onRetrieveBusinessObjects()
      throws BusinessObjectUnavailableException
  {
  }

  public void onFulfillDisplayObjects()
  {
    // getAggregate().getAttributes().setTitle(R.string.applicationName);
  }

  public void onSynchronizeDisplayObjects()
  {
  }

  @Override
  public List<StaticMenuCommand> getMenuCommands()
  {
    final List<StaticMenuCommand> commands = new ArrayList<StaticMenuCommand>();
    commands.add(new StaticMenuCommand(R.string.Home_menu_settings, '1', 's', android.R.drawable.ic_menu_preferences, new Commands.StaticEnabledExecutable()
    {
      @Override
      public void run()
      {
        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
      }
    }));
    commands.add(new StaticMenuCommand(R.string.Home_menu_about, '2', 'a', android.R.drawable.ic_menu_info_details, new Commands.StaticEnabledExecutable()
    {
      @Override
      public void run()
      {
        startActivity(new Intent(getApplicationContext(), AboutActivity.class));
      }
    }));
    commands.add(new StaticMenuCommand("Test", '2', 'a', android.R.drawable.ic_menu_info_details, new Commands.StaticEnabledExecutable()
    {
      @Override
      public void run()
      {
        startActivityForResult(new Intent(Intent.ACTION_PICK).setType("image/*"), HomeActivity.PICK_IMAGE_REQUEST_CODE);
      }
    }));
    return commands;
  }

  private Bitmap decodeFileDescriptorAsBitmap(Uri avatarUri, Rect paddingRectangle, BitmapFactory.Options options)
      throws IOException
  {
    final AssetFileDescriptor fileDescriptor = getContentResolver().openAssetFileDescriptor(avatarUri, "r");
    final FileInputStream inputStream = fileDescriptor.createInputStream();
    try
    {
      // We just ask for the picture bounds
      return BitmapFactory.decodeStream(inputStream, paddingRectangle, options);
    }
    finally
    {
      inputStream.close();
    }
  }
}
