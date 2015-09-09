package com.intbridge.projects.gaucholife;

import android.app.PendingIntent;
import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
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
                    List<String> foods = common.getList(key);
                    if(commonDictionary==null) commonDictionary = new LinkedHashMap<>();
                    mealDict = commonDictionary.get(meal);
                    if(mealDict == null) mealDict = new LinkedHashMap<>();
                    mealDict.put(mealAndFood[1], foods);
                    commonDictionary.put(meal,mealDict);
                }
                dateDictionary.put(commonName,commonDictionary);
            }else{
                // not the right date
            }
        }
    }

    public List<String> getFavoriteList(){
        List<String> favoriteList = null;
        ParseQuery query = ParseQuery.getQuery("DiningFavorite");
        query.fromLocalDatastore();
        ParseObject listObject;
        try {
            listObject = query.getFirst();
            if(listObject == null) listObject.put("myFavorite",new ArrayList<String>());
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
            listObject.put("myFavorite",new ArrayList<String>());
            favoriteList = listObject.getList("myFavorite");
            favoriteList.add(food);
        }
    }

    public void removeFoodToLocalFavoriteList(String food){
        List<String> favoriteList = null;
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
            listObject.put("myFavorite",new ArrayList<String>());
            //favoriteList = listObject.getList("myFavorite");
            try {
                listObject.pin();
            } catch (ParseException pin) {
                pin.printStackTrace();
            }
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
            Log.e("clearAllDining","ParseException");
        }
    }

    public boolean updateLocalNotificationTimestamp(int dateInt){
        ParseQuery query = ParseQuery.getQuery("DiningNotification");
        query.fromLocalDatastore();
        ParseObject listObject;
        try {
            Log.e("update: ","start");
            listObject = query.getFirst();
            if(listObject == null) {
                // new item haven't schedule notification yet, return true
                Log.e("update: ","return null");
                listObject = new ParseObject("DiningNotification");
                listObject.put("notificationTimestamp", dateInt);
                return true;
            }
            Log.e("update: ","c");
            if ((int)listObject.get("notificationTimestamp") <= dateInt) return false;
            else{
                listObject.put("notificationTimestamp", dateInt);
                return true;
            }
        } catch (ParseException e) {
            // DiningNotification is null
            // new item haven't schedule notification yet, return true
            Log.e("update: ","ParseException");
            listObject = new ParseObject("DiningNotification");
            listObject.put("notificationTimestamp", dateInt);
            return true;
        }
    }

    public void resetLocalNotificationTimestamp(int dateInt){
        ParseQuery query = ParseQuery.getQuery("DiningNotification");
        query.fromLocalDatastore();
        ParseObject listObject;
        try {
            Log.e("reset: ","start");
            listObject = query.getFirst();
            if(listObject == null) {
                // new item haven't schedule notification yet
                Log.e("update: ","return null");
                listObject = new ParseObject("DiningNotification");
            }
            Log.e("reset: ","c");
            listObject.put("notificationTimestamp", dateInt);
        } catch (ParseException e) {
            // DiningNotification is null
            // new item haven't schedule notification yet, return true
            Log.e("reset: ","ParseException");
            listObject = new ParseObject("DiningNotification");
            listObject.put("notificationTimestamp", dateInt);
        }
    }

    public void storePendingIntentArray(List<PendingIntent> pendingIntents){
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
                listObject.put("notificationPendingIntent", new ArrayList<PendingIntent>());
            }
            Log.e("storePending: ", "c");
            List<PendingIntent> storedArray = (ArrayList<PendingIntent>)listObject.get("notificationPendingIntent");
            listObject.put("notificationPendingIntent", storedArray.addAll(pendingIntents));
        } catch (ParseException e) {
            // Setting is null
            // new item haven't schedule notification yet
            Log.e("storePending: ","ParseException");
            listObject = new ParseObject("Setting");
            listObject.put("notificationPendingIntent", pendingIntents);
        }
    }

    public List<PendingIntent> getPendingIntentArray(){
        ParseQuery query = ParseQuery.getQuery("Setting");
        query.fromLocalDatastore();
        ParseObject listObject;
        try {
            Log.e("getPending: ","start");
            listObject = query.getFirst();
            if(listObject == null) {
                // new item haven't schedule notification yet
                return  null;
            }
            Log.e("getPending: ", "c");
            return (List<PendingIntent>)listObject.get("notificationPendingIntent");
        } catch (ParseException e) {
            // Setting is null
            // new item haven't schedule notification yet
            Log.e("getPending: ", "ParseException");
            return  null;
        }
    }

    public int convertDateToInteger(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        // Note: zero based!
        String dateString = String.format("%d%02d%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
        return Integer.parseInt(dateString);
    }
}
