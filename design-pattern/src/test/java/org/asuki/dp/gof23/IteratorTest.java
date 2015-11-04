package org.asuki.dp.gof23;

import static java.lang.System.out;
import static org.asuki.dp.gof23.Iterator.ItemType.ANY;
import static org.asuki.dp.gof23.Iterator.ItemType.POTION;
import static org.asuki.dp.gof23.Iterator.ItemType.RING;

import org.asuki.dp.gof23.Iterator.ItemIterator;
import org.asuki.dp.gof23.Iterator.Treasure;
import org.testng.annotations.Test;

public class IteratorTest {

    @Test
    public void test() {
        Treasure treasure = new Treasure();

        printIterator(treasure.iterator(RING));

        out.println();

        printIterator(treasure.iterator(POTION));

        out.println();

        printIterator(treasure.iterator(ANY));
    }

    private void printIterator(ItemIterator it) {
        while (it.hasNext()) {
            out.println(it.next());
        }
    }
}
