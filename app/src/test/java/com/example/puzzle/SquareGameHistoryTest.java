package com.example.puzzle;

import com.example.puzzle.history.HistoryItem;
import com.example.puzzle.history.SquareGameHistory;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.Assert.*;

public class SquareGameHistoryTest {

    @Test
    public void SquareGameHistory_hasCorrectEquals() {
        SquareGameHistory obj1 = new SquareGameHistory("Simple", 200L, 10, 1, 5, 5);
        SquareGameHistory obj2 = new SquareGameHistory("Simple", 200L, 10, 1, 5, 5);

        assertEquals(obj1, obj1);
        assertEquals(obj1, obj2);

        ArrayList<SquareGameHistory> list = new ArrayList<SquareGameHistory>();
        list.add(new SquareGameHistory("Simpled", 200L, 10, 1, 5, 5));
        list.add(new SquareGameHistory("Simple", 200L, 10, 1, 5, 5));
        list.add(new SquareGameHistory("Simple", 201L, 10, 1, 5, 5));
        list.add(new SquareGameHistory("Simple", 201L, 101, 1, 5, 5));
        list.add(new SquareGameHistory("Simple", 201L, 101, 12, 5, 5));
        list.add(new SquareGameHistory("Simple", 201L, 101, 12, 55, 5));
        list.add(new SquareGameHistory("Simple", 201L, 101, 12, 55, 6));

        for (int i = 0; i < list.size() - 1; ++i) {
            assertNotEquals(list.get(i), list.get(i + 1));
        }

        obj1 = new SquareGameHistory(null, 200L, 10, 1, 5, 5);
        obj2 = new SquareGameHistory("Simple", 200L, 10, 1, 5, 5);
        assertNotEquals(obj1, obj2);

        obj1 = new SquareGameHistory("Shell", 200L, 10, 1, 5, 5);
        obj2 = new SquareGameHistory(null, 200L, 10, 1, 5, 5);
        assertNotEquals(obj1, obj2);

        obj1 = new SquareGameHistory(null, 200L, 10, 1, 5, 5);
        obj2 = new SquareGameHistory(null, 200L, 10, 1, 5, 5);
        assertEquals(obj1, obj2);

        obj1 = new SquareGameHistory(null, 200L, 10, 1, 5, 5);
        obj2 = new SquareGameHistory("", 200L, 10, 1, 5, 5);
        assertEquals(obj1, obj2);

        obj1 = new SquareGameHistory("", 200L, 10, 1, 5, 5);
        obj2 = new SquareGameHistory("", 200L, 10, 1, 5, 5);
        assertEquals(obj1, obj2);

        obj1 = new SquareGameHistory("", 200L, 10, 1, 5, 5);
        obj2 = new SquareGameHistory(null, 200L, 10, 1, 5, 5);
        assertEquals(obj1, obj2);
    }

    @Test
    public void SquareGameHistory_doesCorrectSort() {
        SquareGameHistory obj1 = new SquareGameHistory("Simple", 3000L, 10, 1, 5, 5);
        SquareGameHistory obj2 = new SquareGameHistory("Simple", 10L, 10, 1, 5, 5);
        SquareGameHistory obj3 = new SquareGameHistory("Simple", 200L, 10, 1, 5, 5);

        ArrayList<SquareGameHistory> list = new ArrayList<SquareGameHistory>();
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
        SquareGameHistory obj1 = new SquareGameHistory("Simple", 3000L, 10, 1, 5, 5);
        SquareGameHistory obj2 = new SquareGameHistory("Shell", 3000L, 100, 1, 5, 55);
        SquareGameHistory rebuilt = SquareGameHistory.getInstance(obj1.toString());

        assertEquals(obj1, rebuilt);
        assertNotEquals(obj2, rebuilt);

        obj1 = new SquareGameHistory(null, 3000L, 100, 1, 5, 55);
        SquareGameHistory rebuilt1 = SquareGameHistory.getInstance(obj1.toString());
        assertEquals(obj1, rebuilt1);

        obj2 = new SquareGameHistory("", 3000L, 100, 1, 5, 55);
        SquareGameHistory rebuilt2 = SquareGameHistory.getInstance(obj1.toString());
        assertEquals(obj2, rebuilt2);

        assertEquals(rebuilt1, rebuilt2);
        assertEquals(rebuilt2, rebuilt1);
    }

    @Test
    public void SquareGameHistory_isAddedCorrectly() {
        // test1
        SquareGameHistory obj0 = new SquareGameHistory("Simple", 3000L, 10, 1, 5, 5);
        SquareGameHistory obj1 = new SquareGameHistory("Shell", 3000L, 100, 1, 5, 55);

        String data = "";
        data = HistoryItem.addInstanceToDataString(data, obj0);
        data = HistoryItem.addInstanceToDataString(data, obj1);

        SquareGameHistory[] arr = SquareGameHistory.getInstanceArray(data);

        assertEquals(arr.length, 2);
        assertEquals(obj0, arr[0]);
        assertEquals(obj1, arr[1]);


        // test2
        obj0 = new SquareGameHistory("Simple", 3000L, 10, 1, 5, 5);
        obj1 = new SquareGameHistory("Shell", 3000L, 100, 1, 5, 55);

        data = HistoryItem.addInstanceToDataString(null, obj0);
        data = HistoryItem.addInstanceToDataString(data, obj1);

        arr = SquareGameHistory.getInstanceArray(data);

        assertEquals(arr.length, 2);
        assertEquals(obj0, arr[0]);
        assertEquals(obj1, arr[1]);


        // test3
        obj0 = new SquareGameHistory("Simple", 3000L, 10, 1, 5, 5);
        obj1 = new SquareGameHistory("Shell", 3000L, 100, 1, 5, 55);

        data = HistoryItem.addInstanceToDataString("", obj0);
        data = HistoryItem.addInstanceToDataString(data, obj1);

        arr = SquareGameHistory.getInstanceArray(data);

        assertEquals(arr.length, 2);
        assertEquals(obj0, arr[0]);
        assertEquals(obj1, arr[1]);
    }
}
