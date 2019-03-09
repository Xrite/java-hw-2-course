package ru.hse.aabukov.hashtable;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;


/** Implements an associative array interface for pairs of strings using hash table */
public class HashTable {
    private final int INITIAL_CAPACITY = 5;
    private KeyValueList[] buckets;
    private int capacity;
    private int keysCount;

    /** Creates an empty hash table */
    public HashTable() {
        buckets = new KeyValueList[INITIAL_CAPACITY];
        capacity = INITIAL_CAPACITY;
        Arrays.setAll(buckets, i -> new KeyValueList());
    }

    private int getPosition(@NotNull String key) {
        return Math.abs(key.hashCode()) % capacity;
    }

    private void expand() {
        String[] keysBuffer = new String[keysCount];
        String[] valuesBuffer = new String[keysCount];
        int pointer = 0;
        for (var bucket : buckets) {
            while (!bucket.empty()) {
                keysBuffer[pointer] = bucket.frontKey();
                valuesBuffer[pointer++] = bucket.frontValue();
                bucket.popFront();
            }
        }
        buckets = new KeyValueList[capacity *= 2];
        Arrays.setAll(buckets, i -> new KeyValueList());
        keysCount = 0;
        for (int i = 0; i < keysBuffer.length; i++) {
            put(keysBuffer[i], valuesBuffer[i]);
        }
    }

    /** Returns the number of element */
    public int size() {
        return keysCount;
    }

    /**
     * Checks whether the hash table contains an element with given key
     *
     * @param key the key of element to check
     */
    public boolean contains(@NotNull String key) {
        return buckets[getPosition(key)].contains(key);
    }

    /**
     * Returns an element with given key
     *
     * @param key the key of an element to find
     * @return An element if it is in the hash table, null otherwise
     */
    public String get(@NotNull String key) {
        return buckets[getPosition(key)].get(key);
    }

    /**
     * Puts the element with given key and returns a previous element
     *
     * @param key   the key ot the element
     * @param value the element to put
     * @return A previous element if the table contained an element with given key, null otherwise
     */
    public String put(@NotNull String key, String value) {
        var previousValue = buckets[getPosition(key)].put(key, value);
        if (previousValue == null) {
            keysCount++;
        }
        if (keysCount >= capacity) {
            expand();
        }
        return previousValue;
    }

    /**
     * Removes an element with given key
     *
     * @param key the key of an element to remove
     * @return An element with given key if an element with this key contained in the table, null otherwise
     */
    public String remove(@NotNull String key) {
        var valueByKey = buckets[getPosition(key)].remove(key);
        if (valueByKey != null) {
            keysCount--;
        }
        return valueByKey;
    }

    /** Removes all elements and frees an allocated memory */
    public void clear() {
        capacity = INITIAL_CAPACITY;
        buckets = new KeyValueList[capacity];
        Arrays.setAll(buckets, i -> new KeyValueList());
        keysCount = 0;
    }
}
