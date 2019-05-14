package com.example.puzzle.jigsaw;

import android.graphics.Rect;
import android.support.v7.view.menu.ActionMenuItem;

import com.example.puzzle.ActivityJigsawGame;

import java.util.LinkedList;
import java.util.List;

public class JigsawPieceGroup {
    private ActivityJigsawGame context;
    private LinkedList<JigsawPiece> pieceList;
    private int minI, maxI, minJ, maxJ;
    private float translationX, translationY;

    public ActivityJigsawGame getContext() {
        return context;
    }

    public int getMinI() {
        return minI;
    }

    public void setMinI(int minI) {
        this.minI = minI;
    }

    public int getMaxI() {
        return maxI;
    }

    public void setMaxI(int maxI) {
        this.maxI = maxI;
    }

    public int getMinJ() {
        return minJ;
    }

    public void setMinJ(int minJ) {
        this.minJ = minJ;
    }

    public int getMaxJ() {
        return maxJ;
    }

    public void setMaxJ(int maxJ) {
        this.maxJ = maxJ;
    }

    public float getTranslationX() {
        return translationX;
    }

    public float getTranslationY() {
        return translationY;
    }

    public int getTotalHeight() {
        if (this.getPieceList().size() == 0) {
            return 0;
        }

        return (this.getMaxI() - this.getMinI()) * this.getPieceList().getFirst().getContentHeight();
    }

    public int getTotalWidth() {
        if (this.getPieceList().size() == 0) {
            return 0;
        }

        return (this.getMaxJ() - this.getMinJ()) * this.getPieceList().getFirst().getContentWidth();
    }

    public float getCenterTranslationX() {
        return this.getTranslationX() + this.getTotalWidth() / 2;
    }

    public float getCenterTranslationY() {
        return this.getTranslationY() + this.getTotalHeight() / 2;
    }

    public LinkedList<JigsawPiece> getPieceList() {
        return pieceList;
    }



    public JigsawPieceGroup(ActivityJigsawGame context, JigsawPiece piece) {
        this.context = context;
        this.setMinI(piece.getI());
        this.setMaxI(piece.getI());
        this.setMinJ(piece.getJ());
        this.setMaxJ(piece.getJ());
        this.translationX = piece.getView().getTranslationX() + piece.getLeftOffset();
        this.translationY = piece.getView().getTranslationY() + piece.getTopOffset();

        this.pieceList = new LinkedList<>();
        this.pieceList.add(piece);
        piece.setGroup(this);
    }

    public void addPiece(JigsawPiece piece) {
        this.setMinI( Math.min(this.getMinI(), piece.getI()) );
        this.setMaxI( Math.max(this.getMaxI(), piece.getI()) );
        this.setMinJ( Math.min(this.getMinJ(), piece.getJ()) );
        this.setMaxJ( Math.max(this.getMaxJ(), piece.getJ()) );

        this.pieceList.add(piece);
        piece.setGroup(this);
    }

    public void addGroup(JigsawPieceGroup other) {
        int oldMinI = this.getMinI();
        int oldMinJ = this.getMinJ();

        for (JigsawPiece piece : other.getPieceList()) {
            this.addPiece(piece);
        }

        this.setTranslationFromOldIJ(oldMinI, oldMinJ);
    }

    public void addGroups(List<JigsawPieceGroup> otherGroups) {
        int oldMinI = this.getMinI();
        int oldMinJ = this.getMinJ();

        for (JigsawPieceGroup other : otherGroups) {
            for (JigsawPiece piece : other.getPieceList()) {
                this.addPiece(piece);
            }
        }

        this.setTranslationFromOldIJ(oldMinI, oldMinJ);
    }

    public void setTranslation(float translationX, float translationY) {
        this.translationX = translationX;
        this.translationY = translationY;

        this.repositionPieces();
    }

    public void setTranslationByDifference(float xDiff, float yDiff) {
        this.setTranslation(this.getTranslationX() + xDiff, this.getTranslationY() + yDiff);
    }

    public void setTranslationFromOldIJ(int oldMinI, int oldMinJ) {
        int iDiff = oldMinI - this.getMinI();
        int jDiff = oldMinJ - this.getMinJ();

        float newTranslationX = this.getTranslationX() - jDiff * this.context.getPieceDimension();
        float newTranslationY = this.getTranslationY() - iDiff * this.context.getPieceDimension();
        this.setTranslation(newTranslationX, newTranslationY);
    }

    public void setTranslationByCenter(float translationX, float translationY) {
        this.setTranslation(translationX - this.getTotalWidth() / 2, translationY - this.getTotalHeight() / 2);
    }

    public void repositionPieces() {
        for (JigsawPiece piece : this.getPieceList()) {
            int xDiff = (piece.getJ() - this.getMinJ()) * piece.getContentWidth();
            int yDiff = (piece.getI() - this.getMinI()) * piece.getContentHeight();

            float newTranslationX = this.getTranslationX() + xDiff;
            float newTranslationY = this.getTranslationY() + yDiff;
            piece.setTraslation(newTranslationX, newTranslationY);

            piece.getView().bringToFront();
        }
    }

    public boolean intersect(JigsawPieceGroup other) {
        Rect myExtendedRect = new Rect(this.getMinJ() - 1, this.getMinI() - 1, this.getMaxJ() + 1, this.getMaxI() + 1);
        Rect otherRect = new Rect(other.getMinJ(), other.getMinI(), other.getMaxJ(), other.getMaxI());

        if (ActivityJigsawGame.intersects(myExtendedRect, otherRect) == false) {
            return false;
        }

        for (JigsawPiece myPiece : this.getPieceList()) {
            for (JigsawPiece otherPiece : other.getPieceList()) {
                if (myPiece.connectsWith(otherPiece)) {
                    return true;
                }
            }
        }

        return false;
    }
}
