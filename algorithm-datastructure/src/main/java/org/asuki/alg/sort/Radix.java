package org.asuki.alg.sort;

import static java.lang.System.arraycopy;

public class Radix {

    // 0..9
    private static final int NUMBERS_SIZE = 10;

    public static void radixSort(int[] array) {
        int max = getMax(array);

        // exp = 1, 10, 100, ...
        for (int exp = 1; max / exp > 0; exp *= NUMBERS_SIZE) {
            sortByExp(array, exp);
        }
    }

    private static int getMax(int[] array) {
        int max = array[0];

        for (int i = 1, len = array.length; i < len; i++) {
            if (array[i] > max) {
                max = array[i];
            }
        }

        return max;
    }

    private static void sortByExp(int[] array, int exp) {
        final int len = array.length;

        int[] tmp = new int[len];
        int[] buckets = new int[NUMBERS_SIZE];

        // see bucket sort
        for (int value : array) {
            buckets[(value / exp) % NUMBERS_SIZE]++;
        }

        // decide the indexes in tmp
        for (int i = 1; i < NUMBERS_SIZE; i++) {
            buckets[i] += buckets[i - 1];
        }

        for (int i = len - 1; i >= 0; i--) {
            int index = (array[i] / exp) % NUMBERS_SIZE;
            tmp[buckets[index] - 1] = array[i];
            buckets[index]--;
        }

        arraycopy(tmp, 0, array, 0, len);

        tmp = null;
        buckets = null;
    }

}
