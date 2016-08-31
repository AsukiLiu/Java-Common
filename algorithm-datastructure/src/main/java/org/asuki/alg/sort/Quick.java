package org.asuki.alg.sort;

public class Quick {

    public static void quickSort(int[] array, int left, int right) {

        if (left >= right) {
            return;
        }

        int i = left;
        int j = right;
        int x = array[i];

        while (i < j) {
            // get the first value that is less than x from right to left
            while (i < j && array[j] > x) {
                j--;
            }
            if (i < j) {
                array[i++] = array[j];
            }

            // get the first value that is greater than x from left to right
            while (i < j && array[i] < x) {
                i++;
            }
            if (i < j) {
                array[j--] = array[i];
            }
        }

        array[i] = x;

        quickSort(array, left, i - 1);
        quickSort(array, i + 1, right);
    }
}
