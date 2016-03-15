package com.intbridge.projects.gaucholife.controllers;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.intbridge.projects.gaucholife.MainActivity;
import com.intbridge.projects.gaucholife.PGDatabaseManager;
import com.intbridge.projects.gaucholife.R;

/**
 * left menu
 * Created by Derek on 3/7/2016.
 */
public class FeedbackFragment extends Fragment {

    private MainActivity host;
    private PGDatabaseManager databaseManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_feedback, container, false);

        databaseManager = new PGDatabaseManager();
        host = (MainActivity)getActivity();

        setFacebookLink(v);

        setInternalMessage(v);

        return v;
    }

    private void setInternalMessage(View v) {
        TextView messageText = (TextView)v.findViewById(R.id.messagehint);
        Button sendBotton = (Button)v.findViewById(R.id.sendbotton);
        final LinearLayout messageGroup = (LinearLayout)v.findViewById(R.id.sendmessagegroup);

        messageText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageGroup.setVisibility(View.VISIBLE);
            }
        });
        sendBotton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText userMessage = (EditText)v.findViewById(R.id.anonymousmessage);
                Editable raw = userMessage.getText();
                String message = "";
                if(raw != null) message = userMessage.getText().toString();
                userMessage.setText("");
                databaseManager.sendUserReport(message);
                Toast.makeText(host, "Success", Toast.LENGTH_LONG).show();

                InputMethodManager imm = (InputMethodManager) host.getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                messageGroup.setVisibility(View.GONE);
            }
        });
    }

    private void setFacebookLink(View v) {
        Button fbBotton = (Button)v.findViewById(R.id.facebookbotton);
        fbBotton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fbIntent = getOpenFacebookIntent(getActivity());
                startActivity(fbIntent);
            }
        });
    }

    private Intent getOpenFacebookIntent(Context context) {
        try {
            context.getPackageManager().getPackageInfo("com.facebook.katana", 0);
            return new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/475816682600877"));
        } catch (Exception e) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/gaucholife"));
        }
    }
}
