package com.intbridge.projects.gaucholife;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements SearchView.OnQueryTextListener, SearchView.OnFocusChangeListener, SearchView.OnSuggestionListener, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private SearchView search;
    private SearchSuggestions searchSuggestions;
    private List<String> items;
    private SearchAdapter searchAdapter = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //set up actionbar
        setActionBar();

        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar, menu);

        // setup flip button
        Button flipButton = (Button) findViewById(R.id.button);

        flipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FlipActivity.class);
                startActivity(intent);
                finish();
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    // this will handle the click on action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_layout:
                Toast.makeText(getApplicationContext(), "Layout feature is coming soon!", Toast.LENGTH_LONG).show();
                return true;
            case R.id.action_direction:
                Toast.makeText(getApplicationContext(), "Click the marker and get navigation!", Toast.LENGTH_LONG).show();
                return true;
            case R.id.action_settings:
                Toast.makeText(getApplicationContext(), "No setting currently", Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
    // here, I move the camera to UCSB TODO: move the camera based on user location ( which campus )
    private void setUpMap() {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(34.412327, -119.846978), 13));
        mMap.getUiSettings().setZoomControlsEnabled(false);
    }


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
        setMarkerWithAnimation(key,lalo.get(0),lalo.get(1));
        // hide the keyboard
        InputMethodManager imm = (InputMethodManager)getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(search.getWindowToken(), 0);

        return true;
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {

        Intent navigation = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + marker.getPosition().latitude + "," + marker.getPosition().longitude + "&mode=w"));
        startActivity(navigation);
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

    // set the marker and focus on that marker
    public void setMarkerWithAnimation(String key,Double la,Double lo){
        mMap.clear();
        LatLng lalo = new LatLng(la,lo);
        Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(lalo)
                        .title(key)
        );
        mMap.setOnMarkerClickListener(this);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lalo, 13));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17), 2000, null);
        marker.showInfoWindow();

    }

}