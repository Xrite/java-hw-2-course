import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LinkedHashMapTest {
    LinkedHashMap<Integer, Integer> generateId(int count) {
        var hashTable = new LinkedHashMap<Integer, Integer>();
        for (Integer i = 1; i <= count; i++) {
            hashTable.put(i, i);
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
    void get() {
        var oneToTen = generateId(10);
        assertNull(oneToTen.get(11));
        assertNull(oneToTen.get(228));
        for (Integer i = 1; i <= 10; i++) {
            assertEquals(i, oneToTen.get(i));
        }
    }

    @Test
    void put() {
        var oneToTen = new LinkedHashMap<Integer, Integer>();
        for (Integer i = 1; i <= 10; i++) {
            assertNull(oneToTen.put(i, i));
        }
        for (Integer i = 1; i <= 10; i++) {
            Integer j = 10 - i;
            assertEquals(i, oneToTen.put(i, j));
        }
        for (Integer i = 1; i <= 10; i++) {
            Integer j = 10 - i;
            assertEquals(j, oneToTen.put(i, i));
        }
    }

    @Test
    void remove() {
        var oneToTen = generateId(10);
        for (Integer i = 1; i <= 10; i++) {
            assertEquals(i, oneToTen.remove(i));
        }
        for (Integer i = 1; i <= 10; i++) {
            assertNull(oneToTen.remove(i));
        }
        oneToTen = generateId(10);
        for (Integer i = 1; i <= 10; i++) {
            assertEquals(i, oneToTen.remove(i));
            assertNull(oneToTen.remove(i));
        }

    }

    @Test
    void clear() {
        var oneToTen = generateId(10);
        oneToTen.clear();
        for (Integer i = 1; i <= 10; i++) {
            assertNull(oneToTen.get(i));
        }
    }
}