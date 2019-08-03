package com.example.puzzle.squareGame;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.puzzle.Constants;
import com.example.puzzle.R;
import com.example.puzzle.Utils;
import com.example.puzzle.history.HistoryItem;
import com.example.puzzle.history.PieceGameHistory;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Random;

public class SGOnePiece extends AppCompatActivity {
    private final String TAG = Constants.COMMON_TAG + this.getClass().getSimpleName();

    protected int smallImageId = 0;
    protected int numHorizontal = 0, numVertical = 0;

    protected Long startTimeInMilliseconds;

    protected int pieceWidth = 0;
    protected int pieceHeight = 0;

    protected boolean hasAttached = false;
    protected int attachedOffsetX = 0, attachedOffsetY = 0;

    protected Bitmap imageBitmap = null;
    protected SGPiece[][] pieceMatrix = null;
    protected int numPlacedPieces = 0;

    protected RelativeLayout topLayout = null;
    protected LinkedList<SGPiece> pieceList = new LinkedList<>();
    protected SGState initState = null;

    public int getStatusBarHeight() {
        int statusBarHeight = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }

        return statusBarHeight;
    }

    public int getTopBarDimension() {
        int menuId = R.dimen.TopGameMenu_Size;
        int menuSize = (int)this.getResources().getDimension(menuId);
        return getStatusBarHeight() + menuSize;
    }

    public int getTotalScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public int getTotalScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public int getContainerWidth() {
        return this.getTotalScreenWidth();
    }

    public int getContainerHeight() {
        return this.getTotalScreenHeight() - this.getTopBarDimension();
    }

    protected String getGamemodeString() {
        return "One Piece";
    }

    protected int getLayoutId() {
        return R.layout.activity_square_game;
    }

    protected RelativeLayout getTopLayout() {
        return findViewById(R.id.squareGamePuzzleLayout);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(this.getLayoutId());

        this.topLayout = this.getTopLayout();
        this.setGameParameters();

        this.setup();

        this.topLayout.requestLayout();
    }

    protected void setGameParameters() {
        this.initState = new SGState(this, this.getIntent());
        this.numVertical = this.initState.numVertical;
        this.numHorizontal = this.initState.numHorizontal;
        this.smallImageId = this.initState.smallImageId;
        this.imageBitmap = Utils.scaleBitmapAndRecycle(this.initState.imageBitmap, this.getContainerWidth(), this.getContainerHeight());

        this.startTimeInMilliseconds = Calendar.getInstance().getTimeInMillis() - this.initState.duration;

        this.pieceMatrix = new SGPiece[this.numVertical][this.numHorizontal];
        this.pieceWidth = this.getContainerWidth() / this.numHorizontal;
        this.pieceHeight = this.getContainerHeight() / this.numVertical;
    }

    protected SGPiece makePiece(int pos) {

        SGPiece.Builder builder = new SGPiece.Builder(
                this,
                this.imageBitmap,
                this.numHorizontal,
                this.numVertical,
                this.pieceWidth,
                this.pieceHeight
        );
        builder.setPosition(pos);
        builder.setContainerDims(this.getContainerWidth(), this.getContainerHeight());
        double ratioX = this.initState.pieceContentRatioX[pos];
        double ratioY = this.initState.pieceContentRatioY[pos];
        builder.setContainerRatios(ratioX, ratioY);
        SGPiece piece = builder.build();

        return piece;
    }

    protected void setup() {

        for (int pos : this.initState.placedPieceIds) {
            SGPiece piece = this.makePiece(pos);

            this.pieceList.addFirst(piece);
            this.topLayout.addView(piece.imageView);
            this.placePiece(piece);
        }

        for (int pos : this.initState.freePieceIds) {
            SGPiece piece = this.makePiece(pos);
            this.pieceList.addFirst(piece);
            this.topLayout.addView(piece.imageView);

            double ratioX = this.initState.pieceContentRatioX[pos];
            double ratioY = this.initState.pieceContentRatioY[pos];
            int leftMargin = SGUtils.getMargin(this.getContainerWidth(), this.pieceWidth, ratioX);
            int topMargin = SGUtils.getMargin(this.getContainerHeight(), this.pieceHeight, ratioY);
            SGUtils.changeSGPiecePosition(piece, leftMargin, topMargin, this.attachedOffsetX, this.attachedOffsetY);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        freeMemory();

        Log.i(TAG, "onSaveInstanceState was called");

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        freeMemory();

        Log.i(TAG, "onDestroy was called");

        super.onDestroy();
    }

    protected void freeMemory() {
        if (this.pieceMatrix != null) {
            for (int i = 0; i < numVertical; ++i) {
                for (int j = 0; j < numHorizontal; ++j) {
                    if (this.pieceMatrix[i][j] != null) {
                        this.pieceMatrix[i][j].originalPieceImage.recycle();
                    }
                }
            }
        }

        for (SGPiece piece : this.pieceList) {
            if (piece != null && piece.originalPieceImage != null) {
                piece.originalPieceImage.recycle();
            }
        }

        if (this.imageBitmap != null) {
            this.imageBitmap.recycle();
        }
    }

    protected void updateHistory() {
        SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = shared.edit();

        Long endTimeInMilliseconds = Calendar.getInstance().getTimeInMillis();

        PieceGameHistory item = new PieceGameHistory(
                "SquareGame - " + this.getGamemodeString(),
                this.startTimeInMilliseconds,
                this.smallImageId,
                endTimeInMilliseconds - this.startTimeInMilliseconds,
                this.numHorizontal,
                this.numVertical
        );

        String key = Constants.KEY_HISTORY_PIECEGAME;
        String data = shared.getString(key, null);
        data = HistoryItem.addInstanceToDataString(data, item);
        editor.putString(key, data);
        editor.commit();
    }

    protected void updateText() {
        TextView textView = findViewById(R.id.TopGameMenu_Text);
        String text;

        ++this.numPlacedPieces;
        if (this.numPlacedPieces == this.numHorizontal * this.numVertical) {
            text = this.getResources().getString(R.string.TopGameMenu_WonText);
            this.updateHistory();
        }
        else {
            text = this.numPlacedPieces + " / " + this.numHorizontal * this.numVertical;
        }

        textView.setText(text);
        textView.requestLayout();
    }

    protected void placePiece(SGPiece piece) {
        int targetX = piece.targetj * this.pieceWidth;
        int targetY = piece.targeti * this.pieceHeight;

        this.updateText();
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) piece.imageView.getLayoutParams();
        params.leftMargin = targetX;
        params.topMargin = targetY;

        this.pieceMatrix[piece.targeti][piece.targetj] = piece;
        this.pieceList.removeFirst();

        piece.resetBorderColors();
        piece.update(this.pieceMatrix, true, this);

        Utils.bringPiecesToFrontAscending(this.pieceList);
        piece.imageView.requestLayout();
    }

    private void generateNewPiece() {
        if (this.numPlacedPieces == this.numHorizontal * this.numVertical) {
            return;
        }

        ArrayList<Integer> availablePositions = new ArrayList<>();
        for (int i = 0; i < this.numVertical; ++i) {
            for (int j = 0; j < this.numHorizontal; ++j) {
                if (this.pieceMatrix[i][j] != null) {
                    continue;
                }
                // we have an empty position;

                boolean hasNeighbour = false;
                for (int k = 0; k < Constants.di.length; ++k) {
                    int ni = i + Constants.di[k];
                    int nj = j + Constants.dj[k];

                    if ( !(0 <= ni && ni < this.numVertical && 0 <= nj && nj < this.numHorizontal) ) {
                        continue;
                    }

                    if (this.pieceMatrix[ni][nj] != null) {
                        hasNeighbour = true;
                        break;
                    }
                }

                if (hasNeighbour) {
                    int pos = i * this.numHorizontal + j;
                    availablePositions.add(pos);
                }
            }
        }

        Random random = new Random();
        int idx = random.nextInt(availablePositions.size());
        int pos = availablePositions.get(idx);

        int[] outerColor = new int[4], innerColor = new int[4];
        for (int k = 0; k < 4; ++k) {
            outerColor[k] = ContextCompat.getColor(this, R.color.outerOnePieceColor);
            innerColor[k] = ContextCompat.getColor(this, R.color.innerOnePieceColor);
        }

        SGPiece.Builder builder = new SGPiece.Builder(this, this.imageBitmap, this.numHorizontal, this.numVertical, this.pieceWidth, this.pieceHeight);
        builder.setPosition(pos);
        builder.setContainerDims(this.getContainerWidth(), this.getContainerHeight());
        builder.setBorderColors(outerColor, innerColor);
        builder.setContainerRatios(this.initState.pieceContentRatioX[pos], this.initState.pieceContentRatioY[pos]);
        SGPiece piece = builder.build();

        this.pieceList.add(piece);
        this.topLayout.addView(piece.imageView);

        piece.imageView.bringToFront();
        piece.imageView.requestLayout();
    }

    protected boolean pieceCanBePlaced(SGPiece piece) {
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int eventX = (int)event.getX();
        int eventY = (int)event.getY() - this.getTopBarDimension();

        if (eventY < 0) {
            return true;
        }

        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            ListIterator<SGPiece> it = this.pieceList.listIterator();
            while (it.hasNext()) {
                SGPiece piece = it.next();

                if (SGUtils.pointIsInsideView(piece.imageView, eventX, eventY)) {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) piece.imageView.getLayoutParams();
                    this.attachedOffsetX = eventX - params.leftMargin;
                    this.attachedOffsetY = eventY - params.topMargin;

                    it.remove();
                    this.pieceList.addFirst(piece);

                    this.hasAttached = true;
                    piece.imageView.bringToFront();

                    break;
                }
            }
        }
        else if (event.getAction() == MotionEvent.ACTION_UP) {
            if (this.hasAttached == false) {
                return true;
            }

            SGPiece piece = this.pieceList.getFirst();

            Log.i(this.TAG, "pieceCanBePlaced: " + this.pieceCanBePlaced(piece)); /////////////////////////////////////////////

            if ( (this.pieceCanBePlaced(piece)) && SGUtils.isSGPieceCloseEnough(piece, eventX, eventY, this.attachedOffsetX, this.attachedOffsetY) ) {
                placePiece(piece);
                this.generateNewPiece();
            }

            this.hasAttached = false;
        }
        else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (this.hasAttached) {
                SGUtils.changeSGPiecePosition(this.pieceList.getFirst(), eventX, eventY, this.attachedOffsetX, this.attachedOffsetY);
            }

//            Log.i(TAG, "Movement at: " + Float.toString(eventX) + ", " + Float.toString(eventY));
        }

        return true;
    }

    public void backButtonPressed(View view) {
        finish();
    }
}
