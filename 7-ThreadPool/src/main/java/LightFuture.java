import java.util.function.Function;

public interface LightFuture<T> {
    boolean isReady();

    T get() throws InterruptedException, LightExecutionException;

    <V> LightFuture<V> thenApply(Function<? super T, ? extends V> function);
}
