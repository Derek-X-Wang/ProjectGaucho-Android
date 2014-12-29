package com.intbridge.projects.projectgaucho;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
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

public class MapsActivity extends FragmentActivity implements SearchView.OnQueryTextListener{

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private SearchView search;
    private ListView searchListView;
    private SearchSuggestions searchSuggestions;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e("onCreate","Before setActionBar()");
        setActionBar();
        Log.e("onCreate","After setActionBar()");
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();

        // Get the intent, verify the action and get the query
        //Intent intent = getIntent();
        //if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
         //   String query = intent.getStringExtra(SearchManager.QUERY);
         //   //doMySearch(query);
        //}

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        //TODO: Issue with rotation
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar, menu);
        Log.e("onCreateOptionsMenu","Here 1");

        //search=(SearchView) findViewById(R.id.searchView);
//        searchListView=(ListView)findViewById(R.id.listView);
//        searchListView.setAdapter(new ArrayAdapter <String>(getApplicationContext(),android.R.layout.simple_list_item_1,searchSuggestions.getTotalStringList()));
//        searchListView.setTextFilterEnabled(true);
        Log.e("onCreateOptionsMenu","Here 2");
        // Associate searchable configuration with the SearchView
       //SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//       SearchView searchView = (SearchView) menu.findItem(R.id.searchView).getActionView();
       //search.setSearchableInfo( searchManager.getSearchableInfo(getComponentName()));

//        SearchManager searchManager = (SearchManager) getSystemService( Context.SEARCH_SERVICE );
//        Log.e("onCreateOptionsMenu","Here 3");
//        SearchView searchView = (SearchView) findViewById(R.id.searchView);
//        Log.e("onCreateOptionsMenu","Here 4");
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
//        Log.e("onCreateOptionsMenu","Here 5");
//        searchView.setOnQueryTextListener(this);

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
                new LatLng(34.412327,-119.846978),13));
    }


//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                FriendListActivity.this.finish();
//                FriendListActivity.this.overridePendingTransition(R.anim.stay_in, R.anim.bottom_out);
//                ActivityUtils.hideSoftKeyboard(this);
//
//                return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    /**
     * Set action bar
     *      1. properties
     *      2. title with custom font
     */
    private void setActionBar() {
        //getActionBar().setTitle("");
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.action_bar_top);

        //Log.e("setActionBar","Here 1");

        searchSuggestions = new SearchSuggestions(this,"UCSB");
        //Log.e("setActionBar","Here 2");

        search=(SearchView) findViewById(R.id.searchView);
        searchListView=(ListView)findViewById(R.id.listView);
        searchListView.setAdapter(new ArrayAdapter <String>(getApplicationContext(),android.R.layout.simple_list_item_1,searchSuggestions.getTotalStringList()));
        searchListView.setTextFilterEnabled(true);
        search.setOnQueryTextListener(this);
    }

//    /**
//     * Initialize friend list
//     */
//    private void initFriendList() {
//
//        // set up click listener
//        searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if(position>0 && position <= friendList.size()) {
//                    handelListItemClick((User)friendListAdapter.getItem(position - 1));
//                }
//            }
//        });
//    }
//
//    /**
//     * Navigate to share activity form here
//     * @param user user
//     */
//    private void handelListItemClick(User user) {
//        // close search view if its visible
//        if (searchView.isShown()) {
//            searchMenuItem.collapseActionView();
//            searchView.setQuery("", false);
//        }
//
//        // pass selected user and sensor to share activity
//        Intent intent = new Intent(this, ShareActivity.class);
//        intent.putExtra("com.score.senzors.pojos.User", user);
//        intent.putExtra("com.score.senzors.pojos.Sensor", application.getCurrentSensor());
//        this.startActivity(intent);
//        this.overridePendingTransition(R.anim.right_in, R.anim.stay_in);
//    }
//
//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        this.overridePendingTransition(R.anim.stay_in, R.anim.bottom_out);
//    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
//        Log.e("onQueryTextChange","Here 1");
//        if (newText != null && newText.length() > 0) {
//            Log.e("onQueryTextChange","Here 2");
//            ArrayList<String> filteredList = searchSuggestions.generateFilteredStringList(newText);
//            Log.e("onQueryTextChange","Here 3");
//            Log.d("this is my newText", newText);
//            String[] stringList = new String[ filteredList.size() ];
//            Log.e("onQueryTextChange","Here 4");
//            stringList = filteredList.toArray(stringList);
//            Log.e("onQueryTextChange","Here 5");
//            //Log.d("this is my array", "arr: " + Arrays.toString(arr));
//            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
//                    MapsActivity.this,
//                    android.R.layout.simple_list_item_1,
//                    stringList);
//            searchListView.setAdapter(arrayAdapter);
//        }
        if (TextUtils.isEmpty(newText))
        {
            searchListView.clearTextFilter();
        }
        else
        {
            searchListView.setFilterText(newText.toString());
        }
        return true;
    }

}
