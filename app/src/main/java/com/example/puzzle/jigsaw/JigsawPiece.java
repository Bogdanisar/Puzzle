package com.example.puzzle.jigsaw;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.LinkedList;

public class JigsawPiece {

    // instance code;
    protected ActivityJigsawGame context;
    protected int i, j;
    protected JigsawPieceGroup group;
    protected ImageView view;
    protected boolean[] outerNichePresence;
    protected Bitmap pieceMask;
    protected Bitmap pieceBitmap;


    public ActivityJigsawGame getContext() {
        return context;
    }

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public int getJ() {
        return j;
    }

    public void setJ(int j) {
        this.j = j;
    }

    public int getLeftOffset() {
        if (this.outerNichePresence[LEFT]) {
            return this.context.getNicheHeightDimension();
        }
        return 0;
    }

    public int getTopOffset() {
        if (this.outerNichePresence[TOP]) {
            return this.context.getNicheHeightDimension();
        }
        return 0;
    }

    public ImageView getView() {
        return view;
    }

    public JigsawPieceGroup getGroup() {
        return group;
    }

    public void setGroup(JigsawPieceGroup group) {
        this.group = group;
    }

    public int getTotalHeight() {
        int ans = this.context.getPieceDimension();
        if (this.outerNichePresence[TOP]) {
            ans += this.context.getNicheHeightDimension();
        }

        if (this.outerNichePresence[BOTTOM]) {
            ans += this.context.getNicheHeightDimension();
        }

        return ans;
    }

    public int getTotalWidth() {
        int ans = this.context.getPieceDimension();
        if (this.outerNichePresence[LEFT]) {
            ans += this.context.getNicheHeightDimension();
        }

        if (this.outerNichePresence[RIGHT]) {
            ans += this.context.getNicheHeightDimension();
        }

        return ans;
    }

    public int getContentHeight() {
        return this.context.getPieceDimension();
    }

    public int getContentWidth() {
        return this.context.getPieceDimension();
    }

    public void setTraslation(float translationX, float translationY) {
        this.getView().setTranslationX(translationX - this.getLeftOffset());
        this.getView().setTranslationY(translationY - this.getTopOffset());
    }

    public float getContentTranslationX() {
        return this.getView().getTranslationX() + this.getLeftOffset();
    }

    public float getContentTranslationY() {
        return this.getView().getTranslationY() + this.getTopOffset();
    }

    JigsawPiece(ActivityJigsawGame context) {
        this.context = context;
        this.outerNichePresence = new boolean[4];
    }






    public boolean isTouchedBy(float x, float y) {
        ImageView view = this.getView();
        float left = view.getTranslationX();
        float right = left + this.getTotalWidth() - 1;
        float top = view.getTranslationY();
        float bottom = top + this.getTotalHeight() - 1;

        RectF rect = new RectF(left, top, right, bottom);
        if (rect.contains(x, y) == false) {
            return false;
        }

        x -= left;
        y -= top;

        // bring a point from [0, actualImageDimension) to [0, bitmapDimension)
        // so you don't have to resize the bitmap
        x = x * this.pieceBitmap.getWidth() / this.getTotalWidth();
        y = y * this.pieceBitmap.getHeight() / this.getTotalHeight();

        if (this.pieceBitmap.getPixel((int)x, (int)y) == Color.TRANSPARENT) {
            return false;
        }
        return true;
    }

    public boolean isToTheRight(JigsawPiece other) {
        if (this.getI() != other.getI()) {
            return false;
        }

        if (this.getJ() + 1 != other.getJ()) {
            return false;
        }

        if (this.context.positionsAreCloseEnough(this.getContentTranslationY(), other.getContentTranslationY()) == false) {
            return false;
        }

        if (this.context.positionsAreCloseEnough(this.getContentTranslationX() + this.context.getPieceDimension(), other.getContentTranslationX()) == false) {
            return false;
        }

        return true;
    }

    public boolean isDownwards(JigsawPiece other) {
        if (this.getJ() != other.getJ()) {
            return false;
        }

        if (this.getI() + 1 != other.getI()) {
            return false;
        }

        if (this.context.positionsAreCloseEnough(this.getContentTranslationX(), other.getContentTranslationX()) == false) {
            return false;
        }

        if (this.context.positionsAreCloseEnough(this.getContentTranslationY() + this.context.getPieceDimension(), other.getContentTranslationY()) == false) {
            return false;
        }

        return true;
    }

    public boolean connectsWith(JigsawPiece other) {
        if (this.isToTheRight(other) || other.isToTheRight(this)) {
            return true;
        }
        if (this.isDownwards(other) || other.isDownwards(this)) {
            return true;
        }

        return false;
    }










