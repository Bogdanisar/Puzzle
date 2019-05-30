package com.example.puzzle;

import com.example.puzzle.history.HistoryItem;
import com.example.puzzle.history.PieceGameHistory;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class PieceGameHistoryTest {
    @Test
    public void SquareGameHistory_hasCorrectEquals() {
        PieceGameHistory obj1, obj2;
        obj1 = new PieceGameHistory("Simple", 200L, 10, 1, 5, 5);
        obj2 = new PieceGameHistory("Simple", 200L, 10, 1, 5, 5);

        assertEquals(obj1, obj1);
        assertEquals(obj1, obj2);

        obj1 = new PieceGameHistory(null, 200L, 10, 1, 5, 5);
        obj2 = new PieceGameHistory("Simple", 200L, 10, 1, 5, 5);
        assertNotEquals(obj1, obj2);

        obj1 = new PieceGameHistory("Shell", 200L, 10, 1, 5, 5);
        obj2 = new PieceGameHistory(null, 200L, 10, 1, 5, 5);
        assertNotEquals(obj1, obj2);

        obj1 = new PieceGameHistory(null, 200L, 10, 1, 5, 5);
        obj2 = new PieceGameHistory(null, 200L, 10, 1, 5, 5);
        assertEquals(obj1, obj2);

        obj1 = new PieceGameHistory(null, 200L, 10, 1, 5, 5);
        obj2 = new PieceGameHistory("", 200L, 10, 1, 5, 5);
        assertEquals(obj1, obj2);

        obj1 = new PieceGameHistory("", 200L, 10, 1, 5, 5);
        obj2 = new PieceGameHistory("", 200L, 10, 1, 5, 5);
        assertEquals(obj1, obj2);

        obj1 = new PieceGameHistory("", 200L, 10, 1, 5, 5);
        obj2 = new PieceGameHistory(null, 200L, 10, 1, 5, 5);
        assertEquals(obj1, obj2);
    }

    @Test
    public void SquareGameHistory_isDifferent() {

        ArrayList<PieceGameHistory> list = new ArrayList<PieceGameHistory>();
        list.add(new PieceGameHistory("Simpled", 200L, 10, 1, 5, 5));
        list.add(new PieceGameHistory("Simple", 200L, 10, 1, 5, 5));
        list.add(new PieceGameHistory("Simple", 201L, 10, 1, 5, 5));
        list.add(new PieceGameHistory("Simple", 201L, 101, 1, 5, 5));
        list.add(new PieceGameHistory("Simple", 201L, 101, 12, 5, 5));
        list.add(new PieceGameHistory("Simple", 201L, 101, 12, 55, 5));
        list.add(new PieceGameHistory("Simple", 201L, 101, 12, 55, 6));

        for (int i = 0; i < list.size() - 1; ++i) {
            assertNotEquals(list.get(i), list.get(i + 1));
        }
    }



    @Test
    public void SquareGameHistory_doesCorrectSort() {
        PieceGameHistory obj1 = new PieceGameHistory("Simple", 3000L, 10, 1, 5, 5);
        PieceGameHistory obj2 = new PieceGameHistory("Simple", 10L, 10, 1, 5, 5);
        PieceGameHistory obj3 = new PieceGameHistory("Simple", 200L, 10, 1, 5, 5);

        ArrayList<PieceGameHistory> list = new ArrayList<PieceGameHistory>();
        list.add(obj1);
        list.add(obj2);
        list.add(obj3);

        Collections.sort(list);

        assertEquals(list.get(0), obj2);
        assertEquals(list.get(1), obj3);
        assertEquals(list.get(2), obj1);
    }

    @Test
    public void SquareGameHistory_isRebuildCorrectly() {
        PieceGameHistory obj1 = new PieceGameHistory("Simple", 3000L, 10, 1, 5, 5);
        PieceGameHistory obj2 = new PieceGameHistory("Shell", 3000L, 100, 1, 5, 55);
        PieceGameHistory rebuilt = PieceGameHistory.getInstance(obj1.toString());

        assertEquals(obj1, rebuilt);
        assertNotEquals(obj2, rebuilt);

        obj1 = new PieceGameHistory(null, 3000L, 100, 1, 5, 55);
        PieceGameHistory rebuilt1 = PieceGameHistory.getInstance(obj1.toString());
        assertEquals(obj1, rebuilt1);

        obj2 = new PieceGameHistory("", 3000L, 100, 1, 5, 55);
        PieceGameHistory rebuilt2 = PieceGameHistory.getInstance(obj1.toString());
        assertEquals(obj2, rebuilt2);

        assertEquals(rebuilt1, rebuilt2);
        assertEquals(rebuilt2, rebuilt1);
    }

    @Test
    public void SquareGameHistory_isAddedCorrectly() {
        // test1
        PieceGameHistory obj0 = new PieceGameHistory("Simple", 3000L, 10, 1, 5, 5);
        PieceGameHistory obj1 = new PieceGameHistory("Shell", 3000L, 100, 1, 5, 55);

        String data = "";
        data = HistoryItem.addInstanceToDataString(data, obj0);
        data = HistoryItem.addInstanceToDataString(data, obj1);

        List<PieceGameHistory> arr = PieceGameHistory.getInstanceArray(data);

        assertEquals(arr.size(), 2);
        assertEquals(obj0, arr.get(0));
        assertEquals(obj1, arr.get(1));


        // test2
        obj0 = new PieceGameHistory("Simple", 3000L, 10, 1, 5, 5);
        obj1 = new PieceGameHistory("Shell", 3000L, 100, 1, 5, 55);

        data = HistoryItem.addInstanceToDataString(null, obj0);
        data = HistoryItem.addInstanceToDataString(data, obj1);

        arr = PieceGameHistory.getInstanceArray(data);

        assertEquals(arr.size(), 2);
        assertEquals(obj0, arr.get(0));
        assertEquals(obj1, arr.get(1));


        // test3
        obj0 = new PieceGameHistory("Simple", 3000L, 10, 1, 5, 5);
        obj1 = new PieceGameHistory("Shell", 3000L, 100, 1, 5, 55);

        data = HistoryItem.addInstanceToDataString("", obj0);
        data = HistoryItem.addInstanceToDataString(data, obj1);

        arr = PieceGameHistory.getInstanceArray(data);

        assertEquals(arr.size(), 2);
        assertEquals(obj0, arr.get(0));
        assertEquals(obj1, arr.get(1));
    }
}
