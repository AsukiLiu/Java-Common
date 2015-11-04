package org.asuki.dp.gof23;

import static java.lang.System.out;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;

public class Mediator {

    interface Boss {
        void send(String message, Colleague c);

        void addColleague(Colleague c);
    }

    static class BossImpl implements Boss {
        private final List<Colleague> colleagues;

        public BossImpl() {
            colleagues = new ArrayList<>();
        }

        @Override
        public void addColleague(Colleague c) {
            this.colleagues.add(c);
        }

        @Override
        public void send(String message, Colleague c) {
            colleagues.stream().filter(colleague -> colleague != c)
                    .forEach(colleague -> colleague.receive(message));
        }
    }

    @AllArgsConstructor
    static abstract class Colleague {
        protected final Boss boss;

        abstract void send(String message);

        abstract void receive(String message);
    }

    static class ConcreteColleague1 extends Colleague {

        public ConcreteColleague1(Boss boss) {
            super(boss);
        }

        @Override
        public void send(String message) {
            boss.send(message, this);
        }

        @Override
        public void receive(String message) {
            out.println("ConcreteColleague1 received:" + message);
        }
    }

    static class ConcreteColleague2 extends Colleague {

        public ConcreteColleague2(Boss boss) {
            super(boss);
        }

        @Override
        public void send(String message) {
            boss.send(message, this);
        }

        @Override
        public void receive(String message) {
            out.println("ConcreteColleague2 received:" + message);
        }
    }

}
