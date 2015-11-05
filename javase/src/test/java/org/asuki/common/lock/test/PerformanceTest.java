package org.asuki.common.lock.test;

import static java.lang.String.format;
import static java.lang.System.out;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

import org.asuki.common.lock.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.base.Joiner;
import com.google.common.base.Joiner.MapJoiner;

public class PerformanceTest {

    private static int NUM_WRITERS;
    private static int NUM_READERS;
    private static long TEST_DURATION = 3;

    private static ExecutorService EXECUTOR;

    // @formatter:off
    private static final Spaceship[] SPACESHIPS = {
        new SynchronizedMethodSpaceship(),
        new SynchronizedBlockSpaceship(),

        new ReadWriteLockSpaceShip(),
        new ReentrantLockSpaceship(),

        new MonitorSpaceship(),

        new StampedLockSpaceship(),
        new OptimisticStampedLockSpaceship(),

        new AtomicSpaceship(),
        new AdderSpaceship(),

        new LockFreeSpaceship(),
    };
    // @formatter:on

    @BeforeMethod
    public void init() {
        EXECUTOR = Executors.newCachedThreadPool();
    }

    @SneakyThrows
    @Test(enabled = false, dataProvider = "readerWriterData")
    public void test(int readerNum, int writerNum) {
        NUM_READERS = readerNum;
        NUM_WRITERS = writerNum;

        for (int i = 0; i < 3; i++) {
            out.format("*** Run - %d *** \n", i);
            for (Spaceship spaceship : SPACESHIPS) {
                System.gc();
                TimeUnit.SECONDS.sleep(TEST_DURATION);

                doRun(spaceship);
            }
        }

        EXECUTOR.shutdown();
    }

    @DataProvider(name = "readerWriterData")
    private Object[][] readerWriterData() {
        // NUM_READERS, NUM_WRITERS
        return new Object[][] { { 3, 1 }, { 1, 3 }, { 2, 2 } };
    }

    @SneakyThrows
    private static void doRun(Spaceship spaceship) {
        final Results results = new Results();
        final CyclicBarrier startBarrier = new CyclicBarrier(NUM_READERS
                + NUM_WRITERS + 1);
        final CountDownLatch finishLatch = new CountDownLatch(NUM_READERS
                + NUM_WRITERS);
        final AtomicBoolean runningFlag = new AtomicBoolean(true);

        for (int i = 0; i < NUM_WRITERS; i++) {
            EXECUTOR.execute(new WriterRunner(i, results, spaceship,
                    runningFlag, startBarrier, finishLatch));
        }

        for (int i = 0; i < NUM_READERS; i++) {
            EXECUTOR.execute(new ReaderRunner(i, results, spaceship,
                    runningFlag, startBarrier, finishLatch));
        }

        startBarrier.await();

        TimeUnit.SECONDS.sleep(TEST_DURATION);
        runningFlag.set(false);

        finishLatch.await();

        out.format("%d readers %d writers %30s %s\n", NUM_READERS, NUM_WRITERS,
                spaceship.getClass().getSimpleName(), results);
    }

    public static class Results {
        long[] reads = new long[NUM_READERS];
        long[] writes = new long[NUM_WRITERS];

        long[] readAttempts = new long[NUM_READERS];
        long[] writeAttempts = new long[NUM_WRITERS];

        long[] observedWrites = new long[NUM_READERS];

        @Override
        public String toString() {
            String readsSum = format("%,d:", Arrays.stream(reads).sum());
            String writesSum = format("%,d:", Arrays.stream(writes).sum());

            Map<String, String> info = new LinkedHashMap<>();
            info.put("reads", readsSum + Arrays.toString(reads));
            info.put("readAttempts", Arrays.toString(readAttempts));
            info.put("writes", writesSum + Arrays.toString(writes));
            info.put("writeAttempts", Arrays.toString(writeAttempts));
            info.put("observedWrites", Arrays.toString(observedWrites));

            MapJoiner joiner = Joiner.on(" ").withKeyValueSeparator("=");
            return joiner.join(info);
        }
    }

    @AllArgsConstructor
    public static class WriterRunner implements Runnable {

        private final int id;
        private final Results results;
        private final Spaceship spaceship;
        private final AtomicBoolean runningFlag;
        private final CyclicBarrier barrier;
        private final CountDownLatch latch;

        @SneakyThrows
        @Override
        public void run() {
            barrier.await();

            long writesCount = 0;
            long writeAttemptsCount = 0;

            while (runningFlag.get()) {
                writeAttemptsCount += spaceship.write(1, 1);

                ++writesCount;
            }

            results.writeAttempts[id] = writeAttemptsCount;
            results.writes[id] = writesCount;

            latch.countDown();
        }
    }

    @AllArgsConstructor
    public static class ReaderRunner implements Runnable {

        private final int id;
        private final Results results;
        private final Spaceship spaceship;
        private final AtomicBoolean runningFlag;
        private final CyclicBarrier barrier;
        private final CountDownLatch latch;

        @SneakyThrows
        @Override
        public void run() {
            barrier.await();

            int[] currentCoordinates = { 0, 0 };
            int[] lastCoordinates = { 0, 0 };

            long readsCount = 0;
            long readAttemptsCount = 0;

            long observedWrites = 0;

            while (runningFlag.get()) {
                readAttemptsCount += spaceship.read(currentCoordinates);

                ++readsCount;

                if (lastCoordinates[0] != currentCoordinates[0]
                        || lastCoordinates[1] != currentCoordinates[1]) {

                    ++observedWrites;
                    lastCoordinates[0] = currentCoordinates[0];
                    lastCoordinates[1] = currentCoordinates[1];
                }
            }

            results.reads[id] = readsCount;
            results.readAttempts[id] = readAttemptsCount;

            results.observedWrites[id] = observedWrites;

            latch.countDown();
        }
    }
}
