package com.intbridge.projects.gaucholife;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.intbridge.projects.gaucholife.controllers.CouponsFragment;
import com.intbridge.projects.gaucholife.controllers.DiningFragment;
import com.intbridge.projects.gaucholife.controllers.MapsFragment;
import com.intbridge.projects.gaucholife.controllers.SettingsFragment;
import com.intbridge.projects.gaucholife.utils.ClientStatManager;
import com.intbridge.projects.gaucholife.views.IconWithTextView;

import java.util.Date;
import java.util.Map;


public class MainActivity extends Activity implements View.OnClickListener{

    private CouponsFragment couponsFragment;
    private MapsFragment mapsFragemnt;
    private DiningFragment diningFragment;
    private SettingsFragment settingsFragment;

    private int currentTab = 0;
    IconWithTextView tabCoupons;
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
        couponsFragment = new CouponsFragment();
        mapsFragemnt = new MapsFragment();
        diningFragment = new DiningFragment();
        settingsFragment = new SettingsFragment();
//        mapsFragemnt.setRetainInstance(true);
//        diningFragment.setRetainInstance(true);
//        settingsFragment.setRetainInstance(true);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            dataSource = extras.getBoolean("DATASOURCE");
            cleanLocal = extras.getBoolean("CLEANLOCAL");
        }
        Bundle bundle = new Bundle();
        bundle.putBoolean("DATASOURCE",dataSource);
        bundle.putBoolean("CLEANLOCAL", cleanLocal);
        diningFragment.setArguments(bundle);
        //TODO: 我只能说无语了。。。已经试了将近12个小时，还是没解决，何弃疗了。。问题在于activity重建时会自动attach Fragments。之后我init时fragment的view又建了一次，所以call了两次
//        if (savedInstanceState != null){
////            getFragmentManager().beginTransaction()
////                    .detach(mapsFragemnt)
////                    .commit();
////            getFragmentManager().beginTransaction()
////                    .detach(diningFragment)
////                    .commit();
////            getFragmentManager().beginTransaction()
////                    .detach(settingsFragment)
////                    .commit();
////
////            mapsFragemnt = new MapsFragment();
////            diningFragment = new DiningFragment();
////            settingsFragment = new SettingsFragment();
////
////            diningFragment.setArguments(bundle);
//
//            Log.e("attachornotM ",mapsFragemnt.isDetached()+ "");
//            Log.e("attachornotD ",diningFragment.isDetached()+"");
//            Log.e("attachornotS ", settingsFragment.isDetached()+"");
//            Log.e("addornotM ",mapsFragemnt.isAdded()+"");
//            Log.e("addornotD ",diningFragment.isAdded()+"");
//            Log.e("addornotS ", settingsFragment.isAdded() + "");
//            Log.e("hideornotM ",mapsFragemnt.isHidden()+"");
//            Log.e("hideornotD ",diningFragment.isHidden()+"");
//            Log.e("hideornotS ",settingsFragment.isHidden()+"");
//            //reInitView();
//            initView();
//        }else{
//            initView();
//        }
        initView();

//        PGDatabaseManager pgDatabaseManager = new PGDatabaseManager();
//        currentDate = new Date();
//        currentDate = pgDatabaseManager.addDays(currentDate,14);
////        currentDate = null;
//        new SyncWebRequestTask().execute(currentDate);

        // send user stat TODO: uncomment when release
        //ClientStatManager.sendUserStatus(this);

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

        tabCoupons = (IconWithTextView)findViewById(R.id.tab_coupons);
        tabMaps = (IconWithTextView)findViewById(R.id.tab_maps);
        tabDining = (IconWithTextView)findViewById(R.id.tab_dining);
        tabSettings = (IconWithTextView)findViewById(R.id.tab_settings);

        tabCoupons.setOnClickListener(this);
        tabMaps.setOnClickListener(this);
        tabDining.setOnClickListener(this);
        tabSettings.setOnClickListener(this);

        resetOtherTabs();
        switch (currentTab) {
            case 0:
                tabMaps.setIconAlpha(1.0f);
                initFragments();
                getFragmentManager().beginTransaction()
                        .show(mapsFragemnt)
                        .commit();
                break;
            case 1:
                tabDining.setIconAlpha(1.0f);
                initFragments();
                getFragmentManager().beginTransaction()
                        .show(diningFragment)
                        .commit();
                break;
            case 2:
                tabSettings.setIconAlpha(1.0f);
                initFragments();
                getFragmentManager().beginTransaction()
                        .show(settingsFragment)
                        .commit();
                break;
            case 3:
                tabCoupons.setIconAlpha(1.0f);
                initFragments();
                getFragmentManager().beginTransaction()
                        .show(couponsFragment)
                        .commit();
                break;
        }
    }

    private void initFragments(){
        getFragmentManager().beginTransaction().add(R.id.fragment_content, couponsFragment)
                .hide(couponsFragment)
                .commit();
        getFragmentManager().beginTransaction().add(R.id.fragment_content, diningFragment)
                .hide(diningFragment)
                .commit();
        getFragmentManager().beginTransaction().add(R.id.fragment_content, mapsFragemnt)
                .hide(mapsFragemnt)
                .commit();
        getFragmentManager().beginTransaction().add(R.id.fragment_content, settingsFragment)
                .hide(settingsFragment)
                .commit();
    }

    @Override
    public void onClick(View v) {
        resetOtherTabs();
        //ActionBar actionBar = getActionBar();
        switch (v.getId()){
            case R.id.tab_maps:
                currentTab = 0;
                tabMaps.setIconAlpha(1.0f);
                hideAllFragments();
                getFragmentManager().beginTransaction()
                        .show(mapsFragemnt)
                        .commit();
                break;
            case R.id.tab_dining:
                currentTab = 1;
                tabDining.setIconAlpha(1.0f);
                hideAllFragments();
                getFragmentManager().beginTransaction()
                        .show(diningFragment)
                        .commit();
                break;
            case R.id.tab_settings:
                currentTab = 2;
                tabSettings.setIconAlpha(1.0f);
                hideAllFragments();
                getFragmentManager().beginTransaction()
                        .show(settingsFragment)
                        .commit();
                break;
            case R.id.tab_coupons:
                currentTab = 3;
                tabCoupons.setIconAlpha(1.0f);
                hideAllFragments();
                getFragmentManager().beginTransaction()
                        .show(couponsFragment)
                        .commit();
                break;
        }
    }

    private void hideAllFragments() {
        getFragmentManager().beginTransaction()
                .hide(couponsFragment)
                .commit();
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
        tabCoupons.setIconAlpha(0);
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
