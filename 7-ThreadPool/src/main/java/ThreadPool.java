import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.function.Function;
import java.util.function.Supplier;

/** Simple thread pool */
public class ThreadPool {
    @NotNull
    private final TaskQueue<Task<?>> tasks = new TaskQueue<>(new ArrayDeque<>());
    @NotNull
    private final Thread[] workers;
    private volatile boolean isShutdown = false;

    /**
     * Creates a new thread pool with given amount of threads
     *
     * @param n number of threads to use
     */
    public ThreadPool(int n) {
        workers = new Thread[n];
        for (int i = 0; i < n; i++) {
            workers[i] = new Thread(() -> {
                while (!Thread.interrupted()) {
                    Task<?> task;
                    try {
                        task = tasks.poll();
                    } catch (InterruptedException e) {
                        break;
                    }
                    task.compute();
                }
            });
            workers[i].start();
        }
    }

    /**
     * Creates a delayed task from supplier
     *
     * @throws IllegalStateException when tried to add new task to closed thread pool
     */
    @NotNull
    public <T> LightFuture<T> submit(@NotNull Supplier<T> supplier) {
        if (isShutdown) {
            throw new IllegalStateException("ThreadPool was shut down");
        }
        var task = new Task<>(supplier);
        tasks.add(task);
        return task;
    }

    /**
     * Closes the thread pool for new tasks and asks every thread to interrupt
     *
     * @return true if thread pool was successfully closed, false if it was already closed
     */
    public boolean shutdown() {
        if (!isShutdown) {
            isShutdown = true;
            for (var worker : workers) {
                worker.interrupt();
                while (worker.isAlive()) {
                    try {
                        worker.join();
                    } catch (InterruptedException ignored) {
                        //ignored
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }

    private class Task<T> implements LightFuture<T> {
        @NotNull
        private final Supplier<? extends T> supplier;
        @NotNull
        private final List<Task<?>> thenApplyTasks = new ArrayList<>();
        private volatile boolean isReady;
        @Nullable
        private T result;
        @Nullable
        private Exception exception;

        private Task(@NotNull Supplier<? extends T> supplier) {
            this.supplier = supplier;
        }

        /** {@inheritDoc} */
        @Override
        public boolean isReady() {
            return isReady;
        }

        /** {@inheritDoc} */
        @Nullable
        @Override
        public T get() throws LightExecutionException {
            synchronized (thenApplyTasks) {
                while (!isReady) {
                    try {
                        thenApplyTasks.wait();
                    } catch (InterruptedException e) {
                        exception = new LightExecutionException(e);
                        isReady = true;
                    }
                }
            }
            if (exception != null) {
                throw new LightExecutionException(exception);
            }
            return result;
        }

        /** {@inheritDoc} */
        @NotNull
        @Override
        public <V> LightFuture<V> thenApply(@NotNull Function<? super T, ? extends V> function) {
            if (isShutdown) {
                throw new IllegalStateException("ThreadPool was shut down");
            }
            var task = new Task<V>(() -> function.apply(result));
            if (isReady) {
                task.exception = exception;
                tasks.add(task);
            } else {
                synchronized (thenApplyTasks) {
                    if (isReady) {
                        task.exception = exception;
                        tasks.add(task);
                    } else {
                        thenApplyTasks.add(task);
                    }
                }
            }
            return task;
        }

        private void compute() {
            if (exception == null) {
                try {
                    result = supplier.get();
                } catch (Exception e) {
                    exception = e;
                }
            }
            isReady = true;
            synchronized (thenApplyTasks) {
                for (var task : thenApplyTasks) {
                    task.exception = exception;
                    tasks.add(task);
                }
                thenApplyTasks.notify();
            }
        }
    }

    private class TaskQueue<T> {
        @NotNull
        private final Queue<T> queue;

        @NotNull
        private TaskQueue(@NotNull Queue<T> queue) {
            this.queue = queue;
        }

        private synchronized void add(@NotNull T e) {
            queue.add(e);
            notify();
        }

        @NotNull
        private synchronized T poll() throws InterruptedException {
            while (queue.size() == 0) {
                wait();
            }
            return queue.poll();
        }
    }
}
