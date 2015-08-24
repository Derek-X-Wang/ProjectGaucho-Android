package com.intbridge.projects.gaucholife;


import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;


/**
 *
 */
public class DiningFragment extends Fragment{

    private int currentCommon = 0;
    private int currentDay = 0;
    private int currentMeal = 0;

    private MultiSelectionIndicator mIndicatorCommon;
    private MultiSelectionIndicator mIndicatorDate;
    private MultiSelectionIndicator mIndicatorMeal;

    private List<String> commons;
    private List<String> dates;
    private List<String> meals;

    StickyHeaderListViewAdapter adapter;

    private PGDatabaseManager databaseManager;
    private Map<Integer, Map> tempDataStorage;


    public DiningFragment() {
        // Required empty public constructor
    }

    public static DiningFragment newInstance(String title)
    {
        Bundle bundle = new Bundle();
        DiningFragment fragment = new DiningFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseManager = new PGDatabaseManager();
        tempDataStorage = new LinkedHashMap<>();
        currentDate = new Date();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dining, container, false);

        initMultiSelectionIndicators(v);

        StickyListHeadersListView stickyList = (StickyListHeadersListView) v.findViewById(R.id.list);
        adapter = new StickyHeaderListViewAdapter(getActivity());
        stickyList.setAdapter(adapter);
        stickyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView t = (TextView)view.findViewById(R.id.listview_dining_item);
                ImageView i = (ImageView)view.findViewById(R.id.listview_dining_item_heart);

