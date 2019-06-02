package classes;

import annotations.After;
import annotations.AfterClass;
import annotations.Test;

public class IncorrectTestMethods {
    @Test
    private void foo(int x) {
    }

    @After
    protected void bar(int x) {
    }

    @AfterClass
    private void baz(int x, int y) {
    }
}
