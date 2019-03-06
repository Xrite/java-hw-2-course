package ru.hse.aabukov.mytreeset;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Works like {@link TreeSet}. Every modification and search operation is randomized
 * O(log n) time. Note that it does not contain some methods.
 */
public class Treap<E> extends AbstractSet<E> implements MyTreeSet<E> {
    private @NotNull Comparator<? super E> comparator;
    private boolean reversed;
    private @NotNull CommonData data;
    private @NotNull Treap<E> cachedDescendingSet;

    /**
     * Works like {@link TreeSet#TreeSet()}
     */
    @SuppressWarnings("unchecked")
    public Treap() {
        comparator = (Comparator<? super E>) Comparator.naturalOrder();
        data = new CommonData();
        cachedDescendingSet = new Treap<>(comparator, data, this);
    }

    /**
     * Works like {@link TreeSet#TreeSet(Comparator)}
     */
    public Treap(@NotNull Comparator<? super E> comparator) {
        this.comparator = comparator;
        data = new CommonData();
        cachedDescendingSet = new Treap<>(comparator, data, this);
    }

    private Treap(@NotNull Comparator<? super E> comparator, @NotNull CommonData data, @NotNull Treap<E> pairSet) {
        this.comparator = comparator.reversed();
        reversed = true;
        this.data = data;
        cachedDescendingSet = pairSet;
    }

    /**
     * Works like {@link TreeSet#iterator()}
     */
    @NotNull
    @Override
    public Iterator<E> iterator() {
        return new TreapIterator();
    }

    /**
     * Works like {@link TreeSet#size()}
     */
    @Override
    public int size() {
        return data.root == null ? 0 : data.root.size;
    }

    private void invalidate() {
        data.invalidationMark.invalid = true;
        data.invalidationMark = new InvalidationMark();
    }

    /**
     * Works like {@link TreeSet#descendingIterator()}
     */
    @NotNull
    @Override
    public Iterator<E> descendingIterator() {
        return cachedDescendingSet.iterator();
    }

    /**
     * Works like {@link TreeSet#descendingSet()}
     */
    @NotNull
    @Override
    public MyTreeSet<E> descendingSet() {
        return cachedDescendingSet;
    }

    /**
     * Works like {@link TreeSet#first()}
     */
    @Override
    @Nullable
    public E first() {
        var it = iterator();
        if (!it.hasNext()) {
            throw new NoSuchElementException();
        }
        return it.next();
    }

    /**
     * Works like {@link TreeSet#last()}
     */
    @Override
    public E last() {
        return cachedDescendingSet.first();
    }

    @Nullable
    private E lowerBound(@Nullable E e, boolean inclusive) {
        Node node = data.root;
        E candidate = null;
        while (node != null) {
            node.adjustToDirection(reversed);
            if ((comparator.compare(node.value, e) < 0 && !inclusive) || (comparator.compare(node.value, e) <= 0 && inclusive)) {
                candidate = node.value;
                node = node.right;
            } else {
                node = node.left;
            }
        }
        return candidate;
    }

    /**
     * Works like {@link TreeSet#lower(Object)}
     */
    @Override
    @Nullable
    public E lower(@Nullable E e) {
        return lowerBound(e, false);
    }

    /**
     * Works like {@link TreeSet#floor(Object)}
     */
    @Override
    @Nullable
    public E floor(@Nullable E e) {
        return lowerBound(e, true);
    }

    /**
     * Works like {@link TreeSet#ceiling(Object)}
     */
    @Override
    @Nullable
    public E ceiling(@Nullable E e) {
        return cachedDescendingSet.floor(e);
    }

    /**
     * Works like {@link TreeSet#higher(Object)}
     */
    @Override
    @Nullable
    public E higher(@Nullable E e) {
        return cachedDescendingSet.lower(e);
    }

    /**
     * Works like {@link TreeSet#add(Object)}
     */
    @Override
    public boolean add(@Nullable E e) {
        if (contains(e)) {
            return false;
        }
        invalidate();
        var splitted = split(data.root, e);
        var temp = merge(splitted.first, new Node(e));
        data.root = merge(temp, splitted.second);
        return true;
    }

