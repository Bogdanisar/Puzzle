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
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Random;

class Piece {
    public static final int borderSize = 5;

    ImageView image;
    Bitmap originalBitmap;
    int targeti, targetj;
    int[] outerColor = new int[4], innerColor = new int[4];

    Piece(Bitmap imageBitmap, int pos, SquareGame context) {
        int i = pos / SquareGame.numHorizontal;
        int j = pos % SquareGame.numHorizontal;
        this.targeti = i;
        this.targetj = j;

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
        params.leftMargin = getRandomPosition(context.getTotalScreenWidth(), SquareGame.pieceWidth);
        params.topMargin = getRandomPosition(context.getTotalScreenHeight() - context.getStatusBarHeight(), SquareGame.pieceHeight);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        this.image.setLayoutParams(params);

        this.getColors(i, j, this.outerColor, this.innerColor, context);
        this.drawMarginIf(new boolean[] {true, true, true, true});
    }

    public int getRandomPosition(int totalSize, int imageSize) {
        Random random = new Random();

        return random.nextInt(totalSize - imageSize + 1);
    }

    public void update(Piece[][] pieceMatrix, boolean updateNeighbours) {
        int[] dx = new int[] {-1, 0, +1, 0};
        int[] dy = new int[] {0, +1, 0, -1};
        boolean[] shouldDraw = new boolean[] {true, true, true, true};

        for (int k = 0; k < 4; ++k) {
            int nx = this.targeti + dx[k];
            int ny = this.targetj + dy[k];

            if (!(0 <= nx && nx < SquareGame.numVertical && 0 <= ny && ny < SquareGame.numHorizontal)) {
                continue;
            }

            if (pieceMatrix[nx][ny] != null) {
                shouldDraw[k] = false;

                if (updateNeighbours) {
                    pieceMatrix[nx][ny].update(pieceMatrix, false);
                }
            }
        }

        this.drawMarginIf(shouldDraw);
    }

    public void drawMarginIf(boolean[] shouldDraw) {
        Bitmap currentBitmap = this.originalBitmap.copy(this.originalBitmap.getConfig(), true);

        for (int marginIndex = 0; marginIndex < 4; ++marginIndex) {
            if (shouldDraw[marginIndex]) {
                this.drawMargin(currentBitmap, this.outerColor[marginIndex], this.innerColor[marginIndex], marginIndex);
            }
        }

        this.image.setImageBitmap(currentBitmap);
    }

    public void getColors(int i, int j, int[] outerColor, int[] innerColor, Context context) {
        for (int k = 0; k < 4; ++k) {
            outerColor[k] = context.getResources().getColor(R.color.outerPieceColor);
            innerColor[k] = context.getResources().getColor(R.color.innerPieceColor);
        }

        if (i == 0) {
            outerColor[0] = context.getResources().getColor(R.color.outerMarginColor);
            innerColor[0] = context.getResources().getColor(R.color.innerMarginColor);
        }
        if (i == SquareGame.numVertical - 1) {
            outerColor[2] = context.getResources().getColor(R.color.outerMarginColor);
            innerColor[2] = context.getResources().getColor(R.color.innerMarginColor);
        }

        if (j == 0) {
            outerColor[3] = context.getResources().getColor(R.color.outerMarginColor);
            innerColor[3] = context.getResources().getColor(R.color.innerMarginColor);
        }
        if (j == SquareGame.numHorizontal - 1) {
            outerColor[1] = context.getResources().getColor(R.color.outerMarginColor);
            innerColor[1] = context.getResources().getColor(R.color.innerMarginColor);
        }
    }

    public void drawMargin(Bitmap bitmap, int outerColor, int innerColor, int marginIndex) {
        if (marginIndex == 0) {
            this.drawTopMargin(bitmap, outerColor, innerColor);
        }
        else if (marginIndex == 1) {
            this.drawRightMargin(bitmap, outerColor, innerColor);
        }
        else if (marginIndex == 2) {
            this.drawBottomMargin(bitmap, outerColor, innerColor);
        }
        else if (marginIndex == 3) {
            this.drawLeftMargin(bitmap, outerColor, innerColor);
        }
    }

    public void drawTopMargin(Bitmap bitmap, int outerColor, int innerColor) {
        for (int x = 0; x < bitmap.getWidth(); ++x) {
            for (int y = 0; y < Piece.borderSize; ++y) {
                int color;
                if (y % 2 == 0) {
                    color = outerColor;
                }
                else {
                    color = innerColor;
                }

                bitmap.setPixel(x, y, color);
            }
        }
    }

    public void drawBottomMargin(Bitmap bitmap, int outerColor, int innerColor) {
        for (int x = 0; x < bitmap.getWidth(); ++x) {
            for (int y = 0; y < Piece.borderSize; ++y) {
                int color;
                if (y % 2 == 0) {
                    color = outerColor;
                }
                else {
                    color = innerColor;
                }

                bitmap.setPixel(x, bitmap.getHeight() - y - 1, color);
            }
        }
    }

