public class Danger {
    static int answer = 0;

    public static void badSum(){
        final int[] answer = {0};
        int maxValue = 40000;
        int numThreads = 10;
        Thread[] threads = new Thread[numThreads];
        for (int i = 0; i < threads.length; i++) {
            final int finalI = i;
            threads[i] = new Thread(() -> {
                int start = finalI * maxValue / numThreads;
                int end = Math.min((finalI + 1) * maxValue / numThreads, maxValue);
                for (int j = start; j < end; j++) {
                    answer[0] = answer[0] + j;
                }
            });
            threads[i].start();
        }
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                System.err.println("Thread interrupted: " + e.getMessage());
            }
        }
        int correctAnswer = maxValue * (maxValue - 1) / 2;
        System.out.println("Computed answer: " + answer[0]);
        System.out.println("Correct answer: " + correctAnswer);
    }

    }

