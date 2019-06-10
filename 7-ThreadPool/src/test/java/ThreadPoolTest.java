import org.junit.jupiter.api.RepeatedTest;

import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class ThreadPoolTest {
    private static final int REPETITIONS = 20;

    int fib(int n) {
        if (n == 0) {
            return 0;
        } else if (n == 1) {
            return 1;
        } else {
            return fib(n - 1) + fib(n - 2);
        }
    }

    @RepeatedTest(REPETITIONS)
    void testSingleThread() throws LightExecutionException {
        var pool = new ThreadPool(1);
        var future = new ArrayList<LightFuture<Integer>>();
        for (int i = 0; i < 100; i++) {
            future.add(pool.submit(() -> fib(15)));
        }
        for (int i = 0; i < 100; i++) {
            assertEquals(610, (int) future.get(i).get());
        }
    }

    @RepeatedTest(REPETITIONS)
    void testMultipleThreads() throws InterruptedException, LightExecutionException {
        var pool = new ThreadPool(8);
        var future = new ArrayList<LightFuture<Integer>>();
        for (int i = 0; i < 100; i++) {
            future.add(pool.submit(() -> fib(15)));
        }
        Thread.sleep(1000);
        for (int i = 0; i < 100; i++) {
            assertEquals(610, (int) future.get(i).get());
        }
    }

    @RepeatedTest(REPETITIONS)
    void testException() {
        var pool = new ThreadPool(4);
        var future = pool.submit(() -> {
            throw new RuntimeException();
        });
        assertThrows(LightExecutionException.class, future::get);
    }

    @RepeatedTest(REPETITIONS)
    void testThenApplyException() {
        var pool = new ThreadPool(4);
        var future = pool.submit(() -> {
            throw new RuntimeException("Error");
        });
        assertThrows(LightExecutionException.class, () -> future.thenApply(Object::toString).get(), "Error");
    }

    @RepeatedTest(REPETITIONS)
    void testMultipleShutdowns() {
        var pool = new ThreadPool(4);
        pool.submit(() -> 0);
        assertTrue(pool.shutdown());
        assertFalse(pool.shutdown());
        assertFalse(pool.shutdown());
    }

    @RepeatedTest(REPETITIONS)
    void testShutdown() {
        var pool = new ThreadPool(4);
        var future = pool.submit(() -> 0);
        assertTrue(pool.shutdown());
        assertThrows(IllegalStateException.class, () -> pool.submit(() -> 0));
        assertThrows(IllegalStateException.class, () -> future.thenApply(x -> x + 1));
    }

    @RepeatedTest(REPETITIONS)
    void testChain() throws LightExecutionException {
        var pool = new ThreadPool(4);
        var future = new ArrayList<LightFuture<Integer>>(10);
        future.add(pool.submit(() -> 0));
        for (int i = 0; i < 9; i++) {
            future.add(future.get(i).thenApply(x -> x + 1));
        }
        for (int i = 0; i < 10; i++) {
            assertEquals(i, (int) future.get(i).get());
        }
    }

    @RepeatedTest(REPETITIONS)
    void checkIsReady() throws LightExecutionException {
        var pool = new ThreadPool(4);
        var future = pool.submit(() -> 0);
        future.get();
        assertTrue(future.isReady());
        future = pool.submit(() -> {
            while (true) ;
        });
        assertFalse(future.isReady());
    }

    @RepeatedTest(REPETITIONS)
    void testAllThreadsAreWorking() {
        final var names = new HashSet<String>();
        var pool = new ThreadPool(4);
        var tasks = new ArrayList<LightFuture>();
        for (int i = 0; i < 100; i++) {
            tasks.add(pool.submit(() -> {
                synchronized (names) {
                    names.add(Thread.currentThread().getName());
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return 0;
            }));
        }
        tasks.forEach(future -> {
            try {
                future.get();
            } catch (LightExecutionException e) {
                e.printStackTrace();
                fail();
            }
        });

        assertEquals(4, names.size());
    }
}