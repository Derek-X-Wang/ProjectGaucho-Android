package com.intbridge.projects.gaucholife.utils;

import com.intbridge.projects.gaucholife.PGDatabaseManager;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * Handle Cloud Call
 * Created by Derek on 12/29/2015.
 */
public class CloudCodeManager {

    public static List<ParseObject> pickRandomCoupon(String lastCouponID){
        return pickRandomCoupons(1, false, lastCouponID);
    }

    public static List<ParseObject> pickRandomCoupons(int amount, boolean isUnique, String lastCouponID){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserStat");
        query.whereEqualTo("UUID",PGDatabaseManager.getUUID());
        ParseObject userStat = null;
        String userid = "";
        try {
            userStat = query.getFirst();
            userid = userStat.getObjectId();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        HashMap<String, Object> params = new HashMap<>();
        params.put("amount", amount);
        params.put("unique", isUnique);
        params.put("dayofweek", DateUtils.convertDateToDayOfWeek( new Date() ) );
        params.put("userstatid", userid);
        if (!lastCouponID.isEmpty()) params.put("lastcouponid", lastCouponID);
        try {
            return ParseCloud.callFunction("pickRandomCoupon", params);
        } catch (ParseException e) {
            //e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static void redeemCoupon(final String lastCouponID) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserStat");
        query.whereEqualTo("UUID",PGDatabaseManager.getUUID());
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                HashMap<String, Object> params = new HashMap<>();
                params.put("userID", parseObject.getObjectId());
                params.put("couponID", lastCouponID);
                try {
                    ParseCloud.callFunction("redeemCoupon", params);
                } catch (ParseException pe) {
                    //e.printStackTrace();

                }
            }
        });

    }
}
