package com.intbridge.projects.gaucholife;

import android.app.Application;
import android.content.ContextWrapper;

import com.parse.Parse;
import com.pixplicity.easyprefs.library.Prefs;

/**
 *
 * Created by Derek on 8/25/2015.
 */
public class PGApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize the Prefs class
        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("wnZKHTwacIEEBLC7XlzrVDOoKvnEbnNkDZD0liCN")
                .clientKey("Ai5HkwN7nJVKWT4R3MoiYhlb9Hik7SOPiK8i5LaR")
                .server("https://gaucholife.herokuapp.com/parse/") // The trailing slash is important.
                .enableLocalDataStore()
                .build()
        );
    }
}
