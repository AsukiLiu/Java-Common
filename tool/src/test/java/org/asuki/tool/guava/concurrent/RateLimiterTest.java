package org.asuki.tool.guava.concurrent;

import static java.lang.System.out;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;

import com.google.common.base.Stopwatch;
import com.google.common.util.concurrent.RateLimiter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class RateLimiterTest {

    private static final int JOB_SUM = 10;
    private static final int TPS = 2;

    private static AtomicInteger counter = new AtomicInteger(0);

    private ExecutorService executor;

    @BeforeMethod
    public void setUp() {
        executor = Executors.newCachedThreadPool();
    }

    @Test
    public void testRateLimiter() {

        RateLimiter rateLimiter = RateLimiter.create(TPS);

        Stopwatch stopwatch = Stopwatch.createStarted();

        for (int i = 0; i < JOB_SUM; i++) {
            rateLimiter.acquire();

            executor.execute(new Runnable() {
                @Override
                public void run() {
                    out.println(Thread.currentThread().getName());

                    int index = counter.incrementAndGet();

                    if (index == JOB_SUM) {
                        out.printf("time: %s%n", stopwatch);
                        assertThat(stopwatch.elapsed(TimeUnit.SECONDS),
                                is(lessThan(5L)));
                    }
                }
            });
        }

    }

}
