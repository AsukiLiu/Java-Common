package org.asuki.dp.gof23;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.asuki.dp.gof23.Decorator.Animal;
import org.asuki.dp.gof23.Decorator.Rat;
import org.asuki.dp.gof23.Decorator.AnimalDecorator;
import org.asuki.dp.gof23.Decorator.FlyFeature;
import org.asuki.dp.gof23.Decorator.DigFeature;
import org.asuki.dp.gof23.Decorator.Component;
import org.asuki.dp.gof23.Decorator.OriginalComponent;
import org.asuki.dp.gof23.Decorator.TrimDecorator;
import org.asuki.dp.gof23.Decorator.CaseDecorator;
import org.testng.annotations.Test;

public class DecoratorTest {

    @Test
    public void testNormalVersion() {
        // @formatter:off
        Component com = 
                new TrimDecorator(
                        new CaseDecorator(
                                new OriginalComponent()));
        // @formatter:on

        assertThat(com.operation(" abc "), is("ABC"));
    }

    @Test
    public void testReflectionVersion() {
        Animal animal = new Rat();
        animal = new AnimalDecorator(animal, FlyFeature.class);
        animal = new AnimalDecorator(animal, DigFeature.class);
        animal.act();
    }
}
