package org.asuki.dp.gof23;

import static java.lang.System.out;

import org.asuki.dp.gof23.FactoryMethod.Factory;
import org.asuki.dp.gof23.FactoryMethod.ConcreteFactory1;
import org.asuki.dp.gof23.FactoryMethod.ConcreteFactory2;
import org.testng.annotations.Test;

public class FactoryMethodTest {

    @Test
    public void test() {
        Factory factory = new ConcreteFactory1();
        out.println(factory.createProduct().getName());

        factory = new ConcreteFactory2();
        out.println(factory.createProduct().getName());
    }
}
