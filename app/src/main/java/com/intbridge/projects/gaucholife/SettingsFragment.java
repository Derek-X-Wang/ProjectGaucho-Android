package com.intbridge.projects.gaucholife;

import android.app.ActionBar;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;


/**
 *
 */
public class SettingsFragment extends Fragment implements Switch.OnCheckedChangeListener {

    private MainActivity host;
    private Switch carrilloSwitch;
    private Switch delaguerraSwitch;
    private Switch ortegaSwitch;
    private Switch portolaSwitch;

    PGDatabaseManager databaseManager;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseManager = new PGDatabaseManager();
        host = (MainActivity)getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // prevent action bar to show when the program is reopen from background and the view is re-init
        ActionBar actionBar = getActivity().getActionBar();
        if(actionBar != null && actionBar.isShowing()) actionBar.hide();

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_settings, container, false);
        carrilloSwitch = (Switch)v.findViewById(R.id.carrilloswitch);
        delaguerraSwitch = (Switch)v.findViewById(R.id.delaguerraswitch);
        ortegaSwitch = (Switch)v.findViewById(R.id.ortegaswitch);
        portolaSwitch = (Switch)v.findViewById(R.id.portolaswitch);

        initSwitches();
        carrilloSwitch.setOnCheckedChangeListener(this);
        delaguerraSwitch.setOnCheckedChangeListener(this);
        ortegaSwitch.setOnCheckedChangeListener(this);
        portolaSwitch.setOnCheckedChangeListener(this);
        return v;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.e("Setting: ", "onCheckedChanged");
        databaseManager.setNotifiedCommons(carrilloSwitch.isChecked(), delaguerraSwitch.isChecked(), ortegaSwitch.isChecked(), portolaSwitch.isChecked());
        new NotificationUpdateTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
        new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Change Notification")
                .setContentText("Your Notifications are re-scheduled!")
                .setConfirmText("Ok")
                .showCancelButton(false)
                .show();
    }

    private void initSwitches(){
        Log.e("Setting: ","initSwitches");
        List<String> switches = databaseManager.getNotifiedCommons();
        if(switches != null){
            carrilloSwitch.setChecked(false);
            delaguerraSwitch.setChecked(false);
            ortegaSwitch.setChecked(false);
            portolaSwitch.setChecked(false);
            for(String switchString : switches){
                switch (switchString){
                    case "Carrillo":
                        carrilloSwitch.setChecked(true);
                        break;
                    case "De La Guerra":
                        delaguerraSwitch.setChecked(true);
                        break;
                    case "Ortega":
                        ortegaSwitch.setChecked(true);
                        break;
                    case "Portola":
                        portolaSwitch.setChecked(true);
                        break;
                }
            }
        }
    }


    private class NotificationUpdateTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            databaseManager.cancelAllScheduledNotification(host);
            databaseManager.scheduleAllNotification(host,host.getTempDataStorage());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

}
