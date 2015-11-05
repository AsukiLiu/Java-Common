package org.asuki.tool.guava.concurrent;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.util.concurrent.AsyncFunction;
import com.google.common.util.concurrent.CheckedFuture;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.FutureFallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;

public class FluentFuture<T> implements ListenableFuture<T> {

    private final Executor executor;
    private final ListenableFuture<T> future;

    private FluentFuture(ListenableFuture<T> future, Executor executor) {
        this.future = future;
        this.executor = executor;
    }

    private FluentFuture(ListenableFuture<T> future) {
        this(future, MoreExecutors.sameThreadExecutor());
    }

    public static <Y> FluentFuture<Y> from(Y value) {
        return new FluentFuture<Y>(Futures.immediateFuture(value));
    }

    public static <Y> FluentFuture<Y> from(Y value, Executor executor) {
        return new FluentFuture<Y>(Futures.immediateFuture(value), executor);
    }

    public static <Y> FluentFuture<Y> from(ListenableFuture<Y> future) {
        return new FluentFuture<Y>(future);
    }

    public static <Y> FluentFuture<Y> from(ListenableFuture<Y> future,
            Executor executor) {
        return new FluentFuture<Y>(future, executor);
    }

    public static <Y> FluentFuture<List<Y>> from(ListenableFuture<Y>... futures) {
        return new FluentFuture<List<Y>>(Futures.allAsList(Arrays
                .asList(futures)));
    }

    public static <Y> FluentFuture<List<Y>> from(Executor executor,
            ListenableFuture<Y>... futures) {
        return new FluentFuture<List<Y>>(Futures.allAsList(Arrays
                .asList(futures)), executor);
    }

    public static <Y> FluentFuture<List<Y>> from(
            Iterable<ListenableFuture<Y>> futures) {
        return new FluentFuture<List<Y>>(Futures.allAsList(futures));
    }

    public static <Y> FluentFuture<List<Y>> from(
            Iterable<ListenableFuture<Y>> futures, Executor executor) {
        return new FluentFuture<List<Y>>(Futures.allAsList(futures), executor);
    }

    // T→Y

    public <Y> FluentFuture<Y> transform(Function<T, Y> func) {
        // return new FluentFuture<Y>(Futures.transform(future, func));
        return new FluentFuture<Y>(Futures.transform(future, func, executor));
    }

    public <Y> FluentFuture<Y> transform(Function<T, Y> func, Executor executor) {
        return new FluentFuture<Y>(Futures.transform(future, func, executor));
    }

    public <Y> FluentFuture<Y> transform(AsyncFunction<T, Y> func) {
        return new FluentFuture<Y>(Futures.transform(future, func));
    }

    public <Y> FluentFuture<Y> transform(AsyncFunction<T, Y> func,
            Executor executor) {
        return new FluentFuture<Y>(Futures.transform(future, func, executor));
    }

    public FluentFuture<T> withFallback(FutureFallback<T> fallback) {
        return new FluentFuture<T>(Futures.withFallback(future, fallback));
    }

    public FluentFuture<T> withFallback(FutureFallback<T> fallback,
            Executor executor) {
        return new FluentFuture<T>(Futures.withFallback(future, fallback,
                executor));
    }

    public FluentFuture<T> addCallback(FutureCallback<T> callback) {
        Futures.addCallback(future, callback);
        return this;
    }

    public FluentFuture<T> addCallback(FutureCallback<T> callback,
            Executor executor) {
        Futures.addCallback(future, callback, executor);
        return this;
    }

    public <E extends Exception> CheckedFuture<T, E> makeChecked(
            Function<Exception, E> func) {
        return Futures.makeChecked(future, func);
    }

    public FluentFuture<T> filter(Predicate<T> predicate) {
        return transform(new AsyncFunction<T, T>() {
            @Override
            public ListenableFuture<T> apply(T input) throws Exception {
                if (!predicate.apply(input)) {
                    throw new Exception("Does not match");
                }
                return Futures.immediateFuture(input);
            }
        });
    }

    @Override
    public void addListener(Runnable listener, Executor executor) {
        future.addListener(listener, executor);
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        return future.get();
    }

    public <E extends Exception> T get(Class<E> exceptionClass) throws E {
        return Futures.get(future, exceptionClass);
    }

    @Override
    public T get(long timeout, TimeUnit timeUnit) throws InterruptedException,
            ExecutionException, TimeoutException {
        return future.get(timeout, timeUnit);
    }

    public <E extends Exception> T get(long timeout, TimeUnit timeUnit,
            Class<E> exceptionClass) throws E {
        return Futures.get(future, timeout, timeUnit, exceptionClass);
    }

    @Override
    public boolean isDone() {
        return future.isDone();
    }

    @Override
    public boolean isCancelled() {
        return future.isCancelled();
    }

    @Override
    public boolean cancel(boolean b) {
        return future.cancel(b);
    }

}