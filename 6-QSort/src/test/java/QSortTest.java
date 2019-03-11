import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class QSortTest {
    Integer[] generateIntegerArray(int length, int seed) {
        var random = new Random(seed);
        var array = new Integer[length];
        Arrays.setAll(array, i -> random.nextInt());
        return array;
    }

    @Test
    void testEmpty() {
        var array = new Integer[0];
        assertDoesNotThrow(() -> QSort.sort(array));
        assertArrayEquals(new Integer[0], array);
    }

    @Test
    void testSingleton() {
        var array = new Integer[]{0};
        assertDoesNotThrow(() -> QSort.sort(array));
        assertArrayEquals(new Integer[]{0}, array);
    }

    @Test
    void testSmall() {
        for (int i = 1; i <= 10; i++) {
            var array = generateIntegerArray(10 * i, i);
            var arrayCopy = Arrays.copyOf(array, array.length);
            Arrays.sort(arrayCopy);
            assertDoesNotThrow(() -> QSort.sort(array));
            assertArrayEquals(arrayCopy, array);
        }
    }

    @Test
    void testLarge() {
        for (int i = 1; i <= 10; i++) {
            var array = generateIntegerArray(50000 * i, i);
            var arrayCopy = Arrays.copyOf(array, array.length);
            Arrays.sort(arrayCopy);
            assertDoesNotThrow(() -> QSort.sort(array));
            assertArrayEquals(arrayCopy, array);
        }
    }

    @Test
    void testComparator() {
        var array = generateIntegerArray(30, 0);
        var arrayCopy = Arrays.copyOf(array, array.length);
        Arrays.sort(arrayCopy, Comparator.reverseOrder());
        assertDoesNotThrow(() -> QSort.sort(array, Comparator.reverseOrder()));
        assertArrayEquals(arrayCopy, array);
    }

    @Test
    void testCustomComparator() {
        var random = new Random(0);
        var array = new A[20];
        for (int i = 0; i < array.length; i++) {
            array[i] = new A();
            array[i].field = random.nextInt(10);
        }
        var arrayCopy = Arrays.copyOf(array, array.length);
        Comparator<A> comparator = new Comparator<A>() {

            @Override
            public int compare(A o1, A o2) {
                return o2.field - o1.field;
            }
        };
        Arrays.sort(arrayCopy, comparator);
        assertDoesNotThrow(() -> QSort.sort(array, comparator));
        assertArrayEquals(arrayCopy, array);
    }

    @Test
    void testSuperComparator() {
        var random = new Random(0);
        var array = new B[20];
        for (int i = 0; i < array.length; i++) {
            array[i] = new B();
            array[i].field = random.nextInt(10);
        }
        var arrayCopy = Arrays.copyOf(array, array.length);
        Comparator<A> comparator = new Comparator<A>() {

            @Override
            public int compare(A o1, A o2) {
                return o2.field - o1.field;
            }
        };
        Arrays.sort(arrayCopy, comparator);
        assertDoesNotThrow(() -> QSort.sort(array, comparator));
        assertArrayEquals(arrayCopy, array);
    }

    class A {
        public int field;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            A a = (A) o;
            return field == a.field;
        }

        @Override
        public int hashCode() {
            return Objects.hash(field);
        }
    }

    class B extends A {

    }
}