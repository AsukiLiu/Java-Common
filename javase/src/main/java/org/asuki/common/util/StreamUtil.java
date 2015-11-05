package org.asuki.common.util;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiFunction;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public final class StreamUtil {

    private StreamUtil() {
    }

    public static <A, B> Stream<Pair<A, B>> zip(Stream<A> s1, Stream<B> s2) {
        return zip(s1, s2, -1);
    }

    public static <A, B> Stream<Pair<A, B>> zip(Stream<A> s1, Stream<B> s2,
            int size) {

        PairIterator<A, B, Pair<A, B>> itr = new PairIterator<>(s1.iterator(),
                s2.iterator(), Pair<A, B>::new);

        int characteristics = Spliterator.IMMUTABLE | Spliterator.NONNULL;

        if (size < 0) {
            return StreamSupport.stream(
                    Spliterators.spliteratorUnknownSize(itr, characteristics),
                    false);
        }

        return StreamSupport.stream(
                Spliterators.spliterator(itr, size, characteristics), false);
    }

    @AllArgsConstructor
    private static class PairIterator<A, B, T> implements Iterator<T> {
        private final Iterator<A> i1;
        private final Iterator<B> i2;
        private final BiFunction<A, B, T> mapper;

        @Override
        public boolean hasNext() {
            return i1.hasNext() && i2.hasNext();
        }

        @Override
        public T next() {
            return mapper.apply(i1.next(), i2.next());
        }
    }

    @EqualsAndHashCode
    @ToString
    @AllArgsConstructor
    @Getter
    public static class Pair<A, B> {
        private final A first;
        private final B second;
    }
}
