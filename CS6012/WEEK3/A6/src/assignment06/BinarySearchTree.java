package assignment06;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.NoSuchElementException;

public class BinarySearchTree<T extends Comparable<? super T>> implements SortedSet<T>{

    private BinaryNode root;
    private int size;
    private final Comparator<? super T> comparator;

    public BinarySearchTree() {
        this.comparator = null;
        this.size = 0;
        this.root = null;
    }

    public BinarySearchTree(Comparator<? super T> comparator) {
        this.comparator = comparator;
        this.size = 0;
        this.root = null;
    }

    private class BinaryNode {
        T data;
        BinaryNode left;
        BinaryNode right;

        BinaryNode(T data) {
            this.data = data;
        }
    }

    /**
     * Ensures that this set contains the specified item.
     *
     * @param item
     *          - the item whose presence is ensured in this set
     * @return true if this set changed as a result of this method call (that is, if
     *         the input item was actually inserted); otherwise, returns false
     * @throws NullPointerException
     *           if the item is null
     */
    @Override
    public boolean add(T item) {
        if (item == null) {
            throw new NullPointerException("Item cannot be null");
        }
        // set first if no tree exist
        if (root == null) {
            root = new BinaryNode(item);
            size++;
            return true;
        }

        // start from the root
        BinaryNode current = root;
        BinaryNode parent = null;

        while (current != null) {
            int comparison = item.compareTo(current.data);
            if (comparison == 0) { //equal == exist
                return false;
            }
            parent = current;
            if (comparison < 0) {
                current = current.left;
            } else {
                current = current.right;
            }
        }
        if (item.compareTo(parent.data) < 0) { // small to left
            parent.left = new BinaryNode(item);
        } else { // bigger to right
            parent.right = new BinaryNode(item);
        }
        size++;
        return true;
    }

    /**
     * Ensures that this set contains all items in the specified collection.
     *
     * @param items
     *          - the collection of items whose presence is ensured in this set
     * @return true if this set changed as a result of this method call (that is, if
     *         any item in the input collection was actually inserted); otherwise,
     *         returns false
     * @throws NullPointerException
     *           if any of the items is null
     */
    @Override
    public boolean addAll(Collection<? extends T> items) {
        if (items == null) {
            throw new NullPointerException("The collection cannot be null");
        }
        boolean isAdded = false;
        for (T item : items) {
            if (item == null) {
                throw new NullPointerException("Items in the collection cannot be null");
            }
            if (add(item)) {
                isAdded = true;
            }
        }
        return isAdded;
    }

    /**
     * Removes all items from this set. The set will be empty after this method
     * call.
     */
    @Override
    public void clear() {
        root = null;
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
     * @throws NullPointerException
     *           if the item is null
     */
    @Override
    public boolean contains(T item) {
        if (item == null) {
            throw new NullPointerException("Item cannot be null");
        }
        BinaryNode current = root;
        while (current != null) {
            int comparison = item.compareTo(current.data);
            if (comparison == 0) {
                return true;
            } else if (comparison < 0) { // if smaller, find the left
                current = current.left;
            } else {
                current = current.right;
            }
        }
        return false;
    }

    /**
     * Determines if for each item in the specified collection, there is an item in
     * this set that is equal to it.
     *
     * @param items
     *          - the collection of items sought in this set
     * @return true if for each item in the specified collection, there is an item
     *         in this set that is equal to it; otherwise, returns false
     * @throws NullPointerException
     *           if any of the items is null
     */
    @Override
    public boolean containsAll(Collection<? extends T> items) {
        if (items == null) {
            throw new NullPointerException("The collection cannot be null");
        }

        for (T item : items) {
            if (!contains(item)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the first (i.e., smallest) item in this set.
     *
     * @throws NoSuchElementException
     *           if the set is empty
     */
    @Override
    public T first() throws NoSuchElementException {
        if (root == null) {
            throw new NoSuchElementException("The set is empty");
        }

        // find the most left one
        BinaryNode current = root;
        while (current.left != null) {
            current = current.left;
        }
        return current.data;
    }

    /**
     * Returns true if this set contains no items.
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns the last (i.e., largest) item in this set.
     *
     * @throws NoSuchElementException
     *           if the set is empty
     */
    @Override
    public T last() throws NoSuchElementException {
        if (root == null) {
            throw new NoSuchElementException("The set is empty");
        }

        // find the most right one
        BinaryNode current = root;
        while (current.right != null) {
            current = current.right;
        }
        return current.data;
    }

    /**
     * Ensures that this set does not contain the specified item.
     *
     * @param item
     *          - the item whose absence is ensured in this set
     * @return true if this set changed as a result of this method call (that is, if
     *         the input item was actually removed); otherwise, returns false
     * @throws NullPointerException
     *           if the item is null
     */
    @Override
    public boolean remove(T item) { // can do it recursively
        if (item == null) {
            throw new NullPointerException("Item cannot be null");
        }

        // Find the node to remove and its parent
        BinaryNode parent = null;
        BinaryNode current = root;
        boolean isLeftChild = true;

        // Find the node
        while (current != null) {
            int comparison = item.compareTo(current.data);
            if (comparison == 0) {
                break;
            }
            parent = current;
            if (comparison < 0) {
                current = current.left;
                isLeftChild = true;
            } else {
                current = current.right;
                isLeftChild = false;
            }
        }

        // If item not found, return false
        if (current == null) {
            return false;
        }

        // if Node to be removed has no children
        if (current.left == null && current.right == null) {
            if (current == root) {
                root = null;
            } else if (isLeftChild) {
                parent.left = null;
            } else {
                parent.right = null;
            }
        }
        // if Node has only one child
        else if (current.right == null) { // Only left child
            if (current == root) {
                root = current.left;
            } else if (isLeftChild) {
                parent.left = current.left;
            } else {
                parent.right = current.left;
            }
        }
        else if (current.left == null) { // Only right child
            if (current == root) {
                root = current.right;
            } else if (isLeftChild) {
                parent.left = current.right;
            } else {
                parent.right = current.right;
            }
        }
        // if Node has two children
        else {
            // Find successor
            BinaryNode successor = current.right;
            BinaryNode successorParent = current;

            while (successor.left != null) {
                successorParent = successor;
                successor = successor.left;
            }

            // Replace current node's data with successor's data
            current.data = successor.data;

            // If the successor is the direct right child of the current node
            if (successorParent == current) {
                successorParent.right = successor.right; // Bypass the successor
            } else {
                successorParent.left = successor.right; // Bypass the successor
            }
        }

        size--;
        return true;
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
     * @throws NullPointerException
     *           if any of the items is null
     */
    @Override
    public boolean removeAll(Collection<? extends T> items) {
        if (items == null) {
            throw new NullPointerException("The collection cannot be null");
        }

        boolean isRemoved = false;
        for (T item : items) {
            if (remove(item)) {
                isRemoved = true;
            }
        }
        return isRemoved;
    }

    /**
     * Returns the number of items in this set.
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Returns an ArrayList containing all of the items in this set, in sorted
     * order.
     */
    @Override
    public ArrayList<T> toArrayList() {
        ArrayList<T> arrayList = new ArrayList<>();
        goThroughOneSide(root, arrayList);
        return arrayList;
    }

    // recursive to loop
    private void goThroughOneSide(BinaryNode node, ArrayList<T> list) {
        if (node == null) {
            return;
        }
        goThroughOneSide(node.left, list);
        list.add(node.data);
        goThroughOneSide(node.right, list);
    }
}
