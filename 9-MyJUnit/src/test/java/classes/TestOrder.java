package classes;

import annotations.*;

public class TestOrder {
    private Object beforeClassObject = null;
    private Object beforeObject = null;
    private Object testObject = null;
    private Object afterObject = null;

    @BeforeClass
    void beforeClass() {
        beforeClassObject = new Object();
    }

    @Before
    void before() {
        if(beforeClassObject == null) {
            throw new RuntimeException();
        }
        beforeObject = new Object();
    }

    @Test
    void test() {
        if(beforeObject == null) {
            throw new RuntimeException();
        }
        testObject = new Object();
    }

    @After
    void after() {
        if(testObject == null) {
            throw new RuntimeException();
        }
        afterObject = new Object();
    }

    @AfterClass
    void afterClass() {
        if(afterObject == null) {
            throw new RuntimeException();
        }
    }
}
