public class OnlyInnerClasses {
  class A {
    A() {}

    class C {
      C() {}
    }
  }

  private class B {
    private B() {}
  }

  public OnlyInnerClasses() {}
}

