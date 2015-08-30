package com.intbridge.projects.gaucholife;

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

/**
 * Created by Derek on 8/10/2015.
 */
public class PGDatabaseManager {
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

    public List<String> getFavoriteList(){
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
}
