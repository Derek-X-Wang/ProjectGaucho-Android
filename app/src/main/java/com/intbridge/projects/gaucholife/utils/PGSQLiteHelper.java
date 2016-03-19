package com.intbridge.projects.gaucholife.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SQLite helper
 * Created by Derek on 3/15/2016.
 */
public class PGSQLiteHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "PGDatabase";

    // table name
    private static final String TABLE_INFO = "Notifications";

    // date index for table info
    private static final String IDX_TABLE_INFO_LOCATION = "index_date";

    // LocationInfos Table Columns names
    public static final String KEY_TIME = "time";
    public static final String KEY_DESCRIPTION = "description";

    public PGSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public PGSQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    // Creating Tables
    //Please note here is no primary key in the table, time serves as the unique key
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_INFO_TABLE = "CREATE TABLE " + TABLE_INFO + "("
                + KEY_TIME + " TEXT," + KEY_DESCRIPTION + " TEXT " + ")";
        db.execSQL(CREATE_INFO_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INFO);

        // Create tables again
        onCreate(db);
    }

    public void addNotification(String description, String date) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TIME, date);
        values.put(KEY_DESCRIPTION, description);
        // Inserting Row
        db.insert(TABLE_INFO, null, values);

        db.close(); // Closing database connection
    }

    public void deleteNotification(String time, String message) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_INFO,
                KEY_TIME + "=? AND " + KEY_DESCRIPTION + "=?",
                new String[] {time, message});
        db.close();
    }

    public List<Map<String,String>> getAllNotification() {
        List<Map<String,String>> notificationList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_INFO;

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Map<String,String> notification = new HashMap<>();
                notification.put(KEY_TIME,cursor.getString(0));
                notification.put(KEY_DESCRIPTION,cursor.getString(1));
                notificationList.add(notification);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return notificationList;
    }
}
