package com.intbridge.projects.gaucholife;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Derek on 9/9/2015.
 */
public class PGSplashScreen extends Activity {
    // Splash screen timer
    private static int SPLASH_TIME_OUT = 6000;
    private SweetAlertDialog dialog;
    private boolean FORCE_ENTER = true;
    private boolean dataSource = false;
    private boolean cleanLocal = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        setDialog();

        PGFader.runAlphaAnimation(this, R.id.imgLogo);
        final ImageView logo = (ImageView)findViewById(R.id.imgLogo);
        if(isNetworkConnected()){
            ParseQuery query = ParseQuery.getQuery("ControlPanel");
            query.getFirstInBackground(new GetCallback<ParseObject>() {
                public void done(ParseObject object, ParseException e) {

                    if (object == null) {
                        Log.d("RBSE", "The getFirst request failed.");
                    } else {
                        FORCE_ENTER = false;
                        if(dialog.isShowing()){
                            dialog.dismiss();
                        }
                        // get the panel
                        logo.clearAnimation();
                        Intent i = new Intent(PGSplashScreen.this, MainActivity.class);
                        i.putExtra("DATASOURCE", object.getBoolean("DataSource"));
                        i.putExtra("CLEANLOCAL", object.getBoolean("CleanLocal"));
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

    public void setDialog() {
        this.dialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE).setTitleText("Bad Internet Connection!")
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
                        i.putExtra("DATASOURCE", false);
                        i.putExtra("CLEANLOCAL", false);
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
