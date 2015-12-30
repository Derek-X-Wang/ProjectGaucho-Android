package com.intbridge.projects.gaucholife.controllers;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.intbridge.projects.gaucholife.MainActivity;
import com.intbridge.projects.gaucholife.R;

/**
 * Created by Derek on 11/7/2015.
 */
public class CouponsFragment extends Fragment {

    private MainActivity host;
    private MapFragment mMapView;
    private GoogleMap googleMap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_coupons, container, false);

        host = (MainActivity)getActivity();
        mMapView = (MapFragment) getFragmentManager().findFragmentById(R.id.couponMapView);
        googleMap = mMapView.getMap();

        //setUpMap();
        return v;
    }

    private void setUpMap() {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(34.412327, -119.846978), 13));
        googleMap.getUiSettings().setZoomControlsEnabled(false);
    }

    // set the marker and focus on that marker
//    public void setMarkerWithAnimation(String key,Double la,Double lo){
//        googleMap.clear();
//        LatLng lalo = new LatLng(la,lo);
//        Marker marker = googleMap.addMarker(new MarkerOptions()
//                        .position(lalo)
//                        .title(key)
//        );
//        googleMap.setOnMarkerClickListener(this);
//        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lalo, 13));
//        googleMap.animateCamera(CameraUpdateFactory.zoomTo(17), 2000, null);
//        marker.showInfoWindow();
//
//    }
}
