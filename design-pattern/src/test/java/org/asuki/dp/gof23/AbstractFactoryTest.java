package org.asuki.dp.gof23;

import static org.asuki.dp.gof23.AbstractFactory.Factory.getFactory;

import org.asuki.dp.gof23.AbstractFactory.Client;
import org.asuki.dp.gof23.AbstractFactory.ProductA;
import org.asuki.dp.gof23.AbstractFactory.ProductB;
import org.asuki.dp.gof23.AbstractFactory.Factory;
import org.asuki.dp.gof23.AbstractFactory.ConcreteFactory1;
import org.asuki.dp.gof23.AbstractFactory.ConcreteFactory2;
import org.testng.annotations.Test;

public class AbstractFactoryTest {

    @Test
    public void testCase1() {
        Factory factory = getFactory("factory1");

        ProductA productA = factory.createProductA();
        ProductB productB = factory.createProductB();

        productA.printDescription();
        productB.printDescription();
    }

    @Test
    public void testCase2() {
        Client client = new Client();
        Factory factory = new ConcreteFactory2();

        ProductA productA = client.getProductA(factory);
        ProductB productB = client.getProductB(factory);

        productA.printDescription();
        productB.printDescription();
    }

    @Test
    public void testCase3() {
        Client client = new Client();
        Factory factory = new ConcreteFactory1();

        client.requestProducts(factory);

        client.getProductA().printDescription();
        client.getProductB().printDescription();
    }
}
