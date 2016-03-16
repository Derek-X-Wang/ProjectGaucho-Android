package com.intbridge.projects.gaucholife.controllers;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.intbridge.projects.gaucholife.MainActivity;
import com.intbridge.projects.gaucholife.PGDatabaseManager;
import com.intbridge.projects.gaucholife.R;
import com.baoyz.swipemenulistview.SwipeMenuAdapter;
import com.intbridge.projects.gaucholife.utils.PGSQLiteHelper;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Store Notification
 * Created by Derek on 3/14/2016.
 */
public class NotificationFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_notification, container, false);

        initSwipeList(v);
        return v;
    }

    private void initSwipeList(View v) {
        SwipeMenuListView mListView = (SwipeMenuListView) v.findViewById(R.id.notification_list);
        List<Map<String,String>> notificationList;
        PGSQLiteHelper helper = new PGSQLiteHelper(getActivity());
        notificationList = helper.getAllNotification();
        NotificationAdapter mAdapter = new NotificationAdapter(notificationList);
        mListView.setAdapter(mAdapter);

        // step 1. create a MenuCreator
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getActivity().getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(dp2px(90));
                // set a icon
                deleteItem.setIcon(R.drawable.ic_delete_white_36dp);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
        // set creator
        mListView.setMenuCreator(creator);

        // step 2. listener item click event
        mListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        // delete
                        break;
                }
                return false;
            }
        });
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }


    class NotificationAdapter extends BaseAdapter {

        private List<Map<String,String>> dataList;

        public NotificationAdapter(List<Map<String,String>> list) {
            dataList = list;
        }
        @Override
        public int getCount() {
            return dataList.size();
        }

        @Override
        public Object getItem(int position) {
            return dataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = View.inflate(getActivity().getApplicationContext(),
                        R.layout.listview_notification, null);
                holder = new ViewHolder(convertView);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Map<String,String> dict = (Map<String, String>) getItem(position);
            String message = dict.get(PGSQLiteHelper.KEY_DESCRIPTION);
            String time = dict.get(PGSQLiteHelper.KEY_TIME);
            holder.message.setText(message);
            holder.time.setText(time);
            convertView.setTag(holder);
            return convertView;
        }

        class ViewHolder {
            public TextView message;
            public TextView time;

            public ViewHolder(View view) {
                message = (TextView) view.findViewById(R.id.listview_notification_message);
                time = (TextView) view.findViewById(R.id.listview_notification_time);
            }
        }
    }
}
