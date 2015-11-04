package org.asuki.dp.gof23;

import static java.lang.System.out;
import static org.asuki.dp.gof23.Memento.State.OFF;
import static org.asuki.dp.gof23.Memento.State.ON;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.util.Stack;

import org.asuki.dp.gof23.Memento.Caretaker;
import org.asuki.dp.gof23.Memento.Mementor;
import org.asuki.dp.gof23.Memento.Originator;
import org.testng.annotations.Test;

public class MementoTest {

    @Test
    public void testOnce() {
        Originator o = new Originator();
        o.setState(ON);

        Caretaker ct = new Caretaker();
        ct.setMementor(o.createMementor());

        o.setState(OFF);

        o.setMementor(ct.getMementor());

        assertThat(o.getState(), is(ON));
    }

    @Test
    public void testSeveralTimes() {
        Stack<Mementor> mementors = new Stack<>();

        Originator o = new Originator();
        mementors.add(o.createMementor());

        o.setState(ON);
        mementors.add(o.createMementor());

        o.setState(OFF);
        mementors.add(o.createMementor());

        o.setState(OFF);
        mementors.add(o.createMementor());

        while (mementors.size() > 0) {
            o.setMementor(mementors.pop());
            out.println(o.getState());
        }

    }
}
