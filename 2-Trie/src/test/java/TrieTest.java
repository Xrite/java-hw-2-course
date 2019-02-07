import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class TrieTest {
    private String[] simpleStrings = {"aba", "caba", "abab", "abacaba", "abracadabra", "baca", ""};

    private Trie simpleTrie() {
        var trie = new Trie();
        trie.addAll(Arrays.asList(simpleStrings));
        return trie;
    }

    @Test
    void add() {
        var empty = new Trie();
        assertTrue(empty.add("a"));
        assertTrue(empty.contains("a"));
        assertTrue(empty.add(""));
        assertFalse(empty.add(""));
        assertTrue(empty.contains("a"));
        assertTrue(empty.contains(""));
        assertTrue(empty.add("b"));
        assertFalse(empty.add("b"));
        assertTrue(empty.contains("b"));
        assertTrue(empty.contains("a"));
        assertTrue(empty.contains(""));
        assertTrue(empty.add("caba"));
        assertTrue(empty.contains("caba"));
        assertTrue(empty.contains("b"));
        assertTrue(empty.contains("a"));
        assertTrue(empty.contains(""));
    }

    @Test
    void contains() {
        var empty = new Trie();
        assertFalse(empty.contains(""));
        var simple = simpleTrie();
        assertTrue(simple.contains("aba"));
        assertTrue(simple.contains("caba"));
        assertTrue(simple.contains("abab"));
        assertTrue(simple.contains("abacaba"));
        assertTrue(simple.contains("abracadabra"));
        assertTrue(simple.contains("baca"));
        assertTrue(simple.contains(""));
    }

    @Test
    void remove() {
        var simple = simpleTrie();
        assertTrue(simple.remove("aba"));
        assertFalse(simple.remove("aba"));
        assertFalse(simple.contains("aba"));
        assertTrue(simple.remove("caba"));
        assertFalse(simple.remove("caba"));
        assertFalse(simple.contains("caba"));
        assertTrue(simple.remove("abab"));
        assertFalse(simple.remove("abab"));
        assertFalse(simple.contains("abab"));
        assertTrue(simple.remove("abacaba"));
        assertFalse(simple.remove("abacaba"));
        assertFalse(simple.contains("abacaba"));
        assertTrue(simple.remove("abracadabra"));
        assertFalse(simple.remove("abracadabra"));
        assertFalse(simple.contains("abracadabra"));
        assertFalse(simple.remove("cabab"));
        assertTrue(simple.remove(""));
        assertFalse(simple.remove(""));
        assertFalse(simple.contains(""));
        assertTrue(simple.remove("baca"));
        assertFalse(simple.remove("baca"));
        assertFalse(simple.contains("baca"));
    }

    @Test
    void size() {
        var simple = simpleTrie();
        assertEquals(7, simple.size());
        simple.remove("aba");
        assertEquals(6, simple.size());
        simple.remove("caba");
        assertEquals(5, simple.size());
        simple.remove("abab");
        assertEquals(4, simple.size());
        simple.remove("abacaba");
        assertEquals(3, simple.size());
        simple.remove("abracadabra");
        assertEquals(2, simple.size());
        simple.remove("baca");
        assertEquals(1, simple.size());
        simple.remove("");
        assertEquals(0, simple.size());
        simple.add("aba");
        assertEquals(1, simple.size());
        simple.add("abab");
        assertEquals(2, simple.size());
        simple.add("aba");
        assertEquals(2, simple.size());
        simple.add("caba");
        assertEquals(3, simple.size());
    }

    @Test
    void howManyStartWithPrefix() {
        var simple = simpleTrie();
        assertEquals(7, simple.howManyStartWithPrefix(""));
        assertEquals(4, simple.howManyStartWithPrefix("a"));
        assertEquals(4, simple.howManyStartWithPrefix("ab"));
        assertEquals(0, simple.howManyStartWithPrefix("x"));
        assertEquals(1, simple.howManyStartWithPrefix("b"));
        assertEquals(1, simple.howManyStartWithPrefix("baca"));
        assertEquals(1, simple.howManyStartWithPrefix("abra"));
        assertEquals(3, simple.howManyStartWithPrefix("aba"));
    }

    void checkIdentity(Trie trie) throws IOException {
        var output = new ByteArrayOutputStream();
        trie.serialize(output);
        var input = new ByteArrayInputStream(output.toByteArray());
        var newTrie = new Trie();
        newTrie.deserialize(input);
        assertEquals(trie, newTrie);
    }

    @Test
    void serializeEmpty() throws IOException {
        var empty = new Trie();
        checkIdentity(empty);
    }

    @Test
    void serializeNotEmpty() throws IOException {
        var simple = simpleTrie();
        checkIdentity(simple);
    }

    @Test
    void equals() {
        var first = new Trie();
        var second = new Trie();
        assertNotEquals(first, null);
        assertEquals(first, second);
        for(var string : simpleStrings) {
            first.add(string);
            assertNotEquals(first, second);
            second.add(string);
            assertEquals(first, second);
        }
        second = new Trie();
        for(var string : simpleStrings) {
            assertNotEquals(first, second);
            second.add(string);
        }
        assertEquals(first, second);
    }
}