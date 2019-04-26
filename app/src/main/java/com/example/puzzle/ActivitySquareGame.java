package com.example.puzzle;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

public class ActivitySquareGame extends AppCompatActivity {
    public static String TAG = ActivityMain.COMMON_TAG;

    static int imageId = R.drawable.p1;
    static int numHorizontal = 5;
    static int numVertical = 5;
    static final int maxImageWidth = 1000, maxImageHeight = 1000;
    static int pieceWidth = 0;
    static int pieceHeight = 0;

    static boolean gamemodeSimple = false;
    static boolean gamemodeShell = false;
    static boolean gamemodeOnePiece = false;

    int minX, minY, maxX, maxY;
    boolean hasAttached = false;
    int attachedOffsetX = 0, attachedOffsetY = 0;

    SquareGamePiece[][] pieceMatrix;
    int numPlacedPieces = 0;

    RelativeLayout topLayout = null;
    LinkedList<SquareGamePiece> pieceList = null;

    public int getStatusBarHeight() {
        int statusBarHeight = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }

        return statusBarHeight;
    }

    public int getTopBarDimension() {
        int menuId = R.dimen.squareGameMenuSize;
        int menuSize = (int)this.getResources().getDimension(menuId);
        return getStatusBarHeight() + menuSize;
    }

    public static int getTotalScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getTotalScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public int getContainerWidth() {
        return ActivitySquareGame.getTotalScreenWidth();
    }

    public int getContainerHeight() {
        return ActivitySquareGame.getTotalScreenHeight() - this.getTopBarDimension();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_square_game);

        this.setGameParameters();

        ActivitySquareGame.TAG += this.getClass().getSimpleName();
        this.topLayout = findViewById(R.id.squareGamePuzzleLayout);

        Log.i(TAG, "statusBarHeight: " + this.getStatusBarHeight());
        ActivitySquareGame.pieceWidth = this.getContainerWidth() / numHorizontal;
        ActivitySquareGame.pieceHeight = this.getContainerHeight() / numVertical;
        this.setLimits();
        Log.i(TAG, "statusBarHeight: " + this.getStatusBarHeight());
        Log.i(TAG, "minY: " + this.minY);

        Bitmap scaledImage = this.getScaledImage();

        this.pieceList = new LinkedList<>();
        for (int pos = 0; pos < ActivitySquareGame.numHorizontal * ActivitySquareGame.numVertical; ++pos) {
            SquareGamePiece piece = new SquareGamePiece(scaledImage, pos, this);
            pieceList.add(piece);
            topLayout.addView(piece.image);
        }

        Iterator<SquareGamePiece> it = this.pieceList.descendingIterator();
        while (it.hasNext()) {
            SquareGamePiece piece = it.next();
            ImageView image = piece.image;
            image.bringToFront();
        }

        topLayout.requestLayout();
    }

    private void setGameParameters() {
        Bundle bundle = this.getIntent().getExtras();
        ActivitySquareGame.imageId = (Integer)bundle.get("imageSelected");
        ActivitySquareGame.numVertical = (Integer)bundle.get("columnNumber");
        ActivitySquareGame.numHorizontal = (Integer)bundle.get("rowNumber");

        Object typeObject = bundle.get("type");
        if (typeObject != null && ((String)typeObject).equals("shell")) {
            this.gamemodeShell = true;
        }
        else {
            this.gamemodeSimple = true;
        }

        this.pieceMatrix = new SquareGamePiece[ActivitySquareGame.numVertical][ActivitySquareGame.numHorizontal];
    }

    public void setLimits() {
        this.minX = 0;
        this.minY = 0;

        this.maxX = this.getContainerWidth() - ActivitySquareGame.pieceWidth;
        this.maxY = this.getContainerHeight() - ActivitySquareGame.pieceHeight;
    }

    public Bitmap getScaledImage() {
        Bitmap imageBitmap = BitmapFactory.decodeResource(this.getResources(), ActivitySquareGame.imageId);
        int newImageWidth = Math.min(ActivitySquareGame.maxImageWidth, imageBitmap.getWidth());
        int newImageHeight = Math.min(ActivitySquareGame.maxImageHeight, imageBitmap.getHeight());
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

    public void changePosition(View v, float x, float y) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) v.getLayoutParams();

        int newX = (int)x - this.attachedOffsetX;
        newX = Math.max(newX, this.minX);
        newX = Math.min(newX, this.maxX);

        int newY = (int)y - this.attachedOffsetY;
        newY = Math.max(newY, this.minY);
        newY = Math.min(newY, this.maxY);

        params.leftMargin = newX;
        params.topMargin = newY;
        v.requestLayout();
    }

    private boolean pointIsInsideView(View v, int x, int y) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) v.getLayoutParams();

        if (!(params.leftMargin <= x && x < params.leftMargin + params.width)) {
            return false;
        }
        if (!(params.topMargin <= y && y < params.topMargin + params.height)) {
            return false;
        }
        return true;
    }

    private boolean pieceCanBePlaced(SquareGamePiece piece) {
        if (this.gamemodeSimple) {
            return true;
        }
        if (this.gamemodeShell) {
            int i = piece.targeti, j = piece.targetj;
            if (i == 0 || i == ActivitySquareGame.numVertical - 1) {
                return true;
            }
            if (j == 0 || j == ActivitySquareGame.numHorizontal - 1) {
                return true;
            }

            int[] di = new int[] {-1, 0, +1, 0};
            int[] dj = new int[] {0, +1, 0, -1};
            for (int k = 0; k < 4; ++k) {
                int ni = i + di[k];
                int nj = j + dj[k];

                if (this.pieceMatrix[ni][nj] != null) {
                    return true;
                }
            }

            return false;
        }

        return true;
    }

    private void updateText() {
        TextView textView = findViewById(R.id.menuGameText);
        String text;

        ++this.numPlacedPieces;
        if (this.numPlacedPieces == ActivitySquareGame.numHorizontal * ActivitySquareGame.numVertical) {
            text = this.getResources().getString(R.string.wonText);
        } else {
            text = this.numPlacedPieces + " / " + ActivitySquareGame.numHorizontal * ActivitySquareGame.numVertical;
        }

        textView.setText(text);
        textView.requestLayout();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int eventX = (int)event.getX();
        int eventY = (int)event.getY() - this.getTopBarDimension();

        if (eventY < 0) {
            return true;
        }

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
//            String str = "The screen has been touched at: " + event.getX() + ", " + event.getY();
//            Log.i("myTag", str);
//            Log.i("myTag", "============================");

            ListIterator<SquareGamePiece> it = this.pieceList.listIterator();
            while (it.hasNext()) {
                SquareGamePiece piece = it.next();

                if (this.pointIsInsideView(piece.image, eventX, eventY)) {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) piece.image.getLayoutParams();
                    this.attachedOffsetX = eventX - params.leftMargin;
                    this.attachedOffsetY = eventY - params.topMargin;
//                    Log.i(TAG, "attachedOffsetX = " + this.attachedOffsetX);
//                    Log.i(TAG, "attachedOffsetY = " + this.attachedOffsetY);

                    it.remove();
                    this.pieceList.addFirst(piece);

                    this.hasAttached = true;
                    piece.image.bringToFront();

                    break;
                }
            }
        }
        else if (event.getAction() == MotionEvent.ACTION_UP) {
//            String str = "The screen has been released at: " + event.getX() + ", " + event.getY();
//            Log.i("myTag", str);
//            Log.i("myTag", "============================");

            if (this.hasAttached == false) {
                return true;
            }

            SquareGamePiece piece = this.pieceList.getFirst();

            int cornerX = eventX - this.attachedOffsetX;
            cornerX = Math.max(cornerX, this.minX);
            cornerX = Math.min(cornerX, this.maxX);

            int cornerY = eventY - this.attachedOffsetY;
            cornerY = Math.max(cornerY, this.minY);
            cornerY = Math.min(cornerY, this.maxY);

            int errorX = ActivitySquareGame.pieceWidth / 3;
            int errorY = ActivitySquareGame.pieceHeight / 3;

            int targetX = piece.targetj * ActivitySquareGame.pieceWidth;
            int targetY = piece.targeti * ActivitySquareGame.pieceHeight;

            Log.i(TAG, "pieceCanBePlace: " + this.pieceCanBePlaced(piece)); /////////////////////////////////////////////
            if ( (this.pieceCanBePlaced(piece)) && (Math.abs(targetX - cornerX) <= errorX && Math.abs(targetY - cornerY) <= errorY) ) {
                this.updateText();
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) piece.image.getLayoutParams();
                params.leftMargin = targetX;
                params.topMargin = targetY;

                this.pieceMatrix[piece.targeti][piece.targetj] = piece;

                piece.update(this.pieceMatrix, true);
                this.pieceList.removeFirst();

                Iterator<SquareGamePiece> it = this.pieceList.descendingIterator();
                while (it.hasNext()) {
                    SquareGamePiece currentPiece = it.next();
                    ImageView image = currentPiece.image;
                    image.bringToFront();
                }

                this.topLayout.requestLayout();
            }

            this.hasAttached = false;
        }
        else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (this.hasAttached) {
                this.changePosition(this.pieceList.getFirst().image, eventX, eventY);
            }

//            Log.i(TAG, "Movement at: " + Float.toString(eventX) + ", " + Float.toString(eventY));
        }

        return true;
    }
}
