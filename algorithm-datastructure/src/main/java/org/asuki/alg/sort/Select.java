package org.asuki.alg.sort;

public class Select {

    public static void selectSort(int[] array) {
        final int len = array.length;
        int min;

        for (int i = 0; i < len; i++) {
            min = i;

            // find min between array[i+1] and array[len]
            for (int j = i + 1; j < len; j++) {
                if (array[j] < array[min]) {
                    min = j;
                }
            }

            // after swap, then array[0]..array[i] have been ordered
            if (min != i) {
                int tmp = array[i];
                array[i] = array[min];
                array[min] = tmp;
            }
        }
    }
}
