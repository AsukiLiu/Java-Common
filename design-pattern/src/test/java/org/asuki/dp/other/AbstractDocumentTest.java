package org.asuki.dp.other;

import org.asuki.dp.other.AbstractDocument.*;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.ImmutableMap.of;
import static java.util.Arrays.asList;
import static org.asuki.dp.other.AbstractDocument.HasModel.MODEL;
import static org.asuki.dp.other.AbstractDocument.HasPrice.PRICE;
import static org.asuki.dp.other.AbstractDocument.HasWheels.WHEELS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class AbstractDocumentTest {

    @Test
    public void test() {
        final Set<Object> objects = new HashSet<>();

        objects.add(new Car(carSpec()));
        objects.add(new Wheel(of(MODEL, "C", PRICE, 700)));

        objects.stream()
                .filter(HasModel.class::isInstance)
                .filter(HasPrice.class::isInstance)
                .filter(HasWheels.class::isInstance)
                .map(p -> (HasModel & HasPrice & HasWheels) p)
                .forEach(car ->
                        {
                            assertThat(car.getModel(), is("Toyota"));
                            assertThat(car.getPrice(), is(100_000));

                            List<Wheel> wheels = car.getWheels();

                            assertThat(wheels.get(0).getModel(), is("A"));
                            assertThat(wheels.get(0).getPrice(), is(500));
                        }
                );
    }

    private static Map<String, Object> carSpec() {
        return of(
                MODEL, "Toyota",
                PRICE, 100_000,
                WHEELS, asList(
                        of(MODEL, "A", PRICE, 500),
                        of(MODEL, "B", PRICE, 600))
        );
    }
}
