package assignment05;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class SinglyLinkedList<E> implements List<E> {
    private static class Node<E> {
        E data;
        Node<E> next;
        Node(E data) {
            this.data = data;
            this.next = null;
        }
    }
    private Node<E> head;
    private int size;
    SinglyLinkedList() {
        head = null;
        size = 0;
    }

    /**
     * Inserts an element at the beginning of the list.
     * O(1) for a singly-linked list.
     *
     * @param element - the element to add
     */
    @Override
    public void insertFirst(E element) {
        Node<E> newNode = new Node<>(element);
        newNode.next = head;
        head = newNode;
        size++;
    }

    /**
     * Inserts an element at a specific position in the list.
     * O(N) for a singly-linked list.
     *
     * @param index - the specified position
     * @param element - the element to add
     * @throws IndexOutOfBoundsException if index is out of range (index < 0 || index > size())
     */
    @Override
    public void insert(int index, E element) throws IndexOutOfBoundsException {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException();
        }
        Node<E> newNode = new Node<>(element);
        if (index == 0) {
            newNode.next = head;
            head = newNode;
        } else {
            Node<E> current = head;
            for (int i = 0; i < index - 1; i++) {
                current = current.next;
            }
            newNode.next = current.next;
            current.next = newNode;
        }
        size++;
    }

    /**
     * Gets the first element in the list.
     * O(1) for a singly-linked list.
     *
     * @return the first element in the list
     * @throws NoSuchElementException if the list is empty
     */
    @Override
    public E getFirst() throws NoSuchElementException {
        if (size == 0) {
            throw new NoSuchElementException();
        }
        return head.data;
    }

    /**
     * Gets the element at a specific position in the list.
     * O(N) for a singly-linked list.
     *
     * @param index - the specified position
     * @return the element at the position
     * @throws IndexOutOfBoundsException if index is out of range (index < 0 || index >= size())
     */
    @Override
    public E get(int index) throws IndexOutOfBoundsException {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        Node<E> current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current.data;
    }

    /**
     * Deletes and returns the first element from the list.
     * O(1) for a singly-linked list.
     *
     * @return the first element
     * @throws NoSuchElementException if the list is empty
     */
    @Override
    public E deleteFirst() throws NoSuchElementException {
        if (size == 0) {
            throw new NoSuchElementException();
        }
        E data = head.data;
        head = head.next;
        size--;
        return data;
    }

    /**
     * Deletes and returns the element at a specific position in the list.
     * O(N) for a singly-linked list.
     *
     * @param index - the specified position
     * @return the element at the position
     * @throws IndexOutOfBoundsException if index is out of range (index < 0 || index >= size())
     */
    @Override
    public E delete(int index) throws IndexOutOfBoundsException {
        if (index < 0 || index > size - 1) {
            throw new IndexOutOfBoundsException();
        }
        if (index == 0) {
            return deleteFirst();
        }
        Node<E> current = head;
        for (int i = 0; i < index - 1; i++) {
            current = current.next;
        }
        E data = current.next.data;
        current.next = current.next.next;
        size--;
        return data;
    }


    /**
     *
     * @param element - the specific element for search
     * @return the index of the element position
     */
    @Override
    public int indexOf(E element) {
        Node<E> current = head;
        for (int i = 0; i < size; i++) {
            if (current.data.equals(element)) {
                return i;
            }
            current = current.next;
        }
        return -1;
    }

    /**
     * O(1) for a singly-linked list.
     *
     * @return the number of elements in this list
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * O(1) for a singly-linked list.
     *
     * @return true if this collection contains no elements; false, otherwise
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Removes all of the elements from this list.
     * O(1) for a singly-linked list.
     */
    @Override
    public void clear() {
        head = null;
        size = 0;
    }

    /**
     * Generates an array containing all of the elements in this list in proper sequence
     * (from first element to last element).
     * O(N) for a singly-linked list.
     *
     * @return an array containing all of the elements in this list, in order
     */
    @Override
    public E[] toArray() {
        E[] array = (E[]) new Object[size];
        Node<E> current = head;
        for (int i = 0; i < size; i++) {
            array[i] = current.data;
            current = current.next;
        }
        return array;
    }

    /**
     * @return an iterator over the elements in this list in proper sequence (from first
     * element to last element)
     */
    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private Node<E> current = head;
            private Node<E> previous = null;
            private Node<E> nodeRemove = null;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public E next() {
                nodeRemove = current;
                E data = current.data;
                previous = current;
                current = current.next;
                return data;
            }

            @Override
            public void remove() {
                if (previous == null) {
                    throw new IllegalStateException("You must call next() before you can remove an element.");
                }

                if (previous == head) { // if removeFirst
                    head = head.next;
                } else {
                    Node<E> temp = head;
                    while (temp.next != previous) {
                        temp = temp.next;
                    }
                    temp.next = previous.next;
                }
                size--;
                previous = null;
            }
        };
    }
}