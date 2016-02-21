package org.asuki.dp.other;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class AbstractDocument {

    interface Document {
        Object put(String key, Object value);

        Object get(String key);

        <T> Stream<T> children(
                String key,
                Function<Map<String, Object>, T> constructor
        );
    }

    static abstract class BaseDocument implements Document {
        private final Map<String, Object> entries;

        protected BaseDocument(Map<String, Object> entries) {
            if (entries == null) {
                entries = new HashMap<>();
            }
            this.entries = entries;
        }

        @Override
        public final Object put(String key, Object value) {
            return entries.put(key, value);
        }

        @Override
        public final Object get(String key) {
            return entries.get(key);
        }

        @Override
        public final <T> Stream<T> children(
                String key,
                Function<Map<String, Object>, T> constructor) {

            final List<Map<String, Object>> children =
                    (List<Map<String, Object>>) get(key);

            return children == null
                    ? Stream.empty()
                    : children.stream().map(constructor);
        }
    }

    interface HasModel extends Document {
        String MODEL = "model";

        default String getModel() {
            return (String) get(MODEL);
        }

        default void setModel(String name) {
            put(MODEL, name);
        }
    }

    interface HasPrice extends Document {
        String PRICE = "price";

        default Number getPrice() {
            return (Number) get(PRICE);
        }

        default void setPrice(Number price) {
            put(PRICE, price);
        }
    }

    interface HasWheels extends Document {
        String WHEELS = "wheels";

        default List<Wheel> getWheels() {
            return children(WHEELS, Wheel::new).collect(toList());
        }

        default void setWheels(List<Wheel> wheels) {
            put(WHEELS, wheels);
        }
    }

    static class Car extends BaseDocument implements HasModel, HasPrice, HasWheels {
        protected Car(Map<String, Object> entries) {
            super(entries);
        }
    }

    static class Wheel extends BaseDocument implements HasModel, HasPrice {
        protected Wheel(Map<String, Object> entries) {
            super(entries);
        }
    }
}
