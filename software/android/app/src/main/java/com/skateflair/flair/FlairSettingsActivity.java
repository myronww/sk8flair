package com.skateflair.flair;

import android.os.Bundle;
import android.app.Activity;

public class FlairSettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flair_settings);
        getActionBar().setDisplayHomeAsUpEnabled(true);


    }

}
