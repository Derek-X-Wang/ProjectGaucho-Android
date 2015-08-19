package com.intbridge.projects.gaucholife.test;

import com.intbridge.projects.gaucholife.PGDatabaseManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Map;

/**
 * Created by Derek on 8/20/2015.
 */
@Config(manifest= Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class quickTest {
    @Test
    public void testWebRequest() {
        PGDatabaseManager databaseManager = new PGDatabaseManager();
        Map<String,Map> e = databaseManager.getUCSBCommonsDataFromHTML("2015", "08", "19");
//        //assertThat();
//            for (Element name : e) {
//                Log.e("Name: ", name.text());
//            }
    }
}