    public void drawLeftMargin(Bitmap bitmap, int outerColor, int innerColor) {
        for (int x = 0; x < Piece.borderSize; ++x) {
            for (int y = 0; y < bitmap.getHeight(); ++y) {
                int color;
                if (x % 2 == 0) {
                    color = outerColor;
                }
                else {
                    color = innerColor;
                }

                bitmap.setPixel(x, y, color);
            }
        }
    }

    public void drawRightMargin(Bitmap bitmap, int outerColor, int innerColor) {
        for (int x = 0; x < Piece.borderSize; ++x) {
            for (int y = 0; y < bitmap.getHeight(); ++y) {
                int color;
                if (x % 2 == 0) {
                    color = outerColor;
                }
                else {
                    color = innerColor;
                }

                bitmap.setPixel(bitmap.getWidth() - x - 1, y, color);
            }
        }
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
    boolean hasAttached = false;
    int attachedOffsetX = 0, attachedOffsetY = 0;

    Piece[][] pieceMatrix = new Piece[SquareGame.numVertical][SquareGame.numHorizontal];

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
        this.topLayout = findViewById(R.id.squareGameLayout);

        Log.i(TAG, "statusBarHeight: " + this.getStatusBarHeight());
        SquareGame.pieceWidth = SquareGame.getTotalScreenWidth() / numHorizontal;
        SquareGame.pieceHeight = (SquareGame.getTotalScreenHeight() - this.getStatusBarHeight()) / numVertical;
        this.setLimits();
        Log.i(TAG, "statusBarHeight: " + this.getStatusBarHeight());
        Log.i(TAG, "minY: " + this.minY);

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
        this.minY = 0;
        this.maxX = this.getTotalScreenWidth() - SquareGame.pieceWidth;
        this.maxY = (this.getTotalScreenHeight() - this.getStatusBarHeight()) - SquareGame.pieceHeight;
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






    public void changePosition(View v, float x, float y) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) v.getLayoutParams();

        int newX = (int)x - this.attachedOffsetX;
        newX = Math.max(newX, this.minX);
        newX = Math.min(newX, this.maxX);

        int newY = (int)y - this.attachedOffsetY - this.getStatusBarHeight();
//        Log.i(TAG, "newY = " + newY); ////////////////////////////////////////////////////////////////////////////////////
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
//            String str = "The screen has been touched at: " + event.getX() + ", " + event.getY();
//            Log.i("myTag", str);
//            Log.i("myTag", "============================");

            ListIterator<Piece> it = this.pieceList.listIterator();
            while (it.hasNext()) {
                Piece piece = it.next();
                int eventX = (int)event.getX();
                int eventY = (int)event.getY();

                if (this.pointIsInsideView(piece.image, eventX, eventY)) {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) piece.image.getLayoutParams();
                    this.attachedOffsetX = eventX - params.leftMargin;
                    this.attachedOffsetY = eventY - (params.topMargin + this.getStatusBarHeight());
                    Log.i(TAG, "attachedOffsetX = " + this.attachedOffsetX); ////////////////////////////////////////////////////////////////
                    Log.i(TAG, "attachedOffsetY = " + this.attachedOffsetY); ////////////////////////////////////////////////////////////////

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

            Piece piece = this.pieceList.getFirst();
            int eventX = (int)event.getX();
            int eventY = (int)event.getY();

            int cornerX = (int)eventX - this.attachedOffsetX;
            cornerX = Math.max(cornerX, this.minX);
            cornerX = Math.min(cornerX, this.maxX);

            int cornerY = (int)eventY - this.attachedOffsetY - this.getStatusBarHeight();
            cornerY = Math.max(cornerY, this.minY);
            cornerY = Math.min(cornerY, this.maxY);

            int errorX = SquareGame.pieceWidth / 4;
            int errorY = SquareGame.pieceHeight / 4;

            int targetX = piece.targetj * SquareGame.pieceWidth;
            int targetY = piece.targeti * SquareGame.pieceHeight;

            if (Math.abs(targetX - cornerX) <= errorX && Math.abs(targetY - cornerY) <= errorY) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) piece.image.getLayoutParams();
                params.leftMargin = targetX;
                params.topMargin = targetY;

                this.pieceMatrix[piece.targeti][piece.targetj] = piece;

                piece.update(this.pieceMatrix, true);
                this.pieceList.removeFirst();

                Iterator<Piece> it = this.pieceList.descendingIterator();
                while (it.hasNext()) {
                    Piece currentPiece = it.next();
                    ImageView image = currentPiece.image;
                    image.bringToFront();
                }

                this.topLayout.requestLayout();
            }

            this.hasAttached = false;
        }
        else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            float eventX = event.getX();
            float eventY = event.getY();
            if (this.hasAttached) {
                this.changePosition(this.pieceList.getFirst().image, eventX, eventY);
            }

            Log.i(TAG, "Movement at: " + Float.toString(eventX) + ", " + Float.toString(eventY));
        }

        return true;
    }
}
