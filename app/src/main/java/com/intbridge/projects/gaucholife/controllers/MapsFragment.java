package com.intbridge.projects.gaucholife.controllers;


import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.intbridge.projects.gaucholife.MainActivity;
import com.intbridge.projects.gaucholife.R;
import com.intbridge.projects.gaucholife.utils.ClientStatManager;
import com.intbridge.projects.gaucholife.utils.LocationHelper;
import com.intbridge.projects.gaucholife.utils.LocationSuggestion;
import com.intbridge.projects.gaucholife.utils.SearchSuggestions;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Derek Wang
 * 08/07/2015 *
 */
public class MapsFragment extends Fragment implements GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener{

    private MainActivity host;
    private MapFragment mMapView;
    private GoogleMap googleMap;
    private FloatingSearchView searchView;
    private SearchSuggestions searchSuggestions;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflat and return the layout
        View v = inflater.inflate(R.layout.fragment_maps, container,
                false);

        host = (MainActivity)getActivity();

        mMapView = getMapFragment();

        googleMap = mMapView.getMap();

        searchView = (FloatingSearchView)v.findViewById(R.id.floating_search_view);
        searchSuggestions = new SearchSuggestions(getActivity(), "UCSB");
        
        setUpMap();
        setUpFloatingSearchView();

        return v;
    }

    private MapFragment getMapFragment() {
        FragmentManager fm = null;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            fm = getFragmentManager();
        } else {
            fm = getChildFragmentManager();
        }
        return (MapFragment) fm.findFragmentById(R.id.mapView);
    }

    private void setUpFloatingSearchView() {
        searchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {

                if (!oldQuery.equals("") && newQuery.equals("")) {
                    searchView.clearSuggestions();
                } else {

                    searchView.showProgress();

                    List<LocationSuggestion> results = searchSuggestions.generateFilteredLocationSuggestionList(newQuery);
                    searchView.swapSuggestions(results);

                    searchView.hideProgress();

                }

                Log.d("MapF", "onSearchTextChanged()");
            }
        });

        searchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {

                LocationSuggestion locationSuggestion = (LocationSuggestion) searchSuggestion;
                String key = locationSuggestion.getLocationName();
                ArrayList<Double> lalo = searchSuggestions.getLaLo(key);
                setMarkerWithAnimation(key, lalo.get(0), lalo.get(1));
                // hide the keyboard
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);

                Log.d("MapF", "onSuggestionClicked()");

            }

            @Override
            public void onSearchAction() {

                Log.d("MapF", "onSearchAction()");
            }
        });

        searchView.setOnFocusChangeListener(new FloatingSearchView.OnFocusChangeListener() {
            @Override
            public void onFocus() {

                //show suggestions when search bar gains focus (typically history suggestions)
                searchView.swapSuggestions(searchSuggestions.getTotalLocationSuggestion());

                Log.d("MapF", "onFocus()");
            }

            @Override
            public void onFocusCleared() {
                searchView.clearSuggestions();
                Log.d("MapF", "onFocusCleared()");
            }
        });

        searchView.setOnHomeActionClickListener(new FloatingSearchView.OnHomeActionClickListener() {
            @Override
            public void onHomeClicked() {
                searchView.clearSuggestions();
                Log.d("MapF", "onHomeClicked()");
            }
        });

        searchView.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
            @Override
            public void onActionMenuItemSelected(MenuItem item) {

                if (item.getItemId() == R.id.action_currentlocation) {
                    LocationHelper location = new LocationHelper(getActivity());
                    if (location.getLocationStatus()) {
                        setBlueDotWithAnimation(location.getLatitude(), location.getLongitude());
                    } else {
                        location.showSettingsAlert();
                    }

                } else {

                    //just print action
                    Toast.makeText(getActivity(), "other pop",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    // here, I move the camera to UCSB TODO: move the camera based on user location ( which campus )
    private void setUpMap() {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(34.412327, -119.846978), 13));
        googleMap.getUiSettings().setZoomControlsEnabled(false);
    }

    // set the marker and focus on that marker
    public void setMarkerWithAnimation(String key,Double la,Double lo){
        googleMap.clear();
        LatLng lalo = new LatLng(la,lo);
        Marker marker = googleMap.addMarker(new MarkerOptions()
                        .position(lalo)
                        .title(key)
        );
        googleMap.setOnMarkerClickListener(this);
        googleMap.setOnInfoWindowClickListener(this);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lalo, 13));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(17), 2000, null);
        marker.showInfoWindow();

    }

    private void setBlueDotWithAnimation(Double la,Double lo){
        googleMap.clear();
        LatLng lalo = new LatLng(la,lo);
        Marker marker = googleMap.addMarker(new MarkerOptions()
                        .position(lalo)
                        .anchor(0.5f,0.5f)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.bluedot))
        );
        googleMap.setOnMarkerClickListener(this);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lalo, 13));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(17), 2000, null);
        marker.showInfoWindow();

    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        ClientStatManager.startGoogleMapApp(getActivity(),marker);
        return true;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        ClientStatManager.startGoogleMapApp(getActivity(),marker);
    }

}
