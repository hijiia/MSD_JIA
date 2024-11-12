package lab02;

import java.util.ArrayList;

public class RemoveFrontTimer extends TimerTemplate {
    private ArrayList<Integer> list;


    public RemoveFrontTimer(int[] problemSizes, int numTrials) {
        super(problemSizes, numTrials);
        list = new ArrayList<>();
    }

    // Fills the ArrayList with n elements
    @Override
    protected void setup(int n) {
        list.clear();
        for (int i = 0; i < n; i++) {
            list.add(i);
        }
    }


    @Override
    protected void timingIteration(int n) {
        if (!list.isEmpty()) {
            list.remove(0);  // Remove the first element
            list.add(n);  // Add an element to maintain the size
        }
    }


    @Override
    protected void compensationIteration(int n) {
        if (!list.isEmpty()) {
            list.set(list.size() - 1, n);  // Modify the last element to simulate the cost of `add()`
        }
    }


    public static void main(String[] args) {
        ArrayList<Integer> ns = new ArrayList<>();
        for (double n = 100; n < 1000000; n *= 1.5) {
            ns.add((int) n);
        }


        int[] problemSizes = new int[ns.size()];
        for (int i = 0; i < problemSizes.length; i++) {
            problemSizes[i] = ns.get(i);
        }


        var timer = new RemoveFrontTimer(problemSizes, 10);
        var results = timer.run();
        System.out.println("n, time");
        for (var result : results) {
            System.out.println(result.n() + ", " + result.avgNanoSecs());
        }
    }
}