package com.example.puzzle;

import android.graphics.Bitmap;

import com.example.puzzle.squareGame.SGPiece;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class Utils {

    public static class Pair<T> {
        public T x, y;
        Pair(T x, T y) {
            this.x = x;
            this.y = y;
        }
    }



    public static Bitmap scaleBitmapAndRecycle(Bitmap original, int newWidth, int newHeight) {
        Bitmap scaledOriginal = Bitmap.createScaledBitmap(original, newWidth, newHeight, true);
        if (original != scaledOriginal) {
            original.recycle();
        }

        return scaledOriginal;
    }

    public static int getMargin(int totalSize, int pieceSize, double sizeRatio) {
        int exclusiveLimit = totalSize - pieceSize + 1;
        int margin = (int)(sizeRatio * exclusiveLimit);
        return margin;
    }



    public static int getIndexDistance(int i0, int j0, int i1, int j1) {
        return Math.abs(i0 - i1) + Math.abs(j0 - j1);
    }

    public static int getIndexDistance(Pair<Integer> p, Pair<Integer> r) {
        return Math.abs(p.x - r.x) + Math.abs(p.y - r.y);
    }

    public static boolean indexIsNextToCorner(int i, int j, int numLines, int numColumns) {
        ArrayList< Pair<Integer> > positions = new ArrayList<>();
        positions.add( new Pair<>(0, 0) );
        positions.add( new Pair<>(0, numColumns - 1) );
        positions.add( new Pair<>(numLines - 1, 0) );
        positions.add( new Pair<>(numLines - 1, numColumns - 1) );

        Pair<Integer> curr = new Pair<>(i, j);
        for (Pair<Integer> pos : positions) {
            if (Utils.getIndexDistance(curr, pos) == 1) {
                return true;
            }
        }

        return false;
    }



    public static int getPositionFromIndexes(int i, int j, int numColumns) {
        return i * numColumns + j;
    }

    public static Pair<Integer> getIndexesFromPosition(int pos, int numColumns) {
        int i = pos / numColumns;
        int j = pos % numColumns;
        return new Pair<>(i, j);
    }



    public static void bringPiecesToFrontAscending(List<SGPiece> list) {
        ListIterator<SGPiece> it = list.listIterator(list.size());

        while (it.hasPrevious()) {
            SGPiece currentPiece = it.previous();
            currentPiece.imageView.bringToFront();
        }
    }
}
