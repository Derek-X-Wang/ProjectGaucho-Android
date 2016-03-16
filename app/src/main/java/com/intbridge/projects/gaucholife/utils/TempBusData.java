package com.intbridge.projects.gaucholife.utils;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Temp storage for bus info
 * Created by Derek on 3/15/2016.
 */
public class TempBusData {
    public static final Map<String,List<LatLng>> routes;
    private static final List<LatLng> route11;
    private static final List<LatLng> route27;
    //private static final Map<String,List<Location>> TRIM_KEY_REGEXPS2;
    static{
        route11 = new ArrayList<>();
        route11.add(new LatLng(34.430272, -119.869791));
        route11.add(new LatLng(34.417453, -119.869754));
        route11.add(new LatLng(34.417329, -119.853756));
        route11.add(new LatLng(34.415337, -119.851471));
        route11.add(new LatLng(34.415612, -119.848390));
        route11.add(new LatLng(34.418354, -119.848189));
        route11.add(new LatLng(34.419168, -119.847796));
        route11.add(new LatLng(34.417741, -119.845386));
        route11.add(new LatLng(34.416979, -119.843124));
        route11.add(new LatLng(34.416068, -119.842041));
        route11.add(new LatLng(34.416330, -119.840215));
        route11.add(new LatLng(34.414929, -119.839344));
        route11.add(new LatLng(34.416472, -119.835687));
        route11.add(new LatLng(34.418084, -119.833333));
        route11.add(new LatLng(34.419005, -119.835179));
        route11.add(new LatLng(34.425412, -119.835308));
        route11.add(new LatLng(34.426828, -119.830308));
        route11.add(new LatLng(34.436067, -119.830694));

        route27 = new ArrayList<>();
        route27.add(new LatLng(34.415607, -119.848461));
        route27.add(new LatLng(34.415404, -119.851371));
        route27.add(new LatLng(34.413757, -119.853261));
        route27.add(new LatLng(34.410411, -119.853409));
        route27.add(new LatLng(34.410446, -119.856255));
        route27.add(new LatLng(34.410902, -119.856297));
        route27.add(new LatLng(34.411164, -119.856913));
        route27.add(new LatLng(34.411830, -119.857083));
        route27.add(new LatLng(34.411848, -119.858697));
        route27.add(new LatLng(34.417419, -119.858718));
        route27.add(new LatLng(34.417419, -119.869740));
        route27.add(new LatLng(34.430153, -119.869718));
        route27.add(new LatLng(34.430083, -119.875919));
        route27.add(new LatLng(34.427018, -119.875580));
        route27.add(new LatLng(34.426685, -119.875155));
        route27.add(new LatLng(34.426668, -119.869697));

        routes = new LinkedHashMap<>();
        routes.put("11",route11);
        routes.put("27",route27);


    }
}
