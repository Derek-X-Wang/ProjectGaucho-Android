package com.intbridge.projects.gaucholife.controllers;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.intbridge.projects.gaucholife.MainActivity;
import com.intbridge.projects.gaucholife.R;

/**
 * left menu
 * Created by Derek on 3/7/2016.
 */
public class MenuLeftFragment extends Fragment implements View.OnClickListener {

    MainActivity host;
    View tabCoupon;
    View tabMaps;
    View tabDining;
    View tabBus;
    View tabSetting;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_menu_left, container, false);

        initViews(v);
        return v;
    }

    private void initViews(View v) {
        host = (MainActivity)getActivity();

        tabCoupon = v.findViewById(R.id.tab_coupons);
        tabMaps = v.findViewById(R.id.tab_maps);
        tabDining = v.findViewById(R.id.tab_dining);
        tabBus = v.findViewById(R.id.tab_bus);
        tabSetting = v.findViewById(R.id.tab_settings);

        tabCoupon.setOnClickListener(this);
        tabMaps.setOnClickListener(this);
        tabDining.setOnClickListener(this);
        tabBus.setOnClickListener(this);
        tabSetting.setOnClickListener(this);

        setCurrentTabColor(host.getCurrentTab());
    }

    @Override
    public void onClick(View v) {
        resetTabsColor();
        setCurrentTabColor(v.getId());
        host.onContentFragmentChange(v.getId());
    }

    private void setCurrentTabColor(int id) {
        int currentTabColor = Color.parseColor("#FFDEDEDE");
        switch (id) {
            case R.id.tab_coupons:
                tabCoupon.setBackgroundColor(currentTabColor);
                break;
            case R.id.tab_maps:
                tabMaps.setBackgroundColor(currentTabColor);
                break;
            case R.id.tab_dining:
                tabDining.setBackgroundColor(currentTabColor);
                break;
            case R.id.tab_bus:
                tabBus.setBackgroundColor(currentTabColor);
                break;
            case R.id.tab_settings:
                tabSetting.setBackgroundColor(currentTabColor);
                break;
        }
    }

    private void resetTabsColor() {
        tabCoupon.setBackgroundColor(Color.WHITE);
        tabMaps.setBackgroundColor(Color.WHITE);
        tabDining.setBackgroundColor(Color.WHITE);
        tabBus.setBackgroundColor(Color.WHITE);
        tabSetting.setBackgroundColor(Color.WHITE);
    }
}
