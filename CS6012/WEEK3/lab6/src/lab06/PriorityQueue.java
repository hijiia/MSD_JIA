package lab06;
//Define a generic priority queue interface that works with any comparable type
public interface PriorityQueue<T extends Comparable<T>> {
    void add(T element);
    T removeMin();
    boolean isEmpty();
}
