package com.example.puzzle.squareGame;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import com.example.puzzle.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

public class SGState {
    public static final int maxImageWidth = 1000, maxImageHeight = 1000;

    int numVertical, numHorizontal;
    Long duration;

    int[] placedPieceIds, onTrackPieceIds, freePieceIds;
    double[] pieceContentRatioX, pieceContentRatioY;

    int smallImageId;
    Bitmap imageBitmap;

    private final void commonInit(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        this.numVertical = (Integer)bundle.get("columnNumber");
        this.numHorizontal = (Integer)bundle.get("rowNumber");
        int numPieces = this.numHorizontal * this.numVertical;
        this.smallImageId = (Integer)bundle.get("smallImageSelected");
        this.duration = 0L;

        if (bundle.get("imageSelected") == null) {
            Uri uri = Uri.parse( (String)bundle.get("userImageUri") );

            try {
                this.imageBitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
            }
            catch (Exception except) {
                throw new IllegalArgumentException("Can't find imageView at specified URI");
            }
        }
        else {
            int imageId = (Integer)bundle.get("imageSelected");
            this.imageBitmap = BitmapFactory.decodeResource(context.getResources(), imageId);
        }
        this.imageBitmap = this.getScaledImage(this.imageBitmap);

        this.pieceContentRatioX = new double[numPieces];
        this.pieceContentRatioY = new double[numPieces];

        Random rand = new Random();
        for (int i = 0; i < numPieces; ++i) {
            this.pieceContentRatioX[i] = rand.nextDouble();
            this.pieceContentRatioY[i] = rand.nextDouble();
        }
    }

    private final int[] getFreePieceIds() {
        Set<Integer> freePieces = new TreeSet<>();

        for (int i = 0; i < this.numHorizontal * this.numVertical; ++i) {
            freePieces.add(i);
        }

        for (int val : this.placedPieceIds) {
            freePieces.remove(val);
        }

        for (int val : this.onTrackPieceIds) {
            freePieces.remove(val);
        }

        Integer[] aux = freePieces.toArray(new Integer[0]);
        int[] ret = new int[aux.length];
        for (int i = 0; i < aux.length; ++i) {
            ret[i] = aux[i];
        }

        return ret;
    }


    public SGState(SGOnePiece context, Intent intent) {
        this.commonInit(context, intent);

        this.placedPieceIds = new int[]{
                0,
                this.numHorizontal - 1,
                this.numHorizontal * this.numVertical - this.numHorizontal,
                this.numHorizontal * this.numVertical - 1,
        };
        this.onTrackPieceIds = new int[0];

        ArrayList<Integer> availablePositions = new ArrayList<>();
        for (int i = 0; i < this.numVertical; ++i) {
            for (int j = 0; j < this.numHorizontal; ++j) {
                if (Utils.indexIsNextToCorner(i, j, this.numVertical, this.numHorizontal)) {
                    availablePositions.add( Utils.getPositionFromIndexes(i, j, this.numHorizontal) );
                }
            }
        }

        Random random = new Random();
        int idx = random.nextInt(availablePositions.size());
        int pos = availablePositions.get(idx);

        this.freePieceIds = new int[] {pos};
    }

    public SGState(SGSimple context, Intent intent) {
        this.commonInit(context, intent);

        SGState.this.placedPieceIds = new int[0];
        SGState.this.onTrackPieceIds = new int[0];

        this.freePieceIds = this.getFreePieceIds();
    }


    public Bitmap getScaledImage(Bitmap imageBitmap) {
        int newImageWidth = Math.min(SGState.maxImageWidth, imageBitmap.getWidth());
        int newImageHeight = Math.min(SGState.maxImageHeight, imageBitmap.getHeight());
        newImageWidth = newImageWidth / this.numHorizontal * this.numHorizontal;
        newImageHeight = newImageHeight / this.numVertical * this.numVertical;

        Bitmap scaledImage = Utils.scaleBitmapAndRecycle(imageBitmap, newImageWidth, newImageHeight);
        return scaledImage;
    }
}
