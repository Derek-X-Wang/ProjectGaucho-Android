package com.intbridge.projects.gaucholife.utils;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;

import com.google.android.gms.maps.model.Marker;
import com.intbridge.projects.gaucholife.PGDatabaseManager;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Handle User Stat Event
 * Created by Derek on 12/29/2015.
 */
public class ClientStatManager {

    public static void sendUserStatus(Activity host){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserStat");
        final String uuid = PGDatabaseManager.getUUID();
        query.whereEqualTo("UUID",uuid);
        final boolean isLocation = isLocationEnable(host);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                int countNotification = PGDatabaseManager.getFavoriteList().size();
                boolean isPush = true;
                if(countNotification == 0) isPush = false;

                int countBlueTooth = 0;
                boolean isBlueTooth = isBlueToothEnable();
                if(isBlueTooth) countBlueTooth = 1;

                int countLocation = 0;
                if(isLocation) countLocation = 1;

                if(parseObject == null){
                    // create a new
                    ParseObject newObject = new ParseObject("UserStat");
                    newObject.put("UUID",uuid);
                    newObject.put("AndroidAPI",currentapiVersion);
                    newObject.put("device", Devices.getDeviceName());
                    newObject.put("countLogin", 1);
                    newObject.put("isBlueTooth",isBlueTooth);
                    newObject.put("countBlueTooth", countBlueTooth);
                    newObject.put("isLocation",isLocation);
                    newObject.put("countLocation", countLocation);
                    newObject.put("isPush",isPush);
                    newObject.put("countPush", countNotification);
                    newObject.saveEventually();
                }else{
                    // update
                    parseObject.put("AndroidAPI", currentapiVersion);

                    int currentLoginCount = parseObject.getInt("countLogin");
                    parseObject.put("countLogin", currentLoginCount+1);

                    parseObject.put("isBlueTooth",isBlueTooth);
                    int currentBlueToothCount = parseObject.getInt("countBlueTooth");
                    parseObject.put("countBlueTooth", currentBlueToothCount + countBlueTooth);

                    int currentLocationCount = parseObject.getInt("countLocation");
                    parseObject.put("isLocation",isLocation);
                    parseObject.put("countLocation", currentLocationCount + countLocation);

                    parseObject.put("isPush",isPush);
                    parseObject.put("countPush", countNotification);

                    parseObject.saveEventually();
                }
            }
        });

    }

    public static boolean isBlueToothEnable(){
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            return false;
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                // Bluetooth is not enable :)
                return false;
            }
            return true;
        }
    }

    public static boolean isLocationEnable(Activity host){
        LocationManager manager = (LocationManager) host.getSystemService(Context.LOCATION_SERVICE);

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            return false;
        }
        return true;
    }

    public static boolean startGoogleMapApp(Activity host, Marker marker){
        if (LocationHelper.isGoogleMapsInstalled( host ) ) {
            Intent navigation = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + marker.getPosition().latitude + "," + marker.getPosition().longitude + "&mode=w"));
            host.startActivity(navigation);
            return true;
        } else {
            new SweetAlertDialog(host, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Missing Google Map")
                    .setContentText("Please install Google Maps!")
                    .setConfirmText("Ok")
                    .showCancelButton(false)
                    .show();

            return false;
        }
    }
}