    // static code
    public static final int TOP = 0;
    public static final int RIGHT = 1;
    public static final int BOTTOM = 2;
    public static final int LEFT = 3;
    public static final int FOREGROUND_COLOR = 0xFF0000FF;
    public static final int BACKGROUND_COLOR = 0xFF00FFFF;
    public static final int OUTER_BORDER_COLOR = 0xFFFFFFFF;
    public static final int INNER_BORDER_COLOR = 0xFF000000;

    public static int NICHE_WIDTH = -1;
    public static int NICHE_HEIGHT = -1;

    public static enum NICHE_STATE {
        OUTER, INNER, NONE
    };
    public static Bitmap[] outerNiches = new Bitmap[4];
    public static Bitmap[] innerNiches = new Bitmap[4];



    public static NICHE_STATE invertState(NICHE_STATE state) {
        if (state == NICHE_STATE.OUTER) {
            return NICHE_STATE.INNER;
        }
        else if (state == NICHE_STATE.INNER) {
            return NICHE_STATE.OUTER;
        }
        return NICHE_STATE.NONE;
    }

    public static void setNicheDimensions(int nicheHeight, int nicheWidth) {
        NICHE_WIDTH = nicheWidth;
        NICHE_HEIGHT = nicheHeight;
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return rotatedBitmap;
    }

    public static Bitmap invertNiche(Bitmap bitmap) {
        Bitmap niche = bitmap.copy(bitmap.getConfig(), true);

        int[] pixels = new int[niche.getHeight() * niche.getWidth()];
        niche.getPixels(pixels, 0, niche.getWidth(), 0, 0, niche.getWidth(), niche.getHeight());

        for (int i = 0; i < pixels.length; ++i) {
            if (pixels[i] == FOREGROUND_COLOR) {
                pixels[i] = BACKGROUND_COLOR;
            }
            else {
                pixels[i] = FOREGROUND_COLOR;
            }
        }

        niche.setPixels(pixels, 0, niche.getWidth(), 0, 0, niche.getWidth(), niche.getHeight());
        return niche;
    }

    public static void buildNiches(int nicheHeightDimension, int nicheWidthDimension) {
        JigsawPiece.setNicheDimensions(nicheHeightDimension, nicheWidthDimension);

        Paint paint = new Paint();
        paint.setColor(FOREGROUND_COLOR);
        paint.setStyle(Paint.Style.STROKE);

        final int dim = 500;
        Bitmap bitmap = Bitmap.createBitmap(dim, dim / 3, Bitmap.Config.ARGB_8888);


        // start drawing the puzzle top outer niche into the bitmap;
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(BACKGROUND_COLOR);

        Path path = new Path();
        float diameter = 2.8f * bitmap.getHeight() / 4f;
        float radius = diameter / 2;
        float cx = bitmap.getWidth() / 2, cy = radius + 5;
        float horizontalAdd = 12;
        path.addOval(new RectF(cx - radius - horizontalAdd, cy - radius, cx + radius + horizontalAdd, cy + radius), Path.Direction.CW);

        float dif = 32, startX, endX, sweepAngle = 130, topOffset = 95;
        endX = cx - dif;
        startX = endX - diameter;
        path.addArc(new RectF(startX, topOffset, endX, bitmap.getHeight()), 90 - sweepAngle, sweepAngle);

        startX = cx + dif;
        endX = startX + diameter;
        path.addArc(new RectF(startX, topOffset, endX, bitmap.getHeight()), 90, sweepAngle);

        canvas.drawPath(path, paint);
        // finish drawing the puzzle top outer niche into the bitmap;


        // fill the inside;
        for (int y = 0; y < bitmap.getHeight(); ++y) {
            int minX = (int)1e9;
            int maxX = -1;
            for (int x = 1; x < bitmap.getWidth(); ++x) {
                if (bitmap.getPixel(x, y) == FOREGROUND_COLOR) {
                    minX = Math.min(minX, x);
                    maxX = Math.max(maxX, x);
                }
            }

            for (int x = minX; x <= maxX; ++x) {
                bitmap.setPixel(x, y, FOREGROUND_COLOR);
            }
        }

        Bitmap rescaledBitmap = bitmap.createScaledBitmap(bitmap, NICHE_WIDTH, NICHE_HEIGHT, true);
        if (bitmap != rescaledBitmap) {
            bitmap.recycle();
        }
        bitmap = rescaledBitmap;


        // build the other niches based on the top outer niche;
        outerNiches[TOP] = bitmap;
        outerNiches[RIGHT] = JigsawPiece.rotateBitmap(outerNiches[TOP], 90);
        outerNiches[BOTTOM] = JigsawPiece.rotateBitmap(outerNiches[RIGHT], 90);
        outerNiches[LEFT] = JigsawPiece.rotateBitmap(outerNiches[BOTTOM], 90);

        innerNiches[TOP] = JigsawPiece.invertNiche(outerNiches[BOTTOM]);
        innerNiches[RIGHT] = JigsawPiece.invertNiche(outerNiches[LEFT]);
        innerNiches[BOTTOM] = JigsawPiece.invertNiche(outerNiches[TOP]);
        innerNiches[LEFT] = JigsawPiece.invertNiche(outerNiches[RIGHT]);
    }





