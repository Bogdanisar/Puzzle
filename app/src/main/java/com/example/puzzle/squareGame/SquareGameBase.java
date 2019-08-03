package com.example.puzzle.squareGame;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.puzzle.ActivityMain;
import com.example.puzzle.Constants;
import com.example.puzzle.R;
import com.example.puzzle.history.HistoryItem;
import com.example.puzzle.history.PieceGameHistory;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Random;

public abstract class SquareGameBase extends AppCompatActivity {
//    public static final String KEY_HISTORY_PIECEGAME = "PieceGameHistory";
//
//    protected int smallImageId = 0;
//    protected int numHorizontal = 0, numVertical = 0;
//
//    protected Long startTimeInMilliseconds;
//
//    protected int pieceWidth = 0;
//    protected int pieceHeight = 0;
//
//    protected int minX, minY, maxX, maxY;
//    protected boolean hasAttached = false;
//    protected int attachedOffsetX = 0, attachedOffsetY = 0;
//
//    protected Bitmap imageBitmap = null;
//    protected SGPiece[][] pieceMatrix = null;
//    protected int numPlacedPieces = 0;
//    protected int[] di = new int[] {-1, 0, +1, 0};
//    protected int[] dj = new int[] {0, +1, 0, -1};
//
//    protected RelativeLayout topLayout = null;
//    protected LinkedList<SGPiece> pieceList = new LinkedList<>();
//    protected SGState initState = null;
//
//    public int getStatusBarHeight() {
//        int statusBarHeight = 0;
//        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
//        if (resourceId > 0) {
//            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
//        }
//
//        return statusBarHeight;
//    }
//
//    public int getTopBarDimension() {
//        int menuId = R.dimen.TopGameMenu_Size;
//        int menuSize = (int)this.getResources().getDimension(menuId);
//        return getStatusBarHeight() + menuSize;
//    }
//
//    public int getTotalScreenWidth() {
//        return Resources.getSystem().getDisplayMetrics().widthPixels;
//    }
//
//    public int getTotalScreenHeight() {
//        return Resources.getSystem().getDisplayMetrics().heightPixels;
//    }
//
//    public int getContainerWidth() {
//        return this.getTotalScreenWidth();
//    }
//
//    public int getContainerHeight() {
//        return this.getTotalScreenHeight() - this.getTopBarDimension();
//    }
//
////    protected int setContentView() {
////        return R.layout.activity_square_game;
////    }
////
////    protected android.widget.RelativeLayout getTopLayout() {
////        return findViewById(R.id.squareGamePuzzleLayout);
////    }
//
//
//    protected abstract String getGamemodeString();
//    protected abstract int getLayoutId();
//    protected abstract RelativeLayout getTopLayout();
//    protected abstract void setup();
//    protected abstract boolean pieceCanBePlaced(SGPiece piece);
//
//    protected String getTag() {
//        return Constants.COMMON_TAG + this.getClass().getSimpleName();
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        super.setContentView(this.getLayoutId());
//
//        this.topLayout = this.getTopLayout();
//        this.setGameParameters();
//        this.setLimits();
//
//        this.setup();
//
//        this.topLayout.requestLayout();
//    }
//
//    protected void setGameParameters() {
//        this.initState = new SGState(this.getIntent(), this);
//        this.numVertical = this.initState.numVertical;
//        this.numHorizontal = this.initState.numHorizontal;
//        this.smallImageId = this.initState.smallImageId;
//        this.imageBitmap = this.initState.imageBitmap;
//        this.startTimeInMilliseconds = this.initState.startTimeInMilliseconds;
//
//        this.pieceMatrix = new SGPiece[this.numVertical][this.numHorizontal];
//        this.pieceWidth = this.getContainerWidth() / this.numHorizontal;
//        this.pieceHeight = this.getContainerHeight() / this.numVertical;
//    }
//
//    public void setLimits() {
//        this.minX = 0;
//        this.minY = 0;
//
//        this.maxX = this.getContainerWidth() - this.pieceWidth;
//        this.maxY = this.getContainerHeight() - this.pieceHeight;
//    }
//
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        freeMemory();
//
//        Log.i(this.getTag(), "onSaveInstanceState was called");
//
//        super.onSaveInstanceState(outState);
//    }
//
//    @Override
//    protected void onDestroy() {
//        freeMemory();
//
//        Log.i(this.getTag(), "onDestroy was called");
//
//        super.onDestroy();
//    }
//
//    protected void freeMemory() {
//        if (this.pieceMatrix != null) {
//            for (int i = 0; i < numVertical; ++i) {
//                for (int j = 0; j < numHorizontal; ++j) {
//                    if (this.pieceMatrix[i][j] != null) {
//                        this.pieceMatrix[i][j].originalBitmap.recycle();
//                    }
//                }
//            }
//        }
//
//        for (SGPiece piece : this.pieceList) {
//            if (piece != null && piece.originalBitmap != null) {
//                piece.originalBitmap.recycle();
//            }
//        }
//
//        if (this.imageBitmap != null) {
//            this.imageBitmap.recycle();
//        }
//    }
//
//
//
//    // methods for piece drag & drop and manipulation;
//    public void changePosition(View v, float x, float y) {
//        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) v.getLayoutParams();
//
//        int newX = (int)x - this.attachedOffsetX;
//        newX = Math.max(newX, this.minX);
//        newX = Math.min(newX, this.maxX);
//
//        int newY = (int)y - this.attachedOffsetY;
//        newY = Math.max(newY, this.minY);
//        newY = Math.min(newY, this.maxY);
//
//        params.leftMargin = newX;
//        params.topMargin = newY;
//        v.requestLayout();
//    }
//
//    protected boolean pointIsInsideView(View v, int x, int y) {
//        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) v.getLayoutParams();
//
//        if (!(params.leftMargin <= x && x < params.leftMargin + params.width)) {
//            return false;
//        }
//        if (!(params.topMargin <= y && y < params.topMargin + params.height)) {
//            return false;
//        }
//        return true;
//    }
//
//    protected void updateHistory() {
//        SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(this);
//        SharedPreferences.Editor editor = shared.edit();
//
//        Long endTimeInMilliseconds = Calendar.getInstance().getTimeInMillis();
//
//        PieceGameHistory item = new PieceGameHistory(
//                "SquareGame - " + this.getGamemodeString(),
//                this.startTimeInMilliseconds,
//                this.smallImageId,
//                endTimeInMilliseconds - this.startTimeInMilliseconds,
//                this.numHorizontal,
//                this.numVertical
//        );
//
//        String key = SquareGameBase.KEY_HISTORY_PIECEGAME;
//        String data = shared.getString(key, null);
//        data = HistoryItem.addInstanceToDataString(data, item);
//        editor.putString(key, data);
//        editor.commit();
//    }
//
//    protected void updateText() {
//        TextView textView = findViewById(R.id.TopGameMenu_Text);
//        String text;
//
//        ++this.numPlacedPieces;
//        if (this.numPlacedPieces == this.numHorizontal * this.numVertical) {
//            text = this.getResources().getString(R.string.TopGameMenu_WonText);
//            this.updateHistory();
//        }
//        else {
//            text = this.numPlacedPieces + " / " + this.numHorizontal * this.numVertical;
//        }
//
//        textView.setText(text);
//        textView.requestLayout();
//    }
//
//    protected void placePiece(SGPiece piece) {
//        int targetX = piece.targetj * this.pieceWidth;
//        int targetY = piece.targeti * this.pieceHeight;
//
//        this.updateText();
//        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) piece.imageView.getLayoutParams();
//        params.leftMargin = targetX;
//        params.topMargin = targetY;
//
//        this.pieceMatrix[piece.targeti][piece.targetj] = piece;
//
//        piece.update(this.pieceMatrix, true, this);
//        this.pieceList.removeFirst();
//
//        Iterator<SGPiece> it = this.pieceList.descendingIterator();
//        while (it.hasNext()) {
//            SGPiece currentPiece = it.next();
//            ImageView imageView = currentPiece.imageView;
//            imageView.bringToFront();
//        }
//
//        piece.imageView.requestLayout();
//    }
//
//    protected boolean pieceIsCloseEnough(SGPiece piece, int eventX, int eventY) {
//        int cornerX = eventX - this.attachedOffsetX;
//        cornerX = Math.max(cornerX, this.minX);
//        cornerX = Math.min(cornerX, this.maxX);
//
//        int cornerY = eventY - this.attachedOffsetY;
//        cornerY = Math.max(cornerY, this.minY);
//        cornerY = Math.min(cornerY, this.maxY);
//
//        int errorX = this.pieceWidth / 3;
//        int errorY = this.pieceHeight / 3;
//
//        int targetX = piece.targetj * this.pieceWidth;
//        int targetY = piece.targeti * this.pieceHeight;
//
//        if (Math.abs(targetX - cornerX) <= errorX && Math.abs(targetY - cornerY) <= errorY) {
//            return true;
//        }
//        return false;
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        int eventX = (int)event.getX();
//        int eventY = (int)event.getY() - this.getTopBarDimension();
//
//        if (eventY < 0) {
//            return true;
//        }
//
//        if (event.getAction() == MotionEvent.ACTION_DOWN) {
//
//            ListIterator<SGPiece> it = this.pieceList.listIterator();
//            while (it.hasNext()) {
//                SGPiece piece = it.next();
//
//                if (this.pointIsInsideView(piece.imageView, eventX, eventY)) {
//                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) piece.imageView.getLayoutParams();
//                    this.attachedOffsetX = eventX - params.leftMargin;
//                    this.attachedOffsetY = eventY - params.topMargin;
////                    Log.i(this.getTag(), "attachedOffsetX = " + this.attachedOffsetX);
////                    Log.i(this.getTag(), "attachedOffsetY = " + this.attachedOffsetY);
//
//                    it.remove();
//                    this.pieceList.addFirst(piece);
//
//                    this.hasAttached = true;
//                    piece.imageView.bringToFront();
//
//                    break;
//                }
//            }
//        }
//        else if (event.getAction() == MotionEvent.ACTION_UP) {
//            if (this.hasAttached == false) {
//                return true;
//            }
//
//            SGPiece piece = this.pieceList.getFirst();
//
//            Log.i(this.getTag(), "pieceCanBePlaced: " + this.pieceCanBePlaced(piece)); /////////////////////////////////////////////
//
//            if ( (this.pieceCanBePlaced(piece)) && this.pieceIsCloseEnough(piece, eventX, eventY) ) {
//                placePiece(piece);
//            }
//
//            this.hasAttached = false;
//        }
//        else if (event.getAction() == MotionEvent.ACTION_MOVE) {
//            if (this.hasAttached) {
//                this.changePosition(this.pieceList.getFirst().imageView, eventX, eventY);
//            }
//
////            Log.i(this.getTag(), "Movement at: " + Float.toString(eventX) + ", " + Float.toString(eventY));
//        }
//
//        return true;
//    }
//
//    public void backButtonPressed(View view) {
//        finish();
//    }
}
