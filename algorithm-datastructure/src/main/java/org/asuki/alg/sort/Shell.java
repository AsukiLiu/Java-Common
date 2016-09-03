package org.asuki.alg.sort;

public class Shell {

    public static void shellSort(int[] array) {
        final int len = array.length;

        for (int gap = len / 2; gap > 0; gap /= 2) {
            // sort per group
            for (int i = 0; i < gap; i++) {
                sortGroup(array, i, gap);
            }
        }
    }

    private static void sortGroup(int[] array, int start, int gap) {
        final int len = array.length;

        for (int j = start + gap; j < len; j += gap) {

            // see insert sort
            if (array[j] < array[j - gap]) {

                int tmp = array[j];
                int k = j - gap;
                while (k >= 0 && array[k] > tmp) {
                    array[k + gap] = array[k];
                    k -= gap;
                }
                array[k + gap] = tmp;
            }
        }

    }

}
