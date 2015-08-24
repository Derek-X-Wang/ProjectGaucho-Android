package com.intbridge.projects.gaucholife;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by Derek on 8/25/2015.
 */
public class PGApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "wnZKHTwacIEEBLC7XlzrVDOoKvnEbnNkDZD0liCN", "Ai5HkwN7nJVKWT4R3MoiYhlb9Hik7SOPiK8i5LaR");
    }
}
