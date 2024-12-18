package assignment04;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Comparator;
import static org.junit.jupiter.api.Assertions.*;

class SortUtilTest {

    private ArrayList<Integer> bestCaseList;
    private ArrayList<Integer> averageCaseList;
    private ArrayList<Integer> worstCaseList;

    @BeforeEach
    void setUp() {
        bestCaseList = SortUtil.generateBestCase(10);
        averageCaseList = SortUtil.generateAverageCase(10);
        worstCaseList = SortUtil.generateWorstCase(10);
    }

    @Test
    void testGenerateBestCase() {
        ArrayList<Integer> expected = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            expected.add(i);
        }
        assertEquals(expected, bestCaseList, "Best case generation failed");
    }

    @Test
    void testGenerateAverageCase() {
        assertEquals(10, averageCaseList.size(), "Average case size is incorrect");
        assertNotEquals(bestCaseList, averageCaseList, "Average case should be shuffled");
    }

    @Test
    void testGenerateWorstCase() {
        ArrayList<Integer> expected = new ArrayList<>();
        for (int i = 10; i >= 1; i--) {
            expected.add(i);
        }
        assertEquals(expected, worstCaseList, "Worst case generation failed");
    }

    @Test
    void testMergesortBestCase() {
        SortUtil.mergesort(bestCaseList, Comparator.naturalOrder());
        assertTrue(isSorted(bestCaseList), "Mergesort failed on best case input");
    }

    @Test
    void testMergesortAverageCase() {
        SortUtil.mergesort(averageCaseList, Comparator.naturalOrder());
        assertTrue(isSorted(averageCaseList), "Mergesort failed on average case input");
    }

    @Test
    void testMergesortWorstCase() {
        SortUtil.mergesort(worstCaseList, Comparator.naturalOrder());
        assertTrue(isSorted(worstCaseList), "Mergesort failed on worst case input");
    }

    @Test
    void testQuicksortBestCase() {
        SortUtil.quicksort(bestCaseList, Comparator.naturalOrder());
        assertTrue(isSorted(bestCaseList), "Quicksort failed on best case input");
    }

    @Test
    void testQuicksortAverageCase() {
        SortUtil.quicksort(averageCaseList, Comparator.naturalOrder());
        assertTrue(isSorted(averageCaseList), "Quicksort failed on average case input");
    }

    @Test
    void testQuicksortWorstCase() {
        SortUtil.quicksort(worstCaseList, Comparator.naturalOrder());
        assertTrue(isSorted(worstCaseList), "Quicksort failed on worst case input");
    }

    // Helper method to check if a list is sorted
    private <T extends Comparable<T>> boolean isSorted(ArrayList<T> list) {
        for (int i = 1; i < list.size(); i++) {
            if (list.get(i - 1).compareTo(list.get(i)) > 0) {
                return false;
            }
        }
        return true;
    }
}