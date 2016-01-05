package com.intbridge.projects.gaucholife.controllers;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.tbouron.shakedetector.library.ShakeDetector;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.intbridge.projects.gaucholife.MainActivity;
import com.intbridge.projects.gaucholife.PGDatabaseManager;
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
import java.util.Date;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

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
    private TextView remainingCoupon;
    private Button redeemButton;

    private String lastCouponID = "";
    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            //Log.e("Coupon UI: ", "h2");
            Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            if (android.os.Build.VERSION.SDK_INT >= 16)
                couponView.setBackground(new BitmapDrawable(getResources(), maskBitmap(mutableBitmap)));
            else
                couponView.setBackgroundDrawable(new BitmapDrawable(getResources(), maskBitmap(mutableBitmap)));
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
        mMapView = getMapFragment();
        googleMap = mMapView.getMap();

        mainLayout = v.findViewById(R.id.couponMainLayout);
        welcomeLayout = v.findViewById(R.id.couponWelcomeLayout);
        couponLayout = v.findViewById(R.id.couponResultLayout);

        couponView = couponLayout.findViewById(R.id.couponView);
        storeTitleText = (TextView)couponLayout.findViewById(R.id.couponViewTitle);
        couponDetail = (TextView)couponLayout.findViewById(R.id.couponViewDetail);
        addressText = (TextView)couponLayout.findViewById(R.id.couponAddress);

        remainingCoupon = (TextView)v.findViewById(R.id.remainingCoupon);
        if (PGDatabaseManager.isRestoreCouponAmount()) {
            remainingCoupon.setText("15");
        }
        redeemButton = (Button)v.findViewById(R.id.redeemButton);
        redeemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CloudCodeManager.redeemCoupon(lastCouponID);
                Toast.makeText(getActivity(), "Redeem!", Toast.LENGTH_SHORT).show();
            }
        });

        ShakeDetector.create(getActivity(), new ShakeDetector.OnShakeListener() {
            @Override
            public void OnShake() {
                if (host.getCurrentTab() == 3) {
                    if (welcomeLayout.getVisibility() == View.VISIBLE) {
                        welcomeLayout.setVisibility(View.GONE);
                        couponLayout.setVisibility(View.VISIBLE);
                    }
                    int currentCouponAmount = Integer.parseInt(remainingCoupon.getText().toString());
                    if (currentCouponAmount > 0) {
                        new UpdateCouponTask().execute(lastCouponID);
                    } else {
                        Log.e("Shake: ", currentCouponAmount+" no more coupon");
                        new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("No Coupon Available!")
                                .setContentText("Please wait for another day.")
                                .setConfirmText("Okay")
                                .showCancelButton(false)
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        // reuse previous dialog instance
                                        sDialog.dismiss();

                                    }
                                }).show();
                    }

                }
            }
        });
        return v;
    }

    private MapFragment getMapFragment() {
        FragmentManager fm = null;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            fm = getFragmentManager();
        } else {
            fm = getChildFragmentManager();
        }
        return (MapFragment) fm.findFragmentById(R.id.couponMapView);
    }

    private void updateUI() {
        List<ParseObject> coupons = CloudCodeManager.pickRandomCoupon("");
//        for (ParseObject coupon : coupons) {
//            Log.e("coupon array has ", coupon.getString("title"));
//        }
        ParseObject firstCoupon = coupons.get(0);
        ParseGeoPoint geoPoint = firstCoupon.getParseGeoPoint("site");
        ParseFile couponImageFile = firstCoupon.getParseFile("image");
        Uri uri = Uri.parse(couponImageFile.getUrl());
        Picasso.with(getActivity()).load(uri).into(target);
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

    private Bitmap maskBitmap(Bitmap bitmap) {
        LinearGradient shader = new LinearGradient(bitmap.getWidth()/2, bitmap.getHeight(),bitmap.getWidth()/2, 0, 0xFFFFFFFF, 0x00FFFFFF, Shader.TileMode.CLAMP);
        Paint p = new Paint();
        p.setDither(true);
        p.setShader(shader);
        Canvas c = new Canvas(bitmap);
        c.drawRect(0, 0, bitmap.getWidth(), bitmap.getHeight(), p);

        return bitmap;
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

    @Override
    public void onResume() {
        super.onResume();
        ShakeDetector.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        ShakeDetector.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ShakeDetector.destroy();
    }

    private class UpdateCouponTask extends AsyncTask<String, Integer, List<ParseObject>> {
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            ShakeDetector.stop();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.show();

            googleMap.clear();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                couponView.setBackground(getResources().getDrawable(R.drawable.storephotoplacehoder, getActivity().getTheme()));
            } else if (android.os.Build.VERSION.SDK_INT >= 16) {
                couponView.setBackground(getResources().getDrawable(R.drawable.storephotoplacehoder));
            } else {
                couponView.setBackgroundDrawable(getResources().getDrawable(R.drawable.storephotoplacehoder));
            }
        }

        @Override
        protected List<ParseObject> doInBackground(String... params) {
            // params comes from the execute() call: use params[0] for the first.

            return CloudCodeManager.pickRandomCoupon(params[0]);

        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(List<ParseObject> coupons) {
            ParseObject firstCoupon = coupons.get(0);
            ParseGeoPoint geoPoint = firstCoupon.getParseGeoPoint("site");
            ParseFile couponImageFile = firstCoupon.getParseFile("image");
            Uri uri = Uri.parse(couponImageFile.getUrl());
            Picasso.with(getActivity()).load(uri).into(target);
            storeTitleText.setText(firstCoupon.getString("title"));
            couponDetail.setText(firstCoupon.getString("description"));
            addressText.setText(firstCoupon.getString("store"));
            setUpMap(firstCoupon.getString("title"), geoPoint.getLatitude(), geoPoint.getLongitude());
            lastCouponID = firstCoupon.getObjectId();
            int currentCouponAmount = Integer.parseInt(remainingCoupon.getText().toString());
            remainingCoupon.setText((currentCouponAmount-1)+"");
            progressDialog.dismiss();
            ShakeDetector.start();
        }
    }
}
