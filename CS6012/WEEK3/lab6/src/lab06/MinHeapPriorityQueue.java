package lab06;

import java.util.ArrayList;
import java.util.Collections;

public class MinHeapPriorityQueue<T extends Comparable<T>> implements PriorityQueue<T> {
    private ArrayList<T> heap;

    public MinHeapPriorityQueue() {
        this.heap = new ArrayList<>();
    }

    public MinHeapPriorityQueue(ArrayList<T> elements) {
        this.heap = new ArrayList<>(elements); // Copy or alias the input list
        heapify();
    }

    @Override
    public void add(T element) {
        heap.add(element);
        percolateUp(heap.size() - 1);
    }

    @Override
    public T removeMin() {
        if (heap.isEmpty()) return null;
        T min = heap.get(0);
        Collections.swap(heap, 0, heap.size() - 1);
        heap.remove(heap.size() - 1);
        percolateDown(0);
        return min;
    }

    @Override
    public boolean isEmpty() {
        return heap.isEmpty();
    }

    private void percolateUp(int index) {
        while (index > 0) {
            int parentIndex = (index - 1) / 2;
            if (heap.get(index).compareTo(heap.get(parentIndex)) < 0) {
                Collections.swap(heap, index, parentIndex);
                index = parentIndex;
            } else {
                break;
            }
        }
    }

    private void percolateDown(int index) {
        int size = heap.size();
        while (true) {
            int leftChild = 2 * index + 1;
            int rightChild = 2 * index + 2;
            int smallest = index;

            if (leftChild < size && heap.get(leftChild).compareTo(heap.get(smallest)) < 0) {
                smallest = leftChild;
            }

            if (rightChild < size && heap.get(rightChild).compareTo(heap.get(smallest)) < 0) {
                smallest = rightChild;
            }

            if (smallest == index) {
                break;
            }

            Collections.swap(heap, index, smallest);
            index = smallest;
        }
    }

    private void heapify() {
        for (int i = heap.size() / 2 - 1; i >= 0; i--) {
            percolateDown(i);
        }
    }
}