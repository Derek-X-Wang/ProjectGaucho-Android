package com.intbridge.projects.gaucholife;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * Created by Derek on 9/9/2015.
 */
public class PGSplashScreen extends Activity {

    String now_playing, earned;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        /**
         * Showing splashscreen while making network calls to download necessary
         * data before launching the app Will use AsyncTask to make http call
         */
        PGFader.runAlphaAnimation(this, R.id.imgLogo);
        final ImageView logo = (ImageView)findViewById(R.id.imgLogo);
        //new PrefetchData().execute();
        ParseQuery query = ParseQuery.getQuery("ControlPanel");
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (object == null) {
                    Log.d("RBSE", "The getFirst request failed.");
                } else {
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
    }

}
