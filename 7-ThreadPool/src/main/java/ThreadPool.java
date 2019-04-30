import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.function.Function;
import java.util.function.Supplier;

public class ThreadPool {
    @NotNull
    private final TaskQueue<Task<?>> tasks = new TaskQueue<>(new ArrayDeque<>());
    @NotNull
    private final Thread[] workers;
    @NotNull
    private final Object taskAvailable = new Object();
    private volatile boolean isShutdown = false;

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

    @Nullable
    public <T> LightFuture<T> submit(@NotNull Supplier<T> supplier) {
        if (isShutdown) {
            return null;
        }
        var task = new Task<>(supplier);
        tasks.add(task);
        return task;
    }

    public boolean shutdown() {
        if (!isShutdown) {
            isShutdown = true;
            for (var worker : workers) {
                worker.interrupt();
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
        private final Object readyLock = new Object();
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

        @Override
        public boolean isReady() {
            return isReady;
        }

        @Nullable
        @Override
        public T get() throws InterruptedException, LightExecutionException {
            while (!isReady) {
                readyLock.wait();
            }
            if (exception != null) {
                throw new LightExecutionException(exception);
            }
            return result;
        }

        @NotNull
        @Override
        public <V> LightFuture<V> thenApply(@NotNull Function<? super T, ? extends V> function) {
            var task = new Task<V>(() -> function.apply(result));
            if (isReady) {
                tasks.add(task);
            } else {
                synchronized (thenApplyTasks) {
                    if (isReady) {
                        tasks.add(task);
                    } else {
                        thenApplyTasks.add(task);
                    }
                }
            }
            return task;
        }

        private void compute() {
            try {
                result = supplier.get();
            } catch (Exception e) {
                exception = e;
            }
            isReady = true;
            readyLock.notify();
            synchronized (thenApplyTasks) {
                for (var task : thenApplyTasks) {
                    tasks.add(task);
                }
            }
        }
    }

    private class TaskQueue<T> {
        @NotNull
        private final Queue<T> queue;
        @NotNull
        private final Object notEmpty = new Object();

        private TaskQueue(@NotNull Queue<T> queue) {
            this.queue = queue;
        }

        private synchronized boolean add(@NotNull T e) {
            var result = queue.add(e);
            if (queue.size() == 1) {
                notify();
            }
            return result;
        }

        @NotNull
        private synchronized T poll() throws InterruptedException {
            if (queue.size() == 0) {
                wait();
            }
            var result = queue.poll();
            assert result != null;
            return result;
        }
    }
}
