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

import com.intbridge.projects.gaucholife.utils.Fader;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;
import rx.Observable;
import rx.Observer;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 *
 * Created by Derek on 9/9/2015.
 */
public class PGSplashScreen extends Activity {
    // Splash screen timer
    public static String DATA_SOURCE = "DATASOURCE";
    public static String CLEAN_LOCAL = "CLEANLOCAL";

    private static int SPLASH_TIME_OUT = 2000;
    private SweetAlertDialog dialog;
    private boolean DATA_RECEIVED = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        setDialog();

        Fader.runAlphaAnimation(this, R.id.imgLogo);
        final ImageView logo = (ImageView)findViewById(R.id.imgLogo);
        if(isNetworkConnected()){
            Observable.fromCallable(new Callable<ParseObject>() {
                @Override
                public ParseObject call() throws Exception {
                    return ParseQuery
                            .getQuery("ControlPanel")
                            .whereEqualTo("OS","Android")
                            .getFirst();
                }
            })
                    .mergeWith(Observable.timer(SPLASH_TIME_OUT, TimeUnit.MILLISECONDS)
                            .map(new Func1<Long, ParseObject>() {
                                @Override
                                public ParseObject call(Long aLong) {
                                    return null;
                                }
                            }))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<ParseObject>() {
                        @Override
                        public void onCompleted() {
                            Log.d("Stream onCompleted: ", "stream completed");
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(ParseObject parseObject) {
                            // DATA_RECEIVED prevent open dialog after data is received
                            if (parseObject == null) {
                                if(!DATA_RECEIVED) {
                                    Log.e("Parse Request: ", "request timeout, show dialog");
                                    dialog.setTitleText("Internet Congestion!");
                                    dialog.show();
                                }
                            } else {
                                if(dialog.isShowing()){
                                    Log.e("Parse Request: ", "dialog is showing");
                                    dialog.dismiss();
                                }
                                DATA_RECEIVED = true;
                                Log.e("Parse Request: ", "object received");
                                logo.clearAnimation();
                                Prefs.putBoolean(DATA_SOURCE, parseObject.getBoolean("DataSource"));
                                Prefs.putBoolean(CLEAN_LOCAL, parseObject.getBoolean("CleanLocal"));
                                startActivity(new Intent(PGSplashScreen.this, MainActivity.class));
                                finish();
                            }
                        }
                    });
        } else { dialog.show();}

    }

    private void setDialog() {
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
                        Prefs.putBoolean(DATA_SOURCE, false);
                        Prefs.putBoolean(CLEAN_LOCAL, false);
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
