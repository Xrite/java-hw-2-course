package classes;

import annotations.Test;

public class TestExceptionTypes {
    @Test(exception = IllegalStateException.class)
    void wrongType() {
        throw new IllegalArgumentException();
    }

    @Test(exception = IllegalStateException.class)
    void correctType() {
        throw new IllegalStateException();
    }
}
