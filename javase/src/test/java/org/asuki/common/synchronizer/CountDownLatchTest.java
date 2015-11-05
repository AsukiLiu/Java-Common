package org.asuki.common.synchronizer;

import static java.lang.System.out;
import static java.util.stream.IntStream.range;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

import org.testng.annotations.Test;

public class CountDownLatchTest {

    @SneakyThrows
    @Test
    public void test() {
        int num = 10;

        // 発令
        CountDownLatch begin = new CountDownLatch(1);
        // 10人のRunner
        CountDownLatch end = new CountDownLatch(num);

        // Runner毎のレース
        ExecutorService es = Executors.newFixedThreadPool(num);
        // Runner毎のスコア
        List<Future<Integer>> futures = new ArrayList<>();

        // Ready!
        range(0, num).forEach(
                i -> futures.add(es.submit(new Runner(begin, end))));

        // Go!
        begin.countDown();

        // 全員到着を待つ
        end.await();

        int count = 0;
        for (Future<Integer> future : futures) {
            count += future.get();
        }

        out.println(count);
    }

    @AllArgsConstructor
    private static class Runner implements Callable<Integer> {
        // 発令
        private CountDownLatch begin;
        // 終了
        private CountDownLatch end;

        @Override
        public Integer call() throws Exception {
            int score = new Random().nextInt(25);

            // 発令を待つ
            begin.await();

            // Running...
            TimeUnit.MILLISECONDS.sleep(score);

            // 終点に到着
            end.countDown();

            return score;
        }
    }
}
