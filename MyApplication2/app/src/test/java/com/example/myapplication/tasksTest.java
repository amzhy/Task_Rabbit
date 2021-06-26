package com.example.myapplication;

import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.*;

public class tasksTest {

    //public boolean checkTime(String taskTime, int deadline)
    tasks t = new tasks();
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    LocalDate localDate = LocalDate.now();

    DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("HH:mm");
    LocalDateTime now2 = LocalDateTime.now();
    String timeNow = dtf2.format(now2);

    @Test
    public void checkTimeDialog1() {
        String dt = dtf.format(localDate);
        assertTrue(t.checkTime(dt, 0));
        assertTrue(t.checkTime(dt, 1));
        System.out.println("                                                               DATE " + dt);
        System.out.println("                                                               DATE " + localDate);
        assertTrue(t.checkTime(dt, 2));
        assertTrue(t.checkTime(dt, 3));
        assertTrue(t.checkTime(dt, 4));
        assertTrue(t.checkTime(dt, 5));
        assertTrue(t.checkTime(dt, 6));
        assertTrue(t.checkTime(dt, 7));
    }

    @Test
    public void checkTimeDialog2() {
        String dt = dtf.format(localDate.plusDays(1));
        assertTrue(t.checkTime(dt, 0));
        System.out.println("                                                               DATE " + dt);
        assertTrue(t.checkTime(dt, 1));
        assertTrue(t.checkTime(dt, 2));
        assertTrue(t.checkTime(dt, 3));
        assertTrue(t.checkTime(dt, 4));
        assertTrue(t.checkTime(dt, 5));
        assertTrue(t.checkTime(dt, 6));
        assertTrue(t.checkTime(dt, 7));
    }

    @Test
    public void checkTimeDialog3() {
        String dt = dtf.format(localDate.plusDays(2));
        assertTrue(t.checkTime(dt, 0));
        System.out.println("                                                               DATE " + dt);
        assertFalse(t.checkTime(dt, 1));
        assertTrue(t.checkTime(dt, 2));
        assertTrue(t.checkTime(dt, 3));
        assertTrue(t.checkTime(dt, 4));
        assertTrue(t.checkTime(dt, 5));
        assertTrue(t.checkTime(dt, 6));
        assertTrue(t.checkTime(dt, 7));
    }
    @Test
    public void checkTimeDialog4() {
        String dt = dtf.format(localDate.plusDays(3));
        assertTrue(t.checkTime(dt, 0));
        System.out.println("                                                               DATE " + dt);
        assertFalse(t.checkTime(dt, 1));
        assertFalse(t.checkTime(dt, 2));
        assertTrue(t.checkTime(dt, 3));
        assertTrue(t.checkTime(dt, 4));
        assertTrue(t.checkTime(dt, 5));
        assertTrue(t.checkTime(dt, 6));
        assertTrue(t.checkTime(dt, 7));
    }
    @Test
    public void checkTimeDialog5() {
        String dt = dtf.format(localDate.plusDays(4));
        assertTrue(t.checkTime(dt, 0));
        System.out.println("                                                               DATE " + dt);
        assertFalse(t.checkTime(dt, 1));
        assertFalse(t.checkTime(dt, 2));
        assertFalse(t.checkTime(dt, 3));
        assertTrue(t.checkTime(dt, 4));
        assertTrue(t.checkTime(dt, 5));
        assertTrue(t.checkTime(dt, 6));
        assertTrue(t.checkTime(dt, 7));
    }

    @Test
    public void checkTimeDialog6() {
        String dt = dtf.format(localDate.plusDays(5));
        assertTrue(t.checkTime(dt, 0));
        System.out.println("                                                               DATE " + dt);
        assertFalse(t.checkTime(dt, 1));
        assertFalse(t.checkTime(dt, 2));
        assertFalse(t.checkTime(dt, 3));
        assertFalse(t.checkTime(dt, 4));
        assertTrue(t.checkTime(dt, 5));
        assertTrue(t.checkTime(dt, 6));
        assertTrue(t.checkTime(dt, 7));
    }

    @Test
    public void checkTime1() {
        HashMap<String, String> task = makeTask(1);
        assertTrue(t.checkTime(task));
    }

    @Test
    public void checkTime2() {
        HashMap<String, String> task = makeTask(-1);
        assertFalse(t.checkTime(task));
    }

    @Test
    public void checkTime3() {
        HashMap<String, String> task = makeTask(0);
        assertFalse(t.checkTime(task));
    }

    @Test
    public void checkTime4() {
        HashMap<String, String> task = makeTask(2);
        assertTrue(t.checkTime(task));
    }

    @Test
    public void checkTime5() {
        HashMap<String, String> task = makeTask(-2);
        assertFalse(t.checkTime(task));
    }


    private HashMap<String, String> makeTask(int i) {
        String dt = dtf.format(localDate.plusDays(i));
        String time = timeNow;
        HashMap<String, String> task = new HashMap<>();
        task.put("date", dt);
        task.put("time", time);
        ArrayList<String> arr = new ArrayList<>();
        arr.add("location");
        arr.add("title");
        arr.add("description");
        arr.add("price");
        arr.add("userId");
        arr.add("taskId");
        arr.add("taskerId");
        arr.add("category");
        for (String s: arr) {
            task.put(s, "test");
        }

        return task;
    }
}