package assignment09;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class TimeCollision {
    /**
     * Generates a random set of segments within a bounded area
     */
    private static ArrayList<Segment> generateRandomSegments(int n, Random rand) {
        ArrayList<Segment> segments = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            double x1 = rand.nextDouble() * 1000;
            double y1 = rand.nextDouble() * 1000;
            double x2 = x1 + rand.nextDouble() * 100 - 50; // +/- 50 from x1
            double y2 = y1 + rand.nextDouble() * 100 - 50; // +/- 50 from y1
            segments.add(new Segment(x1, y1, x2, y2));
        }
        return segments;
    }

    /**
     * Generates a query segment that will intersect with a target segment
     */
    private static Segment generateIntersectingQuery(Segment target, Random rand) {
        // Create a segment that crosses the middle of the target segment
        double midX = (target.x1() + target.x2()) / 2;
        double midY = (target.y1() + target.y2()) / 2;

        // Create a perpendicular segment through the midpoint
        double dx = target.x2() - target.x1();
        double dy = target.y2() - target.y1();
        double length = Math.sqrt(dx * dx + dy * dy);

        // Perpendicular vector
        double perpX = -dy / length * 100;  // Scale to length 100
        double perpY = dx / length * 100;

        return new Segment(
                midX - perpX, midY - perpY,
                midX + perpX, midY + perpY
        );
    }

    /**
     * Measures time for optimized collision detection
     */
    private static long measureOptimizedCollision(BSPTree tree, Segment query) {
        long startTime = System.nanoTime();
        Segment result = tree.collision(query);
        long endTime = System.nanoTime();
        return endTime - startTime;
    }

    /**
     * Measures time for brute force collision detection using traverseFarToNear
     */
    private static long measureBruteForceCollision(BSPTree tree, Segment query) {
        AtomicBoolean collisionFound = new AtomicBoolean(false);

        long startTime = System.nanoTime();
        tree.traverseFarToNear(0, 0, (segment) -> {
            if (segment.intersects(query)) {
                collisionFound.set(true);
            }
        });
        long endTime = System.nanoTime();

        return endTime - startTime;
    }

    /**
     * Runs the experiment with different tree sizes and query types
     */
    public static void main(String[] args) {
        Random rand = new Random(42); // Fixed seed for reproducibility
        int[] sizes = {100, 200, 500, 1000, 2000, 5000, 10000};
        int trialsPerSize = 100;  // Number of queries to average over

        System.out.println("Size\tOptimized(µs)\tBruteForce(µs)\tRatio");
        System.out.println("------------------------------------------------");

        for (int size : sizes) {
            // Generate segments and build tree
            ArrayList<Segment> segments = generateRandomSegments(size, rand);
            BSPTree tree = new BSPTree(segments);

            long totalOptimizedTime = 0;
            long totalBruteForceTime = 0;

            // Run multiple trials with different queries
            for (int trial = 0; trial < trialsPerSize; trial++) {
                // Generate a query that definitely intersects with a random segment
                Segment targetSegment = segments.get(rand.nextInt(segments.size()));
                Segment query = generateIntersectingQuery(targetSegment, rand);

                totalOptimizedTime += measureOptimizedCollision(tree, query);
                totalBruteForceTime += measureBruteForceCollision(tree, query);
            }

            // Calculate averages
            double avgOptimizedUs = (totalOptimizedTime / trialsPerSize) / 1000.0;  // Convert ns to µs
            double avgBruteForceUs = (totalBruteForceTime / trialsPerSize) / 1000.0;
            double ratio = avgBruteForceUs / avgOptimizedUs;

            System.out.printf("%d\t%.2f\t%.2f\t%.2f%n",
                    size, avgOptimizedUs, avgBruteForceUs, ratio);
        }
    }
}
