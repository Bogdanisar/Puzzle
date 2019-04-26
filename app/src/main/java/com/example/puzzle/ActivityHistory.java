package com.example.puzzle;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.example.puzzle.history.SquareGameHistory;

public class ActivityHistory extends AppCompatActivity {
    public static String COMMON_TAG = "puzzletag";
    public static String TAG = ActivityHistory.COMMON_TAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        LinearLayout layout = findViewById(R.id.historyItemLinearLayout);

        String key = ActivitySquareGame.HISTORY_PREFERENCE_KEY;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SquareGameHistory[] itemArray = SquareGameHistory.getInstanceArray(preferences.getString(key, ""));

        for (SquareGameHistory item : itemArray) {
            View view = item.getViewForHistory(this);
            layout.addView(view);
        }

        layout.requestLayout();
    }
}
