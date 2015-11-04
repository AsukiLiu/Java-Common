package org.asuki.dp.gof23;

import static java.lang.System.out;
import static java.util.Arrays.asList;

import java.util.List;

public class Facade {

    static abstract class System {

        public void process(Action... actions) {
            for (Action action : actions) {
                process(action);
            }
        }

        private void process(Action action) {
            switch (action) {
            case START:
                start();
                break;
            case STOP:
                stop();
                break;
            default:
                break;
            }
        }

        protected abstract void start();

        protected abstract void stop();
    }

    static class SubSystemA extends System {
        @Override
        protected void start() {
            out.println(this.getClass().getSimpleName() + ": start");
        }

        @Override
        protected void stop() {
            out.println(this.getClass().getSimpleName() + ": stop");
        }
    }

    static class SubSystemB extends System {
        @Override
        protected void start() {
            out.println(this.getClass().getSimpleName() + ": start");
        }

        @Override
        protected void stop() {
            out.println(this.getClass().getSimpleName() + ": stop");
        }
    }

    static class MainFacade {
        private final List<System> systems;

        public MainFacade() {
            systems = asList(new SubSystemA(), new SubSystemB());
        }

        public void operation1() {
            execute(systems, Action.START);
        }

        public void operation2() {
            execute(systems, Action.START, Action.STOP);
        }

        private void execute(List<System> systems, Action... actions) {
            systems.stream().parallel()
                    .forEach(system -> system.process(actions));
        }
    }

    enum Action {
        START, STOP
    }
}
