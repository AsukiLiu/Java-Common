package org.asuki.dp.gof23;

import org.asuki.dp.gof23.Bridge.Abstraction;
import org.asuki.dp.gof23.Bridge.RefinedAbstraction;
import org.asuki.dp.gof23.Bridge.ConcreteImplementorA;
import org.asuki.dp.gof23.Bridge.ConcreteImplementorB;
import org.testng.annotations.Test;

public class BridgeTest {

    @Test
    public void test() {
        Abstraction abstraction = new RefinedAbstraction();

        abstraction.setImplementor(new ConcreteImplementorA());
        abstraction.action();

        abstraction.setImplementor(new ConcreteImplementorB());
        abstraction.action();
    }
}
