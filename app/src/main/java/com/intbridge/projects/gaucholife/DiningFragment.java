package com.intbridge.projects.gaucholife;


import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Arrays;
import java.util.List;


/**
 *
 */
public class DiningFragment extends Fragment {

    private static final String[] TITLE = new String[] { "头条", "房产", "另一面", "女人",
            "财经", "数码", "情感", "科技" };

    public static final String BUNDLE_TITLE = "title";
    private String mTitle = "DefaultValue";

    private List<String> mDatas = Arrays.asList("短信1", "短信2", "短信3", "短信4",
            "短信5", "短信6", "短信7", "短信8", "短信9");
//	private List<String> mDatas = Arrays.asList("短信", "收藏", "推荐");

    private MultiSelectionIndicator mIndicatorCommon;
    private MultiSelectionIndicator mIndicatorDate;
    private MultiSelectionIndicator mIndicatorMeal;

    private List<String> commons;
    private List<String> dates;
    private List<String> meals;


    public DiningFragment() {
        // Required empty public constructor
    }

    public static DiningFragment newInstance(String title)
    {
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_TITLE, title);
        DiningFragment fragment = new DiningFragment();
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

        // Initialize the ViewPager and set an adapter
//        ViewPager pager = (ViewPager) v.findViewById(R.id.diningViewPager);
//        pager.setAdapter(new PagerAdapter() {
//            @Override
//            public int getCount() {
//                return 0;
//            }
//
//            @Override
//            public boolean isViewFromObject(View view, Object o) {
//                return false;
//            }
//        });
//
//        // Bind the tabs to the ViewPager
//        PagerSlidingTabStrip tabsDate = (PagerSlidingTabStrip) v.findViewById(R.id.tabs_date);
//        PagerSlidingTabStrip tabsCommon = (PagerSlidingTabStrip) v.findViewById(R.id.tabs_common);
//        PagerSlidingTabStrip tabsMeal = (PagerSlidingTabStrip) v.findViewById(R.id.tabs_meal);
//        tabs.setViewPager(pager);

//        Bundle arguments = getArguments();
//        if (arguments != null)
//        {
//            mTitle = arguments.getString(BUNDLE_TITLE);
//        }
//
//        TextView tv = (TextView)v.findViewById(R.id.testText);
//        tv.setText(mTitle);
        //tv.setGravity(Gravity.CENTER);

        mIndicatorCommon = (MultiSelectionIndicator) v.findViewById(R.id.msi_common);
        mIndicatorDate = (MultiSelectionIndicator) v.findViewById(R.id.msi_date);
        mIndicatorMeal = (MultiSelectionIndicator) v.findViewById(R.id.msi_meal);

        commons = Arrays.asList("Carrillo", "De La Guerra", "Ortega","Portola");
        dates = Arrays.asList("20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30");
        meals = Arrays.asList("Breakfast", "Lunch", "Dinner");

        mIndicatorCommon.setTabItemTitles(Arrays.asList("Carrillo", "De La Guerra", "Ortega","Portola"));
        mIndicatorDate.setTabItemTitles(Arrays.asList("20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30"));
        mIndicatorMeal.setTabItemTitles(Arrays.asList("Breakfast", "Lunch", "Dinner"));

        mIndicatorCommon.setCallbackManager(new MultiSelectionIndicator.CallbackManager() {
            @Override
            public void notifyChange(int position) {
                changeInnerFragementData(0,commons.get(position));
            }
        });
        mIndicatorDate.setCallbackManager(new MultiSelectionIndicator.CallbackManager(){
            @Override
            public void notifyChange(int position) {
                changeInnerFragementData(1,dates.get(position));
            }
        });
        mIndicatorMeal.setCallbackManager(new MultiSelectionIndicator.CallbackManager(){
            @Override
            public void notifyChange(int position) {
                changeInnerFragementData(2,meals.get(position));
            }
        });
        
        return v;
    }

    private void changeInnerFragementData(int code, String newString){
        switch (code){
            case 0:
                Log.e("Callback: ","Common is changed to "+newString);
                break;
            case 1:
                Log.e("Callback: ","Date is changed to "+newString);
                break;
            case 2:
                Log.e("Callback: ","Meal is changed to "+newString);
                break;
        }
    }

}
