import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;

public class Main {
    private static final int SIZE = 1000000;

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        var in = new Scanner(System.in);
        System.out.println("Enter directory");
        var filename = in.nextLine();
        long startSingleTime = System.nanoTime();
        var singleThreadHash = calcHash(Path.of(filename));
        long endSingleTime = System.nanoTime();
        long startMultiTime = System.nanoTime();
        var multiThreadHash = calcHashForkJoin(8, Path.of(filename));
        long endMultiTime = System.nanoTime();
        System.out.println("Single:");
        System.out.println(Arrays.toString(singleThreadHash));
        System.out.println("Time: " + (endSingleTime - startSingleTime));
        System.out.println("Multi:");
        System.out.println(Arrays.toString(multiThreadHash));
        System.out.println("Time: " + (endMultiTime - startMultiTime));
    }

    @NotNull public static byte[] calcHash(@NotNull Path path) throws NoSuchAlgorithmException, IOException {
        var digest = MessageDigest.getInstance("MD5");
        if (Files.isDirectory(path)) {
            digest.update(path.getFileName().toString().getBytes());
            var dirs = Files.walk(path, 1).filter(Files::isRegularFile).collect(Collectors.toList());
            for (var dir : dirs) {
                digest.update(calcHash(dir));
            }
            return digest.digest();
        } else {
            var buffer = new byte[SIZE];
            try (var in = Files.newInputStream(path)) {
                var stream = new DigestInputStream(in, digest);
                while (stream.read(buffer) != -1) {
                }
                return stream.getMessageDigest().digest();
            }
        }
    }

    @NotNull public static byte[] calcHashForkJoin(int parallelism, @NotNull Path path) {
        var pool = new ForkJoinPool(parallelism);
        return pool.invoke(new Task(path));
    }

    private static class Task extends RecursiveTask<byte[]> {
        private @NotNull Path path;

        private Task(@NotNull Path path) {
            this.path = path;
        }

        @Override
        @NotNull protected byte[] compute() {
            try {
                MessageDigest digest = MessageDigest.getInstance("MD5");
                if (Files.isDirectory(path)) {
                    digest.update(path.getFileName().toString().getBytes());
                    var tasks = Files.walk(path, 1).filter(Files::isRegularFile).map(Task::new).collect(Collectors.toList());
                    for (var task : tasks) {
                        task.fork();
                    }
                    for (var task : tasks) {
                        digest.update(task.join());
                    }
                    return digest.digest();
                } else {
                    var buffer = new byte[SIZE];
                    try (var in = Files.newInputStream(path)) {
                        var stream = new DigestInputStream(in, digest);
                        while (stream.read(buffer) != -1);
                        return stream.getMessageDigest().digest();
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
                return new byte[0];
            }
        }
    }
}
