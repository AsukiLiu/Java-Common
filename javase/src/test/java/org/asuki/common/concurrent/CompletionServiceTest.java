package org.asuki.common.concurrent;

import static java.lang.System.out;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.IntStream;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class CompletionServiceTest {

    private static final int RETURN_VALUE = 3;
    private ExecutorService service;
    private CompletionService<Integer> completionService;

    private Function<Integer, Integer> process = i -> {
        int randomValue = (int) (Math.random() * 10);
        try {
            TimeUnit.SECONDS.sleep(randomValue);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return i;
    };

    private Callable<Integer> task = () -> {
        TimeUnit.SECONDS.sleep(1);
        return RETURN_VALUE;
    };

    @BeforeMethod
    public void setUp() {
        service = Executors.newFixedThreadPool(10);
        completionService = new ExecutorCompletionService<>(service);
    }

    @AfterMethod
    public void tearDown() {
        service.shutdown();
    }

    @Test
    public void shouldTakeFuture() throws Exception {
        // ExecutorService
        Future<Integer> future = service.submit(task);
        assertThat(future.get(), is(RETURN_VALUE));

        // ForkJoinPool
        ForkJoinTask<Integer> forkJoinTask = ForkJoinPool.commonPool().submit(
                task);
        TimeUnit.MILLISECONDS.sleep(100);
        forkJoinTask.complete(5);
        assertThat(forkJoinTask.get(), is(5));
        TimeUnit.SECONDS.sleep(2);
        assertThat(forkJoinTask.get(), is(RETURN_VALUE));

        // CompletionService
        completionService.submit(task);
        future = completionService.take();
        assertThat(future.get(), is(RETURN_VALUE));
    }

    @Test
    public void shouldTakeFutures() throws Exception {
        List<Future<Integer>> futures = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            CallableImpl task = new CallableImpl(i);
            futures.add(completionService.submit(task));
        }

        for (Future<Integer> future : futures) {
            // order by index
            // Integer result = future.get();

            // order by process
            Integer result = completionService.take().get();
            out.println(result);
        }

        out.println("DONE");
    }

    @Test
    public void shouldTakeFuturesByLambda() throws Exception {
        List<Future<Integer>> futures = new ArrayList<>();

        IntStream.range(0, 10).forEach(i -> {
            futures.add(completionService.submit(() -> process.apply(i)));
        });

        futures.stream().mapToInt(future -> {
            try {
                // order by index
                // return future.get();

                // order by process
                return completionService.take().get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).forEach(out::println);

        out.println("DONE");
    }

    private class CallableImpl implements Callable<Integer> {
        private final int index;

        public CallableImpl(int index) {
            this.index = index;
        }

        @Override
        public Integer call() throws Exception {
            return process.apply(index);
        }
    }

}
