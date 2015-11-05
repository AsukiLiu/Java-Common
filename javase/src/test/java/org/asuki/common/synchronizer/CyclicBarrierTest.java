package org.asuki.common.synchronizer;

import static java.lang.String.format;
import static java.lang.System.out;
import static java.lang.Thread.currentThread;

import java.util.Random;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

import org.testng.annotations.Test;

public class CyclicBarrierTest {

    private static final int THREAD_NUM = 2;

    @SneakyThrows
    @Test
    public void test() {
        CyclicBarrier cb = new CyclicBarrier(THREAD_NUM,
                () -> out.println("do barrier action"));

        new Thread(new Worker(cb), "Worker1").start();
        new Thread(new Worker(cb), "Worker2").start();

        TimeUnit.SECONDS.sleep(10);
    }

    @AllArgsConstructor
    private static class Worker implements Runnable {
        private CyclicBarrier cb;

        @SneakyThrows
        @Override
        public void run() {
            TimeUnit.SECONDS.sleep(new Random().nextInt(10));
            out.println(format("%s arrived!", currentThread().getName()));

            // Waiting for all threads
            cb.await();

            out.println("do something per thread");
        }
    }
}
