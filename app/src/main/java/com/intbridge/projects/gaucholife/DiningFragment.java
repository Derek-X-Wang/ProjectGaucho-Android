package com.intbridge.projects.gaucholife;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;


/**
 *
 */
public class DiningFragment extends Fragment {

    public static final String BUNDLE_TITLE = "title";

    private List<String> mDatas = Arrays.asList("短信1", "短信2", "短信3", "短信4",
            "短信5", "短信6", "短信7", "短信8", "短信9");

    private MultiSelectionIndicator mIndicatorCommon;
    private MultiSelectionIndicator mIndicatorDate;
    private MultiSelectionIndicator mIndicatorMeal;

    private List<String> commons;
    private List<String> dates;
    private List<String> meals;

    StickyHeaderListViewAdapter adapter;

    private PGDatabaseManager databaseManager;


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
        databaseManager = new PGDatabaseManager();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dining, container, false);

        initMultiSelectionIndicators(v);

        StickyListHeadersListView stickyList = (StickyListHeadersListView) v.findViewById(R.id.list);
        adapter = new StickyHeaderListViewAdapter(getActivity());
        stickyList.setAdapter(adapter);
        return v;
    }

    private void initMultiSelectionIndicators(View v) {
        mIndicatorCommon = (MultiSelectionIndicator) v.findViewById(R.id.msi_common);
        mIndicatorDate = (MultiSelectionIndicator) v.findViewById(R.id.msi_date);
        mIndicatorMeal = (MultiSelectionIndicator) v.findViewById(R.id.msi_meal);

        commons = Arrays.asList("Carrillo", "De La Guerra", "Ortega", "Portola");
        dates = Arrays.asList("20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30");
        meals = Arrays.asList("Breakfast", "Lunch", "Dinner");

        mIndicatorCommon.setTabItemTitles(Arrays.asList("Carrillo", "De La Guerra", "Ortega","Portola"));
        mIndicatorDate.setTabItemTitles(Arrays.asList("20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30"));
        mIndicatorMeal.setTabItemTitles(Arrays.asList("Breakfast", "Lunch", "Dinner"));

        mIndicatorCommon.setCallbackManager(new MultiSelectionIndicator.CallbackManager() {
            @Override
            public void notifyChange(int position) {
                changeFragmentData(0, commons.get(position));
            }
        });
        mIndicatorDate.setCallbackManager(new MultiSelectionIndicator.CallbackManager(){
            @Override
            public void notifyChange(int position) {
                changeFragmentData(1, dates.get(position));
            }
        });
        mIndicatorMeal.setCallbackManager(new MultiSelectionIndicator.CallbackManager(){
            @Override
            public void notifyChange(int position) {
                changeFragmentData(2,meals.get(position));
            }
        });
    }

    private void changeFragmentData(int code, String newString){
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

    public class StickyHeaderListViewAdapter extends BaseAdapter implements StickyListHeadersAdapter {

        private String[] countries;
        private LayoutInflater inflater;
        private Map<String, List> foodList;


        public StickyHeaderListViewAdapter(Context context) {
            inflater = LayoutInflater.from(context);
            //countries = context.getResources().getStringArray(R.array.countries);


        }

        @Override
        public int getCount() {
            return countries.length;
        }

        @Override
        public Object getItem(int position) {
            return countries[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.listview_dining_item, parent, false);
                //holder.text = (TextView) convertView.findViewById(R.id.text);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            //holder.text.setText(countries[position]);

            return convertView;
        }

        @Override
        public View getHeaderView(int position, View convertView, ViewGroup parent) {
            HeaderViewHolder holder;
            if (convertView == null) {
                holder = new HeaderViewHolder();
                convertView = inflater.inflate(R.layout.listview_dining_header, parent, false);
                //holder.text = (TextView) convertView.findViewById(R.id.text);
                convertView.setTag(holder);
            } else {
                holder = (HeaderViewHolder) convertView.getTag();
            }
            //set header text as first char in name
            String headerText = "" + countries[position].subSequence(0, 1).charAt(0);
            //holder.text.setText(headerText);
            return convertView;
        }

        @Override
        public long getHeaderId(int position) {
            //return the first character of the country as ID because this is what headers are based upon
            return countries[position].subSequence(0, 1).charAt(0);
        }

        class HeaderViewHolder {
            //TextView text;
        }

        class ViewHolder {
            //TextView text;
        }

    }

}
