import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TestFindSmallestDiff {
    private int[] arr1, arr2, arr3, arr4, arr5, arr6, arr7;

    @org.junit.jupiter.api.BeforeEach
    protected void setUp() throws Exception {
        arr1 = new int[0];
        arr2 = new int[] { 3, 3, 3 };
        arr3 = new int[] { 52, 4, -8, 0, -17 };
        arr4 = new int[] {-1, 5, 5, 2, 0 };
        arr5 = new int[] { 10, 15 };
        arr6 = new int[] {Integer.MAX_VALUE, Integer.MAX_VALUE - 1, Integer.MIN_VALUE};
        arr7 = new int[] {1, 2, 3, 4, 5};
    }


    @org.junit.jupiter.api.AfterEach
    void tearDown() {
    }

    @Test
    public void emptyArray() {
        assertEquals(-1, DiffUtil.findSmallestDiff(arr1));
    }

    @Test
    public void allArrayElementsEqual() {
        assertEquals(0, DiffUtil.findSmallestDiff(arr2));
    }

    @Test
    public void smallRandomArrayElements() {
        assertEquals(4, DiffUtil.findSmallestDiff(arr3));
    }

    @Test
    public void duplicatedArrayElements() {
        assertEquals(0, DiffUtil.findSmallestDiff(arr4));
    }
    @Test
    public void twoElementsArray() {
        assertEquals(5, DiffUtil.findSmallestDiff(arr5));
    }
    @Test
    public void largeNumbersArray() {
        assertEquals(1, DiffUtil.findSmallestDiff(arr6));
    }
    @Test
    public void consecutiveNumbersArray() {
        assertEquals(1, DiffUtil.findSmallestDiff(arr7));
    }

}