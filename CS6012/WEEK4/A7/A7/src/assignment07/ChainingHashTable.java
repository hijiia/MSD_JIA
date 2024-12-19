package assignment07;

import java.util.Collection;
import java.util.LinkedList;

public class ChainingHashTable implements Set<String>{
    private int capacity;
    private LinkedList<String>[] storage;
    private HashFunctor functor;
    private int size;

    public ChainingHashTable(int capacity, HashFunctor functor) {
        this.capacity = capacity;
        this.functor = functor;
        size = 0;
        storage = (LinkedList<String>[]) new LinkedList[capacity];
        for (int i = 0; i < capacity; i++) {
            storage[i] = new LinkedList<>();
        }
    }

    /**
     * Ensures that this set contains the specified item.
     *
     * @param item
     *          - the item whose presence is ensured in this set
     * @return true if this set changed as a result of this method call (that is, if
     *         the input item was actually inserted); otherwise, returns false
     */
    @Override
    public boolean add(String item) {
        if (contains(item) || item == null) {
            return false;
        }
        if (size == capacity) {
            expandCapacity();
        }
        int initialSize = size;
        int index = functor.hash(item) % capacity;
        storage[index].add(item);
        size ++;
        return initialSize != size;
    }

    private void expandCapacity() {
        this.capacity *= 2;
    }

    /**
     * Ensures that this set contains all items in the specified collection.
     *
     * @param items
     *          - the collection of items whose presence is ensured in this set
     * @return true if this set changed as a result of this method call (that is, if
     *         any item in the input collection was actually inserted); otherwise,
     *         returns false
     */
    @Override
    public boolean addAll(Collection<? extends String> items) {
        if (containsAll(items)) {
            return false;
        }
        int initialSize = size;
        for (String item : items) {
            int index = functor.hash(item) % capacity;
            storage[index].add(item);
            size ++;
        }
        return initialSize != size;
    }

    /**
     * Removes all items from this set. The set will be empty after this method
     * call.
     */
    @Override
    public void clear() {
        storage[0].clear();
        size = 0;
    }

    /**
     * Determines if there is an item in this set that is equal to the specified
     * item.
     *
     * @param item
     *          - the item sought in this set
     * @return true if there is an item in this set that is equal to the input item;
     *         otherwise, returns false
     */
    @Override
    public boolean contains(String item) {
        if (isEmpty() || item == null) {
            return false;
        }
        int index = functor.hash(item) % capacity;
        return storage[index].contains(item);
    }

    /**
     * Determines if for each item in the specified collection, there is an item in
     * this set that is equal to it.
     *
     * @param items
     *          - the collection of items sought in this set
     * @return true if for each item in the specified collection, there is an item
     *         in this set that is equal to it; otherwise, returns false
     */
    @Override
    public boolean containsAll(Collection<? extends String> items) {
        if (isEmpty()) {
            return false;
        }
        for (String item : items) {
            contains(item);
        }
        return true;
    }

    /**
     * Returns true if this set contains no items.
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Ensures that this set does not contain the specified item.
     *
     * @param item
     *          - the item whose absence is ensured in this set
     * @return true if this set changed as a result of this method call (that is, if
     *         the input item was actually removed); otherwise, returns false
     */
    @Override
    public boolean remove(String item) {
        if (!contains(item)) {
            return false;
        }
        int initialSize = size;
        int index = functor.hash(item) % capacity;
        storage[index].remove(item);
        size --;
        return initialSize != size;
    }

    /**
     * Ensures that this set does not contain any of the items in the specified
     * collection.
     *
     * @param items
     *          - the collection of items whose absence is ensured in this set
     * @return true if this set changed as a result of this method call (that is, if
     *         any item in the input collection was actually removed); otherwise,
     *         returns false
     */
    @Override
    public boolean removeAll(Collection<? extends String> items) {
        int initialSize = size;
        for (String item : items) {
            remove(item);
        }
        return initialSize != size;
    }

    /**
     * Returns the number of items in this set.
     */
    @Override
    public int size() {
        return size;
    }
}
