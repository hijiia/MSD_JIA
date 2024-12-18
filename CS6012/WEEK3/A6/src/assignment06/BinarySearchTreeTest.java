package assignment06;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class BinarySearchTreeTest {
    private BinarySearchTree<Integer> tree;
    private ArrayList<Integer> list;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        tree = new BinarySearchTree<>();
        list = new ArrayList<>();
        list.add(5);
        list.add(3);
        list.add(7);
        tree.addAll(list);
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
    }

    @org.junit.jupiter.api.Test
    void add() {
        assertFalse(tree.add(5));
        assertTrue(tree.add(2));
    }

    @org.junit.jupiter.api.Test
    void addAll() {
        ArrayList<Integer> list2 = new ArrayList<>();
        list2.add(9);
        list2.add(1);
        assertFalse(tree.addAll(list));
        assertTrue(tree.addAll(list2));
    }

    @org.junit.jupiter.api.Test
    void clear() {
        tree.clear();
        assertTrue(tree.isEmpty());
        assertEquals(0, tree.size());
    }

    @org.junit.jupiter.api.Test
    void contains() {
        assertTrue(tree.contains(5));
        assertTrue(tree.contains(3));
        assertFalse(tree.contains(10)); // Not added
    }

    @org.junit.jupiter.api.Test
    void containsAll() {
        ArrayList<Integer> list2 = new ArrayList<>();
        list2.add(5);
        list2.add(3);
        assertTrue(tree.containsAll(list2));
        list2.add(9);
        assertFalse(tree.containsAll(list2)); // 9 not added
    }

    @org.junit.jupiter.api.Test
    void first() {
        assertEquals(3, (int) tree.first());
    }

    @org.junit.jupiter.api.Test
    void isEmpty() {
        tree = new BinarySearchTree<>();
        assertTrue(tree.isEmpty());
    }

    @org.junit.jupiter.api.Test
    void last() {
        assertEquals(7, (int) tree.last());
    }

    @org.junit.jupiter.api.Test
    void remove() {
        assertTrue(tree.remove(5));
    }

    @org.junit.jupiter.api.Test
    void removeAll() {
        assertTrue(tree.removeAll(list));
    }

    @org.junit.jupiter.api.Test
    void size() {
        assertEquals(list.size(), tree.size());
    }

    @org.junit.jupiter.api.Test
    void toArrayList() {
        ArrayList<Integer> list2 = new ArrayList<>();
        list2.add(3);
        list2.add(5);
        list2.add(7);
        assertEquals(tree.toArrayList(), list2);
    }
}