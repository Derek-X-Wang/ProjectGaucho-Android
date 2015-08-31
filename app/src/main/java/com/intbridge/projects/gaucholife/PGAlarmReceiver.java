package com.intbridge.projects.gaucholife;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

/**
 * Created by Derek on 8/31/2015.
 */
public class PGAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
//        Intent notificationIntent = new Intent(context, MainActivity.class);

//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
//        stackBuilder.addParentStack(MainActivity.class);
//        stackBuilder.addNextIntent(notificationIntent);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Bundle bundle = intent.getExtras();
        Notification notification;
        int id = 0;
        if(bundle!=null){
//            Log.e("receive: ",bundle.getString("common"));
//            Log.e("receive: ", bundle.getString("item"));
            id = bundle.getInt("id");

            notification = buildNotification(context, pendingIntent,bundle.getString("common"),bundle.getString("item"));
        }else{
            notification = buildNotification(context, pendingIntent,"none","none");
        }
        pendingIntent = PendingIntent.getActivity(context, id, intent, 0);
        //PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);



        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, notification);
    }

    private Notification buildNotification(Context context, PendingIntent pendingIntent, String common,String food){
        Notification notification = new NotificationCompat.Builder(context)
                .setContentTitle("GauchoLife")
                .setContentText(String.format("Your favorite food %s is served at %s",food,common))
                .setSmallIcon(R.drawable.pg_launcher)
                .setContentIntent(pendingIntent)
                .build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        return notification;
    }
}
