import annotations.*;
import exceptions.NoException;
import exceptions.TestingException;
import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Class that performs testing of given class methods with annotations in given order:
 * At first all methods with @BeforeClass are called
 * Then before every method with @Test all methods with @Before called and then after all methods with @After are called
 * At the end all methods with @AfterClass are called
 */
class Testing {
    @NotNull
    private static final Predicate<?>[] predicates = {
            (Method method) -> method.isAnnotationPresent(After.class),
            (Method method) -> method.isAnnotationPresent(Before.class),
            (Method method) -> method.isAnnotationPresent(AfterClass.class),
            (Method method) -> method.isAnnotationPresent(BeforeClass.class),
            (Method method) -> method.isAnnotationPresent(Test.class),
    };

    /**
     * Performs testing of class with the given name
     *
     * @param className name of the class
     * @return TestingSummary object that contains information about testing result
     * @throws TestingException when there are no class with such name or some methods contain multiple annotations
     *                          or testing method has arguments or method that is not annotated with @Test throws an exception or some unexpected
     *                          problem occurred
     */
    @NotNull
    static TestingSummary testClass(@NotNull String className) throws TestingException {
        var clazz = loadClass(className);
        return testClass(clazz);
    }

    /**
     * Performs testing of class with the given name
     *
     * @param clazz Class object to test
     * @return TestingSummary object that contains information about testing result
     * @throws TestingException TestingException when there are no class with such name or some methods contain multiple annotations
     *                          or testing method has arguments or method that is not annotated with @Test throws an exception or some unexpected
     *                          problem occurred
     */
    @SuppressWarnings("unchecked")
    @NotNull
    static TestingSummary testClass(@NotNull Class<?> clazz) throws TestingException {
        var instance = getInstance(clazz);
        var filtered = filterUniqueMethods(clazz, (Predicate<Method>[]) predicates);
        var afterMethods = filtered[0];
        var beforeMethods = filtered[1];
        var afterClassMethods = filtered[2];
        var beforeClassMethods = filtered[3];
        var testMethods = filtered[4];
        var summary = TestingSummary.empty();
        invokeMethodsNoThrows(instance, beforeClassMethods, "@BeforeClass");
        for (var method : testMethods) {
            invokeMethodsNoThrows(instance, beforeMethods, "@Before");
            summary.addTest(TestingSummary.MethodSummary.runMethod(method, instance));
            invokeMethodsNoThrows(instance, afterMethods, "@After");
        }
        invokeMethodsNoThrows(instance, afterClassMethods, "@AfterClass");
        return summary;
    }

    private static void invokeMethodsNoThrows(@NotNull Object instance, @NotNull List<Method> methods, @NotNull String annotation) throws TestingException {
        for (var method : methods) {
            try {
                method.invoke(instance);
            } catch (IllegalAccessException | InvocationTargetException e) {
                var exception = new TestingException("Exception was thrown when calling " + method.getName() + " annotated " + annotation);
                exception.addSuppressed(e);
                throw exception;
            }
        }
    }

    @NotNull
    private static Object getInstance(Class<?> clazz) throws TestingException {
        try {
            return clazz.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            var exception = new TestingException("Can't instantiate test class");
            exception.addSuppressed(e);
            throw exception;
        }
    }

