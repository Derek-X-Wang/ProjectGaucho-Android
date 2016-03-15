package com.intbridge.projects.gaucholife.controllers;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

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
        View v = inflater.inflate(R.layout.fragment_bus, container,
                false);
        mMapView = getMapFragment();
        googleMap = mMapView.getMap();
        setUpMap();
        return v;
    }

    private MapFragment getMapFragment() {
        FragmentManager fm = null;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            fm = getFragmentManager();
        } else {
            fm = getChildFragmentManager();
        }
        return (MapFragment) fm.findFragmentById(R.id.map_bus);
    }

    private void setUpMap() {
        googleMap.setMyLocationEnabled(true);
        View myLocationButton = ((View)mMapView.getView().findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) myLocationButton.getLayoutParams();
        // position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(0, 0, 30, 30);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(34.412327, -119.846978), 13));
        googleMap.getUiSettings().setZoomControlsEnabled(false);
    }
}