    /**
     * Works like {@link TreeSet#contains(Object)} except it only accepts objects that could be cast to {@code E}
     *
     * @throws IllegalArgumentException if the argument can not be cast to {@code E}
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean contains(@Nullable Object o) {
        try {
            Node node = data.root;
            while (node != null) {
                node.adjustToDirection(reversed);
                if (comparator.compare(node.value, (E) o) < 0) {
                    node = node.right;
                } else if (comparator.compare(node.value, (E) o) == 0) {
                    return true;
                } else {
                    node = node.left;
                }
            }
            return false;
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Objects that can not be cast to E are not supported");
        }
    }

    /**
     * Works like {@link TreeSet#contains(Object)} except it only accepts objects that could be cast to {@code E}
     *
     * @throws IllegalArgumentException if the argument can not be cast to {@code E}
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean remove(@Nullable Object o) {
        if (!contains(o)) {
            return false;
        }
        try {
            invalidate();
            if (comparator.compare(first(), (E) o) == 0) {
                var splitted = split(data.root, (E) o);
                data.root = splitted.second;
            } else {
                E pred = lower((E) o);
                var left = split(data.root, pred).first;
                var right = split(data.root, (E) o).second;
                data.root = merge(left, right);
            }
            return true;
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Objects that can not be cast to E are not supported");
        }
    }

    @Nullable
    private Node merge(@Nullable Node left, @Nullable Node right) {
        if (left == null) {
            return right;
        }
        if (right == null) {
            return left;
        }
        left.adjustToDirection(reversed);
        right.adjustToDirection(reversed);
        if (left.priority > right.priority) {
            left.right = merge(left.right, right);
            left.right.parent = left;
            left.right.direction = 1;
            left.direction = 0;
            left.update();
            return left;
        } else {
            right.left = merge(left, right.left);
            right.left.parent = right;
            right.left.direction = -1;
            right.direction = 0;
            right.update();
            return right;
        }
    }

    @NotNull
    private Pair split(@Nullable Node node, @Nullable E element) {
        if (node == null) {
            return new Pair(null, null);
        }
        node.adjustToDirection(reversed);
        if (comparator.compare(node.value, element) <= 0) {
            var splitted = split(node.right, element);
            node.right = splitted.first;
            if (node.right != null) {
                node.right.parent = node;
                node.right.direction = 1;
            }
            node.direction = 0;
            node.update();
            return new Pair(node, splitted.second);
        } else {
            var splitted = split(node.left, element);
            node.left = splitted.second;
            if (node.left != null) {
                node.left.parent = node;
                node.left.direction = -1;
            }
            node.direction = 0;
            node.update();
            return new Pair(splitted.first, node);
        }
    }

    private class TreapIterator implements Iterator<E> {
        private @Nullable Node pointer;
        private @NotNull InvalidationMark invalidationMark;

        private TreapIterator() {
            invalidationMark = data.invalidationMark;
            pointer = data.root;
            if (pointer != null) {
                pointer.adjustToDirection(reversed);
            }
            while (pointer != null && pointer.left != null) {
                pointer = pointer.left;
                pointer.adjustToDirection(reversed);
            }
        }

        @Nullable
        private Node tryNext(@NotNull Node node) {
            node.adjustToDirection(reversed);
            if (node.right != null) {
                node = node.right;
                node.adjustToDirection(reversed);
                while (node.left != null) {
                    node = node.left;
                    node.adjustToDirection(reversed);
                }
                return node;
            }
            while (node.direction == 1) {
                node = node.parent;
                node.adjustToDirection(reversed);
            }
            return node.parent;
        }

        @Override
        public boolean hasNext() {
            if (invalidationMark.invalid) {
                throw new ConcurrentModificationException();
            }
            return pointer != null;
        }

        @Override
        @Nullable
        public E next() {
            if (invalidationMark.invalid) {
                throw new ConcurrentModificationException();
            }
            Node previous = pointer;
            if (pointer == null) {
                throw new NoSuchElementException();
            }
            pointer = tryNext(pointer);
            return previous.value;
        }
    }

    private class CommonData {
        private @NotNull Random rand = new Random();
        private @Nullable Node root;
        private @NotNull InvalidationMark invalidationMark = new InvalidationMark();
    }

    private class Node {
        private @Nullable E value;
        private @Nullable Node left;
        private @Nullable Node right;
        private @Nullable Node parent;
        private int priority;
        private int size;
        private int direction; // -1 if left child, 1 if right child, 0 if root
        private boolean reversedNode;

        Node(@Nullable E value) {
            priority = data.rand.nextInt();
            this.value = value;
            size = 1;
        }

        private void adjustToDirection(boolean isReversed) {
            if (isReversed != reversedNode) {
                var temp = left;
                left = right;
                right = temp;
                direction *= -1;
                reversedNode = isReversed;
            }
        }

        private void update() {
            size = 1;
            if (left != null) {
                size += left.size;
            }
            if (right != null) {
                size += right.size;
            }
        }
    }

    private class InvalidationMark {
        private boolean invalid = false;
    }

    private class Pair {
        private @Nullable Node first;
        private @Nullable Node second;

        Pair(@Nullable Node first, @Nullable Node second) {
            this.first = first;
            this.second = second;
        }
    }
}