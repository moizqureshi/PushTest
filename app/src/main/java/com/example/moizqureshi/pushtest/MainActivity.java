package com.example.moizqureshi.pushtest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.onesignal.OneSignal;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONObject;
import org.json.JSONException;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    String myEmail;
    String partnerEmail;
    String partnerID;
    String msg;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText msgTxt = (EditText)findViewById(R.id.msgText);
        final EditText searchEmailTxt = (EditText)findViewById(R.id.searchEmailText);
        final EditText setEmailTxt = (EditText)findViewById(R.id.setEmailTxt);
        final Button setEmailBtn = (Button)findViewById(R.id.setEmailBtn);
        final Button searchEmailBtn = (Button)findViewById(R.id.searchEmailBtn);
        final Button sendPushBtn = (Button)findViewById(R.id.pushBtn);



        OneSignal.startInit(this)
                .setAutoPromptLocation(true)
                .setNotificationOpenedHandler(new ExampleNotificationOpenedHandler())
                .init();
        OneSignal.enableInAppAlertNotification(false);
        OneSignal.enableNotificationsWhenActive(false);


        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                .applicationId("moizqureshipushtest")
                .server("http://pushtest126.herokuapp.com/parse/")
                .clientKey("qureshi1990")
                .build()
        );

        setEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myEmail = setEmailTxt.getText().toString();
                setEmail();
            }
        });

        searchEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                partnerEmail = searchEmailTxt.getText().toString();
                searchEmail();
            }
        });

        sendPushBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                msg = msgTxt.getText().toString();
                try {
                    OneSignal.postNotification(new JSONObject("{'contents': {'en':'" + msg + "'}, 'include_player_ids': ['" + partnerID + "']}"), null);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void setEmail() {
        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(String userId, String registrationId) {
                final String signal_id = userId;
                ParseQuery<ParseObject> query = ParseQuery.getQuery("user");
                query.whereEqualTo("email", myEmail);
                query.getFirstInBackground(new GetCallback<ParseObject>() {
                    public void done(ParseObject object, ParseException e) {
                        if (object == null) {
                            ParseObject obj = new ParseObject("user");
                            obj.put("email", myEmail);
                            obj.put("signal_id", signal_id);
                            obj.saveInBackground();
                        } else {
                            // object exists
                        }
                    }
                });
            }
        });
    }

    private void searchEmail() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("user");
        query.whereEqualTo("email", partnerEmail);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (object == null) {
                    alertDialog(false);
                } else {
                    partnerID = object.getString("signal_id");
                    alertDialog(true);
                }
            }
        });
    }

    private void alertDialog(boolean success) {
        if(!success){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Sorry");
            builder.setMessage("Could not find your partner: " + partnerEmail);
            builder.setCancelable(true);
            builder.show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Sucess");
            builder.setMessage("Found your partner:" + partnerEmail);
            builder.setCancelable(true);
            builder.show();
        }

    }

    private class ExampleNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
        /**
         * Callback to implement in your app to handle when a notification is opened from the Android status bar or
         * a new one comes in while the app is running.
         * This method is located in this Application class as an example, you may have any class you wish implement NotificationOpenedHandler and define this method.
         *
         * @param message        The message string the user seen/should see in the Android status bar.
         * @param additionalData The additionalData key value pair section you entered in on onesignal.com.
         * @param isActive       Was the app in the foreground when the notification was received.
         */
        @Override
        public void notificationOpened(String message, JSONObject additionalData, boolean isActive) {
            String additionalMessage = "";

            try {
                if (additionalData != null) {
                    if (additionalData.has("actionSelected"))
                        additionalMessage += "Pressed ButtonID: " + additionalData.getString("actionSelected");

                    additionalMessage = message + "\nFull additionalData:\n" + additionalData.toString();
                }

                Log.d("OneSignalExample", "message:\n" + message + "\nadditionalMessage:\n" + additionalMessage);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }





}
