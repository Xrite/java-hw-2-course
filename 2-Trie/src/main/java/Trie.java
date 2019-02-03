import java.io.*;
import java.util.HashMap;

/** A data structure that implements a set of strings using tree-like finite automaton */
public class Trie {
    public Trie() {
        size = 0;
        root = new Node();
    }

    private class Node {
        private boolean terminal = false;
        private int terminalSubtreeSum = 0;
        private HashMap<Character, Node> transition;

        private Node getTransition(char c) {
            return transition.get(c);
        }

        private Node addTransition(char c) {
            var newNode = new Node();
            transition.put(c, newNode);
            return newNode;
        }

        private void removeTransition(char c) {
            transition.remove(c);
        }

        private void serialize(DataOutputStream out) throws IOException {
            out.writeBoolean(terminal);
            out.writeInt(terminalSubtreeSum);
            out.writeInt(transition.size());
            for (var entry : transition.entrySet()) {
                out.writeChar(entry.getKey());
                entry.getValue().serialize(out);
            }
        }

        private void deserialize(DataInputStream in) throws IOException {
            terminal = in.readBoolean();
            terminalSubtreeSum = in.readInt();
            int transitionsNumber = in.readInt();
            transition.clear();
            for (int i = 0; i < transitionsNumber; i++) {
                Character edge = in.readChar();
                Node child = new Node();
                child.deserialize(in);
                transition.put(edge, child);
            }
        }
    }

    private int size;
    private Node root;

    /**
     * Adds a string into the trie
     * @param element a string to add into the trie
     * @return True if the trie did not contain the given string, false otherwise
     */
    public boolean add(String element) {
        if(contains(element)) {
            return false;
        }
        size++;
        var currentNode = root;
        for (char c: element.toCharArray()) {
            if (currentNode.getTransition(c) == null) {
                currentNode.addTransition(c);
            }
            currentNode.terminalSubtreeSum++;
            currentNode = currentNode.getTransition(c);
        }
        currentNode.terminal = true;
        currentNode.terminalSubtreeSum++;
        return true;
    }

    /**
     * Checks whether a string contains in the trie
     * @param element a string to check
     * @return True if the given string contains in the trie
     */
    public boolean contains(String element) {
        var currentnode = root;
        for (char c: element.toCharArray()) {
            if (currentnode.getTransition(c) == null) {
                return false;
            }
            currentnode = currentnode.getTransition(c);
        }
        return currentnode.terminal;
    }

    /**
     * Removes a string from the trie
     * @param element a string to remove
     * @return true if the string was in the trie, false otherwise
     */
    public boolean remove(String element) {
        if(!contains(element)) {
            return false;
        }
        size--;
        var currentNode = root;
        currentNode.terminalSubtreeSum--;
        for (char c: element.toCharArray()) {
            var parent = currentNode;
            currentNode = currentNode.getTransition(c);
            currentNode.terminalSubtreeSum--;
            if(currentNode.terminalSubtreeSum == 0) {
                parent.removeTransition(c);
                return true;
            }
        }
        currentNode.terminal = false;
        return true;
    }

    /** Returns a number of strings in the trie */
    public int size() {
        return size;
    }

    /**
     * Counts a number of strings in the trie starts with the given prefix
     * @param prefix a prefix
     */
    public int howManyStartWithPrefix(String prefix) {
        var currentnode = root;
        for (char c: prefix.toCharArray()) {
            if (currentnode.getTransition(c) == null) {
                return 0;
            }
            currentnode = currentnode.getTransition(c);
        }
        return currentnode.terminalSubtreeSum;
    }

    /**
     * Writes the trie into the OutputStream
     * @throws IOException
     */
    public void serialize(OutputStream out) throws IOException {
        try(var dataOut = new DataOutputStream(out)) {
            dataOut.writeInt(size);
            root.serialize(dataOut);
        }
    }

    /**
     * Reads the trie from the InputStream
     * @throws IOException
     */
    public void deserialize(InputStream in) throws  IOException {
        try(var dataIn = new DataInputStream(in)) {
            size = dataIn.readInt();
            root = new Node();
            root.deserialize(dataIn);
        }
    }
}
