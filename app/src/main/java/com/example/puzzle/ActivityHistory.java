package com.example.puzzle;

import android.app.ActivityManager;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.example.puzzle.history.HistoryItem;
import com.example.puzzle.history.SquareGameHistory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ActivityHistory extends AppCompatActivity {
    public static String TAG = ActivityMain.COMMON_TAG + "_ActivityHistory";

//    private List<SquareGameHistory> getHistoryItems(String key, SharedPreferences preferences) {
//        return SquareGameHistory.getInstanceArray(preferences.getString(key, ""));
//    }

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


//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_history);
//
//        LinearLayout layout = findViewById(R.id.historyItemLinearLayout);
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
//        String[] historyItemKeys = {
//                ActivitySquareGame.HISTORY_PREFERENCE_KEY,
//                ActivityJigsawGame.HISTORY_PREFERENCE_KEY
//        };
//
//        List<HistoryItem> allHistoryItems = new LinkedList<>();
//        for (String key : historyItemKeys) {
//            Log.i(ActivityHistory.TAG, "key = " + key);
//
//            List<? extends HistoryItem> items = this.getHistoryItems(key, preferences);
//            for (HistoryItem item : items) {
//                Log.i(ActivityHistory.TAG, "itemGamemode = " + item.getGamemode());
//                Log.i(ActivityHistory.TAG, "item.toString() = " + item.toString());
//                Log.i(ActivityHistory.TAG, ActivityMain.SEPARATOR);
//            }
//
//            allHistoryItems.addAll(items);
//        }
//        Collections.sort(allHistoryItems);
//
//
//        for (HistoryItem item : allHistoryItems) {
//            View view = item.getViewForHistory(this);
//            layout.addView(view);
//        }
//        layout.requestLayout();
//    }
}
