//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
package assignment04;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;

public class SortUtil {

    // Mergesort driver method
    public static <T> void mergesort(ArrayList<T> list, Comparator<? super T> comparator) {
        if (list == null || list.size() <= 1) return;
        mergesortHelper(list, 0, list.size() - 1, comparator);
    }

    // Recursive helper method for mergesort
    private static <T> void mergesortHelper(ArrayList<T> list, int left, int right, Comparator<? super T> comparator) {
        int threshold = 10; // Change as needed during experimentation
        if (right - left + 1 <= threshold) {
            insertionSort(list, left, right, comparator);
            return;
        }

        if (left < right) {
            int mid = (left + right) / 2;
            mergesortHelper(list, left, mid, comparator);
            mergesortHelper(list, mid + 1, right, comparator);
            merge(list, left, mid, right, comparator);
        }
    }

    // Merge method for mergesort
    private static <T> void merge(ArrayList<T> list, int left, int mid, int right, Comparator<? super T> comparator) {
        ArrayList<T> leftSublist = new ArrayList<>(list.subList(left, mid + 1));
        ArrayList<T> rightSublist = new ArrayList<>(list.subList(mid + 1, right + 1));

        int i = 0, j = 0, k = left;
        while (i < leftSublist.size() && j < rightSublist.size()) {
            if (comparator.compare(leftSublist.get(i), rightSublist.get(j)) <= 0) {
                list.set(k++, leftSublist.get(i++));
            } else {
                list.set(k++, rightSublist.get(j++));
            }
        }
        while (i < leftSublist.size()) list.set(k++, leftSublist.get(i++));
        while (j < rightSublist.size()) list.set(k++, rightSublist.get(j++));
    }

    // Insertion sort method used by mergesort
    private static <T> void insertionSort(ArrayList<T> list, int left, int right, Comparator<? super T> comparator) {
        for (int i = left + 1; i <= right; i++) {
            T key = list.get(i);
            int j = i - 1;
            while (j >= left && comparator.compare(list.get(j), key) > 0) {
                list.set(j + 1, list.get(j));
                j--;
            }
            list.set(j + 1, key);
        }
    }

    // Quicksort driver method
    public static <T> void quicksort(ArrayList<T> list, Comparator<? super T> comparator) {
        if (list == null || list.size() <= 1) return;
        quicksortHelper(list, 0, list.size() - 1, comparator);
    }

    // Recursive helper method for quicksort
    private static <T> void quicksortHelper(ArrayList<T> list, int left, int right, Comparator<? super T> comparator) {
        int threshold = 10; // Optional threshold for insertion sort
        if (right - left + 1 <= threshold) {
            insertionSort(list, left, right, comparator);
            return;
        }

        if (left < right) {
            int pivotIndex = choosePivot(left, right); // You can implement different strategies here
            T pivot = list.get(pivotIndex);
            int partitionIndex = partition(list, left, right, pivot, comparator);
            quicksortHelper(list, left, partitionIndex - 1, comparator);
            quicksortHelper(list, partitionIndex, right, comparator);
        }
    }

    // Partition method for quicksort
    private static <T> int partition(ArrayList<T> list, int left, int right, T pivot, Comparator<? super T> comparator) {
        int i = left;
        int j = right;
        while (i <= j) {
            while (comparator.compare(list.get(i), pivot) < 0) i++;
            while (comparator.compare(list.get(j), pivot) > 0) j--;
            if (i <= j) {
                Collections.swap(list, i, j);
                i++;
                j--;
            }
        }
        return i;
    }

    // Pivot selection strategy
    private static int choosePivot(int left, int right) {
        return left + (right - left) / 2; // Simple median strategy
    }

    // Generate best-case scenario list
    public static ArrayList<Integer> generateBestCase(int size) {
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 1; i <= size; i++) {
            list.add(i);
        }
        return list;
    }

    // Generate average-case scenario list
    public static ArrayList<Integer> generateAverageCase(int size) {
        ArrayList<Integer> list = generateBestCase(size);
        Collections.shuffle(list);
        return list;
    }

    // Generate worst-case scenario list
    public static ArrayList<Integer> generateWorstCase(int size) {
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = size; i >= 1; i--) {
            list.add(i);
        }
        return list;
    }
}