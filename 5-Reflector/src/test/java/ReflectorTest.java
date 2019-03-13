import com.google.googlejavaformat.java.FormatterException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class ReflectorTest {
    void compareSourceCode(String fileName) throws IOException {
        var answer = Paths.get("testCode/" + fileName + ".java");
        var file = Paths.get(fileName + ".java");
        assertEquals(Files.readAllLines(answer), Files.readAllLines(file));
    }

    void testPrint(Class<?> clazz) {
        assertDoesNotThrow(() -> Reflector.printStructure(clazz));
        assertDoesNotThrow(() -> compareSourceCode(clazz.getSimpleName()));
        var file = new File(clazz.getSimpleName() + ".java");
        assertTrue(file.delete());
    }

    @Test
    void printEmptyClass() {
        testPrint(EmptyClass.class);
    }

    @Test
    void printExtendsAndImplements() {
        testPrint(ExtendsAndImplements.class);
    }

    @Test
    void printGenerics() {
        testPrint(Generics.class);
    }

    @Test
    void printOnlyInnerClasses() {
        testPrint(OnlyInnerClasses.class);
    }

    @Test
    void printOnlyInnerInterfaces() {
        testPrint(OnlyInnerInterfaces.class);
    }

    @Test
    void printOnlyMethods() {
        testPrint(OnlyMethods.class);
    }

    @Test
    void printOnlyNestedClasses() {
        testPrint(OnlyNestedClasses.class);
    }

    @Test
    void printSimpleConstructors() {
        testPrint(SimpleConstructors.class);
    }

    @Test
    void printSimpleFields() {
        testPrint(SimpleFields.class);
    }

    @Test
    void printThrows() {
        testPrint(Throws.class);
    }

    @Test
    void printComplexClass() {
        testPrint(ComplexClass.class);
    }

}