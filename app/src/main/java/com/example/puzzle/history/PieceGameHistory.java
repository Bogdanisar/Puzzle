package com.example.puzzle.history;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.puzzle.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class PieceGameHistory extends HistoryItem {
    static public String fieldSeparator = "&";
    static public String keyValueSeparator = "=";

    private String gamemode;
    private Long startTimeInMilliseconds;
    private Integer imageId;
    private Long durationInMilliseconds;
    private Integer numHorizontal, numVertical;

    public PieceGameHistory(String gamemode, long startTimeInMilliseconds, int imageId, long durationInMilliseconds, int numHorizontal, int numVertical) {
        if ("".equals(gamemode)) {
            gamemode = null;
        }

        this.gamemode = gamemode;
        this.startTimeInMilliseconds = startTimeInMilliseconds;
        this.imageId = imageId;
        this.durationInMilliseconds = durationInMilliseconds;
        this.numHorizontal = numHorizontal;
        this.numVertical = numVertical;
    }

    @Override
    public String getGamemode() {
        return gamemode;
    }

    @Override
    public int getImageId() {
        return imageId;
    }

    public Long getStartTimeInMilliseconds() {
        return startTimeInMilliseconds;
    }

    public long getDurationInMilliseconds() {
        return durationInMilliseconds;
    }

    public int getNumHorizontal() {
        return numHorizontal;
    }

    public int getNumVertical() {
        return numVertical;
    }

    public static PieceGameHistory getInstance(String data) {
        String gamemode = "";
        Long startTimeInSeconds = 0L, durationInMilliseconds = 0L;
        Integer imageId = 0, numHorizontal = 0, numVertical = 0;

        for (String field : data.split(PieceGameHistory.fieldSeparator)) {
            String fieldKey, fieldValue;
            String[] keyValue = field.split(PieceGameHistory.keyValueSeparator);

            fieldKey = keyValue[0];
            if (fieldKey.equals("gamemode")) {
                if (keyValue.length == 1) {
                    gamemode = null;
                }
                else {
                    gamemode = keyValue[1];
                }

                continue;
            }

            fieldValue = keyValue[1];
            if (fieldKey.equals("startTimeInMilliseconds")) {
                startTimeInSeconds = Long.parseLong(fieldValue);
            }
            else if (fieldKey.equals("durationInMilliseconds")) {
                durationInMilliseconds = Long.parseLong(fieldValue);
            }
            else if (fieldKey.equals("imageId")) {
                imageId = Integer.parseInt(fieldValue);
            }
            else if (fieldKey.equals("numHorizontal")) {
                numHorizontal = Integer.parseInt(fieldValue);
            }
            else if (fieldKey.equals("numVertical")) {
                numVertical = Integer.parseInt(fieldValue);
            }
        }

        return new PieceGameHistory(gamemode, startTimeInSeconds, imageId, durationInMilliseconds, numHorizontal, numVertical);
    }

    public String toString() {
        String result = "";

        String add = "";
        if (this.gamemode != null) {
            add = this.gamemode;
        }
        result += "gamemode" + PieceGameHistory.keyValueSeparator + add + PieceGameHistory.fieldSeparator;

        result += "startTimeInMilliseconds" + PieceGameHistory.keyValueSeparator + this.startTimeInMilliseconds + PieceGameHistory.fieldSeparator;
        result += "durationInMilliseconds" + PieceGameHistory.keyValueSeparator + this.durationInMilliseconds + PieceGameHistory.fieldSeparator;
        result += "imageId" + PieceGameHistory.keyValueSeparator + this.imageId + PieceGameHistory.fieldSeparator;
        result += "numHorizontal" + PieceGameHistory.keyValueSeparator + this.numHorizontal + PieceGameHistory.fieldSeparator;
        result += "numVertical" + PieceGameHistory.keyValueSeparator + this.numVertical + PieceGameHistory.fieldSeparator;

        return result;
    }


    public static List<PieceGameHistory> getInstanceArray(String data) {
        List<PieceGameHistory> ret = new LinkedList<>();

        if (data == null || data.length() == 0) {
            return ret;
        }

        for (String itemData : data.split(HistoryItem.itemSeparator)) {
            PieceGameHistory item = PieceGameHistory.getInstance(itemData);
            ret.add(item);
        }

        return ret;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PieceGameHistory == false) {
            return false;
        }
        PieceGameHistory other = (PieceGameHistory) obj;

        if (this.gamemode != null && this.gamemode.equals(other.gamemode) == false) {
            return false;
        }
        if (this.gamemode == null && other.gamemode != null) {
            return false;
        }

        if (this.startTimeInMilliseconds.equals(other.startTimeInMilliseconds) == false) {
            return false;
        }
        if (this.durationInMilliseconds.equals(other.durationInMilliseconds) == false) {
            return false;
        }
        if (this.imageId.equals(other.imageId) == false) {
            return false;
        }
        if (this.numHorizontal.equals(other.numHorizontal) == false) {
            return false;
        }
        if (this.numVertical.equals(other.numVertical) == false) {
            return false;
        }

        return true;
    }

    @Override
    public View getViewForHistory(Activity activity) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View top = inflater.inflate(R.layout.history_item, null, false);

        ImageView image = top.findViewById(R.id.historyItemImageView);
        image.setImageResource(this.imageId);


        TextView gamemodeView = top.findViewById(R.id.historyItemGamemode);
        gamemodeView.setText(this.gamemode);


        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(this.startTimeInMilliseconds);
        Date date = calendar.getTime();
        String dateString = new SimpleDateFormat("HH:mm:ss 'on' dd/MM/yyyy").format(date);

        TextView datePlayedView = top.findViewById(R.id.historyItemDate);
        datePlayedView.setText(dateString);


        Long duration = this.durationInMilliseconds;
        long numDays = duration / (1000 * 60 * 60 * 24);
        duration %= (1000 * 60 * 60 * 24);
        long numHours = duration / (1000 * 60 * 60);
        duration %= (1000 * 60 * 60);
        long numMinutes = duration / (1000 * 60);
        duration %= (1000 * 60);
        double numSeconds = (double)duration / 1000;

        String durationString;
        if (numDays != 0) {
            durationString = String.format("%d D, %d H, %d M, %.2f S", numDays, numHours, numMinutes, numSeconds);
        }
        else if (numHours != 0) {
            durationString = String.format("%d H, %d M, %.2f S", numHours, numMinutes, numSeconds);
        }
        else if (numMinutes != 0) {
            durationString = String.format("%d Min, %.2f Sec", numMinutes, numSeconds);
        }
        else {
            durationString = String.format("%.2f Seconds", numSeconds);
        }

        TextView gameTimeView = top.findViewById(R.id.historyItemTimePlayed);
        gameTimeView.setText(durationString);


        TextView dimensionsView = top.findViewById(R.id.historyItemDimensions);
        dimensionsView.setText(this.numHorizontal + "x" + this.numVertical);

        return top;
    }
}
