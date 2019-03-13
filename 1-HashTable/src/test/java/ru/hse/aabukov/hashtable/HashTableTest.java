package ru.hse.aabukov.hashtable;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HashTableTest {

    HashTable generateId(int count) {
        HashTable hashTable = new HashTable();
        for (Integer i = 1; i <= count; i++) {
            hashTable.put(i.toString(), i.toString());
        }
        return hashTable;
    }

    @Test
    void size() {
        assertEquals(10, generateId(10).size());
        assertEquals(3, generateId(3).size());
        assertEquals(100, generateId(100).size());
    }

    @Test
    void contains() {
        HashTable oneToTen = generateId(10);
        for(Integer i = 1; i <= 10; i++) {
            assertTrue(oneToTen.contains(i.toString()));
        }
        assertFalse(oneToTen.contains("0"));
        assertFalse(oneToTen.contains("11"));
        assertFalse(oneToTen.contains("ab"));
    }

    @Test
    void get() {
        HashTable oneToTen = generateId(10);
        assertNull(oneToTen.get("11"));
        assertNull(oneToTen.get("228"));
        for(Integer i = 1; i <= 10; i++) {
            assertEquals(i.toString(), oneToTen.get(i.toString()));
        }
    }

    @Test
    void put() {
        HashTable oneToTen = new HashTable();
        for(Integer i = 1; i <= 10; i++) {
            assertNull(oneToTen.put(i.toString(), i.toString()));
        }
        for(Integer i = 1; i <= 10; i++) {
            Integer j = 10 - i;
            assertEquals(i.toString(), oneToTen.put(i.toString(), j.toString()));
        }
        for(Integer i = 1; i <= 10; i++) {
            Integer j = 10 - i;
            assertEquals(j.toString(), oneToTen.put(i.toString(), i.toString()));
        }
    }

    @Test
    void remove() {
        HashTable oneToTen = generateId(10);
        for(Integer i = 1; i <= 10; i++) {
            assertEquals(i.toString(), oneToTen.remove(i.toString()));
        }
        for(Integer i = 1; i <= 10; i++) {
            assertNull(oneToTen.remove(i.toString()));
        }
        oneToTen = generateId(10);
        for(Integer i = 1; i <= 10; i++) {
            assertEquals(i.toString(), oneToTen.remove(i.toString()));
            assertNull(oneToTen.remove(i.toString()));
        }

    }

    @Test
    void clear() {
        HashTable oneToTen = generateId(10);
        oneToTen.clear();
        for(Integer i = 1; i <= 10; i++) {
            assertNull(oneToTen.get(i.toString()));
        }
    }
}