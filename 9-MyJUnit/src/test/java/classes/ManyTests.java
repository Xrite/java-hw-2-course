package classes;

import annotations.*;

public class ManyTests {
    @BeforeClass
    void beforeClass() {
    }

    @Before
    void before() {
    }

    @Test
    void failedTest1() throws Exception {
        throw new Exception();
    }

    @Test(exception = IllegalStateException.class)
    void failedTest2() {
        throw new IllegalArgumentException();
    }

    @Test(ignored = true, reason = "Test")
    void ignoredTest1() {
    }

    @Test(ignored = true)
    void ignoredTest2() {
    }

    @Test(exception = IllegalStateException.class)
    void passedTest1() {
        throw new IllegalStateException();
    }

    @Test
    void passedTest2() {
    }

    @After
    void after() {
    }

    @AfterClass
    void afterClass() {
    }
}
