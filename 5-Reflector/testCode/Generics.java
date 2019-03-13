public class Generics<T, V, U extends java.lang.Integer> {
  <K, N extends K> Generics(V t0, U t1) {}

  <K, N extends K> void f2() {
    return;
  }

  <K> Generics(T t0) {}

  <K> void f1(T t0, V t1, U t2) {
    return;
  }

  Generics() {}

  T t1;
  final V t2 = null;

  final int f3(java.lang.Comparable<? extends T> t0) {
    return 0;
  }

  private U t3;
  private U[] t4;

  private class A<K> {
    K v;

    private A() {}
  }

  public abstract static interface I<K> {
    public abstract K get();
  }

  static void f4(java.util.List<?> t0) {
    return;
  }
}

