import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

class Reflector {
    /**
     * Prints formatted structure code of the given class
     *
     * @throws FileNotFoundException if the program failed to write java file
     * @throws FormatterException    should not be thrown. Throwing this exception means that program contains bug.
     */
    static void printStructure(@NotNull Class<?> clazz) throws FileNotFoundException, FormatterException {
        var file = new File(clazz.getSimpleName() + ".java");
        var writer = new PrintWriter(file);
        var formatter = new Formatter();
        var output = formatter.formatSource(ClassPrinter.printStructure(clazz));
        writer.println(output);
        writer.flush();
        writer.close();
    }

    /** Compares two classes and output to stdin unique classes and methods for each class */
    static void diffClasses(@NotNull Class<?> a, @NotNull Class<?> b) {
        List<Set<String>> sets = ClassPrinter.diffClasses(a, b);
        assert (sets.size() == 2);
        Set<String> aSet = sets.get(0);
        Set<String> bSet = sets.get(1);
        for (var line : aSet) {
            System.out.println("first: " + line);
        }
        for (var line : bSet) {
            System.out.println("second: " + line);
        }
    }
}
