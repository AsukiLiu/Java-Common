package org.asuki.dp.gof23;

import static java.lang.System.out;

public class Bridge {

    interface Implementor {
        void operation();
    }

    static class ConcreteImplementorA implements Implementor {
        @Override
        public void operation() {
            out.println(this.getClass().getSimpleName());
        }
    }

    static class ConcreteImplementorB implements Implementor {
        @Override
        public void operation() {
            out.println(this.getClass().getSimpleName());
        }
    }

    static abstract class Abstraction {
        protected Implementor imp;

        public void setImplementor(Implementor imp) {
            this.imp = imp;
        }

        public abstract void action();
    }

    static class RefinedAbstraction extends Abstraction {
        @Override
        public void action() {
            this.imp.operation();
        }
    }
}
