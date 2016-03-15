package com.intbridge.projects.gaucholife.controllers;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.intbridge.projects.gaucholife.MainActivity;
import com.intbridge.projects.gaucholife.PGDatabaseManager;
import com.intbridge.projects.gaucholife.R;

/**
 * Store Notification
 * Created by Derek on 3/14/2016.
 */
public class NotificationFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_notification, container, false);

        return v;
    }
}
