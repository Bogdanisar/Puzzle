package com.example.puzzle.jigsaw;

import java.util.LinkedList;
import java.util.List;

public class JigsawPieceGroup {
    private LinkedList<JigsawPiece> pieceList;
    private int minI, maxI, minJ, maxJ;
    private float translationX, translationY;

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

    public LinkedList<JigsawPiece> getPieceList() {
        return pieceList;
    }



    public JigsawPieceGroup(JigsawPiece piece) {
        this.setMinI(piece.getI());
        this.setMaxI(piece.getI());
        this.setMinJ(piece.getJ());
        this.setMaxJ(piece.getJ());
        this.translationX = piece.getView().getTranslationX();
        this.translationY = piece.getView().getTranslationY();

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

    public void addGroup(JigsawPieceGroup group) {
        for (JigsawPiece piece : group.getPieceList()) {
            this.addPiece(piece);
        }

        this.setTranslationByDifference(0, 0);
    }

    public void setTranslation(float translationX, float translationY) {
        this.translationX = translationX;
        this.translationY = translationY;

        this.repositionPieces();
    }

    public void setTranslationByDifference(float xDiff, float yDiff) {
        this.setTranslation(this.getTranslationX() + xDiff, this.getTranslationY() + yDiff);
    }

    public void repositionPieces() {
        for (JigsawPiece piece : pieceList) {
            int xDiff = (piece.getJ() - this.getMinJ()) * piece.getWidth();
            int yDiff = (piece.getI() - this.getMinI()) * piece.getHeight();

            float newTranslationX = this.getTranslationX() + xDiff;
            float newTranslationY = this.getTranslationY() + yDiff;
            piece.setTraslation(newTranslationX, newTranslationY);

            piece.getView().bringToFront();
        }
    }
}
