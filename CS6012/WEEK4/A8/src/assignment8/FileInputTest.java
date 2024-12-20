package assignment8;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FileInputTest {
    String filename;
    FileInput fileInput;

    @BeforeEach
    void setUp() {
        filename = "src/mazeTest.txt";
        fileInput = new FileInput(filename);
    }

    @Test
    void close() {
    }

    @Test
    void getHeight() {
        assertEquals(5, fileInput.getHeight());
    }

    @Test
    void getWidth() {
        assertEquals(6, fileInput.getWidth());
    }

    @Test
    void mapConvert() {
    }

    @Test
    void getMatrix() {
        int[][] matrix = fileInput.getMatrix();
        int[][] matrix2 = new int[][]{
                {1,1,1,1,1,1},
                {1,0,0,0,1,1},
                {1,0,0,0,1,1},
                {1,0,0,1,0,1},
                {1,1,1,1,1,1},
        };
        assertArrayEquals(matrix2, matrix);
    }

    @Test
    void getStart() {
        int[] start = new int[]{0,0};
        assertArrayEquals(start, fileInput.getStart());
    }

    @Test
    void getEnd() {
        int[] end = new int[]{0,0};
        assertArrayEquals(end, fileInput.getEnd());
    }

    @Test
    void printMap() {
    }
}