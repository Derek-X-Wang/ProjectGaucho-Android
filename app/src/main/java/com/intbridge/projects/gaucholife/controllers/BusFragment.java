package com.intbridge.projects.gaucholife.controllers;

import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Color;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.intbridge.projects.gaucholife.R;
import com.intbridge.projects.gaucholife.utils.TempBusData;
import com.intbridge.projects.gaucholife.views.MultiSelectionIndicator;

import java.util.Arrays;
import java.util.List;

/**
 * Bus Schedule
 * Created by Derek on 3/7/2016.
 */
public class BusFragment extends Fragment {

    private MapFragment mMapView;
    private GoogleMap googleMap;

    private MultiSelectionIndicator mIndicatorBus;
    private List<String> busList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflat and return the layout
        View v = inflater.inflate(R.layout.fragment_bus, container,
                false);
        mMapView = getMapFragment();
        googleMap = mMapView.getMap();
        setUpMap();
        initMultiSelectionIndicator(v);
        return v;
    }

    private void initMultiSelectionIndicator(View v) {
        mIndicatorBus = (MultiSelectionIndicator) v.findViewById(R.id.msi_bus_route);
        busList = Arrays.asList("11", "27");
        mIndicatorBus.setTabItemTitles(busList);
        mIndicatorBus.setCallbackManager(new MultiSelectionIndicator.CallbackManager() {
            @Override
            public void notifyChange(int position) {
                googleMap.clear();
                String currentIndicator = busList.get(position);
                showBusInfo(currentIndicator);
            }
        });
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
        //default showing bus11
        showBusInfo("11");

    }

    private void showBusInfo(String s) {
        drawBusRoute(s);
        switch (s) {
            case "11":
                googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(34.430095, -119.869745))
                        .title("Storke&Hollister"));
                googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(34.415589, -119.848418))
                        .title("UCSB"));
                googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(34.424263, -119.835243))
                        .title("SB airport"));
                break;
            case "27":
                googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(34.428437, -119.875664))
                        .title("Santa Felicia & Marketplace"));
                googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(34.417388, -119.866514))
                        .title("El Colegio & Camino Corto"));
                googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(34.410398, -119.853475))
                        .title("Embarcadero & Sábado Tarde"));
                googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(34.415531, -119.848272))
                        .title("UCSB"));
                break;
        }
    }

    private void drawBusRoute(String s) {
        googleMap.addPolyline(new PolylineOptions()
                .addAll(TempBusData.routes.get(s))
                .color(Color.BLUE));
    }
}
