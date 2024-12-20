package assignment8;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class FileInput {
    private File file;
    private Scanner input;
    private String[] dimensions;
    private int height;
    private int width;
    private int[][] matrix;
    private int[] start;
    private int[] end;

    public FileInput(String fileName) {
        file = new File(fileName);
        try {
            if (!file.exists() ) {
                throw new IllegalArgumentException("File does not exist " + fileName);
            }
            if (!file.canRead()) {
                throw new IllegalArgumentException("File is not readable: " + fileName);
            }
            input = new Scanner(file);
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("File not found: " + fileName, e);
        }
        dimensions = input.nextLine().split(" ");
        height = Integer.parseInt(dimensions[0]);
        width = Integer.parseInt(dimensions[1]);
        matrix = new int[height][width];
        start = new int[2];
        end = new int[2];
    }

    public void close(){
        input.close();
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public void mapConvert(){
        int heightNum = 0;
        while (input.hasNextLine()) {
            int widthNum = 0;
            String line = input.nextLine();
            for (char c : line.toCharArray()) {
                switch (c) {
                    case ' ':
                        matrix[heightNum][widthNum] = 0;
                        break;
                    case 'X':
                        matrix[heightNum][widthNum] = 1;
                        break;
                    case 'S':
                        matrix[heightNum][widthNum] = 0;
                        start[0] = heightNum;
                        start[1] = widthNum;
                        break;
                    case 'G':
                        matrix[heightNum][widthNum] = 0;
                        end[0] = heightNum;
                        end[1] = widthNum;
                        break;
                }
                widthNum++;
            }
            heightNum++;
        }
    }

    public int[][] getMatrix() {
        return matrix;
    }

    public int[] getStart() {
        return start;
    }

    public int[] getEnd() {
        return end;
    }

    public void printMap(){
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
    }


}
