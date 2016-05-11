package org.asuki.dp.other;

import com.google.common.base.Stopwatch;
import org.testng.annotations.Test;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.google.common.base.Stopwatch.createStarted;
import static java.lang.System.out;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.asuki.dp.other.ScatterGather.*;

public class ScatterGatherTest {

    @Test
    public void testSequential() {
        Stopwatch sw = createStarted();

        List<String> result = sequential(this::generateTask);

        out.println(result);
        out.println(sw.elapsed(SECONDS));
    }

    private String generateTask(int i) {
        delay(2);
        return i + "-test";
    }

    @Test
    public void testCompletableFuture() {
        ExecutorService executor = Executors.newFixedThreadPool(5);

        Stopwatch sw = createStarted();

        List<String> result = unsequentialByCompletableFuture(
                i -> this.generateTaskByCompletableFuture(i, executor).exceptionally(Throwable::getMessage)
        );

        out.println(result);
        out.println(sw.elapsed(SECONDS));
    }

    private CompletableFuture<String> generateTaskByCompletableFuture(int i, ExecutorService executor) {
        return supplyAsync(() -> {
            if (i == 3) {
                throw new RuntimeException("Exception occurred!");
            }

            delay(2);
            return i + "-test";
        }, executor);
    }

    @Test
    public void testObservable() {
        ExecutorService executor = Executors.newFixedThreadPool(5);

        Stopwatch sw = createStarted();

        List<String> result = unsequentialByObservable(
                i -> generateTaskByObservable(i, executor)
        );

        out.println(result);
        out.println(sw.elapsed(SECONDS));
    }

    private Observable<String> generateTaskByObservable(int i, ExecutorService executor) {
        return Observable
                .<String>create(s -> {
                    if (i == 3) {
                        throw new RuntimeException("Exception occurred!");
                    }

                    delay(2);
                    s.onNext(i + "-test");
                    s.onCompleted();
                })
                .onErrorReturn(Throwable::getMessage)
                .subscribeOn(Schedulers.from(executor));
    }

    private static void delay(int seconds) {
        try {
            SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            throw new RuntimeException("Sleep failed");
        }
    }

}
