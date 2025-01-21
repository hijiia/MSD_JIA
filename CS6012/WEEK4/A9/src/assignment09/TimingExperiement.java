package assignment09;

import java.util.ArrayList;

public class TimingExperiement {
    /**
     * Generates vertical segments in worst-case order (left to right)
     */
    private static ArrayList<Segment> generateWorstCaseSegments(int n) {
        ArrayList<Segment> segments = new ArrayList<>();
        double spacing = 100.0 / n;  // Spread segments across 0-100 range

        // Create vertical segments from left to right
        for (int i = 0; i < n; i++) {
            double x = i * spacing;
            segments.add(new Segment(x, 0, x, 100));  // Vertical segment from (x,0) to (x,100)
        }

        return segments;
    }

    /**
     * Measures time for bulk construction
     */
    private static long measureBulkConstruction(ArrayList<Segment> segments) {
        long startTime = System.nanoTime();
        BSPTree tree = new BSPTree(segments);
        long endTime = System.nanoTime();
        return endTime - startTime;
    }

    /**
     * Measures time for incremental construction
     */
    private static long measureIncrementalConstruction(ArrayList<Segment> segments) {
        long startTime = System.nanoTime();
        BSPTree tree = new BSPTree();
        for (Segment segment : segments) {
            tree.insert(segment);
        }
        long endTime = System.nanoTime();
        return endTime - startTime;
    }

    /**
     * Runs the experiment for various input sizes
     */
    public static void main(String[] args) {
        // Test with different sizes
        int[] sizes = {10, 20, 50, 100, 200, 500, 1000, 2000, 5000};

        System.out.println("Size\tBulk(ms)\tIncremental(ms)");
        System.out.println("------------------------------------");

        for (int size : sizes) {
            ArrayList<Segment> segments = generateWorstCaseSegments(size);

            // Run each test multiple times and take average to reduce noise
            int numTrials = 5;
            long totalBulkTime = 0;
            long totalIncrementalTime = 0;

            for (int trial = 0; trial < numTrials; trial++) {
                totalBulkTime += measureBulkConstruction(segments);
                totalIncrementalTime += measureIncrementalConstruction(segments);
            }

            double avgBulkMs = (totalBulkTime / numTrials) / 1_000_000.0;
            double avgIncrementalMs = (totalIncrementalTime / numTrials) / 1_000_000.0;

            System.out.printf("%d\t%.2f\t%.2f%n", size, avgBulkMs, avgIncrementalMs);
        }
    }
}
