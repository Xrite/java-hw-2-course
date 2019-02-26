package ru.hse.aabukov.mytreeset;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.api.function.ThrowingSupplier;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

class TreapTest {

    Treap<Integer> numbers(int bound) {
        var treap = new Treap<Integer>();
        for(int i = 0; i < bound; i++) {
            treap.add(i);
        }
        return treap;
    }

    @Test
    void testNotComparableObjects() {
        var objects = new Treap<Object>();
        Executable newObject = () -> objects.add(new Object());
        assertDoesNotThrow(newObject);
        assertThrows(IllegalArgumentException.class, newObject);
    }

    @Test
    void iterator() {
        var treap = numbers(10);
        var it = treap.iterator();
        for(int i = 0; i < 10 ; i++) {
            assertTrue(it.hasNext());
            assertEquals(Integer.valueOf(i), it.next());
        }
        assertFalse(it.hasNext());
        assertFalse(it.hasNext());
        assertThrows(NoSuchElementException.class, it::next);
    }

    @Test
    void size() {
        assertEquals(10, numbers(10).size());
        assertEquals(0, new Treap<Object>().size());
    }

    @Test
    void descendingIterator() {
        var simple = numbers(10);
        var it = simple.descendingIterator();
        for(int i = 9; i >= 0; i--) {
            assertTrue(it.hasNext());
            assertEquals(Integer.valueOf(i), it.next());
        }
        assertFalse(it.hasNext());
        assertThrows(NoSuchElementException.class, it::next);
    }

    @Test
    void descendingSet() {
        var trie = numbers(10);
        var reversed = trie.descendingSet();
        trie.add(10);
        assertEquals(Integer.valueOf(10), reversed.first());
        trie.add(20);
        trie.add(30);
        trie.add(40);
        assertEquals(14, reversed.size());
        assertTrue(reversed.contains(10));
        assertTrue(reversed.contains(20));
        assertTrue(reversed.contains(30));
        assertTrue(reversed.contains(40));
        assertTrue(reversed.contains(5));
        assertFalse(reversed.add(20));
        assertTrue(reversed.add(-1));
        assertTrue(trie.contains(-1));
        assertTrue(reversed.remove(10));
        assertFalse(trie.contains(10));
    }

    @Test
    void first() {
        var treap = numbers(5);
        assertEquals(Integer.valueOf(0), treap.first());
        treap.remove(0);
        treap.remove(1);
        assertEquals(Integer.valueOf(2), treap.first());
        treap.remove(2);
        treap.remove(3);
        assertEquals(Integer.valueOf(4), treap.first());
        treap.remove(4);
        assertThrows(NoSuchElementException.class, treap::first);
    }

    @Test
    void lower() {
        var treap = new Treap<Integer>();
        treap.add(1);
        assertThrows(NullPointerException.class, () -> treap.lower(null));
        assertNull(treap.lower(0));
        assertNull(treap.lower(1));
        assertEquals(Integer.valueOf(1), treap.lower(2));
        treap.add(2);
        assertEquals(Integer.valueOf(1), treap.lower(2));
        assertEquals(Integer.valueOf(2), treap.lower(3));
        treap.add(100);
        assertEquals(Integer.valueOf(2), treap.lower(100));
        assertEquals(Integer.valueOf(100), treap.lower(100000));
    }

    @Test
    void floor() {
        var treap = new Treap<Integer>();
        treap.add(1);
        assertThrows(NullPointerException.class, () -> treap.floor(null));
        assertEquals(Integer.valueOf(1), treap.floor(1));
        treap.add(2);
        assertEquals(Integer.valueOf(2), treap.floor(2));
        assertEquals(Integer.valueOf(2), treap.floor(3));
        treap.add(100);
        assertEquals(Integer.valueOf(100), treap.floor(100));
        assertEquals(Integer.valueOf(100), treap.floor(100000));
    }

    @Test
    void checkInvalidation() {
        var treap = numbers(10);
        var it = treap.iterator();
        var reversed = treap.descendingIterator();
        treap.add(10);
        assertThrows(ConcurrentModificationException.class, it::next);
        assertThrows(ConcurrentModificationException.class, it::hasNext);
        assertThrows(ConcurrentModificationException.class, reversed::next);
        assertThrows(ConcurrentModificationException.class, reversed::hasNext);
        it = treap.iterator();
        treap.lower(4);
        assertDoesNotThrow(it::next);
        assertDoesNotThrow(it::hasNext);
        treap.floor(4);
        assertDoesNotThrow(it::next);
        assertDoesNotThrow(it::hasNext);
        assertTrue(treap.contains(5));
        assertDoesNotThrow(it::next);
        assertDoesNotThrow(it::hasNext);
        treap.first();
        assertDoesNotThrow(it::next);
        assertDoesNotThrow(it::hasNext);
        treap.descendingSet();
        assertDoesNotThrow(it::next);
        assertDoesNotThrow(it::hasNext);
        treap.add(5);
        assertDoesNotThrow(it::next);
        assertDoesNotThrow(it::hasNext);
        treap.remove(100);
        assertDoesNotThrow(it::next);
        assertDoesNotThrow(it::hasNext);
        treap.remove(5);
        assertThrows(ConcurrentModificationException.class, it::next);
        assertThrows(ConcurrentModificationException.class, it::hasNext);
    }

}