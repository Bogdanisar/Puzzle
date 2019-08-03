package com.example.puzzle;

public interface Constants {
    String COMMON_TAG = "puzzletag_";
    String SEPARATOR = "===================================================================";

    // history keys
    String KEY_HISTORY_PIECEGAME = "PieceGameHistory";

    int[] dx = new int[] {-1, 0, +1, 0};
    int[] dy = new int[] {0, +1, 0, -1};

    int[] di = new int[] {-1, 0, +1, 0};
    int[] dj = new int[] {0, +1, 0, -1};
}
