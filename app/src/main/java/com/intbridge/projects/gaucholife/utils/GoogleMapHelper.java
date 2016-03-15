package com.intbridge.projects.gaucholife.utils;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Build;

import com.google.android.gms.maps.MapFragment;
import com.intbridge.projects.gaucholife.R;

/**
 * Since three fragments use google map
 * Extract some common functions
 * Created by Derek on 3/13/2016.
 */
public class GoogleMapHelper {
    public static MapFragment getMapFragment(Fragment fragment) {
        FragmentManager fm = null;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            fm = fragment.getFragmentManager();
        } else {
            fm = fragment.getChildFragmentManager();
        }
        return (MapFragment) fm.findFragmentById(R.id.map_bus);
    }
}
