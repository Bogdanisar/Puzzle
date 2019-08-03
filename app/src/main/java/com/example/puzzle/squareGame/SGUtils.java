package com.example.puzzle.squareGame;

import android.view.View;
import android.widget.RelativeLayout;

public class SGUtils {

    public static int getMargin(int totalSize, int pieceSize, double sizeRatio) {
        int exclusiveLimit = totalSize - pieceSize + 1;
        int margin = (int)(sizeRatio * exclusiveLimit);
        return margin;
    }

    public static boolean isSGPieceCloseEnough(SGPiece piece, int eventX, int eventY, int attachedOffsetX, int attachedOffsetY) {
        int minX = 0, maxX = piece.getContainerWidth() - piece.getCurrentPieceWidth();
        int minY = 0, maxY = piece.getContainerHeight() - piece.getCurrentPieceHeight();

        int cornerX = eventX - attachedOffsetX;
        cornerX = Math.max(cornerX, minX);
        cornerX = Math.min(cornerX, maxX);

        int cornerY = eventY - attachedOffsetY;
        cornerY = Math.max(cornerY, minY);
        cornerY = Math.min(cornerY, maxY);

        int errorX = piece.getGenericPieceHeight() / 3;
        int errorY = piece.getGenericPieceHeight() / 3;

        int targetX = piece.getTargetj() * piece.getGenericPieceWidth();
        int targetY = piece.getTargeti() * piece.getGenericPieceHeight();

        if (Math.abs(targetX - cornerX) <= errorX && Math.abs(targetY - cornerY) <= errorY) {
            return true;
        }
        return false;
    }



    public static void changeSGPiecePosition(SGPiece piece, float x, float y, int attachedOffsetX, int attachedOffsetY) {
        int minX = 0, maxX = piece.getContainerWidth() - piece.getCurrentPieceWidth();
        int minY = 0, maxY = piece.getContainerHeight() - piece.getCurrentPieceHeight();

        View v = piece.imageView;
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) v.getLayoutParams();

        int newX = (int)x - attachedOffsetX;
        newX = Math.max(newX, minX);
        newX = Math.min(newX, maxX);

        int newY = (int)y - attachedOffsetY;
        newY = Math.max(newY, minY);
        newY = Math.min(newY, maxY);

        params.leftMargin = newX;
        params.topMargin = newY;
        v.requestLayout();
    }

    public static boolean pointIsInsideView(View v, int x, int y) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) v.getLayoutParams();

        if (!(params.leftMargin <= x && x < params.leftMargin + params.width)) {
            return false;
        }
        if (!(params.topMargin <= y && y < params.topMargin + params.height)) {
            return false;
        }
        return true;
    }

}
