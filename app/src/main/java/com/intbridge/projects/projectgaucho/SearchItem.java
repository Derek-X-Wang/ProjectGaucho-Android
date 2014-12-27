package com.intbridge.projects.projectgaucho;

/**
 * Created by Derek on 12/26/2014.
 */
public class SearchItem {
    private String key;
    private Double la;
    private Double lo;

    SearchItem(String k, Double a, Double o){
        key = k;
        la = a;
        lo = o;
    }

    public String getKey(){
        return key;
    }

    public Double getLatitude(){
        return la;
    }

    public Double getLongitude(){
        return lo;
    }
}
