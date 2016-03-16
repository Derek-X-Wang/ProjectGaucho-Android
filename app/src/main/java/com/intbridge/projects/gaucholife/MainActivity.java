package com.intbridge.projects.gaucholife;

import android.app.Activity;
import android.app.Fragment;
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

        if (savedInstanceState == null) {
        }else{
            dataSource = savedInstanceState.getBoolean("DATASOURCE");
            cleanLocal = savedInstanceState.getBoolean("CLEANLOCAL");
            currentTab = savedInstanceState.getInt("CURRENTTAB");
        }
        couponsFragment = new CouponsFragment();
        mapsFragemnt = new MapsFragment();
        diningFragment = new DiningFragment();
        busFragment = new BusFragment();
        settingsFragment = new SettingsFragment();
        notificationFragment = new NotificationFragment();
        feedbackFragment = new FeedbackFragment();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            dataSource = extras.getBoolean("DATASOURCE");
            cleanLocal = extras.getBoolean("CLEANLOCAL");
        }
        Bundle bundle = new Bundle();
        bundle.putBoolean("DATASOURCE",dataSource);
        bundle.putBoolean("CLEANLOCAL", cleanLocal);
        diningFragment.setArguments(bundle);

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
        outState.putInt("CURRENTTAB",currentTab);
        outState.putBoolean("DATASOURCE",dataSource);
        outState.putBoolean("CLEANLOCAL", cleanLocal);
        outState.putBoolean("ONRESUME", true);
    }

    public Map<Integer, Map> getTempDataStorage() {
        return diningFragment.getTempDataStorage();
    }

    private void initView(){

        initFragments();
        switch (currentTab) {
            case R.id.tab_coupons:
                getFragmentManager().beginTransaction()
                        .show(couponsFragment)
                        .commit();
                break;
            case R.id.tab_maps:
                getFragmentManager().beginTransaction()
                        .show(mapsFragemnt)
                        .commit();
                break;
            case R.id.tab_dining:
                getFragmentManager().beginTransaction()
                        .show(diningFragment)
                        .commit();
                break;
            case R.id.tab_bus:
                getFragmentManager().beginTransaction()
                        .show(busFragment)
                        .commit();
                break;
            case R.id.tab_settings:
                getFragmentManager().beginTransaction()
                        .show(settingsFragment)
                        .commit();
                break;
            case R.id.tab_notification:
                getFragmentManager().beginTransaction()
                        .show(notificationFragment)
                        .commit();
                break;
            case R.id.tab_feedback:
                getFragmentManager().beginTransaction()
                        .show(feedbackFragment)
                        .commit();
                break;
        }

        initDrawer();
    }

    private void initDrawer() {
        drawerLayout = (DrawerLayout)findViewById(R.id.activity_main);
        View menuLeft = findViewById(R.id.main_menu_left);
        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.7);
        DrawerLayout.LayoutParams params = (android.support.v4.widget.DrawerLayout.LayoutParams) menuLeft.getLayoutParams();
        params.width = width;
//        drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
//            @Override
//            public void onDrawerSlide(View drawerView, float slideOffset) {
//                View mContent = drawerLayout.getChildAt(0);
//                View mMenu = drawerView;
//                float scale = 1 - slideOffset;
//                float rightScale = 0.8f + scale * 0.2f;
//
//                float leftScale = 1 - 0.3f * scale;
//
//                ViewHelper.setScaleX(mMenu, leftScale);
//                ViewHelper.setScaleY(mMenu, leftScale);
//                ViewHelper.setAlpha(mMenu, 0.6f + 0.4f * (1 - scale));
//                ViewHelper.setTranslationX(mContent,
//                        mMenu.getMeasuredWidth() * (1 - scale));
//                ViewHelper.setPivotX(mContent, 0);
//                ViewHelper.setPivotY(mContent,
//                        mContent.getMeasuredHeight() / 2);
//                mContent.invalidate();
//                ViewHelper.setScaleX(mContent, rightScale);
//                ViewHelper.setScaleY(mContent, rightScale);
//            }
//
//            @Override
//            public void onDrawerOpened(View drawerView) {
//
//            }
//
//            @Override
//            public void onDrawerClosed(View drawerView) {
//
//            }
//
//            @Override
//            public void onDrawerStateChanged(int newState) {
//
//            }
//        });
        
    }

    private void initFragments(){
        // order matter, if we want the walkaround works, for two onShow fragment
        // the one that added later will show
        getFragmentManager().beginTransaction().add(R.id.fragment_content, mapsFragemnt)
                .hide(mapsFragemnt)
                .commit();
        getFragmentManager().beginTransaction().add(R.id.fragment_content, couponsFragment)
                .hide(couponsFragment)
                .commit();
        getFragmentManager().beginTransaction().add(R.id.fragment_content, diningFragment)
                .hide(diningFragment)
                .commit();
        getFragmentManager().beginTransaction().add(R.id.fragment_content, busFragment)
                .hide(busFragment)
                .commit();
        getFragmentManager().beginTransaction().add(R.id.fragment_content, settingsFragment)
                .hide(settingsFragment)
                .commit();
        getFragmentManager().beginTransaction().add(R.id.fragment_content, notificationFragment)
                .hide(notificationFragment)
                .commit();
        getFragmentManager().beginTransaction().add(R.id.fragment_content, feedbackFragment)
                .hide(feedbackFragment)
                .commit();
        //walkaround for map floating search bar no menu item
        getFragmentManager().beginTransaction()
                .show(mapsFragemnt)
                .commit();
    }

    public void onContentFragmentChange(int id) {
        switch (id){
            case R.id.tab_coupons:
                currentTab = R.id.tab_coupons;
                hideAllFragments();
                getFragmentManager().beginTransaction()
                        .show(couponsFragment)
                        .commit();
                break;
            case R.id.tab_maps:
                currentTab = R.id.tab_maps;
                hideAllFragments();
                getFragmentManager().beginTransaction()
                        .show(mapsFragemnt)
                        .commit();
                break;
            case R.id.tab_dining:
                currentTab = R.id.tab_dining;
                hideAllFragments();
                getFragmentManager().beginTransaction()
                        .show(diningFragment)
                        .commit();
                break;
            case R.id.tab_bus:
                currentTab = R.id.tab_bus;
                hideAllFragments();
                getFragmentManager().beginTransaction()
                        .show(busFragment)
                        .commit();
                break;
            case R.id.tab_settings:
                currentTab = R.id.tab_settings;
                hideAllFragments();
                getFragmentManager().beginTransaction()
                        .show(settingsFragment)
                        .commit();
                break;
            case R.id.tab_notification:
                currentTab = R.id.tab_notification;
                hideAllFragments();
                getFragmentManager().beginTransaction()
                        .show(notificationFragment)
                        .commit();
                break;
            case R.id.tab_feedback:
                currentTab = R.id.tab_feedback;
                hideAllFragments();
                getFragmentManager().beginTransaction()
                        .show(feedbackFragment)
                        .commit();
                break;
        }
        drawerLayout.closeDrawers();
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
                .hide(busFragment)
                .commit();
        getFragmentManager().beginTransaction()
                .hide(settingsFragment)
                .commit();
        getFragmentManager().beginTransaction()
                .hide(notificationFragment)
                .commit();
        getFragmentManager().beginTransaction()
                .hide(feedbackFragment)
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
