package com.intbridge.projects.gaucholife;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
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
                }
            }
        });
    }

    /**
     * Async Task to make http call
     */
    private class PrefetchData extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // before making http calls

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            /*
             * Will make http call here This call will download required data
             * before launching the app
             * example:
             * 1. Downloading and storing in SQLite
             * 2. Downloading images
             * 3. Fetching and parsing the xml / json
             * 4. Sending device information to server
             * 5. etc.,
             */


            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // After completing http call
            // will close this activity and lauch main activity
            Intent i = new Intent(PGSplashScreen.this, MainActivity.class);
            i.putExtra("now_playing", now_playing);
            i.putExtra("earned", earned);
            startActivity(i);

            // close this activity
            finish();
        }

    }
}
