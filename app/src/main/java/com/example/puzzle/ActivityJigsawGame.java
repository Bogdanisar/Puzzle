package com.example.puzzle;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.preference.PreferenceManager;
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

import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

import static java.lang.Float.NaN;

public class ActivityJigsawGame extends AppCompatActivity {
    public static final String TAG = ActivityMain.COMMON_TAG + "_JigsawGame";
    public static final String HISTORY_PREFERENCE_KEY = "JigsawGameHistory";

    public final double initialMarginPercent = 0.05;
    public final int maxImageWidth = 1000, maxImageHeight = 1000;

    public RelativeLayout topLayout = null;
    public int initialHeightMargin = 0, initialWidthMargin = 0;
    public Bitmap imageBitmap = null;

    public int imageId = -1, smallImageId = -1;
    public int numVertical = -1, numHorizontal = -1;
    public int numClusters = -1;

    public int nicheHeightDimension = 0;
    public final int nicheWidthMultiplier = 3;
    public final int zoomDimensionAddition = 10;
    public final int minimumPieceDimension = 50;

    public JigsawPiece.NICHE_STATE[][] rightMargin, bottomMargin;
    public LinkedList<JigsawPieceGroup> pieceGroupList = new LinkedList<>();
    private JigsawPieceGroup touchedGroup = null;
    private float lastEventX = 0, lastEventY = 0;

    long startTimeInMilliseconds = 0;


    public int getNicheHeightDimension() {
        return this.nicheHeightDimension;
    }

    public int getNicheWidthDimension() {
        return this.nicheHeightDimension * this.nicheWidthMultiplier;
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
        return getStatusBarHeight() + menuSize;
    }

    public int getBotBarDimension() {
        int menuId = R.dimen.BotGameMenu_Size;
        int menuSize = (int)this.getResources().getDimension(menuId);
        return menuSize;
    }

    public static int getTotalScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getTotalScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public int getContainerWidth() {
        return getTotalScreenWidth();
    }

    public int getContainerHeight() {
        return getTotalScreenHeight() - this.getTopBarDimension() - this.getBotBarDimension();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jigsaw_game);

        this.setGameParameters();
        this.imageBitmap = this.getScaledImage(this.imageId);

        JigsawPiece.buildNiches(this.getNicheHeightDimension(), this.getNicheWidthDimension());
        this.generateBorderMatrices();
        this.generatePieces();

        this.updateText();

        Log.i(ActivityJigsawGame.TAG, "containerWidth =" + this.getContainerWidth());
        Log.i(ActivityJigsawGame.TAG, "containerHeight =" + this.getContainerHeight());

