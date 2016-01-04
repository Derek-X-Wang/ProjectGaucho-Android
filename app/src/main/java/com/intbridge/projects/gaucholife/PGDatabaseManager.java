package com.intbridge.projects.gaucholife;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;

import com.intbridge.projects.gaucholife.utils.Devices;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 *
 * Created by Derek on 8/10/2015.
 */
public class PGDatabaseManager {

    public boolean checkDataSourse(){
        ParseObject controlPanel = (ParseObject)getControlPanelFromParse();
        // if no panel, run default -> use html
        if(controlPanel == null) return true;

        return controlPanel.getBoolean("DataSource");
    }

    private Object getControlPanelFromParse(){
        ParseQuery query = ParseQuery.getQuery("ControlPanel");
        query.whereEqualTo("OS",false);
        try {
            return query.getFirst();
        } catch (ParseException e) {
            Log.e("ControlPanel: ", "ParseException");
            e.printStackTrace();
            return null;
        }
    }
    // get a dict of commons in a day
    public Map<String, Map> getUCSBCommonsDataFromHTML(String year,String month,String day){
        //Log.e("currentdatabaseM: ",year+month+day);
        Document doc = null;
        // init the return value
        Map<String, Map> dictionary = new LinkedHashMap<>();
        try {
            String url = "https://appl.housing.ucsb.edu/" + "menu?day=" + year + "-" + month + "-" + day + "&meal=";
            doc = Jsoup.connect(url).get();

            // get each common section
            Elements commons = doc.getElementsByClass("col-sm-6");
            //Log.e("Start", " Loging**");
            for (Element common : commons) {
                Element commonName = common.getElementsByTag("h4").first();
                // get each meal section, ex lunch, dinner. ps. it'll only return the meals existed
                Elements commonMeals = common.getElementsByClass("panel-success");
                //Log.e("*Common: ", commonName.text()+"**");
                if(commonMeals.isEmpty()){
                    // This common is closed this day
                }else{
                    Map<String, Map> commonDictionary = new LinkedHashMap<>();
                    for (Element meal : commonMeals) {
                        Element mealName = meal.getElementsByTag("h5").first();
                        // get each food section
                        Elements mealFoods = meal.getElementsByTag("dl");
                        //Log.e("**Meal: ", mealName.text()+"**");

                        Map<String, List> foodDictionary = new LinkedHashMap<>();

                        for(Element food : mealFoods){
                            Element foodName = food.getElementsByTag("dt").first();
                            Elements foodList = food.getElementsByTag("dd");
                            //Log.e("***Food: ", foodName.text()+"**");
                            List<String> list = new ArrayList<>();
                            for(Element name : foodList){
                                //Log.e("****Item: ", name.text()+"**");
                                list.add(name.text());
                            }

                            foodDictionary.put(foodName.text(),list);
                        }
                        commonDictionary.put(mealName.text(),foodDictionary);
                    }
                    dictionary.put(commonName.text(),commonDictionary);
                }


            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return dictionary;
    }

    // get a dict of commons in a day, wrap for Date object
    public Map<String, Map> getUCSBCommonsDataFromHTML(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        String year = String.format("%d", cal.get(Calendar.YEAR));
        String month = String.format("%02d", cal.get(Calendar.MONTH) + 1); // Note: zero based!
        String day = String.format("%02d", cal.get(Calendar.DAY_OF_MONTH));

        return getUCSBCommonsDataFromHTML(year,month,day);
    }


    // the range starts from today and include today
    public List<Map> getUCSBCommonsDataFromHTMLWithRange(int range){

        return null;
    }

    public Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }

    public Map<String, List> filteringDictionay(int date,String common,String meal){

        return null;
    }

    public List getCommonDataFromParse(String common, int startDateInt, int endDateInt){
        if(common.equals("De La Guerra")) common = "DeLaGuerra";
        ParseQuery query = ParseQuery.getQuery(common);
        query.whereGreaterThanOrEqualTo("date",startDateInt);
        query.whereLessThanOrEqualTo("date",endDateInt);
        List listObjects = null;
        try {
            listObjects = query.find();
            return listObjects;
        } catch (ParseException e) {
            Log.e("getCommon: ","ParseException");
        }
        return  listObjects;
    }

    public Map getUCSBDiningCommonsDictionaryFromParse(Date startDate,int range){
        int startDateInt = convertDateToInteger(startDate);
        int endDateInt = convertDateToInteger(addDays(startDate, range));
        List<ParseObject> Carrillo = getCommonDataFromParse("Carrillo", startDateInt, endDateInt);
        List<ParseObject> DeLaGuerra = getCommonDataFromParse("DeLaGuerra", startDateInt, endDateInt);
        List<ParseObject> Ortega = getCommonDataFromParse("Ortega", startDateInt, endDateInt);
        List<ParseObject> Portola = getCommonDataFromParse("Portola", startDateInt, endDateInt);

        Date dateItr = startDate;
        Map<Integer, Map> dictionary = new LinkedHashMap<>();
        for(int i = 0;i<range;i++){
            Map<String, Map> dateDictionary = new LinkedHashMap<>();

            constructDictFromParseObject("Carrillo",Carrillo, dateItr, dateDictionary);
            constructDictFromParseObject("De La Guerra",DeLaGuerra, dateItr, dateDictionary);
            constructDictFromParseObject("Ortega",Ortega, dateItr, dateDictionary);
            constructDictFromParseObject("Portola",Portola, dateItr, dateDictionary);

            dictionary.put(convertDateToInteger(dateItr), dateDictionary);
            dateItr = addDays(dateItr,1);
        }


        return dictionary;
    }

    private void constructDictFromParseObject(String commonName, List<ParseObject> parseObjectList, Date dateItr, Map<String, Map> dateDictionary) {
        for(ParseObject common : parseObjectList){
            Map<String, Map> commonDictionary = null;
            int date = common.getInt("date");
            if(date == convertDateToInteger(dateItr)){
                Set<String> keySet = common.keySet();
                //keySet.remove("date");
                Map<String, List> mealDict = null;
                for(String key:keySet){
                    if(key.equals("date")) continue;
                    String[] mealAndFood = key.split("_");
                    String meal = mealAndFood[0];
                    if(meal.equals("LateNight")) meal = "Late Night";
                    if(meal.equals("BrightMeal")) meal = "Bright Meal";
                    List<String> foods = common.getList(key);
                    if(commonDictionary==null) commonDictionary = new LinkedHashMap<>();
                    mealDict = commonDictionary.get(meal);
                    if(mealDict == null) mealDict = new LinkedHashMap<>();
                    mealDict.put(convertCombinedStringToSeparated(mealAndFood[1]), foods);
                    commonDictionary.put(meal,mealDict);
                }
                dateDictionary.put(commonName,commonDictionary);
            }else{
                // not the right date
            }
        }
    }

    public static List<String> getFavoriteList(){
        List<String> favoriteList = null;
        ParseQuery query = ParseQuery.getQuery("DiningFavorite");
        query.fromLocalDatastore();
        ParseObject listObject;
        try {
            listObject = query.getFirst();
            favoriteList = listObject.getList("myFavorite");
        } catch (ParseException e) {
            listObject = new ParseObject("DiningFavorite");
            listObject.put("myFavorite",new ArrayList<String>());
            favoriteList = listObject.getList("myFavorite");
            try {
                listObject.pin();
            } catch (ParseException pin) {
                pin.printStackTrace();
            }
        }
        return  favoriteList;
    }
//    public boolean isFoodInLocalFavoriteList(String food){
//        ParseQuery query = ParseQuery.getQuery("DiningFavorite");
//        query.fromLocalDatastore();
//        try {
//            ParseObject listObject = query.getFirst();
//            if(listObject == null){
//                listObject = new ParseObject("DiningFavorite");
//                listObject.put("myFavorite",new ArrayList<String>());
//                favoriteList = listObject.getList("myFavorite");
//                listObject.pin();
//                return false;
//            }else{
//                favoriteList = listObject.getList("myFavorite");
//                return favoriteList.contains(food);
//            }
//
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }

