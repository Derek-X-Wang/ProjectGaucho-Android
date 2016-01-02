package com.intbridge.projects.gaucholife.utils;

import java.util.Calendar;
import java.util.Date;

/**
 *
 * Created by Derek on 12/29/2015.
 */
public class DateUtils {

    // Sun(0) Mon(1).....Sat(6)
    public static int convertDateToDayOfWeek(Date date){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.DAY_OF_WEEK)-1;
    }
}
