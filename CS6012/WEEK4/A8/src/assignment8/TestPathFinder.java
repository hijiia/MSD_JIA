package assignment8;
import java.io.File;

public class TestPathFinder {
    public static void main(String[] args) {
        File file = new File("src/mazeTest.txt");
        try {
            if (file.exists()) {
                System.out.println("yes");
            }else{
                System.out.println("no");
            }
        } catch (Exception e) {
            System.out.println("no");
        }
        FileInput input = new FileInput("src/mazeTest.txt");
        input.mapConvert();
        input.printMap();
        PathFinder pathFinder = new PathFinder();
        pathFinder.solveMaze("src/mazeTest.txt","solution");

    }
}
