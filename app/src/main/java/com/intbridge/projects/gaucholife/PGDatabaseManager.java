package com.intbridge.projects.gaucholife;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * Created by Derek on 8/10/2015.
 */
public class PGDatabaseManager {
    public void getUCSBCommonsDataFromHTML(String year,String month,String day){
        Document doc = null;
        try {
            String url = "https://appl.housing.ucsb.edu/" + "menu?day=" + year + "-" + month + "-" + day + "&meal=";
            doc = Jsoup.connect(url).get();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        
    }
}
