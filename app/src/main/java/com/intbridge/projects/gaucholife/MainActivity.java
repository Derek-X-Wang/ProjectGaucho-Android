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
import com.intbridge.projects.gaucholife.utils.DinningDataUpload;
import com.intbridge.projects.gaucholife.views.IconWithTextView;

import java.util.Date;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends Activity {

    private MapsFragment mapsFragemnt;
    private DiningFragment diningFragment;
    private SettingsFragment settingsFragment;

    private int currentTab = 0;
    @BindView(R.id.tab_maps) IconWithTextView tabMaps;
    @BindView(R.id.tab_dining) IconWithTextView tabDining;
    @BindView(R.id.tab_settings) IconWithTextView tabSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if (savedInstanceState != null) {
            currentTab = savedInstanceState.getInt("CURRENTTAB");
        }
        mapsFragemnt = new MapsFragment();
        diningFragment = new DiningFragment();
        settingsFragment = new SettingsFragment();

        initView();

//        DinningDataUpload dataUpload = new DinningDataUpload(5);
//        dataUpload.uploadParseServer(7);


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
    }

    public Map<Integer, Map> getTempDataStorage() {
        return diningFragment.getTempDataStorage();
    }

    private void initView(){

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

    @OnClick(R.id.tab_maps)
    public void onTabMapsClicked(View view) {
        resetOtherTabs();
        hideAllFragments();
        currentTab = 0;
        tabMaps.setIconAlpha(1.0f);
        getFragmentManager().beginTransaction()
                .show(mapsFragemnt)
                .commit();
    }

    @OnClick(R.id.tab_dining)
    public void onDiningClicked(View view) {
        resetOtherTabs();
        hideAllFragments();
        currentTab = 1;
        tabDining.setIconAlpha(1.0f);
        getFragmentManager().beginTransaction()
                .show(diningFragment)
                .commit();
    }

    @OnClick(R.id.tab_settings)
    public void onSettingsClicked(View view) {
        resetOtherTabs();
        hideAllFragments();
        currentTab = 2;
        tabSettings.setIconAlpha(1.0f);
        getFragmentManager().beginTransaction()
                .show(settingsFragment)
                .commit();
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

}
