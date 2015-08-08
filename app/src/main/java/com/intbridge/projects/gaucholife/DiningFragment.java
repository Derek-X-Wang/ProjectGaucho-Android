package com.intbridge.projects.gaucholife;


import android.app.Fragment;
import android.os.Bundle;
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

    private MultiSelectionIndicator mIndicator;

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

//        mIndicator = (MultiSelectionIndicator) v.findViewById(R.id.msi_date);
//        mIndicator.setTabItemTitles(mDatas);
        
        return v;
    }



//    class TabPageIndicatorAdapter extends FragmentPagerAdapter {
//        public TabPageIndicatorAdapter(FragmentManager fm) {
//            super(fm);
//        }
//
//        @Override
//        public Fragment getItem(int position) {
//            //新建一个Fragment来展示ViewPager item的内容，并传递参数
//            Fragment fragment = new ItemFragment();
//            Bundle args = new Bundle();
//            args.putString("arg", TITLE[position]);
//            fragment.setArguments(args);
//
//            return fragment;
//        }
//
//        @Override
//        public CharSequence getPageTitle(int position) {
//            return TITLE[position % TITLE.length];
//        }
//
//        @Override
//        public int getCount() {
//            return TITLE.length;
//        }
//    }
}
