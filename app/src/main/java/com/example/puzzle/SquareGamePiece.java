package com.example.puzzle;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.Random;

class SquareGamePiece {
    public static final int borderSize = 5;

    ImageView image;
    Bitmap originalBitmap;
    int targeti, targetj;
    int[] outerColor = new int[4], innerColor = new int[4];

    SquareGamePiece(Bitmap imageBitmap, int pos, ActivitySquareGame context) {
        int i = pos / ActivitySquareGame.numHorizontal;
        int j = pos % ActivitySquareGame.numHorizontal;
        this.targeti = i;
        this.targetj = j;

        int subImageWidth = imageBitmap.getWidth() / ActivitySquareGame.numHorizontal;
        int subImageHeight = imageBitmap.getHeight() / ActivitySquareGame.numVertical;
        int x = j * subImageWidth;
        int y = i * subImageHeight;
        this.originalBitmap = Bitmap.createBitmap(imageBitmap, x, y, subImageWidth, subImageHeight);

        Bitmap scaledOriginal = Bitmap.createScaledBitmap(this.originalBitmap, ActivitySquareGame.pieceWidth, ActivitySquareGame.pieceHeight, true);
        if (scaledOriginal != this.originalBitmap) {
            this.originalBitmap.recycle();
        }
        this.originalBitmap = scaledOriginal;

        this.image = new ImageView(context);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ActivitySquareGame.pieceWidth, ActivitySquareGame.pieceHeight);
        params.leftMargin = getRandomPosition(context.getContainerWidth(), ActivitySquareGame.pieceWidth);
        params.topMargin = getRandomPosition(context.getContainerHeight(), ActivitySquareGame.pieceHeight);
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

    public void update(SquareGamePiece[][] pieceMatrix, boolean updateNeighbours) {
        int[] dx = new int[] {-1, 0, +1, 0};
        int[] dy = new int[] {0, +1, 0, -1};
        boolean[] shouldDraw = new boolean[] {true, true, true, true};

        for (int k = 0; k < 4; ++k) {
            int nx = this.targeti + dx[k];
            int ny = this.targetj + dy[k];

            if (!(0 <= nx && nx < ActivitySquareGame.numVertical && 0 <= ny && ny < ActivitySquareGame.numHorizontal)) {
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
        if (i == ActivitySquareGame.numVertical - 1) {
            outerColor[2] = context.getResources().getColor(R.color.outerMarginColor);
            innerColor[2] = context.getResources().getColor(R.color.innerMarginColor);
        }

        if (j == 0) {
            outerColor[3] = context.getResources().getColor(R.color.outerMarginColor);
            innerColor[3] = context.getResources().getColor(R.color.innerMarginColor);
        }
        if (j == ActivitySquareGame.numHorizontal - 1) {
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
            for (int y = 0; y < SquareGamePiece.borderSize; ++y) {
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
            for (int y = 0; y < SquareGamePiece.borderSize; ++y) {
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
        for (int x = 0; x < SquareGamePiece.borderSize; ++x) {
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
        for (int x = 0; x < SquareGamePiece.borderSize; ++x) {
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
