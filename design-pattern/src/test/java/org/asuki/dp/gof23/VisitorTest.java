package org.asuki.dp.gof23;

import static org.asuki.dp.gof23.Visitor.calculatePrice;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import org.asuki.dp.gof23.Visitor.Book;
import org.asuki.dp.gof23.Visitor.Fruit;
import org.asuki.dp.gof23.Visitor.Item;
import org.asuki.dp.gof23.Visitor.Target;
import org.testng.annotations.Test;
import org.asuki.dp.gof23.Visitor.CartImpl;

public class VisitorTest {

    @Test
    public void testApproach1() {
        Item[] items = { new Book(2000, "ISBN-1234"),
                new Fruit(100, 2, "Banana") };

        int total = calculatePrice(items, new CartImpl());

        assertThat(total, is(2000 + 100 * 2));
    }

    @Test
    public void testApproach2() {
        Target target = new Target();
        target.attach(new Book(2000, "ISBN-1234"));
        target.attach(new Fruit(100, 2, "Banana"));

        int total = target.calculatePrice(new CartImpl());

        assertThat(total, is(2000 + 100 * 2));
    }

}
