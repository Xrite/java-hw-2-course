import classes.*;
import exceptions.TestingException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestingTest {

    @Test
    void testNoClass() {
        assertThrows(TestingException.class, () -> Testing.testClass("NoSuchClass228"));
    }

    @Test
    void testEmpty() {
        assertDoesNotThrow(() -> Testing.testClass(Empty.class));
    }

    @Test
    void testMultipleAnnotations() {
        assertThrows(TestingException.class, () -> Testing.testClass(MultipleAnnotations.class));
    }

    @Test
    void testIncorrectTestMethods() {
        assertThrows(TestingException.class, () -> Testing.testClass(IncorrectTestMethods.class));
    }

    @Test
    void testIgnored() throws TestingException {
        assertDoesNotThrow(() -> Testing.testClass(IgnoredTests.class));
        var summary = Testing.testClass(IgnoredTests.class);
        assertCount(summary, 0, 0, 2);
    }

    @Test
    void testOrder() throws TestingException {
        assertDoesNotThrow(() -> Testing.testClass(TestOrder.class));
        var summary = Testing.testClass(TestOrder.class);
        assertCount(summary, 1, 0, 0);
    }

    @Test
    void testExceptions() throws TestingException {
        assertDoesNotThrow(() -> Testing.testClass(TestExceptionTypes.class));
        var summary = Testing.testClass(TestExceptionTypes.class);
        assertCount(summary, 1, 1, 0);
    }

    @Test
    void testMany() throws TestingException {
        assertDoesNotThrow(() -> Testing.testClass(ManyTests.class));
        var summary = Testing.testClass(ManyTests.class);
        summary.printSummary(System.out);
        assertCount(summary, 2, 2, 2);
    }

    private void assertCount(Testing.TestingSummary summary, int passed, int failed, int ignored) {
        assertEquals(passed, summary.getPassed().size());
        assertEquals(failed, summary.getFailed().size());
        assertEquals(ignored, summary.getIgnored().size());
    }

}