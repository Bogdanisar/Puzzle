package com.example.puzzle;

import android.content.Intent;
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
        Intent intent = new Intent(this, SquareGame.class);
//        String message = mMessageEditText.getText().toString();
//        intent.putExtra(EXTRA_MESSAGE, message);
//        startActivityForResult(intent, TEXT_REQUEST);
        startActivity(intent);
    }

    public void startGame(View view) {
        Intent intent = new Intent(this, GameOptions.class);
        startActivity(intent);
    }
}
