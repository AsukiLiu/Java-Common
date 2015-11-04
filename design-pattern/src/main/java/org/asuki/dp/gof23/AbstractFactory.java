package org.asuki.dp.gof23;

import static java.lang.System.out;

import java.util.Map;

import lombok.Getter;

import com.google.common.collect.ImmutableMap;

public class AbstractFactory {

    private static Map<String, Factory> factoryMap;

    static {
        factoryMap = ImmutableMap.of("factory1", new ConcreteFactory1(),
                "factory2", new ConcreteFactory2());
    }

    static class Client {
        @Getter
        private ProductA productA;
        @Getter
        private ProductB productB;

        public void requestProducts(Factory factory) {
            this.productA = factory.createProductA();
            this.productB = factory.createProductB();
        }

        public ProductA getProductA(Factory factory) {
            return factory.createProductA();
        }

        public ProductB getProductB(Factory factory) {
            return factory.createProductB();
        }
    }

    static abstract class Factory {
        public abstract ProductA createProductA();

        public abstract ProductB createProductB();

        public static Factory getFactory(String factoryName) {
            return factoryMap.get(factoryName);
        }
    }

    static class ConcreteFactory1 extends Factory {
        @Override
        public ProductA createProductA() {
            return new ProductA1();
        }

        @Override
        public ProductB createProductB() {
            return new ProductB1();
        }
    }

    static class ConcreteFactory2 extends Factory {
        @Override
        public ProductA createProductA() {
            return new ProductA2();
        }

        @Override
        public ProductB createProductB() {
            return new ProductB2();
        }
    }

    interface ProductA {
        void printDescription();
    }

    static class ProductA1 implements ProductA {
        @Override
        public void printDescription() {
            out.println(this.getClass().getSimpleName());
        }
    }

    static class ProductA2 implements ProductA {
        @Override
        public void printDescription() {
            out.println(this.getClass().getSimpleName());
        }
    }

    interface ProductB {
        void printDescription();
    }

    static class ProductB1 implements ProductB {
        @Override
        public void printDescription() {
            out.println(this.getClass().getSimpleName());
        }
    }

    static class ProductB2 implements ProductB {
        @Override
        public void printDescription() {
            out.println(this.getClass().getSimpleName());
        }
    }
}
