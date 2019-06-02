package classes;

import annotations.Test;

public class IgnoredTests {
    @Test(ignored = true, reason = "Test")
    public void foo() {
    }

    @Test(ignored = true, reason = "Test")
    private void bar() {
    }
}
