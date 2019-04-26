package com.example.puzzle;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ActivityGameOptions extends AppCompatActivity {
    public static String COMMON_TAG = "puzzletag";
    public static String TAG = ActivityMain.COMMON_TAG;
    private EditText rowInput;
    private EditText columnInput;
    private Integer imageSelected = null;
    private Integer imageSelectedPrior = null;
    private Integer gamemodeSelected = null;
    private Integer gamemodeSelectedPrior = null;

    private List<Integer> imageViewArray = new ArrayList<>();
    private List<Integer> imageResourceArray = new ArrayList<>();

    {
        imageViewArray.add(R.id.imageView1);
        imageResourceArray.add(R.drawable.p1);

        imageViewArray.add(R.id.imageView2);
        imageResourceArray.add(R.drawable.p2);

        imageViewArray.add(R.id.imageView3);
        imageResourceArray.add(R.drawable.p3);

        imageViewArray.add(R.id.imageView4);
        imageResourceArray.add(R.drawable.p4);

        imageViewArray.add(R.id.imageView5);
        imageResourceArray.add(R.drawable.p5);

        imageViewArray.add(R.id.imageView6);
        imageResourceArray.add(R.drawable.p6);

        imageViewArray.add(R.id.imageView7);
        imageResourceArray.add(R.drawable.p7);

        imageViewArray.add(R.id.imageView8);
        imageResourceArray.add(R.drawable.p8);

        imageViewArray.add(R.id.imageView9);
        imageResourceArray.add(R.drawable.p9);

        imageViewArray.add(R.id.imageView10);
        imageResourceArray.add(R.drawable.p10);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_options);

        TAG += this.getClass().getSimpleName();

        rowInput = findViewById(R.id.inputRowNumber);
        columnInput = findViewById(R.id.inputColumnNumber);
    }

    public void startGame(View view) {
        String rowsEntered = rowInput.getText().toString();
        if(rowsEntered.equals("")){
            Toast toast = Toast.makeText(getApplicationContext(), "No Row Number Entered!", Toast.LENGTH_LONG);
            toast.show();
            return;
        }
        String columnsEntered = columnInput.getText().toString();
        if(columnsEntered.equals("")){
            Toast toast = Toast.makeText(getApplicationContext(), "No Column Number Entered!", Toast.LENGTH_LONG);
            toast.show();
            return;
        }

        Integer rowNumber = Integer.parseInt(rowsEntered);
        Integer columnNumber = Integer.parseInt(columnsEntered);

        boolean incorrectFlag = false;
        String message = null;

        if(rowNumber<2){
            incorrectFlag = true;
            message = "Row Number Too Small!";
        }
        if(!incorrectFlag && rowNumber>9){
            incorrectFlag = true;
            message = "Row Number Too Big!";
        }
        if(!incorrectFlag && columnNumber<2){
            incorrectFlag = true;
            message = "Column Number Too Small!";
        }
        if(!incorrectFlag && columnNumber>10){
            incorrectFlag = true;
            message = "Column Number Too Big!";
        }

        if (!incorrectFlag && this.gamemodeSelected == null) {
            incorrectFlag = true;
            message = "No Gamemode Selected!";
        }

        if(!incorrectFlag && this.imageSelected == null){
            incorrectFlag = true;
            message = "No Image Selected!";
        }

        if(incorrectFlag){
            Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
            toast.show();
            return;
        }

        Log.i(ActivityGameOptions.TAG, this.imageSelected.toString());
        Log.i(ActivityGameOptions.TAG, rowNumber.toString());
        Log.i(ActivityGameOptions.TAG, columnNumber.toString());

        Integer intentImage = null;
        for (int i = 0; i < this.imageViewArray.size(); ++i) {
            if (this.imageSelected.equals(this.imageViewArray.get(i))) {
                intentImage = this.imageResourceArray.get(i);
                break;
            }
        }

        if (intentImage == null) {
            Log.e(ActivityGameOptions.TAG, "imageSelected was not found, reverting to default; id: " + Integer.toString(this.imageSelected));
            intentImage = R.drawable.p1;
        }

        if (gamemodeSelected.equals(R.id.gamemodeSquareGameView)) {
            Intent intent = new Intent(this, ActivitySquareGame.class);
            intent.putExtra("rowNumber", rowNumber);
            intent.putExtra("columnNumber", columnNumber);
            intent.putExtra("imageSelected", intentImage);
            intent.putExtra("type", "simple");
            startActivity(intent);
        }
        else if (gamemodeSelected.equals(R.id.gamemodeSquareGameShellView)) {
            Intent intent = new Intent(this, ActivitySquareGame.class);
            intent.putExtra("rowNumber", rowNumber);
            intent.putExtra("columnNumber", columnNumber);
            intent.putExtra("imageSelected", intentImage);
            intent.putExtra("type", "shell");
            startActivity(intent);
        }
        else if (gamemodeSelected.equals(R.id.gamemodeSquareGameOnePieceView)) {
            Intent intent = new Intent(this, ActivitySquareGame.class);
            intent.putExtra("rowNumber", rowNumber);
            intent.putExtra("columnNumber", columnNumber);
            intent.putExtra("imageSelected", intentImage);
            intent.putExtra("type", "onePiece");
            startActivity(intent);
        }

        //in ActivitySquareGame Activity: String value = getIntent().getExtras().getString(key);
    }


    // methods for selecting, deselecting
    private void applyBackgroundFromId(View view, int id) {
        Drawable drawable = ContextCompat.getDrawable(this, id);
        this.applyBackground(view, drawable);
    }

    private void applyBackgroundFromColor(View view, int color) {
        Drawable drawable = new ColorDrawable(color);
        this.applyBackground(view, drawable);
    }

    private void applyBackground(View view, Drawable drawable) {
        int sdk = android.os.Build.VERSION.SDK_INT;
        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackgroundDrawable(drawable);
        }
        else {
            view.setBackground(drawable);
        }
    }

    private void removeBackground(Integer id) {
        if (id == null) {
            return;
        }

        View view = findViewById(id);
        view.setBackgroundColor(0x00000000);
    }

    public void selectImage(View view) {
        if (this.imageSelected != null && this.imageSelected.equals(view.getId())) {
            return;
        }

        this.imageSelectedPrior = this.imageSelected;
        this.imageSelected = view.getId();

        this.applyBackgroundFromId(view, R.color.colorAccent);
        this.removeBackground(this.imageSelectedPrior);
    }

    public void selectGamemode(View view) {
        if (this.gamemodeSelected != null && this.gamemodeSelected.equals(view.getId())) {
            return;
        }

        this.gamemodeSelectedPrior = this.gamemodeSelected;
        this.gamemodeSelected = view.getId();

        this.applyBackgroundFromId(view, R.color.colorAccentText);
        this.removeBackground(this.gamemodeSelectedPrior);
    }
}
