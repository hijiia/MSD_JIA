package assignment03;

import java.util.*;
import java.util.Comparator;


//a generic class BinarySearchSet<E> that implements both our custom SortedSet.java, and the standard library Iterable
public class BinarySearchSet<E> implements SortedSet<E>, Iterable<E> {
    // create an array of Object type，declaration is legal but instantiation is illegal
    private E[] elements;
    int size;
    private Comparator<? super E> comparator;// define a comparator can compare E and parent of E

    //Cast Object[] to E[] + natural order
    public BinarySearchSet() {
        this.size = 0;
        this.elements = (E[]) new Object[10];//String Integer
        this.comparator = null;
    }

    //Cast Object[] to E[] + use custom comparator to order； same method name,parameter different
    public BinarySearchSet(Comparator<? super E> comparator) {
        this.elements = (E[]) new Object[10];
        this.size = 0;
        this.comparator = comparator;
    }

// comparator interface method
    public int compare(E e1, E e2) {
        if (comparator != null) {
            return comparator.compare(e1, e2);//use custom comparator
        } else {
            //cast
            return ((Comparable<E>) e1).compareTo(e2); //e1 implement
        }
    }

    private int binarySearch(E element) {
        int low = 0;
        int high = size - 1;
        while (low <= high) {
            int mid = (low + high) / 2;
            int cmp = compare(element, elements[mid]);//if custom comparator is in
            if (cmp < 0) {
                high = mid - 1;
            } else if (cmp > 0) {
                low = mid + 1;
            } else {
                return mid;
            }

        }
        return -(low + 1);//low
    }

    private void ensureCapacity() {
        if (size >= elements.length) {
            E[] newElements = (E[]) new Object[elements.length * 2];
            System.arraycopy(elements, 0, newElements, 0, size);
            elements = newElements;
        }
    }

    @Override
    public Comparator<? super E> comparator() {
        return comparator;
    }

    @Override
    public E first() throws NoSuchElementException {
        if (size == 0) {
            throw new NoSuchElementException("set is empty");
        }
        return elements[0];
    }

    @Override
    public E last() throws NoSuchElementException {
        if (size == 0) {
            throw new NoSuchElementException("set is empty");
        }
        return elements[size - 1];
    }

    @Override
    public boolean add(E element) {
        if (element == null) throw new NullPointerException("Element cannot be null");

        int index = binarySearch(element);
        if (index >= 0) {
            // Element already exists
            return false;
        }

        ensureCapacity();
        int insertPos = -index - 1;
        System.arraycopy(elements, insertPos, elements, insertPos + 1, size - insertPos);
        elements[insertPos] = element;
        size++;
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends E> elements) {
        boolean changed = false;
        for (E element : elements) {
            if (add(element)) {
                changed = true;
            }
        }
        return changed;
    }

    @Override
    public void clear() {
        elements = (E[]) new Object[size];
        size = 0;
    }

    @Override
    public boolean contains(E element) {
        return binarySearch(element) >= 0;
    }


    @Override
    public boolean containsAll(Collection<? extends E> elements) {
        for (E element : elements) {
            if (!contains(element)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public Iterator<E> iterator() {
        Iterator<E> iterator = new Iterator<>() {
            private int index = 0;
            private boolean canRemove = false;

            @Override
            public boolean hasNext() {
                return index < size;
            }

            @Override
            public E next() {
                if (!hasNext()) {
                    throw new NoSuchElementException("no more elements");
                }
                canRemove = true;
                return elements[index++];
            }

            @Override
            public void remove() {
                if (!canRemove) {
                    throw new IllegalStateException("call remove before next or already removed");
                }
                BinarySearchSet.this.remove(elements[--index]);
                canRemove = false;
            }
        };
        return iterator;
    }

        @Override
        public boolean remove (E element){
            int index = binarySearch(element);
            if (index >= 0) {
                // Element found, shift to remove
                System.arraycopy(elements, index + 1, elements, index, size - index - 1);
                elements[size - 1] = null;  // Clear the last element
                size--;
                return true;
            }
            return false;  // Element not found
        }

        @Override
        public boolean removeAll (Collection < ? extends E > elements){
            boolean changed = false;
            for (E element : elements) {
                if (remove(element)) {
                    changed = true;
                }
            }
            return changed;
        }
        @Override
        public int size () {
            return size;
        }

        @Override
        public E[] toArray () {
            return Arrays.copyOf(elements, size);
        }
    }



