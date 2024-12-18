package assignment05;

public class TimingExperiment {
    public static void main(String[] args) {
        int[] sizes = {1000, 5000, 10000, 20000}; // Test different stack sizes

        System.out.println("Size\tArrayPush\tLinkedPush\tArrayPop\tLinkedPop\tArrayPeek\tLinkedPeek");

        for (int size : sizes) {
            ArrayStack<Integer> arrayStack = new ArrayStack<>();
            LinkedListStack<Integer> linkedStack = new LinkedListStack<>();

            // push
            long startTime = System.nanoTime();
            for (int i = 0; i < size; i++) {
                arrayStack.push(i);
            }
            long arrayPushTime = System.nanoTime() - startTime;

            startTime = System.nanoTime();
            for (int i = 0; i < size; i++) {
                linkedStack.push(i);
            }
            long linkedPushTime = System.nanoTime() - startTime;

            //peek performance
            startTime = System.nanoTime();
            for (int i = 0; i < size; i++) {
                arrayStack.peek();
            }
            long arrayPeekTime = System.nanoTime() - startTime;

            startTime = System.nanoTime();
            for (int i = 0; i < size; i++) {
                linkedStack.peek();
            }
            long linkedPeekTime = System.nanoTime() - startTime;

            // pop
            startTime = System.nanoTime();
            while (!arrayStack.isEmpty()) {
                arrayStack.pop();
            }
            long arrayPopTime = System.nanoTime() - startTime;

            startTime = System.nanoTime();
            while (!linkedStack.isEmpty()) {
                linkedStack.pop();
            }
            long linkedPopTime = System.nanoTime() - startTime;

            //milliseconds
            System.out.printf("%d\t%.2f\t%.2f\t%.2f\t%.2f\t%.2f\t%.2f%n",
                    size,
                    arrayPushTime / 1e6, linkedPushTime / 1e6,
                    arrayPopTime / 1e6, linkedPopTime / 1e6,
                    arrayPeekTime / 1e6, linkedPeekTime / 1e6);
        }
    }
}

//ArrayPush and LinkedPush are relatively close. As the stack size increases,
// the push times also increase, but the rate of increase is relatively slow.
//The ArrayStack performs better for pop and peek operations, especially when the stack size is smaller.
//ArrayPeek is consistently faster than LinkedPeek,
// the time difference is relatively small compared to push and pop operations,
// especially at larger sizes