package com.example.puzzle.squareGame;

import com.example.puzzle.Constants;

public class SGShell extends SGSimple {
    @Override
    protected String getGamemodeString() {
        return "Shell";
    }

    @Override
    protected boolean pieceCanBePlaced(SGPiece piece) {
        int i = piece.targeti, j = piece.targetj;
        if (i == 0 || i == this.numVertical - 1) {
            return true;
        }
        if (j == 0 || j == this.numHorizontal - 1) {
            return true;
        }

        for (int k = 0; k < Constants.di.length; ++k) {
            int ni = i + Constants.di[k];
            int nj = j + Constants.dj[k];

            if (this.pieceMatrix[ni][nj] != null) {
                return true;
            }
        }

        return false;
    }
}
