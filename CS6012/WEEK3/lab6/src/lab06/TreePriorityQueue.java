package lab06;

import java.util.TreeSet;
//TreeSet-Based Implementation
public class TreePriorityQueue <T extends Comparable<T>> implements PriorityQueue<T>{
    private TreeSet<T> elements;
    public TreePriorityQueue(){
        this.elements = new TreeSet<>();
    }

    @Override
    public void add(T element) {
        elements.add(element);
    }

    @Override
    public T removeMin() {
        return elements.pollFirst();
    }

    @Override
    public boolean isEmpty() {
        return elements.isEmpty();
    }
}
