package org.asuki.dp.gof23;

import static java.lang.System.out;

import java.util.EnumMap;
import java.util.Map;

import javax.annotation.Nonnull;

public class Flyweight {

    interface Flyweightable {
        void operation();
    }

    static class ConcreteFlyweightA implements Flyweightable {
        @Override
        public void operation() {
            out.println(this.getClass().getSimpleName());
        }
    }

    static class ConcreteFlyweightB implements Flyweightable {
        @Override
        public void operation() {
            out.println(this.getClass().getSimpleName());
        }
    }

    static class FlyweightFactory {
        private Map<Type, Flyweightable> pool = new EnumMap<>(Type.class);

        public FlyweightFactory() {
            pool.put(Type.A, new ConcreteFlyweightA());
            pool.put(Type.B, new ConcreteFlyweightB());
        }

        @Nonnull
        public Flyweightable getFlyweight(Type type) {
            Flyweightable flyweight = this.pool.get(type);
            if (flyweight == null) {
                switch (type) {
                case A:
                case C:
                    flyweight = new ConcreteFlyweightA();
                    break;
                case B:
                case D:
                    flyweight = new ConcreteFlyweightB();
                    break;
                default:
                    break;
                }

                this.pool.put(type, flyweight);
            }

            return flyweight;
        }
    }

    enum Type {
        A, B, C, D;
    }
}
