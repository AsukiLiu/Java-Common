package org.asuki.common.synchronizer;

import static java.lang.String.format;
import static java.lang.System.out;
import static java.lang.Thread.currentThread;

import java.util.concurrent.Exchanger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import lombok.SneakyThrows;

import org.testng.annotations.Test;

public class ExchangerTest {

    private static final String BEFORE_INFO = "%s Value(before):%s";
    private static final String AFTER_INFO = "%s Value(after):%s";

    @SneakyThrows
    @Test
    public void test() {
        final ExecutorService service = Executors.newCachedThreadPool();

        final Exchanger<String> exchanger = new Exchanger<>();

        service.execute(() -> exchange(exchanger, "AAA"));
        service.execute(() -> exchange(exchanger, "BBB"));

        TimeUnit.SECONDS.sleep(10);
    }

    @SneakyThrows
    private void exchange(Exchanger<String> exchanger, String value) {
        TimeUnit.SECONDS.sleep((int) (Math.random() * 10));

        String before = value;
        out.println(format(BEFORE_INFO, currentThread().getName(), before));
        String after = exchanger.exchange(before);
        out.println(format(AFTER_INFO, currentThread().getName(), after));
    }

}
