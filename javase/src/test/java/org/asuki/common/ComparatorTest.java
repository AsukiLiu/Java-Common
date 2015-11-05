package org.asuki.common;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.ToString;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;

public class ComparatorTest {

    private List<ComparableTarget> list;

    @BeforeMethod
    public void setup() {
        list = Lists.newArrayList(
                new ComparableTarget(22), 
                new ComparableTarget(31), 
                new ComparableTarget(21),
                new ComparableTarget(12), 
                new ComparableTarget(19)
        );
    }

    @Test
    public void testCompareCaseA() {

        // @formatter:off

        // Faster
        List<ComparableTarget> sortedList1 = list.stream()
                .sorted(CustomComparator.INSTANCE)
                .collect(Collectors.toList());

        List<ComparableTarget> sortedList2 = list
                .stream()
                .sorted(Comparator.comparing(ComparableTarget::getA)
                        .thenComparing(ComparableTarget::getB)
                        .thenComparing(ComparableTarget::getC))
                .collect(Collectors.toList());

        List<ComparableTarget> sortedList3 = list
                .stream()
                .sorted(Comparator.comparingInt(ComparableTarget::getA)
                        .thenComparingInt(ComparableTarget::getB)
                        .thenComparingInt(ComparableTarget::getC))
                .collect(Collectors.toList());

        // @formatter:on

        assertThat(sortedList1.toString(), is(sortedList2.toString()));
        assertThat(sortedList1.toString(), is(sortedList3.toString()));
    }

    @Test
    public void testCompareCaseB() {

        list.sort((s1, s2) -> s1.compareTo(s2));
        String sortedString1 = list.toString();

        list.sort(Comparator.comparing(e -> e.getC()));
        String sortedString2 = list.toString();

        list.sort(Comparator.comparing(ComparableTarget::getC));
        String sortedString3 = list.toString();

        Collections.sort(list, (s1, s2) -> s1.compareTo(s2));
        String sortedString4 = list.toString();

        Collections.sort(list, Comparator.naturalOrder());
        String sortedString5 = list.toString();

        Collections.sort(list, (s1, s2) -> s2.compareTo(s1));
        String sortedString6 = list.toString();

        Collections.sort(list, Comparator.reverseOrder());
        String sortedString7 = list.toString();

        assertThat(sortedString1.toString(), is(sortedString2.toString()));
        assertThat(sortedString1.toString(), is(sortedString3.toString()));
        assertThat(sortedString1.toString(), is(sortedString4.toString()));
        assertThat(sortedString1.toString(), is(sortedString5.toString()));

        assertThat(sortedString1.toString(), is(not(sortedString6.toString())));
        assertThat(sortedString6.toString(), is(sortedString7.toString()));
    }

    @Test
    public void testCompareCaseC() {

        ComparableTarget target = list.stream()
                .max(Comparator.comparing(ComparableTarget::getC)).get();
        assertThat(target.getC(), is(31));

        Comparator<ComparableTarget> comparator = Comparator.comparing(e -> e
                .getC());
        list.sort(comparator.reversed());

        Comparator<ComparableTarget> groupByComparator = Comparator
                .comparing(ComparableTarget::getA)
                .thenComparing(ComparableTarget::getB)
                .thenComparing(ComparableTarget::getC);
        list.sort(groupByComparator);
        String sortedString1 = list.toString();

        ComparableTarget[] array = list.toArray(new ComparableTarget[list
                .size()]);
        Arrays.parallelSort(array, groupByComparator);
        String sortedString2 = Arrays.toString(array);

        assertThat(sortedString1.toString(), is(sortedString2.toString()));
    }
}

enum CustomComparator implements Comparator<ComparableTarget> {
    INSTANCE;

    @Override
    public int compare(ComparableTarget o1, ComparableTarget o2) {
        int comp = Integer.compare(o1.getA(), o2.getA());
        if (comp == 0) {
            comp = Integer.compare(o1.getB(), o2.getB());
            if (comp == 0) {
                comp = Integer.compare(o1.getC(), o2.getC());
            }
        }
        return comp;
    }
}

@ToString
class ComparableTarget implements Comparable<ComparableTarget> {
    @Getter
    private int a, b, c;

    public ComparableTarget(int i) {
        a = i % 2;
        b = i % 10;
        c = i;
    }

    @Override
    public int compareTo(ComparableTarget ct) {
        return this.getC() - ct.getC();
    }
}
