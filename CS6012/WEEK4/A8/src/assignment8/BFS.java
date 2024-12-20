package assignment8;

import java.util.LinkedList;
import java.util.Queue;

public class BFS {
    private int[][] matrix;
    private boolean[][] visited;
    private int[] start;
    private int[] end;
    private Queue<int[]> queue;
    static int[] dx = {-1, 1, 0, 0}; // up and down
    static int[] dy = {0, 0, -1, 1}; // right and left
    String[][] path;

    private int[][][] parent;
    private boolean pathFound = false;

    /**
     * construct BFS method
     * @param matrix -- the original map to walk with 0 and 1,
     *               1 for wall, 0 for walkable path
     * @param end -- the end point with x and y position in array
     * @param start -- the start point with x and y position in array
     */
    public BFS(int[][] matrix, int[] start, int[] end) {
        this.matrix = matrix;
        this.start = start;
        this.end = end;
        visited = new boolean[matrix.length][matrix[0].length];
        queue = new LinkedList<>();
        // path string used to record the '.'
        // initialize path string was to copy the original matrix
        // and change the possible path ' ' into '.'
        path = new String[matrix.length][matrix[0].length];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                path[i][j] = matrix[i][j] + "";
            }
        }

        parent = new int[matrix.length][matrix[0].length][2];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                parent[i][j][0] = -1;
                parent[i][j][1] = -1;
            }
        }
    }

    /**
     * The method to find the shortest way to solve the maze
     * use parent array to trace every dot
     */
    public void mazeDotBFS() {
        int rows = matrix.length;
        int cols = matrix[0].length;
        queue.add(start);
        visited[start[0]][start[1]] = true;
        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            if (current[0] == end[0] && current[1] == end[1]) {
                pathFound = true;
                break;
            }

            for (int i = 0; i < 4; i++) {
                int nextX = current[0] + dx[i];
                int nextY = current[1] + dy[i];
                if (nextX >= 0 && nextX < rows && nextY >= 0 && nextY < cols) {
                    if (!visited[nextX][nextY] && matrix[nextX][nextY] != 1) {
                        queue.add(new int[]{nextX, nextY});
                        visited[nextX][nextY] = true;

                        parent[nextX][nextY][0] = current[0];
                        parent[nextX][nextY][1] = current[1];
                    }
                }
            }
        }
    }

    public String[][] printPath() {
        String[][]pathString = new String[matrix.length][matrix[0].length];
        if (!pathFound) {
            pathString = convertNumtoString(path,start,end);
            return pathString;
        }
        int[] current = end;
        while (current[0] != start[0] || current[1] != start[1]) {
            path[current[0]][current[1]] = ".";
            current = new int[]{parent[current[0]][current[1]][0],
                    parent[current[0]][current[1]][1]};
        }
        pathString = convertNumtoString(path,start,end);
        return pathString;
    }

    public String[][] convertNumtoString(String[][]path, int[]start, int[]end) {
        String[][] pathString = new String[path.length][path[0].length];
        for (int i = 0; i < path.length; i++) {
            for (int j = 0; j < path[0].length; j++) {
                if (i == start[0] && j == start[1]) {
                    pathString[i][j] = "S";
                    System.out.print("S");
                } else if (i == end[0] && j == end[1]) {
                    pathString[i][j] = "G";
                    System.out.print("G");
                }
                else if (path[i][j].equals(".")) {
                    pathString[i][j] = ".";
                    System.out.print(path[i][j]);
                } else if (path[i][j].equals("1")) {
                    pathString[i][j] = "X";
                    System.out.print("X");
                } else if (path[i][j].equals("0")) {
                    pathString[i][j] = " ";
                    System.out.print(" ");
                }
            }
            System.out.println();
        }
        return pathString;
    }
}