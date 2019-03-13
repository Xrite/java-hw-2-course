public class OnlyInnerInterfaces {
  abstract static interface C {}

  public OnlyInnerInterfaces() {}

  public abstract static interface A {
    public abstract static interface B {}
  }
}