    public void addFoodToLocalFavoriteList(String food){
        List<String> favoriteList = null;
        ParseQuery query = ParseQuery.getQuery("DiningFavorite");
        query.fromLocalDatastore();
        ParseObject listObject;
        try {
            listObject = query.getFirst();
            favoriteList = listObject.getList("myFavorite");
            favoriteList.add(food);
            //listObject.put("myFavorite", favoriteList);
        } catch (ParseException e) {
            // favoriteList is null
            listObject = new ParseObject("DiningFavorite");
            favoriteList = new ArrayList<String>();
            favoriteList.add(food);
            listObject.put("myFavorite",favoriteList);
        }

        try {
            listObject.pin();
        } catch (ParseException pin) {
            pin.printStackTrace();
        }
    }

    public void removeFoodToLocalFavoriteList(String food){
        List<String> favoriteList = new ArrayList<>();
        ParseQuery query = ParseQuery.getQuery("DiningFavorite");
        query.fromLocalDatastore();
        ParseObject listObject;
        try {
            listObject = query.getFirst();
            favoriteList = listObject.getList("myFavorite");
            favoriteList.remove(food);
        } catch (ParseException e) {
            // favoriteList is null, which might be unnecessary for removing
            listObject = new ParseObject("DiningFavorite");
        }
        listObject.put("myFavorite", favoriteList);
        try {
            listObject.pin();
        } catch (ParseException pin) {
            pin.printStackTrace();
        }
    }

