package com.intbridge.projects.projectgaucho;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Derek on 12/29/2014.
 */
public class SearchAdapter extends CursorAdapter {
    //TODO: I think there is a error here,maybe in bindView, which cause the changing search suggestion text after delete to 0
    private List<String> items;

    private TextView text;

    public SearchAdapter(Context context, Cursor cursor, List items) {

        super(context, cursor, false);

        this.items = items;

    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        // Show list item data from cursor
        text.setText(items.get(cursor.getPosition()));

        // Alternatively show data direct from database
        //text.setText(cursor.getString(cursor.getColumnIndex("column_name")));

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.item, parent, false);

        text = (TextView) view.findViewById(R.id.text);

        return view;

    }

}