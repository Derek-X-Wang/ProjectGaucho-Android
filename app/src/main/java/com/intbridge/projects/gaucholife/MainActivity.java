package com.intbridge.projects.gaucholife;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.database.MatrixCursor;
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
import java.util.List;


public class MainActivity extends Activity implements SearchView.OnQueryTextListener, SearchView.OnFocusChangeListener, SearchView.OnSuggestionListener, View.OnClickListener{

    private SearchView search;
    private SearchSuggestions searchSuggestions;
    private List<String> items;
    private SearchAdapter searchAdapter = null;
    
    private MapsFragment mapsFragemnt;
    private DiningFragment diningFragment;
    private SettingsFragment settingsFragment;

    IconWithTextView tabMaps;
    IconWithTextView tabDining;
    IconWithTextView tabSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setActionBar();
        initView();

        mapsFragemnt = new MapsFragment();
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.fragment_content, mapsFragemnt)
                    .commit();
        }

        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    //Your code goes here
                    Log.e("Start", " here1");
                    PGDatabaseManager databaseManager = new PGDatabaseManager();
                    databaseManager.getUCSBCommonsDataFromHTML("2015", "08", "19");
                    Log.e("Start", " here2");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();


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

    private void initView(){

        tabMaps = (IconWithTextView)findViewById(R.id.tab_maps);
        tabDining = (IconWithTextView)findViewById(R.id.tab_dining);
        tabSettings = (IconWithTextView)findViewById(R.id.tab_settings);
        
        tabMaps.setOnClickListener(this);
        tabDining.setOnClickListener(this);
        tabSettings.setOnClickListener(this);
        
        tabMaps.setIconAlpha(1.0f);
        
    }

    @Override
    public void onClick(View v) {
        resetOtherTabs();
        ActionBar actionBar = getActionBar();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        switch (v.getId()){
            case R.id.tab_maps:
                tabMaps.setIconAlpha(1.0f);
                if(actionBar != null && !actionBar.isShowing()) actionBar.show();
                if(mapsFragemnt == null) mapsFragemnt = new MapsFragment();
                if(mapsFragemnt.isAdded()) break;
                ft.replace(R.id.fragment_content, mapsFragemnt);
                ft.commit();
                break;
            case R.id.tab_dining:
                tabDining.setIconAlpha(1.0f);
                if (!search.isIconified()) search.setIconified(true);
                if(actionBar != null && actionBar.isShowing()) actionBar.hide();
                if(diningFragment == null) diningFragment = new DiningFragment();
                if(diningFragment.isAdded()) break;
                ft.replace(R.id.fragment_content, diningFragment);
                ft.commit();
                break;
            case R.id.tab_settings:
                tabSettings.setIconAlpha(1.0f);
                if (!search.isIconified()) search.setIconified(true);
                if(actionBar != null && actionBar.isShowing()) actionBar.hide();
                if(settingsFragment == null) settingsFragment = new SettingsFragment();
                if(settingsFragment.isAdded()) break;
                ft.replace(R.id.fragment_content, settingsFragment);
                ft.commit();
                break;
        }
    }

    private void resetOtherTabs(){
        tabMaps.setIconAlpha(0);
        tabDining.setIconAlpha(0);
        tabSettings.setIconAlpha(0);
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
