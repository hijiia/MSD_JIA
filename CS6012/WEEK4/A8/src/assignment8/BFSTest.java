package assignment8;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class BFSTest {
    int[][] regularMaze;
    int[][] notWorkableMaze;


    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        regularMaze = new int[][] {
                {1,1,1,1,1},
                {1,0,0,0,1},
                {1,0,0,0,1},
                {1,0,0,0,1},
                {1,1,1,1,1}, //start 2,2 end 4,4
        };
        notWorkableMaze = new int[][] {
                {1,1,1,1,1},
                {1,0,0,0,1},
                {1,0,0,1,1},
                {1,0,1,0,1},
                {1,1,1,1,1},//start 2,2 end 4,4
        };

    }

    @org.junit.jupiter.api.Test
    void mazeDotBFS() {


    }

    @Test
    void printPath() {
        BFS regular = new BFS(regularMaze, new int[]{1,1},new int[]{3,3});
        regular.mazeDotBFS();
        String[][]regularPath = new String[][]{
                {"X","X","X","X","X"},
                {"X","S"," "," ","X"},
                {"X","."," "," ","X"},
                {"X",".",".","G","X"},
                {"X","X","X","X","X"}
        };
       assertTrue(Arrays.deepEquals(regularPath, regular.printPath()));

        BFS notWorkableTest = new BFS(notWorkableMaze, new int[]{1,1},new int[]{3,3});
        notWorkableTest.mazeDotBFS();
        String[][]notWorkablePath = new String[][]{
                {"X","X","X","X","X"},
                {"X","S"," "," ","X"},
                {"X"," "," ","X","X"},
                {"X"," ","X","G","X"},
                {"X","X","X","X","X"}
        };
        assertTrue(Arrays.deepEquals(notWorkablePath, notWorkableTest.printPath()));
    }

    @org.junit.jupiter.api.Test
    void convertNumtoString() {
    }
}