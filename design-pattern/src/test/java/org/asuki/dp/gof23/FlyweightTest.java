package org.asuki.dp.gof23;

import org.asuki.dp.gof23.Flyweight.FlyweightFactory;
import org.asuki.dp.gof23.Flyweight.Flyweightable;
import org.asuki.dp.gof23.Flyweight.Type;
import org.testng.annotations.Test;

public class FlyweightTest {

    @Test
    public void test() {

        FlyweightFactory factory = new FlyweightFactory();

        Flyweightable flyweight = factory.getFlyweight(Type.A);
        flyweight.operation();

        flyweight = factory.getFlyweight(Type.B);
        flyweight.operation();

        flyweight = factory.getFlyweight(Type.C);
        flyweight.operation();

        flyweight = factory.getFlyweight(Type.D);
        flyweight.operation();
    }
}
