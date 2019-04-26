package com.example.puzzle;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ActivityHistory extends AppCompatActivity {
    public static String COMMON_TAG = "puzzletag";
    public static String TAG = ActivityHistory.COMMON_TAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
    }
}
