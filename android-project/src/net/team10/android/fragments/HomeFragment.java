package net.team10.android.fragments;

import java.security.NoSuchAlgorithmException;

import net.team10.android.Constants;
import net.team10.android.PoiTypeChooserActivity;
import net.team10.android.R;
import net.team10.android.ReparonsParisApplication;
import net.team10.android.ReparonsParisApplication.GoogleAccountInformations;
import net.team10.android.TitleBar;
import net.team10.android.ws.ReparonsParisServices;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.smartnsoft.droid4me.LifeCycle.BusinessObjectsRetrievalAsynchronousPolicy;
import com.smartnsoft.droid4me.app.SmartCommands;
import com.smartnsoft.droid4me.support.v4.app.SmartFragment;
import com.smartnsoft.droid4me.ws.WebServiceClient.CallException;

/**
 * The starting screen of the application.
 * 
 * @author Steeve Guillaume, Edouard Mercier, Willy Noel, Jocelyn Girard
 * @since 2012.02.18
 */
public final class HomeFragment
    extends SmartFragment<TitleBar.TitleBarAggregate>
    implements BusinessObjectsRetrievalAsynchronousPolicy, View.OnClickListener
{

  private Button reportButton;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
  {
    final View view = inflater.inflate(R.layout.home, null);

    reportButton = (Button) view.findViewById(R.id.reportButton);

    setHasOptionsMenu(true);
    return view;
  }

  public void onRetrieveDisplayObjects()
  {
  }

  public void onRetrieveBusinessObjects()
      throws BusinessObjectUnavailableException
  {

  }

  public void onFulfillDisplayObjects()
  {
    reportButton.setOnClickListener(this);
  }

  public void onSynchronizeDisplayObjects()
  {

  }

  public void onClick(View view)
  {
    if (view == reportButton)
    {
      if (getPreferences().contains(Constants.EMAIL_MD5_PREFERENCE_KEY) == false)
      {
        final GoogleAccountInformations googleAccount = ReparonsParisApplication.getGoogleAccountInformations(getCheckedActivity());
        final AlertDialog.Builder builder = new AlertDialog.Builder(getCheckedActivity());
        builder.setMessage(googleAccount.email);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface dialog, int which)
          {
            try
            {
              final String md5sum = ReparonsParisApplication.md5sum(googleAccount.email);
              final ProgressDialog progressDialog = new ProgressDialog(getCheckedActivity());
              progressDialog.setIndeterminate(true);
              progressDialog.show();
              SmartCommands.execute(new Runnable()
              {
                public void run()
                {
                  try
                  {
                    final String nickname = "Nick";
                    ReparonsParisServices.getInstance().createAccount(md5sum, nickname);
                  }
                  catch (CallException e)
                  {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                  }
                }
              });
              getPreferences().edit().putString(Constants.EMAIL_MD5_PREFERENCE_KEY, md5sum).commit();
            }
            catch (NoSuchAlgorithmException exception)
            {
              if (log.isErrorEnabled())
              {
                log.error("Cannot compute a local contact e-mail MD5: cannot check whether it belongs to the suggested contacts", exception);
              }
            }
            startActivity(new Intent(getCheckedActivity(), PoiTypeChooserActivity.class));
          }
        });
        builder.setCancelable(true);
        builder.show();
      }
      else
      {
        startActivity(new Intent(getCheckedActivity(), PoiTypeChooserActivity.class));
      }
    }
  }
}
