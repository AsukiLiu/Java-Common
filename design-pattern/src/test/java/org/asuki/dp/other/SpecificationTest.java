package org.asuki.dp.other;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.stream.Stream;

import lombok.AllArgsConstructor;
import lombok.Getter;

import org.asuki.dp.other.Specification.CompositeSpecification;
import org.asuki.dp.other.Specification.Specificationable;
import org.testng.annotations.Test;

public class SpecificationTest {

    @Test
    public void test() {
        // @formatter:off
        Man[] candidates = { 
                new Man("Andy", 1, 1, false),
                new Man("John", 2, 3, true), 
                new Man("Tom", 0, 1, false),
                new Man("Jack", 0, 0, false) };
        // @formatter:on

        HasHouseSpecification hasHouse = new HasHouseSpecification();
        HasCarSpecification hasCar = new HasCarSpecification();
        MarriedSpecification married = new MarriedSpecification();

        Specificationable<Man> spec = hasHouse.and(hasCar).and(married.not());

        Man mrRight = Stream.of(candidates)
                .filter(man -> spec.isSatisfiedBy(man)).findAny().get();

        assertThat(mrRight.getName(), is("Andy"));
    }

    @AllArgsConstructor
    @Getter
    static class Man {
        private String name;
        private int cars;
        private int houses;
        private boolean married;
    }

    static class HasCarSpecification extends CompositeSpecification<Man> {
        @Override
        public boolean isSatisfiedBy(Man m) {
            return m.getCars() > 0;
        }
    }

    static class HasHouseSpecification extends CompositeSpecification<Man> {
        @Override
        public boolean isSatisfiedBy(Man m) {
            return m.getHouses() > 0;
        }
    }

    static class MarriedSpecification extends CompositeSpecification<Man> {
        @Override
        public boolean isSatisfiedBy(Man m) {
            return m.isMarried();
        }
    }
}
