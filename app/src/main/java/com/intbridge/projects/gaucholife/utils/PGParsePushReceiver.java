package com.intbridge.projects.gaucholife.utils;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Handle Push From Parse.com
 * Created by Derek on 3/15/2016.
 */
public class PGParsePushReceiver extends ParsePushBroadcastReceiver {
    @Override
    protected Class<? extends Activity> getActivity(Context context, Intent intent) {
        return super.getActivity(context, intent);
    }

    @Override
    protected Notification getNotification(Context context, Intent intent) {
        //Log.e("rec","getNotification");
        return super.getNotification(context, intent);
    }

    @Override
    protected void onPushReceive(Context context, Intent intent) {
        super.onPushReceive(context, intent);
        Bundle extras = intent.getExtras();
        // save data to SQLite database
        try {
            String jsonData = extras.getString("com.parse.Data");
            JSONObject notification = new JSONObject(jsonData);
            String message = notification.getString("alert");
            PGSQLiteHelper helper = new PGSQLiteHelper(context);
            helper.addNotification(message, DateUtils.convertDateToString(new Date()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Log.e("rec","onPushReceive");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        //Log.e("rec","onReceive");
    }


}
