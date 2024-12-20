package assignment8;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class PathFinder {

    public static void solveMaze(String inputFile, String outputFile){
        FileInput maze = new FileInput(inputFile);
        maze.mapConvert();
        BFS mazeSolution = new BFS(maze.getMatrix(), maze.getStart(), maze.getEnd());
        mazeSolution.mazeDotBFS();
        String[][] output = mazeSolution.printPath();

        try(PrintWriter outputF = new PrintWriter(new FileWriter(outputFile))) {
            outputF.println(maze.getHeight() + " " + maze.getWidth());
            for (int i = 0; i < output.length; i++) {
                for (int j = 0; j < output[i].length; j++) {
                    outputF.print(output[i][j]);
                }
                outputF.println();
            }
        } catch (IOException e){
            System.out.println("Can not write to file");
            e.printStackTrace();
        }

    }
}
