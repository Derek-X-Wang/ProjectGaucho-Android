package com.intbridge.projects.projectgaucho;

import android.app.ActionBar;
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
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
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
    private ListView searchListView;
    private SearchSuggestions searchSuggestions;
    private List<String> items;
    private SearchAdapter searchAdapter = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Log.e("onCreate", "Before setActionBar()");
        setActionBar();
        //Log.e("onCreate", "After setActionBar()");


        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar, menu);

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
    private void setUpMap() {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(34.412327, -119.846978), 13));
        mMap.getUiSettings().setZoomControlsEnabled(false);
    }


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
        search.setOnSuggestionListener(this);
    }


    @Override
    public boolean onQueryTextSubmit(String query) {

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        //Log.d("this is my newText", newText);

        if (TextUtils.isEmpty(newText))
        {
            //Log.e("onQueryTextChange","B 1");
            loadData(null);
        }
        else
        {
            //Log.e("onQueryTextChange","B 2");
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

    @Override
    public boolean onSuggestionSelect(int position) {

        return false;
    }

    @Override
    public boolean onSuggestionClick(int position) {

        //Log.e("onSuggestionClick","Here 1");
        String key = searchAdapter.getKey(position);
        //Log.e("onSuggestionClick","key is "+ key);
        ArrayList<Double> lalo = searchSuggestions.getLaLo(key);
        //Log.e("onSuggestionClick","la is "+ lalo.get(0)+" and lo is "+lalo.get(1));
        setMarkerWithAnimation(lalo.get(0),lalo.get(1));

        return true;
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {

        //Log.e("onMarkerClick","Here 1");
        Intent navigation = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + marker.getPosition().latitude + "," + marker.getPosition().longitude));
        //Log.e("onMarkerClick","Here 2");
        startActivity(navigation);
        //Log.e("onMarkerClick","Here 3");
        return true;
    }

    private void loadData(String query) {

        // Load data from list to cursor
        String[] columns = new String[]{"_id", "text"};
        Object[] temp = new Object[]{0, "default"};

        MatrixCursor cursor = new MatrixCursor(columns);
        //Log.e("loadData","Here 1");
        if(query==null) {
            //Log.e("loadData","Here 2");
            items = searchSuggestions.getTotalStringList();
        }else {
            //Log.e("loadData","Here 3");
            items = searchSuggestions.generateFilteredStringList(query);
        }
        for (int i = 0; i < items.size(); i++) {
            temp[0] = i;
            temp[1] = items.get(i);

            cursor.addRow(temp);
        }
        searchAdapter = new SearchAdapter(this, cursor, items);
        //Log.e("loadData","Here 4");
        search.setSuggestionsAdapter(searchAdapter);
        //Log.e("loadData","Here 5");
        searchSuggestions.resetFilteredStringList();
    }

    public void setMarkerWithAnimation(Double la,Double lo){
        mMap.clear();
        LatLng lalo = new LatLng(la,lo);
        mMap.addMarker(new MarkerOptions()
                        .position(lalo)
        );
        mMap.setOnMarkerClickListener(this);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lalo, 13));

        mMap.animateCamera(CameraUpdateFactory.zoomTo(17), 2000, null);
    }

}