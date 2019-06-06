import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    Path openTest(String test) {
        return Paths.get("testdirs", test);
    }

    byte[] fileHash(Path path) throws NoSuchAlgorithmException, IOException {
        var digest = MessageDigest.getInstance("MD5");
        var in = Files.newInputStream(path);
        digest.update(in.readAllBytes());
        return digest.digest();
    }
    @Test
    void testEmptyDir() throws NoSuchAlgorithmException, IOException {
    }

    @Test
    void testSingleFile() throws NoSuchAlgorithmException, IOException {
        var testName = "test-single-file";
        var path = openTest(testName);
        var digest = MessageDigest.getInstance("MD5");
        digest.update(testName.getBytes());
        digest.update(fileHash(Path.of("testdirs", testName, "1")));
        var result = digest.digest();
        assertArrayEquals(result, Main.calcHash(path));
        assertArrayEquals(result, Main.calcHashForkJoin(8, path));
    }

    @Test
    void testMultipleFiles() throws NoSuchAlgorithmException, IOException {
        var testName = "test-multiple-files";
        var path = openTest(testName);
        assertArrayEquals(Main.calcHash(path), Main.calcHashForkJoin(8, path));
    }

    @Test
    void testTree() throws NoSuchAlgorithmException, IOException {
        var testName = "test-tree";
        var path = openTest(testName);
        assertArrayEquals(Main.calcHash(path), Main.calcHashForkJoin(8, path));
    }
}
