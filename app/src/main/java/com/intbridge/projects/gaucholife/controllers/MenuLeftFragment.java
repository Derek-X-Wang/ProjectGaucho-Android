package com.intbridge.projects.gaucholife.controllers;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.intbridge.projects.gaucholife.R;

/**
 * left menu
 * Created by Derek on 3/7/2016.
 */
public class MenuLeftFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_menu_left, container, false);
    }
}
