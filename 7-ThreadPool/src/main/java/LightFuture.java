import java.util.function.Function;

/**
 * Interface of task with delayed execution
 *
 * @param <T> Type of result of the task
 */
public interface LightFuture<T> {
    /** Returns true if the task was completed, false otherwise */
    boolean isReady();

    /**
     * Waiting for task to be executed
     *
     * @return Result of the task
     * @throws InterruptedException    When the task was interrupted
     * @throws LightExecutionException When an exception occurred during execution of task
     */
    T get() throws InterruptedException, LightExecutionException;

    /**
     * Applies a function to the result of the task and makes a new delayed task
     *
     * @param function function to apply
     * @param <V>      Return type of composition
     * @return Delayed task describing execution of composition
     */
    <V> LightFuture<V> thenApply(Function<? super T, ? extends V> function);
}
