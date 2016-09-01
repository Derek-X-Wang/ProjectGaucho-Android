package com.intbridge.projects.gaucholife.utils;

import java.text.SimpleDateFormat;
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

    public static String convertDateToString(Date date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd HH:mm");
        return dateFormat.format(date);
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

    public static Date convertIntegerToDate(int dateInt){
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

    public static int convertDateToInteger(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        // Note: zero based!
        String dateString = String.format("%d%02d%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
        return Integer.parseInt(dateString);
    }

    public static Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }
}
