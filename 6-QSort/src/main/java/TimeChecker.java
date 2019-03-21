import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

class TimeChecker {
    private static final int[] sizes = {10, 100, 1000, 5000, 10000, 50000, 100000, 1000000, 5000000, 10000000, 50000000};

    private static Integer[] randomArray(int size) {
        var random = new Random();
        var array = new Integer[size];
        Arrays.setAll(array, i -> random.nextInt());
        return array;
    }

    private static Integer[] arrayCopy(Integer[] array) {
        return Arrays.copyOf(array, array.length);
    }

    private static long getTime(Runnable runnable) {
        var startTime = System.nanoTime();
        runnable.run();
        var endTime = System.nanoTime();
        return endTime - startTime;
    }

    private static void compareSorts(int size) throws Throwable {
        System.out.println("Size: " + size);
        var array = randomArray(size);
        var myTime = getTime(() -> {
            try {
                QSort.sort(arrayCopy(array));
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        });
        var sortTime = getTime(() -> Arrays.sort(array));
        System.out.println("My sort time: " + myTime + " nanos; " +
                TimeUnit.MICROSECONDS.convert(myTime, TimeUnit.NANOSECONDS) + " micros; " +
                TimeUnit.MILLISECONDS.convert(myTime, TimeUnit.NANOSECONDS) + " millis");
        System.out.println("Arrays.sort time: " + sortTime + " nanos; " +
                TimeUnit.MICROSECONDS.convert(sortTime, TimeUnit.NANOSECONDS) + " micros; " +
                TimeUnit.MILLISECONDS.convert(sortTime, TimeUnit.NANOSECONDS) + " millis");
        if (myTime < sortTime) {
            System.out.println("My sort wins");
        } else {
            System.out.println("Arrays.sort wins");
        }

    }

    public static void main(String[] args) throws Throwable {
        for (var size : sizes) {
            compareSorts(size);
        }
    }
}
