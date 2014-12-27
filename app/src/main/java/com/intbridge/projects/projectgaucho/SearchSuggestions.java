package com.intbridge.projects.projectgaucho;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import xmlwise.Plist;
import xmlwise.XmlParseException;

/**
 * Created by Derek on 12/26/2014.
 */
public class SearchSuggestions {

    private ArrayList<SearchItem> totalList = new ArrayList<SearchItem>();
    private ArrayList<SearchItem> filteredList = new ArrayList<SearchItem>();
    private Map<String, Object> LocationMap = null;

    private Map<String, Object> loadPListByXmlwise(String school){
        Map<String, Object> schoolMap = null;
        try{
            schoolMap = Plist.load("../res/raw/UCSB.plist"); // loads the (nested) properties.
        }catch (XmlParseException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            return schoolMap;
        }
    }
    SearchSuggestions(String school){
            LocationMap = loadPListByXmlwise(school);
    }

    private boolean matchKey(String key, String queryText){
        if(queryText.length() > key.length()){
            return false;
        }
        for (int i = 0; i <queryText.length(); i++){
            if(key.charAt(i)!=queryText.charAt(i)){
                return  false;
            }
        }
        return true;
    }
    private boolean checkKey(String key, String queryText){
        String key1 = null;
        String key2 = null;
        if (key.contains("|")) {
            String[] parts = key.split("|");
            key1 = parts[0];
            key2 = parts[1];
            if(matchKey(key1,queryText)||matchKey(key2,queryText)){
                return true;
            }else {
                return false;
            }
        } else {
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


}
