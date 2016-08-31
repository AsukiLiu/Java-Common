package org.asuki.alg.sort;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.function.Consumer;

import static org.asuki.alg.sort.Bucket.bucketSort;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class SortTest {

    @Test(dataProvider = "sortData")
    public void testSort(Consumer<int[]> consumer) {
        int[] array = {19, 12, 13, 15, 13, 16, 16, 13, 17};

        consumer.accept(array);

        assertThat(Arrays.toString(array), is("[12, 13, 13, 13, 15, 16, 16, 17, 19]"));
    }

    @DataProvider
    private Object[][] sortData() {
        Consumer<int[]> bucketSort = array -> bucketSort(array, 20);
        Consumer<int[]> bubbleSort1 = Bubble::bubbleSort1;
        Consumer<int[]> bubbleSort2 = Bubble::bubbleSort2;

        return new Object[][]{
                {bucketSort},
                {bubbleSort1},
                {bubbleSort2},
        };
    }
}
