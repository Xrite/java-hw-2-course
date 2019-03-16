import java.lang.reflect.Array;
import java.util.*;

/**
 * Implements hash map with adding order
 * @param <K>
 * @param <V>
 */
public class LinkedHashMap<K, V> extends AbstractMap<K, V> {
    private final int INITIAL_CAPACITY = 5;
    private PrimitiveSet<MyMapEntry>[] buckets;
    private int capacity;
    private int keysCount = 0;
    private MyMapEntry begin = null;

    public LinkedHashMap() {
        buckets = (PrimitiveSet<MyMapEntry>[]) Array.newInstance(PrimitiveSet.class, INITIAL_CAPACITY);
        capacity = INITIAL_CAPACITY;
        for (int i = 0; i < INITIAL_CAPACITY; i++) {
            buckets[i] = new PrimitiveSet<MyMapEntry>();
        }
    }

    private int getPosition(K key) {
        return (int) (Math.abs((long) key.hashCode()) % (long) capacity);
    }

    @Override
    public int size() {
        return keysCount;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        Set<Entry<K, V>> set = new PrimitiveSet<>();
        var iterator = new LinkedHashMapIterator();
        while (iterator.hasNext()) {
            set.add(iterator.next());
        }
        return set;
    }

    @Override
    public V put(K key, V value) {
        MyMapEntry entry = new MyMapEntry(key, value);
        if (begin == null) {
            begin = entry;
        }
        int bucket = getPosition(key);
        V previousValue = null;
        if (buckets[bucket].contains(entry)) {
            previousValue = buckets[bucket].find(entry).value;
            keysCount--;
            buckets[bucket].remove(entry);
        }
        buckets[bucket].add(entry);
        expand();
        return previousValue;
    }

    @Override
    public V remove(Object key) {
        var castedKey = (K) key;
        int bucket = getPosition(castedKey);
        V oldValue = null;
        if (buckets[bucket].contains(new MyMapEntry(castedKey, null))) {
            oldValue = buckets[bucket].find(new MyMapEntry(castedKey, null)).value;
            buckets[bucket].remove(new MyMapEntry(castedKey, null));
        }
        return oldValue;
    }

    @Override
    public void clear() {
        buckets = (PrimitiveSet<MyMapEntry>[]) Array.newInstance(PrimitiveSet.class, INITIAL_CAPACITY);
        capacity = INITIAL_CAPACITY;
        keysCount = 0;
        begin = null;
    }

    private void expand() {
        if(keysCount != capacity) {
            return;
        }
        var oldEntries = entrySet();
        buckets = (PrimitiveSet<MyMapEntry>[]) Array.newInstance(PrimitiveSet.class, capacity *= 2);
        keysCount = 0;
        begin = null;
        for(int i = 0; i < capacity; i++) {
            buckets[i] = new PrimitiveSet<MyMapEntry>();
        }
        for (var entry : oldEntries) {
            put(entry.getKey(), entry.getValue());
        }
    }

    private class LinkedHashMapIterator implements Iterator<Entry<K, V>> {
        MyMapEntry pointer = begin;

        @Override
        public boolean hasNext() {
            return pointer != null;
        }

        @Override
        public Entry<K, V> next() {
            var entry = pointer;
            pointer = pointer.nextEntry;
            return entry;
        }
    }

    private class MyMapEntry implements Entry<K, V> {
        K key;
        V value;
        private MyMapEntry nextEntry;
        private MyMapEntry previousEntry;

        public MyMapEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            var oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MyMapEntry entry = (MyMapEntry) o;
            return Objects.equals(key, entry.key);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key);
        }
    }

    private class PrimitiveSet<E> extends AbstractSet<E> {
        E[] container;
        int size;

        public PrimitiveSet() {
            container = (E[]) new Object[INITIAL_CAPACITY];
            size = 0;
        }

        @Override
        public PrimitiveSetIterator iterator() {
            return new PrimitiveSetIterator();
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public boolean add(E e) {
            if (contains(e)) {
                return false;
            }
            expand();
            container[size++] = e;
            return true;
        }

        public E find(E e) {
            var iterator = new PrimitiveSetIterator();
            while (iterator.hasNext()) {
                var currentElement = iterator.next();
                if (currentElement.equals(e)) {
                    return currentElement;
                }
            }
            return null;
        }

        private void expand() {
            if (size == container.length) {
                Object[] newContainer = new Object[size * 2];
                System.arraycopy(container, 0, newContainer, 0, size);
                container = (E[]) newContainer;
            }
        }

        private void removeByIndex(int index) {
            for (int i = index; i < size - 1; i++) {
                container[i] = container[i + 1];
            }
            size--;
        }

        private class PrimitiveSetIterator implements Iterator<E> {
            int pointer = 0;

            @Override
            public boolean hasNext() {
                return pointer != container.length;
            }

            @Override
            public E next() {
                return container[pointer++];
            }

            @Override
            public void remove() {
                if (pointer != 0) {
                    removeByIndex(pointer - 1);
                }
            }
        }
    }
}
