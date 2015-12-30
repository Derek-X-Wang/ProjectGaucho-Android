package com.intbridge.projects.gaucholife.utils;

import com.intbridge.projects.gaucholife.PGDatabaseManager;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;

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
        HashMap<String, Object> params = new HashMap<>();
        params.put("amount", amount);
        params.put("unique", isUnique);
        params.put("dayofweek", DateUtils.convertDateToDayOfWeek(new Date()) );
        params.put("userstatid", PGDatabaseManager.getUUID());
        params.put("lastcouponid", lastCouponID);
        try {
            return ParseCloud.callFunction("pickRandomCoupon", params);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
