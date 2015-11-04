package org.asuki.dp.gof23;

import static java.lang.System.out;
import lombok.Getter;
import lombok.Setter;

public class State {

    interface Statable {
        void handle(Context context);
    }

    static class Context {
        @Getter
        @Setter
        private Statable state;

        public Context(Statable state) {
            this.state = state;
        }

        public void request() {
            state.handle(this);
        }

        public void display() {
            out.println("STATE:" + this.state.getClass().getName());
        }
    }

    static class ConcreteStateA implements Statable {
        @Override
        public void handle(Context context) {
            context.setState(new ConcreteStateB());
        }
    }

    static class ConcreteStateB implements Statable {
        @Override
        public void handle(Context context) {
            context.setState(new ConcreteStateA());
        }
    }

}
