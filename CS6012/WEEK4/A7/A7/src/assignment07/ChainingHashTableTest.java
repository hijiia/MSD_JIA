package assignment07;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class ChainingHashTableTest {
    private ChainingHashTable goodChainingHashTable;
    private ChainingHashTable emptyHashTable;
    GoodHashFunctor goodHashFunctor;
    MediocreHashFunctor mediocreHashFunctor;
    BadHashFunctor badHashFunctor;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        goodHashFunctor = new GoodHashFunctor();
        mediocreHashFunctor = new MediocreHashFunctor();
        badHashFunctor = new BadHashFunctor();
        goodChainingHashTable = new ChainingHashTable(10,goodHashFunctor);
        goodChainingHashTable.add("This");
        goodChainingHashTable.add("is");
        goodChainingHashTable.add("a");
        goodChainingHashTable.add("Hash");

        emptyHashTable = new ChainingHashTable(10,badHashFunctor);
        emptyHashTable.add("This");

    }

    @org.junit.jupiter.api.Test
    void add() {
        assertTrue(goodChainingHashTable.add("Table"));
        assertFalse(goodChainingHashTable.add("Table"));
        assertTrue(goodChainingHashTable.contains("Table"));
    }

    @org.junit.jupiter.api.Test
    void addAll() {
        String[] strArr = new String[] {"Hello","World"};
        goodChainingHashTable.addAll(List.of(strArr));
    }

    @org.junit.jupiter.api.Test
    void clear() {
        emptyHashTable.clear();
    }

    @org.junit.jupiter.api.Test
    void contains() {
        assertTrue(goodChainingHashTable.contains("This"));
    }

    @org.junit.jupiter.api.Test
    void containsAll() {
        assertTrue(goodChainingHashTable.containsAll(List.of("Hello","World")));

    }

    @org.junit.jupiter.api.Test
    void isEmpty() {
        emptyHashTable.clear();
        assertTrue(emptyHashTable.isEmpty());
    }

    @org.junit.jupiter.api.Test
    void remove() {
        goodChainingHashTable.add("Table");
        goodChainingHashTable.remove("Table");
        assertFalse(goodChainingHashTable.contains("Table"));
    }

    @org.junit.jupiter.api.Test
    void removeAll() {
        assertTrue(goodChainingHashTable.removeAll(List.of("This","is")));
        assertFalse(goodChainingHashTable.contains("Hello"));
    }

    @org.junit.jupiter.api.Test
    void size() {
        assertEquals(4, goodChainingHashTable.size());
    }
}