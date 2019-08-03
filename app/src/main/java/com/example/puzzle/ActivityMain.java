package com.example.puzzle;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.puzzle.about.ActivityAbout;
import com.example.puzzle.gameOptions.ActivityGameOptions;
import com.example.puzzle.history.ActivityHistory;
import com.example.puzzle.settings.ActivitySettings;

public class ActivityMain extends AppCompatActivity {
    public static String TAG = Constants.COMMON_TAG + "ActivityMain";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TAG += this.getClass().getSimpleName();

        Log.i(TAG, "test");
    }

    public void startGame(View view) {
        Intent intent = new Intent(this, ActivityGameOptions.class);
        startActivity(intent);
    }

    public void openHistory(View view) {
        Intent intent = new Intent(this, ActivityHistory.class);
        startActivity(intent);
    }

    public void openAbout(View view) {
        Intent intent = new Intent(this, ActivityAbout.class);
        startActivity(intent);
    }
    public void openSettings(View view) {
        Intent intent = new Intent(this, ActivitySettings.class);
        startActivity(intent);
    }


}
