package com.example.myapplication;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ProfileFragmentTest {
    /*
    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }
    */
    ProfileFragment f  = new ProfileFragment();
    @Test
    public void checkInputTest1() {
        assertTrue(f.checkInput("hello", "12345678", "blk 123"));
    }

    @Test
    public void checkInputTest2() {
        assertFalse(f.checkInput("", "12345678", "blk 123"));
    }

    @Test
    public void checkInputTest3() {
        assertFalse(f.checkInput("hello", "12345678", ""));
    }

    @Test
    public void checkInputTest4() {
        assertFalse(f.checkInput("", "12345678", ""));
    }

    @Test
    public void checkInputTest5() {
        assertFalse(f.checkInput("a", "1234", "blk"));
    }

    @Test
    public void checkInputTest6() {
        assertFalse(f.checkInput("a", "1234567", ""));
    }

    @Test
    public void checkInputTest7() {
        assertFalse(f.checkInput("ababababababababababa", "12365678", "p"));
    }
}