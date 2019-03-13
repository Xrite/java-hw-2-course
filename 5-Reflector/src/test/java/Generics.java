import java.util.List;

public class Generics<T, V, U extends Integer> {
    T t1;
    final V t2 = null;
    private U t3;
    private U[] t4;

    <K> void f1(T a1, V a2, U a3) {}
    <K, N extends K> void f2() {}
    final int f3(Comparable<? extends T> a1) {return 0;};
    static void f4(List<?> a1) {}

    Generics() {}
    <K> Generics(T a1) {}
    <K, N extends K> Generics(V a1, U a2) {}

    private class A<K> {
        K v;
    }

    public interface I<K> {
       K get();
    }
}
