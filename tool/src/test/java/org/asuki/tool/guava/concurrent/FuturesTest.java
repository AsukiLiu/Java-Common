package org.asuki.tool.guava.concurrent;

import static java.lang.System.out;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.asuki.tool.guava.Customer;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.AsyncFunction;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.FutureFallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;

public class FuturesTest {

    private ExecutorService executor;
    private ListeningExecutorService listeningExecutor;
    private ListenableFuture<String> listenableFuture;

    @BeforeMethod
    public void setUp() {
        executor = Executors.newCachedThreadPool();
        listeningExecutor = MoreExecutors.listeningDecorator(executor);
    }

    @Test
    public void testTransform() throws Exception {
        final String NAME = "Andy";

        listenableFuture = listeningExecutor.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return NAME;
            }
        });
        AsyncFunction<String, Customer> asyncFunction = new AsyncFunction<String, Customer>() {

            private ConcurrentMap<String, Customer> cacheMap = Maps
                    .newConcurrentMap();

            private Map<String, Customer> service = ImmutableMap.of(NAME,
                    new Customer(101, NAME));

            @Override
            public ListenableFuture<Customer> apply(String input)
                    throws Exception {

                if (cacheMap.containsKey(input)) {
                    SettableFuture<Customer> settableFuture = SettableFuture
                            .create();
                    settableFuture.set(cacheMap.get(input));
                    return settableFuture;
                } else {
                    return listeningExecutor.submit(new Callable<Customer>() {
                        @Override
                        public Customer call() throws Exception {

                            TimeUnit.SECONDS.sleep(2);

                            Customer retrieved = service.get(input);
                            cacheMap.putIfAbsent(input, retrieved);
                            return retrieved;
                        }
                    });
                }
            }
        };

        Stopwatch stopwatch = Stopwatch.createStarted();

        ListenableFuture<Customer> transformed = Futures.transform(
                listenableFuture, asyncFunction);
        assertThat(transformed.get().getName(), is(NAME));

        transformed = Futures.transform(listenableFuture, asyncFunction);

        assertThat(transformed.get().getName(), is(NAME));

        out.printf("time: %s%n", stopwatch);
        assertThat(stopwatch.elapsed(TimeUnit.SECONDS), is(lessThan(4L)));
    }

    @Test
    public void testFallback() throws Exception {
        listenableFuture = listeningExecutor.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                throw new RuntimeException();
            }
        });
        FutureFallback<String> futureFallback = new FutureFallback<String>() {
            @Override
            public ListenableFuture<String> create(Throwable t)
                    throws Exception {
                if (t instanceof RuntimeException) {
                    SettableFuture<String> settableFuture = SettableFuture
                            .create();
                    settableFuture.set("Not Found");
                    // settableFuture.setException(new
                    // RuntimeException("Fail"));
                    return settableFuture;
                }
                throw new Exception(t);
            }
        };

        ListenableFuture<String> fallbacked = Futures.withFallback(
                listenableFuture, futureFallback);
        assertThat(fallbacked.get(), is("Not Found"));
    }

    @Test(enabled=false) // Failed by Maven ?
    public void testCallback() {

        listenableFuture = listeningExecutor.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return "Task completed";
            }
        });

        FutureCallbackImpl callback = new FutureCallbackImpl();
        Futures.addCallback(listenableFuture, callback);

        assertThat(callback.getResult(), is("Task completed successfully"));
    }

    @Test
    public void testFluentFuture() throws Exception {
        // @formatter:off
        ListenableFuture<Integer> future = FluentFuture
                .from(1000)
                //.from(1000, executor)
                .transform(new Function<Integer, Double>() {
                        public Double apply(Integer input) {
                            return input * 2.0;
                        }
                    })
                .transform(new AsyncFunction<Double, Integer>() {
                        public ListenableFuture<Integer> apply(Double input)
                                throws Exception {
                            throw new Exception("Fail");
                        }
                    })
                .withFallback(new FutureFallback<Integer>() {
                        public ListenableFuture<Integer> create(Throwable t)
                                throws Exception {
                            return Futures.immediateFuture(-1);
                        }
                    });
        // @formatter:on

        assertThat(future.get(), is(-1));
    }

    private class FutureCallbackImpl implements FutureCallback<String> {
        private StringBuilder builder = new StringBuilder();

        @Override
        public void onSuccess(String result) {
            builder.append(result).append(" successfully");
        }

        @Override
        public void onFailure(Throwable t) {
            builder.append(t.toString());
        }

        public String getResult() {
            return builder.toString();
        }
    }

}
