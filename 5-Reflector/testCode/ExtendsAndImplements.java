public class ExtendsAndImplements extends EmptyClass implements SimpleInterface1, SimpleInterface2 {
  private abstract static interface I extends SimpleInterface1, SimpleInterface2 {}

  private class A {
    private A() {}
  }

  public ExtendsAndImplements() {}

  public class B extends ExtendsAndImplements.A implements SimpleInterface1 {
    private class C extends ExtendsAndImplements.A implements SimpleInterface2 {
      private C() {}
    }

    public B() {}
  }
}