        this.topLayout.requestLayout();
    }

    void setGameParameters() {
        this.topLayout = findViewById(R.id.jigsawGamePuzzleLayout);
        Bundle bundle = getIntent().getExtras();

        this.imageId = (Integer)bundle.get("imageSelected");
        this.smallImageId = (Integer)bundle.get("smallImageSelected");
        this.numVertical = (Integer)bundle.get("rowNumber");
        this.numHorizontal = (Integer)bundle.get("columnNumber");
        this.numClusters = this.numHorizontal * this.numVertical;


        this.initialHeightMargin = (int)(this.getContainerHeight() * initialMarginPercent);
        this.initialWidthMargin = (int)(this.getContainerWidth() * initialMarginPercent);

        int wantedPieceHeight = (this.getContainerHeight() - 2 * this.initialHeightMargin) / this.numVertical;
        int wantedPieceWidth = (this.getContainerWidth() - 2 * this.initialWidthMargin) / this.numHorizontal;

        int pieceWidthAproximate = Math.min(wantedPieceHeight, wantedPieceWidth);
        this.nicheHeightDimension = pieceWidthAproximate / this.nicheWidthMultiplier;

        Log.i(ActivityJigsawGame.TAG, "nicheWidthDimension = " + this.getNicheWidthDimension());
        Log.i(ActivityJigsawGame.TAG, "piecePositionError = " + this.getPiecePositionError());
        Log.i(ActivityJigsawGame.TAG, ActivityMain.SEPARATOR);


        this.startTimeInMilliseconds = Calendar.getInstance().getTimeInMillis();
    }

    Bitmap getScaledImage(int imageId) {
        Bitmap imageBitmap = BitmapFactory.decodeResource(this.getResources(), imageId);
        int newImageWidth = Math.min(maxImageWidth, imageBitmap.getWidth());
        int newImageHeight = Math.min(maxImageHeight, imageBitmap.getHeight());

        newImageWidth = newImageWidth / numHorizontal * numHorizontal;
        newImageHeight = newImageHeight / numVertical * numVertical;

        Bitmap scaledImage = Bitmap.createScaledBitmap(imageBitmap, newImageWidth, newImageHeight, true);
        if (scaledImage != imageBitmap) {
            imageBitmap.recycle();
        }

        return scaledImage;
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

    void generatePieces() {
        int rectLeft = initialWidthMargin, rectTop = initialHeightMargin;
        int rectRight = this.getContainerWidth() - initialWidthMargin;
        int rectBottom = this.getContainerHeight() - initialHeightMargin;
        Rect centerRect = new Rect(rectLeft, rectTop, rectRight, rectBottom);

        for (int i = 0; i < this.numVertical; ++i) {
            for (int j = 0; j < this.numHorizontal; ++j) {

                JigsawPiece piece = JigsawPiece.getPiece(i, j, centerRect, this);
                JigsawPieceGroup group = new JigsawPieceGroup(this, piece);

                this.pieceGroupList.addFirst(group);
                this.topLayout.addView(piece.getView());
                piece.getView().bringToFront();
            }
        }
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


    // ZOOMING IS NOT FUNCTIONAL

    public void resizePieces(int oldPieceDimension) {
        Iterator<JigsawPieceGroup> it = this.pieceGroupList.descendingIterator();
        while (it.hasNext()) {
            JigsawPieceGroup group = it.next();

            float oldGroupCenterX = group.getCenterTranslationX();
            float oldGroupCenterY = group.getCenterTranslationY();

            for (JigsawPiece piece : group.getPieceList()) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) piece.getView().getLayoutParams();
                params.width = piece.getTotalWidth();
                params.height = piece.getTotalHeight();
            }

            float containerCenterX = this.getContainerWidth() / 2;
            float containerCenterY = this.getContainerHeight() / 2;
            float xDiff = containerCenterX - oldGroupCenterX;
            float yDiff = containerCenterY - oldGroupCenterY;
            float redimensionPercentage = this.getPieceDimension() / (float) oldPieceDimension;
            xDiff *= redimensionPercentage;
            yDiff *= redimensionPercentage;

            group.setTranslationByCenter(containerCenterX - xDiff, containerCenterY - yDiff);
        }

        this.topLayout.requestLayout();
    }

    public boolean pieceDimensionIsOk(final int newNicheHeightDimension) {
        final int newNicheWidthDimension = newNicheHeightDimension * this.nicheWidthMultiplier;
        final int newPieceDimension = newNicheWidthDimension;
        final int bigPieceDimension = newPieceDimension + 2 * newNicheHeightDimension;

        Log.i(ActivityJigsawGame.TAG, "trying to resize to newNicheHeightDimension: " + newNicheHeightDimension);
        Log.i(ActivityJigsawGame.TAG, "trying to resize to newNicheWidthDimension: " + newNicheWidthDimension);
        Log.i(ActivityJigsawGame.TAG, "trying to resize to newPieceDimension: " + newPieceDimension);
        Log.i(ActivityJigsawGame.TAG, "trying to resize to bigPieceDimension: " + bigPieceDimension);
        Log.i(ActivityJigsawGame.TAG, ActivityMain.SEPARATOR);

        return this.minimumPieceDimension <= bigPieceDimension && bigPieceDimension <= 0.75 * Math.min(this.getContainerHeight(), this.getContainerWidth());
    }

    public void zoomTo(int newNicheHeightDimension) {
        if (this.pieceDimensionIsOk(newNicheHeightDimension)) {
            int oldPieceDimension = this.getPieceDimension();
            this.nicheHeightDimension = newNicheHeightDimension;
            this.resizePieces(oldPieceDimension);
        }

        Log.i(ActivityJigsawGame.TAG, "this.nicheHeightDimension = " + this.nicheHeightDimension);
    }

    public void zoomIn(View view) {
        this.zoomTo(this.getNicheHeightDimension() + this.zoomDimensionAddition);
    }

    public void zoomOut(View view) {
        this.zoomTo(this.getNicheHeightDimension() - this.zoomDimensionAddition);
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

    private void printScaledBitmap(Bitmap bitmap, int translationX, int translationY, double scale) {
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