    public static NICHE_STATE[] getNicheStates(int i, int j, NICHE_STATE[][] rightMargin, NICHE_STATE[][] bottomMargin) {
        NICHE_STATE[] states = new NICHE_STATE[4];

        // determine TOP state
        if (i == 0) {
            states[TOP] = NICHE_STATE.NONE;
        }
        else {
            states[TOP] = JigsawPiece.invertState(bottomMargin[i - 1][j]);
        }

        // determine RIGHT state
        states[RIGHT] = rightMargin[i][j];

        // determine BOTTOM state
        states[BOTTOM] = bottomMargin[i][j];

        // determine LEFT state
        if (j == 0) {
            states[LEFT] = NICHE_STATE.NONE;
        }
        else {
            states[LEFT] = JigsawPiece.invertState(rightMargin[i][j - 1]);
        }

        return states;
    }

    private void setMaskAndOffsets(NICHE_STATE[] state) {
        for (int i = 0; i < 4; ++i) {
            this.outerNichePresence[i] = (state[i] == NICHE_STATE.OUTER);
        }


        Paint foreground_paint = new Paint();
        foreground_paint.setColor(FOREGROUND_COLOR);

        this.pieceMask = Bitmap.createBitmap(this.getTotalWidth(), this.getTotalHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(this.pieceMask);
        canvas.drawColor(BACKGROUND_COLOR);

        canvas.drawRect(new Rect(this.getLeftOffset(), this.getTopOffset(), this.getLeftOffset() + this.getContentWidth(), this.getTopOffset() + this.getContentWidth()), foreground_paint);

        if (state[TOP] == NICHE_STATE.OUTER) {
            canvas.drawBitmap(outerNiches[TOP], this.getLeftOffset(), 0, null);
        }
        else if (state[TOP] == NICHE_STATE.INNER) {
            canvas.drawBitmap(innerNiches[TOP], this.getLeftOffset(), 0, null);
        }

        if (state[LEFT] == NICHE_STATE.OUTER) {
            canvas.drawBitmap(outerNiches[LEFT], 0, this.getTopOffset(), null);
        }
        else if (state[LEFT] == NICHE_STATE.INNER) {
            canvas.drawBitmap(innerNiches[LEFT], 0, this.getTopOffset(), null);
        }

        if (state[BOTTOM] == NICHE_STATE.OUTER) {
            canvas.drawBitmap(outerNiches[BOTTOM], this.getLeftOffset(), this.getTopOffset() + this.getContentHeight(), null);
        }
        else if (state[BOTTOM] == NICHE_STATE.INNER) {
            canvas.drawBitmap(innerNiches[BOTTOM], this.getLeftOffset(), this.getTopOffset() + this.getContentHeight() - NICHE_HEIGHT, null);
        }

        if (state[RIGHT] == NICHE_STATE.OUTER) {
            canvas.drawBitmap(outerNiches[RIGHT], this.getLeftOffset() + this.getContentWidth(), this.getTopOffset(), null);
        }
        else if (state[RIGHT] == NICHE_STATE.INNER) {
            canvas.drawBitmap(innerNiches[RIGHT], this.getLeftOffset() + this.getContentWidth() - NICHE_HEIGHT, this.getTopOffset(), null);
        }
    }



    private static class QueueElement {
        public int i, j;
        QueueElement(int i, int j) {
            this.i = i;
            this.j = j;
        }
    }

    private static void addBorder(int outerColor, int[] piecePixels, int height, int width) {
        int dx[] = {-1, -1, -1, +0, +0, +1, +1, +1};
        int dy[] = {-1, +0, +1, -1, +1, -1, +0, +1};

        int[][] dist = new int[height][width];

        LinkedList<QueueElement> queue = new LinkedList<>();
        for (int i = 0, k = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j, ++k) {
                if (piecePixels[k] == outerColor) {
                    dist[i][j] = 1;
                    QueueElement elem = new QueueElement(i, j);
                    queue.addLast(elem);
                }
            }
        }

        int[] js = {0, width - 1};
        for (int i = 0; i < height; ++i) {
            for (int j : js) {

                if (dist[i][j] == 0) {
                    dist[i][j] = 2;
                    QueueElement elem = new QueueElement(i, j);
                    queue.addLast(elem);
                }
            }
        }

        int[] is = {0, height - 1};
        for (int i : is) {
            for (int j = 0; j < width; ++j) {

                if (dist[i][j] == 0) {
                    dist[i][j] = 2;
                    QueueElement elem = new QueueElement(i, j);
                    queue.addLast(elem);
                }
            }
        }

        while (queue.isEmpty() == false) {
            QueueElement curr = queue.getFirst();
            queue.removeFirst();
            int ci = curr.i;
            int cj = curr.j;
            int cdist = dist[ci][cj];
            int ck = ActivityJigsawGame.BidimIndexToOnedimIndex(ci, cj, width);

            if (piecePixels[ck] != outerColor) {
                if (cdist % 2 == 0) {
                    piecePixels[ck] = OUTER_BORDER_COLOR;
                }
                else {
                    piecePixels[ck] = INNER_BORDER_COLOR;
                }
            }

            if (cdist >= 3) {
                continue;
            }

            for (int d = 0; d < dx.length; ++d) {
                int ni = ci + dx[d];
                int nj = cj + dy[d];

                if ( !(0 <= ni && ni < height && 0 <= nj && nj < width) ) {
                    continue;
                }

                if (dist[ni][nj] != 0) {
                    continue;
                }

                QueueElement nextElem = new QueueElement(ni, nj);
                dist[ni][nj] = cdist + 1;
                queue.addLast(nextElem);
            }
        }
    }

