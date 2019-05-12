package com.example.puzzle.jigsaw;

import java.util.LinkedList;
import java.util.List;

public class JigsawPieceGroup {
    private LinkedList<JigsawPiece> pieceList;
    private int minI, maxI, minJ, maxJ;

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



    public JigsawPieceGroup(JigsawPiece piece) {
        this.setMinI(piece.getI());
        this.setMaxI(piece.getI());
        this.setMinJ(piece.getJ());
        this.setMaxJ(piece.getJ());

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
}
