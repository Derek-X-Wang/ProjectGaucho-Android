package com.intbridge.projects.gaucholife.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.intbridge.projects.gaucholife.MainActivity;
import com.intbridge.projects.gaucholife.R;

/**
 *
 * Created by Derek on 8/31/2015.
 */
public class PGAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent notificationIntent = new Intent(context, MainActivity.class);

//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
//        stackBuilder.addParentStack(MainActivity.class);
//        stackBuilder.addNextIntent(notificationIntent);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Bundle bundle = intent.getExtras();
        Notification notification;
        int id = 0;
        if(bundle!=null){
//            Log.e("receive: ",bundle.getString("common"));
//            Log.e("receive: ", bundle.getString("item"));
            //id = bundle.getInt("id");
            id = generateNotificationID(bundle.getString("common"),bundle.getString("meal"));
            notification = buildNotification(context, pendingIntent,bundle.getString("common"),bundle.getString("meal"));
        }else{
            notification = buildNotification(context, pendingIntent,"none","none");
        }
        pendingIntent = PendingIntent.getActivity(context, id, intent, 0);
        //PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);



        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, notification);
    }

    private Notification buildNotification(Context context, PendingIntent pendingIntent, String common,String meal){
//        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
//        bigTextStyle.setBigContentTitle("Your favorite food is coming");
//        bigTextStyle.bigText(String.format("Your favorite food, %s, will be served at %s tomorrow", food, common));

        Notification notification = new NotificationCompat.Builder(context)
                .setContentTitle("GauchoLife")
                .setContentText(String.format("Your favorite %s is serving at %s",meal,common))
                .setSmallIcon(R.drawable.pg_launcher)
                .setContentIntent(pendingIntent)
                .build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        return notification;
    }

    private int generateNotificationID(String common,String meal){
        String encodedCommon = "0";
        String encodedMeal = "0";
        switch (common){
            case "Carrillo":
                encodedCommon = "1";
                break;
            case "De La Guerra":
                encodedCommon = "2";
                break;
            case "Ortega":
                encodedCommon = "3";
                break;
            case "Portola":
                encodedCommon = "4";
                break;
        }
        switch (meal) {
            case "Breakfast":
                encodedMeal = "1";
                break;
            case "Brunch":
                encodedMeal = "2";
                break;
            case "Lunch":
                encodedMeal = "3";
                break;
            case "Dinner":
                encodedMeal = "4";
                break;
            case "Late Night":
                encodedMeal = "5";
                break;
        }

        return Integer.parseInt(encodedCommon+encodedMeal);
    }
}
