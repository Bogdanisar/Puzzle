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

import java.util.ArrayList;
import java.util.List;

public class GameOptions extends AppCompatActivity {
    public static String COMMON_TAG = "puzzletag";
    public static String TAG = MainActivity.COMMON_TAG;
    private EditText rowInput;
    private EditText columnInput;
    private Integer imageSelected = null;
    private Integer imageSelectedPrior = null;

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

        Integer intentImage = null;
        for (int i = 0; i < this.imageViewArray.size(); ++i) {
            if (this.imageSelected.equals(this.imageViewArray.get(i))) {
                intentImage = this.imageResourceArray.get(i);
                break;
            }
        }

        if (intentImage == null) {
            Log.e(GameOptions.TAG, "imageSelected was not found, reverting to default; id: " + Integer.toString(this.imageSelected));
            intentImage = R.drawable.p1;
        }

        intent.putExtra("rowNumber", rowNumber);
        intent.putExtra("columnNumber", columnNumber);
        intent.putExtra("imageSelected", intentImage);
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

        if(this.imageSelectedPrior != null){
            ImageView currentSelectedImage = findViewById(this.imageSelectedPrior);
            removeSelectedImageTheme(currentSelectedImage);
        }
    }

    private void removeSelectedImageTheme (ImageView imageView){
        imageView.setBackgroundColor(0x00000000);
    }

    public void selectImage(View view) {
        if (this.imageSelected != null && this.imageSelected.equals(view.getId())) {
            return;
        }

        this.imageSelectedPrior = this.imageSelected;
        this.imageSelected = view.getId();

        this.applySelectedImageTheme(view);
    }
}
