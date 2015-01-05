package com.intbridge.projects.projectgaucho;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import xmlwise.Plist;
import xmlwise.XmlParseException;

/**
 * Created by Derek on 12/26/2014.
 */
public class SearchSuggestions {

    private ArrayList<SearchItem> totalList = new ArrayList<SearchItem>();
    private ArrayList<SearchItem> filteredList = new ArrayList<SearchItem>();
    private Map<String, Object> LocationMap = null;
    private ArrayList<String> totalStringList = new ArrayList<String>();
    private ArrayList<String> filteredStringList = new ArrayList<String>();

    private Map<String, Object> loadPListByXmlwise(Context here,String school){
        Map<String, Object> schoolMap = null;
        try{
            InputStream inputStream = null;
            BufferedReader bufferedReader = null;
            inputStream = here.getResources().openRawResource(R.raw.ucsb);
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            schoolMap = Plist.fromXml(sb.toString());
            //schoolMap = Plist.load("../res/raw/ucsb.plist"); // loads the (nested) properties.
        }catch (XmlParseException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            return schoolMap;
        }
    }

    public SearchSuggestions(Context here,String school){
        //Log.e("SearchSuggestions","Here 1");
            LocationMap = loadPListByXmlwise(here,school);
        if(LocationMap == null){
        }else {
            for(String key : LocationMap.keySet()) {
                totalStringList.add(key);
            }
            Collections.sort(totalStringList);
        }
    }

    private boolean matchKey(String key, String queryText){
        if(key == null){
           // Log.d("matchKey","key is null");
            return false;
        }
        if(queryText.length() > key.length()){
            //Log.d("matchKey","queryT > key");
            return false;
        }
        //Log.d("matchKey","loop start");
        for (int i = 0; i <queryText.length(); i++){
            if(key.charAt(i)!=queryText.charAt(i)){
                return  false;
            }
        }
        //Log.d("matchKey","loop end");
        return true;
    }
    private boolean checkKey(String key, String queryText){
        String key1 = null;
        String key2 = null;
        String key3 = null;
        if (key.contains("|")) {
            //TODO: key may contains more that 3 parts separate by |, this model can not handle that situation. Make the code more expandable. Also, clean and refactor the code
            //Log.d("checkKey","The key have |");
            String[] parts = key.split("\\|");
            key1 = parts[0];
            key2 = parts[1];
            if(parts.length==3){
                key3 = parts[2];
                key3 = key3.substring(1);
                //Log.d("checkKey","The key3 is "+key3);
                if (key3.charAt(key3.length()-1)==" ".charAt(0)) {
                    key3 = key1.substring(0, key3.length() - 1);
                }
            }
            if (key1.charAt(key1.length()-1)==" ".charAt(0)) {
                key1 = key1.substring(0, key1.length() - 1);
            }
            key2 = key2.substring(1);
            if (key2.charAt(key2.length()-1)==" ".charAt(0)) {
                key2 = key2.substring(0, key2.length() - 1);
            }
            //Log.d("checkKey","The key1 is "+key1);
            //Log.d("checkKey","The key2 is "+key2);
            if(matchKey(key1,queryText)||matchKey(key2,queryText)||matchKey(key3,queryText)){
                return true;
            }else {
                return false;
            }
        } else {
            //Log.d("checkKey","The key have no |");
            if(matchKey(key,queryText)){
                return true;
            }else {
                return false;
            }
        }
    }

    @SuppressWarnings("unchecked")
    public ArrayList<SearchItem> generateFilteredList(String newText){
        if(LocationMap == null){
            return null;
        }else {
            for(String key : LocationMap.keySet()) {
                if (checkKey(key, newText)) {
                    Map<String, Object> vMap = (Map<String, Object>) LocationMap.get(key);
                    Double la = (Double) vMap.get("la");
                    Double lo = (Double) vMap.get("lo");
                    SearchItem item = new SearchItem(key, la, lo);
                    filteredList.add(item);
                }
            }
            return filteredList;
        }
    }

    public ArrayList<String> generateFilteredStringList(String newText){
        //Log.e("generateFilteredStringList","Here 1");
        if(LocationMap == null){
            //Log.e("generateFilteredStringList","Here 2");
            return null;
        }else {
            //Log.e("generateFilteredStringList","Here 3");
            for(String key : totalStringList) {
                //Log.d("generateFilteredStringList","The key is " + key);
                if (checkKey(key.toLowerCase(), newText.toLowerCase())) {
                  //  Log.e("generateFilteredStringList","Here 4");
                    filteredStringList.add(key);
                }
            }
            Collections.sort(filteredStringList);
            //Log.e("generateFilteredStringList","Here 5");
            return filteredStringList;
        }
    }

    public void resetFilteredStringList(){
        filteredStringList = new ArrayList<String>();
    }

    public ArrayList<String> getTotalStringList(){
        return totalStringList;
    }

    @SuppressWarnings("unchecked")
    public ArrayList<Double> getLaLo(String key){
        if(LocationMap == null){
            return null;
        }else {
            //Log.e("getLaLo","Here 1");
            ArrayList<Double> lalo = new ArrayList<Double>();
            Map<String, Object> vMap = (Map<String, Object>) LocationMap.get(key);
            lalo.add((Double) vMap.get("la"));
            lalo.add((Double) vMap.get("lo"));
            return lalo;
        }
    }


}
