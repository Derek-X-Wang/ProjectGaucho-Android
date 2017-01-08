package com.intbridge.projects.gaucholife;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.intbridge.projects.gaucholife.controllers.DiningFragment;
import com.intbridge.projects.gaucholife.controllers.MapsFragment;
import com.intbridge.projects.gaucholife.controllers.SettingsFragment;
import com.intbridge.projects.gaucholife.utils.ClientStatManager;
import com.intbridge.projects.gaucholife.views.IconWithTextView;

import java.util.Date;
import java.util.Map;


public class MainActivity extends Activity implements View.OnClickListener{

    private MapsFragment mapsFragemnt;
    private DiningFragment diningFragment;
    private SettingsFragment settingsFragment;

    private int currentTab = 0;
    IconWithTextView tabMaps;
    IconWithTextView tabDining;
    IconWithTextView tabSettings;

    private boolean dataSource = true;
    private boolean cleanLocal = false;

    Date currentDate;
    int dateLoaded = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
        }else{
            dataSource = savedInstanceState.getBoolean("DATASOURCE");
            cleanLocal = savedInstanceState.getBoolean("CLEANLOCAL");
            currentTab = savedInstanceState.getInt("CURRENTTAB");
        }
        mapsFragemnt = new MapsFragment();
        diningFragment = new DiningFragment();
        settingsFragment = new SettingsFragment();

        initView();

//        PGDatabaseManager pgDatabaseManager = new PGDatabaseManager();
//        currentDate = new Date();
//        currentDate = pgDatabaseManager.addDays(currentDate,14);
////        currentDate = null;
//        new SyncWebRequestTask().execute(currentDate);

        // send user stat TODO: uncomment when release
        //ClientStatManager.sendUserStatus(this);

    }

    public int getCurrentTab(){
        return currentTab;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("CURRENTTAB",currentTab);
        outState.putBoolean("DATASOURCE",dataSource);
        outState.putBoolean("CLEANLOCAL", cleanLocal);
        outState.putBoolean("ONRESUME", true);
    }

    public Map<Integer, Map> getTempDataStorage() {
        return diningFragment.getTempDataStorage();
    }

    private void initView(){

        tabMaps = (IconWithTextView)findViewById(R.id.tab_maps);
        tabDining = (IconWithTextView)findViewById(R.id.tab_dining);
        tabSettings = (IconWithTextView)findViewById(R.id.tab_settings);

        tabMaps.setOnClickListener(this);
        tabDining.setOnClickListener(this);
        tabSettings.setOnClickListener(this);

        resetOtherTabs();
        initFragments();
        switch (currentTab) {
            case 0:
                tabMaps.setIconAlpha(1.0f);
                getFragmentManager().beginTransaction()
                        .show(mapsFragemnt)
                        .commit();
                break;
            case 1:
                tabDining.setIconAlpha(1.0f);
                getFragmentManager().beginTransaction()
                        .show(diningFragment)
                        .commit();
                break;
            case 2:
                tabSettings.setIconAlpha(1.0f);
                getFragmentManager().beginTransaction()
                        .show(settingsFragment)
                        .commit();
                break;
        }
    }

    private void initFragments(){
        // order matter, if we want the walkaround works, for two onShow fragment
        // the one that added later will show
        getFragmentManager().beginTransaction().add(R.id.fragment_content, mapsFragemnt)
                .hide(mapsFragemnt)
                .commit();
        getFragmentManager().beginTransaction().add(R.id.fragment_content, diningFragment)
                .hide(diningFragment)
                .commit();
        getFragmentManager().beginTransaction().add(R.id.fragment_content, settingsFragment)
                .hide(settingsFragment)
                .commit();
        //walkaround for map floating search bar no menu item
        getFragmentManager().beginTransaction()
                .show(mapsFragemnt)
                .commit();
    }

    @Override
    public void onClick(View v) {
        resetOtherTabs();
        //ActionBar actionBar = getActionBar();
        switch (v.getId()){
            case R.id.tab_maps:
                currentTab = 1;
                tabMaps.setIconAlpha(1.0f);
                hideAllFragments();
                getFragmentManager().beginTransaction()
                        .show(mapsFragemnt)
                        .commit();
                break;
            case R.id.tab_dining:
                currentTab = 2;
                tabDining.setIconAlpha(1.0f);
                hideAllFragments();
                getFragmentManager().beginTransaction()
                        .show(diningFragment)
                        .commit();
                break;
            case R.id.tab_settings:
                currentTab = 3;
                tabSettings.setIconAlpha(1.0f);
                hideAllFragments();
                getFragmentManager().beginTransaction()
                        .show(settingsFragment)
                        .commit();
                break;
        }
    }

    private void hideAllFragments() {
        getFragmentManager().beginTransaction()
                .hide(mapsFragemnt)
                .commit();
        getFragmentManager().beginTransaction()
                .hide(diningFragment)
                .commit();
        getFragmentManager().beginTransaction()
                .hide(settingsFragment)
                .commit();
    }

    private void resetOtherTabs(){
        tabMaps.setIconAlpha(0);
        tabDining.setIconAlpha(0);
        tabSettings.setIconAlpha(0);
    }


    // used for update parse.com data
    private class SyncWebRequestTask extends AsyncTask<Date, Integer, Map<String, Map>> {
        PGDatabaseManager databaseManager;

        @Override
        protected void onPreExecute() {
            if(databaseManager == null){
                databaseManager = new PGDatabaseManager();
            }
        }

        @Override
        protected Map<String, Map> doInBackground(Date... params) {
            // params comes from the execute() call: use params[0] for the first.
            currentDate = params[0];
            Map<String, Map> result = databaseManager.getUCSBCommonsDataFromHTML(currentDate);
            int dateInt = databaseManager.convertDateToInteger(currentDate);
            databaseManager.getParseObjectFromHTML(dateInt, result);
            return result;
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(Map<String, Map> result) {
            Log.e("Mcurrent: ", databaseManager.convertDateToInteger(currentDate) + "");
            dateLoaded--;
            if(dateLoaded > 0){
                currentDate = databaseManager.addDays(currentDate,1);
                new SyncWebRequestTask().execute(currentDate);
            }
        }
    }


}
