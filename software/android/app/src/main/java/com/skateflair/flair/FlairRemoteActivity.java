package com.skateflair.flair;

/**
 * Created by myron on 7/11/16.
 */
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class FlairRemoteActivity extends Activity {

    public final String TAG = "FlairRemoteActivity";

    String title;
    String text;
    TextView txttitle;
    TextView txttext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_flair_remote);

        // Create Notification Manager
        NotificationManager notificationmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Dismiss Notification
        notificationmanager.cancel(0);

        // Retrive the data from MainActivity.java
        Intent i = getIntent();

        title = i.getStringExtra("title");

        // Locate the Title View
        txttitle = (TextView) findViewById(R.id.lbltitle);

        // Set the data into TextView
        txttitle.setText(title);
    }


}