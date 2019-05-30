package com.example.puzzle;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.puzzle.history.HistoryItem;
import com.example.puzzle.history.PieceGameHistory;
import com.example.puzzle.jigsaw.JigsawPiece;
import com.example.puzzle.jigsaw.JigsawPieceGroup;
import com.example.puzzle.state.JigsawGameState;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

import static java.lang.Float.NaN;

public class ActivityJigsawGame extends AppCompatActivity {
    public static final String TAG = ActivityMain.COMMON_TAG + "_JigsawGame";
    public static final String HISTORY_PREFERENCE_KEY = "JigsawGameHistory";
    public static final String SAVED_STATE_KEY = "JigsawGameState";

    public final double initialMarginPercent = 0.05;
    public final int maxImageWidth = 750;
    public final int maxImageHeight = maxImageWidth;

    public RelativeLayout topLayout = null;
    public int initialHeightMargin = 0, initialWidthMargin = 0;
    public Bitmap imageBitmap = null;

    public Integer imageId = null;
    public int smallImageId = -1;
    public Uri userImageUri = null;
    public int numVertical = -1, numHorizontal = -1;
    public int numClusters = -1;

    public int nicheHeightDimension = 0;
    public final int nicheWidthMultiplier = 3;
    public final int zoomDimensionAddition = 10;
    int totalScreenWidth, totalScreenHeight;

    public JigsawPiece.NICHE_STATE[][] rightMargin, bottomMargin;
    public LinkedList<JigsawPieceGroup> pieceGroupList = new LinkedList<>();
    private JigsawPieceGroup touchedGroup = null;
    private float lastEventX = 0, lastEventY = 0;

    long startTimeInMilliseconds = 0;
    JigsawGameState savedState = null;
    int numZoom = 0;


    public int getNicheHeightDimension() {
        return this.nicheHeightDimension;
    }

    public int getNicheWidthDimension() {
        return this.nicheHeightDimension * this.nicheWidthMultiplier;
    }

    public int getInitialNicheHeightDimension() {
        int wantedPieceHeight = this.getTotalScreenHeight() / this.numVertical;
        int wantedPieceWidth = this.getTotalScreenWidth() / this.numHorizontal;

        int pieceDimensionAproximate = Math.min(wantedPieceHeight, wantedPieceWidth);
        int initialNicheHeightDimension = pieceDimensionAproximate / this.nicheWidthMultiplier;
        return initialNicheHeightDimension;
    }

    public int getPieceDimension() {
        return this.getNicheWidthDimension();
    }

