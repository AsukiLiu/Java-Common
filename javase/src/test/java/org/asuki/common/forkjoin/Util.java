package org.asuki.common.forkjoin;

import java.util.Random;

public final class Util {

    private Util() {
    }

    public static int[] createRandomArray(int arraySize) {
        Random rand = new Random(123L);
        int[] data = new int[arraySize];
        for (int i = 0; i < data.length; i++) {
            data[i] = rand.nextInt();
        }
        return data;
    }

    public static boolean isSorted(int[] data) {
        int last = Integer.MIN_VALUE;
        for (int value : data) {
            if (value < last) {
                return false;
            }
            last = value;
        }
        return true;
    }

    public static int findMax(int[] data) {
        int max = Integer.MIN_VALUE;
        for (int value : data) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }
}
