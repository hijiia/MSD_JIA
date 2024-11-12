package lab03;

import java.util.SortedSet;
import java.util.TreeSet;
import java.io.PrintWriter;

public class ContainsTimer extends TimerTemplate {
    private SortedSet<Integer> set;

    public ContainsTimer(int[] problemSizes, int numTrials) {
        super(problemSizes, numTrials);
        set = new TreeSet<>();
    }

    @Override
    protected void setup(int n) {
        set.clear();
        for (int i = 0; i < n; i++) {
            set.add(i);
        }
    }

    @Override
    protected void timingIteration(int n) {
        set.contains(n - 1);
    }

    @Override
    protected void compensationIteration(int n) {
        // Leave blank or use an alternative low-cost operation if needed
    }

    public static void main(String[] args) {
        int[] problemSizes = new int[11];
        for (int i = 0; i < problemSizes.length; i++) {
            problemSizes[i] = (int) Math.pow(2, 10 + i);
        }

        var timer = new ContainsTimer(problemSizes, 1000);
        var results = timer.run();

        try (PrintWriter writer = new PrintWriter("data.csv")) {
            writer.println("n, time");
            for (var result : results) {
                writer.println(result.n() + ", " + result.avgNanoSecs());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("CSV file generated: data.csv");
    }
}