package org.asuki.dp.gof23;

import static java.lang.System.out;

import java.util.stream.Stream;

import org.asuki.dp.gof23.Prototype.Item;
import org.testng.annotations.Test;

public class PrototypeTest {

    @Test
    public void test() throws CloneNotSupportedException {
        Item itemA = new Item("A");
        Item itemB = new Item("B");

        Item[] items = new Item[10];

        for (int i = 0; i < items.length; i++) {
            if (i % 2 == 0) {
                items[i] = itemA.clone();
            } else {
                items[i] = itemB.clone();
            }
        }

        Stream.of(items).forEach(p -> out.println(p.getName()));
    }
}
