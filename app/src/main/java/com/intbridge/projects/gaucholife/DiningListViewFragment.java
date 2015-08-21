package com.intbridge.projects.gaucholife;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * Derek Wang
 * 08212015
 */
public class DiningListViewFragment extends Fragment {
    public DiningListViewFragment() {
        // Required empty public constructor
    }

    public static DiningListViewFragment newInstance(String common,String date,String meal)
    {
        Bundle bundle = new Bundle();
        bundle.putString("COMMON", common);
        bundle.putString("DATE", date);
        bundle.putString("MEAL", meal);
        DiningListViewFragment fragment = new DiningListViewFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dining, container, false);

        Bundle arguments = getArguments();
        if (arguments != null)
        {
            //mTitle = arguments.getString(BUNDLE_TITLE);
        }
        return v;
    }
}

