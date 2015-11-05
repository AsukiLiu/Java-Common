package org.asuki.common;

import static java.lang.System.out;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import org.testng.annotations.Test;

public class MethodTest {

    @Test
    public void testDefaultMethod() {

        // Formula formula = (num) -> sqrt(num * 100); // Compile error
        Formula formula = new Formula() {
            @Override
            public double calculate(int num) {
                return sqrt(num * 100);
            }
        };

        assertThat(formula.calculate(100), is(100.0));
        assertThat(formula.sqrt(16), is(4.0));
    }

    @Test
    public void testMethodReference() {
        Car car = Car.create(Car::new);

        List<Car> cars = Arrays.asList(car);
        cars.forEach(Car::staticMethodWithOneParameter);
        cars.forEach(Car::instanceMethod);
        cars.forEach(car::instanceMethodWithOneParameter);
    }
}

interface Formula {

    double calculate(int a);

    default double sqrt(int a) {
        return Math.sqrt(a);
    }
}

class Car {

    public static Car create(Supplier<Car> supplier) {
        return supplier.get();
    }

    public static void staticMethodWithOneParameter(Car car) {
        out.println("staticMethodWithOneParameter: " + car.toString());
    }

    public void instanceMethodWithOneParameter(Car car) {
        out.println("instanceMethodWithOneParameter: " + car.toString());
    }

    public void instanceMethod() {
        out.println("instanceMethod: " + this.toString());
    }
}