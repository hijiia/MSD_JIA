package assignment03;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

class BinarySearchSetTest {
    private BinarySearchSet<Integer> set;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        Comparator<Integer> comparator = (a, b) -> b - a;
        set = new BinarySearchSet<> (comparator);

    }

    @org.junit.jupiter.api.Test
    void add() {
        assertTrue(set.add(10));
        assertTrue(set.add(20));
        assertTrue(set.contains(10));  // contain 10
        assertTrue(set.contains(20));  // contain 20
    }

    @org.junit.jupiter.api.Test
    void first() {
        set.add(10);
        set.add(20);
        set.add(5);
        assertEquals(20, set.first());
    }

    @org.junit.jupiter.api.Test
    void last() {
        set.add(10);
        set.add(20);
        set.add(5);
        assertEquals(5, set.last());
    }

    @org.junit.jupiter.api.Test
    void clear() {
        set.add(10);
        set.add(20);
        set.add(5);
        set.clear();
    }

    @org.junit.jupiter.api.Test
    void containsAll() {
        set.add(10);
        set.add(20);
        set.add(5);

        assertTrue(set.containsAll(Arrays.asList(10,20)));
        assertFalse(set.containsAll(Arrays.asList(10,6)));
    }

    @org.junit.jupiter.api.Test
    void iterator() {
        set.add(10);
        set.add(20);
        set.add(5);
        Iterator<Integer> iterator = set.iterator();//next rmv
    }

    @org.junit.jupiter.api.Test
    void remove() {
        set.add(10);
        set.add(20);
        set.add(5);
        set.remove(5);
    }

    @org.junit.jupiter.api.Test
    void size() {
        set.add(10);
        set.add(20);
        set.add(5);
        assertEquals(3, set.size());

    }

    @org.junit.jupiter.api.Test
    void toArray() {
        set.add(10);
        set.add(20);
        set.add(5);
        set.toArray();
    }
}