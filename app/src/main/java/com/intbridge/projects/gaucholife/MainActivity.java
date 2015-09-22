package com.intbridge.projects.gaucholife;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.database.MatrixCursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class MainActivity extends Activity implements SearchView.OnQueryTextListener, SearchView.OnFocusChangeListener, SearchView.OnSuggestionListener, View.OnClickListener{

    private SearchView search;
    private SearchSuggestions searchSuggestions;
    private List<String> items;
    private SearchAdapter searchAdapter = null;
    
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

        setActionBar();

        if (savedInstanceState == null) {
        }else{
            dataSource = savedInstanceState.getBoolean("DATASOURCE");
            cleanLocal = savedInstanceState.getBoolean("CLEANLOCAL");
            currentTab = savedInstanceState.getInt("CURRENTTAB");
        }
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

        PGDatabaseManager pgDatabaseManager = new PGDatabaseManager();
//        currentDate = new Date();
//        currentDate = pgDatabaseManager.addDays(currentDate,14);
//        //currentDate = null;
//        new SyncWebRequestTask().execute(currentDate);

        // send user stat
        pgDatabaseManager.sendUserReport(this);

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
        switch (currentTab){
            case 0:
                tabMaps.setIconAlpha(1.0f);
                getFragmentManager().beginTransaction().add(R.id.fragment_content, diningFragment)
                        .hide(diningFragment)
                        .commit();
                getFragmentManager().beginTransaction().add(R.id.fragment_content, settingsFragment)
                        .hide(settingsFragment)
                        .commit();
                getFragmentManager().beginTransaction().add(R.id.fragment_content, mapsFragemnt)
                        .commit();
                break;
            case 1:
                tabDining.setIconAlpha(1.0f);
                getFragmentManager().beginTransaction().add(R.id.fragment_content, mapsFragemnt)
                        .hide(mapsFragemnt)
                        .commit();
                getFragmentManager().beginTransaction().add(R.id.fragment_content, settingsFragment)
                        .hide(settingsFragment)
                        .commit();
                getFragmentManager().beginTransaction().add(R.id.fragment_content, diningFragment)
                        .commit();
                break;
            case 2:
                tabSettings.setIconAlpha(1.0f);
                getFragmentManager().beginTransaction().add(R.id.fragment_content, diningFragment)
                        .hide(diningFragment)
                        .commit();
                getFragmentManager().beginTransaction().add(R.id.fragment_content, mapsFragemnt)
                        .hide(mapsFragemnt)
                        .commit();
                getFragmentManager().beginTransaction().add(R.id.fragment_content, settingsFragment)
                        .commit();
                break;
        }
        
    }

    @Override
    public void onClick(View v) {
        resetOtherTabs();
        ActionBar actionBar = getActionBar();
        switch (v.getId()){
            case R.id.tab_maps:
                currentTab = 0;
                tabMaps.setIconAlpha(1.0f);
                if(actionBar != null && !actionBar.isShowing()) actionBar.show();
                getFragmentManager().beginTransaction()
                        .hide(diningFragment)
                        .commit();
                getFragmentManager().beginTransaction()
                        .hide(settingsFragment)
                        .commit();
                getFragmentManager().beginTransaction()
                        .show(mapsFragemnt)
                        .commit();
                break;
            case R.id.tab_dining:
                currentTab = 1;
                tabDining.setIconAlpha(1.0f);
                if (!search.isIconified()) search.setIconified(true);
                if(actionBar != null && actionBar.isShowing()) actionBar.hide();
                getFragmentManager().beginTransaction()
                        .hide(mapsFragemnt)
                        .commit();
                getFragmentManager().beginTransaction()
                        .hide(settingsFragment)
                        .commit();
                getFragmentManager().beginTransaction()
                        .show(diningFragment)
                        .commit();
                break;
            case R.id.tab_settings:
                currentTab = 2;
                tabSettings.setIconAlpha(1.0f);
                if (!search.isIconified()) search.setIconified(true);
                if(actionBar != null && actionBar.isShowing()) actionBar.hide();
                getFragmentManager().beginTransaction()
                        .hide(mapsFragemnt)
                        .commit();
                getFragmentManager().beginTransaction()
                        .hide(diningFragment)
                        .commit();
                getFragmentManager().beginTransaction()
                        .show(settingsFragment)
                        .commit();
                break;
        }
    }

    private void resetOtherTabs(){
        tabMaps.setIconAlpha(0);
        tabDining.setIconAlpha(0);
        tabSettings.setIconAlpha(0);
    }


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

    /*
     * The code below will make the location search
    
     */
    // setup action bar
    private void setActionBar() {
        ActionBar actionBar = getActionBar();
        // setup the top view in action bar
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.action_bar_top);

        // get a filter for search
        searchSuggestions = new SearchSuggestions(this, "UCSB");

        search = (SearchView) findViewById(R.id.searchView);

        // setup search display threshold
        int autoCompleteTextViewID = getResources().getIdentifier("android:id/search_src_text", null, null);
        AutoCompleteTextView searchAutoCompleteTextView = (AutoCompleteTextView) search.findViewById(autoCompleteTextViewID);
        searchAutoCompleteTextView.setThreshold(0);

        // setup matrix cursor and adapter
        loadData(null);
        // setup listener
        search.setOnQueryTextListener(this);
        search.setOnSuggestionListener(this);
    }


    @Override
    public boolean onQueryTextSubmit(String query) {

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        if (TextUtils.isEmpty(newText))        {
            loadData(null);
        }
        else{
            loadData(newText);
        }
        return true;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus)
    {
        if (!hasFocus){
        }
    }

    @Override
    public boolean onSuggestionSelect(int position) {

        return false;
    }

    @Override
    public boolean onSuggestionClick(int position) {

        String key = searchAdapter.getKey(position);
        ArrayList<Double> lalo = searchSuggestions.getLaLo(key);
        mapsFragemnt.setMarkerWithAnimation(key,lalo.get(0),lalo.get(1));
        // hide the keyboard
        InputMethodManager imm = (InputMethodManager)getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(search.getWindowToken(), 0);

        return true;
    }

    // here, search suggestion is handled
    private void loadData(String query) {

        // Load data from list to cursor
        String[] columns = new String[]{"_id", "text"};
        Object[] temp = new Object[]{0, "default"};

        MatrixCursor cursor = new MatrixCursor(columns);
        if(query==null) {
            items = searchSuggestions.getTotalStringList();
        }else {
            items = searchSuggestions.generateFilteredStringList(query);
        }
        for (int i = 0; i < items.size(); i++) {
            temp[0] = i;
            temp[1] = items.get(i);

            cursor.addRow(temp);
        }
        searchAdapter = new SearchAdapter(this, cursor, items);
        search.setSuggestionsAdapter(searchAdapter);
        searchSuggestions.resetFilteredStringList();
    }

}
