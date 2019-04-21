package com.example.puzzle;

import android.app.ActionBar;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

class Piece {
    ImageView image;
    Bitmap originalBitmap;

    Piece(Bitmap imageBitmap, int pos, SquareGame context) {
        int i = pos / SquareGame.numHorizontal;
        int j = pos % SquareGame.numHorizontal;

        int subImageWidth = imageBitmap.getWidth() / SquareGame.numHorizontal;
        int subImageHeight = imageBitmap.getHeight() / SquareGame.numVertical;
        int x = j * subImageWidth;
        int y = i * subImageHeight;
        this.originalBitmap = Bitmap.createBitmap(imageBitmap, x, y, subImageWidth, subImageHeight);

        Bitmap scaledOriginal = Bitmap.createScaledBitmap(this.originalBitmap, SquareGame.pieceWidth, SquareGame.pieceHeight, true);
        if (scaledOriginal != this.originalBitmap) {
            this.originalBitmap.recycle();
        }
        this.originalBitmap = scaledOriginal;

        this.image = new ImageView(context);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(SquareGame.pieceWidth, SquareGame.pieceHeight);
//        params.leftMargin = j * SquareGame.pieceWidth;
//        params.topMargin = i * SquareGame.pieceHeight;

        params.leftMargin = getRandomPosition(context.getTotalScreenWidth(), SquareGame.pieceWidth);
        params.topMargin = getRandomPosition(context.getTotalScreenHeight() - context.getStatusBarHeight(), SquareGame.pieceHeight);

        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        this.image.setLayoutParams(params);
        this.image.setImageBitmap(this.originalBitmap);

        Log.i(SquareGame.TAG, Integer.toString(pos));
        Log.i(SquareGame.TAG, Integer.toString(i));
        Log.i(SquareGame.TAG, Integer.toString(j));
        Log.i(SquareGame.TAG, Integer.toString(params.leftMargin));
        Log.i(SquareGame.TAG, Integer.toString(params.topMargin));;
        Log.i(SquareGame.TAG, "=============================");
    }

    public int getRandomPosition(int totalSize, int imageSize) {
        Random random = new Random();

        return random.nextInt(totalSize - imageSize + 1);
    }
}

public class SquareGame extends AppCompatActivity {
    public static String TAG = MainActivity.COMMON_TAG;

    static final int imageId = R.drawable.p1;
    static final int numHorizontal = 5;
    static final int numVertical = 5;
    static final int maxImageWidth = 1000, maxImageHeight = 1000;
    static int pieceWidth = 0;
    static int pieceHeight = 0;

    int minX, minY, maxX, maxY;

    RelativeLayout topLayout = null;
    LinkedList<Piece> pieceList = null;

    public static int getTotalScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getTotalScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public int getStatusBarHeight() {
        int statusBarHeight = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }

        return statusBarHeight;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_square_game);

        SquareGame.TAG += this.getClass().getSimpleName();
        this.setLimits();
        this.topLayout = findViewById(R.id.squareGameLayout);

        SquareGame.pieceWidth = SquareGame.getTotalScreenWidth() / numHorizontal;
        SquareGame.pieceHeight = (SquareGame.getTotalScreenHeight() - this.getStatusBarHeight()) / numVertical;

        Bitmap scaledImage = this.getScaledImage();

        this.pieceList = new LinkedList<>();
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

    public void setLimits() {
        this.minX = 0;
        this.minY = this.getStatusBarHeight();
        this.maxX = this.getTotalScreenWidth();
        this.maxY = this.getTotalScreenHeight();
    }

    public Bitmap getScaledImage() {
        Bitmap imageBitmap = BitmapFactory.decodeResource(this.getResources(), SquareGame.imageId);
        int newImageWidth = Math.min(SquareGame.maxImageWidth, imageBitmap.getWidth());
        int newImageHeight = Math.min(SquareGame.maxImageHeight, imageBitmap.getHeight());
        Bitmap auxBitmap = Bitmap.createScaledBitmap(imageBitmap, newImageWidth, newImageHeight, true);
        if (auxBitmap != imageBitmap) {
            imageBitmap.recycle();
        }
        imageBitmap = auxBitmap;

        newImageWidth = imageBitmap.getWidth() / numHorizontal * numHorizontal;
        newImageHeight = imageBitmap.getHeight() / numVertical * numVertical;

        Bitmap scaledImage = Bitmap.createScaledBitmap(imageBitmap, newImageWidth, newImageHeight, true);
        if (scaledImage != imageBitmap) {
            imageBitmap.recycle();
        }

        return scaledImage;
    }
}
