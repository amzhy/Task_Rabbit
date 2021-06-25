package com.example.myapplication;

import org.junit.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.*;

public class tasksTest {

    //public boolean checkTime(String taskTime, int deadline)
    tasks t = new tasks();
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    LocalDate localDate = LocalDate.now();

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
}