package org.asuki.alg.sort;

public class Insert {

    public static void insertSort(int[] array) {
        final int len = array.length;
        int j, k;

        for (int i = 1; i < len; i++) {

            // find a location in the ordered array[0..i-1] for array[i]
            for (j = i - 1; j >= 0; j--) {
                if (array[j] < array[i]) {
                    break;
                }
            }

            if (j != i - 1) {
                // move values that are greater than array[i] backward
                int tmp = array[i];
                for (k = i - 1; k > j; k--) {
                    array[k + 1] = array[k];
                }
                array[k + 1] = tmp;
            }
        }
    }
}
