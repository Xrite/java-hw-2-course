import com.google.googlejavaformat.java.FormatterException;
import net.openhft.compiler.CompilerUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClassPrinterTest {

    void adaf() {
        Reflector.diffClasses(ComplexClass.class, ComplexClass2.class);
    }

    @Test
    void diffSimpleInterfaces() {
        List<Set<String>> sets = ClassPrinter.diffClasses(SimpleInterface1.class, SimpleInterface2.class);
        assertEquals(2, sets.size());
        assertEquals(0, sets.get(0).size());
        assertEquals(0, sets.get(1).size());
    }

    @Test
    void diffComplexClasses() {
        List<Set<String>> sets = ClassPrinter.diffClasses(ComplexClass.class, ComplexClass2.class);
        assertEquals(2, sets.size());
        assertEquals(sets.get(0), Set.of("private synchronized<V, U extends java.lang.Comparable<? super V>> T setKek(T t0, U t1, int t2, java.lang.Integer t3) {return null;}",
                "private void foo(SomeClass<?> t0) {return ;}",
                "private volatile T kek;",
                "java.util.List<java.lang.Integer> l;"));
        assertEquals(sets.get(1), Set.of("java.util.List<java.lang.Integer> al;",
                "private volatile java.lang.Object kek;",
                "private synchronized<V, U extends java.lang.Comparable<? super V>> T setKek(T t0, U t1, int t2) {return null;}",
                "private void foo(ComplexClass<?> t0) {return ;}"));
    }

    @Test
    void compileAndCompareComplexClass() throws ClassNotFoundException, FileNotFoundException, FormatterException {
        Class<?> clazz = compileAndGetClass(ComplexClass.class, "A");
        List<Set<String>> sets = ClassPrinter.diffClasses(ComplexClass.class, clazz);
        assertEquals(2, sets.size());
        assertEquals(0, sets.get(0).size());
        assertEquals(0, sets.get(1).size());
        cleanupClass("A");
    }

    @Test
    void compileAndCompareEmptyClass() throws ClassNotFoundException, FileNotFoundException, FormatterException {
        Class<?> clazz = compileAndGetClass(EmptyClass.class, "B");
        List<Set<String>> sets = ClassPrinter.diffClasses(EmptyClass.class, clazz);
        assertEquals(2, sets.size());
        assertEquals(0, sets.get(0).size());
        assertEquals(0, sets.get(1).size());
        cleanupClass("B");
    }

    @Test
    void compileAndCompareGenerics() throws ClassNotFoundException, FileNotFoundException, FormatterException {
        Class<?> clazz = compileAndGetClass(Generics.class, "C");
        List<Set<String>> sets = ClassPrinter.diffClasses(Generics.class, clazz);
        assertEquals(2, sets.size());
        assertEquals(0, sets.get(0).size());
        assertEquals(0, sets.get(1).size());
        cleanupClass("C");
    }

    Class<?> compileAndGetClass(Class<?> clazz, String className) throws ClassNotFoundException {
        String javaCode = ClassPrinter.printStructure(clazz, className);
        Class<?> compiledClass = CompilerUtils.CACHED_COMPILER.loadFromJava(className, javaCode);
        return compiledClass;
    }

    void cleanupClass(String className) {
        var file = new File(className + ".java");
        file.delete();
    }
}