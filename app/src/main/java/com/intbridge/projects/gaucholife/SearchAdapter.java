package com.intbridge.projects.gaucholife;

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
 * A simple adapter for searching suggestions with matrixCursor
 */
public class SearchAdapter extends CursorAdapter {

    // Store the string list that needed to show for search
    private List<String> items;

    public SearchAdapter(Context context, Cursor cursor, List items) {

        super(context, cursor, false);

        this.items = items;

    }

    // handle which suggestion has been clicked
    public String getKey(int position){
        return items.get(position);
    }

    // this function update the search list view
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        // Show list item data from cursor
        TextView text = (TextView) view.findViewById(R.id.text);
        text.setText(items.get(cursor.getPosition()));

        // Alternatively show data direct from database
        //text.setText(cursor.getString(cursor.getColumnIndex("text")));

    }

    // inflate for more list view if needed
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item, parent, false);

        return view;

    }

}