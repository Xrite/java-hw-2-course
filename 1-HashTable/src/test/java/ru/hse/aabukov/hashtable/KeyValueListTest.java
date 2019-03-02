package ru.hse.aabukov.hashtable;

import org.junit.jupiter.api.Test;

import java.security.Key;

import static org.junit.jupiter.api.Assertions.*;

class KeyValueListTest {

    @Test
    void popFront() {
        KeyValueList list = new KeyValueList();
        list.put("", "");
        list.popFront();
        assertTrue(list.empty());
        list.put("", "");
        list.put("", "");
        assertFalse(list.empty());
    }

    @Test
    void frontKey() {
        KeyValueList list = new KeyValueList();
        assertNull(list.frontKey());
        list.put("1", "2");
        assertEquals("1", list.frontKey());
        list.put("3", "4");
        assertEquals("3", list.frontKey());
        list.put("5", "6");
        assertEquals("5", list.frontKey());
    }

    @Test
    void frontValue() {
        KeyValueList list = new KeyValueList();
        assertNull(list.frontKey());
        list.put("1", "2");
        assertEquals("2", list.frontValue());
        list.put("3", "4");
        assertEquals("4", list.frontValue());
        list.put("5", "6");
        assertEquals("6", list.frontValue());
    }

    @Test
    void empty() {
        KeyValueList list = new KeyValueList();
        assertTrue(list.empty());
        list.put("", "");
        assertFalse(list.empty());
        list.put("", "");
        assertFalse(list.empty());
    }

    @Test
    void contains() {
        KeyValueList list = new KeyValueList();
        assertFalse(list.contains(""));
        list.put("1", "1");
        list.put("2", "2");
        list.put("3", "3");
        assertTrue(list.contains("1"));
        assertTrue(list.contains("2"));
        assertTrue(list.contains("3"));
        assertFalse(list.contains("0"));
        assertFalse(list.contains("100"));
    }

    @Test
    void get() {
        KeyValueList list = new KeyValueList();
        assertNull(list.get(""));
        list.put("1", "2");
        assertNull(list.get(""));
        assertEquals("2", list.get("1"));
        list.put("3", "4");
        list.put("5", "6");
        assertEquals("2", list.get("1"));
        assertEquals("4", list.get("3"));
        assertEquals("6", list.get("5"));
    }

    @Test
    void put() {
        KeyValueList list = new KeyValueList();
        assertNull(list.put("1", "2"));
        assertEquals("2", list.put("1", "3"));
        assertNull(list.put("2", "4"));
        assertEquals("4", list.put("2", "1"));
        assertNull(list.put("3", "6"));
        assertEquals("6", list.put("3", "1"));
    }

    @Test
    void remove() {
        KeyValueList list = new KeyValueList();
        list.put("1", "2");
        list.put("3", "4");
        list.put("5", "6");
        assertNull(list.remove("2"));
        assertEquals("2", list.remove("1"));
        assertNull(list.remove("1"));
        assertEquals("4", list.remove("3"));
        assertNull(list.remove("3"));
        assertEquals("6", list.remove("5"));
        assertNull(list.remove("5"));
    }
}