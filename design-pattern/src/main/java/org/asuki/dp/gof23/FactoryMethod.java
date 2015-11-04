package org.asuki.dp.gof23;

public class FactoryMethod {

    interface Factory {
        Product createProduct();
    }

    static class ConcreteFactory1 implements Factory {
        @Override
        public Product createProduct() {
            return new ConcreteProductA();
        }
    }

    static class ConcreteFactory2 implements Factory {
        @Override
        public Product createProduct() {
            return new ConcreteProductB();
        }
    }

    interface Product {
        String getName();
    }

    static class ConcreteProductA implements Product {
        @Override
        public String getName() {
            return this.getClass().getSimpleName();
        }
    }

    static class ConcreteProductB implements Product {
        @Override
        public String getName() {
            return this.getClass().getSimpleName();
        }
    }
}
