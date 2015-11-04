package org.asuki.dp.gof23;

import static java.lang.System.out;
import static org.asuki.dp.gof23.Builder.Target.builderA;
import static org.asuki.dp.gof23.Builder.Target.builderB;

import org.asuki.dp.gof23.Builder.Builderable;
import org.asuki.dp.gof23.Builder.ConcreteBuilder;
import org.asuki.dp.gof23.Builder.Director;
import org.asuki.dp.gof23.Builder.Target;
import org.testng.annotations.Test;

public class BuilderTest {

    @Test
    public void testDeprecatedVersion() {
        Builderable builder = new ConcreteBuilder();

        Director director = new Director();
        director.constract(builder);

        builder.getProduct().show();
    }

    @Test
    public void testChainVersion() {
        Target target = builderA(10).item1(20).item2(30).build1();

        out.println(target);

        target = builderA(10).item1(20).item2(30).build2();

        out.println(target);

        target = builderB(10).itemA(5).itemB(6).item1(20).item2(30).build();

        out.println(target);
    }

}
