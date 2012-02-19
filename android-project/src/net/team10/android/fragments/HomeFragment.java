package net.team10.android.fragments;

import java.security.NoSuchAlgorithmException;

import net.team10.android.Constants;
import net.team10.android.PoiTypeChooserActivity;
import net.team10.android.R;
import net.team10.android.ReparonsParisApplication;
import net.team10.android.ReparonsParisApplication.GoogleAccountInformations;
import net.team10.android.SettingsActivity;
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
<<<<<<< HEAD
import android.widget.EditText;
=======
import android.widget.Toast;
>>>>>>> 99bd08d7ab3adc6d5db35097cbaa29014cb8e3db

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

  private Button agentButton;

  private Button paramsButton;

  private Button donateButton;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
  {
    final View view = inflater.inflate(R.layout.home, null);

    reportButton = (Button) view.findViewById(R.id.reportButton);
    agentButton = (Button) view.findViewById(R.id.agentButton);
    paramsButton = (Button) view.findViewById(R.id.settingsButton);
    donateButton = (Button) view.findViewById(R.id.paypalButton);

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
    agentButton.setOnClickListener(this);
    paramsButton.setOnClickListener(this);
    donateButton.setOnClickListener(this);
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
        //builder.setMessage(googleAccount.email);
        final View dialogView = getActivity().getLayoutInflater().inflate(R.layout.credential_item_dialog_box, null);
        final EditText email = (EditText) dialogView.findViewById(R.id.email);
        final EditText name = (EditText) dialogView.findViewById(R.id.pseudo);
        builder.setView(dialogView);
        builder.setTitle("Entrer vos informations");
        email.setText(googleAccount.email);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
        {
          private ProgressDialog progressDialog;

          public void onClick(DialogInterface dialog, int which)
          {
            try
            {
              final String md5sum = ReparonsParisApplication.md5sum(googleAccount.email);
              progressDialog = new ProgressDialog(getCheckedActivity());
              progressDialog.setIndeterminate(true);
              progressDialog.show();
              SmartCommands.execute(new Runnable()
              {
                public void run()
                {
                  try
                  {
                    final String nickname = name.getText().toString();
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
            progressDialog.dismiss();
          }
        });
        builder.setCancelable(true);
        builder.show();
      }
      else
      {
        // startActivity(new Intent(getCheckedActivity(), PoiTypeChooserActivity.class));
        startActivity(new Intent(getCheckedActivity(), PoiTypeChooserActivity.class));
      }
    }
    else if (view == agentButton || view == donateButton)
    {
      Toast.makeText(getCheckedActivity(), "Bientôt disponible !", Toast.LENGTH_SHORT).show();
    }
    else if (view == paramsButton)
    {
      startActivity(new Intent(getCheckedActivity(), SettingsActivity.class));
    }
  }
}
