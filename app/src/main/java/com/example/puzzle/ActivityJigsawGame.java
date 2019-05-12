package com.example.puzzle;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.puzzle.jigsaw.JigsawPiece;
import com.example.puzzle.jigsaw.JigsawPieceGroup;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.Random;

public class ActivityJigsawGame extends AppCompatActivity {
    public static final String TAG = ActivityMain.COMMON_TAG + "_JigsawGame";
    public static final double initialMarginPercent = 0.05;
    static final int maxImageWidth = 1000, maxImageHeight = 1000;

    public RelativeLayout topLayout = null;
    public int initialHeightMargin = 0, initialWidthMargin = 0;

    public int imageId = -1, smallImageId = -1;
    public int numVertical = -1, numHorizontal = -1;
    public int pieceDimension;
    public Bitmap imageBitmap = null;

    public JigsawPiece.NICHE_STATE[][] rightMargin, bottomMargin;
    LinkedList<JigsawPieceGroup> pieceGroupList = new LinkedList<>();

    long startTimeInMilliseconds = 0;


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
//        this.printScaledBitmap(this.imageBitmap, 0, 0, 0.5); ///////////////////////////////////////

        JigsawPiece.buildNiches(this.pieceDimension);
//        printScaledBitmap(JigsawPiece.outerNiches[JigsawPiece.TOP], 0, 0, 1.0); //////////////////////

        this.generateBorderMatrices();
        this.generatePieces();

//        JigsawPiece piece = JigsawPiece.getPiece(0, 0, centerRect, this);
//        this.topLayout.addView(piece.getView());



        this.topLayout.requestLayout();
    }

    void setGameParameters() {
        this.topLayout = findViewById(R.id.jigsawGamePuzzleLayout);
        Bundle bundle = getIntent().getExtras();

        this.imageId = (Integer)bundle.get("imageSelected");
        this.smallImageId = (Integer)bundle.get("smallImageSelected");
        this.numVertical = (Integer)bundle.get("rowNumber");
        this.numHorizontal = (Integer)bundle.get("columnNumber");


        this.initialHeightMargin = (int)(this.getContainerHeight() * initialMarginPercent);
        this.initialWidthMargin = (int)(this.getContainerWidth() * initialMarginPercent);

        int wantedPieceHeight = (this.getContainerHeight() - 2 * this.initialHeightMargin) / this.numVertical;
        int wantedPieceWidth = (this.getContainerWidth() - 2 * this.initialWidthMargin) / this.numHorizontal;
        this.pieceDimension = Math.min(wantedPieceHeight, wantedPieceWidth);


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
                JigsawPieceGroup group = new JigsawPieceGroup(piece);

                this.pieceGroupList.addFirst(group);
                this.topLayout.addView(piece.getView());
                piece.getView().bringToFront();
            }
        }
    }






    public static int BidimIndexToOnedimIndex(int i, int j, int columns) {
        return i * columns + j;
    }

    public static int getRandomInt(int lower, int upper) {
        Random random = new Random();
        int ret = random.nextInt(upper - lower + 1) + lower;
        return ret;
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
