package com.example.puzzle.jigsaw;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.puzzle.ActivityJigsawGame;
import com.example.puzzle.ActivityMain;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

public class JigsawPiece {
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

    public static void setNicheDimensions(int nicheWidth) {
        NICHE_WIDTH = nicheWidth;
        NICHE_HEIGHT = NICHE_WIDTH / 3;
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

    public static void buildNiches(int pieceDimension) {
        JigsawPiece.setNicheDimensions(pieceDimension);

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




    // instance code;
    protected int i, j, leftOffset, topOffset;
    protected JigsawPieceGroup group;
    protected ImageView view;
    private Bitmap pieceMask;

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
        return leftOffset;
    }

    public void setLeftOffset(int leftOffset) {
        this.leftOffset = leftOffset;
    }

    public int getTopOffset() {
        return topOffset;
    }

    public void setTopOffset(int topOffset) {
        this.topOffset = topOffset;
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


    private void setMaskAndOffsets(NICHE_STATE[] state) {
        this.leftOffset = 0;
        this.topOffset = 0;

        int contentWidth = NICHE_WIDTH;
        int contentHeight = NICHE_WIDTH;
        int totalWidth = contentWidth;
        int totalHeight = contentHeight;

        if (state[TOP] == NICHE_STATE.OUTER) {
            this.topOffset += NICHE_HEIGHT;
            totalHeight += NICHE_HEIGHT;
        }
        if (state[LEFT] == NICHE_STATE.OUTER) {
            this.leftOffset += NICHE_HEIGHT;
            totalWidth += NICHE_HEIGHT;
        }

        if (state[BOTTOM] == NICHE_STATE.OUTER) {
            totalHeight += NICHE_HEIGHT;
        }
        if (state[RIGHT] == NICHE_STATE.OUTER) {
            totalWidth += NICHE_HEIGHT;
        }


        Paint foreground_paint = new Paint();
        foreground_paint.setColor(FOREGROUND_COLOR);

        this.pieceMask = Bitmap.createBitmap(totalWidth, totalHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(this.pieceMask);
        canvas.drawColor(BACKGROUND_COLOR);

        canvas.drawRect(new Rect(this.leftOffset, this.topOffset, this.leftOffset + contentWidth, this.topOffset + contentWidth), foreground_paint);

        if (state[TOP] == NICHE_STATE.OUTER) {
            canvas.drawBitmap(outerNiches[TOP], this.leftOffset, 0, null);
        }
        else if (state[TOP] == NICHE_STATE.INNER) {
            canvas.drawBitmap(innerNiches[TOP], this.leftOffset, 0, null);
        }

        if (state[LEFT] == NICHE_STATE.OUTER) {
            canvas.drawBitmap(outerNiches[LEFT], 0, this.topOffset, null);
        }
        else if (state[LEFT] == NICHE_STATE.INNER) {
            canvas.drawBitmap(innerNiches[LEFT], 0, this.topOffset, null);
        }

        if (state[BOTTOM] == NICHE_STATE.OUTER) {
            canvas.drawBitmap(outerNiches[BOTTOM], this.leftOffset, this.topOffset + contentHeight, null);
        }
        else if (state[BOTTOM] == NICHE_STATE.INNER) {
            canvas.drawBitmap(innerNiches[BOTTOM], this.leftOffset, this.topOffset + contentHeight - NICHE_HEIGHT, null);
        }

        if (state[RIGHT] == NICHE_STATE.OUTER) {
            canvas.drawBitmap(outerNiches[RIGHT], this.leftOffset + contentWidth, this.topOffset, null);
        }
        else if (state[RIGHT] == NICHE_STATE.INNER) {
            canvas.drawBitmap(innerNiches[RIGHT], this.leftOffset + contentWidth - NICHE_HEIGHT, this.topOffset, null);
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

    private void setView(Bitmap wholeImage, ActivityJigsawGame game) {
        int pieceDimension = game.pieceDimension;
        int numHorizontal = game.numHorizontal;
        int numVertical = game.numVertical;
        int left = this.j * pieceDimension, top = i * pieceDimension;


        Bitmap subImage = Bitmap.createBitmap(wholeImage, left, top, wholeImage.getWidth() / numHorizontal, wholeImage.getHeight() / numVertical);
        Bitmap rescaledSubImage = Bitmap.createScaledBitmap(subImage, this.pieceMask.getWidth(), this.pieceMask.getHeight(), true);
        if (subImage != rescaledSubImage) {
            subImage.recycle();
        }
        subImage = rescaledSubImage;

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

        // add the border
        addBorder(Color.TRANSPARENT, piecePixels, this.pieceMask.getHeight(), this.pieceMask.getWidth());

        this.pieceMask.setPixels(piecePixels, 0, this.pieceMask.getWidth(), 0, 0, this.pieceMask.getWidth(), this.pieceMask.getHeight());


        this.view = new ImageView(game);
        this.view.setAdjustViewBounds(true);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(subImage.getWidth(), subImage.getHeight());
        params.topMargin = 0;
        params.leftMargin = 0;
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        params.addRule(RelativeLayout.ALIGN_PARENT_START);
        this.view.setLayoutParams(params);

        ActivityJigsawGame.setImageBackground(this.view, this.pieceMask, game);
    }

    private void setRandomPosition(Rect centerRect) {
        int minX = centerRect.left, maxX = centerRect.right - this.pieceMask.getWidth();
        int minY = centerRect.top, maxY = centerRect.bottom - this.pieceMask.getHeight();

        int x = ActivityJigsawGame.getRandomInt(minX, maxX);
        int y = ActivityJigsawGame.getRandomInt(minY, maxY);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) this.view.getLayoutParams();
        params.leftMargin = x;
        params.topMargin = y;
    }




    public static JigsawPiece getPiece(int i, int j, Rect centerRect, ActivityJigsawGame game) {
        Bitmap image = game.imageBitmap;
        NICHE_STATE[][] rightMargin = game.rightMargin;
        NICHE_STATE[][] bottomMargin = game.bottomMargin;

        JigsawPiece piece = new JigsawPiece();
        piece.setI(i);
        piece.setJ(j);
        piece.setGroup(null);

        NICHE_STATE[] states = JigsawPiece.getNicheStates(i, j, rightMargin, bottomMargin);
        piece.setMaskAndOffsets(states);
        piece.setView(image, game);
        piece.setRandomPosition(centerRect);

        return piece;
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






}
