package org.asuki.tool.guava.concurrent;

import static java.lang.System.out;
import static java.lang.Thread.currentThread;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

public class ListenableFutureTest {

    private static final int NUM_THREADS = 5;

    private ListeningExecutorService executorService;

    private CountDownLatch startSignal;
    private CountDownLatch endSignal;

    private boolean isCallbackRan;

    @BeforeMethod
    public void setUp() {
        executorService = MoreExecutors.listeningDecorator(Executors
                .newFixedThreadPool(NUM_THREADS));

        startSignal = new CountDownLatch(1);
        endSignal = new CountDownLatch(1);

        isCallbackRan = false;
    }

    @AfterMethod
    public void tearDown() {
        executorService.shutdownNow();
    }

    @Test
    public void testAddListener() throws Exception {

        ListenableFuture<String> futureTask = executorService.submit(new Task(
                startSignal));

        futureTask.addListener(new Runnable() {
            // Completed, No return value
            @Override
            public void run() {
                isCallbackRan = true;
                endSignal.countDown();
            }
        }, executorService);

        startSignal.countDown();
        endSignal.await();

        assertThat(isCallbackRan, is(true));
    }

    @Test
    public void testAddListeners() throws Exception {

        ListenableFuture<String> futureTask = executorService
                .submit(new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        return " Task completed";
                    }
                });

        futureTask.addListener(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                out.println(currentThread().getName() + futureTask.get());

                TimeUnit.SECONDS.sleep(1);

                out.println("job1 completed");
            }
        }, executorService);// MoreExecutors.sameThreadExecutor());

        futureTask.addListener(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                out.println(currentThread().getName() + futureTask.get());

                TimeUnit.SECONDS.sleep(1);

                out.println("job2 completed");
            }
        }, executorService);// MoreExecutors.sameThreadExecutor());

        TimeUnit.SECONDS.sleep(2);
    }

    @Test(dataProvider = "taskData")
    public void testFutureCallback(CountDownLatch signal, String expected)
            throws Exception {

        ListenableFuture<String> futureTask = executorService.submit(new Task(
                signal));

        FutureCallbackImpl callback = new FutureCallbackImpl();
        Futures.addCallback(futureTask, callback);

        startSignal.countDown();
        endSignal.await();

        assertThat(callback.getCallbackResult(), is(expected));
    }

    private class FutureCallbackImpl implements FutureCallback<String> {

        private StringBuilder sb = new StringBuilder();

        @Override
        public void onSuccess(String result) {
            sb.append(result);
            endSignal.countDown();
        }

        @Override
        public void onFailure(Throwable t) {
            sb.append(t.toString());
            endSignal.countDown();
        }

        public String getCallbackResult() {
            return sb.toString();
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    private class Task implements Callable<String> {

        private CountDownLatch start;

        @Override
        public String call() throws Exception {

            start.await(1, TimeUnit.SECONDS);

            TimeUnit.SECONDS.sleep(2);

            return "Task Done";
        }
    }

    @DataProvider(name = "taskData")
    private Object[][] taskData() {
        // @formatter:off
        return new Object[][] { 
                { new CountDownLatch(1), "Task Done" },
                { null, "java.lang.NullPointerException" } };
        // @formatter:on
    }

}
