package com.example.puzzle.history;

import android.app.Activity;
import android.view.View;

public abstract class HistoryItem implements Comparable<HistoryItem> {
    static public String itemSeparator = ";";

    public abstract String getGamemode();
    public abstract Long getStartTimeInMilliseconds();
    public abstract int getImageId();
    public abstract View getViewForHistory(Activity activity);

    @Override
    public int compareTo(HistoryItem other) {
        return this.getStartTimeInMilliseconds().compareTo(other.getStartTimeInMilliseconds());
    }

    @Override
    public abstract String toString();

    public static String addInstanceToDataString(String data, HistoryItem instance) {
        if (data == null || data.equals("")) {
            data = instance.toString();
        }
        else {
            data += itemSeparator;
            data += instance.toString();
        }

        return data;
    }
}
