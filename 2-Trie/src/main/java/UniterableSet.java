import java.util.List;

public interface UniterableSet<E> {
    boolean add(E e);

    boolean contains(E o);

    boolean remove(E e);

    int size();

    default void addAll(List<E> list) {
        for (var element : list) {
            this.add(element);
        }
    }

    default void removeAll(List<E> list) {
        for (var element : list) {
            this.remove(element);
        }
    }
}
