package com.example.puzzle;

import android.app.ActionBar;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

class Piece {
    ImageView image;
    Bitmap originalBitmap;

    Piece(Bitmap imageBitmap, int pos, Context context) {
        int i = pos / SquareGame.numHorizontal;
        int j = pos % SquareGame.numHorizontal;

        int subImageWidth = imageBitmap.getWidth() / SquareGame.numHorizontal;
        int subImageHeight = imageBitmap.getHeight() / SquareGame.numVertical;
        int x = i * subImageWidth;
        int y = j * subImageHeight;
        this.originalBitmap = Bitmap.createBitmap(imageBitmap, x, y, subImageWidth, subImageHeight);

        Bitmap scaledOriginal = Bitmap.createScaledBitmap(this.originalBitmap, SquareGame.pieceWidth, SquareGame.pieceHeight, false);
        if (scaledOriginal != this.originalBitmap) {
            this.originalBitmap.recycle();
        }
        this.originalBitmap = scaledOriginal;

        this.image = new ImageView(context);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(SquareGame.pieceWidth, SquareGame.pieceHeight);
        params.leftMargin = i * SquareGame.pieceWidth;
        params.topMargin = j * SquareGame.pieceHeight;
        this.image.setLayoutParams(params);
        this.image.setImageBitmap(this.originalBitmap);
    }
}

public class SquareGame extends AppCompatActivity {
    static final int imageId = R.drawable.p1;
    static final int numHorizontal = 5;
    static final int numVertical = 5;
    static final int pieceWidth = getScreenWidth() / numVertical;
    static final int pieceHeight = getScreenHeight() / numHorizontal;

    RelativeLayout topLayout = null;
    LinkedList<Piece> pieceList = null;

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_square_game);

        this.topLayout = findViewById(R.id.squareGameLayout);

        Bitmap imageBitmap = BitmapFactory.decodeResource(this.getResources(), SquareGame.imageId);
        int screenWidth = SquareGame.getScreenWidth();
        int screenHeight = SquareGame.getScreenHeight();

        int newImageWidth = imageBitmap.getWidth() / numHorizontal * numHorizontal;
        int newImageHeight = imageBitmap.getHeight() / numVertical * numVertical;

        Bitmap scaledImage = Bitmap.createScaledBitmap(imageBitmap, newImageWidth, newImageHeight, false);
        if (scaledImage != imageBitmap) {
            imageBitmap.recycle();
        }

        LinkedList<Piece> pieceList = new LinkedList<>();
        for (int pos = 0; pos < SquareGame.numHorizontal * SquareGame.numVertical; ++pos) {
            Piece piece = new Piece(scaledImage, pos, this);
            pieceList.add(piece);
            topLayout.addView(piece.image);
        }

        Iterator<Piece> it = this.pieceList.descendingIterator();
        while (it.hasNext()) {
            Piece piece = it.next();
            ImageView image = piece.image;
            image.bringToFront();
        }

        topLayout.requestLayout();
    }
}
