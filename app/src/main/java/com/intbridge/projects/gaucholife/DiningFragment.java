package com.intbridge.projects.gaucholife;


import android.app.ActionBar;
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

import com.parse.ParseObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.pedant.SweetAlert.SweetAlertDialog;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;


/**
 *
 */
public class DiningFragment extends Fragment{

    MainActivity host;

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

    private List<String> favoriteList;
    private ImageView heart;
    private TextView hint;

    private Date currentDate;

    private int loadLoopIndicator = 0;

    private final int LOADDAYRANGE = 9;
    private int loadDayLimit = LOADDAYRANGE;


    private boolean dataSource;


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
        // it is a bit messy now since loading html will not reduce loadDayLimit but loading parse will
        host = (MainActivity)getActivity();
        databaseManager = new PGDatabaseManager();
        tempDataStorage = new LinkedHashMap<>();
        currentDate = new Date();
        //currentDate = databaseManager.addDays(currentDate,3);
        favoriteList = databaseManager.getFavoriteList();
        loadDayLimit = LOADDAYRANGE;
        //Log.e("onCreate: ", "11111111");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dining, container, false);
        
        hint = (TextView)v.findViewById(R.id.fragemt_dining_hint);

        // prevent action bar to show when the program is reopen from background and the view is re-init
        ActionBar actionBar = host.getActionBar();
        if(actionBar != null && actionBar.isShowing()) actionBar.hide();

        initMultiSelectionIndicators(v);

        initStickyListView(v);

        Bundle args = getArguments();
        dataSource = args.getBoolean("DATASOURCE");
        boolean cleanLocal = args.getBoolean("CLEANLOCAL");
        loadDayLimit = LOADDAYRANGE;
        if(cleanLocal){
            databaseManager.clearAllDiningDataFromParseLocalDatastore();
            if(dataSource){
                // load from html
                new WebRequestTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
            }else{
                // load from Parse
                new ParseRequestTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
            }
        }else{
            new LoadLocalTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
        }

