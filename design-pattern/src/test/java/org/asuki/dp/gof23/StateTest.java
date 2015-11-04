package org.asuki.dp.gof23;

import org.asuki.dp.gof23.State.Context;
import org.asuki.dp.gof23.State.ConcreteStateB;
import org.testng.annotations.Test;

public class StateTest {

    @Test
    public void test() {
        Context c = new Context(new ConcreteStateB());
        c.display();

        c.request();
        c.display();

        c.request();
        c.display();
    }
}
