package classes;

import annotations.*;

public class MultipleAnnotations {
    @Before
    @Test
    private void foo() {
    }

    @After
    @BeforeClass
    @AfterClass
    protected void bar() {
    }

    @Test
    @After
    private void baz() {
    }
}
