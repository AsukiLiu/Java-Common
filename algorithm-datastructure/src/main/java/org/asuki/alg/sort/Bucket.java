package org.asuki.alg.sort;

public class Bucket {

    public static void bucketSort(int[] array, int max) {

        if (array == null || max < 1) {
            return;
        }

        int[] buckets = new int[max];

        // 1. set value to buckets
        for (int i = 0, len = array.length; i < len; i++) {
            buckets[array[i]]++;
        }

        // 2. get value from buckets
        for (int i = 0, j = 0; i < max; i++) {
            while ((buckets[i]--) > 0) {
                array[j++] = i;
            }
        }

        buckets = null;
    }

}
