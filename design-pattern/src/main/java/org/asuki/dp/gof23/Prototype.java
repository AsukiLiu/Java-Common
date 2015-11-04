package org.asuki.dp.gof23;

import java.util.concurrent.TimeUnit;

import lombok.Getter;
import lombok.SneakyThrows;

public class Prototype {

    static class Item implements Cloneable {
        @Getter
        private final String name;

        @SneakyThrows
        public Item(String name) {
            TimeUnit.SECONDS.sleep(2);

            this.name = name;
        }

        @Override
        public Item clone() throws CloneNotSupportedException {
            // Shallow clone
            return (Item) super.clone();
        }
    }
}
