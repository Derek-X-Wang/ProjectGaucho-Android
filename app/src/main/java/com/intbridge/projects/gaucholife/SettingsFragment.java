package com.intbridge.projects.gaucholife;

import android.app.ActionBar;
import android.app.AlarmManager;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
        databaseManager.setNotifiedCommons(carrilloSwitch.isChecked(), delaguerraSwitch.isChecked(), ortegaSwitch.isChecked(), portolaSwitch.isChecked());
        List<Integer> pendingIntents = databaseManager.getPendingIntentArray();
        cancelAllScheduledNotification(pendingIntents);
        databaseManager.storePendingIntentArray(new ArrayList<Integer>());

        Map<Integer, Map> tempDataStorage = host.getTempDataStorage();
        if(host.getLoadDayLimit() == 0){
            scheduleAllNotification(tempDataStorage);
        }
        new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Change Notification")
                .setContentText("Your Notifications are re-scheduled!")
                .setConfirmText("Ok")
                .showCancelButton(false)
                .show();
    }

    private void initSwitches(){
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


    private void cancelAllScheduledNotification(List<Integer> pendingIntents){
        if(pendingIntents == null) return;
        // Retrieve alarm manager from the system
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        for(Integer pendingIntent : pendingIntents){
            Intent notificationIntent = new Intent("android.media.action.DISPLAY_NOTIFICATION");
            PendingIntent broadcast = PendingIntent.getBroadcast(getActivity(), pendingIntent, notificationIntent, 0);
            alarmManager.cancel(broadcast);
        }
    }

    private void createScheduledNotification(Date date, String common, String meal)
    {
        // Get new calendar object and set the date to now
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        // Add defined amount of days to the date
        //calendar.add(Calendar.HOUR_OF_DAY, days * 24);
        calendar.add(Calendar.SECOND, 10);

        // Retrieve alarm manager from the system
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        // Every scheduled intent needs a different ID, else it is just executed once
        int id = (int) System.currentTimeMillis();

        // Prepare the intent which should be launched at the date
        Intent notificationIntent = new Intent("android.media.action.DISPLAY_NOTIFICATION");
        //notificationIntent.addCategory("android.intent.category.DEFAULT");
        notificationIntent.putExtra("common", common);
        notificationIntent.putExtra("meal", meal);
        notificationIntent.putExtra("id",id);

        // Prepare the pending intent
        PendingIntent broadcast = PendingIntent.getBroadcast(getActivity(), id, notificationIntent, 0);

        // store PendingIntent for canceling reference
        databaseManager.addPendingIntentIDTolocalDatastore(id);

        // Register the alert in the system. You have the option to define if the device has to wake up on the alert or not
        alarmManager.set(AlarmManager.RTC_WAKEUP, databaseManager.getScheduledNotificationTime(date, common, meal).getTimeInMillis(), broadcast);
    }



    private void scheduleAllNotification(Map<Integer,Map> tempDataStorage){
        List<String> favoriteList = databaseManager.getFavoriteList();
        for(Map.Entry<Integer,Map> entry : tempDataStorage.entrySet()){
            int dateInt = entry.getKey();
            Date date = convertIntegerToDate(dateInt);
            Map<String,Map> commonDict = entry.getValue();
            List<String> notifiedCommon = databaseManager.getNotifiedCommons();
            for(Map.Entry<String,Map> common : commonDict.entrySet()){
                String commonName = common.getKey();
                if(notifiedCommon.contains(commonName)){
                    Map<String,Map> mealDict = common.getValue();
                    for(Map.Entry<String,Map> meal : mealDict.entrySet()){
                        String mealName = meal.getKey();
                        Map<String,List> foodDict = meal.getValue();
                        breakLoop:
                        for(Map.Entry<String,List> food : foodDict.entrySet()){
                            String foodName = food.getKey();
                            List<String> itemList = food.getValue();
                            for(String item : itemList){
                                if(favoriteList.contains(item)){
                                    createScheduledNotification(date,commonName,mealName);
                                    break breakLoop;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private Date convertIntegerToDate(int dateInt){
        String dateString = Integer.toString(dateInt);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        Date date;
        try {
            date = formatter.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            date = null;
        }
        return date;
    }
}
