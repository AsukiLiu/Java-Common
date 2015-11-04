package org.asuki.dp.gof23;

import org.asuki.dp.gof23.Strategy.Context;
import org.asuki.dp.gof23.Strategy.StrategyA;
import org.asuki.dp.gof23.Strategy.StrategyB;
import org.testng.annotations.Test;

public class StrategyTest {

    @Test
    public void test() {
        Context c = new Context(new StrategyA());
        c.execute();

        c.changeStrategy(new StrategyB());
        c.execute();
    }
}
