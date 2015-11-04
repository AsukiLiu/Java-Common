package org.asuki.dp.gof23;

import static java.lang.System.out;
import lombok.AllArgsConstructor;

public class Strategy {

    interface Strategiable {
        void algorithm();
    }

    static class StrategyA implements Strategiable {
        @Override
        public void algorithm() {
            out.println(this.getClass().getSimpleName());
        }
    }

    static class StrategyB implements Strategiable {
        @Override
        public void algorithm() {
            out.println(this.getClass().getSimpleName());
        }
    }

    @AllArgsConstructor
    static class Context {
        private Strategiable st;

        public void changeStrategy(Strategiable st) {
            this.st = st;
        }

        public void execute() {
            st.algorithm();
        }
    }
}
