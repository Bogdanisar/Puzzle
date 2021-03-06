package com.example.puzzle.gameOptions;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.puzzle.Constants;
import com.example.puzzle.R;
import com.example.puzzle.jigsaw.ActivityJigsawGame;
import com.example.puzzle.squareGame.SGOnePiece;
import com.example.puzzle.squareGame.SGShell;
import com.example.puzzle.squareGame.SGSimple;

import java.util.ArrayList;
import java.util.List;

public class ActivityGameOptions extends AppCompatActivity {
    public static String TAG = Constants.COMMON_TAG + "ActivityGameOptions";
    public static int GALLERY_IMAGE_INTENT = 1;

    public static final int minPieces = 3;
    public static final int maxPieces = 6;

    private EditText rowInput;
    private EditText columnInput;
    private Integer imageSelected = null;
    private Integer gamemodeSelected = null;

    private List<Integer> imageViewArray = new ArrayList<>();
    private List<Integer> imageResourceArray = new ArrayList<>();
    private List<Integer> smallImageResourceArray = new ArrayList<>();

    private Uri userBitmapUri = null;

    {
        imageViewArray.add(R.id.imageView1);
        imageResourceArray.add(R.drawable.p1);
        smallImageResourceArray.add(R.drawable.p1small);

        imageViewArray.add(R.id.imageView2);
        imageResourceArray.add(R.drawable.p2);
        smallImageResourceArray.add(R.drawable.p2small);

        imageViewArray.add(R.id.imageView3);
        imageResourceArray.add(R.drawable.p3);
        smallImageResourceArray.add(R.drawable.p3small);

        imageViewArray.add(R.id.imageView4);
        imageResourceArray.add(R.drawable.p4);
        smallImageResourceArray.add(R.drawable.p4small);

        imageViewArray.add(R.id.imageView5);
        imageResourceArray.add(R.drawable.p5);
        smallImageResourceArray.add(R.drawable.p5small);

        imageViewArray.add(R.id.imageView6);
        imageResourceArray.add(R.drawable.p6);
        smallImageResourceArray.add(R.drawable.p6small);

        imageViewArray.add(R.id.imageView7);
        imageResourceArray.add(R.drawable.p7);
        smallImageResourceArray.add(R.drawable.p7small);

        imageViewArray.add(R.id.imageView8);
        imageResourceArray.add(R.drawable.p8);
        smallImageResourceArray.add(R.drawable.p8small);

        imageViewArray.add(R.id.imageView9);
        imageResourceArray.add(R.drawable.p9);
        smallImageResourceArray.add(R.drawable.p9small);

        imageViewArray.add(R.id.imageView10);
        imageResourceArray.add(R.drawable.p10);
        smallImageResourceArray.add(R.drawable.p10small);

        imageViewArray.add(R.id.imageView11);
        imageResourceArray.add(R.drawable.p11);
        smallImageResourceArray.add(R.drawable.p11small);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_game_options);

        TAG += this.getClass().getSimpleName();

        rowInput = findViewById(R.id.inputRowNumber);
        columnInput = findViewById(R.id.inputColumnNumber);


        // setting default game parameter values;
        rowInput.setText("3");
        columnInput.setText("3");
        selectGamemode( findViewById(R.id.gamemodeJigsawGameView) );
        selectImage( findViewById(R.id.imageView1) );
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

        if (rowNumber < minPieces){
            incorrectFlag = true;
            message = "Row Number Too Small!";
        }
        else if (maxPieces < rowNumber){
            incorrectFlag = true;
            message = "Row Number Too Big!";
        }
        else if (columnNumber < minPieces){
            incorrectFlag = true;
            message = "Column Number Too Small!";
        }
        else if (maxPieces < columnNumber){
            incorrectFlag = true;
            message = "Column Number Too Big!";
        }
        else if (this.gamemodeSelected == null) {
            incorrectFlag = true;
            message = "No Gamemode Selected!";
        }
        else if (this.imageSelected == null && this.userBitmapUri == null){
            incorrectFlag = true;
            message = "No Image Selected!";
        }

        if (incorrectFlag) {
            Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
            toast.show();
            return;
        }

        Log.i(ActivityGameOptions.TAG, rowNumber.toString());
        Log.i(ActivityGameOptions.TAG, columnNumber.toString());

        Integer intentImageId = null;
        Integer smallIntentImageId = null;
        for (int i = 0; i < this.imageViewArray.size(); ++i) {
            if (this.imageViewArray.get(i).equals(this.imageSelected)) {
                intentImageId = this.imageResourceArray.get(i);
                smallIntentImageId = this.smallImageResourceArray.get(i);
                break;
            }
        }

        Intent intent = null;
        if (gamemodeSelected.equals(R.id.gamemodeSquareGameView)) {
            intent = new Intent(this, SGSimple.class);
        }
        else if (gamemodeSelected.equals(R.id.gamemodeSquareGameShellView)) {
            intent = new Intent(this, SGShell.class);
        }
        else if (gamemodeSelected.equals(R.id.gamemodeSquareGameOnePieceView)) {
            intent = new Intent(this, SGOnePiece.class);
        }
        else if (gamemodeSelected.equals(R.id.gamemodeJigsawGameView)) {
            intent = new Intent(this, ActivityJigsawGame.class);
        }

        intent.putExtra("rowNumber", rowNumber);
        intent.putExtra("columnNumber", columnNumber);

        if (intentImageId != null) {
            intent.putExtra("imageSelected", intentImageId);
            intent.putExtra("smallImageSelected", smallIntentImageId);
        }
        else {
            intent.putExtra("userImageUri", this.userBitmapUri.toString());
            intent.putExtra("smallImageSelected", R.drawable.user_image_small);
        }

        startActivity(intent);
        finish();
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

        Integer imageSelectedPrior = this.imageSelected;
        this.imageSelected = view.getId();

        this.applyBackgroundFromId(view, R.color.colorAccent);
        this.removeBackground(imageSelectedPrior);
    }

    public void selectGamemode(View view) {
        if (this.gamemodeSelected != null && this.gamemodeSelected.equals(view.getId())) {
            return;
        }

        Integer gamemodeSelectedPrior = this.gamemodeSelected;
        this.gamemodeSelected = view.getId();

        this.applyBackgroundFromId(view, R.color.colorAccentText);
        this.removeBackground(gamemodeSelectedPrior);
    }



    public void chooseImage(View view) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, GALLERY_IMAGE_INTENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == GALLERY_IMAGE_INTENT && resultCode == Activity.RESULT_OK && intent != null) {

            // Get the URI of the selected file
            final Uri uri = intent.getData();
            this.userBitmapUri = uri;

            this.removeBackground(this.imageSelected);
            this.imageSelected = null;
        }
    }
}