    private void setView(Bitmap wholeImage) {
        int subImageContentWidth = this.context.getPieceDimension();
        int subImageContentHeight = this.context.getPieceDimension();
        int subImageTotalWidth = this.pieceMask.getWidth();
        int subImageTotalHeight = this.pieceMask.getHeight();
        int left = this.j * subImageContentWidth, top = this.i * subImageContentHeight;
        int subImageTopNicheHeight = this.context.getNicheHeightDimension();
        int subImageLeftNicheWidth = this.context.getNicheHeightDimension();

        if (this.outerNichePresence[TOP]) {
            top -= subImageTopNicheHeight;
        }

        if (this.outerNichePresence[LEFT]) {
            left -= subImageLeftNicheWidth;
        }


        Bitmap subImage = Bitmap.createBitmap(wholeImage, left, top, subImageTotalWidth, subImageTotalHeight);

        // merge the mask with the subImage
        int[] piecePixels = new int[ this.pieceMask.getHeight() * this.pieceMask.getWidth() ];
        int[] subImagePixels = new int[ this.pieceMask.getHeight() * this.pieceMask.getWidth() ];
        this.pieceMask.getPixels(piecePixels, 0, this.pieceMask.getWidth(), 0, 0, this.pieceMask.getWidth(), this.pieceMask.getHeight());
        subImage.getPixels(subImagePixels, 0, this.pieceMask.getWidth(), 0, 0, this.pieceMask.getWidth(), this.pieceMask.getHeight());


        for (int i = 0; i < piecePixels.length; ++i) {
            if (piecePixels[i] == FOREGROUND_COLOR) {
                piecePixels[i] = subImagePixels[i];
            } else {
                piecePixels[i] = Color.TRANSPARENT;
            }
        }

        subImage.recycle();

        // add the border
        addBorder(Color.TRANSPARENT, piecePixels, this.pieceMask.getHeight(), this.pieceMask.getWidth());

        this.pieceBitmap = this.pieceMask;
        this.pieceBitmap.setPixels(piecePixels, 0, this.pieceMask.getWidth(), 0, 0, this.pieceMask.getWidth(), this.pieceMask.getHeight());


        this.view = new ImageView(this.context);
        this.view.setAdjustViewBounds(true);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(this.pieceBitmap.getWidth(), this.pieceBitmap.getHeight());
        params.topMargin = 0;
        params.leftMargin = 0;
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        params.addRule(RelativeLayout.ALIGN_PARENT_START);
        this.view.setLayoutParams(params);

        ActivityJigsawGame.setImageBackground(this.view, this.pieceBitmap, this.context);
    }

    public void setRandomPosition(Rect centerRect) {
        int minX = centerRect.left, maxX = centerRect.right - this.getTotalWidth();
        int minY = centerRect.top, maxY = centerRect.bottom - this.getTotalHeight();

        int x = ActivityJigsawGame.getRandomInt(minX, maxX);
        int y = ActivityJigsawGame.getRandomInt(minY, maxY);

        this.getGroup().setTranslation(x, y);
    }




    public static JigsawPiece getPiece(int i, int j, ActivityJigsawGame game) {
        JigsawPiece piece = new JigsawPiece(game);
        piece.setI(i);
        piece.setJ(j);
        piece.setGroup(null);

        NICHE_STATE[] states = JigsawPiece.getNicheStates(i, j, game.rightMargin, game.bottomMargin);
        piece.setMaskAndOffsets(states);
        piece.setView(game.imageBitmap);

        return piece;
    }
}
