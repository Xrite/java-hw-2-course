import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/** This class contains methods that implements multithreaded quick sort algorithm */
class QSort {
    private static final int THRESHOLD = 500000; // was calculated experimentally
    private static final int NUMBER_OF_THREADS = 8;
    private static volatile Random randomGenerator = new Random();

    /**
     * Sorts the array using natural ordering
     *
     * @throws Throwable {@code InterruptedException} or an exception that occurred during the sort (due to invalid comparator)
     */
    static <T extends Comparable<? super T>> void sort(@NotNull T[] array) throws Throwable {
        sort(array, Comparator.naturalOrder());
    }

    /**
     * Sorts the array using comparator
     *
     * @throws Throwable {@code InterruptedException} or an exception that occurred during the sort (due to invalid comparator)
     */
    static <T> void sort(@NotNull T[] array, @NotNull Comparator<? super T> comparator) throws Throwable {
        if (array.length < THRESHOLD) {
            var sorter = new SingleThreadSorter<T>(array, comparator);
            sorter.sortSingleThread(0, array.length);
            return;
        }
        ExecutorService threadPool = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
        Lock lock = new ReentrantLock();
        Condition isDone = lock.newCondition();
        AtomicInteger alreadyDone = new AtomicInteger(0);
        try {
            threadPool.submit(new Sorter<T>(array, 0, array.length, comparator, threadPool,
                    lock, isDone, alreadyDone)).get();
        } catch (ExecutionException e) {
            throw e.getCause();
        }
        lock.lock();
        if (alreadyDone.get() != array.length) {
            isDone.awaitUninterruptibly();
        }
        lock.unlock();
        threadPool.shutdown();
    }

    private static class SingleThreadSorter<T> {
        @NotNull T[] array;
        @NotNull Comparator<? super T> comparator;

        SingleThreadSorter(@NotNull T[] array, @NotNull Comparator<? super T> comparator) {
            this.array = array;
            this.comparator = comparator;
        }

        int partition(int left, int right) {
            int pivot = left + randomGenerator.nextInt(right - left);
            T pivotValue = array[pivot];
            int leftPointer = left;
            int rightPointer = right - 1;
            while (leftPointer <= rightPointer) {
                while (comparator.compare(array[leftPointer], pivotValue) < 0) {
                    leftPointer++;
                }
                while (comparator.compare(pivotValue, array[rightPointer]) < 0) {
                    rightPointer--;
                }
                if (leftPointer <= rightPointer) {
                    T temp = array[leftPointer];
                    array[leftPointer] = array[rightPointer];
                    array[rightPointer] = temp;
                    leftPointer++;
                    rightPointer--;
                }
            }
            return (leftPointer + rightPointer + 1) / 2;
        }

        void sortSingleThread(int left, int right) {
            if (right - left < 2) {
                return;
            }
            int middle = partition(left, right);
            if (right - left == 2) {
                return;
            }
            if (left < middle) {
                sortSingleThread(left, middle);
            }
            if (middle < right) {
                sortSingleThread(middle, right);
            }
        }
    }

    private static class Sorter<T> extends SingleThreadSorter<T> implements Runnable {
        private int left;
        private int right;
        private @NotNull ExecutorService executorService;
        private @NotNull Lock lock;
        private @NotNull Condition isDone;
        private @NotNull AtomicInteger alreadyDone;

        Sorter(@NotNull T[] array, int left, int right, @NotNull Comparator<? super T> comparator,
               @NotNull ExecutorService executorService, @NotNull Lock lock, @NotNull Condition isDone,
               @NotNull AtomicInteger alreadyDone) {
            super(array, comparator);
            this.left = left;
            this.right = right;
            this.executorService = executorService;
            this.lock = lock;
            this.isDone = isDone;
            this.alreadyDone = alreadyDone;
        }

        @Override
        public void run() {
            if (right - left < THRESHOLD) {
                sortSingleThread(left, right);
                alreadyDone.getAndAdd(right - left);
                if (alreadyDone.get() == array.length) {
                    lock.lock();
                    isDone.signal();
                    lock.unlock();
                }
            } else {
                int middle = partition(left, right);
                if (left < middle) {
                    executorService.submit(new Sorter<T>(array, left, middle, comparator, executorService, lock, isDone, alreadyDone));
                }
                if (middle < right) {
                    executorService.submit(new Sorter<T>(array, middle, right, comparator, executorService, lock, isDone, alreadyDone));
                }
            }
        }
    }
}
