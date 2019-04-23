package com.example.puzzle;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class GameOptions extends AppCompatActivity {
    public static String COMMON_TAG = "puzzletag";
    public static String TAG = MainActivity.COMMON_TAG;
    private EditText rowInput;
    private EditText columnInput;
    private Integer imageSelected = null;
    private Integer imageSelectedPrior = null;

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

        if(!incorrectFlag && imageSelected==null){
            incorrectFlag = true;
            message = "No Image Selected!";
        }

        if(incorrectFlag){
            Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
            toast.show();
            return;
        }

        Intent intent = new Intent(this, SquareGame.class);
        Log.i(GameOptions.TAG, this.imageSelected.toString());
        Log.i(GameOptions.TAG, rowNumber.toString());
        Log.i(GameOptions.TAG, columnNumber.toString());
        intent.putExtra("rowNumber", rowNumber);
        intent.putExtra("columnNumber", columnNumber);
        intent.putExtra("imageSelected", this.imageSelected);
        startActivity(intent);

        //in SquareGame Activity: String value = getIntent().getExtras().getString(key);
    }

    private void applySelectedImageTheme (View view){
        ImageView image = (ImageView)view;

        String name = "@color/colorAccent";
        int id = getResources().getIdentifier(name, "drawable", getPackageName());
        Drawable drawable = getResources().getDrawable(id);

        int sdk = android.os.Build.VERSION.SDK_INT;
        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            image.setBackgroundDrawable(drawable);
        } else {
            image.setBackground(drawable);
        }

        if(imageSelected != null){
            ImageView currentSelectedImage = findViewById(imageSelectedPrior);
            removeSelectedImageTheme(currentSelectedImage);
        }
    }

    private void removeSelectedImageTheme (ImageView imageView){
        imageView.setBackgroundColor(0x00000000);
    }

    public void selectImage(View view) {
        if(view.getId()==R.id.imageView1){
            applySelectedImageTheme(view);

            imageSelected = R.drawable.p1;
            imageSelectedPrior = R.id.imageView1;
            return;
        }
        if(view.getId()==R.id.imageView2){
            applySelectedImageTheme(view);
            imageSelected = R.drawable.p2;
            imageSelectedPrior = R.id.imageView2;
            return;
        }
    }
}
