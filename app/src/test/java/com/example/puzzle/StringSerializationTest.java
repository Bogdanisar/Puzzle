package com.example.puzzle;


import android.os.SystemClock;

import org.junit.Test;
import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;
import java.io.*;


class TestClass implements Serializable {
    int x, y;
    double d;
    int[] integers;
    ArrayList<Integer> list;

    TestClass(int x, int y, double d) {
        this.x = x;
        this.y = y;
        this.d = d;
        this.integers = new int[]{1, 2, 3};
        this.list = new ArrayList<>();

        for (int i = 0; i < 5; ++i) {
            this.list.add(i * 10);
        }
    }

    @Override
    public String toString() {
        String str = "";
        str += "x = " + this.x + "; ";
        str += "y = " + this.y + "; ";
        str += "d = " + this.d + "; ";
        str += "integers = " + Arrays.toString(this.integers) + "; ";
        str += "list = " + this.list.toString() + "; ";
        return str;
    }
}

public class StringSerializationTest {
    @Test
    public void testPrint() {
        while (true) {
            System.out.println("THIS A TEST");
        }
    }

    @Test
    public void testFalse() {
        assertTrue(false);
    }

    @Test
    public void testSerialization() {
        TestClass myObj = new TestClass(2, 3, 3.14);
        TestClass retrievedObject = null;
        String redisString = null;

        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream so = new ObjectOutputStream(bo);
            so.writeObject(myObj);
            so.flush();
            redisString = new String(Base64.getEncoder().encode(bo.toByteArray()));
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        try {
            byte b[] = Base64.getDecoder().decode(redisString.getBytes());
            ByteArrayInputStream bi = new ByteArrayInputStream(b);
            ObjectInputStream si = new ObjectInputStream(bi);

            retrievedObject = (TestClass)si.readObject();

            // List<String> stringList2 = (List<String>)si.readObject();
            // System.out.println(stringList2.get(1));
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(myObj);
        System.out.println(retrievedObject);

        assertTrue(myObj.toString().equals(retrievedObject.toString()));
//        assertTrue(false);
    }
}
