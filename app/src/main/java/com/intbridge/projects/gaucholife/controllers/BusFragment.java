package com.intbridge.projects.gaucholife.controllers;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.intbridge.projects.gaucholife.R;

/**
 * Bus Schedule
 * Created by Derek on 3/7/2016.
 */
public class BusFragment extends Fragment {

    private MapFragment mMapView;
    private GoogleMap googleMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflat and return the layout
        View v = inflater.inflate(R.layout.fragment_maps, container,
                false);
        mMapView = (MapFragment) getFragmentManager().findFragmentById(R.id.map_bus);
        googleMap = mMapView.getMap();
        setUpMap();
        return v;
    }

    private void setUpMap() {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(34.412327, -119.846978), 13));
        googleMap.getUiSettings().setZoomControlsEnabled(false);
    }
}