    @NotNull
    private static Class<?> loadClass(@NotNull String name) throws TestingException {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            throw new TestingException("Can't find such class");
        }
    }

    @SuppressWarnings("unchecked")
    @NotNull
    private static List<Method>[] filterUniqueMethods(@NotNull Class<?> clazz, @NotNull Predicate<Method>[] predicates) throws TestingException {
        var methods = clazz.getDeclaredMethods();
        var result = (ArrayList<Method>[]) new ArrayList[predicates.length];
        for (int i = 0; i < predicates.length; i++) {
            result[i] = new ArrayList<>();
        }
        for (var method : methods) {
            method.setAccessible(true);
            int predicatesSatisfied = 0;
            for (int i = 0; i < predicates.length; i++) {
                if (predicates[i].test(method)) {
                    if (method.getParameterCount() != 0) {
                        throw new TestingException("Testing methods must have no arguments. Failed on " + method.getName());
                    }
                    predicatesSatisfied++;
                    result[i].add(method);
                }
                if (predicatesSatisfied > 1) {
                    throw new TestingException("Method " + method.getName() + " have multiple annotations");
                }
            }
        }
        return result;
    }


    /** Class that contains testing summary */
    public static class TestingSummary {

        private @NotNull List<MethodSummary> tests = new ArrayList<>();

        @NotNull
        private static TestingSummary empty() {
            return new TestingSummary();
        }

        private void addTest(@NotNull MethodSummary test) {
            tests.add(test);
        }

        /** Prints a summary to the given PrintStream */
        void printSummary(@NotNull PrintStream stream) {
            stream.println(getPassed().size() + " tests passed");
            stream.println(getFailed().size() + " tests failed");
            stream.println(getIgnored().size() + " tests ignored");
            tests.forEach(test -> stream.println(test.show()));
        }

        /** Returns all passed methods summaries */
        List<MethodSummary.Passed> getPassed() {
            return tests.stream().filter(test -> test instanceof TestingSummary.MethodSummary.Passed).map(test -> (MethodSummary.Passed) test).collect(Collectors.toList());
        }

        /** Returns all failed methods summaries */
        List<MethodSummary.Failed> getFailed() {
            return tests.stream().filter(test -> test instanceof TestingSummary.MethodSummary.Failed).map(test -> (MethodSummary.Failed) test).collect(Collectors.toList());
        }

        /** Returns all ignored methods summaries */
        List<MethodSummary.Ignored> getIgnored() {
            return tests.stream().filter(test -> test instanceof TestingSummary.MethodSummary.Ignored).map(test -> (MethodSummary.Ignored) test).collect(Collectors.toList());
        }

        /** Class that contains method testing summary */
        abstract static class MethodSummary {

            private static double convertNanosToMillis(long nanos) {
                return (double) nanos / 1e6;
            }

            @NotNull
            private static MethodSummary runMethod(@NotNull Method method, @NotNull Object instance) throws TestingException {
                if (!method.isAnnotationPresent(Test.class)) {
                    throw new IllegalArgumentException("Attempt to test method without @Test annotation");
                }
                if (method.getAnnotation(Test.class).ignored()) {
                    return new Ignored(method, method.getAnnotation(Test.class).reason());
                }
                var expectedException = method.getAnnotation(Test.class).exception();
                var startTime = System.currentTimeMillis();
                try {
                    method.invoke(instance);
                } catch (IllegalAccessException e) {
                    var exception = new TestingException("Can't access method");
                    exception.addSuppressed(e);
                    throw exception;
                } catch (InvocationTargetException e) {
                    var stopTime = System.currentTimeMillis();
                    if (expectedException.equals(e.getCause().getClass())) {
                        return new Passed(method, stopTime - startTime);
                    } else {
                        return new Failed(method, expectedException, e.getCause().getClass(), stopTime - startTime);
                    }
                }
                var stopTime = System.currentTimeMillis();
                return new Passed(method, stopTime - startTime);
            }

            /** Returns summary of method testing */
            @NotNull
            abstract String show();

            private static class Failed extends MethodSummary {
                private @NotNull String methodName;
                private @NotNull String expectedException;
                private @NotNull String thrownException;
                private double timeMillis;

                private Failed(@NotNull Method method, @NotNull Class<? extends Throwable> expected, Class<? extends Throwable> thrown, long timeNanos) {
                    methodName = method.getName();
                    if (expected.equals(NoException.class)) {
                        expectedException = "nothing";
                    } else {
                        expectedException = expected.getName();
                    }
                    thrownException = thrown.getName();
                    timeMillis = convertNanosToMillis(timeNanos);
                }

                /** {@inheritDoc} */
                @Override
                @NotNull
                String show() {
                    return "Test " + methodName + "failed in " + timeMillis + " ms: expected " + expectedException + " but " + thrownException + " was thrown";
                }
            }

            private static class Passed extends MethodSummary {
                private @NotNull String methodName;
                private double timeMillis;

                private Passed(@NotNull Method method, long timeNanos) {
                    methodName = method.getName();
                    timeMillis = convertNanosToMillis(timeNanos);
                }

                /** {@inheritDoc} */
                @Override
                @NotNull
                String show() {
                    return "Test " + methodName + " passed in " + timeMillis + "ms";
                }
            }

            private static class Ignored extends MethodSummary {
                private @NotNull String methodName;
                private @NotNull String reason;

                private Ignored(@NotNull Method method, @NotNull String reason) {
                    methodName = method.getName();
                    this.reason = reason;
                }

                /** {@inheritDoc} */
                @Override
                @NotNull
                String show() {
                    return "Test " + methodName + " ignored: " + reason;
                }
            }
        }
    }
}
