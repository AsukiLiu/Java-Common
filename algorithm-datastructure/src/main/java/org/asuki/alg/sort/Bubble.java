package org.asuki.alg.sort;

public class Bubble {

    public static void bubbleSort1(int[] array) {
        int len = array.length;

        for (int i = len - 1; i > 0; i--) {
            // put max of array[0..i] at the end
            for (int j = 0; j < i; j++) {
                if (array[j] > array[j + 1]) {
                    // swap values
                    int tmp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = tmp;
                }
            }
        }
    }

    public static void bubbleSort2(int[] array) {
        int len = array.length;
        boolean flag;

        for (int i = len - 1; i > 0; i--) {

            flag = false;

            // put max of array[0..i] at the end
            for (int j = 0; j < i; j++) {
                if (array[j] > array[j + 1]) {
                    // swap values
                    int tmp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = tmp;

                    // swap happened
                    flag = true;
                }
            }

            // if swap never happened, then values has been ordered
            if (!flag) {
                break;
            }
        }
    }

}
