package com.example.puzzle.history;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.example.puzzle.jigsaw.ActivityJigsawGame;
import com.example.puzzle.ActivityMain;
import com.example.puzzle.squareGame.ActivitySquareGame;
import com.example.puzzle.R;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ActivityHistory extends AppCompatActivity {
    public static String TAG = ActivityMain.COMMON_TAG + "_ActivityHistory";

    private List<PieceGameHistory> getHistoryItems(String key, SharedPreferences preferences) {
        return PieceGameHistory.getInstanceArray(preferences.getString(key, ""));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        LinearLayout layout = findViewById(R.id.historyItemLinearLayout);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String[] historyItemKeys = {
                ActivitySquareGame.HISTORY_PREFERENCE_KEY,
                ActivityJigsawGame.HISTORY_PREFERENCE_KEY
        };

        List<HistoryItem> allHistoryItems = new LinkedList<>();
        for (String key : historyItemKeys) {

            List<? extends HistoryItem> items = this.getHistoryItems(key, preferences);
            for (HistoryItem item : items) {
                Log.i(ActivityHistory.TAG, "itemGamemode = " + item.getGamemode());
                Log.i(ActivityHistory.TAG, "item.toString() = " + item.toString());
                Log.i(ActivityHistory.TAG, ActivityMain.SEPARATOR);
            }

            allHistoryItems.addAll(items);
        }
        Collections.sort(allHistoryItems);
        Collections.reverse(allHistoryItems);


        for (HistoryItem item : allHistoryItems) {
            View view = item.getViewForHistory(this);
            layout.addView(view);
        }
        layout.requestLayout();
    }
}
