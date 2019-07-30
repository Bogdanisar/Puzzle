package com.example.puzzle.jigsaw;

import android.graphics.Bitmap;
import android.net.Uri;

import com.example.puzzle.jigsaw.JigsawPiece;

import java.io.Serializable;
import java.util.ArrayList;

public class JigsawGameState implements Serializable {
    public Integer imageId, smallImageId, numVertical, numHorizontal;
    public long durationInMiliseconds;
    public double nicheHeightToScreenRatio;
    public ArrayList<ArrayList<Integer>> groupPieceList;
    public double[] groupTranslationRatioX;
    public double[] groupTranslationRatioY;
    public JigsawPiece.NICHE_STATE[][] rightMargin, bottomMargin;
    public Bitmap imageBitmap;

    public JigsawGameState(int numGroups) {
        this.groupTranslationRatioX = new double[numGroups];
        this.groupTranslationRatioY = new double[numGroups];

        this.groupPieceList = new ArrayList<>();
        for (int i = 0; i < numGroups; ++i) {
            this.groupPieceList.add( new ArrayList<Integer>() );
        }
    }

    public void addPieceToGroup(int groupIndex, int pieceIndex) {
        this.groupPieceList.get(groupIndex).add(pieceIndex);
    }

    public void setGroupTranslationRatio(int groupIndex, double translationX, double translationY) {
        this.groupTranslationRatioX[groupIndex] = translationX;
        this.groupTranslationRatioY[groupIndex] = translationY;
    }
}
