package com.intbridge.projects.gaucholife;

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

    // the range starts from today and include today
    public List<Map> getUCSBCommonsDataFromHTMLWithRange(int range){

        return null;
    }

    public Date addDays(Date date, int days)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }

    public Map<String, List> filteringDictionay(int date,String common,String meal){

        return null;
    }

}
