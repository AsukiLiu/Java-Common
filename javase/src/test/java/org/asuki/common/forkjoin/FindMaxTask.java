package org.asuki.common.forkjoin;

import java.util.concurrent.RecursiveTask;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FindMaxTask extends RecursiveTask<Integer> {
    private static final long serialVersionUID = 1L;

    private static final int THRESHOLD = 100_000;

    private int[] array;
    private int start;
    private int end;

    @Override
    protected Integer compute() {
        if (end - start <= THRESHOLD) {
            return doCompute();
        } else {
            int mid = start + (end - start) / 2;

            FindMaxTask left = new FindMaxTask(array, start, mid);
            FindMaxTask right = new FindMaxTask(array, mid, end);

            invokeAll(left, right);

            return Math.max(left.join(), right.join());
        }
    }

    private Integer doCompute() {
        int max = Integer.MIN_VALUE;
        for (int i = start; i < end; i++) {
            if (array[i] > max) {
                max = array[i];
            }
        }
        return max;
    }
}
