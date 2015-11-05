package org.asuki.common.forkjoin;

import static java.lang.System.out;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.util.concurrent.ForkJoinPool;

import org.testng.annotations.Test;

public class ForkJoinTest {

    private static final int ARRAY_SIZE = 10_000_000;

    @Test
    public void testRecursiveTask() {

        int[] data = Util.createRandomArray(ARRAY_SIZE);

        ForkJoinPool pool = new ForkJoinPool();
        Integer result = pool.invoke(new FindMaxTask(data, 0, data.length));

        out.println("Max: " + result);
        assertThat(result.intValue(), is(Util.findMax(data)));
    }

    @Test
    public void testRecursiveAction() {
        // TODO
    }
}
