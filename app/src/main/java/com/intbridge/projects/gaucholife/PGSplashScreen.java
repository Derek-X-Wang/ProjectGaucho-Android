package com.intbridge.projects.gaucholife;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import com.intbridge.projects.gaucholife.controllers.CouponsFragment;
import com.intbridge.projects.gaucholife.controllers.DiningFragment;
import com.intbridge.projects.gaucholife.controllers.MapsFragment;
import com.intbridge.projects.gaucholife.utils.Fader;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 *
 * Created by Derek on 9/9/2015.
 */
public class PGSplashScreen extends Activity {
    // Splash screen timer
    private static int SPLASH_TIME_OUT = 6000;
    private SweetAlertDialog dialog;
    private boolean FORCE_ENTER = true;
    private SharedPreferences sharedSettings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        setDialog();

        Fader.runAlphaAnimation(this, R.id.imgLogo);
        fetchRemoteSettings();

    }

    private void fetchRemoteSettings() {
        initPreferences();
        final ImageView logo = (ImageView)findViewById(R.id.imgLogo);
        if(isNetworkConnected()){
            ParseQuery query = ParseQuery.getQuery("ControlPanel");
            query.getFirstInBackground(new GetCallback<ParseObject>() {
                public void done(ParseObject object, ParseException e) {
                    if (object == null) {
                        Log.d("PGSplashScreen", "The getFirst request failed.");
                    } else {
                        FORCE_ENTER = false;
                        if(dialog.isShowing()){
                            dialog.dismiss();
                        }
                        // get the panel
                        SharedPreferences.Editor editor = sharedSettings.edit();
                        editor.putBoolean(MainActivity.DATA_SOURCE, object.getBoolean("DataSource"));
                        editor.putBoolean(MainActivity.CLEAN_LOCAL, object.getBoolean("CleanLocal"));
                        editor.apply();
                        logo.clearAnimation();
                        Intent i = new Intent(PGSplashScreen.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                }
            });

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(FORCE_ENTER) {
                        dialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener(){
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismiss();
                            }
                        });
                        dialog.show();
                    }
                }
            }, SPLASH_TIME_OUT);
        }else{
            dialog.show();
        }
    }

    private void initPreferences() {
        sharedSettings = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedSettings.edit();
        editor.putBoolean(MainActivity.DATA_SOURCE, false);
        editor.putBoolean(MainActivity.CLEAN_LOCAL, false);
        editor.putBoolean(DiningFragment.REOPEN_DINING, false);
        editor.putBoolean(CouponsFragment.REOPEN_COUPONS, false);
        editor.putBoolean(MapsFragment.REOPEN_MAPS, false);
        editor.apply();
    }

    public void setDialog() {
        dialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE).setTitleText("Bad Internet Connection!")
                .setContentText("Without internet, some of the features may not function correctly!")
                .setConfirmText("Okay, enter anyway")
                .setCancelText("Cancel")
                .showCancelButton(true)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        // reuse previous dialog instance
                        sDialog.dismiss();
                        Intent i = new Intent(PGSplashScreen.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                })
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                        finish();
                    }
                });
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            return false;
        } else
            return true;
    }

}
