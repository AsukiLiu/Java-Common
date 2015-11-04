package org.asuki.dp.gof23;

import static com.google.common.base.Objects.toStringHelper;
import static java.lang.System.out;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

public class Builder {

    /*
     * Deprecated version
     */

    static class Director {
        public void constract(Builderable builder) {
            builder.buildPartA();
            builder.buildPartB();
        }
    }

    interface Builderable {
        void buildPartA();

        void buildPartB();

        Product getProduct();
    }

    static class ConcreteBuilder implements Builderable {
        @Getter
        private Product product = new Product();

        @Override
        public void buildPartA() {
            product.addPart("partA");
        }

        @Override
        public void buildPartB() {
            product.addPart("partB");
        }
    }

    static class Product {
        private List<String> parts = new ArrayList<>();

        public void addPart(String part) {
            parts.add(part);
        }

        public void show() {
            parts.forEach(out::println);
        }
    }

    /*
     * Chain version
     */

    static class Target {
        // Required
        @Getter
        private final int item0;

        // Optional
        @Getter
        private final int item1;
        @Getter
        private final int item2;

        @Getter
        private int itemA;
        @Getter
        private int itemB;

        private Target(TargetBuilder builder) {
            this.item0 = builder.item0;
            this.item1 = builder.item1;
            this.item2 = builder.item2;
        }

        private Target(int item0, int item1, int item2) {
            this.item0 = item0;
            this.item1 = item1;
            this.item2 = item2;
        }

        private Target(int item0, int item1, int item2, int itemA, int itemB) {
            this.item0 = item0;
            this.item1 = item1;
            this.item2 = item2;

            this.itemA = itemA;
            this.itemB = itemB;
        }

        @Override
        public String toString() {
            // @formatter:off
            return toStringHelper(this).omitNullValues()
                    .add("item0", this.item0)
                    .add("item1", this.item1)
                    .add("item2", this.item2)
                    .add("itemA", this.itemA)
                    .add("itemB", this.itemB)
                    .toString();
            // @formatter:on
        }

        /*
         * Normal way
         */

        public static TargetBuilder builderA(int item) {
            return new TargetBuilder(item);
        }

        public static class TargetBuilder {
            private final int item0;
            private int item1;
            private int item2;

            public TargetBuilder(int item0) {
                this.item0 = item0;
            }

            public TargetBuilder item1(int val) {
                this.item1 = val;
                return this;
            }

            public TargetBuilder item2(int val) {
                this.item2 = val;
                return this;
            }

            public Target build1() {
                return new Target(item0, item1, item2);
            }

            public Target build2() {
                return new Target(this);
            }
        }

        /*
         * Ordered way
         */

        public static IItemA builderB(int item) {
            return new BuilderImpl(item);
        }

        public interface IItemA {
            IItemB itemA(int val);
        }

        public interface IItemB {
            IBuild itemB(int val);
        }

        public interface IBuild {
            IBuild item1(int val);

            IBuild item2(int val);

            Target build();
        }

        private static class BuilderImpl implements IItemA, IItemB, IBuild {
            private final int item0;
            private int itemA;
            private int itemB;
            private int item1;
            private int item2;

            public BuilderImpl(int item0) {
                this.item0 = item0;
            }

            @Override
            public IItemB itemA(int val) {
                this.itemA = val;
                return this;
            }

            @Override
            public IBuild itemB(int val) {
                this.itemB = val;
                return this;
            }

            @Override
            public IBuild item1(int val) {
                this.item1 = val;
                return this;
            }

            @Override
            public IBuild item2(int val) {
                this.item2 = val;
                return this;
            }

            @Override
            public Target build() {
                return new Target(this.item0, this.item1, this.item2,
                        this.itemA, this.itemB);
            }
        }
    }

}
