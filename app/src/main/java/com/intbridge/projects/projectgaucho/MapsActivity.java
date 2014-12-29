package com.intbridge.projects.projectgaucho;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.MatrixCursor;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import android.app.ActionBar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import xmlwise.Plist;
import xmlwise.XmlParseException;

public class MapsActivity extends FragmentActivity implements SearchView.OnQueryTextListener, SearchView.OnFocusChangeListener{

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private SearchView search;
    private ListView searchListView;
    private SearchSuggestions searchSuggestions;
    private List<String> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e("onCreate", "Before setActionBar()");
        setActionBar();
        Log.e("onCreate", "After setActionBar()");


        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        //TODO: Issue with rotation
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(34.412327, -119.846978), 13));
    }


    /**
     * Set action bar
     * 1. properties
     * 2. title with custom font
     */
    private void setActionBar() {
        //getActionBar().setTitle("");
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.action_bar_top);

        searchSuggestions = new SearchSuggestions(this, "UCSB");

        search = (SearchView) findViewById(R.id.searchView);

        int autoCompleteTextViewID = getResources().getIdentifier("android:id/search_src_text", null, null);
        AutoCompleteTextView searchAutoCompleteTextView = (AutoCompleteTextView) search.findViewById(autoCompleteTextViewID);
        searchAutoCompleteTextView.setThreshold(0);

        loadData(null);
        search.setOnQueryTextListener(this);
    }


    @Override
    public boolean onQueryTextSubmit(String query) {

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        Log.d("this is my newText", newText);

        if (TextUtils.isEmpty(newText))
        {
            Log.e("onQueryTextChange","B 1");
            loadData(null);
        }
        else
        {
            Log.e("onQueryTextChange","B 2");
            loadData(newText);
        }

        return true;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus)
    {
        if (!hasFocus){
            //searchSuggestions.resetFilteredStringList();
        }

    }

    private void loadData(String query) {

        // Load data from list to cursor
        String[] columns = new String[]{"_id", "text"};
        Object[] temp = new Object[]{0, "default"};

        MatrixCursor cursor = new MatrixCursor(columns);
        Log.e("loadData","Here 1");
        if(query==null) {
            Log.e("loadData","Here 2");
            items = searchSuggestions.getTotalStringList();
        }else {
            Log.e("loadData","Here 3");
            items = searchSuggestions.generateFilteredStringList(query);
        }
        for (int i = 0; i < items.size(); i++) {
            temp[0] = i;
            temp[1] = items.get(i);

            cursor.addRow(temp);
        }
        Log.e("loadData","Here 4");
        search.setSuggestionsAdapter(new SearchAdapter(this, cursor, items));
        Log.e("loadData","Here 5");
        searchSuggestions.resetFilteredStringList();
    }

}