package com.intbridge.projects.gaucholife.controllers;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.intbridge.projects.gaucholife.MainActivity;
import com.intbridge.projects.gaucholife.R;
import com.intbridge.projects.gaucholife.utils.ClientStatManager;
import com.intbridge.projects.gaucholife.utils.CloudCodeManager;
import com.parse.Parse;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.util.List;

/**
 * Coupons Fragment
 * Created by Derek on 11/7/2015.
 */
public class CouponsFragment extends Fragment implements GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener{

    private MainActivity host;
    private MapFragment mMapView;
    private GoogleMap googleMap;

    private View mainLayout;
    private View welcomeLayout;
    private View couponLayout;

    private View couponView;
    private TextView storeTitleText;
    private TextView couponDetail;
    private TextView addressText;

    private String lastCouponID = "";
    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            Log.e("Coupon UI: ", "h2");
            if (android.os.Build.VERSION.SDK_INT >= 16)
                couponView.setBackground(new BitmapDrawable(getResources(), bitmap));
            else
                couponView.setBackgroundDrawable(new BitmapDrawable(getResources(), bitmap));
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            Log.e("Coupon UI: ", "Picasso cannot load image");
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };
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

        mainLayout = v.findViewById(R.id.couponMainLayout);
        welcomeLayout = v.findViewById(R.id.couponWelcomeLayout);
        couponLayout = v.findViewById(R.id.couponResultLayout);

        couponView = couponLayout.findViewById(R.id.couponView);
        storeTitleText = (TextView)couponLayout.findViewById(R.id.couponViewTitle);
        couponDetail = (TextView)couponLayout.findViewById(R.id.couponViewDetail);
        addressText = (TextView)couponLayout.findViewById(R.id.couponAddress);

        updateUI();

        return v;
    }

    private void updateUI() {
        CloudCodeManager.pickRandomCoupon("");
        List<ParseObject> coupons = CloudCodeManager.pickRandomCoupon("");
        for (ParseObject coupon : coupons) {
            Log.e("coupon array has ", coupon.getString("title"));
        }
        ParseObject firstCoupon = coupons.get(0);
        ParseGeoPoint geoPoint = firstCoupon.getParseGeoPoint("site");
        ParseFile couponImageFile = firstCoupon.getParseFile("image");
        Uri uri = Uri.parse(couponImageFile.getUrl());
        Log.e("Coupon UI: ","h1");
        Picasso.with(getActivity()).load(uri).into(target);
        Log.e("Coupon UI: ", "h3");
//        try {
//            Bitmap couponBitmap = Picasso.with(getActivity()).load(uri).get();
//            if (android.os.Build.VERSION.SDK_INT >= 16)
//                couponView.setBackground(new BitmapDrawable(getResources(),couponBitmap));
//            else
//                couponView.setBackgroundDrawable(new BitmapDrawable(getResources(),couponBitmap));
//        } catch (IOException e) {
//            e.printStackTrace();
//            Log.e("Coupon UI: ","Picasso cannot load image");
//        }
        //couponView.setBackground();
        storeTitleText.setText(firstCoupon.getString("title"));
        couponDetail.setText(firstCoupon.getString("description"));
        addressText.setText(firstCoupon.getString("store"));
        setUpMap(firstCoupon.getString("title"), geoPoint.getLatitude(), geoPoint.getLongitude());
        
    }

    private void setUpMap(String key,Double la,Double lo) {
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        googleMap.getUiSettings().setScrollGesturesEnabled(false);
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

    @Override
    public boolean onMarkerClick(final Marker marker) {
        ClientStatManager.startGoogleMapApp(getActivity(), marker);
        return true;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        ClientStatManager.startGoogleMapApp(getActivity(), marker);
    }

}