    public int getPiecePositionError() {
        return Math.max(30, this.getPieceDimension() / 4);
    }


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
        return menuSize + this.getStatusBarHeight();
    }

    public int getBotBarDimension() {
        int menuId = R.dimen.BotGameMenu_Size;
        int menuSize = (int)this.getResources().getDimension(menuId);
        return menuSize;
    }

    // THESE VALUES CHANGE TO THE NEW ORIENTATION WHEN IN onSaveInstanceState SO I NEED TO SAVE THEM LOCALLY;
    public int getCurrentTotalScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }
    public int getCurrentTotalScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public int getTotalScreenWidth() {
        return this.totalScreenWidth;
    }

    public int getTotalScreenHeight() {
        return this.totalScreenHeight;
    }

    public int getContainerWidth() {
        return getTotalScreenWidth();
    }

    public int getContainerHeight() {
        return getTotalScreenHeight() - this.getTopBarDimension() - this.getBotBarDimension();
    }

    public int getMinimumPieceDimension() {
        return 50;
    }

    public int getMaximumPieceDimension() {
        double dim = 0.75 * Math.min(this.getContainerHeight(), this.getContainerWidth());
        return (int) dim;
    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(ActivityJigsawGame.TAG, "savedInstanceState is null: " + (savedInstanceState == null));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jigsaw_game);

        if (this.isRestored(savedInstanceState)) {
            this.setupGameFromState(savedInstanceState);
        }
        else {
            this.setupNewGame();
        }
    }

    protected boolean isRestored(Bundle savedInstanceState) {
        return savedInstanceState != null;
    }

    protected void setupNewGame() {
        this.setGameParametersFromIntent();
        int actualNicheHeightDimension = this.getNicheHeightDimension();
        this.nicheHeightDimension = this.getInitialNicheHeightDimension();


        // get the pieces using the initialNicheHeightDimension, which should be bigger
        // and will make the pieces clearer when zooming and rotating the orientation;
        this.getScaledImage();
        JigsawPiece.buildNiches(this.getNicheHeightDimension(), this.getNicheWidthDimension());
        this.generateBorderMatrices();

        List<JigsawPiece> pieceList = this.generatePieces();

        for (JigsawPiece piece : pieceList) {
            JigsawPieceGroup group = new JigsawPieceGroup(this, piece);
            this.pieceGroupList.addFirst(group);
        }

        this.resizePieces(actualNicheHeightDimension);
        this.randomlyPositionPieces();

        this.updateText();
        this.topLayout.requestLayout();
    }

    protected void setupGameFromState(Bundle savedInstanceState) {
        this.savedState = (JigsawGameState) savedInstanceState.getSerializable(ActivityJigsawGame.SAVED_STATE_KEY);
        this.setGameParametersFromState();
        this.nicheHeightDimension = this.getInitialNicheHeightDimension();


        // get the pieces using the initialNicheHeightDimension, which should be bigger
        // and will make the pieces clearer when zooming and rotating the orientation;
        this.getScaledImage();
        JigsawPiece.buildNiches(this.getNicheHeightDimension(), this.getNicheWidthDimension());
        this.rightMargin = this.savedState.rightMargin;
        this.bottomMargin = this.savedState.bottomMargin;

        List<JigsawPiece> pieceList = this.generatePieces();
        for (int groupIndex = 0; groupIndex < this.savedState.groupPieceList.size(); ++groupIndex) {
            JigsawPieceGroup group = new JigsawPieceGroup(this);

            for (int pieceIndex : this.savedState.groupPieceList.get(groupIndex)) {
                group.addPiece( pieceList.get(pieceIndex) );
            }

            this.pieceGroupList.add(group);
        }

        int newNicheHeightDimension = (int) (this.savedState.nicheHeightToScreenRatio * this.getTotalScreenHeight());
        this.resizePieces(newNicheHeightDimension);

        for (int groupIndex = this.savedState.groupPieceList.size() - 1; groupIndex >= 0; --groupIndex) {
            JigsawPieceGroup group = this.pieceGroupList.get(groupIndex);

            float containerCenterX = (float)this.getContainerWidth() / 2;
            float containerCenterY = (float)this.getContainerHeight() / 2;
            double xRatio = this.savedState.groupTranslationRatioX[groupIndex];
            double yRatio = this.savedState.groupTranslationRatioY[groupIndex];

            double fixedReferenceDimension = Math.min(this.getContainerHeight(), this.getContainerWidth()) * this.getPieceDimension();
            double xDiff = xRatio * fixedReferenceDimension;
            double yDiff = yRatio * fixedReferenceDimension;

            float translationX = (float) (containerCenterX - xDiff);
            float translationY = (float) (containerCenterY - yDiff);
            group.setTranslation(translationX, translationY);
        }


        this.numClusters = this.pieceGroupList.size();
        this.updateText();
        this.topLayout.requestLayout();
    }

    void setGameParametersFromIntent() {
        this.topLayout = findViewById(R.id.jigsawGamePuzzleLayout);

        Bundle bundle = this.getIntent().getExtras();
        this.numVertical = (Integer)bundle.get("columnNumber");
        this.numHorizontal = (Integer)bundle.get("rowNumber");
        this.smallImageId = (Integer)bundle.get("smallImageSelected");
        if (bundle.get("imageSelected") == null) {
            this.userImageUri = Uri.parse( (String)bundle.get("userImageUri") );

            try {
                this.imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), this.userImageUri);
            }
            catch (Exception except) {
                Log.e(TAG, "There was an error loading the userBitmap");
                finish();
            }
        }
        else {
            this.imageId = (Integer)bundle.get("imageSelected");
            this.imageBitmap = BitmapFactory.decodeResource(this.getResources(), this.imageId);
        }

        this.numClusters = this.numHorizontal * this.numVertical;
        this.setGameParametersCommon();
    }

    void setGameParametersFromState() {
        this.topLayout = findViewById(R.id.jigsawGamePuzzleLayout);

        this.imageId = this.savedState.imageId;
        this.smallImageId = this.savedState.smallImageId;
        this.numVertical = this.savedState.numVertical;
        this.numHorizontal = this.savedState.numHorizontal;
        this.numClusters = this.numHorizontal * this.numVertical;
        this.startTimeInMilliseconds = Calendar.getInstance().getTimeInMillis() - this.savedState.durationInMiliseconds;
        this.imageBitmap = this.savedState.imageBitmap;

        this.setGameParametersCommon();
    }

    void setGameParametersCommon() {
        this.totalScreenHeight = this.getCurrentTotalScreenHeight();
        this.totalScreenWidth = this.getCurrentTotalScreenWidth();

        this.initialHeightMargin = (int)(this.getContainerHeight() * initialMarginPercent);
        this.initialWidthMargin = (int)(this.getContainerWidth() * initialMarginPercent);

        int wantedPieceHeight = (this.getContainerHeight() - 2 * this.initialHeightMargin) / this.numVertical;
        int wantedPieceWidth = (this.getContainerWidth() - 2 * this.initialWidthMargin) / this.numHorizontal;

        int pieceDimensionAproximate = Math.min(wantedPieceHeight, wantedPieceWidth);
        this.nicheHeightDimension = pieceDimensionAproximate / this.nicheWidthMultiplier;

        Log.i(ActivityJigsawGame.TAG, "nicheWidthDimension = " + this.getNicheWidthDimension());
        Log.i(ActivityJigsawGame.TAG, "piecePositionError = " + this.getPiecePositionError());
        Log.i(ActivityJigsawGame.TAG, ActivityMain.SEPARATOR);


        this.startTimeInMilliseconds = Calendar.getInstance().getTimeInMillis();
    }

    void getScaledImage() {
        int newImageWidth = this.numHorizontal * this.getPieceDimension();
        int newImageHeight = this.numVertical * this.getPieceDimension();

        Bitmap scaledImage = Bitmap.createScaledBitmap(this.imageBitmap, newImageWidth, newImageHeight, true);
        if (scaledImage != this.imageBitmap) {
            this.imageBitmap.recycle();
        }
        this.imageBitmap = scaledImage;

        Log.i(ActivityJigsawGame.TAG, "The newImage width is " + newImageWidth);
        Log.i(ActivityJigsawGame.TAG, "The newImage height is " + newImageHeight);
        Log.i(ActivityJigsawGame.TAG, "The pieceDimension is " + this.getPieceDimension());
    }

    void generateBorderMatrices() {
        this.rightMargin = new JigsawPiece.NICHE_STATE[this.numVertical][this.numHorizontal];
        this.bottomMargin = new JigsawPiece.NICHE_STATE[this.numVertical][this.numHorizontal];

        for (int i = 0; i < this.numVertical; ++i) {
            for (int j = 0; j < this.numHorizontal; ++j) {
                double r;

                r = Math.random();
                if (r < 0.5) {
                    rightMargin[i][j] = JigsawPiece.NICHE_STATE.OUTER;
                }
                else {
                    rightMargin[i][j] = JigsawPiece.NICHE_STATE.INNER;
                }

                r = Math.random();
                if (r < 0.5) {
                    bottomMargin[i][j] = JigsawPiece.NICHE_STATE.OUTER;
                }
                else {
                    bottomMargin[i][j] = JigsawPiece.NICHE_STATE.INNER;
                }
            }
        }

        for (int i = 0; i < this.numVertical; ++i) {
            rightMargin[i][this.numHorizontal - 1] = JigsawPiece.NICHE_STATE.NONE;
        }

        for (int j = 0; j < this.numHorizontal; ++j) {
            bottomMargin[this.numVertical - 1][j] = JigsawPiece.NICHE_STATE.NONE;
        }
    }

    List<JigsawPiece> generatePieces() {
        LinkedList<JigsawPiece> list = new LinkedList<>();
        for (int i = 0; i < this.numVertical; ++i) {
            for (int j = 0; j < this.numHorizontal; ++j) {
                JigsawPiece piece = JigsawPiece.getPiece(i, j, this);
                this.topLayout.addView(piece.getView());
                list.add(piece);
            }
        }

        Iterator<JigsawPiece> it = list.descendingIterator();
        while (it.hasNext()) {
            JigsawPiece piece = it.next();
            piece.getView().bringToFront();
        }

        return list;
    }

    void randomlyPositionPieces() {
        int rectLeft = initialWidthMargin, rectTop = initialHeightMargin;
        int rectRight = this.getContainerWidth() - initialWidthMargin;
        int rectBottom = this.getContainerHeight() - initialHeightMargin;
        Rect centerRect = new Rect(rectLeft, rectTop, rectRight, rectBottom);

        Iterator<JigsawPieceGroup> it = this.pieceGroupList.descendingIterator();
        while (it.hasNext()) {
            JigsawPieceGroup group = it.next();

            JigsawPiece piece = group.getPieceList().getFirst();
            piece.setRandomPosition(centerRect);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        JigsawGameState state = new JigsawGameState(this.pieceGroupList.size());
        state.imageId = this.imageId;
        state.smallImageId = this.smallImageId;
        state.numHorizontal = this.numHorizontal;
        state.numVertical = this.numVertical;
        state.imageBitmap = this.imageBitmap;
        state.durationInMiliseconds = Calendar.getInstance().getTimeInMillis() - this.startTimeInMilliseconds;
        state.nicheHeightToScreenRatio = (double) this.getNicheHeightDimension() / (double) this.getTotalScreenHeight();

        for (int i = 0; i < this.pieceGroupList.size(); ++i) {
            JigsawPieceGroup group = this.pieceGroupList.get(i);
            for (int j = 0; j < group.getPieceList().size(); ++j) {
                JigsawPiece piece = group.getPieceList().get(j);
                int pieceIndex = ActivityJigsawGame.BidimIndexToOnedimIndex(piece.getI(), piece.getJ(), this.numHorizontal);
                state.addPieceToGroup(i, pieceIndex);
            }


            float containerCenterX = (float)this.getContainerWidth() / 2;
            float containerCenterY = (float)this.getContainerHeight() / 2;
            float xDiff = containerCenterX - group.getTranslationX();
            float yDiff = containerCenterY - group.getTranslationY();

            double fixedReferenceDimension = Math.min(this.getContainerHeight(), this.getContainerWidth()) * this.getPieceDimension();
            double xRatio = xDiff / fixedReferenceDimension;
            double yRatio = yDiff / fixedReferenceDimension;

            state.setGroupTranslationRatio(i, xRatio, yRatio);
        }

        state.rightMargin = this.rightMargin;
        state.bottomMargin = this.bottomMargin;


//        Log.i(ActivityJigsawGame.TAG, ActivityMain.SEPARATOR);
//        Log.i(ActivityJigsawGame.TAG, "IN SAVEINSTACESTATE:");
//        Log.i(ActivityJigsawGame.TAG, "screenHeight = " + this.getTotalScreenHeight());
//        Log.i(ActivityJigsawGame.TAG, "screenWidth = " + this.getTotalScreenWidth());
//        Log.i(ActivityJigsawGame.TAG, "containerWidth = " + this.getContainerWidth());
//        Log.i(ActivityJigsawGame.TAG, "containerHeight = " + this.getContainerHeight());
//        Log.i(ActivityJigsawGame.TAG, "saved ratioX = " + Arrays.toString(state.groupTranslationRatioX));
//        Log.i(ActivityJigsawGame.TAG, "saved ratioY = " + Arrays.toString(state.groupTranslationRatioY));
//        Log.i(ActivityJigsawGame.TAG, ActivityMain.SEPARATOR);



        outState.putSerializable(ActivityJigsawGame.SAVED_STATE_KEY, state);
        super.onSaveInstanceState(outState);
    }

    public void backButtonPressed(View view) {
        finish();
    }




    private void updateHistory() {
        SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = shared.edit();

        Long endTimeInMilliseconds = Calendar.getInstance().getTimeInMillis();

        PieceGameHistory item = new PieceGameHistory(
                "Jigsaw",
                this.startTimeInMilliseconds,
                this.smallImageId,
                endTimeInMilliseconds - this.startTimeInMilliseconds,
                this.numHorizontal,
                this.numVertical
        );

        String key = ActivityJigsawGame.HISTORY_PREFERENCE_KEY;
        String data = shared.getString(key, null);
        data = HistoryItem.addInstanceToDataString(data, item);
        editor.putString(key, data);
        editor.commit();
    }

    private void updateText() {
        TextView textView = findViewById(R.id.TopGameMenu_Text);
        String text;

        if (this.numClusters == 1) {
            text = this.getResources().getString(R.string.TopGameMenu_WonText);
            this.updateHistory();
        }
        else {
            text = this.numClusters + " clusters";
        }

        textView.setText(text);
        textView.requestLayout();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = this.dealWithMotionEvent(event);
        this.topLayout.requestLayout();

        if (event.getAction() == MotionEvent.ACTION_UP) {
            this.lastEventX = this.lastEventY = NaN;
        }
        else {
            int eventX = (int)event.getX();
            int eventY = (int)event.getY() - this.getTopBarDimension();
            this.lastEventX = eventX;
            this.lastEventY = eventY;
        }

        return result;
    }

    public boolean dealWithMotionEvent(MotionEvent event) {
        int eventX = (int)event.getX();
        int eventY = (int)event.getY() - this.getTopBarDimension();

        if (eventY < 0) {
            return true;
        }

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            ListIterator<JigsawPieceGroup> it = this.pieceGroupList.listIterator();

            while (it.hasNext()) {
                JigsawPieceGroup group = it.next();

                boolean touching = false;
                for (JigsawPiece piece : group.getPieceList()) {
                    if ( piece.isTouchedBy(eventX, eventY) ) {
                        touching = true;
                        break;
                    }
                }

                if (touching) {
                    touchedGroup = group;
                    it.remove();
                    break;
                }
            }
        }
        else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (this.touchedGroup != null) {
                if (Float.isNaN(this.lastEventX) || Float.isNaN(this.lastEventY)) {
                    return true;
                }
                else {
                    float xDiff = eventX - this.lastEventX;
                    float yDiff = eventY - this.lastEventY;

                    this.touchedGroup.setTranslationByDifference(xDiff, yDiff);
                }

            }
            else {
                float xDiff = eventX - this.lastEventX;
                float yDiff = eventY - this.lastEventY;

                Iterator<JigsawPieceGroup> it = this.pieceGroupList.descendingIterator();
                while (it.hasNext()) {
                    JigsawPieceGroup group = it.next();
                    group.setTranslationByDifference(xDiff, yDiff);
                }
            }
        }
        else if (event.getAction() == MotionEvent.ACTION_UP) {
            if (this.touchedGroup == null) {
                return true;
            }

            float xDiff = eventX - this.lastEventX;
            float yDiff = eventY - this.lastEventY;
            this.touchedGroup.setTranslationByDifference(xDiff, yDiff);

            List<JigsawPieceGroup> groupsToAdd = new LinkedList<JigsawPieceGroup>();
            ListIterator<JigsawPieceGroup> it = this.pieceGroupList.listIterator();
            while (it.hasNext()) {
                JigsawPieceGroup otherGroup = it.next();

                if (this.touchedGroup.intersect(otherGroup)) {
                    --this.numClusters;
                    groupsToAdd.add(otherGroup);
                    it.remove();
                }
            }
            this.touchedGroup.addGroups(groupsToAdd);
            if (groupsToAdd.size() != 0) {
                this.updateText();
            }

            this.pieceGroupList.addFirst(this.touchedGroup);
            this.touchedGroup = null;
        }

        return true;
    }


    // zooming functions

    public void resizePieces(int newNicheHeightDimension) {
        int oldPieceDimension = this.getPieceDimension();
        this.nicheHeightDimension = newNicheHeightDimension;

        Iterator<JigsawPieceGroup> it = this.pieceGroupList.descendingIterator();
        while (it.hasNext()) {
            JigsawPieceGroup group = it.next();

            for (JigsawPiece piece : group.getPieceList()) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) piece.getView().getLayoutParams();
                params.width = piece.getTotalWidth();
                params.height = piece.getTotalHeight();
            }

            float oldGroupTranslationX = group.getTranslationX();
            float oldGroupTranslationY = group.getTranslationY();

            float containerCenterX = (float)this.getContainerWidth() / 2;
            float containerCenterY = (float)this.getContainerHeight() / 2;
            float xDiff = containerCenterX - oldGroupTranslationX;
            float yDiff = containerCenterY - oldGroupTranslationY;
            float redimensionPercentage = this.getPieceDimension() / (float) oldPieceDimension;
            xDiff *= redimensionPercentage;
            yDiff *= redimensionPercentage;

            group.setTranslation(containerCenterX - xDiff, containerCenterY - yDiff);
        }

        this.topLayout.requestLayout();
    }


    public void zoomIn(View view) {
        final int newNicheHeightDimension = this.getNicheHeightDimension() + this.zoomDimensionAddition;
        final int newNicheWidthDimension = newNicheHeightDimension * this.nicheWidthMultiplier;
        final int newPieceDimension = newNicheWidthDimension;
        final int bigPieceDimension = newPieceDimension + 2 * newNicheHeightDimension;

        Log.i(ActivityJigsawGame.TAG, "trying to resize to newNicheHeightDimension: " + newNicheHeightDimension);
        Log.i(ActivityJigsawGame.TAG, "trying to resize to newNicheWidthDimension: " + newNicheWidthDimension);
        Log.i(ActivityJigsawGame.TAG, "trying to resize to newPieceDimension: " + newPieceDimension);
        Log.i(ActivityJigsawGame.TAG, "trying to resize to bigPieceDimension: " + bigPieceDimension);
        Log.i(ActivityJigsawGame.TAG, ActivityMain.SEPARATOR);

        if (bigPieceDimension <= this.getMaximumPieceDimension()) {
            this.resizePieces(newNicheHeightDimension);
        }
    }

    public void zoomOut(View view) {
        final int newNicheHeightDimension = this.getNicheHeightDimension() - this.zoomDimensionAddition;
        final int newNicheWidthDimension = newNicheHeightDimension * this.nicheWidthMultiplier;
        final int newPieceDimension = newNicheWidthDimension;
        final int bigPieceDimension = newPieceDimension + 2 * newNicheHeightDimension;

        Log.i(ActivityJigsawGame.TAG, "trying to resize to newNicheHeightDimension: " + newNicheHeightDimension);
        Log.i(ActivityJigsawGame.TAG, "trying to resize to newNicheWidthDimension: " + newNicheWidthDimension);
        Log.i(ActivityJigsawGame.TAG, "trying to resize to newPieceDimension: " + newPieceDimension);
        Log.i(ActivityJigsawGame.TAG, "trying to resize to bigPieceDimension: " + bigPieceDimension);
        Log.i(ActivityJigsawGame.TAG, ActivityMain.SEPARATOR);

        if (this.getMinimumPieceDimension() <= bigPieceDimension) {
            this.resizePieces(newNicheHeightDimension);
        }
    }





    // helper functions

    public static int BidimIndexToOnedimIndex(int i, int j, int columns) {
        return i * columns + j;
    }

    public static int getRandomInt(int lower, int upper) {
        Random random = new Random();
        int ret = random.nextInt(upper - lower + 1) + lower;
        return ret;
    }



    public boolean positionsAreCloseEnough(float a, float b) {
        if (a - this.getPiecePositionError() <= b && b <= a + this.getPiecePositionError()) {
            return true;
        }

        return false;
    }

    public static boolean intersects(int a, int b, int x, int y) {
        return Math.max(a, x) <= Math.min(b, y);
    }

    public static boolean intersects(Rect a, Rect b) {
        boolean result = true;
        result = result && intersects(a.left, a.right, b.left, b.right);
        result = result && intersects(a.top, a.bottom, b.top, b.bottom);

        return result;
    }



    public static void setImageBackground(ImageView image, Bitmap background, Context context) {
        int sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
//            image.setBackgroundDrawable(new BitmapDrawable(background));
            image.setImageDrawable(new BitmapDrawable(background));
        }
        else {
//            image.setBackground(new BitmapDrawable(getResources(), background));
            image.setImageDrawable(new BitmapDrawable(context.getResources(), background));
        }
    }

    public void printScaledBitmap(Bitmap bitmap, int translationX, int translationY, double scale) {
        RelativeLayout layout = findViewById(R.id.jigsawGamePuzzleLayout);

        ImageView image = new ImageView(this);
        image.setAdjustViewBounds(true);
        int width = (int)(bitmap.getWidth() * scale);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, RelativeLayout.LayoutParams.WRAP_CONTENT);
        image.setLayoutParams(params);

        setImageBackground(image, bitmap, this);
        image.setTranslationX(translationX);
        image.setTranslationY(translationY);

        layout.addView(image);
        layout.requestLayout();
    }
}
