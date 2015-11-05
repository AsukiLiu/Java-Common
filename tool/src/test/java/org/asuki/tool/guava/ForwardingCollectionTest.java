package org.asuki.tool.guava;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.primitives.Ints.asList;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.util.List;

import org.asuki.tool.guava.forwarding.CounterMultiset;
import org.asuki.tool.guava.forwarding.MaxMultiset;
import org.asuki.tool.guava.forwarding.MinMultiset;
import org.asuki.tool.guava.forwarding.MovableList;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.LinkedHashMultiset;
import com.google.common.collect.Multiset;

public class ForwardingCollectionTest {

    @Test(dataProvider = "data")
    public void testMovableList(Function<MovableList<String>, Boolean> move,
            String expectString) {

        List<String> array = newArrayList("1", "2", "3", "4", "5");
        MovableList<String> list = new MovableList<>(array);

        move.apply(list);

        assertThat(Joiner.on(',').join(list), is(expectString));
    }

    @DataProvider(name = "data")
    private Object[][] data() {
        final int targetIndex = 2;

        // @formatter:off
        return new Object[][] { 
            { 
                new Function<MovableList<String>, Boolean>() {
                    public Boolean apply(MovableList<String> input) {
                        return input.moveUp(targetIndex);
                    }
                }, "1,3,2,4,5" 
            }, 
            { 
                new Function<MovableList<String>, Boolean>() {
                    public Boolean apply(MovableList<String> input) {
                        return input.moveDown(targetIndex);
                    }
                }, "1,2,4,3,5" 
            }, 
            { 
                new Function<MovableList<String>, Boolean>() {
                    public Boolean apply(MovableList<String> input) {
                        return input.moveTop(targetIndex);
                    }
                }, "3,1,2,4,5" 
            }, 
            { 
                new Function<MovableList<String>, Boolean>() {
                    public Boolean apply(MovableList<String> input) {
                        return input.moveBottom(targetIndex);
                    }
                }, "1,2,4,5,3" 
            }, 
        };
        // @formatter:on
    }

    @Test
    public void testOnceDecoration() {
        Multiset<String> multiset = LinkedHashMultiset.create();
        CounterMultiset<String> counter = new CounterMultiset<>(multiset);

        for (int j = 0; j < 3; j++) {
            for (int i = 0; i < 3; i++) {
                counter.add("element" + i);
            }
        }

        List<Integer> sizes = asList(multiset.size(), counter.size());

        for (int size : sizes) {
            assertThat(size, is(3 * 3));
        }

        assertThat(multiset.elementSet().toString(),
                is("[element0, element1, element2]"));

        counter.clear();
        sizes = asList(multiset.size(), counter.size());
        for (int size : sizes) {
            assertThat(size, is(0));
        }
    }

    @Test
    public void testMoreDecorations() {
        final String target = "element";
        Multiset<String> multiset = LinkedHashMultiset.create();
        CounterMultiset<String> counter = new CounterMultiset<>(multiset);
        MaxMultiset<String> max = new MaxMultiset<>(counter);
        MinMultiset<String> min = new MinMultiset<>(max);

        for (int i = 1; i < 4; i++) {
            min.add(target, i + 1);
        }

        List<Integer> sizes = asList(multiset.size(), counter.size(),
                max.size(), min.size(), counter.count(target),
                max.count(target), min.count(target));

        for (int size : sizes) {
            assertThat(size, is(3 * 3));
        }

        assertThat(counter.countCounter(target), is(4 - 1));
        assertThat(max.countMax(target), is(3 + 1));
        assertThat(min.countMin(target), is(1 + 1));

        min.clear();
        sizes = asList(multiset.size(), counter.size(), max.size(), min.size());
        for (int size : sizes) {
            assertThat(size, is(0));
        }
    }

}