                String text = (String)t.getText();
                // check text with favor list
                // existed -> user would like to delete a favor
                // not existed -> add new item to the favor list
            }
        });
        new WebRequestTask().execute();
        return v;
    }

    private void initMultiSelectionIndicators(View v) {
        mIndicatorCommon = (MultiSelectionIndicator) v.findViewById(R.id.msi_common);
        mIndicatorDate = (MultiSelectionIndicator) v.findViewById(R.id.msi_date);
        mIndicatorMeal = (MultiSelectionIndicator) v.findViewById(R.id.msi_meal);

        String[] dateStrings = convertDateToStringArray(new Date());

        commons = Arrays.asList("Carrillo", "De La Guerra", "Ortega", "Portola");
        dates = new ArrayList<>();
        dates.add(dateStrings[2]);
        meals = Arrays.asList("Breakfast", "Brunch", "Lunch", "Dinner", "Late Night");

        mIndicatorCommon.setTabItemTitles(commons);
        mIndicatorDate.setTabItemTitles(dates);
        mIndicatorMeal.setTabItemTitles(meals);

        mIndicatorCommon.setCallbackManager(new MultiSelectionIndicator.CallbackManager() {
            @Override
            public void notifyChange(int position) {
                currentCommon = position;
                changeFragmentData();
            }
        });
        mIndicatorDate.setCallbackManager(new MultiSelectionIndicator.CallbackManager(){
            @Override
            public void notifyChange(int position) {
                currentDay = position;
                changeFragmentData();
            }
        });
        mIndicatorMeal.setCallbackManager(new MultiSelectionIndicator.CallbackManager() {
            @Override
            public void notifyChange(int position) {
                currentMeal = position;
                changeFragmentData();
            }
        });
    }

    public String[] convertDateToStringArray(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        String year = String.format("%d", cal.get(Calendar.YEAR));
        String month = String.format("%02d", cal.get(Calendar.MONTH) + 1); // Note: zero based!
        String day = String.format("%02d", cal.get(Calendar.DAY_OF_MONTH));
        return new String[]{year,month,day};
    }

    public int convertDateToInteger(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        // Note: zero based!
        String dateString = String.format("%d%02d%02d",cal.get(Calendar.YEAR),cal.get(Calendar.MONTH) + 1,cal.get(Calendar.DAY_OF_MONTH));
        return Integer.parseInt(dateString);
    }

    private void changeFragmentData(){
        // get current status
        String commonString = commons.get(currentCommon);
        String dayString = dates.get(currentDay);
        String mealString = meals.get(currentMeal);
        int dateInt = matchDayWithTempDictDate(dayString);
        if(dateInt == -1){
            // no data available for the given dateInt
            adapter.setFoodList(null);
            adapter.notifyDataSetChanged();
        }else{
            // unpack the dict, is there a better way?
            Map<String, Map> unpackedDict1 = tempDataStorage.get(dateInt);
            Map<String, Map> unpackedDict2 = unpackedDict1.get(commonString);
            Map<String, List> unpackedDict3 = unpackedDict2.get(mealString);

            // set new data to adapter
            adapter.setFoodList(unpackedDict3);
            adapter.notifyDataSetChanged();
        }


//        Log.e("Callback: ","Common is changed to "+newString);
//        Log.e("Callback: ","Date is changed to "+newString);
//        Log.e("Callback: ","Meal is changed to "+newString);

    }

    private int matchDayWithTempDictDate(String day){
        Set<Integer> keys = tempDataStorage.keySet();
        int dayInt = Integer.parseInt(day);
        for(int i:keys){
            if( i%100 == dayInt) return i;
        }
        return -1;
    }

    private Date currentDate;
    private int loadDayLimit = 9;
    private int loadLoopIndicator = 0;
    private void AsyncTaskProgressCheck(Map<String, Map> result){
        // store the result in the fragment
        tempDataStorage.put(convertDateToInteger(currentDate),result);
        // get day 2 digit string
        String[] dateStrings = convertDateToStringArray(currentDate);
        // first MultiSelectionIndicator of day is added already, avoid to add the first again
        if(loadLoopIndicator == 0){
            // first round
        }else{
            // need to add new day to MultiSelectionIndicator
            dates.add(dateStrings[2]);
            mIndicatorDate.setTabItemTitles(dates);
            mIndicatorDate.updateSelection(currentDay);
            mIndicatorDate.invalidate();
        }
        loadLoopIndicator++;
        if(loadLoopIndicator < loadDayLimit){
            // execute next
            new WebRequestTask().execute(0);
        }else{
            // reset
            loadLoopIndicator = 0;
        }
    }
    private class WebRequestTask extends AsyncTask<Integer, Integer, Map<String, Map>> {
        @Override
        protected void onPreExecute() {
            if(databaseManager == null){
                databaseManager = new PGDatabaseManager();
            }
            if(tempDataStorage == null){
                tempDataStorage = new LinkedHashMap<>();
            }
            if(currentDate == null){
                currentDate = new Date();
            }
        }

        @Override
        protected Map<String, Map> doInBackground(Integer... params) {
            // params comes from the execute() call: use params[0] for the first.
            return databaseManager.getUCSBCommonsDataFromHTML(currentDate);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(Map<String, Map> result) {
            AsyncTaskProgressCheck(result);
            currentDate = databaseManager.addDays(currentDate,1);
        }
    }

    public class StickyHeaderListViewAdapter extends BaseAdapter implements StickyListHeadersAdapter {

        private LayoutInflater inflater;
        private Map<String, List> foodList;


        public StickyHeaderListViewAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        public Map<String, List> getFoodList() {
            return foodList;
        }

        public void setFoodList(Map<String, List> foodList) {
            this.foodList = foodList;
        }

        @Override
        public int getCount(){
            if(foodList == null) return 0;
            int sum = 0;
            for (List value : foodList.values()) {
                // sum all arraylist
                sum = sum + value.size();
            }
            return sum;
        }

        @Override
        public Object getItem(int position) {
            if(foodList == null) return null;
            int sum = 0;
            for (List value : foodList.values()) {
                int offset = sum;
                // sum all arraylist
                sum = sum + value.size();
                // the item is in this list
                if(sum > position) return value.get(position-offset);
            }
            // out of the range
            return null;
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
                holder.text = (TextView) convertView.findViewById(R.id.listview_dining_item);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            String itemText = (String)getItem(position);
            holder.text.setText(itemText);

            return convertView;
        }

        @Override
        public View getHeaderView(int position, View convertView, ViewGroup parent) {
            HeaderViewHolder holder;
            if (convertView == null) {
                holder = new HeaderViewHolder();
                convertView = inflater.inflate(R.layout.listview_dining_header, parent, false);
                holder.text = (TextView) convertView.findViewById(R.id.listview_dining_header);
                convertView.setTag(holder);
            } else {
                holder = (HeaderViewHolder) convertView.getTag();
            }
            //set header text as first char in name
            String headerText = (String)getHeaderItem(position);
            holder.text.setText(headerText);
            return convertView;
        }

        @Override
        public long getHeaderId(int position) {
            //return by the group number(count) * 107
            if(foodList == null) return 0;
            int sum = 0;
            int count = 1;
            for (List value : foodList.values()) {
                // sum all arraylist
                sum = sum + value.size();
                // the item is in this list
                if(sum > position) return count*107;
                count++;
            }
            return 0;
        }

        public Object getHeaderItem(int position){
            if(foodList == null) return null;
            int sum = 0;
            for (Map.Entry<String, List> entry : foodList.entrySet()) {
                String key = entry.getKey();
                List value = entry.getValue();
                // sum all arraylist
                sum = sum + value.size();
                if(sum > position) return key;
            }
            // out of the range
            return null;
        }

        class HeaderViewHolder {
            TextView text;
        }

        class ViewHolder {
            TextView text;
        }

    }



}