    public void storeDictToParseLocalDatastore(int dateInt, Map<String, Map> dict){
        ParseObject listObject = new ParseObject("DiningDictionary");
        listObject.put("dateInt", dateInt);
        listObject.put("dictionary", dict);
        try {
            listObject.pin();
            //Log.e("store: ", dateInt + "stored");
        } catch (ParseException e) {
            //Log.e("store: ", dateInt + "store fail "+e.getMessage());
        }
    }

    public boolean isDictExistInParseLocalDatastore(int dateInt){
        ParseQuery query = ParseQuery.getQuery("DiningDictionary");
        query.fromLocalDatastore();
        query.whereEqualTo("dateInt", dateInt);
        List parseList = null;
        try {
            parseList = query.find();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //if(parseList == null) return false;
        if(parseList.size() == 0) return false;
        return true;
    }

    public List<ParseObject> getDictionariesGreaterThanOrEqualToFromParseLocalDatastore(int dateInt){
        ParseQuery query = ParseQuery.getQuery("DiningDictionary");
        query.fromLocalDatastore();
        query.whereGreaterThanOrEqualTo("dateInt", dateInt);
        query.orderByAscending("dateInt");
        List<ParseObject> parseList = null;
        try {
            parseList = query.find();
            return  parseList;
        } catch (ParseException e) {
            return null;
        }
    }

    public List<ParseObject> getDictionariesLessThanFromParseLocalDatastore(int dateInt){
        ParseQuery query = ParseQuery.getQuery("DiningDictionary");
        query.fromLocalDatastore();
        query.whereLessThan("dateInt", dateInt);
        query.orderByDescending("dateInt");
        List<ParseObject> parseList = null;
        try {
            parseList = query.find();
            return  parseList;
        } catch (ParseException e) {
            return null;
        }
    }

    public void clearAllDiningDataFromParseLocalDatastore(){
        ParseQuery query = ParseQuery.getQuery("DiningDictionary");
        query.fromLocalDatastore();
        List<ParseObject> parseList = null;
        try {
            parseList = query.find();
            if(parseList != null){
                for(ParseObject dict : parseList){
                    dict.unpinInBackground();
                }
            }
        } catch (ParseException e) {
            //Log.e("clearAllDining", "ParseException");
        }
    }

    public boolean updateLocalNotificationTimestamp(int dateInt){
        ParseQuery query = ParseQuery.getQuery("Setting");
        query.fromLocalDatastore();
        ParseObject listObject;
        try {
            Log.e("update: ","start");
            listObject = query.getFirst();
            if(listObject == null) {
                // new item haven't schedule notification yet, return true
                Log.e("update: ","return null");
                listObject = new ParseObject("Setting");
                listObject.put("notificationTimestamp", dateInt);
                listObject.pinInBackground();
                return true;
            }
            Log.e("update: ", "c");
            if ((int)listObject.get("notificationTimestamp") <= dateInt) return false;
            else{
                listObject.put("notificationTimestamp", dateInt);
                listObject.pinInBackground();
                return true;
            }
        } catch (ParseException e) {
            // DiningNotification is null
            // new item haven't schedule notification yet, return true
            Log.e("update: ","ParseException");
            listObject = new ParseObject("Setting");
            listObject.put("notificationTimestamp", dateInt);
            listObject.pinInBackground();
            return true;
        }
    }

    public void resetLocalNotificationTimestamp(int dateInt){
        ParseQuery query = ParseQuery.getQuery("Setting");
        query.fromLocalDatastore();
        ParseObject listObject;
        try {
            Log.e("reset: ","start");
            listObject = query.getFirst();
            if(listObject == null) {
                // new item haven't schedule notification yet
                Log.e("update: ", "return null");
                listObject = new ParseObject("Setting");
            }
            Log.e("reset: ","c");
            listObject.put("notificationTimestamp", dateInt);
        } catch (ParseException e) {
            // DiningNotification is null
            // new item haven't schedule notification yet, return true
            Log.e("reset: ","ParseException");
            listObject = new ParseObject("Setting");
            listObject.put("notificationTimestamp", dateInt);
        }
        listObject.pinInBackground();
    }

    public void storePendingIntentArray(List<Integer> pendingIntents){
        ParseQuery query = ParseQuery.getQuery("Setting");
        query.fromLocalDatastore();
        ParseObject listObject;
        try {
            Log.e("storePending: ","start");
            listObject = query.getFirst();
            if(listObject == null) {
                // new item haven't schedule notification yet
                Log.e("storePending: ","return null");
                listObject = new ParseObject("Setting");
            }
            Log.e("storePending: ", "c");
            listObject.put("notificationPendingIntent", pendingIntents);

        } catch (ParseException e) {
            // Setting is null
            // new item haven't schedule notification yet
            Log.e("storePending: ","ParseException");
            listObject = new ParseObject("Setting");
            listObject.put("notificationPendingIntent", pendingIntents);
        }
        try {
            listObject.pin();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void addPendingIntentIDToLocalDatastore(int id){
        List<Integer> idList = null;
        ParseQuery query = ParseQuery.getQuery("Setting");
        query.fromLocalDatastore();
        ParseObject listObject;
        try {
            Log.e("addPending: ", "start");
            listObject = query.getFirst();
            idList = listObject.getList("notificationPendingIntent");
        } catch (ParseException e) {
            // Setting is null
            // new item haven't schedule notification yet
            Log.e("addPending: ","ParseException");
            listObject = new ParseObject("Setting");
        }
        if(idList == null) idList = new ArrayList<>();
        idList.add(id);
        listObject.put("notificationPendingIntent", idList);
        try {
            listObject.pin();
        } catch (ParseException pin) {
            pin.printStackTrace();
        }
    }

    public List<Integer> getPendingIntentArray(){
        ParseQuery query = ParseQuery.getQuery("Setting");
        query.fromLocalDatastore();
        ParseObject listObject;
        try {
            //Log.e("getPending: ","start");
            listObject = query.getFirst();
            if(listObject == null) {
                // new item haven't schedule notification yet
                return  null;
            }
            //Log.e("getPending: ", "c");
            return listObject.getList("notificationPendingIntent");
        } catch (ParseException e) {
            // Setting is null
            // new item haven't schedule notification yet
            //Log.e("getPending: ", "ParseException");
            return  null;
        }
    }

    public List<String> getNotifiedCommons(){
        ParseQuery query = ParseQuery.getQuery("Setting");
        query.fromLocalDatastore();
        ParseObject listObject;
        try {
            //Log.e("getNotified: ","start");
            listObject = query.getFirst();
            if(listObject == null || listObject.getList("notifiedCommons") == null) {
                // new item haven't schedule notification yet
                return  Arrays.asList("Carrillo", "De La Guerra", "Ortega", "Portola");
            }
            //Log.e("getNotified: ", "c");
            return listObject.getList("notifiedCommons");
        } catch (ParseException e) {
            // Setting is null
            // new item haven't schedule notification yet
            //Log.e("getNotified: ", "ParseException");
            setNotifiedCommons(true, true, true, true);
            return  Arrays.asList("Carrillo", "De La Guerra", "Ortega", "Portola");
        }
    }

    public void setNotifiedCommons(boolean carrillo,boolean delaguerra,boolean ortega,boolean portola){
        List<String> notifiedCommons = new ArrayList<>();
        if(carrillo){
            notifiedCommons.add("Carrillo");
        }
        if(delaguerra){
            notifiedCommons.add("De La Guerra");
        }
        if(ortega){
            notifiedCommons.add("Ortega");
        }
        if(portola){
            notifiedCommons.add("Portola");
        }
        ParseQuery query = ParseQuery.getQuery("Setting");
        query.fromLocalDatastore();
        ParseObject listObject;
        try {
            //Log.e("setNotified: ","start");
            listObject = query.getFirst();
            if(listObject == null) {
                // new item haven't schedule notification yet
                //Log.e("setNotified: ","return null");
                listObject = new ParseObject("Setting");
            }
            //Log.e("setNotified: ", "c");
            listObject.put("notifiedCommons", notifiedCommons);
        } catch (ParseException e) {
            // Setting is null
            // new item haven't schedule notification yet
            //Log.e("setNotified: ","ParseException");
            listObject = new ParseObject("Setting");
            listObject.put("notifiedCommons", notifiedCommons);
        }
        try {
            listObject.pin();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public Calendar getScheduledNotificationTime(Date date, String common, String meal){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        switch (meal) {
            case "Breakfast":
                calendar.set(Calendar.HOUR_OF_DAY,6);
                calendar.set(Calendar.MINUTE,15);
                calendar.set(Calendar.SECOND,0);
                break;
            case "Brunch":
                calendar.set(Calendar.HOUR_OF_DAY,9);
                calendar.set(Calendar.MINUTE,30);
                calendar.set(Calendar.SECOND,0);
                break;
            case "Lunch":
                calendar.set(Calendar.HOUR_OF_DAY,10);
                calendar.set(Calendar.MINUTE,0);
                calendar.set(Calendar.SECOND,0);
                break;
            case "Dinner":
                calendar.set(Calendar.HOUR_OF_DAY,16);
                calendar.set(Calendar.MINUTE,0);
                calendar.set(Calendar.SECOND,0);
                break;
            case "Late Night":
                calendar.set(Calendar.HOUR_OF_DAY,20);
                calendar.set(Calendar.MINUTE,0);
                calendar.set(Calendar.SECOND,0);
                break;
            case "Bright Meal":
                calendar.set(Calendar.HOUR_OF_DAY,6);
                calendar.set(Calendar.MINUTE,40);
                calendar.set(Calendar.SECOND,0);
                break;
        }
        return  calendar;
    }


    public int convertDateToInteger(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        // Note: zero based!
        String dateString = String.format("%d%02d%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
        return Integer.parseInt(dateString);
    }

    private String convertCombinedStringToSeparated(String str){
        String res = ""+str.charAt(0);
        for(int i = 1; i < str.length(); i++) {
            Character ch = str.charAt(i);
            if(Character.isUpperCase(ch))
                res += " " + ch;
            else
                res += ch;
        }
        return  res;
    }

    private String convertSeparatedStringToCombined(String str){
        String res = "";
        for(int i = 0; i < str.length(); i++) {
            Character ch = str.charAt(i);
            if(Character.isWhitespace(ch))
                res += "";
            else
                res += ch;
        }
        return  res;
    }

    public void getParseObjectFromHTML(int dateInt,Map<String, Map> dict){
        for(Map.Entry<String,Map> common : dict.entrySet()){
            String commonName = common.getKey();
            if(commonName.equals("De La Guerra")) commonName = "DeLaGuerra";
            Map<String, Map> mealDict = common.getValue();
            if(mealDict != null) saveCommonToParse(dateInt,commonName,mealDict);
        }
    }

    private void saveCommonToParse(int dateInt, String commonName, Map<String, Map> mealDict){
        ParseObject listObject = new ParseObject(commonName);
        listObject.put("date",dateInt);
        for(Map.Entry<String,Map> meal : mealDict.entrySet()){
            String mealName = meal.getKey();
            mealName = convertSeparatedStringToCombined(mealName);
            Map<String, List> foodDict = meal.getValue();
            for(Map.Entry<String,List> food : foodDict.entrySet()){
                String foodName = food.getKey();
                foodName = cleanStringForParseColumn(foodName);
                List<String> itemList = food.getValue();
                String column = mealName+"_"+foodName;
                listObject.put(column,itemList);
            }
        }
        try {
            listObject.save();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private String cleanStringForParseColumn(String str){
        String res = "";
        for(int i = 0; i < str.length(); i++) {
            Character ch = str.charAt(i);
            if(ch =='(') break;
            else if(ch ==' ') {}
            else if(ch == '/'){}
            else{
                res += ch;
            }
        }
        return  res;
    }

    public void scheduleAllNotification(Activity host, Map<Integer,Map> tempDataStorage){
        List<String> notifiedCommon = getNotifiedCommons();
        List<String> favoriteList = getFavoriteList();
        for(Map.Entry<Integer,Map> entry : tempDataStorage.entrySet()){
            int dateInt = entry.getKey();
            Date date = convertIntegerToDate(dateInt);
            Map<String,Map> commonDict = entry.getValue();
            for(Map.Entry<String,Map> common : commonDict.entrySet()){
                String commonName = common.getKey();
                if(notifiedCommon.contains(commonName)){
                    Map<String,Map> mealDict = common.getValue();
                    for(Map.Entry<String,Map> meal : mealDict.entrySet()){
                        String mealName = meal.getKey();
                        Map<String,List> foodDict = meal.getValue();
                        // ignore the notification if it is pass already on that day
                        Date currentTime = new Date();
                        Calendar targetCalender = getScheduledNotificationTime(currentTime, commonName, mealName);
                        //targetCalender.add(Calendar.HOUR_OF_DAY,2);
                        Date targetTime = targetCalender.getTime();
                        if(currentTime.getTime() < targetTime.getTime()){
                            breakLoop:
                            for(Map.Entry<String,List> food : foodDict.entrySet()){
                                String foodName = food.getKey();
                                List<String> itemList = food.getValue();
                                for(String item : itemList){
                                    if(favoriteList.contains(item)){
                                        createScheduledNotification(host,date,commonName,mealName);
                                        break breakLoop;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void createScheduledNotification(Activity host, Date date, String common, String meal)
    {
        // Get new calendar object and set the date to now
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        // Add defined amount of days to the date
        //calendar.add(Calendar.HOUR_OF_DAY, days * 24);
        calendar.add(Calendar.SECOND, 10);

        // Retrieve alarm manager from the system
        AlarmManager alarmManager = (AlarmManager) host.getSystemService(Context.ALARM_SERVICE);
        // Every scheduled intent needs a different ID, else it is just executed once
        int Min = 9;
        int Max = 99999;
        int id = Min + (int)(Math.random() * ((Max - Min) + 1));

        // Prepare the intent which should be launched at the date
        Intent notificationIntent = new Intent("android.media.action.DISPLAY_NOTIFICATION");
        //notificationIntent.addCategory("android.intent.category.DEFAULT");
        notificationIntent.putExtra("common", common);
        notificationIntent.putExtra("meal", meal);
        notificationIntent.putExtra("id",id);

        // Prepare the pending intent
        PendingIntent broadcast = PendingIntent.getBroadcast(host, id, notificationIntent, 0);

        // store PendingIntent for canceling reference
        addPendingIntentIDToLocalDatastore(id);

        // Register the alert in the system. You have the option to define if the device has to wake up on the alert or not
        alarmManager.set(AlarmManager.RTC_WAKEUP, getScheduledNotificationTime(date, common, meal).getTimeInMillis(), broadcast);
    }

    public void cancelAllScheduledNotification(Activity host){
        List<Integer> pendingIntents = getPendingIntentArray();
        if(pendingIntents == null) return;
        List<Integer> newPendingIntents = new ArrayList<>();
        for(int p : pendingIntents) {
            newPendingIntents.add(p);
        }
        // Retrieve alarm manager from the system
        AlarmManager alarmManager = (AlarmManager) host.getSystemService(Context.ALARM_SERVICE);
        for(Integer pendingIntent : newPendingIntents){
            Intent notificationIntent = new Intent("android.media.action.DISPLAY_NOTIFICATION");
            PendingIntent broadcast = PendingIntent.getBroadcast(host, pendingIntent, notificationIntent, 0);
            alarmManager.cancel(broadcast);
        }
        storePendingIntentArray(new ArrayList<Integer>());
    }

    private Date convertIntegerToDate(int dateInt){
        String dateString = Integer.toString(dateInt);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        Date date;
        try {
            date = formatter.parse(dateString);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
            date = null;
        }
        return date;
    }



    public static String getUUID(){
        ParseQuery query = ParseQuery.getQuery("Setting");
        query.fromLocalDatastore();
        ParseObject listObject;
        UUID uuid = UUID.randomUUID();
        String uuidString;
        try {
            //Log.e("getUUID: ","start");
            listObject = query.getFirst();
            uuidString = listObject.getString("UUID");
            if(uuidString == null){
                uuidString = uuid.toString();
                listObject.put("UUID", uuid.toString());
                listObject.pinInBackground();
            }

        } catch (ParseException e) {
            // Setting is null
            // new item haven't schedule notification yet
            //Log.e("getUUID: ","ParseException");
            listObject = new ParseObject("Setting");
            uuidString = uuid.toString();
            listObject.put("UUID", uuidString);
            listObject.pinInBackground();
        }
        return uuidString;
    }



    private String lastMessage = "";
    public void sendUserReport(String message){
        if (message.equalsIgnoreCase(lastMessage)){

        } else {
            lastMessage = message;
            ParseObject messageObject = new ParseObject("UserReport");
            messageObject.put("Comment", message);
            String uuid = getUUID();
            messageObject.put("UUID", uuid);
            messageObject.saveEventually();
        }
    }

    public static String getUserID(){
        ParseQuery query = ParseQuery.getQuery("Setting");
        query.fromLocalDatastore();
        ParseObject listObject;
        UUID uuid = UUID.randomUUID();
        String uuidString;
        try {
            //Log.e("getUUID: ","start");
            listObject = query.getFirst();
            uuidString = listObject.getString("UUID");
            if(uuidString == null){
                uuidString = uuid.toString();
                listObject.put("UUID", uuid.toString());
                listObject.pinInBackground();
            }

        } catch (ParseException e) {
            // Setting is null
            // new item haven't schedule notification yet
            //Log.e("getUUID: ","ParseException");
            listObject = new ParseObject("Setting");
            uuidString = uuid.toString();
            listObject.put("UUID", uuidString);
            listObject.pinInBackground();
        }
        return uuidString;
    }

    public static boolean isRestoreCouponAmount(){
        Date recordDate = null;
        Date currentDate = new Date();
        ParseQuery query = ParseQuery.getQuery("Setting");
        query.fromLocalDatastore();
        ParseObject object;
        try {
            object = query.getFirst();
            recordDate = object.getDate("CouponDate");
            if(recordDate == null){
                object.put("CouponDate", currentDate);
                object.pinInBackground();
                return true;
            }

        } catch (ParseException e) {
            // Setting is null
            object = new ParseObject("Setting");
            object.put("CouponDate", currentDate);
            object.pinInBackground();
            return true;
        }
        return !isSameDay(recordDate, currentDate);

    }

    public static boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
        return sameDay;
    }

}
