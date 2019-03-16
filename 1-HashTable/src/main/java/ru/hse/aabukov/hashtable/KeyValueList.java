package ru.hse.aabukov.hashtable;

import org.jetbrains.annotations.NotNull;

/** Implements associative array interface for pairs of strings using linked list */
public class KeyValueList {

    private Node begin = null;

    /** Removes the first element from the list */
    public void popFront() {
        if (begin != null) {
            begin = begin.next;
        }
    }

    /**
     * Returns the key of the first element in the list
     *
     * @return The key of the first element
     */
    public String frontKey() {
        if (begin == null) {
            return null;
        }
        return begin.key;
    }

    /**
     * Returns the first element in the list
     *
     * @return The first element
     */
    public String frontValue() {
        if (begin == null) {
            return null;
        }
        return begin.value;
    }


    /**
     * Checks whether the list is empty
     *
     * @return True if the list is empty
     */
    public boolean empty() {
        return begin == null;
    }

    /**
     * Checks whether the list contains an element with given key
     *
     * @param key the key of an element to check
     * @return True if an element with given key contains in the list
     */
    public boolean contains(@NotNull String key) {
        return findByKey(key) != null;
    }

    /**
     * Returns an element with given key
     *
     * @param key the key of an element to find
     * @return An element if it is in the list, null otherwise
     */
    public String get(@NotNull String key) {
        var foundNode = findByKey(key);
        if (foundNode != null) {
            return foundNode.value;
        } else {
            return null;
        }
    }

    /**
     * Puts the element with given key and returns previous element
     *
     * @param key   the key ot the element
     * @param value the element to put
     * @return A previous element if table contained an element with given key, null otherwise
     */
    public String put(@NotNull String key, String value) {
        var foundNode = findByKey(key);
        if (foundNode == null) {
            begin = new Node(key, value, begin);
            return null;
        }
        var previousValue = foundNode.value;
        foundNode.value = value;
        return previousValue;
    }

    /**
     * Removes an element with given key
     *
     * @param key the key of an element to remove
     * @return An element with given key if an element with this key contained in the list, null otherwise
     */
    public String remove(@NotNull String key) {
        var valueByKey = get(key);
        if (valueByKey == null) {
            return null;
        }
        begin = removeByKey(key, begin);
        return valueByKey;
    }

    private Node removeByKey(@NotNull String key, Node list) {
        if (list == null) {
            return null;
        }
        if (key.equals(list.key)) {
            return list.next;
        }
        list.next = removeByKey(key, list.next);
        return list;
    }

    private Node findByKey(@NotNull String key) {
        var currentNode = begin;
        while (currentNode != null && !key.equals(currentNode.key)) {
            currentNode = currentNode.next;
        }
        return currentNode;
    }

    private class Node {
        private Node next;
        private @NotNull String key;
        private String value;

        Node(@NotNull String key, String value, Node next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }
}
