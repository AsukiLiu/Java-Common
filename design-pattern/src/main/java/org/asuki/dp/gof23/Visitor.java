package org.asuki.dp.gof23;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class Visitor {

    interface Item {
        int accept(Cart cart);
    }

    @AllArgsConstructor
    @Getter
    static class Book implements Item {
        private final int cost;
        private final String isbn;

        @Override
        public int accept(Cart cart) {
            return cart.visit(this);
        }
    }

    @AllArgsConstructor
    @Getter
    static class Fruit implements Item {
        private final int cost;
        private final int number;
        private final String name;

        @Override
        public int accept(Cart cart) {
            return cart.visit(this);
        }
    }

    // Visitor
    interface Cart {
        int visit(Book book);

        int visit(Fruit fruit);
    }

    static class CartImpl implements Cart {

        @Override
        public int visit(Book book) {
            return book.getCost();
        }

        @Override
        public int visit(Fruit fruit) {
            return fruit.getCost() * fruit.getNumber();
        }

    }

    // Approach 1
    static int calculatePrice(Item[] items, Cart cart) {
        return Stream.of(items).mapToInt(item -> item.accept(cart)).sum();
    }

    static class Target {

        private List<Item> items = new ArrayList<>();

        public void attach(Item item) {
            items.add(item);
        }

        public void detatch(Item item) {
            items.remove(item);
        }

        // Approach 2
        public int calculatePrice(Cart cart) {
            return items.stream().mapToInt(item -> item.accept(cart)).sum();
        }
    }
}
