import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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
        for (int i = simpleStrings.length; i > 0; i--) {
            assertEquals(i, simple.size());
            simple.remove(simpleStrings[i - 1]);
        }
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
    void checkSerializeEmpty() throws IOException {
        var empty = new Trie();
        var output = new ByteArrayOutputStream();
        var dataOutput = new DataOutputStream(output);
        dataOutput.writeInt(0);
        dataOutput.writeBoolean(false);
        dataOutput.writeInt(0);
        dataOutput.writeInt(0);
        var trieOutput = new ByteArrayOutputStream();
        empty.serialize(trieOutput);
        assertEquals(trieOutput.toString(), output.toString());
    }

    @Test
    void checkSerializeNotEmpty() throws IOException {
        var trie = new Trie();
        trie.add("aba");
        var output = new ByteArrayOutputStream();
        var dataOutput = new DataOutputStream(output);
        dataOutput.writeInt(1);
        dataOutput.writeBoolean(false);
        dataOutput.writeInt(1);
        dataOutput.writeInt(1);
        dataOutput.writeChar('a');
        dataOutput.writeBoolean(false);
        dataOutput.writeInt(1);
        dataOutput.writeInt(1);
        dataOutput.writeChar('b');
        dataOutput.writeBoolean(false);
        dataOutput.writeInt(1);
        dataOutput.writeInt(1);
        dataOutput.writeChar('a');
        dataOutput.writeBoolean(true);
        dataOutput.writeInt(1);
        dataOutput.writeInt(0);
        var trieOutput = new ByteArrayOutputStream();
        trie.serialize(trieOutput);
        assertEquals(trieOutput.toString(), output.toString());
    }

    @Test
    void checkDeserializeEmpty() throws IOException {
        var empty = new Trie();
        var output = new ByteArrayOutputStream();
        var dataOutput = new DataOutputStream(output);
        dataOutput.writeInt(0);
        dataOutput.writeBoolean(false);
        dataOutput.writeInt(0);
        dataOutput.writeInt(0);
        var input = new ByteArrayInputStream(output.toByteArray());
        var readTrie = new Trie();
        readTrie.deserialize(input);
        assertEquals(empty, readTrie);
    }

    @Test
    void checkDeserializeNotEmpty() throws IOException {
        var trie = new Trie();
        trie.add("cab");
        var output = new ByteArrayOutputStream();
        var dataOutput = new DataOutputStream(output);
        dataOutput.writeInt(1);
        dataOutput.writeBoolean(false);
        dataOutput.writeInt(1);
        dataOutput.writeInt(1);
        dataOutput.writeChar('c');
        dataOutput.writeBoolean(false);
        dataOutput.writeInt(1);
        dataOutput.writeInt(1);
        dataOutput.writeChar('a');
        dataOutput.writeBoolean(false);
        dataOutput.writeInt(1);
        dataOutput.writeInt(1);
        dataOutput.writeChar('b');
        dataOutput.writeBoolean(true);
        dataOutput.writeInt(1);
        dataOutput.writeInt(0);
        var input = new ByteArrayInputStream(output.toByteArray());
        var readTrie = new Trie();
        readTrie.deserialize(input);
        assertEquals(trie, readTrie);
    }

    @Test
    void equals() {
        var first = new Trie();
        var second = new Trie();
        assertNotEquals(first, null);
        assertEquals(first, second);
        for (var string : simpleStrings) {
            first.add(string);
            assertNotEquals(first, second);
            second.add(string);
            assertEquals(first, second);
        }
        second = new Trie();
        for (var string : simpleStrings) {
            assertNotEquals(first, second);
            second.add(string);
        }
        assertEquals(first, second);
    }

    @Test
    void removeAll() {
        var trie = simpleTrie();
        trie.removeAll(Arrays.asList(simpleStrings));
        assertEquals(0, trie.size());
    }

    @Test
    void addAll() {
        var empty = new Trie();
        var correct = new Trie();
        for(var string : simpleStrings) {
            correct.add(string);
        }
        empty.addAll(Arrays.asList(simpleStrings));
        assertEquals(correct, empty);
    }
}