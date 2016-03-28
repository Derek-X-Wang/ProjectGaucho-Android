package com.intbridge.projects.gaucholife;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.intbridge.projects.gaucholife.controllers.BusFragment;
import com.intbridge.projects.gaucholife.controllers.CouponsFragment;
import com.intbridge.projects.gaucholife.controllers.DiningFragment;
import com.intbridge.projects.gaucholife.controllers.FeedbackFragment;
import com.intbridge.projects.gaucholife.controllers.MapsFragment;
import com.intbridge.projects.gaucholife.controllers.NotificationFragment;
import com.intbridge.projects.gaucholife.controllers.SettingsFragment;
import com.intbridge.projects.gaucholife.utils.ClientStatManager;
import com.intbridge.projects.gaucholife.views.IconWithTextView;

import java.util.Date;
import java.util.Map;

import com.nineoldandroids.view.ViewHelper;


public class MainActivity extends Activity{

    public static final String DATA_SOURCE = "DATASOURCE";
    public static final String CLEAN_LOCAL = "CLEANLOCAL";
    private CouponsFragment couponsFragment;
    private MapsFragment mapsFragemnt;
    private DiningFragment diningFragment;
    private BusFragment busFragment;
    private SettingsFragment settingsFragment;
    private NotificationFragment notificationFragment;
    private FeedbackFragment feedbackFragment;

    private int currentTab = R.id.tab_coupons;

    private DrawerLayout drawerLayout;

    private boolean dataSource = true;
    private boolean cleanLocal = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            currentTab = savedInstanceState.getInt("CURRENTTAB");
        }
        couponsFragment = new CouponsFragment();
        mapsFragemnt = new MapsFragment();
        diningFragment = new DiningFragment();
        busFragment = new BusFragment();
        settingsFragment = new SettingsFragment();
        notificationFragment = new NotificationFragment();
        feedbackFragment = new FeedbackFragment();

        initView();

        // Uncomment the code below to run the dinning common crawlers and upload data to Parse.com
//        PGDatabaseManager pgDatabaseManager = new PGDatabaseManager();
//        new SyncWebRequestTask().execute(pgDatabaseManager.addDays(new Date(),0));

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
        outState.putInt("CURRENTTAB", currentTab);
    }

    public Map<Integer, Map> getTempDataStorage() {
        return diningFragment.getTempDataStorage();
    }

    private void initView(){
        initFragments();
        //attachFragment(getFragment(currentTab));
        initDrawer();
    }

    private void initDrawer() {
        drawerLayout = (DrawerLayout)findViewById(R.id.activity_main);
        View menuLeft = findViewById(R.id.main_menu_left);
        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.7);
        DrawerLayout.LayoutParams params = (android.support.v4.widget.DrawerLayout.LayoutParams) menuLeft.getLayoutParams();
        params.width = width;
    }

    private void initFragments(){
        // order matter, if we want the walkaround works, for two onShow fragment
        // the one that added later will show
        getFragmentManager().beginTransaction()
                .add(R.id.fragment_content, getFragment(currentTab))
                .commit();
//        initFragment(couponsFragment);
//        initFragment(mapsFragemnt);
//        initFragment(diningFragment);
//        initFragment(busFragment);
//        initFragment(settingsFragment);
//        initFragment(notificationFragment);
//        initFragment(feedbackFragment);
//        //walkaround for map floating search bar no menu item
//        getFragmentManager().beginTransaction()
//                .show(mapsFragemnt)
//                .commit();
    }

    private void initFragment(Fragment fragment) {
        getFragmentManager().beginTransaction().add(R.id.fragment_content, fragment)
                .detach(fragment)
                .commit();
    }

    public void onContentFragmentChange(int id) {
        //detachFragment(getFragment(currentTab));
        //attachFragment(getFragment(id));
        replaceFragment(getFragment(id));
        currentTab = id;
        drawerLayout.closeDrawers();
    }

    private Fragment getFragment(int id) {
        switch (id){
            case R.id.tab_coupons:
                return couponsFragment;
            case R.id.tab_maps:
                return mapsFragemnt;
            case R.id.tab_dining:
                return diningFragment;
            case R.id.tab_bus:
                return busFragment;
            case R.id.tab_settings:
                return settingsFragment;
            case R.id.tab_notification:
                return notificationFragment;
            case R.id.tab_feedback:
                return feedbackFragment;
            default:
                return null;
        }
    }

    private void replaceFragment(Fragment fragment) {
        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_content, fragment)
                .commit();
    }

    private void attachFragment(Fragment fragment) {
        getFragmentManager().beginTransaction()
                .attach(fragment)
                .commit();
    }

    private void detachFragment(Fragment fragment) {
        getFragmentManager().beginTransaction()
                .detach(fragment)
                .commit();
    }

    protected Date currentDate;
    protected int dateLoaded = 14;
    // used for update parse.com data, backup for planB
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
            Map<String, Map> result = databaseManager.getUCSBCommonsDataFromHTML(params[0]);
            int dateInt = databaseManager.convertDateToInteger(params[0]);
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
