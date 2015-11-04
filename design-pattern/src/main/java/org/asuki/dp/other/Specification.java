package org.asuki.dp.other;

import lombok.AllArgsConstructor;

public class Specification {

    interface Specificationable<T> {
        boolean isSatisfiedBy(T t);

        Specificationable<T> and(Specificationable<T> other);

        Specificationable<T> or(Specificationable<T> other);

        Specificationable<T> not();
    }

    static abstract class CompositeSpecification<T> implements
            Specificationable<T> {

        @Override
        public Specificationable<T> and(Specificationable<T> other) {
            return new AndSpecification<T>(this, other);
        }

        @Override
        public Specificationable<T> not() {
            return new NotSpecification<T>(this);
        }

        @Override
        public Specificationable<T> or(Specificationable<T> other) {
            return new OrSpecification<T>(this, other);
        }
    }

    @AllArgsConstructor
    static class AndSpecification<T> extends CompositeSpecification<T> {

        private final Specificationable<T> a;
        private final Specificationable<T> b;

        @Override
        public boolean isSatisfiedBy(T t) {
            return a.isSatisfiedBy(t) && b.isSatisfiedBy(t);
        }
    }

    @AllArgsConstructor
    static class OrSpecification<T> extends CompositeSpecification<T> {

        private final Specificationable<T> a;
        private final Specificationable<T> b;

        @Override
        public boolean isSatisfiedBy(T t) {
            return a.isSatisfiedBy(t) || b.isSatisfiedBy(t);
        }
    }

    @AllArgsConstructor
    static class NotSpecification<T> extends CompositeSpecification<T> {

        private final Specificationable<T> a;

        @Override
        public boolean isSatisfiedBy(T t) {
            return !a.isSatisfiedBy(t);
        }
    }

}
