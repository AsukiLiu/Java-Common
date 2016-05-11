package org.asuki.dp.other;

import rx.Observable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.IntStream;

import static java.lang.System.out;
import static java.util.concurrent.CompletableFuture.allOf;
import static java.util.stream.Collectors.toList;
import static rx.Observable.merge;

public class ScatterGather {

    public static List<String> sequential(Function<Integer, String> task) {
        return IntStream.range(0, 5)
                .boxed()
                .map(task)
                .collect(toList());
    }

    public static List<String> unsequentialByCompletableFuture(Function<Integer, CompletableFuture<String>> task) {

        List<CompletableFuture<String>> futures =
                IntStream.range(0, 5)
                        .boxed()
                        .map(task)
                        .collect(toList());

        CompletableFuture<List<String>> merged = allOf(futures.toArray(new CompletableFuture[futures.size()]))
                .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .collect(toList())
                );

        merged.thenAccept(l ->
                out.println("thenAccept: " + l.toString())
        );

        try {
            return merged.get();
        } catch (InterruptedException | ExecutionException e) {
            return new ArrayList<>();
        }
    }

    public static List<String> unsequentialByObservable(Function<Integer, Observable<String>> task) {

        List<Observable<String>> obs =
                IntStream.range(0, 5)
                        .boxed()
                        .map(task)
                        .collect(toList());

        Observable<List<String>> merged = merge(obs).toList();

        merged.subscribe(
                l -> out.println("subscribe: " + l.toString())
        );

        merged.forEach(
                l -> out.println("forEach: " + l.toString())
        );

        return merged.toBlocking().first();
    }
}
