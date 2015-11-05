package org.asuki.common.synchronizer;

import static java.lang.String.format;
import static java.lang.System.out;
import static java.lang.Thread.currentThread;
import static java.util.stream.IntStream.range;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

import org.testng.annotations.Test;

public class SemaphoreTest {

    private static final int THREAD_NUM = 3;

    @SneakyThrows
    @Test
    public void test() {
        final ExecutorService service = Executors.newCachedThreadPool();

        // Limit the number of threads
        final Semaphore sp = new Semaphore(THREAD_NUM);

        range(0, 5).forEach(m -> service.execute(new Worker(sp)));

        TimeUnit.SECONDS.sleep(10);
    }

    @AllArgsConstructor
    private static class Worker implements Runnable {

        private Semaphore sp;

        @SneakyThrows
        @Override
        public void run() {
            sp.acquire();

            out.println(format("%s entered! Current threads:%d",
                    currentThread().getName(),
                    (THREAD_NUM - sp.availablePermits())));

            TimeUnit.SECONDS.sleep((int) (Math.random() * 10));

            sp.release();
            out.println(format("%s left! Current threads:%d", currentThread()
                    .getName(), (THREAD_NUM - sp.availablePermits())));
        }

    }
}
