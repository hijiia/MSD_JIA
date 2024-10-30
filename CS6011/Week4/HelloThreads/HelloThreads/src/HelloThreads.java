public class HelloThreads {
    public static void sayHello() {
        Thread[] threads = new Thread[10];
        //create each thread
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 1; j < 100; j++) {
                    System.out.println("hello number" + j + " from thread " + Thread.currentThread().getId() + " ");
                    if (j % 10 == 0) {
                        System.out.println();
                    }
                }
            });
            threads[i].start();
        }
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            }
        }
    }
