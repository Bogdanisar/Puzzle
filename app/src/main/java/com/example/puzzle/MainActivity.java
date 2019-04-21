package com.example.puzzle;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    public static String COMMON_TAG = "puzzletag";
    public static String TAG = MainActivity.COMMON_TAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TAG += this.getClass().getSimpleName();

        Log.i(TAG, "test");
    }

    public void test(View view) {

    }
}
