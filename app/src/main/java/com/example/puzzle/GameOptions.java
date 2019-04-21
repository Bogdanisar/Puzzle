package com.example.puzzle;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class GameOptions extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_options);
    }

    public void startGame(View view) {
        Intent intent = new Intent(this, SquareGame.class);
        startActivity(intent);
    }
}
