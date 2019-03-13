public class ExtendsAndImplements extends EmptyClass implements SimpleInterface1, SimpleInterface2 {
    private interface I extends SimpleInterface1, SimpleInterface2 {

    }

    private class A {

    }

    public class B extends A implements SimpleInterface1 {
        private class C extends A implements SimpleInterface2 {

        }
    }
}