        return v;
    }


    public Map<Integer, Map> getTempDataStorage() {
        return tempDataStorage;
    }

    private void initStickyListView(View v) {
        StickyListHeadersListView stickyList = (StickyListHeadersListView) v.findViewById(R.id.list);
        adapter = new StickyHeaderListViewAdapter(host);
        stickyList.setAdapter(adapter);
        stickyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                TextView t = (TextView) view.findViewById(R.id.listview_dining_item);
                heart = (ImageView) view.findViewById(R.id.listview_dining_item_heart);

                final String text = (String) t.getText();
                // check text with favor list
                if (favoriteList.contains(text)) {
                    // existed -> user would like to delete a favor
                    String deleteFavoriteContent = String.format("GauchoLife will no longer notify you when \"%s\" is being served.", text);
                    new SweetAlertDialog(host, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Unmark Favorite")
                            .setContentText(deleteFavoriteContent)
                            .setConfirmText("Ok")
                            .setCancelText("Cancel")
                            .showCancelButton(true)
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    // reuse previous dialog instance
                                    sDialog.setTitleText("Deleted!")
                                            .setContentText("\"" + text + "\" is deleted to your favorite list!")
                                            .setConfirmText("OK")
                                            .setConfirmClickListener(null)
                                            .showCancelButton(false)
                                            .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                    databaseManager.removeFoodToLocalFavoriteList(text);
                                    heart.setImageResource(R.drawable.emptyfavoriteheart);
                                    // can be improve by favoriteList.remove(), but it may ruin the concept
                                    favoriteList = databaseManager.getFavoriteList();
                                    // reset push
                                    new NotificationUpdateTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                                }
                            })
                            .show();

                } else {
                    // not existed -> add new item to the favor list
                    String addFavoriteContent = String.format("Do you want to mark \"%s\" as favorite?\n\nGauchoLife will let you know when it is being served.", text);
                    new SweetAlertDialog(host, SweetAlertDialog.NORMAL_TYPE)
                            .setTitleText("Mark as Favorite")
                            .setContentText(addFavoriteContent)
                            .setConfirmText("Yes!")
                            .setCancelText("Cancel")
                            .showCancelButton(true)
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    // reuse previous dialog instance
                                    sDialog.setTitleText("Added!")
                                            .setContentText("\"" + text + "\" is added to your favorite list!")
                                            .setConfirmText("Ok")
                                            .setConfirmClickListener(null)
                                            .showCancelButton(false)
                                            .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                    databaseManager.addFoodToLocalFavoriteList(text);
                                    heart.setImageResource(R.drawable.favoriteheart);
                                    // can be improve by favoriteList.add(), but it may ruin the programming concept
                                    favoriteList = databaseManager.getFavoriteList();
                                    // reset push
                                    new NotificationUpdateTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                                }
                            })
                            .show();
                }

                // not existed -> add new item to the favor list
            }
        });
    }

    private void initMultiSelectionIndicators(View v) {
        mIndicatorCommon = (MultiSelectionIndicator) v.findViewById(R.id.msi_common);
        mIndicatorDate = (MultiSelectionIndicator) v.findViewById(R.id.msi_date);
        mIndicatorMeal = (MultiSelectionIndicator) v.findViewById(R.id.msi_meal);



        commons = Arrays.asList("Carrillo", "De La Guerra", "Ortega", "Portola");
        dates = new ArrayList<>();
        Date baseDate = new Date();
        //baseDate = databaseManager.addDays(baseDate,3);
        for(int i=0;i<loadDayLimit;i++){
            Date addDate = databaseManager.addDays(baseDate,i);
            String[] dateStrings = convertDateToStringArray(addDate);
            dates.add(dateStrings[2]);
        }


        meals = Arrays.asList("Breakfast", "Brunch", "Lunch", "Dinner", "Late Night","Bright Meal");

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
        mIndicatorDate.setCallbackManager(new MultiSelectionIndicator.CallbackManager() {
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
        String dateString = String.format("%d%02d%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
        return Integer.parseInt(dateString);
    }

    private Date convertIntegerToDate(int dateInt){
        String dateString = Integer.toString(dateInt);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        Date date;
        try {
            date = formatter.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            date = null;
        }
        return date;
    }

    private void changeFragmentData(){
        // get current status
        String commonString = commons.get(currentCommon);
        String dayString = dates.get(currentDay);
        String mealString = meals.get(currentMeal);
        int dateInt = matchDayWithTempDictDate(dayString);
        hint.setText("Loading....");
        if(dateInt == -1){
            // no data available for the given dateInt
            adapter.setFoodList(null);
            adapter.notifyDataSetChanged();
        }else{
            updateStickyListView(commonString, mealString, dateInt);
        }
    }

    private void updateStickyListView(String commonString, String mealString, int dateInt) {
        // unpack the dict, is there a better way?
        hint.setVisibility(View.GONE);
        Map<String, Map> unpackedDict1 = tempDataStorage.get(dateInt);
        Map<String, Map> unpackedDict2 = null;
        Map<String, List> unpackedDict3 = null;
        if(unpackedDict1==null){
            hint.setText("Loading....");
            hint.setVisibility(View.VISIBLE);
        }else{
            unpackedDict2 = unpackedDict1.get(commonString);
            if(unpackedDict2 == null){
                hint.setText("The common is closed");
                hint.setVisibility(View.VISIBLE);
            }else{
                unpackedDict3 = unpackedDict2.get(mealString);
                if(unpackedDict3 == null) {
                    hint.setText("Not serving");
                    hint.setVisibility(View.VISIBLE);
                }
            }
        }

        // set new data to adapter
        adapter.setFoodList(unpackedDict3);
        adapter.notifyDataSetChanged();
    }

    private void updateStickyListView(Map<String, Map> result) {
        // get current status
        String commonString = commons.get(currentCommon);
        String dayString = dates.get(currentDay);
        String mealString = meals.get(currentMeal);

        // store the result in the fragment
        int dateInt = convertDateToInteger(currentDate);
        tempDataStorage.put(dateInt, result);
        // add to local datastore if it isn't been added yet; the if check may not be necessary
        if(!databaseManager.isDictExistInParseLocalDatastore(dateInt)
                && result != null
                && result.size() > 0) {
            databaseManager.storeDictToParseLocalDatastore(dateInt,result);
        }
        // get day 2 digit string
        String[] dateStrings = convertDateToStringArray(currentDate);
        // if user open this fragment current and data is ready -> update the listview
        if(dateStrings[2].equals(dayString)){
            hint.setVisibility(View.GONE);
            updateStickyListView(commonString, mealString, dateInt);
        }
    }

    private int matchDayWithTempDictDate(String day){
        Set<Integer> keys = tempDataStorage.keySet();
        int dayInt = Integer.parseInt(day);
        for(int i:keys){
            if( i%100 == dayInt) return i;
        }
        return -1;
    }

    private class LoadLocalTask extends AsyncTask<Integer, Integer, Void> {

        private List<ParseObject> todayAndAfter;
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
        protected Void doInBackground(Integer... params) {
            // params comes from the execute() call: use params[0] for the first.
            int dateInt = convertDateToInteger(currentDate);
            // get local data
            todayAndAfter = databaseManager.getDictionariesGreaterThanOrEqualToFromParseLocalDatastore(dateInt);
            // get need-to-delete data
            List<ParseObject> beforeToday = databaseManager.getDictionariesLessThanFromParseLocalDatastore(dateInt);

            if(beforeToday != null){
                for(ParseObject dict : beforeToday){
                    dict.unpinInBackground();
                }
            }
            //Log.e("Dinning: ", "doInBackground");
            return null;
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(Void result) {
            //Log.e("Dinning: ", "after onPostExecute");
            if(todayAndAfter != null){
                // load local data if there is any
                for(ParseObject dict : todayAndAfter){
                    updateStickyListView((Map<String,Map>)dict.get("dictionary"));
                    // reduce the amount needed to load from internet
                    // loadDayLimit may become negative if todayAndAfter is big. However, if loadDayLimit is constant then it should be fine
                    loadDayLimit--;
                    currentDate = databaseManager.addDays(currentDate,1);
                }
            }
            // load from internet if needed
            //Log.e("main: ", dateInt + "loadlimit " + loadDayLimit);
            if(loadDayLimit > 0){
                //Log.e("Dinning: ", "loadDayLimit is " + loadDayLimit);
                if(dataSource){
                    // load from html
                    //Log.e("Dinning: ", "before web");
                    new WebRequestTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                }else{
                    // load from Parse
                    //Log.e("Dinning: ", "before parse");
                    new ParseRequestTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                }
            }
        }
    }

    private class ParseRequestTask extends AsyncTask<Integer, Integer, Map<Integer, Map>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
        protected Map<Integer, Map> doInBackground(Integer... params) {
            // params comes from the execute() call: use params[0] for the first.
            Map<Integer, Map> dict = databaseManager.getUCSBDiningCommonsDictionaryFromParse(currentDate, loadDayLimit);
            //Log.e("ParseRequestTask: ", "do1");
            databaseManager.cancelAllScheduledNotification(host);
            //Log.e("ParseRequestTask: ", "do1");
            databaseManager.scheduleAllNotification(host,dict);
            return dict;
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(Map<Integer, Map> parseDict) {
            super.onPostExecute(parseDict);
            //Log.e("ParseRequestTask: ", "onPostExecute");
            //if(!doublePostExecute){
                //Log.e("ParseRequestTask: ", "doublePostExecute");
                int i = 0;
                for (Map.Entry<Integer, Map> entry : parseDict.entrySet()) {
                    Map<String,Map> value = entry.getValue();
                    updateStickyListView(value);
                    i++;
                    currentDate = databaseManager.addDays(currentDate,1);
                    loadDayLimit--;
                }
            //}

        }
    }

    private class WebRequestTask extends AsyncTask<Void, Void, Map<Integer, Map>> {
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
        protected Map<Integer, Map> doInBackground(Void... params) {
            // params comes from the execute() call: use params[0] for the first.
            Map<Integer, Map> result = new LinkedHashMap<>();
            Date loopDate = currentDate;
            for(int i = 0; i < loadDayLimit; i++){
                //Log.e("webtimeBG: ",""+convertDateToInteger(loopDate));
                Map<String, Map> temp = databaseManager.getUCSBCommonsDataFromHTML(loopDate);
                result.put(convertDateToInteger(loopDate),temp);
                loopDate = databaseManager.addDays(loopDate,1);
            }
            databaseManager.cancelAllScheduledNotification(host);
            databaseManager.scheduleAllNotification(host,result);
            return result;
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(Map<Integer, Map> result) {
            //Log.e("WebRequestTask: ", "onPostExecute");
            for(Map.Entry<Integer,Map> entry: result.entrySet()){
                //Log.e("webtimeFG: ",""+convertDateToInteger(currentDate));
                updateStickyListView(entry.getValue());
                currentDate = databaseManager.addDays(currentDate,1);
            }
        }
    }

    private class NotificationUpdateTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            databaseManager.cancelAllScheduledNotification(host);
            databaseManager.scheduleAllNotification(host,tempDataStorage);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
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

            if(favoriteList.contains(itemText)) ((ImageView)convertView.findViewById(R.id.listview_dining_item_heart)).setImageResource(R.drawable.favoriteheart);
            else{
                ((ImageView)convertView.findViewById(R.id.listview_dining_item_heart)).setImageResource(R.drawable.emptyfavoriteheart);
            }

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
