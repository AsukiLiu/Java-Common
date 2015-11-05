package org.asuki.tool.guava;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Ordering.explicit;
import static com.google.common.collect.Ordering.natural;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.testng.annotations.Test;

import com.google.common.base.Functions;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;

public class OrderingTest {

    @Test
    public void testBasic() {
        List<Integer> toSort = asList(3, 5, null, 1, 2);
        Collections.sort(toSort, natural().nullsFirst());
        assertThat(toSort, equalTo(newArrayList(null, 1, 2, 3, 5)));

        Collections.sort(toSort, natural().nullsLast());
        assertThat(toSort, equalTo(newArrayList(1, 2, 3, 5, null)));

        Collections.sort(toSort, natural().nullsLast().reverse());
        assertThat(toSort, equalTo(newArrayList(null, 5, 3, 2, 1)));

        toSort = asList(3, 5, 2, 1, 2);
        Collections.sort(toSort, natural());
        assertThat(natural().isStrictlyOrdered(toSort), is(false));
        assertThat(natural().isOrdered(toSort), is(true));

        List<Integer> leastOf = natural().leastOf(toSort, 3);
        assertThat(leastOf, equalTo(newArrayList(1, 2, 2)));

        toSort = asList(1, 2, 11);
        Collections.sort(toSort, Ordering.usingToString());
        Ordering<Integer> expectedOrder = explicit(newArrayList(1, 11, 2));
        assertThat(expectedOrder.isOrdered(toSort), is(true));

        int found = Ordering.usingToString().binarySearch(toSort, 11);
        assertThat(found, equalTo(1));
        found = Ordering.usingToString().max(toSort);
        assertThat(found, equalTo(2));

        toSort = asList(2, 1, 11, 100, 8);
        Ordering<Object> ordering = natural().onResultOf(
                Functions.toStringFunction());
        List<Integer> sortedCopy = ordering.sortedCopy(toSort);
        assertThat(sortedCopy, equalTo(newArrayList(1, 100, 11, 2, 8)));
    }

    @Test
    public void testOrderByLength() {

        List<String> list = newArrayList("a", "aa", "aaa");
        assertThat(orderByLength.isOrdered(list), is(true));
        assertThat(orderByLength.reverse().isOrdered(list), is(false));

        List<String> toSort = asList("dd", "aa", "b", "ccc");

        Collections.sort(toSort, orderByLength);
        Ordering<String> expectedOrder = explicit(newArrayList("b", "dd", "aa",
                "ccc"));
        assertThat(expectedOrder.isOrdered(toSort), is(true));

        Collections.sort(toSort, orderByLength.compound(natural()));
        expectedOrder = explicit(newArrayList("b", "aa", "dd", "ccc"));
        assertThat(expectedOrder.isOrdered(toSort), is(true));

        toSort = asList("dd", "aa", null, "b", "ccc");
        Collections.sort(toSort, orderByLength.reverse().compound(natural())
                .nullsLast());
        assertThat(toSort, equalTo(newArrayList("ccc", "aa", "dd", "b", null)));
    }

    @Test
    public void testSortedCopy() {
        List<String> toSort = asList("aa", "b", "ccc");
        List<String> sortedCopy = orderByLength.sortedCopy(toSort);

        Ordering<String> expectedOrder = explicit(newArrayList("b", "aa", "ccc"));
        assertThat(expectedOrder.isOrdered(toSort), is(false));
        assertThat(expectedOrder.isOrdered(sortedCopy), is(true));

        List<Double> numbers = newArrayList(0.2, 0.3, 0.1);

        assertThat(orderByLength.from(comparator).isOrdered(numbers), is(false));

        assertThat(digitOrdering.sortedCopy(numbers),
                equalTo(newArrayList(0.1, 0.2, 0.3)));
        assertThat(digitOrdering.reverse().sortedCopy(numbers),
                equalTo(newArrayList(0.3, 0.2, 0.1)));
        assertThat(digitOrdering.from(comparator).sortedCopy(numbers),
                equalTo(newArrayList(0.3, 0.2, 0.1)));

        assertThat(digitOrdering.reverse().explicit(numbers)
                .sortedCopy(numbers), equalTo(newArrayList(0.2, 0.3, 0.1)));

        assertThat(
                digitOrdering.reverse().compound(comparator)
                        .sortedCopy(numbers),
                equalTo(newArrayList(0.3, 0.2, 0.1)));
    }

    private Ordering<String> orderByLength = new Ordering<String>() {
        @Override
        public int compare(String left, String right) {
            return Ints.compare(left.length(), right.length());
        }
    };

    private Ordering<Double> digitOrdering = new Ordering<Double>() {
        @Override
        public int compare(Double left, Double right) {
            return Doubles.compare(left, right);
        }
    };

    private Comparator<Double> comparator = new Comparator<Double>() {
        @Override
        public int compare(Double o1, Double o2) {
            return Doubles.compare(1 / o1, 1 / o2);
        }
    };
}
