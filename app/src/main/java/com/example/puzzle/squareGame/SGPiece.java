package com.example.puzzle.squareGame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.puzzle.Constants;
import com.example.puzzle.R;
import com.example.puzzle.Utils;

import java.util.Random;

public class SGPiece {
    public static final int borderSize = 5;

    // this piece's handler is responsible for calling recycle()
    // on this.originalPieceImage, but not on this.originalCanvas;

    Context context;
    Bitmap originalCanvas, originalPieceImage;
    int numHorizontal, numVertical, pieceWidth, pieceHeight, containerWidth, containerHeight;
    int targeti, targetj;
    double containerRatioX, containerRatioY;
    int[] outerColor;
    int[] innerColor;
    public ImageView imageView;


    private SGPiece() {}

    public static class Builder {
        private SGPiece piece;

        public Builder(Context context, Bitmap imageBitmap, int numHoriz, int numVert, int pWidth, int pHeight) {
            this.piece = new SGPiece();
            this.piece.context = context;
            this.piece.originalCanvas = imageBitmap;
            this.piece.numHorizontal = numHoriz;
            this.piece.numVertical = numVert;
            this.piece.pieceWidth = pWidth;
            this.piece.pieceHeight = pHeight;

            Random rand = new Random();
            this.setPosition(rand.nextInt(this.piece.numHorizontal * this.piece.numVertical));
            this.piece.setBaseBorderColors();
            this.setContainerDims(pWidth, pHeight);
            this.setContainerRatios(rand.nextDouble(), rand.nextDouble());
        }

        public void setPosition(int pos) {
            this.piece.targeti = pos / this.piece.numHorizontal;
            this.piece.targetj = pos % this.piece.numHorizontal;
        }

        public void setContainerDims(int containerWidth, int containerHeight) {
            this.piece.containerWidth = containerWidth;
            this.piece.containerHeight = containerHeight;
        }

        public void setContainerRatios(double containerRatioX, double containerRatioY) {
            this.piece.containerRatioX = containerRatioX;
            this.piece.containerRatioY = containerRatioY;
        }

        public void setBorderColors(int[] outerColor, int[] innerColor) {
            this.piece.outerColor = outerColor;
            this.piece.innerColor = innerColor;
        }

        public SGPiece build() {
            this.piece.setup();
            return this.piece;
        }
    }

    private void setup() {

        int subImageWidth = this.originalCanvas.getWidth() / this.numHorizontal;
        int subImageHeight = this.originalCanvas.getHeight() / this.numVertical;
        int x = this.targetj * subImageWidth;
        int y = this.targeti * subImageHeight;

        this.originalPieceImage = Bitmap.createBitmap(this.originalCanvas, x, y, subImageWidth, subImageHeight);
        this.originalPieceImage = Utils.scaleBitmapAndRecycle(this.originalPieceImage, this.pieceWidth, this.pieceHeight);

        this.imageView = new ImageView(this.context);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(this.pieceWidth, this.pieceHeight);

        params.leftMargin = Utils.getMargin(this.containerWidth, this.pieceWidth, this.containerRatioX);
        params.topMargin = Utils.getMargin(this.containerHeight, this.pieceHeight , this.containerRatioY);

        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        this.imageView.setLayoutParams(params);

        this.updateBorderColorsForPosition(this.targeti, this.targetj, this.outerColor, this.innerColor, context);
        this.drawMarginIf(new boolean[] {true, true, true, true}, this.outerColor, this.innerColor);
    }

    private void setBaseBorderColors() {
        this.outerColor = new int[4];
        this.innerColor = new int[4];

        for (int k = 0; k < 4; ++k) {
            this.outerColor[k] = ContextCompat.getColor(this.context, R.color.outerPieceColor);
            this.innerColor[k] = ContextCompat.getColor(this.context, R.color.innerPieceColor);
        }
    }

    public void updateBorderColorsForPosition(int i, int j, int[] outerColor, int[] innerColor, Context context) {
        if (i == 0) {
            outerColor[0] = context.getResources().getColor(R.color.outerMarginColor);
            innerColor[0] = context.getResources().getColor(R.color.innerMarginColor);
        }
        if (i == this.numVertical - 1) {
            outerColor[2] = context.getResources().getColor(R.color.outerMarginColor);
            innerColor[2] = context.getResources().getColor(R.color.innerMarginColor);
        }

        if (j == 0) {
            outerColor[3] = context.getResources().getColor(R.color.outerMarginColor);
            innerColor[3] = context.getResources().getColor(R.color.innerMarginColor);
        }
        if (j == this.numHorizontal - 1) {
            outerColor[1] = context.getResources().getColor(R.color.outerMarginColor);
            innerColor[1] = context.getResources().getColor(R.color.innerMarginColor);
        }
    }

    public void resetBorderColors() {
        this.setBaseBorderColors();
        this.updateBorderColorsForPosition(this.targeti, this.targetj, this.outerColor, this.innerColor, this.context);
    }

    public void drawMarginIf(boolean[] shouldDraw, int[] outerColor, int[] innerColor) {
        Bitmap currentBitmap = this.originalPieceImage.copy(this.originalPieceImage.getConfig(), true);

        for (int marginIndex = 0; marginIndex < 4; ++marginIndex) {
            if (shouldDraw[marginIndex]) {
                this.drawMargin(currentBitmap, outerColor[marginIndex], innerColor[marginIndex], marginIndex);
            }
        }

        Bitmap old = null;
        if (this.imageView.getDrawable() != null) {
            old = ((BitmapDrawable)this.imageView.getDrawable()).getBitmap();
        }

        this.imageView.setImageBitmap(currentBitmap);

        if (old != null) {
            old.recycle();
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
            for (int y = 0; y < SGPiece.borderSize; ++y) {
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
            for (int y = 0; y < SGPiece.borderSize; ++y) {
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
        for (int x = 0; x < SGPiece.borderSize; ++x) {
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
        for (int x = 0; x < SGPiece.borderSize; ++x) {
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

    public void update(SGPiece[][] pieceMatrix, boolean updateNeighbours, Context context) {
        boolean[] shouldDraw = new boolean[] {true, true, true, true};

        for (int k = 0; k < 4; ++k) {
            int nx = this.targeti + Constants.dx[k];
            int ny = this.targetj + Constants.dy[k];

            if (!(0 <= nx && nx < this.numVertical && 0 <= ny && ny < this.numHorizontal)) {
                continue;
            }

            if (pieceMatrix[nx][ny] != null) {
                shouldDraw[k] = false;

                if (updateNeighbours) {
                    pieceMatrix[nx][ny].update(pieceMatrix, false, context);
                }
            }
        }

        this.drawMarginIf(shouldDraw, this.outerColor, this.innerColor);
    }
}
